package com.xm.demo.refreshurl.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

import com.xm.demo.refreshurl.R;
import com.xm.demo.refreshurl.adapter.UrlsAdapter;
import com.xm.demo.refreshurl.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UrlsActivity extends AppCompatActivity {

    @BindView(R.id.rcy_urls)
    RecyclerView rcyUrls;

    private ArrayList<String> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urls);
        ButterKnife.bind(this);
        mDatas = Utils.getUrls();
        rcyUrls.setLayoutManager(new LinearLayoutManager(this));
        rcyUrls.setAdapter(new UrlsAdapter(this, mDatas));
        rcyUrls.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

}
