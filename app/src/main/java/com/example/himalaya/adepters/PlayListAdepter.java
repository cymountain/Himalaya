package com.example.himalaya.adepters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.views.PopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdepter extends RecyclerView.Adapter<PlayListAdepter.InnerHolder> {

    private List<Track> mData = new ArrayList<>();
    private View mItemView;
    private int playingIndex = 0;
    private PopWindow.PlayListItemClickListener mItemClickListener = null;

    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list, parent, false);

        return new InnerHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(position);
                }
            }
        });
        //设置数据给View
        Track track = mData.get(position);
        TextView trackTitleTv = holder.itemView.findViewById(R.id.track_title_tv);
        //设置正在播放的节目颜色
        trackTitleTv.setTextColor(
                BaseApplication.getAppContext().getResources().getColor(
                        playingIndex == position?R.color.playing_color:R.color.play_list_text_color));

        trackTitleTv.setText(track.getTrackTitle());
        //播放列表状态图标
        View playingIconView = holder.itemView.findViewById(R.id.playing_icon_iv);
        playingIconView.setVisibility(playingIndex == position ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Track> data) {
        //设置数据，更新列表
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setCurrentPlayPosition(int position) {
        playingIndex = position;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(PopWindow.PlayListItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
