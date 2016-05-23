package com.jsqix.dq.navigation.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.jsqix.dq.navigation.R;

/**
 * Created by dq on 2016/5/23.
 */
public class RefreshLayout extends SwipeRefreshLayout {
    private RecyclerView recyclerView;
    private ScrollView scrollView;
    private View mFootView;
    private PullLoadMoreListener mPullLoadMoreListener;
    private boolean hasMore = true;
    //数据不足一屏时是否打开上拉加载模式
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    private Context mContext;

    private int mLastMotionY;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    private void initView() {
        RelativeLayout layout = new RelativeLayout(mContext);
        mFootView = LayoutInflater.from(mContext).inflate(R.layout.view_footer, null);
        RelativeLayout.LayoutParams lp0 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mFootView.setId(R.id.load_more);
        mFootView.setVisibility(View.GONE);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout.addView(mFootView, lp1);
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof RecyclerView || getChildAt(i) instanceof ScrollView) {
                RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                lp2.addRule(RelativeLayout.ABOVE, mFootView.getId());
                if (getChildAt(i) instanceof RecyclerView) {
                    recyclerView = (RecyclerView) getChildAt(i);
                    removeView(getChildAt(i));
                    layout.addView(recyclerView, lp2);
                } else if (getChildAt(i) instanceof ScrollView) {
                    scrollView = (ScrollView) getChildAt(i);
                    removeView(getChildAt(i));
                    layout.addView(scrollView, lp2);
                }
            }

        }
        if (recyclerView == null && scrollView == null) {
            throw new IllegalArgumentException("layout must contain scrollView or recyclerView");
        }
        addView(layout, lp0);

        if (recyclerView != null) {
            recyclerView.addOnScrollListener(new RecyclerViewOnScroll());
            recyclerView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (isRefresh || isLoadMore) {
                        return true;
                    }
                    return false;
                }
            });
        }
        if (scrollView != null) {
            scrollView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (isRefresh || isLoadMore) {
                        return true;
                    }

                    return false;
                }
            });
        }
        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isRefresh()) {
                    setIsRefresh(true);
                    refresh();
                }
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int y = (int) ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 首先拦截down事件,记录y坐标
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // deltaY > 0 是向下运动,< 0是向上运动
                int deltaY = y - mLastMotionY;
                if (scrollView != null) {
                    View child = scrollView.getChildAt(0);
                    if (deltaY > 0) {
                        if (scrollView.getScrollY() == 0) {//刷新
                            if (!isLoadMore()) {
                                setPullRefreshEnable(true);
                            }
                        } else {
                            setPullRefreshEnable(false);
                        }
                    } else if (deltaY < 0) {
                        System.out.println("getMeasuredHeight="+child.getMeasuredHeight()+"\tgetHeight="+scrollView.getHeight()+"\tgetScrollY="+scrollView.getScrollY());
                        if (child.getMeasuredHeight() <= scrollView.getHeight() + scrollView.getScrollY()) {//加载
                            setPullRefreshEnable(false);
                            setIsLoadMore(true);
                            return true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isLoadMore()) {
                    loadMore();
                }
                break;

        }
        return super.onTouchEvent(ev);
    }

    public void setOnPullLoadMoreListener(PullLoadMoreListener listener) {
        mPullLoadMoreListener = listener;
    }

    public void refresh() {
        if (mPullLoadMoreListener != null) {
            mPullLoadMoreListener.onRefresh();
        }
    }

    public void setPullLoadMoreCompleted() {
        isRefresh = false;
        setRefreshing(false);

        isLoadMore = false;
        mFootView.setVisibility(View.GONE);

    }

    public void loadMore() {
        if (mPullLoadMoreListener != null && hasMore) {
            mFootView.setVisibility(View.VISIBLE);
            mPullLoadMoreListener.onLoadMore();

        }
    }

    public void setPullRefreshEnable(boolean enable) {
        setEnabled(enable);
    }

    public boolean getPullRefreshEnable() {
        return isEnabled();
    }

    public boolean isLoadMore() {
        return isLoadMore;
    }

    public void setIsLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setIsRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public interface PullLoadMoreListener {
        public void onRefresh();

        public void onLoadMore();
    }

    class RecyclerViewOnScroll extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItem = 0;
            int firstVisibleItem = 0;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int totalItemCount = layoutManager.getItemCount();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) layoutManager);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                firstVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            } else if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = ((GridLayoutManager) layoutManager);
                //Position to find the final item of the current LayoutManager
                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                firstVisibleItem = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = ((StaggeredGridLayoutManager) layoutManager);
                // since may lead to the final item has more than one StaggeredGridLayoutManager the particularity of the so here that is an array
                // this array into an array of position and then take the maximum value that is the last show the position value
                int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                lastVisibleItem = findMax(lastPositions);
                firstVisibleItem = staggeredGridLayoutManager.findFirstVisibleItemPositions(lastPositions)[0];
            }
            if (firstVisibleItem == 0) {
                if (!isLoadMore()) {
                    setPullRefreshEnable(true);
                }
            } else {
                setPullRefreshEnable(false);
            }


            /**
             * Either horizontal or vertical
             */
            if (!isRefresh() && isHasMore() && (lastVisibleItem >= totalItemCount - 1)
                    && !isLoadMore() && (dx > 0 || dy > 0)) {
                setIsLoadMore(true);
                loadMore();
            }

        }
        //To find the maximum value in the array

        private int findMax(int[] lastPositions) {

            int max = lastPositions[0];
            for (int value : lastPositions) {
                //       int max    = Math.max(lastPositions,value);
                if (value > max) {
                    max = value;
                }
            }
            return max;
        }
    }
}
