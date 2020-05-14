package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface IReconmmendCallBack {

    /**
     * 获取推荐内容结果
     *
     * @param album
     */
    void onReconmmendListLoaded(List<Album> album);

    /**
     * 加载更多
     *
     * @param album
     */
    void onLaoderMore(List<Album> album);

    /**
     * 下拉加载更多
     *
     * @param album
     */
    void onRefreshMore(List<Album> album);

}
