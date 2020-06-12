package com.example.himalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.himalaya.data.HimalayaApi;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.interfaces.IPlayerPresenter;
import com.example.himalaya.utils.LogUtils;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private List<IPlayerCallback> mIPlayerCallbacks = new ArrayList<>();

    private XmPlayerManager mPlayerManager;
    private static final String TAG = "PlayerPresenter";
    public static final int DEFAULT_PLAY_INDEX = 0;
    private Track mCurrentTrack;
    private int mCurrentIndex = DEFAULT_PLAY_INDEX;
    private final SharedPreferences mPlayModeSp;
    private XmPlayListControl.PlayMode mCurrentPlayMode = PLAY_MODEL_LIST;
    private boolean mIsReserve = false;

    /**
     * PLAY_MODEL_LIST
     * PLAY_MODEL_LIST_LOOP
     * PLAY_MODEL_RANDOM
     * PLAY_MODEL_SINGLE_LOOP
     */
    public static final int PLAY_MODEL_LIST_INT = 0;
    public static final int PLAY_MODEL_LIST_LOOP_INT = 1;
    public static final int PLAY_MODEL_RANDOM_INT = 2;
    public static final int PLAY_MODEL_SINGLE_LOOP_INT = 3;

    //sp的 key 和 name
    public static final String PLAY_MODE_SP_NAME = "PlayMode";
    public static final String PLAY_MODE_SP_KEY = "currentPlayMode";
    private int mCurrentPlayProgressPos = 0;
    private int mCurrentPlayProgressDuration = 0;

    private PlayerPresenter() {
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告物料相关的
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器相关的接口
        mPlayerManager.addPlayerStatusListener(this);
        //记录当前的播放模式
        mPlayModeSp = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODE_SP_NAME, Context.MODE_PRIVATE);

    }

    private static PlayerPresenter sPlayerPresenter;

    public static PlayerPresenter getPlayerPresenter() {
        if (sPlayerPresenter == null) {
            synchronized (PlayerPresenter.class) {
                if (sPlayerPresenter == null) {
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }

    private boolean isPlayListSet = false;

    public void setPlayList(List<Track> list, int playIndex) {
        if (mPlayerManager != null) {
            mPlayerManager.setPlayList(list, playIndex);
            isPlayListSet = true;
            mCurrentTrack = list.get(playIndex);
            mCurrentIndex = playIndex;
        } else {
            LogUtils.e(TAG, "mPlayerManager is null");
        }
    }

    @Override
    public void play() {
        if (isPlayListSet) {
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (isPlayListSet) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        //播放前一首
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        //播放后一首
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {
            mCurrentPlayMode = mode;
            mPlayerManager.setPlayMode(mode);
            //更新播放模式
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onPlayModeChange(mode);
            }
            //保存到sp
            SharedPreferences.Editor edit = mPlayModeSp.edit();
            edit.putInt(PLAY_MODE_SP_KEY, getIntByPlayMode(mode));
            edit.commit();
        }
    }

    private int getIntByPlayMode(XmPlayListControl.PlayMode mode) {
        switch (mode) {
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
        }
        return PLAY_MODEL_LIST_INT;
    }

    private XmPlayListControl.PlayMode getModeByMode(int index) {
        switch (index) {
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM;
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;
        }
        return PLAY_MODEL_LIST;
    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onListLoaded(playList);
            }
        }

    }

    @Override
    public void playByIndex(int index) {
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }
    }

    @Override
    public void seekTo(int progress) {
        //跟新播放器进度
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlaying() {
        //返回当前是否正在播放
        return mPlayerManager.isPlaying();
    }

    @Override
    public void reversePlayList() {
        //反转播放列表
        List<Track> playList = mPlayerManager.getPlayList();
        Collections.reverse(playList);
        mIsReserve = !mIsReserve;
        //第一个参数播放列表，第二个参数播放位置
        mCurrentIndex = playList.size() - 1 - mCurrentIndex;
        mPlayerManager.setPlayList(playList, mCurrentIndex);
        //更新ui
        mCurrentTrack = (Track) mPlayerManager.getCurrSound();
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onListLoaded(playList);
            iPlayerCallback.onTrackUpdate(mCurrentTrack, mCurrentIndex);
            iPlayerCallback.updateListOrder(mIsReserve);
        }


    }

    @Override
    public void playByAlbumId(long id) {
        //获取专辑内容
        HimalayaApi himalayaApi = HimalayaApi.getInstance();
        himalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                //设置给播放器
                List<Track> tracks = trackList.getTracks();
                if (trackList != null && tracks.size() > 0) {
                    mPlayerManager.setPlayList(tracks, 0);
                    isPlayListSet = true;
                    mCurrentTrack = tracks.get(DEFAULT_PLAY_INDEX);
                    mCurrentIndex = DEFAULT_PLAY_INDEX;
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        }, (int) id, 1);

    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        //先跟新回调
        if (!mIPlayerCallbacks.contains(iPlayerCallback)) {
            mIPlayerCallbacks.add(iPlayerCallback);
        }
        //跟新之前先让ViewPager有数据
        getPlayList();
        iPlayerCallback.onTrackUpdate(mCurrentTrack, mCurrentIndex);
        iPlayerCallback.onProgressChange(mCurrentPlayProgressPos,mCurrentPlayProgressDuration);
        handlePlayStates(iPlayerCallback);
        //从sp里拿mode
        int anInt = mPlayModeSp.getInt(PLAY_MODE_SP_KEY, PLAY_MODEL_LIST_INT);
        mCurrentPlayMode = getModeByMode(anInt);
        iPlayerCallback.onPlayModeChange(mCurrentPlayMode);
    }

    private void handlePlayStates(IPlayerCallback iPlayerCallback) {
        int playerStatus = mPlayerManager.getPlayerStatus();
        //根据状态调用接口方法
        if (PlayerConstants.STATE_STARTED==playerStatus) {
            iPlayerCallback.onPlayStart();
        }else {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {
        mIPlayerCallbacks.remove(iPlayerCallback);
    }

    //========================================广告相关的回调方法  Start====================================
    @Override
    public void onStartGetAdsInfo() {
        Log.d(TAG, "onStartGetAdsInfo");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        Log.d(TAG, "onGetAdsInfo...");

    }

    @Override
    public void onAdsStartBuffering() {
        Log.d(TAG, "onAdsStartBuffering...");

    }

    @Override
    public void onAdsStopBuffering() {
        Log.d(TAG, "onAdsStopBuffering...");

    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        Log.d(TAG, "onStartPlayAds...");

    }

    @Override
    public void onCompletePlayAds() {
        Log.d(TAG, "onCompletePlayAds...");

    }

    @Override
    public void onError(int what, int extra) {
        Log.d(TAG, "what is --->" + what + "extra is --->" + extra);

    }
    //========================================广告相关的回调方法  End====================================

    //========================================播放器相关的回调方法  Start====================================
    @Override
    public void onPlayStart() {
        Log.d(TAG, "onPlayStart...");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        Log.d(TAG, "onPlayPause...");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        Log.d(TAG, "onPlayStop...");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        Log.d(TAG, "onSoundPlayComplete...");
    }

    @Override
    public void onSoundPrepared() {
        Log.d(TAG, "onSoundPrepared...");
        mPlayerManager.setPlayMode(mCurrentPlayMode);
        if (mPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            //播放器准备好了
            mPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        Log.d(TAG, "onSoundSwitch...");
        mCurrentIndex = mPlayerManager.getCurrentIndex();
        if (curModel instanceof Track) {
            Track currentTrack = (Track) curModel;
            mCurrentTrack = currentTrack;
            LogUtils.d(TAG, "title is  === >" + currentTrack.getTrackTitle());
            //更新Ui
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onTrackUpdate(mCurrentTrack, mCurrentIndex);
            }
        }
    }

    @Override
    public void onBufferingStart() {
        Log.d(TAG, "onBufferingStart...");
    }

    @Override
    public void onBufferingStop() {
        Log.d(TAG, "onBufferingStop...");
    }

    @Override
    public void onBufferProgress(int progress) {
        Log.d(TAG, "play Progress..." + progress);

    }

    @Override
    public void onPlayProgress(int currPos, int duration) {
        this.mCurrentPlayProgressPos =currPos;
        this.mCurrentPlayProgressDuration = duration;
        //Log.d(TAG,"onPlayProgress...");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onProgressChange(currPos, duration);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        Log.d(TAG, "onError Exception is --->" + e);
        return false;
    }

    public boolean hasPlayList() {
        return isPlayListSet;
    }
    //========================================播放器相关的回调方法  End====================================
}
