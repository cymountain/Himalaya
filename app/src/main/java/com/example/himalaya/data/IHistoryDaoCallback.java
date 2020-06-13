package com.example.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryDaoCallback {
    /**
     * 添加历史的结果
     *
     * @param isSuccess
     */
    void onHistoryAdd(boolean isSuccess);

    /**
     * 删除历史的结果
     *
     * @param isSuccess
     */
    void onHistoryDel(boolean isSuccess);

    /**
     * 加载历史的结果
     *
     * @param tracks
     */
    void onHistoryLoaded(List<Track> tracks);

    /**
     * 清除历史的结果
     *
     * @param isSuccess
     */
    void onHistoryClean(boolean isSuccess);
}
