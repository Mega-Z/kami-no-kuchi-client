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
import java.util.Arrays;
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
        new Thread(() -> queryFightEffects(true, false)).start();
    }


    private void queryFightEffects(boolean current, boolean baseline) {
        try {
            Message msg = new Message();
            if(current && !baseline) {
                msg.what = 1;
                msg.obj = effectComputationManager.getFightEffectsByCharacterAttribute(characterAttribute);
            } else if (!current && baseline) {
                msg.what = 2;
                msg.obj = effectComputationManager.getFightEffectsByCharacterAttribute(characterAttributeBaseline);
            }else if (current) { // && baseline
                msg.what = 3;
                msg.obj = Arrays.asList(effectComputationManager.getFightEffectsByCharacterAttribute(characterAttribute),
                        effectComputationManager.getFightEffectsByCharacterAttribute(characterAttributeBaseline));
            }
            effectQueryHandler.sendMessage(msg);
        } catch (RuntimeException e) {
            Message msg = new Message();
            msg.what = -1;
            msg.obj = e.getMessage();
            e.printStackTrace();
            effectQueryHandler.sendMessage(msg);
        }
    }

    private void handleEffectQuery(Message msg) {
        switch (msg.what) {
            case 1: // current only
                fightEffectList.addAll((List<FightEffect>) msg.obj);
                resetEffectViews();
                break;
            case 2: // baseline only
                fightEffectListBaseline.addAll((List<FightEffect>) msg.obj);
                addEffectBaselineViews();
                break;
            case 3: // current & baseline
                List<List<FightEffect>> twoFightEffectLists = (List<List<FightEffect>>) msg.obj;
                fightEffectList.addAll(twoFightEffectLists.get(0));
                fightEffectListBaseline.addAll(twoFightEffectLists.get(1));
                resetEffectViews();
                break;
            case -1:
                toast.setText((String) msg.obj);
                toast.show();
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
            fightEffectFragment.setFightEffect(fightEffect);
            fightEffectFragment.updateEffectView();
        } else {
            for (int i = 0; i < fightEffectListBaseline.size(); i++) {
                if (fightEffectListBaseline.get(i).getEffectId().equals(fightEffect.getEffectId())) {
                    fightEffectListBaseline.set(i, fightEffect);
                }
            }
            FightEffectFragment fightEffectFragment = fightEffectFragmentMap.get(fightEffect.getEffectDesc());
            assert fightEffectFragment != null;
            fightEffectFragment.setFightEffectBaseline(fightEffect);
            fightEffectFragment.updateEffectView();
        }

    }

    public void updateEnemyAttribute(EnemyAttribute enemyAttribute) {
        this.enemyAttribute = enemyAttribute;
        updateEnemyAttributeView();
        for (FightEffect fightEffect : fightEffectList) {
            fightEffect.setEnemyAttribute(enemyAttribute);
            FightEffectFragment fightEffectFragment = fightEffectFragmentMap.get(fightEffect.getEffectDesc());
            assert fightEffectFragment != null;
            fightEffectFragment.setFightEffect(fightEffect);
            fightEffectFragment.updateEffectView();
        }
        for (FightEffect fightEffect : fightEffectListBaseline) {
            fightEffect.setEnemyAttribute(enemyAttribute);
            FightEffectFragment fightEffectFragment = fightEffectFragmentMap.get(fightEffect.getEffectDesc());
            assert fightEffectFragment != null;
            fightEffectFragment.setFightEffectBaseline(fightEffect);
            fightEffectFragment.updateEffectView();
        }
    }

    public void resetEffectsByCharacterAttribute(@NonNull CharacterAttribute characterAttribute,
                                                 CharacterAttribute characterAttributeBaseline) {
        clearEffectViews();
        fightEffectFragmentMap.clear();
        fightEffectList.clear();
        fightEffectListBaseline.clear();
        this.characterAttribute = characterAttribute;
        this.characterAttributeBaseline = characterAttributeBaseline;
        comparing = characterAttributeBaseline != null;
        if (characterAttributeBaseline != null)
            new Thread(() -> queryFightEffects(true, true)).start();
        else // characterAttribute != null && characterAttributeBaseline != null
            new Thread(() -> queryFightEffects(true, false)).start();
    }

    public void addEffectsBaselineByCharacterAttribute(@NonNull CharacterAttribute characterAttributeBaseline) {
        fightEffectListBaseline.clear();
        comparing = true;
        this.characterAttributeBaseline = characterAttributeBaseline;
        new Thread(() -> queryFightEffects(false, true)).start();
    }

    public void disableComparing() {
        comparing = false;
        characterAttributeBaseline = null;
        fightEffectListBaseline.clear();
        fightEffectFragmentMap.clear();
        clearEffectViews();
        resetEffectViews();
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
    }

    private void resetEffectViews() {
        if (fightEffectList.isEmpty() && fightEffectListBaseline.isEmpty()) {
            textNoEffect.setVisibility(View.VISIBLE);
            return;
        } else {
            textNoEffect.setVisibility(View.GONE);
        }
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fightEffectList.sort(Comparator.comparing(FightEffect::getEffectId));
        fightEffectListBaseline.sort(Comparator.comparing(FightEffect::getEffectId));
        Map<String, FightEffect> baselineFightEffectMap = new HashMap<>();
        for(FightEffect fightEffect : fightEffectListBaseline) {
            baselineFightEffectMap.put(fightEffect.getEffectDesc(), fightEffect);
        }
        for (FightEffect fightEffect : fightEffectList) {
            layoutEffects.addView(getDividingLine());
            LinearLayout layoutContainer = new LinearLayout(getContext());
            int id = View.generateViewId();
            layoutContainer.setId(id);
            FightEffectFragment fightEffectFragment = baselineFightEffectMap.containsKey(fightEffect.getEffectDesc()) ?
                    FightEffectFragment.newInstance(fightEffect, baselineFightEffectMap.get(fightEffect.getEffectDesc()), comparing) :
                    FightEffectFragment.newInstance(fightEffect, null, comparing);
            fightEffectFragmentMap.put(fightEffect.getEffectDesc(), fightEffectFragment);
            fragmentTransaction.add(id, fightEffectFragment);
            layoutEffects.addView(layoutContainer);
        }
        for (FightEffect fightEffect : fightEffectListBaseline) {
            if (!fightEffectFragmentMap.containsKey(fightEffect.getEffectDesc())) {
                layoutEffects.addView(getDividingLine());
                LinearLayout layoutContainer = new LinearLayout(getContext());
                int id = View.generateViewId();
                layoutContainer.setId(id);
                FightEffectFragment fightEffectFragment =  FightEffectFragment.newInstance(null, fightEffect, comparing);
                fightEffectFragmentMap.put(fightEffect.getEffectDesc(), fightEffectFragment);
                fragmentTransaction.add(id, fightEffectFragment);
                layoutEffects.addView(layoutContainer);
            }
        }
        fragmentTransaction.commit();
    }

    private void addEffectBaselineViews() {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fightEffectListBaseline.sort(Comparator.comparing(FightEffect::getEffectId));
        for (FightEffect fightEffect : fightEffectListBaseline) {
            if (fightEffectFragmentMap.containsKey(fightEffect.getEffectDesc())) {
                FightEffectFragment fightEffectFragment = fightEffectFragmentMap.get(fightEffect.getEffectDesc());
                assert fightEffectFragment != null;
                fightEffectFragment.setFightEffectBaseline(fightEffect);
                fightEffectFragment.setComparing(true);
                fightEffectFragment.updateEffectView();
            } else {
                layoutEffects.addView(getDividingLine());
                LinearLayout layoutContainer = new LinearLayout(getContext());
                int id = View.generateViewId();
                layoutContainer.setId(id);
                FightEffectFragment fightEffectFragment =  FightEffectFragment.newInstance(null, fightEffect, comparing);
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