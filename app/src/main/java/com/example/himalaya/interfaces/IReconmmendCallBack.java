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
     * 网络错误
     *
     */
    void onNetworkError();

    /**
     * 数据为空
     */
    void onEmpty();

    /**
     * z正在加载
     */
    void onLoading();
}
