package com.example.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryDao {

    void setCallback(IHistoryDaoCallback callback);

    /**
     * 添加历史
     *
     * @param track
     */
    void addHistory(Track track);

    /**
     * 删除历史
     *
     * @param track
     */
    void delHistory(Track track);

    /**
     * 清除历史内容
     */
    void clearHistory();

    /**
     * 获取历史内容
     */
    void listHistory();
}
