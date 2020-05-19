package com.example.himalaya;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.adepters.DetailListAdepter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IAlbumDetialViewCallback;
import com.example.himalaya.presenters.AlbumDetialPresenter;
import com.example.himalaya.utils.ImageBlur;
import com.example.himalaya.views.RoundRectImageView;
import com.example.himalaya.views.UILoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetialViewCallback, UILoader.OnRetryClickListener, DetailListAdepter.ItemClickListenner {

    private ImageView mLargeCover;
    private RoundRectImageView mSamllCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private AlbumDetialPresenter mAlbumDetialPresenter;
    private String TAG = "DetailActivity";
    private int mCurrenpage = 1;
    private RecyclerView mDetailList;
    private DetailListAdepter mDetailListAdepter;
    private FrameLayout mDetailListContainer;
    private UILoader mUiLoader;
    private long mCurrentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        mAlbumDetialPresenter = AlbumDetialPresenter.getInstance();
        mAlbumDetialPresenter.registerViewCallback(this);
    }

    private void initView() {
        mDetailListContainer = findViewById(R.id.detail_list_container);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);
            mUiLoader.setOnRetryClickListener(DetailActivity.this);
        }
        mLargeCover = this.findViewById(R.id.iv_large_cover);
        mSamllCover = this.findViewById(R.id.viv_samll_cover);
        mAlbumTitle = this.findViewById(R.id.tv_album_title);
        mAlbumAuthor = this.findViewById(R.id.tv_album_author);

    }

    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detali_list, container, false);

        mDetailList = detailListView.findViewById(R.id.album_detail_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDetailList.setLayoutManager(layoutManager);
        //设置适配器
        mDetailListAdepter = new DetailListAdepter();
        mDetailList.setAdapter(mDetailListAdepter);
        //设置间距
        mDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 4);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 4);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        mDetailListAdepter.setItemClickListenner(this);
        return detailListView;
    }

    @Override
    public void onDetialListLoaded(List<Track> tracks) {
        //判断数据结果根据结果设置ui
        if (tracks == null || tracks.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //跟新设置UI
        mDetailListAdepter.setData(tracks);
    }

    @Override
    public void onAlblmLoaded(Album album) {
        //获取专辑详情内容
        long id = album.getId();
        mCurrentId = id;

        if (mAlbumDetialPresenter != null) {
            mAlbumDetialPresenter.getAlbumDetial((int) id, mCurrenpage);
        }

        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LAODING);
        }
        if (mAlbumTitle != null) {
            mAlbumTitle.setText(album.getAlbumTitle());
        }
        if (mAlbumAuthor != null) {
            mAlbumAuthor.setText(album.getAnnouncer().getNickname());
        }

        if (mLargeCover != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(mLargeCover, new Callback() {
                @Override
                public void onSuccess() {
                    Drawable drawable = mLargeCover.getDrawable();
                    if (drawable != null) {
                        ImageBlur.makeBlur(mLargeCover, DetailActivity.this);
                    }
                }

                @Override
                public void onError() {
                    Log.e(TAG, "onError");
                }
            });
        }
        if (mSamllCover != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(mSamllCover);
        }
    }

    @Override
    public void onNetworkError(int errorCode, String errorMsg) {
        //发生错误，显示网络异常
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onRetryClick() {
        //重新点击加载
        if (mAlbumDetialPresenter != null) {
            mAlbumDetialPresenter.getAlbumDetial((int) mCurrentId, mCurrenpage);
        }
    }

    @Override
    public void onItemClick() {
        //跳转到播放器界面
        Intent intent = new Intent();
        intent.setClass(this,PlayerActivity.class);
        startActivity(intent);
    }
}
