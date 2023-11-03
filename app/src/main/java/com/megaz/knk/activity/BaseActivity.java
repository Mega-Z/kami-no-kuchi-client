package com.megaz.knk.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {
    protected Toast toast;
    private InputMethodManager inputMethodManager;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;
    protected Typeface typefaceCn, typefaceNum;
    private EditText focusedEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        sharedPreferences = getSharedPreferences("KNK", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        typefaceCn = Typeface.createFromAsset(getAssets(), "fonts/nzbz.ttf");
        typefaceNum = Typeface.createFromAsset(getAssets(), "fonts/tttgbnumber.ttf");
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
        setCallback();
    }

    protected void initView() {

    }

    protected void setCallback() {

    }

    protected void setFocusedEditText(EditText editText) {
        this.focusedEditText = editText;
    }

    protected void hideInputMethod() {
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            if(isTouchView(focusedEditText, ev)){
                focusedEditText.requestFocus();
                return super.dispatchTouchEvent(ev);
            }
            inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static boolean isTouchView(View view, MotionEvent event){
        if (view == null || event == null){
            return false;
        }
        int[] leftTop = {0, 0};
        view.getLocationInWindow(leftTop);
        int left = leftTop[0];
        int top = leftTop[1];
        int bottom = top + view.getHeight();
        int right = left + view.getWidth();
        return event.getRawX() > left && event.getRawX() < right
                && event.getRawY() > top && event.getRawY() < bottom;
    }
}