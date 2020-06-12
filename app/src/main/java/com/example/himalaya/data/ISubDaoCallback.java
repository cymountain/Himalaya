package com.example.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubDaoCallback {



    /**
     * 添加的结果回调方法
     *
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除的结果回调方法
     *
     * @param isSuccess
     */
    void onDelResult(boolean isSuccess);

    /**
     * 加载结果
     * @param result
     */
    void onSubListLoaded(List<Album> result);
}
