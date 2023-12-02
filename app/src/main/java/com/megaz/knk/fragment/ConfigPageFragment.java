package com.megaz.knk.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.megaz.knk.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigPageFragment extends BaseFragment {


    public ConfigPageFragment() {
        // Required empty public constructor
    }
    public static ConfigPageFragment newInstance() {
        return new ConfigPageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_config_page, container, false);
    }
}