package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.megaz.knk.R;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.constant.ShownAttributeEnum;
import com.megaz.knk.utils.ImageResourceUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CharacterAttributeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterAttributeFragment extends BaseFragment {
    private static int MIN_WIDTH, MAX_WIDTH,
            MIN_KEY_MARGIN_RIGHT, MAX_KEY_MARGIN_RIGHT,
            MIN_BASELINE_MARGIN_LEFT, MAX_BASELINE_MARGIN_LEFT;
    private static float SWITCH_THRESHOLD;

    // private CharacterProfileVo characterProfileVo;
    private CharacterAttribute characterAttribute, characterAttributeBaseline;

    private ElementEnum maxDmgElement;
    private Map<ShownAttributeEnum, TextView> textAttributeValue, textAttributePlus, textAttributeBaseline;
    private ImageView imageDmgElem;
    private LinearLayout layoutAttributeValue, layoutAttributeKey, layoutAttributeBaseline, layoutAttributeBg;

    public CharacterAttributeFragment() {
        // Required empty public constructor
    }

    public static CharacterAttributeFragment newInstance() {
        return new CharacterAttributeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MIN_WIDTH = getResources().getDimensionPixelOffset(R.dimen.dp_180);
        MAX_WIDTH = getResources().getDisplayMetrics().widthPixels - getResources().getDimensionPixelOffset(R.dimen.dp_20);
        MIN_KEY_MARGIN_RIGHT = getResources().getDimensionPixelOffset(R.dimen.dp_100);
        MAX_KEY_MARGIN_RIGHT = getResources().getDisplayMetrics().widthPixels * 2 / 5;
        MIN_BASELINE_MARGIN_LEFT = getResources().getDimensionPixelOffset(R.dimen.dp_45);
        MAX_BASELINE_MARGIN_LEFT = getResources().getDimensionPixelOffset(R.dimen.dp_180);
        SWITCH_THRESHOLD = 0.5f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character_attribute, container, false);
    }


    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        initTextViewMaps(view);
        layoutAttributeBg = view.findViewById(R.id.layout_attribute_bg);
        layoutAttributeValue = view.findViewById(R.id.layout_attribute_value);
        layoutAttributeValue.setVisibility(View.GONE);
        layoutAttributeBaseline = view.findViewById(R.id.layout_attribute_baseline);
        layoutAttributeBaseline.setAlpha(0f);
        layoutAttributeKey = view.findViewById(R.id.layout_attribute_key);
        layoutAttributeKey.setVisibility(View.VISIBLE);
        imageDmgElem = view.findViewById(R.id.img_dmg_elem);
    }


    private void initTextViewMaps(@NonNull View view) {
        textAttributeValue = new HashMap<>();
        textAttributePlus = new HashMap<>();
        textAttributeBaseline = new HashMap<>();
        textAttributeValue.put(ShownAttributeEnum.HP, view.findViewById(R.id.text_HP));
        textAttributePlus.put(ShownAttributeEnum.HP, view.findViewById(R.id.text_HP_plus));
        textAttributeBaseline.put(ShownAttributeEnum.HP, view.findViewById(R.id.text_HP_baseline));
        textAttributeValue.put(ShownAttributeEnum.ATK, view.findViewById(R.id.text_ATK));
        textAttributePlus.put(ShownAttributeEnum.ATK, view.findViewById(R.id.text_ATK_plus));
        textAttributeBaseline.put(ShownAttributeEnum.ATK, view.findViewById(R.id.text_ATK_baseline));
        textAttributeValue.put(ShownAttributeEnum.DEF, view.findViewById(R.id.text_DEF));
        textAttributePlus.put(ShownAttributeEnum.DEF, view.findViewById(R.id.text_DEF_plus));
        textAttributeBaseline.put(ShownAttributeEnum.DEF, view.findViewById(R.id.text_DEF_baseline));
        textAttributeValue.put(ShownAttributeEnum.MASTERY, view.findViewById(R.id.text_mastery));
        textAttributePlus.put(ShownAttributeEnum.MASTERY, view.findViewById(R.id.text_mastery_plus));
        textAttributeBaseline.put(ShownAttributeEnum.MASTERY, view.findViewById(R.id.text_mastery_baseline));
        textAttributeValue.put(ShownAttributeEnum.CRIT_RATE, view.findViewById(R.id.text_crit_rate));
        textAttributePlus.put(ShownAttributeEnum.CRIT_RATE, view.findViewById(R.id.text_crit_rate_plus));
        textAttributeBaseline.put(ShownAttributeEnum.CRIT_RATE, view.findViewById(R.id.text_crit_rate_baseline));
        textAttributeValue.put(ShownAttributeEnum.CRIT_DMG, view.findViewById(R.id.text_crit_dmg));
        textAttributePlus.put(ShownAttributeEnum.CRIT_DMG, view.findViewById(R.id.text_crit_dmg_plus));
        textAttributeBaseline.put(ShownAttributeEnum.CRIT_DMG, view.findViewById(R.id.text_crit_dmg_baseline));
        textAttributeValue.put(ShownAttributeEnum.RECHARGE, view.findViewById(R.id.text_recharge));
        textAttributePlus.put(ShownAttributeEnum.RECHARGE, view.findViewById(R.id.text_recharge_plus));
        textAttributeBaseline.put(ShownAttributeEnum.RECHARGE, view.findViewById(R.id.text_recharge_baseline));
        textAttributeValue.put(ShownAttributeEnum.DMG, view.findViewById(R.id.text_dmg));
        textAttributePlus.put(ShownAttributeEnum.DMG, view.findViewById(R.id.text_dmg_plus));
        textAttributeBaseline.put(ShownAttributeEnum.DMG, view.findViewById(R.id.text_dmg_baseline));
        textAttributeValue.put(ShownAttributeEnum.HEAL, view.findViewById(R.id.text_heal));
        textAttributePlus.put(ShownAttributeEnum.HEAL, view.findViewById(R.id.text_heal_plus));
        textAttributeBaseline.put(ShownAttributeEnum.HEAL, view.findViewById(R.id.text_heal_baseline));
        for (TextView textView : textAttributeValue.values()) {
            textView.setTypeface(typefaceNum);
        }
        for (TextView textView : textAttributePlus.values()) {
            textView.setTypeface(typefaceNum);
        }
        for (TextView textView : textAttributeBaseline.values()) {
            textView.setTypeface(typefaceNum);
        }
    }

    public void setCharacterAttribute(CharacterAttribute characterAttribute) {
        this.characterAttribute = characterAttribute;
        setDmgIcon();
        showAttributeSingle();
    }

    public void setCharacterAttributeBaseline(CharacterAttribute characterAttribute) {
        this.characterAttributeBaseline = characterAttribute;
    }

    public void setExtendProcess(float process) {
        process = Math.min(1, Math.max(0, process));
        Log.e("process", process+"");
        if (process >= SWITCH_THRESHOLD) {
            showAttributeComparison();
            layoutAttributeBaseline.setAlpha((process - SWITCH_THRESHOLD) / (1 - SWITCH_THRESHOLD));
        } else {
            showAttributeSingle();
        }
        ViewGroup.MarginLayoutParams paramsBg = (ViewGroup.MarginLayoutParams) layoutAttributeBg.getLayoutParams();
        ViewGroup.MarginLayoutParams paramsKey = (ViewGroup.MarginLayoutParams) layoutAttributeKey.getLayoutParams();
        ViewGroup.MarginLayoutParams paramsBaseline = (ViewGroup.MarginLayoutParams) layoutAttributeBaseline.getLayoutParams();
        paramsBg.width = Math.round(MIN_WIDTH + (MAX_WIDTH - MIN_WIDTH) * process);
        paramsKey.rightMargin = Math.round(MIN_KEY_MARGIN_RIGHT + (MAX_KEY_MARGIN_RIGHT - MIN_KEY_MARGIN_RIGHT) * process);
        paramsBaseline.leftMargin = Math.round(MAX_BASELINE_MARGIN_LEFT - (MAX_BASELINE_MARGIN_LEFT - MIN_BASELINE_MARGIN_LEFT) * process);
        layoutAttributeBg.setLayoutParams(paramsBg);
        layoutAttributeKey.setLayoutParams(paramsKey);
        layoutAttributeBaseline.setLayoutParams(paramsBaseline);
    }

    private void setDmgIcon() {
        if (maxDmgElement == null) {
            selectMaxDmgElement();
        }
        imageDmgElem.setImageBitmap(ImageResourceUtils.getElementIcon(requireContext(), maxDmgElement));
    }

    private void selectMaxDmgElement() {
        maxDmgElement = characterAttribute.getElement();
        double maxElementDmgValue = 0.;
        Map<ElementEnum, Double> dmgUpMap = characterAttribute.getDmgUp();
        for (ElementEnum element : GenshinConstantMeta.ELEMENT_LIST) {
            if (Objects.requireNonNull(dmgUpMap.get(element)) > maxElementDmgValue ||
                    (Objects.requireNonNull(dmgUpMap.get(element)) > maxElementDmgValue && characterAttribute.getElement() == element)) {
                maxDmgElement = element;
                maxElementDmgValue = Objects.requireNonNull(dmgUpMap.get(element));
            }
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void showAttributeSingle() {
        // style
        layoutAttributeValue.setVisibility(View.VISIBLE);
        layoutAttributeBaseline.setVisibility(View.GONE);
        for (ShownAttributeEnum attribute : Arrays.asList(
                ShownAttributeEnum.HP, ShownAttributeEnum.ATK, ShownAttributeEnum.DEF)) {
            Objects.requireNonNull(textAttributeBaseline.get(attribute)).setText("");
            Objects.requireNonNull(textAttributePlus.get(attribute)).setTextColor(requireContext().getColor(R.color.attr_green));
            Objects.requireNonNull(textAttributePlus.get(attribute)).setVisibility(View.VISIBLE);
        }
        for (ShownAttributeEnum attribute : Arrays.asList(
                ShownAttributeEnum.MASTERY, ShownAttributeEnum.CRIT_RATE, ShownAttributeEnum.CRIT_DMG,
                ShownAttributeEnum.RECHARGE, ShownAttributeEnum.DMG, ShownAttributeEnum.HEAL)) {
            Objects.requireNonNull(textAttributeBaseline.get(attribute)).setText("");
            Objects.requireNonNull(textAttributePlus.get(attribute)).setText("");
            Objects.requireNonNull(textAttributePlus.get(attribute)).setVisibility(View.GONE);
        }

        // content
        Objects.requireNonNull(textAttributeValue.get(ShownAttributeEnum.HP)).setText("" + Math.round(characterAttribute.getBaseHp()));
        Objects.requireNonNull(textAttributePlus.get(ShownAttributeEnum.HP)).setText(" +" + Math.round(characterAttribute.getPlusHp()));
        Objects.requireNonNull(textAttributeValue.get(ShownAttributeEnum.ATK)).setText("" + Math.round(characterAttribute.getBaseAtk()));
        Objects.requireNonNull(textAttributePlus.get(ShownAttributeEnum.ATK)).setText(" +" + Math.round(characterAttribute.getPlusAtk()));
        Objects.requireNonNull(textAttributeValue.get(ShownAttributeEnum.DEF)).setText("" + Math.round(characterAttribute.getBaseDef()));
        Objects.requireNonNull(textAttributePlus.get(ShownAttributeEnum.DEF)).setText(" +" + Math.round(characterAttribute.getPlusDef()));
        for (ShownAttributeEnum attribute : Arrays.asList(
                ShownAttributeEnum.MASTERY, ShownAttributeEnum.CRIT_RATE, ShownAttributeEnum.CRIT_DMG,
                ShownAttributeEnum.RECHARGE, ShownAttributeEnum.DMG, ShownAttributeEnum.HEAL)) {
            double value;
            if (attribute == ShownAttributeEnum.DMG) {
                value = characterAttribute.getShownAttribute(attribute, maxDmgElement);
            } else {
                value = characterAttribute.getShownAttribute(attribute, null);
            }
            if (attribute.isPercent()) {
                Objects.requireNonNull(textAttributeValue.get(attribute)).setText(
                        String.format("%.1f", value * 100) + "%");
            } else {
                Objects.requireNonNull(textAttributeValue.get(attribute)).setText(
                        "" + Math.round(value));
            }
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void showAttributeComparison() {
        if (characterAttributeBaseline == null) {
            return;
        }
        // style
        layoutAttributeValue.setVisibility(View.VISIBLE);
        layoutAttributeBaseline.setVisibility(View.VISIBLE);
        for (ShownAttributeEnum attribute : Arrays.asList(
                ShownAttributeEnum.HP, ShownAttributeEnum.ATK, ShownAttributeEnum.DEF,
                ShownAttributeEnum.MASTERY, ShownAttributeEnum.CRIT_RATE, ShownAttributeEnum.CRIT_DMG,
                ShownAttributeEnum.RECHARGE, ShownAttributeEnum.DMG, ShownAttributeEnum.HEAL)) {
            Objects.requireNonNull(textAttributePlus.get(attribute)).setVisibility(View.VISIBLE);
        }

        // content
        for (ShownAttributeEnum attribute : Arrays.asList(
                ShownAttributeEnum.HP, ShownAttributeEnum.ATK, ShownAttributeEnum.DEF,
                ShownAttributeEnum.MASTERY, ShownAttributeEnum.CRIT_RATE, ShownAttributeEnum.CRIT_DMG,
                ShownAttributeEnum.RECHARGE, ShownAttributeEnum.DMG, ShownAttributeEnum.HEAL)) {
            double value, baseline;
            if (attribute == ShownAttributeEnum.DMG) {
                value = characterAttribute.getShownAttribute(attribute, maxDmgElement);
                baseline = characterAttributeBaseline.getShownAttribute(attribute, maxDmgElement);
            } else {
                value = characterAttribute.getShownAttribute(attribute, null);
                baseline = characterAttributeBaseline.getShownAttribute(attribute, null);
            }
            if (value >= baseline) {
                Objects.requireNonNull(textAttributePlus.get(attribute)).
                        setTextColor(requireContext().getColor(R.color.attr_green));
            } else {
                Objects.requireNonNull(textAttributePlus.get(attribute)).
                        setTextColor(requireContext().getColor(R.color.attr_red));
            }
            if (attribute.isPercent()) {
                Objects.requireNonNull(textAttributeValue.get(attribute)).setText(
                        String.format("%.1f", value * 100) + "%");
                Objects.requireNonNull(textAttributeBaseline.get(attribute)).setText(
                        String.format("%.1f", baseline * 100) + "%");
                if (value - baseline > 0.001) {
                    Objects.requireNonNull(textAttributePlus.get(attribute)).setText(
                            "▲" + String.format("%.1f", (value - baseline) * 100) + "%");
                } else if (value - baseline < -0.001) {
                    Objects.requireNonNull(textAttributePlus.get(attribute)).setText(
                            "▼" + String.format("%.1f", (baseline - value) * 100) + "%");
                } else {
                    Objects.requireNonNull(textAttributePlus.get(attribute)).setText("");
                }
            } else {
                Objects.requireNonNull(textAttributeValue.get(attribute)).setText(
                        "" + Math.round(value));
                Objects.requireNonNull(textAttributeBaseline.get(attribute)).setText(
                        "" + Math.round(baseline));
                if (value - baseline > 1) {
                    Objects.requireNonNull(textAttributePlus.get(attribute)).setText(
                            "▲" + Math.round(value - baseline));
                } else if (value - baseline < -1) {
                    Objects.requireNonNull(textAttributePlus.get(attribute)).setText(
                            "▼" + Math.round(baseline - value));
                } else {
                    Objects.requireNonNull(textAttributePlus.get(attribute)).setText("");
                }
            }
        }
    }
}