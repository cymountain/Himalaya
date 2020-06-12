package com.example.himalaya;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.himalaya.adepters.PlayerTrackPageAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.LogUtils;
import com.example.himalaya.views.PopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {

    private static final String TAG = "PlayerActivity";
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView mTotalDuration;
    private TextView mCurrentPosition;
    private SeekBar mDurationBar;
    private int mCurrentProgress = 0;
    private boolean mIsUserTouchProgressBar = false;
    private ImageView mPalyNextBtn;
    private ImageView mPalyPreBtn;
    private TextView mTrackTitleTv;
    private String mTrackTitleText;
    private ViewPager mTrackPageView;
    private PlayerTrackPageAdapter mTrackPageAdapter;
    private boolean isUserSlidePager = false;
    private ImageView mPlayerModeSwitchBtn;

    private XmPlayListControl.PlayMode mCurrentMode = PLAY_MODEL_LIST;

    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sPlayModeRule = new HashMap<>();

    //1.默认列表播放PLAY_MODEL_LIST
    //2.列表循环    PLAY_MODEL_LIST_LOOP
    //3.随机播放    PLAY_MODEL_RANDOM
    //4.单曲循环    PLAY_MODEL_SINGLE_LOOP
    static {
        sPlayModeRule.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        sPlayModeRule.put(PLAY_MODEL_LIST_LOOP, PLAY_MODEL_RANDOM);
        sPlayModeRule.put(PLAY_MODEL_RANDOM, PLAY_MODEL_SINGLE_LOOP);
        sPlayModeRule.put(PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_LIST);
    }

    private ImageView mPlayListBtn;
    private PopWindow mPopWindow;
    private ValueAnimator mEnterBgAnimator;
    private ValueAnimator mOutBgAnimator;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        initEvent();
        initBgAnimation();
    }

    private void initBgAnimation() {
        mEnterBgAnimator = ValueAnimator.ofFloat(1.0f, 0.7f);
        mEnterBgAnimator.setDuration(400);
        mEnterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                //透明度
                //背景渐变透明度
                updateBgAlpha(animatedValue);
            }
        });
        mOutBgAnimator = ValueAnimator.ofFloat(0.7f, 1.0f);
        mOutBgAnimator.setDuration(400);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                updateBgAlpha(animatedValue);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter = null;
        }
    }


    /**
     * 给控件设置监听
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是播放状态就暂停，是暂停状态就播放
                if (mPlayerPresenter.isPlaying()) {
                    mPlayerPresenter.pause();
                } else {
                    mPlayerPresenter.play();
                }

            }
        });
        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if (isFromUser) {
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //手离开的时候跳转播放
                mIsUserTouchProgressBar = false;
                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });
        mPalyNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();
                }
            }
        });

        mPalyPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playPre();
                }
            }
        });
        mTrackPageView.addOnPageChangeListener(this);

        mTrackPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        isUserSlidePager = true;
                        break;
                }
                return false;
            }
        });

        mPlayerModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPlayMode();
            }
        });

        mPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                mEnterBgAnimator.start();
            }
        });

        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //pop窗体消失后恢复
                mOutBgAnimator.start();
            }
        });

        mPopWindow.setPlayListItemClickListener(new PopWindow.PlayListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //说明播放列表被点击
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(position);
                }
            }
        });

        mPopWindow.setPlayListActionListener(new PopWindow.PlayListActionListener() {
            @Override
            public void onPlayModeClick() {
                //切换播放模式
                switchPlayMode();
            }

            @Override
            public void onOrderClick() {
                //切换列表顺序
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.reversePlayList();
                }
            }
        });
    }

    private void switchPlayMode() {
        //处理播放模式切换
        //根据当前的mode获取到下一个mode
        XmPlayListControl.PlayMode playMode = sPlayModeRule.get(mCurrentMode);
        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);
        }
    }

    private void updateBgAlpha(float alpha) {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }

    /**
     * 根据当前播放模式更新图标
     */
    private void updatePlayModeBtnImg() {
        int resId = R.drawable.selector_player_playlist;
        switch (mCurrentMode) {
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_player_playlist;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_player_playloop;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_player_round;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_player_one;
                break;
        }
        mPlayerModeSwitchBtn.setImageResource(resId);
    }

    /**
     * 找到控件
     */
    private void initView() {
        mControlBtn = this.findViewById(R.id.play_or_pause_bt);
        mTotalDuration = this.findViewById(R.id.track_duration);
        mCurrentPosition = this.findViewById(R.id.current_position);
        mDurationBar = this.findViewById(R.id.track_seek_bar);
        mPalyNextBtn = this.findViewById(R.id.player_next);
        mPalyPreBtn = this.findViewById(R.id.player_pre);
        mTrackTitleTv = this.findViewById(R.id.track_title);
        mPlayerModeSwitchBtn = this.findViewById(R.id.player_mode_switch_btn);
        //播放列表
        mPlayListBtn = this.findViewById(R.id.player_list_btn);
        if (!TextUtils.isEmpty(mTrackTitleText)) {
            mTrackTitleTv.setText(mTrackTitleText);
        }
        mTrackPageView = this.findViewById(R.id.track_pager_view);
        //创建适配器
        mTrackPageAdapter = new PlayerTrackPageAdapter();
        //设置适配器
        mTrackPageView.setAdapter(mTrackPageAdapter);
        mPopWindow = new PopWindow();
    }

    @Override
    public void onPlayStart() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.ic_pause);
        }
    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.ic_playsound);
        }
    }

    @Override
    public void onPlayStop() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.ic_playsound);
        }

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
        //把数据设置到设配器
        if (mTrackPageAdapter != null) {
            mTrackPageAdapter.setData(list);
        }
        //数据回来后给播放列表
        if (mPopWindow != null) {
            mPopWindow.setListData(list);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        //更新播放模式，修改UI显示
        mCurrentMode = playMode;
        //更新pop里的模播放模式图标
        mPopWindow.updatePlayMode(mCurrentMode);
        updatePlayModeBtnImg();
    }

    @Override
    public void onProgressChange(int currentDuration, int total) {

        mDurationBar.setMax(total);
        //更新播放进度和进度条
        String totalDuration;
        String currentPosition;
        if (total > 1000 * 60 * 60) {
            totalDuration = mHourFormat.format(total);
            currentPosition = mHourFormat.format(currentDuration);
        } else {
            totalDuration = mMinFormat.format(total);
            currentPosition = mMinFormat.format(currentDuration);
        }
        if (mTotalDuration != null) {
            mTotalDuration.setText(totalDuration);

        }
        //当前时间
        if (mCurrentPosition != null) {
            mCurrentPosition.setText(currentPosition);
        }
        //跟新进度条
        if (!mIsUserTouchProgressBar) {
            mDurationBar.setProgress(currentDuration);
        }
    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinish() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track==null) {
            LogUtils.d(TAG, "onTrackUpdate: Track is null");
            return;
        }
        this.mTrackTitleText = track.getTrackTitle();
        if (mTrackTitleTv != null) {
            //设置当前界面标题
            mTrackTitleTv.setText(mTrackTitleText);
        }
        //当节目改变的时候我们就获取到当前播放器的位置
        //当前节目改变以后也要修改显示的图片
        if (mTrackPageView != null) {
            mTrackPageView.setCurrentItem(playIndex, true);
        }
        //修改播放列表里播放位置
        if (mPopWindow != null) {
            mPopWindow.setCurrentPlayPosition(playIndex);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {
        mPopWindow.updateOrderIcon(isReverse);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //当页面触摸的时候就切换
        if (mPlayerPresenter != null && isUserSlidePager) {
            mPlayerPresenter.playByIndex(position);
        }
        isUserSlidePager = false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
