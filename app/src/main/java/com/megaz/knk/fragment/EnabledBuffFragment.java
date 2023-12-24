package com.megaz.knk.fragment;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

    private int CROSS_WIDTH;
    private final float FADE_OUT_VALUE = 0.4f;
    private final float FADE_IN_VALUE = 0.8f;

    private ValueAnimator animatorCrossExtend, animatorCrossRetract;

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
        CROSS_WIDTH = getResources().getDimensionPixelOffset(R.dimen.dp_60);
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        view.setOnLongClickListener(new BuffOnLongClickListener());
        view.setOnTouchListener(new BuffOnTouchListener());
        view.setOnClickListener(new BuffOnClickListener());
        buttonBuffDisable.setOnClickListener(new DisableBuffOnclickListener());
        animatorCrossExtend = ValueAnimator.ofFloat(0,1);
        animatorCrossExtend.setDuration(200);
        animatorCrossExtend.addUpdateListener(new CrossAnimatorUpdateListener());
        animatorCrossRetract = ValueAnimator.ofFloat(1,0);
        animatorCrossRetract.setDuration(200);
        animatorCrossRetract.setStartDelay(2000);
        animatorCrossRetract.addUpdateListener(new CrossAnimatorUpdateListener());
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void updateBuffNumberByVo(BuffVo buffVo) {
        if(buffVo.getPercent()) {
            textBuffNumber.setText(String.format("%.1f", buffVo.getEffectValue() * 100) + "%");
        } else if (buffVo.getEffectValue() >= 1000) {
            textBuffNumber.setText(String.format("%d", Math.round(buffVo.getEffectValue())));
        } else {
            textBuffNumber.setText(String.format("%.2f", buffVo.getEffectValue()));
        }
    }

    private class CrossAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener{
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) buttonBuffDisable.getLayoutParams();
            layoutParams.width = Math.round(CROSS_WIDTH * (float) animation.getAnimatedValue());
            buttonBuffDisable.setLayoutParams(layoutParams);
            if((float)animation.getAnimatedValue() <= 0){
                buttonBuffDisable.setVisibility(View.GONE);
            } else {
                buttonBuffDisable.setVisibility(View.VISIBLE);
            }
            if((float)animation.getAnimatedValue() >= FADE_OUT_VALUE &&
                    (float)animation.getAnimatedValue() <= FADE_IN_VALUE) {
                buttonBuffDisable.setAlpha(((float)animation.getAnimatedValue() - FADE_OUT_VALUE)/(FADE_IN_VALUE - FADE_OUT_VALUE));
            } else if ((float)animation.getAnimatedValue() > FADE_IN_VALUE) {
                buttonBuffDisable.setAlpha(1f);
            } else {
                buttonBuffDisable.setAlpha(0f);
            }
        }
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
                animatorCrossExtend.start();
                animatorCrossRetract.start();
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