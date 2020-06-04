package com.example.himalaya.presenters;

import android.util.Log;

import com.example.himalaya.api.HimalayaApi;
import com.example.himalaya.interfaces.ISearchCallback;
import com.example.himalaya.interfaces.ISearchPresenter;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.AlbumResult;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

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
        this.mCurrentKeyWord = keyWord;
        search(keyWord);
    }

    private void search(String keyWord) {
        mHimalayaApi.doSearchByKeyWord(keyWord, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                if (albums != null) {
                    Log.e(TAG, "album size -- >" + albums.size());
                    for (ISearchCallback iSearchCallback : mCallbacks) {
                        iSearchCallback.onSearchResultLoad(albums);
                    }
                } else {
                    Log.e(TAG, "album is null");
                }

            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Log.e(TAG, "doSearchByKeyWord errorCode is -->" + errorCode + "doSearchByKeyWord errorMsg is --> " + errorMsg);
                for (ISearchCallback iSearchCallback : mCallbacks) {
                    iSearchCallback.onError(errorCode, errorMsg);
                }
            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyWord);
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getHotWords() {
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
                Log.e(TAG, "getHotWords errorCode is -->" + errorCode + "getHotWords errorMsg is --> " + errorMsg);
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
                    List<AlbumResult> result = suggestWords.getAlbumList();
                    Log.e(TAG, "the suggestWords size is -- >" + result.toString());
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Log.e(TAG, "getRecommendWord errorCode is -->" + errorCode + "getRecommendWord errorMsg is --> " + errorMsg);
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
