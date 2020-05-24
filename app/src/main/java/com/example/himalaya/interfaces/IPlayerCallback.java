package com.example.himalaya.interfaces;

import android.os.Trace;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallback {

    /**
     * 开始播放
     */
    void onPlayStart();

    /**
     * 播放暂停
     */
    void onPlayPause();

    /**
     * 播放停止
     */
    void onPlayStop();

    /**
     * 播放错误
     */
    void onPlayError();

    /**
     * 下一曲
     */
    void onNextPlay(Track track);

    /**
     * 上一曲
     */
    void onPrePlay(Track track);

    /**
     * 获取播放列表
     *
     * @param list 列表数据
     */
    void onListLoaded(List<Track> list);

    /**
     * 切换播放模式
     *
     * @param playMode
     */
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    /**
     * 播放进度条
     *
     * @param currentProgress
     * @param total
     */
    void onProgressChange(int currentProgress, int total);

    /**
     * 广告加载
     */
    void onAdLoading();

    /**
     * 广告加载结束
     */
    void onAdFinish();

    /**
     * 更新当前播放器节目
     * aa
     *
     * @param track
     */
    void onTrackUpdade(Track track, int playIndex);
}
