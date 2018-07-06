package com.xm.demo.refreshurl.adapter;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;

import com.xm.demo.refreshurl.R;

/**
 * viewHolder
 */
public class UrlsViewHolder extends ViewHolder {
    TextView tv;

    public UrlsViewHolder(View itemView) {
        super(itemView);
        tv = itemView.findViewById(R.id.id_num);
    }
}
