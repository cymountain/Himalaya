package com.example.himalaya.presenters;

import com.example.himalaya.interfaces.IAlbumDetialPresenter;
import com.example.himalaya.interfaces.IAlbumDetialViewCallback;
import com.example.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class AlbumDetailPresenter implements IAlbumDetialPresenter {

    private Album mTargetAlbum = null;
    private List<IAlbumDetialViewCallback> mCallbacks = new ArrayList<>();
    private List<Track> mTracks;
    private  String TAG = "AlbumDetailPresenter";

    private AlbumDetailPresenter(){
    }
    private static AlbumDetailPresenter sInstance = null;

    /**
     * 懒汉式获取单例
     *
     * @return
     */
    public static AlbumDetailPresenter getInstance(){
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class){
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pullRefreshMor() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getAlbumDetial(int albumId, int page) {
        //根据页码获取专辑列表
        final Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, albumId+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT+"");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                //获取到数据
                //Log.d(TAG,"current Thread -- > "+Thread.currentThread().getName());
                if (trackList != null) {
                    mTracks = trackList.getTracks();
                    handlerAlbumDetialResult(mTracks);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                handlerError(errorCode, errorMsg);
            }
        });
    }

    /**
     * 如果网络错误就通知UI
     * @param errorCode
     * @param errorMsg
     */
    private void handlerError(int errorCode, String errorMsg) {
        for (IAlbumDetialViewCallback callback : mCallbacks) {
            callback.onNetworkError(errorCode,errorMsg);
        }
    }

    private void handlerAlbumDetialResult(List<Track> tracks) {
        for (IAlbumDetialViewCallback mCallback : mCallbacks) {
            mCallback.onDetialListLoaded(tracks);
        }

    }

    @Override
    public void registerViewCallback(IAlbumDetialViewCallback detialViewCallback) {
        if (!mCallbacks.contains(detialViewCallback)) {
            mCallbacks.add(detialViewCallback);
            if (mTargetAlbum != null) {
                detialViewCallback.onAlblmLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unRegisterViewCallback(IAlbumDetialViewCallback detialViewCallback) {
        mCallbacks.remove(detialViewCallback);
    }

    public void setTargetAlbum(Album targetAlbum){
        this.mTargetAlbum = targetAlbum;
    }
}
