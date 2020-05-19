package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;

public interface IRecommendPresenter extends IBasePresenter<IReconmmendCallBack>{

    //获取推荐内容

    void getRecommendList();

    //下拉刷新

    void pullRefreshMor();

    //上拉加载更多

    void loadMore();

}
