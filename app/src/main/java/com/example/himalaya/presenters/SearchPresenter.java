package com.example.himalaya.presenters;

import android.util.Log;

import com.example.himalaya.data.HimalayaApi;
import com.example.himalaya.interfaces.ISearchCallback;
import com.example.himalaya.interfaces.ISearchPresenter;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtils;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private List<Album> mSearchResult = new ArrayList<>();

    private List<ISearchCallback> mCallbacks = new ArrayList<>();
    //当前搜索关键字
    private String mCurrentKeyWord = null;
    private HimalayaApi mHimalayaApi;
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;
    private String TAG = "SearchPresenter";

    private SearchPresenter() {
        mHimalayaApi = HimalayaApi.getInstance();
    }

    private static SearchPresenter sSearchPresenter = null;

    public static SearchPresenter getSearchPresenter() {
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class) {
                if (sSearchPresenter == null) {
                    sSearchPresenter = new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }

    @Override
    public void doSearch(String keyWord) {
        mCurrentPage = DEFAULT_PAGE;
        mSearchResult.clear();
        this.mCurrentKeyWord = keyWord;
        search(keyWord);
    }

    private void search(String keyWord) {
        mHimalayaApi.doSearchByKeyWord(keyWord, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                mSearchResult.addAll(albums);
                if (albums != null) {
                    LogUtils.d(TAG, "album size -- >" + albums.size());
                    if (mIsLoadMore) {
                        for (ISearchCallback iSearchCallback : mCallbacks) {
                            if (albums.size()==0) {
                                iSearchCallback.onLoadMoreResult(mSearchResult, false);
                            }else {
                                iSearchCallback.onLoadMoreResult(mSearchResult, true);
                            }
                        }
                        mIsLoadMore = false;
                    } else {
                        for (ISearchCallback iSearchCallback : mCallbacks) {
                            iSearchCallback.onSearchResultLoad(mSearchResult);
                        }
                    }
                } else {
                    LogUtils.e(TAG, "album is null");
                }

            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.d(TAG, "doSearchByKeyWord errorCode is -->" + errorCode + "doSearchByKeyWord errorMsg is --> " + errorMsg);
                for (ISearchCallback iSearchCallback : mCallbacks) {
                    if (mIsLoadMore) {
                        iSearchCallback.onLoadMoreResult(mSearchResult, false);
                        mCurrentPage--;
                        mIsLoadMore = false;
                    } else {
                        iSearchCallback.onError(errorCode, errorMsg);
                    }
                }
            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyWord);
    }

    private boolean mIsLoadMore = false;

    @Override
    public void loadMore() {
        if (mSearchResult.size()< Constants.COUNT_DEFAULT) {
            for (ISearchCallback iSearchCallback : mCallbacks) {
                iSearchCallback.onLoadMoreResult(mSearchResult,false);
            }
        }else {
            mIsLoadMore = true;
            mCurrentPage++;
            search(mCurrentKeyWord);
        }
    }

    @Override
    public void getHotWords() {
        //todo:热词缓存，意面多次获取
        mHimalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    Log.d(TAG, "the hot words is --> " + hotWords.toString());
                    for (ISearchCallback iSearchCallback : mCallbacks) {
                        iSearchCallback.onHotWordLoad(hotWords);
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.d(TAG, "getHotWords errorCode is -->" + errorCode + "getHotWords errorMsg is --> " + errorMsg);
                for (ISearchCallback iSearchCallback : mCallbacks) {
                    iSearchCallback.onError(errorCode, errorMsg);
                }
            }
        });
    }

    @Override
    public void getRecommendWord(String keyword) {
        mHimalayaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    LogUtils.d(TAG, "the suggestWords size is -- >" + keyWordList.toString());
                    for (ISearchCallback iSearchCallback : mCallbacks) {
                        iSearchCallback.onRecommendWordLoaded(keyWordList);
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.d(TAG, "getRecommendWord errorCode is -->" + errorCode + "getRecommendWord errorMsg is --> " + errorMsg);
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (!mCallbacks.contains(iSearchCallback)) {
            mCallbacks.add(iSearchCallback);

        }
    }

    @Override
    public void unRegisterViewCallback(ISearchCallback iSearchCallback) {
        mCallbacks.remove(iSearchCallback);
    }
}
