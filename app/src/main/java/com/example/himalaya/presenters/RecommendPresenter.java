package com.example.himalaya.presenters;

import android.util.Log;

import com.example.himalaya.api.HimalayaApi;
import com.example.himalaya.interfaces.IRecommendPresenter;
import com.example.himalaya.interfaces.IReconmmendCallBack;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

public class RecommendPresenter implements IRecommendPresenter {

    private static final String TAG = "RecommendPresenter";

    private List<IReconmmendCallBack> mCallBacks = new ArrayList<>();
    private List<Album> mCurrentRecommend = null;

    private RecommendPresenter(){
    }
    private static RecommendPresenter sInstance = null;

    /**
     * 获取单例
     *
     * @return
     */
    public static RecommendPresenter getInstance(){
        if (sInstance == null) {
            synchronized (RecommendPresenter.class){
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取当前推荐专辑列表
     *
     * @return 推荐专辑列表，使用这个之前要判空
     */
    public List<Album> getCurrentRecommend(){
        return mCurrentRecommend;
    }

    //获取推荐内容，其实就是猜你喜欢
    //文档SDK3.10.6 获取猜你喜欢专辑
    @Override
    public void getRecommendList() {
        //获取推荐内容
        //封装参数
        updateLoading();

        HimalayaApi himalayaApi = HimalayaApi.getInstance();
        himalayaApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                //数据获取成功
                if (gussLikeAlbumList != null) {
                    List<Album>  albumList = gussLikeAlbumList.getAlbumList();
                    //更新ui
                    //upRecommendUI(albumList);
                    handlerRecommendResult(albumList);
                }
            }
            @Override
            public void onError(int i, String s) {
                //获取失败
                Log.e(TAG,"error -- > code" + i +"error -- > massage" + s);
                handlerError();
            }
        });
    }

    private void handlerError() {
        if (mCallBacks != null) {
            for (IReconmmendCallBack callBack : mCallBacks) {
                callBack.onNetworkError();
            }
        }
    }

    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI更新
        if (albumList != null) {
            //测试当数据为空
            //albumList.clear();
            if (albumList.size() == 0) {
                for (IReconmmendCallBack callBack : mCallBacks){
                    callBack.onEmpty();
                }
            } else {
                for (IReconmmendCallBack callBack : mCallBacks) {
                    callBack.onReconmmendListLoaded(albumList);
                }
                this.mCurrentRecommend = albumList;
            }
        }
    }
    private void updateLoading(){
        for (IReconmmendCallBack callBack : mCallBacks) {
            callBack.onLoading();
        }
    }

    @Override
    public void pullRefreshMor() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IReconmmendCallBack callback) {
        if (mCallBacks != null && !mCallBacks.contains(callback)) {
            mCallBacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IReconmmendCallBack callback) {
        if (mCallBacks != null) {
            mCallBacks.remove(callback);
        }
    }
}
