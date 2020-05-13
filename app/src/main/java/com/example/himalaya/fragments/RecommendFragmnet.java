package com.example.himalaya.fragments;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.adepters.RecommendListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragmnet extends BaseFragment {
    private static final String TAG = "RecommendFragmnet";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter mRecommendListAdapter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {

        //view加载完成
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend,container,false);

        //步骤
        //1.找到控件
        mRecommendRv = mRootView.findViewById(R.id.recommend_list);
        //2.设置布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);
        //3.设置适配器
        mRecommendListAdapter = new RecommendListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);

        //拿数据回来
        getRecommendData();


        //返回view给界面显示
        return mRootView;
    }
        //获取推荐内容，其实就是猜你喜欢
        //文档SDK3.10.6 获取猜你喜欢专辑
    private void getRecommendData() {
        //封装参数
        Map<String, String> map = new HashMap<>();
        //返回数据的条数
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMAND_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                //数据获取成功
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    //更新ui
                    upRecommendUI(albumList);

                }
            }

            @Override
            public void onError(int i, String s) {
                //获取失败
                Log.e(TAG,"error -- > code" + i +"error -- > massage" + s);
            }
        });
    }

    private void upRecommendUI(List<Album> albumList) {
        //把数据设置给适配器并更新
        mRecommendListAdapter.setDeta(albumList);
    }
}
