package com.example.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.DetailActivity;
import com.example.himalaya.R;
import com.example.himalaya.adepters.AlbumListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.IReconmmendCallBack;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.RecommendPresenter;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class RecommendFragmnet extends BaseFragment implements IReconmmendCallBack, UILoader.OnRetryClickListener, AlbumListAdapter.OnRecommendItemClickListener {
    private static final String TAG = "RecommendFragmnet";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private AlbumListAdapter mRecommendListAdapter;
    private RecommendPresenter mReconmmendPresenter;
    private UILoader mUiLoader;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {

        mUiLoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater,container);
            }
        };

        //获取到逻辑层对象
        mReconmmendPresenter = RecommendPresenter.getInstance();
        //先要设置通知接口的注册
        mReconmmendPresenter.registerViewCallback(this);
        //获取推荐列表
        mReconmmendPresenter.getRecommendList();
        //返回view给界面显示

        if (mUiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        }

        mUiLoader.setOnRetryClickListener(this);

        return mUiLoader;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        //view加载完成
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend,container,false);
        //步骤
        //1.找到控件
        TwinklingRefreshLayout refreshLayout = mRootView.findViewById(R.id.over_scroll_view);
        refreshLayout.setPureScrollModeOn();
        mRecommendRv = mRootView.findViewById(R.id.recommend_list);
        //2.设置布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);
        mRecommendRv.addItemDecoration( new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 4);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 4);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        //3.设置适配器
        mRecommendListAdapter = new AlbumListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);
        mRecommendListAdapter.setOnRecommendItemClickListener(this);
        return mRootView;
    }


    @Override
    public void onReconmmendListLoaded(List<Album> album) {
        //当我们获取到推荐内容的时候，就会被调用
        //把数据设置给适配器并更新
        mRecommendListAdapter.setDeta(album);
        mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        mUiLoader.updateStatus(UILoader.UIStatus.LAODING);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口注册，
        if (mReconmmendPresenter != null) {
            mReconmmendPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        //表示网络不佳，用户点击了重试
        //重新获取即可
        if (mReconmmendPresenter != null) {
            mReconmmendPresenter.getRecommendList();
        }
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //item被点击了
        Intent intent = new Intent();
        intent.setClass(getContext(), DetailActivity.class);
        startActivity(intent);

    }
}
