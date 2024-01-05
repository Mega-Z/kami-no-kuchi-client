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

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutWeapon = view.findViewById(R.id.layout_weapon);
        imageWeapon = view.findViewById(R.id.img_weapon);
        Bitmap bitmapWeapon = ImageResourceUtils.getIconBitmap(requireContext(), weaponProfileVo.getWeaponIcon());
        imageWeapon.setImageBitmap(bitmapWeapon);
        imageWeapon.setTranslationX(WEAPON_OFFSET_X);
        layoutWeaponInfo = view.findViewById(R.id.layout_weapon_info);
        ((TextView) view.findViewById(R.id.text_weapon_name)).setText(weaponProfileVo.getWeaponName());
        ((TextView) view.findViewById(R.id.text_weapon_level)).setText(
                getString(R.string.text_level_prefix) + weaponProfileVo.getLevel());
        ((TextView) view.findViewById(R.id.text_weapon_level)).setTypeface(typefaceNum);
        ((TextView) view.findViewById(R.id.text_weapon_refine)).setText(
                getString(R.string.text_refine_prefix) + weaponProfileVo.getRefineRank() + getString(R.string.text_refine_suffix));
        ((TextView) view.findViewById(R.id.text_weapon_base_atk_value)).setText("" + Math.round(weaponProfileVo.getBaseAtk()));
        ((TextView) view.findViewById(R.id.text_weapon_base_atk_value)).setTypeface(typefaceNum);
        if(weaponProfileVo.getAttribute() == null) {
            ((TextView) view.findViewById(R.id.text_weapon_sub_attribute)).setVisibility(View.INVISIBLE);
            ((TextView) view.findViewById(R.id.text_weapon_sub_attribute_value)).setVisibility(View.INVISIBLE);
        } else {
            ((TextView) view.findViewById(R.id.text_weapon_sub_attribute)).setText(weaponProfileVo.getAttribute().getDesc());
            if(weaponProfileVo.getAttribute().isPercent()) {
                ((TextView) view.findViewById(R.id.text_weapon_sub_attribute_value)).setText(
                        String.format("%.1f", weaponProfileVo.getAttributeVal() * 100)+"%");
            } else {
                ((TextView) view.findViewById(R.id.text_weapon_sub_attribute_value)).setText(
                        String.format("%d", Math.round(weaponProfileVo.getAttributeVal())));
            }
            ((TextView) view.findViewById(R.id.text_weapon_sub_attribute_value)).setTypeface(typefaceNum);
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