package com.example.himalaya.presenters;

import com.example.himalaya.data.HimalayaApi;
import com.example.himalaya.interfaces.IAlbumDetialPresenter;
import com.example.himalaya.interfaces.IAlbumDetialViewCallback;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailPresenter implements IAlbumDetialPresenter {

    private Album mTargetAlbum = null;
    private List<IAlbumDetialViewCallback> mCallbacks = new ArrayList<>();
    private List<Track> mTracks = new ArrayList<>();
    private String TAG = "AlbumDetailPresenter";
    //当前专辑id
    private int mCurrentAlbumId = -1;
    //当前页
    private int mCurrentPageIndex = 0;

    private AlbumDetailPresenter() {
    }

    private static AlbumDetailPresenter sInstance = null;

    /**
     * 懒汉式获取单例
     *
     * @return
     */
    public static AlbumDetailPresenter getInstance() {
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class) {
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
        //加载更多内容
        mCurrentPageIndex++;
        doLoaded(true,mCurrentAlbumId);
    }

    private void doLoaded(final boolean isLoadMore,int albumId) {
        HimalayaApi himalayaApi = HimalayaApi.getInstance();
        himalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                //获取到数据
                //Log.d(TAG,"current Thread -- > "+Thread.currentThread().getName());
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    if (isLoadMore) {
                        //上拉加载更多，结果放到后面
                        mTracks.addAll(tracks);
                        int size = tracks.size();
                        handlerLoadMoreResult(size);
                    } else {
                        //下拉加载，结果放到最前面
                        mTracks.addAll(0, tracks);
                    }
                    handlerAlbumDetialResult(mTracks);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                if (isLoadMore) {
                    mCurrentPageIndex--;
                }
                handlerError(errorCode, errorMsg);
            }
        },mCurrentAlbumId,mCurrentPageIndex);
    }

    /**
     * 处理加载更多的结果
     * @param size
     */
    private void handlerLoadMoreResult(int size) {
        for (IAlbumDetialViewCallback callback : mCallbacks) {
            callback.onLoadMoreFinished(size);
        }
    }

    @Override
    public void getAlbumDetail(int albumId, int page) {
        mTracks.clear();
        this.mCurrentAlbumId = albumId;
        this.mCurrentPageIndex = page;
        //根据页码获取专辑列表
       doLoaded(false,mCurrentAlbumId);


    }

    /**
     * 如果网络错误就通知UI
     *
     * @param errorCode
     * @param errorMsg
     */
    private void handlerError(int errorCode, String errorMsg) {
        for (IAlbumDetialViewCallback callback : mCallbacks) {
            callback.onNetworkError(errorCode, errorMsg);
        }
    }

    private void handlerAlbumDetialResult(List<Track> tracks) {
        for (IAlbumDetialViewCallback mCallback : mCallbacks) {
            mCallback.onDetailListLoaded(tracks);
        }

    }

    @Override
    public void registerViewCallback(IAlbumDetialViewCallback detialViewCallback) {
        if (!mCallbacks.contains(detialViewCallback)) {
            mCallbacks.add(detialViewCallback);
            if (mTargetAlbum != null) {
                detialViewCallback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unRegisterViewCallback(IAlbumDetialViewCallback detialViewCallback) {
        mCallbacks.remove(detialViewCallback);
    }

    public void setTargetAlbum(Album targetAlbum) {
        this.mTargetAlbum = targetAlbum;
    }
}
