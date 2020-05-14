package com.example.himalaya.interfaces;

public interface IRecommendPresenter {

    //获取推荐内容

    void getRecommendList();

    //下拉刷新

    void pullRefreshMor();

    //上拉加载更多

    void loadMore();

    /**
     * 此方法注册UI的回调
     * @param callback
     */
    void registerViewCallback(IReconmmendCallBack callback);

    /**
     * 取消Ui的回调注册
     * @param callback
     */
    void unRegisterViewCallback(IReconmmendCallBack callback);
}
