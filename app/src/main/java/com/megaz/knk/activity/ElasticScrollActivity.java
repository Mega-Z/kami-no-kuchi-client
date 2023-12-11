package com.megaz.knk.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.util.Log;
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
    private boolean scrollViewsLocked = false;
    private boolean scrollEnabled = true;
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
            if(scrollViewsLocked){
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
                scrollEnabled = true;
            } else {
                scrollEnabled = false;
            }
            ElasticScrollActivity.this.scrollY = 0;
            ElasticScrollActivity.this.prevY = null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean flagEventConsumed = false;
        if(scrollEnabled) {
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
                    elasticScrollAnimator = ValueAnimator.ofFloat(scrollY, SCROLL_MAX);
                } else {
                    elasticScrollAnimator = ValueAnimator.ofFloat(scrollY, 0);
                }
                elasticScrollAnimator.addUpdateListener(new ElasticScrollAnimatorUpdateListener());
                elasticScrollAnimator.start();
                if(scrollY == 0){
                    scrollViewsLocked = false;
                }
            } else {
                scrollY = 0f;
            }
        } else {
            scrollY = 0f;
        }
        if(scrollY > 0) {
            scrollViewsLocked = true;
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
                scrollViewsLocked = false;
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

}
