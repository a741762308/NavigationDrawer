package com.jsqix.dq.navigation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by dq on 2016/5/24.
 */
public class ScrollerView extends ScrollView {
    private OnBorderListener listener;

    public ScrollerView(Context context) {
        this(context, null);
    }

    public ScrollerView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.scrollViewStyle);
    }

    public ScrollerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(OnBorderListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener != null) {
            if ((t + getHeight() >= computeVerticalScrollRange()) || (getChildAt(0).getMeasuredHeight() <= getHeight() + getScrollY())) {
                listener.onBottom();
            } else if (getScrollY() == 0) {
                listener.onTop();
            } else {
                listener.onScrollChanged(l, t, oldl, oldt);
            }
        }
    }

    public interface OnBorderListener {
        void onBottom();

        void onTop();

        void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}
