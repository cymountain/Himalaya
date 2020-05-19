package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;

import java.net.Inet4Address;

public interface IAlbumDetialPresenter extends IBasePresenter<IAlbumDetialViewCallback> {
    //下拉刷新
    void pullRefreshMor();

    //上拉加载更多
    void loadMore();

    /**
     * 获取专辑详情
     *
     * @param albumId
     * @param page
     */
    void getAlbumDetial(int albumId, int page);

}
