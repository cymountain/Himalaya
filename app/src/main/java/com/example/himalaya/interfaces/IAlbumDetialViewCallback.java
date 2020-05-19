package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetialViewCallback {

    /**
     * 加载专辑详情内容
     * @param tracks
     */
    void onDetialListLoaded(List<Track> tracks);

    /**
     * 把albm传给ui
     * @param album
     */
    void onAlblmLoaded(Album album);

    /**
     * 网络错误
     */
    void onNetworkError(int errorCode, String errorMsg);


}
