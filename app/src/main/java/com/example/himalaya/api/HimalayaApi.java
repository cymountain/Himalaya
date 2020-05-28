package com.example.himalaya.api;

import com.example.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

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
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMAND + "");
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
}
