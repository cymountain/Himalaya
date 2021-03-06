package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * 订阅不超过100个
 */
public interface ISubscriptionPresenter extends IBasePresenter<ISubscriptionCallback> {

    /**
     * 添加订阅
     *
     * @param album
     */
    void addSubscription(Album album);

    /**
     * 删除订阅
     *
     * @param album
     */
    void deleteSubscription(Album album);

    /**
     *
     *获取订阅列表
     */
    void getSubscriptionList();

    /**
     * 判断专辑是否搜藏
     * @param album
     */
    boolean isSubscription(Album album);
}
