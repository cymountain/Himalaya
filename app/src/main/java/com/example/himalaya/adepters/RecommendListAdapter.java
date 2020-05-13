package com.example.himalaya.adepters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerHoder> {
    private List<Album> mData = new ArrayList<>();
    @NonNull
    @Override
    //找到View
    public InnerHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);
        return new InnerHoder(itemView);
    }
    //设置数据
    @Override
    public void onBindViewHolder(@NonNull InnerHoder holder, int position) {
        holder.itemView.getTag(position);
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        //返回要显示的个数
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setDeta(List<Album> albumList) {
        if (mData != null) {
            mData.clear();
            mData.addAll(albumList);
        }
        //更新UI
        notifyDataSetChanged();
    }

    public class InnerHoder extends RecyclerView.ViewHolder {
        public InnerHoder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到这个控件，设置数据
            //专辑封面
            ImageView albumCoverIv = itemView.findViewById(R.id.album_cover_img);
            //标题
            TextView albumTitletv = itemView.findViewById(R.id.album_title_tv);
            //描述
            TextView albumdesctv = itemView.findViewById(R.id.album_description_tv);
            //播放数量
            TextView albumPlaytv = itemView.findViewById(R.id.album_play_count);
            //专辑数量
            TextView albumContenttv = itemView.findViewById(R.id.album_content_size);

            albumTitletv.setText(album.getAlbumTitle());
            albumdesctv.setText(album.getAlbumIntro());
            albumPlaytv.setText(album.getPlayCount() + "");
            albumContenttv.setText(album.getIncludeTrackCount() + "");

            Picasso.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(albumCoverIv);

        }
    }
}
