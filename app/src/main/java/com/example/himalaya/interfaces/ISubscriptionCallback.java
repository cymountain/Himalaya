package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubscriptionCallback {

    /**
     * 调用添加的时候去通知ui
     *
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 取消订阅的时候通知ui
     *
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);

    /**
     * 订阅专辑的加载
     *
     * @param albums
     */
    void onSubscriptionsLoaded(List<Album> albums);

    /**
     *
     * 订阅最大数量
     */
    void onSubFull();
}
