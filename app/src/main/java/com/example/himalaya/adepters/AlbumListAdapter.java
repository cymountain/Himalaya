package com.example.himalaya.adepters;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHoder> {
    private static final String TAG = "RecommendListAdapter";
    private List<Album> mData = new ArrayList<>();
    private OnRecommendItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    //找到View
    public InnerHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);
        return new InnerHoder(itemView);
    }
    //设置数据
    @Override
    public void onBindViewHolder(@NonNull InnerHoder holder, final int position) {
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    int clickPosition = (int)v.getTag();
                    mItemClickListener.onItemClick(clickPosition,mData.get(position));
                }
                Log.e(TAG,"position is -- >"+v.getTag());
            }
        });
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

            //Picasso.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(albumCoverIv);
            //String coverUrlLarge = album.getCoverUrlLarge().toString();
            String coverUrlLarge = album.getCoverUrlLarge();
            if (!TextUtils.isEmpty(coverUrlLarge)) {
                Picasso.Builder builder = new Picasso.Builder(itemView.getContext());
                builder.listener(new Picasso.Listener()
                {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        exception.printStackTrace();
                    }
                });
                builder.build().load(coverUrlLarge)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .placeholder(R.color.load_color)
                        .noFade()
                        .error(R.color.error_color).into(albumCoverIv);

                Log.e(TAG,"获取到图片。");
            } else {
                Log.e(TAG,"未获取到图片。");
            }
        }
    }

    public void setOnRecommendItemClickListener(OnRecommendItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public interface OnRecommendItemClickListener{
        void onItemClick(int position, Album album);
    }
}
