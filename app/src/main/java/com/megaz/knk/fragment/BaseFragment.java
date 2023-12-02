package com.megaz.knk.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;


public class BaseFragment extends Fragment {
    protected Toast toast;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;
    protected Typeface typefaceNZBZ, typefaceFZFYKS, typefaceNum;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "", Toast.LENGTH_SHORT);
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("KNK", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        typefaceNZBZ = Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(), "fonts/nzbz.ttf");
        typefaceFZFYKS = Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(), "fonts/fzfyks.ttf");
        typefaceNum = Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(), "fonts/tttgbnumber.ttf");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setCallback(view);
    }

    protected void initView(@NonNull View view) {

    }

    protected void setCallback(@NonNull View view) {

    }
}