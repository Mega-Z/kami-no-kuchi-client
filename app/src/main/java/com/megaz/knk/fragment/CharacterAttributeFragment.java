package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.megaz.knk.R;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.CharacterProfileVo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CharacterAttributeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterAttributeFragment extends BaseFragment {

    private CharacterProfileVo characterProfileVo;
    private ElementEnum maxDmgElement;
    private Double maxElementDmgValue;

    private TextView textHp, textAtk, textDef, textMastery, textCritRate, textCritDmg,
            textRecharge, textDmg, textHeal, textHpPlus, textAtkPlus, textDefPlus,
            textMasteryPlus, textCritRatePlus, textCritDmgPlus,
            textRechargePlus, textDmgPlus, textHealPlus;

    public CharacterAttributeFragment() {
        // Required empty public constructor
    }

    public static CharacterAttributeFragment newInstance(CharacterProfileVo characterProfileVo) {
        CharacterAttributeFragment fragment = new CharacterAttributeFragment();
        Bundle args = new Bundle();
        args.putSerializable("characterProfileVo", characterProfileVo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            characterProfileVo = (CharacterProfileVo) getArguments().getSerializable("characterProfileVo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character_attribute, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAttributeValues(view);
        initAttributeKeys(view);
    }

    private void selectMaxDmgElement() {
        maxDmgElement = characterProfileVo.getElement();
        maxElementDmgValue = 0.;
        Map<ElementEnum, Double> dmgUpMap = characterProfileVo.getDmgUp();
        for (ElementEnum element : GenshinConstantMeta.ELEMENT_LIST) {
            if (Objects.requireNonNull(dmgUpMap.get(element)) > maxElementDmgValue ||
                    (Objects.requireNonNull(dmgUpMap.get(element)) > maxElementDmgValue && characterProfileVo.getElement() == element)) {
                maxDmgElement = element;
                maxElementDmgValue = dmgUpMap.get(element);
            }
        }
    }


    private void initAttributeKeys(@NonNull View view) {
        if (maxDmgElement == null) {
            selectMaxDmgElement();
        }
        ((ImageView) view.findViewById(R.id.img_dmg_elem)).setImageBitmap(
                ImageResourceUtils.getElementIcon(requireContext(), maxDmgElement));
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void initAttributeValues(@NonNull View view) {
        if (maxDmgElement == null) {
            selectMaxDmgElement();
        }
        // view
        textHp = view.findViewById(R.id.text_HP);
        textHpPlus = view.findViewById(R.id.text_HP_plus);
        textAtk = view.findViewById(R.id.text_ATK);
        textAtkPlus = view.findViewById(R.id.text_ATK_plus);
        textDef = view.findViewById(R.id.text_DEF);
        textDefPlus = view.findViewById(R.id.text_DEF_plus);
        textMastery = view.findViewById(R.id.text_mastery);
        textMasteryPlus = view.findViewById(R.id.text_mastery_plus);
        textCritRate = view.findViewById(R.id.text_crit_rate);
        textCritRatePlus = view.findViewById(R.id.text_crit_rate_plus);
        textCritDmg = view.findViewById(R.id.text_crit_dmg);
        textCritDmgPlus = view.findViewById(R.id.text_crit_dmg_plus);
        textRecharge = view.findViewById(R.id.text_recharge);
        textRechargePlus = view.findViewById(R.id.text_recharge_plus);
        textDmg = view.findViewById(R.id.text_dmg);
        textDmgPlus = view.findViewById(R.id.text_dmg_plus);
        textHeal = view.findViewById(R.id.text_heal);
        textHealPlus = view.findViewById(R.id.text_heal_plus);

        // typeface
        textHp.setTypeface(typefaceNum);
        textHpPlus.setTypeface(typefaceNum);
        textAtk.setTypeface(typefaceNum);
        textAtkPlus.setTypeface(typefaceNum);
        textDef.setTypeface(typefaceNum);
        textDefPlus.setTypeface(typefaceNum);
        textMastery.setTypeface(typefaceNum);
        textMasteryPlus.setTypeface(typefaceNum);
        textRecharge.setTypeface(typefaceNum);
        textRechargePlus.setTypeface(typefaceNum);
        textCritRate.setTypeface(typefaceNum);
        textCritRatePlus.setTypeface(typefaceNum);
        textCritDmg.setTypeface(typefaceNum);
        textCritDmgPlus.setTypeface(typefaceNum);
        textDmg.setTypeface(typefaceNum);
        textDmgPlus.setTypeface(typefaceNum);
        textHeal.setTypeface(typefaceNum);
        textHealPlus.setTypeface(typefaceNum);
        // value
        textHp.setText("" + Math.round(characterProfileVo.getBaseHp()));
        textHpPlus.setText("+" + Math.round(characterProfileVo.getPlusHp()));
        textAtk.setText("" + Math.round(characterProfileVo.getBaseAtk()));
        textAtkPlus.setText("+" + Math.round(characterProfileVo.getPlusAtk()));
        textDef.setText("" + Math.round(characterProfileVo.getBaseDef()));
        textDefPlus.setText("+" + Math.round(characterProfileVo.getPlusDef()));
        textMastery.setText("" + Math.round(characterProfileVo.getMastery()));
        textRecharge.setText(String.format("%.1f", characterProfileVo.getRecharge()*100) + "%");
        textCritRate.setText(String.format("%.1f", characterProfileVo.getCritRate()*100) + "%");
        textCritDmg.setText(String.format("%.1f", characterProfileVo.getCritDmg()*100) + "%");
        textDmg.setText(String.format("%.1f", maxElementDmgValue*100) + "%");
        textHeal.setText(String.format("%.1f", characterProfileVo.getHealUp()*100) + "%");
    }
}