package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;

public interface ISearchPresenter extends IBasePresenter<ISearchCallback> {

    /**
     * 搜索动作
     *
     * @param keyWord
     */
    void doSearch(String keyWord);

    /**
     * 重新搜索
     */
    void reSearch();

    /**
     * 加载搜索内容
     */
    void loadMore();

    /**
     * 获取热词
     */
    void getHotWords();

    /**
     *获取推荐的词
     *
     * @param keyword
     */
    void getRecommendWord(String keyword);
}
