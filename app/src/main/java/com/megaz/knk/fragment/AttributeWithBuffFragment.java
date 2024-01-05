package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.megaz.knk.R;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.computation.DamageEffect;
import com.megaz.knk.computation.FightEffect;
import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.constant.ShownAttributeEnum;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.CharacterProfileVo;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttributeWithBuffFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttributeWithBuffFragment extends BaseFragment {

    private FightEffect fightEffect;
    private ElementEnum elementShown;

    private Map<ShownAttributeEnum, TextView> textBaseAttribute;
    private Map<ShownAttributeEnum, TextView> textAttributeWithBuff;

    public AttributeWithBuffFragment() {
        // Required empty public constructor
    }

    public static AttributeWithBuffFragment newInstance(FightEffect fightEffect) {
        AttributeWithBuffFragment fragment = new AttributeWithBuffFragment();
        Bundle args = new Bundle();
        args.putSerializable("fightEffect", fightEffect);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fightEffect = (FightEffect) getArguments().getSerializable("fightEffect");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attribute_with_buff, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAttributeKeys(view);
        setAttributeValues(textBaseAttribute, fightEffect.getCharacterAttribute());
        setAttributeValues(textAttributeWithBuff, fightEffect.getCharacterAttributeWithBuffs());
    }

    public void updateViewByFightEffect(FightEffect fightEffect) {
        this.fightEffect = fightEffect;
        setAttributeValues(textBaseAttribute, fightEffect.getCharacterAttribute());
        setAttributeValues(textAttributeWithBuff, fightEffect.getCharacterAttributeWithBuffs());
    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        selectElement();
        textBaseAttribute = new HashMap<>();
        textBaseAttribute.put(ShownAttributeEnum.HP, view.findViewById(R.id.text_HP));
        textBaseAttribute.put(ShownAttributeEnum.ATK, view.findViewById(R.id.text_ATK));
        textBaseAttribute.put(ShownAttributeEnum.DEF, view.findViewById(R.id.text_DEF));
        textBaseAttribute.put(ShownAttributeEnum.MASTERY, view.findViewById(R.id.text_mastery));
        textBaseAttribute.put(ShownAttributeEnum.CRIT_RATE, view.findViewById(R.id.text_crit_rate));
        textBaseAttribute.put(ShownAttributeEnum.CRIT_DMG, view.findViewById(R.id.text_crit_dmg));
        textBaseAttribute.put(ShownAttributeEnum.RECHARGE, view.findViewById(R.id.text_recharge));
        textBaseAttribute.put(ShownAttributeEnum.DMG, view.findViewById(R.id.text_dmg));
        textBaseAttribute.put(ShownAttributeEnum.HEAL, view.findViewById(R.id.text_heal));
        for(TextView textView:textBaseAttribute.values()) {
            textView.setTypeface(typefaceNum);
        }

        textAttributeWithBuff = new HashMap<>();
        textAttributeWithBuff.put(ShownAttributeEnum.HP, view.findViewById(R.id.text_HP_buff));
        textAttributeWithBuff.put(ShownAttributeEnum.ATK, view.findViewById(R.id.text_ATK_buff));
        textAttributeWithBuff.put(ShownAttributeEnum.DEF, view.findViewById(R.id.text_DEF_buff));
        textAttributeWithBuff.put(ShownAttributeEnum.MASTERY, view.findViewById(R.id.text_mastery_buff));
        textAttributeWithBuff.put(ShownAttributeEnum.CRIT_RATE, view.findViewById(R.id.text_crit_rate_buff));
        textAttributeWithBuff.put(ShownAttributeEnum.CRIT_DMG, view.findViewById(R.id.text_crit_dmg_buff));
        textAttributeWithBuff.put(ShownAttributeEnum.RECHARGE, view.findViewById(R.id.text_recharge_buff));
        textAttributeWithBuff.put(ShownAttributeEnum.DMG, view.findViewById(R.id.text_dmg_buff));
        textAttributeWithBuff.put(ShownAttributeEnum.HEAL, view.findViewById(R.id.text_heal_buff));
        for(TextView textView:textAttributeWithBuff.values()) {
            textView.setTypeface(typefaceNum);
        }
    }

    private void selectElement() {
        if(fightEffect instanceof DamageEffect) {
            elementShown = ((DamageEffect) fightEffect).getElement();
        } else {
            elementShown = ElementEnum.NULL;
        }
    }


    private void initAttributeKeys(@NonNull View view) {
        if(elementShown != ElementEnum.NULL) {
            ((ImageView) view.findViewById(R.id.img_dmg_elem)).setImageBitmap(
                    ImageResourceUtils.getElementIcon(requireContext(), elementShown));
        } else {
            ((ImageView) view.findViewById(R.id.img_dmg_elem)).setImageBitmap(
                    ImageResourceUtils.getElementIcon(requireContext(), ElementEnum.PHYSICAL));
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setAttributeValues(Map<ShownAttributeEnum, TextView> textViews, CharacterAttribute characterAttribute) {
        for(Map.Entry<ShownAttributeEnum, TextView> entry:textViews.entrySet()) {
            ShownAttributeEnum attribute = entry.getKey();
            TextView textView = entry.getValue();
            if(attribute == ShownAttributeEnum.DMG) {
                if(elementShown != ElementEnum.NULL) {
                    textView.setText(String.format("%.1f", characterAttribute.getShownAttribute(attribute, elementShown) * 100) + "%");
                } else {
                    textView.setText("0.0%");
                }
            } else {
                if(attribute.isPercent()) {
                    textView.setText(String.format("%.1f", characterAttribute.getShownAttribute(attribute, null) * 100) + "%");
                } else {
                    textView.setText(String.format("%d", Math.round(characterAttribute.getShownAttribute(attribute, elementShown))));
                }
            }
        }
    }
}