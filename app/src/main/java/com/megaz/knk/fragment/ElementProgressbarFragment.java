package com.megaz.knk.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.megaz.knk.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ElementProgressbarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ElementProgressbarFragment extends Fragment {

    private LinearLayout layoutProgressbar;
    private ImageView imageProgressbar;
    private int width;
    private float progress;

    public ElementProgressbarFragment() {
        // Required empty public constructor
    }

    public static ElementProgressbarFragment newInstance() {
        return new ElementProgressbarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_element_progressbar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutProgressbar = view.findViewById(R.id.layout_progressbar);
        imageProgressbar = view.findViewById(R.id.img_progressbar);
        width = getResources().getDimensionPixelOffset(R.dimen.dp_180);
        setProgress(0);
    }

    public void setProgress(float progress) {
        progress = Math.max(0, progress);
        progress = Math.min(1, progress);
        this.progress = progress;
        layoutProgressbar.setTranslationX(-1 * (1-progress) * width);
        imageProgressbar.setTranslationX((1-progress) * width);
    }

    public float getProgress() {
        return progress;
    }
}