package com.megaz.knk.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.megaz.knk.R;

import java.text.SimpleDateFormat;
import java.util.Objects;


public class BaseDialogFragment extends DialogFragment {
    protected Toast toast;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;
    protected Typeface typefaceNZBZ, typefaceFZFYKS, typefaceNum;
    protected SimpleDateFormat simpleDateFormat;

    public BaseDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setRetainInstance(true);
        toast = Toast.makeText(requireActivity().getApplicationContext(), "", Toast.LENGTH_SHORT);
        sharedPreferences = requireActivity().getSharedPreferences("KNK", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        typefaceNZBZ = Typeface.createFromAsset(requireActivity().getAssets(), "fonts/nzbz.ttf");
        typefaceFZFYKS = Typeface.createFromAsset(requireActivity().getAssets(), "fonts/fzfyks.ttf");
        typefaceNum = Typeface.createFromAsset(requireActivity().getAssets(), "fonts/tttgbnumber.ttf");
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Objects.requireNonNull(getDialog()).requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setCallback(view);
        initialize(view);
    }

    protected void initView(@NonNull View view) {
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawableResource(R.color.transparent);
    }

    protected void setCallback(@NonNull View view) {

    }

    protected void initialize(@NonNull View view) {

    }
}