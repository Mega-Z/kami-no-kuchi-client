package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.megaz.knk.R;
import com.megaz.knk.activity.FightEffectDetailActivity;
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

    private TextView textBuffTitle, textBuffEffect, textBuffNumber;
    private ImageView imageSourceIcon;
    private LinearLayout buttonBuffDisable;


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
        } else if (buffVo.getEffectValue() >= 1000) {
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
        buttonBuffDisable = view.findViewById(R.id.btn_buff_disable);
        buttonBuffDisable.setVisibility(View.GONE);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void updateBuffNumberByVo(BuffVo buffVo) {
        if(buffVo.getPercent()) {
            textBuffNumber.setText(String.format("%.2f", buffVo.getEffectValue() * 100) + "%");
        } else if (buffVo.getEffectValue() >= 1000) {
            textBuffNumber.setText(String.format("%d", Math.round(buffVo.getEffectValue())));
        } else {
            textBuffNumber.setText(String.format("%.2f", buffVo.getEffectValue()));
        }
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        view.setOnLongClickListener(new BuffOnLongClickListener());
        view.setOnTouchListener(new BuffOnTouchListener());
        view.setOnClickListener(new BuffOnClickListener());
        buttonBuffDisable.setOnClickListener(new DisableBuffOnclickListener());
    }

    private class BuffOnLongClickListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            BuffDetailFragment buffDetailFragment = BuffDetailFragment.newInstance(buffVo);
            buffDetailFragment.show(getParentFragmentManager(), "");
            return false;
        }
    }

    private class BuffOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(buttonBuffDisable.getVisibility() == View.GONE) {
                if(!buffVo.getForced()) {
                    buttonBuffDisable.setVisibility(View.VISIBLE);
                }
            } else {
                buttonBuffDisable.setVisibility(View.GONE);
            }
        }
    }

    private static class BuffOnTouchListener implements View.OnTouchListener{

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // v.performClick();
            if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
                v.setBackgroundResource(R.drawable.bg_enabled_buff_pressed);
            }else{
                v.setBackgroundResource(R.drawable.bg_enabled_buff);
            }
            return false;
        }
    }

    private class DisableBuffOnclickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            ((FightEffectDetailActivity)(requireActivity())).toDisableBuff(buffVo);
        }
    }
}