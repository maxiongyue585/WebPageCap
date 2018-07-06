package com.xm.demo.refreshurl.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xm.demo.refreshurl.R;

import java.util.ArrayList;
import java.util.List;

/**
 * recycleview适配器
 */
public class UrlsAdapter extends RecyclerView.Adapter<UrlsViewHolder> {
    protected Context mContext;
    protected ArrayList<String> mDatas;

    public UrlsAdapter(Context mContext, ArrayList<String> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    @NonNull
    @Override
    public UrlsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UrlsViewHolder holder = new UrlsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_urls, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UrlsViewHolder holder, int position) {
        holder.tv.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}
