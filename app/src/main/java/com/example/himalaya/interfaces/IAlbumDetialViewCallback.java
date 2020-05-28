package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetialViewCallback {

    /**
     * 加载专辑详情内容
     * @param tracks
     */
    void onDetailListLoaded(List<Track> tracks);

    /**
     * 把albm传给ui
     * @param album
     */
    void onAlbumLoaded(Album album);

    /**
     * 网络错误
     */
    void onNetworkError(int errorCode, String errorMsg);

    /**
     * 加载更多的结果，size>0表示加载成功的结果，否则表示加载失败的结果
     *
     * @param size
     */
    void onLoadMoreFinished(int size);

    /**
     * size>0表示刷新成功，size = 0表示不刷新成功
     *
     * @param size
     */
    void onRefreshFinished(int size);


}
