package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.megaz.knk.R;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.WeaponProfileVo;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeaponFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeaponFragment extends BaseFragment {
    private int WEAPON_OFFSET_X, WEAPON_WIDTH_CONTRACT, WEAPON_WIDTH_EXTEND;

    private WeaponProfileVo weaponProfileVo;

    private TextView textWeaponName, textWeaponLevel, textWeaponSubAttribute,
            textWeaponSubAttributeValue, textWeaponRefine, textWeaponBaseAtk;
    private ImageView imageWeapon;
    private FrameLayout layoutWeapon;
    private LinearLayout layoutWeaponInfo;

    public WeaponFragment() {
        // Required empty public constructor
    }

    public static WeaponFragment newInstance(WeaponProfileVo weaponProfileVo) {
        WeaponFragment fragment = new WeaponFragment();
        Bundle args = new Bundle();
        args.putSerializable("weaponProfileVo", weaponProfileVo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            weaponProfileVo = (WeaponProfileVo) getArguments().getSerializable("weaponProfileVo");
        }
        initConstants();
    }

    private void initConstants() {
        WEAPON_OFFSET_X = Math.round(-0.3f*getResources().getDimensionPixelOffset(R.dimen.dp_120));
        WEAPON_WIDTH_EXTEND = getResources().getDimensionPixelOffset(R.dimen.dp_120);
        WEAPON_WIDTH_CONTRACT = getResources().getDimensionPixelOffset(R.dimen.dp_55);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weapon, container, false);
    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        layoutWeapon = view.findViewById(R.id.layout_weapon);
        imageWeapon = view.findViewById(R.id.img_weapon);
        imageWeapon.setTranslationX(WEAPON_OFFSET_X);
        layoutWeaponInfo = view.findViewById(R.id.layout_weapon_info);
        textWeaponName = view.findViewById(R.id.text_weapon_name);
        textWeaponLevel = view.findViewById(R.id.text_weapon_level);
        textWeaponLevel.setTypeface(typefaceNum);
        textWeaponRefine = view.findViewById(R.id.text_weapon_refine);
        textWeaponBaseAtk = view.findViewById(R.id.text_weapon_base_atk_value);
        textWeaponBaseAtk.setTypeface(typefaceNum);
        textWeaponSubAttribute = view.findViewById(R.id.text_weapon_sub_attribute);
        textWeaponSubAttributeValue = view.findViewById(R.id.text_weapon_sub_attribute_value);
        textWeaponSubAttributeValue.setTypeface(typefaceNum);
        updateViews(weaponProfileVo);
    }

    public void updateViews(@NonNull WeaponProfileVo weaponProfileVo) {
        this.weaponProfileVo = weaponProfileVo;
        Bitmap bitmapWeapon = ImageResourceUtils.getIconBitmap(requireContext(), weaponProfileVo.getWeaponIcon());
        imageWeapon.setImageBitmap(bitmapWeapon);
        textWeaponName.setText(weaponProfileVo.getWeaponName());
        textWeaponLevel.setText(getString(R.string.text_level, weaponProfileVo.getLevel()));
        textWeaponRefine.setText(getString(R.string.text_refine, weaponProfileVo.getRefineRank()));
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void updateViews(CharacterAttribute characterAttribute) {
        textWeaponBaseAtk.setText(String.valueOf(Math.round(characterAttribute.getWeaponBaseAtk())));
        if(characterAttribute.getWeaponAttribute() == null) {
            textWeaponSubAttribute.setVisibility(View.INVISIBLE);
            textWeaponSubAttributeValue.setVisibility(View.INVISIBLE);
        } else {
            textWeaponSubAttribute.setVisibility(View.VISIBLE);
            textWeaponSubAttributeValue.setVisibility(View.VISIBLE);
            textWeaponSubAttribute.setText(characterAttribute.getWeaponAttribute().getDesc());
            if(characterAttribute.getWeaponAttribute().isPercent()) {
                textWeaponSubAttributeValue.setText(
                        String.format("%.1f", characterAttribute.getWeaponAttributeValue() * 100)+"%");
            } else {
                textWeaponSubAttributeValue.setText(
                        String.format("%d", Math.round(characterAttribute.getWeaponAttributeValue())));
            }
        }
    }

    public void setInfoExtend(float extendRatio) {
        FrameLayout.LayoutParams layoutWeaponParams = (FrameLayout.LayoutParams) layoutWeapon.getLayoutParams();
        FrameLayout.LayoutParams layoutWeaponInfoParams = (FrameLayout.LayoutParams) layoutWeaponInfo.getLayoutParams();
        if(extendRatio <= 0) {
            layoutWeaponParams.width = WEAPON_WIDTH_CONTRACT;
            layoutWeaponInfo.setVisibility(View.GONE);
            imageWeapon.setTranslationX(0);

        } else {
            extendRatio = Math.min(1, extendRatio);
            layoutWeaponParams.width = WEAPON_WIDTH_CONTRACT +
                    Math.round((WEAPON_WIDTH_EXTEND - WEAPON_WIDTH_CONTRACT) * extendRatio);
            layoutWeaponInfo.setVisibility(View.VISIBLE);
            layoutWeaponInfoParams.rightMargin = -1 * Math.round(getResources().getDimensionPixelOffset(R.dimen.dp_70) * (1-extendRatio));
            imageWeapon.setTranslationX(WEAPON_OFFSET_X * extendRatio);
        }
        layoutWeapon.setLayoutParams(layoutWeaponParams);
        layoutWeaponInfo.setLayoutParams(layoutWeaponInfoParams);
    }
}