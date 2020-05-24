package com.example.himalaya.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.adepters.PlayListAdepter;
import com.example.himalaya.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public class PopWindow extends PopupWindow {


    private final View mPopVIew;
    private View mCloseBtn;
    private RecyclerView mTrackList;
    private PlayListAdepter mPlayListAdepter;

    public PopWindow() {
        //设置宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //加载view
        setOutsideTouchable(true);
        mPopVIew = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        //设置内容
        setContentView(mPopVIew);
        //设置pop window动画
        setAnimationStyle(R.style.pop_animation);

        initView();
        initEvent();

    }

    private void initView() {
        mCloseBtn = mPopVIew.findViewById(R.id.play_list_close_btn);
        //找控件
        mTrackList = mPopVIew.findViewById(R.id.play_list_rv);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mTrackList.setLayoutManager(layoutManager);
        //设置适配器
        mPlayListAdepter = new PlayListAdepter();
        mTrackList.setAdapter(mPlayListAdepter);
    }

    private void initEvent() {
        //点击pop window消失
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopWindow.this.dismiss();
            }
        });
    }

    /**
     * 给适配器设置数据
     *
     * @param data
     */
    public void setListData(List<Track> data) {
        if (mPlayListAdepter != null) {
            mPlayListAdepter.setData(data);
        }
    }

    public void setCurrentPlayPosition(int position) {
        if (mPlayListAdepter != null) {
            mPlayListAdepter.setCurrentPlayPosition(position);
            mTrackList.scrollToPosition(position);
        }
    }
}
