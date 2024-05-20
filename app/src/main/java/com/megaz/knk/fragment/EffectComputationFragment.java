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

import org.checkerframework.checker.units.qual.A;

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

    private CharacterAttribute characterAttribute, characterAttributeBaseline;
    private boolean comparing;
    private EnemyAttribute enemyAttribute;
    private List<FightEffect> fightEffectList, fightEffectListBaseline;

    private Handler effectQueryHandler;
    private EffectComputationManager effectComputationManager;

    private LinearLayout layoutEffects, layoutEnemyAttribute;
    private TextView textNoEffect;
    private TextView textEnemyLevel;
    private Map<ElementEnum, TextView> textResist;

    private List<FightEffectFragment> fightEffectFragmentList;
    private Map<String, FightEffectFragment> fightEffectFragmentMap;


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
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        layoutEnemyAttribute.setOnClickListener(new EnemyAttributeOnClickListener());
        layoutEnemyAttribute.setOnTouchListener(new EnemyAttributeOnTouchListener());
        effectQueryHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleEffectQuery(msg);
            }
        };
    }

    @Override
    protected void initialize(@NonNull View view) {
        super.initialize(view);
        fightEffectList = new ArrayList<>();
        fightEffectListBaseline = new ArrayList<>();
        fightEffectFragmentMap = new HashMap<>();
        comparing = false;
        new Thread(() -> queryFightEffects(false)).start();
    }

    private void queryFightEffects(boolean baseline) {
        try {
            Message msg = new Message();
            List<FightEffect> fightEffectList;
            if (!baseline) {
                fightEffectList = effectComputationManager.getFightEffectsByCharacterAttribute(characterAttribute);
                msg.what = 0;
            } else {
                fightEffectList = effectComputationManager.getFightEffectsByCharacterAttribute(characterAttributeBaseline);
                msg.what = 2;
            }
            msg.obj = fightEffectList;
            effectQueryHandler.sendMessage(msg);
        } catch (RuntimeException e) {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = e.getMessage();
            e.printStackTrace();
            effectQueryHandler.sendMessage(msg);
        }
    }

    private void handleEffectQuery(Message msg) {
        switch (msg.what) {
            case 0:
                fightEffectList = (List<FightEffect>) msg.obj;
                refreshEffectViews(false);
                break;
            case 1:
                toast.setText((String) msg.obj);
                toast.show();
                break;
            case 2:
                fightEffectListBaseline = (List<FightEffect>) msg.obj;
                refreshEffectViews(true);
                break;

        }
    }

    public void updateByFightEffect(FightEffect fightEffect, boolean baseline) {
        if (!baseline) {
            for (int i = 0; i < fightEffectList.size(); i++) {
                if (fightEffectList.get(i).getEffectId().equals(fightEffect.getEffectId())) {
                    fightEffectList.set(i, fightEffect);
                }
            }
            FightEffectFragment fightEffectFragment = fightEffectFragmentMap.get(fightEffect.getEffectDesc());
            assert fightEffectFragment != null;
            fightEffectFragment.updateEffectView(fightEffect, fightEffectFragment.getFightEffectBaseline(), comparing);
        } else {
            for (int i = 0; i < fightEffectListBaseline.size(); i++) {
                if (fightEffectListBaseline.get(i).getEffectId().equals(fightEffect.getEffectId())) {
                    fightEffectListBaseline.set(i, fightEffect);
                }
            }
            FightEffectFragment fightEffectFragment = fightEffectFragmentMap.get(fightEffect.getEffectDesc());
            assert fightEffectFragment != null;
            fightEffectFragment.updateEffectView(fightEffectFragment.getFightEffect(), fightEffect, comparing);
        }

    }

    public void updateEnemyAttribute(EnemyAttribute enemyAttribute) {
        this.enemyAttribute = enemyAttribute;
        updateEnemyAttributeView();
        for (FightEffect fightEffect : fightEffectList) {
            fightEffect.setEnemyAttribute(enemyAttribute);
            FightEffectFragment fightEffectFragment = fightEffectFragmentMap.get(fightEffect.getEffectDesc());
            assert fightEffectFragment != null;
            fightEffectFragment.updateEffectView(fightEffect, fightEffectFragment.getFightEffectBaseline(), comparing);
        }
        for (FightEffect fightEffect : fightEffectListBaseline) {
            fightEffect.setEnemyAttribute(enemyAttribute);
            FightEffectFragment fightEffectFragment = fightEffectFragmentMap.get(fightEffect.getEffectDesc());
            assert fightEffectFragment != null;
            fightEffectFragment.updateEffectView(fightEffectFragment.getFightEffect(), fightEffect, comparing);
        }
    }

    public void enableComparing(CharacterAttribute characterAttribute, CharacterAttribute characterAttributeBaseline) {
        if (characterAttribute != null) {
            clearEffectViews();
            this.characterAttribute = characterAttribute;
            new Thread(() -> queryFightEffects(false)).start();
        }
        this.characterAttributeBaseline = characterAttributeBaseline;
        assert characterAttributeBaseline != null;
        comparing = true;
        new Thread(() -> queryFightEffects(true)).start();
    }

    public void disableComparing() {
        comparing = false;
        characterAttributeBaseline = null;
        fightEffectListBaseline.clear();
        clearEffectViews();
        refreshEffectViews(false);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateEnemyAttributeView() {
        textEnemyLevel.setText(getString(R.string.text_level_prefix) + enemyAttribute.getLevel());
        for (ElementEnum element : GenshinConstantMeta.ELEMENT_LIST) {
            Objects.requireNonNull(textResist.get(element))
                    .setText(String.format("%d", (int) (enemyAttribute.getResist(element) * 100)) + "%");
        }
    }

    private void clearEffectViews() {
        textNoEffect.setVisibility(View.VISIBLE);
        layoutEffects.removeAllViews();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        for (FightEffectFragment fightEffectFragment : fightEffectFragmentMap.values()) {
            fragmentTransaction.remove(fightEffectFragment);
        }
        fightEffectFragmentMap.clear();
    }

    private void refreshEffectViews(boolean baseline) {
        if (fightEffectList.isEmpty() && fightEffectListBaseline.isEmpty()) {
            textNoEffect.setVisibility(View.VISIBLE);
            return;
        } else {
            textNoEffect.setVisibility(View.GONE);
        }
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fightEffectList.sort(Comparator.comparing(FightEffect::getEffectId));
        for (FightEffect fightEffect : baseline ? fightEffectListBaseline : fightEffectList) {
            if (fightEffectFragmentMap.containsKey(fightEffect.getEffectDesc())) {
                assert comparing;
                FightEffectFragment fightEffectFragment = fightEffectFragmentMap.get(fightEffect.getEffectDesc());
                assert fightEffectFragment != null;
                if (baseline)
                    fightEffectFragment.updateEffectView(fightEffectFragment.getFightEffect(), fightEffect, true);
                else
                    fightEffectFragment.updateEffectView(fightEffect, fightEffectFragment.getFightEffectBaseline(), true);
            } else {
                layoutEffects.addView(getDividingLine());
                LinearLayout layoutContainer = new LinearLayout(getContext());
                int id = View.generateViewId();
                layoutContainer.setId(id);
                FightEffectFragment fightEffectFragment = baseline ?
                        FightEffectFragment.newInstance(null, fightEffect, comparing)
                        : FightEffectFragment.newInstance(fightEffect, null, comparing);
                fightEffectFragmentMap.put(fightEffect.getEffectDesc(), fightEffectFragment);
                fragmentTransaction.add(id, fightEffectFragment);
                layoutEffects.addView(layoutContainer);
            }
        }
        fragmentTransaction.commit();
    }

    private View getDividingLine() {
        View viewDividingLine = new View(getContext());
        viewDividingLine.setBackgroundResource(R.drawable.bg_div_gr_black);
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.dp_1));
        viewDividingLine.setLayoutParams(lineParams);
        return viewDividingLine;
    }

    @Deprecated
    private void updateEffectViews() {
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
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                v.setBackgroundColor(requireContext().getColor(R.color.fragment_press_gray));
            } else {
                v.setBackgroundColor(requireContext().getColor(R.color.transparent));
            }
            return false;
        }
    }
}