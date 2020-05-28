package com.example.himalaya;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.himalaya.adepters.MianContentAdepter;
import com.example.himalaya.adepters.indicatorAdapter;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.presenters.RecommendPresenter;
import com.example.himalaya.views.RoundRectImageView;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.advertis.MiniPlayer;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

public class MainActivity extends FragmentActivity implements IPlayerCallback {

    private static final String TAG = "MainActivity";
    private MagicIndicator mMagicIndicator;
    private ViewPager mContentPager;
    private indicatorAdapter mIndicatorAdapter;
    private RoundRectImageView mMainTrackCover;
    private TextView mMainHeadTitle;
    private TextView mMainSubTitle;
    private ImageView mMainPlayControl;
    private PlayerPresenter mPlayerPresenter;
    private View mPlayControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();

        initPresenter();
    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
    }

    private void initEvent() {
        mIndicatorAdapter.setOnIndicatorTapClicListenner(new indicatorAdapter.OnIndicatorTapClicListenner() {
            @Override
            public void OnTapClick(int index) {
                Log.e(TAG, "click index is --> " + index);
                if (mContentPager != null) {
                    mContentPager.setCurrentItem(index);
                }
            }
        });
        mMainPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (!hasPlayList) {
                        //没有设置过播放，就播放第一个推荐专辑
                        playFirstRecommend();
                    } else {
                        if (mPlayerPresenter.isPlaying()) {
                            mPlayerPresenter.pause();
                        } else {
                            mPlayerPresenter.play();
                        }
                    }
                }
            }
        });
        mPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPlayList = mPlayerPresenter.hasPlayList();
                if (!hasPlayList) {
                    //没有设置过播放，就播放第一个推荐专辑
                    playFirstRecommend();
                }
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PlayerActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 播放第一个推荐专辑
     */
    private void playFirstRecommend() {
        List<Album> currentRecommend = RecommendPresenter.getInstance().getCurrentRecommend();
        if (currentRecommend != null && currentRecommend.size() > 0) {
            Album album = currentRecommend.get(0);
            long albumId = album.getId();
            mPlayerPresenter.playByAlbumId(albumId);
        }
    }

    private void initView() {
        mMagicIndicator = findViewById(R.id.magic_indicator);
        mMagicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建indicator适配器
        mIndicatorAdapter = new indicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(mIndicatorAdapter);

        //ViewerPager
        mContentPager = this.findViewById(R.id.content_pager);

        //创建内容适配器
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MianContentAdepter mianContentAdepter = new MianContentAdepter(supportFragmentManager);
        mContentPager.setAdapter(mianContentAdepter);

        //把他们绑定起来
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mContentPager);

        //播放控制相关
        mMainTrackCover = this.findViewById(R.id.main_track_cover);
        mMainHeadTitle = this.findViewById(R.id.main_head_title);
        mMainHeadTitle.setSelected(true);
        mMainSubTitle = this.findViewById(R.id.main_sub_title);
        mMainPlayControl = this.findViewById(R.id.main_play_control);
        mPlayControl = this.findViewById(R.id.main_play_control_item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onPlayStart() {
        updatePlayControl(true);
    }

    private void updatePlayControl(boolean isPlaying) {
        if (mMainPlayControl != null) {
            mMainPlayControl.setImageResource(isPlaying ? R.mipmap.ic_main_pause : R.mipmap.ic_main_play);
        }
    }

    @Override
    public void onPlayPause() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onNextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinish() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null) {
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();
            if (mMainHeadTitle != null) {
                mMainHeadTitle.setText(trackTitle);
            }
            if (mMainSubTitle != null) {
                mMainSubTitle.setText(nickname);
            }
            Picasso.with(this).load(coverUrlMiddle).into(mMainTrackCover);

        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
