package com.example.himalaya.presenters;

import android.util.Log;

import com.example.himalaya.interfaces.IRecommendPresenter;
import com.example.himalaya.interfaces.IReconmmendCallBack;
import com.example.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReconmmendPresenter implements IRecommendPresenter {

    private static final String TAG = "ReconmmendPresenter";

    private List<IReconmmendCallBack> mCallBacks = new ArrayList<>();

    private ReconmmendPresenter(){
    }
    private static ReconmmendPresenter sInstance = null;

    /**
     * 获取单例
     *
     * @return
     */
    public static ReconmmendPresenter getInstance(){
        if (sInstance == null) {
            synchronized (ReconmmendPresenter.class){
                if (sInstance == null) {
                    sInstance = new ReconmmendPresenter();
                }
            }
        }
        return sInstance;
    }

    //获取推荐内容，其实就是猜你喜欢
    //文档SDK3.10.6 获取猜你喜欢专辑
    @Override
    public void getRecommendList() {
        //获取推荐内容
        //封装参数
        Map<String, String> map = new HashMap<>();
        //返回数据的条数
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMAND_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
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
            }
        });
    }

    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI更新
        if (mCallBacks != null) {
            for (IReconmmendCallBack callBack : mCallBacks) {
                callBack.onReconmmendListLoaded(albumList);
            }
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
            mCallBacks.remove(mCallBacks);
        }
    }
}