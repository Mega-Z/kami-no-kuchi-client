package com.megaz.knk.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

public abstract class ElasticScrollActivity extends BaseActivity{
    protected List<ScrollView> lockedScrollViews = new ArrayList<>();
    protected List<ScrollView> lockingScrollViews = new ArrayList<>();
    protected int SCROLL_MAX = 300;
    protected int SCROLL_ELASTIC_POSITION = 150;
    protected float SCROLL_THRESHOLD = 5;
    private float scrollY = 0f;
    private Float prevY;
    private boolean lockingOtherScrollViews = false; // 锁住其它可滚动的view
    private boolean scrolling = true; // 当其它可滚动的view归零时可以进行动态滚动
    private boolean scrollEnabled = true; // 由外部因素决定动态滚动开启
    private ValueAnimator elasticScrollAnimator;

    @Override
    protected void setContent() {

    }

    @Override
    protected void initView() {
        super.initView();
        initScrollParameters();
        setConflictingScrollViews();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setCallback() {
        super.setCallback();
        for(ScrollView scrollView:lockedScrollViews) {
            scrollView.setOnTouchListener(new ScrollLockOnTouchListener());
        }
        for(ScrollView scrollView:lockingScrollViews) {
            scrollView.setOnScrollChangeListener(new ScrollLockOnScrollChangeListener());
        }
    }

    private class ScrollLockOnTouchListener implements View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(lockingOtherScrollViews){
                v.setScrollY(0);
                return true;
            }
            return false;
        }
    }

    private class ScrollLockOnScrollChangeListener implements View.OnScrollChangeListener {

        @Override
        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

            if(scrollY == 0f){
                scrolling = true;
            } else {
                scrolling = false;
            }
            ElasticScrollActivity.this.scrollY = 0;
            ElasticScrollActivity.this.prevY = null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean flagEventConsumed = false;
        if(scrolling && scrollEnabled) {
            if(ev.getAction() == MotionEvent.ACTION_DOWN) {
                prevY = ev.getY();
                if(elasticScrollAnimator != null) {
                    elasticScrollAnimator.cancel();
                }
            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                flagEventConsumed = tryUpdateScrollY(ev);
                prevY = ev.getY();
            } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                flagEventConsumed = tryUpdateScrollY(ev);
                if(scrollY > SCROLL_ELASTIC_POSITION) {
                    startScrollingTo(SCROLL_MAX);
                } else {
                    startScrollingTo(0);
                }
                if(scrollY == 0){
                    lockingOtherScrollViews = false;
                }
            } else {
                scrollY = 0f;
            }
        } else {
            scrollY = 0f;
        }
        if(scrollY > 0) {
            lockingOtherScrollViews = true;
        }
        if(flagEventConsumed){
            updateScrollStatus();
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private class ElasticScrollAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            scrollY = (float) animation.getAnimatedValue();
            updateScrollStatus();
            if(scrollY == 0){
                lockingOtherScrollViews = false;
            }
        }
    }

    private boolean tryUpdateScrollY(MotionEvent ev) {
        if(prevY == null) {
            return false;
        }
        if(ev.getY() - prevY > SCROLL_THRESHOLD && scrollY < SCROLL_MAX) {
            scrollY = Math.min(SCROLL_MAX, scrollY+ev.getY() - prevY);
            return true;
        } else if (ev.getY() - prevY < -1 * SCROLL_THRESHOLD && scrollY > 0) {
            scrollY = Math.max(0, scrollY+ev.getY() - prevY);
            return true;
        } else {
            return false;
        }
    }

    protected void initScrollParameters() {
    }

    protected void setConflictingScrollViews() {

    }

    protected abstract void updateScrollStatus();

    protected Float getScrollProgress() {
        return scrollY / SCROLL_MAX;
    }

    protected int getScrollY() {
        return Math.round(scrollY);
    }

    protected void enableScrolling() {
        scrollEnabled = true;
    }

    protected void disableScrolling() {
        scrollY = 0;
        // updateScrollStatus();
        scrollEnabled = false;
        lockingOtherScrollViews = false;
    }

    protected void startScrollingTo(float dest) {
        dest = Math.max(0, Math.min(1, dest));
        elasticScrollAnimator = ValueAnimator.ofFloat(scrollY, dest * SCROLL_MAX);
        elasticScrollAnimator.addUpdateListener(new ElasticScrollAnimatorUpdateListener());
        elasticScrollAnimator.start();
    }
}
