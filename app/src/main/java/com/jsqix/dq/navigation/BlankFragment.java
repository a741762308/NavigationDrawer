package com.jsqix.dq.navigation;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jsqix.dq.navigation.adapter.RecyclerViewAdapter;
import com.jsqix.dq.navigation.view.RefreshLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {
    private RefreshLayout refreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter adapter;
    int mCount=10;

    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        adapter = new RecyclerViewAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);
        refreshLayout.setOnPullLoadMoreListener(new RefreshLayout.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setPullLoadMoreCompleted();
                        mCount=10;
                        adapter.setmCount(mCount);
                        adapter.notifyDataSetChanged();
                    }
                }, 1000);

            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setPullLoadMoreCompleted();
                        mCount+=10;
                        adapter.setmCount(mCount);
                        adapter.notifyDataSetChanged();
                    }
                }, 1000);
            }
        });
    }

}
