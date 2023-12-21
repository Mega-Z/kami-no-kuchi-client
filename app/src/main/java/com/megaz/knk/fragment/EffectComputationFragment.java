package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.megaz.knk.R;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.computation.EnemyAttribute;
import com.megaz.knk.computation.FightEffect;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.manager.EffectComputationManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
    private EnemyAttribute enemyAttribute;
    private List<FightEffect> fightEffectList;

    private Handler effectUpdateHandler;
    private EffectComputationManager effectComputationManager;

    private LinearLayout layoutEffects, layoutEnemyAttribute;
    private TextView textNoEffect;
    private TextView textEnemyLevel;
    private Map<ElementEnum, TextView> textResist;

    private List<FightEffectFragment> fightEffectFragmentList;


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
        fightEffectFragmentList = new ArrayList<>();
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
        layoutEnemyAttribute = view.findViewById(R.id.layout_enemy_attribute);
        textEnemyLevel = view.findViewById(R.id.text_enemy_level);
        textEnemyLevel.setTypeface(typefaceNum);
        textResist = new HashMap<>();
        textResist.put(ElementEnum.PYRO, view.findViewById(R.id.text_resist_pyro));
        textResist.put(ElementEnum.CRYO, view.findViewById(R.id.text_resist_cryo));
        textResist.put(ElementEnum.HYDRO, view.findViewById(R.id.text_resist_hydro));
        textResist.put(ElementEnum.ELECTRO, view.findViewById(R.id.text_resist_electro));
        textResist.put(ElementEnum.ANEMO, view.findViewById(R.id.text_resist_anemo));
        textResist.put(ElementEnum.GEO, view.findViewById(R.id.text_resist_geo));
        textResist.put(ElementEnum.DENDRO, view.findViewById(R.id.text_resist_dendro));
        textResist.put(ElementEnum.PHYSICAL, view.findViewById(R.id.text_resist_phy));
        for (TextView textView : textResist.values()) {
            textView.setTypeface(typefaceNum);
        }
        enemyAttribute = new EnemyAttribute();
        updateEnemyAttributeView();
        new Thread(this::initFightEffects).start();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        layoutEnemyAttribute.setOnClickListener(new EnemyAttributeOnClickListener());
        layoutEnemyAttribute.setOnTouchListener(new EnemyAttributeOnTouchListener());
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
        for (int i = 0; i < fightEffectList.size(); i++) {
            if (fightEffectList.get(i).getEffectId().equals(fightEffect.getEffectId())) {
                fightEffectList.set(i, fightEffect);
            }
        }
        updateEffectViews();
    }

    public void updateEnemyAttribute(EnemyAttribute enemyAttribute) {
        this.enemyAttribute = enemyAttribute;
        updateEnemyAttributeView();
        for(FightEffect fightEffect:fightEffectList) {
            fightEffect.setEnemyAttribute(enemyAttribute);
        }
        updateEffectViews();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateEnemyAttributeView() {
        textEnemyLevel.setText(getString(R.string.text_level_prefix) + enemyAttribute.getLevel());
        for (ElementEnum element : GenshinConstantMeta.ELEMENT_LIST) {
            Objects.requireNonNull(textResist.get(element))
                    .setText(String.format("%d", (int) (enemyAttribute.getResist(element) * 100)) + "%");
        }
    }

    private void updateEffectViews() {
        if (fightEffectList.isEmpty()) {
            return;
        }
        fightEffectList.sort(Comparator.comparing(FightEffect::getEffectId));
        textNoEffect.setVisibility(View.GONE);
        layoutEffects.removeAllViews();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        for (FightEffectFragment fightEffectFragment:fightEffectFragmentList) {
            fragmentTransaction.remove(fightEffectFragment);
        }
        fightEffectFragmentList.clear();
        for (int i = 0; i < fightEffectList.size(); i++) {
            View viewDividingLine = new View(getContext());
            viewDividingLine.setBackgroundResource(R.drawable.bg_div_gr_black);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.dp_1));
            viewDividingLine.setLayoutParams(lineParams);
            layoutEffects.addView(viewDividingLine);
            LinearLayout layoutContainer = new LinearLayout(getContext());
            int id = View.generateViewId();
            layoutContainer.setId(id);
            FightEffectFragment fightEffectFragment = FightEffectFragment.newInstance(fightEffectList.get(i));
            fightEffectFragmentList.add(fightEffectFragment);
            fragmentTransaction.add(id, fightEffectFragment);
            layoutEffects.addView(layoutContainer);
        }
        fragmentTransaction.commit();
    }

    private class EnemyAttributeOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            EnemyAttributeConfigFragment enemyAttributeConfigFragment = EnemyAttributeConfigFragment.newInstance(enemyAttribute);
            enemyAttributeConfigFragment.show(getParentFragmentManager(), "");
        }
    }

    private class EnemyAttributeOnTouchListener implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
                v.setBackgroundColor(requireContext().getColor(R.color.fragment_press_gray));
            }else{
                v.setBackgroundColor(requireContext().getColor(R.color.transparent));
            }
            return false;
        }
    }
}