package com.megaz.knk.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.megaz.knk.R;

public class WeaponWishParamFragment extends CharacterWishParamFragment {
    private Button btnDestined;

    public WeaponWishParamFragment() {
        // Required empty public constructor
    }
    public static WeaponWishParamFragment newInstance() {
        return new WeaponWishParamFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weapon_wish_param, container, false);
    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        btnDestined = view.findViewById(R.id.btn_destined);
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        btnDestined.setOnClickListener(new DestinedBtnOnClickListener());
    }

    private class DestinedBtnOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String text = btnDestined.getText().toString();
            switch (btnDestined.getText().toString()) {
                case "0":
                    btnDestined.setText("1");
                    break;
                case "1":
                    btnDestined.setText("2");
                    break;
                case "2":
                    btnDestined.setText("0");
                    break;
            }
        }
    }

    public int getDestined() {
        return Integer.parseInt(btnDestined.getText().toString());
    }
}
