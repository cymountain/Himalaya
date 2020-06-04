package com.example.himalaya.api;

import com.example.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

public class HimalayaApi {

    private HimalayaApi() {
    }

    private static HimalayaApi sHimalayaApi = null;

    /**
     * 懒汉式获取单例
     *
     * @return
     */
    public static HimalayaApi getInstance() {
        if (sHimalayaApi == null) {
            synchronized (HimalayaApi.class) {
                if (sHimalayaApi == null) {
                    sHimalayaApi = new HimalayaApi();
                }
            }
        }
        return sHimalayaApi;
    }

    /**
     * 获取推荐内容（猜你喜欢）
     *
     * @param callBack 请求结果的回调
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callBack) {
        Map<String, String> map = new HashMap<>();
        //返回数据的条数
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMEND + "");
        CommonRequest.getGuessLikeAlbum(map, callBack);
    }

    /**
     * 根据专辑id获取到专辑内容.
     *
     * @param callBack  获取专辑详情的回调接口
     * @param albumId   专辑的id
     * @param pageIndex 第几页
     */
    public void getAlbumDetail(IDataCallBack<TrackList> callBack, long albumId, int pageIndex) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, pageIndex + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getTracks(map, callBack);
    }

    /**
     * 根据关键字搜索
     *
     * @param keyWord
     * @param page
     * @param callback
     */
    public void doSearchByKeyWord(String keyWord, int page, IDataCallBack<SearchAlbumList> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyWord);
        map.put(DTransferConstants.CATEGORY_ID, 0 + "");
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getSearchedAlbums(map, callback);
    }

    /**
     * 获取推荐热词
     *
     * @param callback
     */
    public void getHotWords(IDataCallBack<HotWordList> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TOP, Constants.COUNT_HOT_WORD + "");
        CommonRequest.getHotWords(map, callback);
    }

    /**
     * 根据关键字获取联想词
     *
     * @param keyword
     * @param callback
     */
    public void getSuggestWord(String keyword, IDataCallBack<SuggestWords> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        CommonRequest.getSuggestWord(map, callback);
    }
}
