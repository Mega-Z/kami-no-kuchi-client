package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.megaz.knk.R;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.BuffVo;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuffDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuffDetailFragment extends DialogFragment {
    private BuffVo buffVo;

    private TextView textBuffTitle, textBuffEffect, textBuffNumber, textBuffDesc;
    private ImageView imageSourceIcon;

    public BuffDetailFragment() {
        // Required empty public constructor
    }

    public static BuffDetailFragment newInstance(BuffVo buffVo) {
        BuffDetailFragment fragment = new BuffDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("buffVo", buffVo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buffVo = (BuffVo) getArguments().getSerializable("buffVo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buff_detail, container, false);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textBuffTitle = view.findViewById(R.id.text_buff_title);
        textBuffTitle.setText(buffVo.getBuffTitle());
        textBuffEffect = view.findViewById(R.id.text_buff_effect);
        textBuffEffect.setBackgroundColor(requireContext()
                .getColor(DynamicStyleUtils.getBuffFieldColor(buffVo.getBuffField())));
        textBuffEffect.setText(buffVo.getEffectText());
        textBuffNumber = view.findViewById(R.id.text_buff_number);
        textBuffNumber.setTypeface(Typeface.createFromAsset(requireActivity().getAssets(), "fonts/tttgbnumber.ttf"));
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
        textBuffDesc = view.findViewById(R.id.text_buff_desc);
        textBuffDesc.setText(buffVo.getBuffDesc()+"\n");

        Objects.requireNonNull(getDialog()).requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });
    }
}