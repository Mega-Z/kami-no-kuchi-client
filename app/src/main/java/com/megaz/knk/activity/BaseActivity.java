package com.megaz.knk.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.megaz.knk.KnkDatabase;
import com.megaz.knk.utils.ViewUtils;

import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {
    protected Toast toast;
    protected KnkDatabase knkDatabase;
    private InputMethodManager inputMethodManager;
    protected DisplayMetrics displayMetrics;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;
    protected Typeface typefaceNZBZ, typefaceFZFYKS, typefaceNum;
    private EditText focusedEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent();
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        knkDatabase = KnkDatabase.getKnkDatabase(getApplicationContext());
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        sharedPreferences = getSharedPreferences("KNK", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        typefaceNZBZ = Typeface.createFromAsset(getAssets(), "fonts/nzbz.ttf");
        typefaceFZFYKS = Typeface.createFromAsset(getAssets(), "fonts/fzfyks.ttf");
        typefaceNum = Typeface.createFromAsset(getAssets(), "fonts/tttgbnumber.ttf");
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
        setCallback();
        initialize();
    }

    protected abstract void setContent();

    protected void initView() {

    }

    protected void setCallback() {

    }

    protected void initialize() {

    }

    private void setFocusedEditText(EditText editText) {
        this.focusedEditText = editText;
    }

    public void hideInputMethod() {
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            if(ViewUtils.isTouchView(focusedEditText, ev)){
                focusedEditText.requestFocus();
                focusedEditText.setCursorVisible(true);
                return super.dispatchTouchEvent(ev);
            }
            if(focusedEditText != null) {
                ((EditText)focusedEditText).setCursorVisible(false);
            }
            inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static class EditTextOnTouchListener implements View.OnTouchListener {

        private BaseActivity activity;

        public EditTextOnTouchListener(BaseActivity activity) {
            this.activity = activity;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            activity.setFocusedEditText((EditText) v);
            return false;
        }
    }

    protected class KnkFragmentPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList;

        public KnkFragmentPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragmentList) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragmentList = fragmentList;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return this.fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return this.fragmentList==null?0:this.fragmentList.size();
        }
    }


}