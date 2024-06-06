package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.megaz.knk.R;
import com.megaz.knk.activity.CharacterDetailActivity;
import com.megaz.knk.activity.FightEffectDetailActivity;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.computation.DirectDamageEffect;
import com.megaz.knk.computation.FightEffect;
import com.megaz.knk.manager.EffectComputationManager;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.vo.EffectDetailVo;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FightEffectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FightEffectFragment extends BaseFragment {
    @Getter
    @Setter
    private FightEffect fightEffect, fightEffectBaseline;
    @Setter
    private boolean comparing;
    private boolean canCritical;

    private EffectComputationManager effectComputationManager;

    private TextView textEffectDesc, textCritOrNot, textAverage, textEffectNumber, textEffectNumberBaseline;
    private LinearLayout layoutEffectNumber, layoutEffectNumberBaseline;


    public FightEffectFragment() {
        // Required empty public constructor
    }

    public static FightEffectFragment newInstance(FightEffect fightEffect, FightEffect fightEffectBaseline, boolean comparing) {
        FightEffectFragment fragment = new FightEffectFragment();
        Bundle args = new Bundle();
        args.putSerializable("fightEffect", fightEffect);
        args.putSerializable("fightEffectBaseline", fightEffectBaseline);
        args.putSerializable("comparing", comparing);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fightEffect = (FightEffect) getArguments().getSerializable("fightEffect");
            fightEffectBaseline = (FightEffect) getArguments().getSerializable("fightEffectBaseline");
            comparing = (boolean) getArguments().getSerializable("comparing");
            effectComputationManager = new EffectComputationManager(getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fight_effect, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        assert fightEffect != null || fightEffectBaseline != null;
        textEffectDesc = view.findViewById(R.id.text_effect_desc);
        textCritOrNot = view.findViewById(R.id.text_crit_or_not);
        textAverage = view.findViewById(R.id.text_average);
        textEffectNumber = view.findViewById(R.id.text_effect_number);
        textEffectNumberBaseline = view.findViewById(R.id.text_effect_number_baseline);
        layoutEffectNumber = view.findViewById(R.id.layout_effect_number);
        layoutEffectNumberBaseline = view.findViewById(R.id.layout_effect_number_baseline);

        textEffectNumber.setTypeface(typefaceNum);
        textEffectNumberBaseline.setTypeface(typefaceNum);
        int numberColor = fightEffect != null ?
                requireContext().getColor(DynamicStyleUtils.getFightEffectColor(fightEffect, R.color.white)) :
                requireContext().getColor(DynamicStyleUtils.getFightEffectColor(fightEffectBaseline, R.color.white));
        textEffectNumber.setTextColor(numberColor);
        textEffectNumberBaseline.setTextColor(numberColor);
        textEffectDesc.setText(fightEffect != null ? fightEffect.getEffectDesc() : fightEffectBaseline.getEffectDesc());
        canCritical = fightEffect != null ?
                fightEffect instanceof DirectDamageEffect : fightEffectBaseline instanceof DirectDamageEffect;
        updateEffectView();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        layoutEffectNumber.setOnTouchListener(new FightEffectOnTouchListener());
        layoutEffectNumber.setOnClickListener(new FightEffectOnClickListener(false));
        layoutEffectNumberBaseline.setOnTouchListener(new FightEffectOnTouchListener());
        layoutEffectNumberBaseline.setOnClickListener(new FightEffectOnClickListener(true));
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void updateEffectView() {
        EffectDetailVo effectDetailVo = null, effectDetailVoBaseline = null;
        if (fightEffect != null) {
            effectDetailVo = effectComputationManager.createFightEffectDetail(fightEffect);
        }
        if (fightEffectBaseline != null) {
            effectDetailVoBaseline = effectComputationManager.createFightEffectDetail(fightEffectBaseline);
        }

        if (comparing) {
            layoutEffectNumberBaseline.setVisibility(View.VISIBLE);
            if (fightEffectBaseline != null) {
                assert  effectDetailVoBaseline != null;
                textEffectNumberBaseline.setText(effectDetailVoBaseline.getNumber());
            } else {
                textEffectNumberBaseline.setText(getString(R.string.text_effect_unavailable));
            }
            if (fightEffect != null && fightEffectBaseline != null) {
                assert effectDetailVo != null && effectDetailVoBaseline != null;
                if (effectDetailVo.compareTo(effectDetailVoBaseline) > 0) {
                    textEffectNumber.setText(effectDetailVo.getNumber() + "▲");
                } else if (effectDetailVo.compareTo(effectDetailVoBaseline) < 0) {
                    textEffectNumber.setText(effectDetailVo.getNumber() + "▼");
                } else {
                    textEffectNumber.setText(effectDetailVo.getNumber());
                }
            } else if (fightEffect != null) {
                assert effectDetailVo != null;
                textEffectNumber.setText(effectDetailVo.getNumber());
            } else {
                textEffectNumber.setText(getString(R.string.text_effect_unavailable));
            }
        } else {
            layoutEffectNumberBaseline.setVisibility(View.GONE);
            assert effectDetailVo != null;
            if (canCritical) {
                textEffectNumber.setText(effectDetailVo.getNumberWithCritical());
            } else {
                textEffectNumber.setText(effectDetailVo.getNumber());
            }
        }

        if (canCritical && comparing) {
            textAverage.setVisibility(View.VISIBLE);
            textCritOrNot.setVisibility(View.GONE);
        } else if (canCritical) {
            textCritOrNot.setVisibility(View.VISIBLE);
            textAverage.setVisibility(View.GONE);
        } else {
            textCritOrNot.setVisibility(View.GONE);
            textAverage.setVisibility(View.GONE);
        }
    }

    private class FightEffectOnTouchListener implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                v.setBackgroundColor(requireContext().getColor(R.color.fight_effect_press));
            } else {
                v.setBackgroundColor(requireContext().getColor(R.color.transparent));
            }
            return false;
        }
    }

    private class FightEffectOnClickListener implements View.OnClickListener {

        boolean isBaseline;

        public FightEffectOnClickListener(boolean isBaseline) {
            this.isBaseline = isBaseline;
        }

        @Override
        public void onClick(View v) {
            if (isBaseline)
                ((CharacterDetailActivity) requireActivity()).toShowFightEffectDetail(fightEffectBaseline, isBaseline);
            else
                ((CharacterDetailActivity) requireActivity()).toShowFightEffectDetail(fightEffect, isBaseline);
        }
    }
}