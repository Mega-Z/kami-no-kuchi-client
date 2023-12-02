package com.megaz.knk.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.megaz.knk.R;
import com.megaz.knk.activity.WishCalculatorActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WishPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WishPageFragment extends BaseFragment {

    private Button btnWishCalculator;

    public WishPageFragment() {
        // Required empty public constructor
    }

    public static WishPageFragment newInstance() {
        return new WishPageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wish_page, container, false);
    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        btnWishCalculator = view.findViewById(R.id.btn_wish_calculator);
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        btnWishCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WishCalculatorActivity.class);
                startActivity(intent);
            }
        });
    }
}