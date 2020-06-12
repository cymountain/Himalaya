package com.example.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.DetailActivity;
import com.example.himalaya.R;
import com.example.himalaya.adepters.AlbumListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.ISubscriptionCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.SubscriptionPresenter;
import com.example.himalaya.utils.Constants;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class    SubscriptionFragment extends BaseFragment implements ISubscriptionCallback, AlbumListAdapter.OnAlbumItemClickListener, AlbumListAdapter.OnAlbumLongClickListener {

    private SubscriptionPresenter mSubscriptionPresenter;
    private RecyclerView mSubListView;
    private AlbumListAdapter mAlbumListAdapter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_subscribtion,container,false);

        TwinklingRefreshLayout refreshLayout = rootView.findViewById(R.id.over_scroll_view);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadmore(false);
        mSubListView = rootView.findViewById(R.id.sub_list);
        mSubListView.setLayoutManager(new LinearLayoutManager( container.getContext()));
        mSubListView.addItemDecoration( new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 4);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 4);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        //
        mAlbumListAdapter = new AlbumListAdapter();
        mAlbumListAdapter.setOnAlbumItemClickListener(this);
        mAlbumListAdapter.setOnAlbumLongClickListener(this);
        mSubListView.setAdapter(mAlbumListAdapter);
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.getSubscriptionList();
        return rootView;
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {

    }

    @Override
    public void onSubscriptionsLoaded(List<Album> albums) {
        //更新ui
        if (mAlbumListAdapter != null) {
            mAlbumListAdapter.setDeta(albums);
        }
    }

    @Override
    public void onSubFull() {
        Toast.makeText(getActivity(),"订阅已满"+ Constants.MAX_SUB_COUNT,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口注册，
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }
        mAlbumListAdapter.setOnAlbumItemClickListener(null);
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //item被点击了
        Intent intent = new Intent();
        intent.setClass(getContext(), DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Album album) {
        //推荐界面长按事件

    }
}
