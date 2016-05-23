package com.jsqix.dq.navigation;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.jsqix.dq.navigation.view.RefreshLayout;

public class BlankActivity extends AppCompatActivity {
    private RefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_redresh);
        refreshLayout = (RefreshLayout) findViewById(R.id.refresh);
        refreshLayout.setOnPullLoadMoreListener(new RefreshLayout.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setPullLoadMoreCompleted();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setPullLoadMoreCompleted();
                    }
                }, 1000);
            }
        });
    }
}
