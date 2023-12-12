package com.megaz.knk.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.megaz.knk.R;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.computation.FightEffect;

import com.megaz.knk.manager.EffectComputationManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EffectComputationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EffectComputationFragment extends BaseFragment {

    private CharacterAttribute characterAttribute;
    private List<FightEffect> fightEffectList;

    private Handler effectUpdateHandler;
    private EffectComputationManager effectComputationManager;

    private LinearLayout layoutEffects;
    private TextView textNoEffect;


    public EffectComputationFragment() {
        // Required empty public constructor
    }
    public static EffectComputationFragment newInstance(CharacterAttribute characterAttribute) {
        EffectComputationFragment fragment = new EffectComputationFragment();
        Bundle args = new Bundle();
        args.putSerializable("characterAttribute", characterAttribute);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            characterAttribute = (CharacterAttribute) getArguments().getSerializable("characterAttribute");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_effect_computation, container, false);
    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        effectComputationManager = new EffectComputationManager(getContext());
        layoutEffects = view.findViewById(R.id.layout_effects);
        textNoEffect = view.findViewById(R.id.text_no_effect);
        new Thread(this::initFightEffects).start();
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        effectUpdateHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleEffectUpdate(msg);
            }
        };
    }

    private void initFightEffects() {
        try {
            List<FightEffect> fightEffectList = effectComputationManager.getFightEffectsByCharacterAttribute(characterAttribute);
            Message msg = new Message();
            msg.what = 0;
            msg.obj = fightEffectList;
            effectUpdateHandler.sendMessage(msg);
        } catch (RuntimeException e) {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = e.getMessage();
            e.printStackTrace();
            effectUpdateHandler.sendMessage(msg);
        }
    }

    private void handleEffectUpdate(Message msg) {
        switch (msg.what) {
            case 0:
                fightEffectList = (List<FightEffect>) msg.obj;
                updateEffectViews();
                break;
        }
    }

    public void updateFightEffect(FightEffect fightEffect) {
        for(int i=0;i<fightEffectList.size();i++) {
            if(fightEffectList.get(i).getEffectId().equals(fightEffect.getEffectId())) {
                fightEffectList.set(i, fightEffect);
            }
        }
        updateEffectViews();
    }

    private void updateEffectViews() {
        if(fightEffectList.isEmpty()) {
            return;
        }
        textNoEffect.setVisibility(View.GONE);
        layoutEffects.removeAllViews();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        for(int i=0;i< fightEffectList.size();i++) {
            if(i>0) {
                View viewDividingLine = new View(getContext());
                viewDividingLine.setBackgroundResource(R.drawable.bg_div_gr_black);
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.dp_1));
                viewDividingLine.setLayoutParams(lineParams);
                layoutEffects.addView(viewDividingLine);
            }
            LinearLayout layoutContainer = new LinearLayout(getContext());
            int id = View.generateViewId();
            layoutContainer.setId(id);
            FightEffectFragment fightEffectFragment = FightEffectFragment.newInstance(fightEffectList.get(i));
            fragmentTransaction.add(id, fightEffectFragment);
            layoutEffects.addView(layoutContainer);
        }
        fragmentTransaction.commit();
    }
}