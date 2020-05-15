package com.example.himalaya.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;

public abstract class UILoader extends FrameLayout {

    private View mLoadingView;
    private View mSuccessView;
    private View mNetworkErrorView;
    private View mEmptyView;

    public enum UIStatus{
        LAODING,SUCCESS,NETWORK_ERROR,EMPTY,NONE
    }
    public UIStatus mCurrentStats = UIStatus.NONE;
    public UILoader(@NonNull Context context) {
        this(context,null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void updateStatus(UIStatus status){
        mCurrentStats = status;
        //更新UI一定要在主线程
        BaseApplication.getHander().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrentStatus();
            }
        });
    }
    /**
     * 初始化UI
     */
    private void init() {
        switchUIByCurrentStatus();
    }

    private void switchUIByCurrentStatus() {
        //加载中
        if (mLoadingView == null) {
            mLoadingView = getLoadingView();
            addView(mLoadingView);
        }
        //是否可见
        mLoadingView.setVisibility(mCurrentStats == UIStatus.LAODING ? VISIBLE : GONE);

        //成功
        if (mSuccessView == null) {
            mSuccessView = getSuccessView(this);
            addView(mSuccessView);
        }
        //是否可见
        mSuccessView.setVisibility(mCurrentStats == UIStatus.SUCCESS ? VISIBLE : GONE);

        //网络错误
        if (mNetworkErrorView == null) {
            mNetworkErrorView = getNetworkErrorView();
            addView(mNetworkErrorView);
        }
        //是否可见
        mNetworkErrorView.setVisibility(mCurrentStats == UIStatus.NETWORK_ERROR ? VISIBLE : GONE);

        //数据为空
        if (mEmptyView == null) {
            mEmptyView = getEmptyView();
            addView(mEmptyView);
        }
        //是否可见
        mEmptyView.setVisibility(mCurrentStats == UIStatus.EMPTY ? VISIBLE : GONE);

    }

    private View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view,this,false);
    }

    private View getNetworkErrorView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_networkerror_view,this,false);
    }

    protected abstract View getSuccessView(ViewGroup container);

    private View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_laoding_view,this,false);
    }
}
