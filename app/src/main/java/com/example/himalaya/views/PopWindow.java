package com.example.himalaya.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.adepters.PlayListAdepter;
import com.example.himalaya.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public class PopWindow extends PopupWindow {


    private final View mPopView;
    private View mCloseBtn;
    private RecyclerView mTrackList;
    private PlayListAdepter mPlayListAdepter;
    private TextView mPlayModeTv;
    private ImageView mPlayModeIv;
    private View mPlayModeContainer;
    private PlayListActionListener mPlayModeClickListener = null;
    private View mPlayOrderContainer;
    private ImageView mOrderIcon;
    private TextView mOrderText;

    public PopWindow() {
        //设置宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //加载view
        setOutsideTouchable(true);
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        //设置内容
        setContentView(mPopView);
        //设置pop window动画
        setAnimationStyle(R.style.pop_animation);

        initView();
        initEvent();

    }

    private void initView() {
        mCloseBtn = mPopView.findViewById(R.id.play_list_close_btn);
        //找控件
        mTrackList = mPopView.findViewById(R.id.play_list_rv);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mTrackList.setLayoutManager(layoutManager);
        //设置适配器
        mPlayListAdepter = new PlayListAdepter();
        mTrackList.setAdapter(mPlayListAdepter);
        mPlayModeTv = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlayModeIv = mPopView.findViewById(R.id.play_list_play_mode_iv);
        mPlayModeContainer = mPopView.findViewById(R.id.play_list_play_mode_container);
        //
        mPlayOrderContainer = mPopView.findViewById(R.id.play_list_order_container);
        mOrderIcon = mPopView.findViewById(R.id.play_list_order_iv);
        mOrderText = mPopView.findViewById(R.id.play_list_order_tv);
    }

    private void initEvent() {
        //点击pop window消失
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopWindow.this.dismiss();
            }
        });
        mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放模式
                if (mPlayModeClickListener != null) {
                    mPlayModeClickListener.onPlayModeClick();
                }
            }
        });
        mPlayOrderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放列表的顺序
                mPlayModeClickListener.onOrderClick();
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

    public void setPlayListItemClickListener(PlayListItemClickListener listener) {
        mPlayListAdepter.setOnItemClickListener(listener);
    }

    /**
     * 跟新播放列表的播放模式
     *
     * @param currentMode
     */
    public void updatePlayMode(XmPlayListControl.PlayMode currentMode) {
        updatePlayModeBtnImg(currentMode);
    }

    /**
     * 更新顺序或者逆序
     *
     * @param isOrder
     */
    public void updateOrderIcon(boolean isOrder) {
        mOrderIcon.setImageResource(isOrder ? R.mipmap.ic_forward : R.mipmap.ic_unorder);
        mOrderText.setText(BaseApplication.getAppContext().getResources().getString(isOrder ? R.string.is_order:R.string.un_order));
    }

    /**
     * 根据当前播放模式更新图标
     */
    private void updatePlayModeBtnImg(XmPlayListControl.PlayMode playMode) {
        int resId = R.drawable.selector_player_playlist;
        int textId = R.string.play_mode_order_text;
        switch (playMode) {
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_player_playlist;
                textId = R.string.play_mode_order_text;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_player_playloop;
                textId = R.string.play_mode_list_play_text;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_player_round;
                textId = R.string.play_mode_random_text;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_player_one;
                textId = R.string.play_mode_single_play_text;
                break;
        }
        mPlayModeIv.setImageResource(resId);
        mPlayModeTv.setText(textId);
    }

    public interface PlayListItemClickListener {
        void onItemClick(int position);
    }

    public void setPlayListActionListener(PlayListActionListener playModeListener) {
        mPlayModeClickListener = playModeListener;
    }

    public interface PlayListActionListener {
        //播放模式切换点击事件
        void onPlayModeClick();

        //播放列表顺序点击事件
        void onOrderClick();
    }
}
