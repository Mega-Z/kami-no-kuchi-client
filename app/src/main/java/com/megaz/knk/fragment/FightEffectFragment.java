package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FightEffectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FightEffectFragment extends BaseFragment {
    private FightEffect fightEffect;

    private EffectComputationManager effectComputationManager;

    private TextView textEffectDesc, textCritOrNot,textEffectNumber;


    public FightEffectFragment() {
        // Required empty public constructor
    }

    public static FightEffectFragment newInstance(FightEffect fightEffect) {
        FightEffectFragment fragment = new FightEffectFragment();
        Bundle args = new Bundle();
        args.putSerializable("fightEffect", fightEffect);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            fightEffect = (FightEffect) getArguments().getSerializable("fightEffect");
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
        textEffectDesc = view.findViewById(R.id.text_effect_desc);
        textCritOrNot = view.findViewById(R.id.text_crit_or_not);
        textEffectNumber = view.findViewById(R.id.text_effect_number);
        textEffectNumber.setTypeface(typefaceNum);
        textEffectNumber.setTextColor(requireContext()
                .getColor(DynamicStyleUtils.getFightEffectColor(fightEffect, R.color.white)));
        effectComputationManager = new EffectComputationManager(getContext());
        updateEffectView();
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        view.setOnTouchListener(new FightEffectOnTouchListener());
        view.setOnClickListener(new FightEffectOnClickListener());
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void updateEffectView() {
        textEffectDesc.setText(fightEffect.getEffectDesc());
        EffectDetailVo effectDetailVo = effectComputationManager.createFightEffectDetail(fightEffect);
        textEffectNumber.setText(effectDetailVo.getEffectValue());
        if(fightEffect instanceof DirectDamageEffect) {
            textCritOrNot.setVisibility(View.VISIBLE);
        } else {
            textCritOrNot.setVisibility(View.GONE);
        }
    }
    private class FightEffectOnTouchListener implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
                v.setBackgroundColor(requireContext().getColor(R.color.fight_effect_press));
            }else{
                v.setBackgroundColor(requireContext().getColor(R.color.transparent));
            }
            return false;
        }
    }

    private class FightEffectOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ((CharacterDetailActivity)requireActivity()).toShowFightEffectDetail(fightEffect);
        }
    }
}