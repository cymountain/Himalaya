package com.example.himalaya.adepters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.himalaya.R;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayerTrackPageAdapter extends PagerAdapter {
    private List<Track> mData = new ArrayList<>();

    @Override
    public int getCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_track_page, container, false);
        container.addView(itemView);
        //设置数据
        ImageView item = itemView.findViewById(R.id.track_pager_item);
        //设置图片
        Track track = mData.get(position);
        String CoverUrlLarge = track.getCoverUrlLarge();
        Picasso.with(container.getContext()).load(CoverUrlLarge).into(item);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
       container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setData(List<Track> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

}
