package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.megaz.knk.R;
import com.megaz.knk.manager.EffectComputationManager;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.BuffVo;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnabledBuffFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnabledBuffFragment extends BaseFragment {
    private BuffVo buffVo;
    
    private EffectComputationManager effectComputationManager;

    private TextView textBuffTitle, textBuffEffect, textBuffNumber;
    private ImageView imageSourceIcon;


    public EnabledBuffFragment() {
        // Required empty public constructor
    }

    public static EnabledBuffFragment newInstance(BuffVo buffVo) {
        EnabledBuffFragment fragment = new EnabledBuffFragment();
        Bundle args = new Bundle();
        args.putSerializable("buffVo", buffVo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            buffVo = (BuffVo) getArguments().getSerializable("buffVo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enabled_buff, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        effectComputationManager = new EffectComputationManager(getContext());
        textBuffTitle = view.findViewById(R.id.text_buff_title);
        textBuffTitle.setText(buffVo.getBuffTitle());
        textBuffEffect = view.findViewById(R.id.text_buff_effect);
        textBuffEffect.setBackgroundColor(requireContext()
                .getColor(DynamicStyleUtils.getBuffFieldColor(buffVo.getBuffField())));
        textBuffEffect.setText(buffVo.getEffectText());
        textBuffNumber = view.findViewById(R.id.text_buff_number);
        textBuffNumber.setTypeface(typefaceNum);
        textBuffNumber.setTextColor(requireContext()
                .getColor(DynamicStyleUtils.getBuffFieldColor(buffVo.getBuffField())));
        if(buffVo.getPercent()) {
            textBuffNumber.setText(String.format("%.2f", buffVo.getEffectValue() * 100) + "%");
        } else if (buffVo.getEffectValue() > 10000) {
            textBuffNumber.setText(String.format("%d", Math.round(buffVo.getEffectValue())));
        } else {
            textBuffNumber.setText(String.format("%.2f", buffVo.getEffectValue()));
        }
        imageSourceIcon = view.findViewById(R.id.img_source_icon);
        if(buffVo.getIcon() != null) {
            imageSourceIcon.setImageBitmap(ImageResourceUtils.getIconBitmap(requireContext(), buffVo.getIcon()));
        } else {
            imageSourceIcon.setImageResource(R.drawable.icon_buff_default);
        }
    }
}