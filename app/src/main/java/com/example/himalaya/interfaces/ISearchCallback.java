package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchCallback {

    /**
     * 搜索专辑结果回调
     *
     * @param result
     */
    void onSearchResultLoad(List<Album> result);

    /**
     * 获取推荐热词回调方法
     *
     * @param hotWordList
     */
    void onHotWordLoad(List<HotWord> hotWordList);

    /**
     * 加载更多结果返回
     *
     * @param result 结果
     * @param isOkay true加载成功，false表示没有更多
     */
    void onLoadMoreResult(List<Album> result, boolean isOkay);

    /**
     * 联想关键字推荐结果
     *
     * @param keyWordList
     */
    void onRecommendWordLoaded(List<QueryResult> keyWordList);

    /**
     * 当搜索出错时通知
     * @param errorCode
     * @param errorMsg
     */
    void onError(int errorCode,String errorMsg);
}
