package com.megaz.knk.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.megaz.knk.R;
import com.megaz.knk.manager.ImageResourceManager;
import com.megaz.knk.vo.WeaponProfileVo;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeaponFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeaponFragment extends Fragment {

    private int WEAPON_OFFSET_X;
    private WeaponProfileVo weaponProfileVo;
    private ImageView imageWeapon;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weapon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageWeapon = view.findViewById(R.id.img_weapon);
        Bitmap bitmapWeapon = ImageResourceManager.getIconBitmap(Objects.requireNonNull(getContext()), weaponProfileVo.getWeaponIcon());
        imageWeapon.setImageBitmap(bitmapWeapon);
        imageWeapon.setX(WEAPON_OFFSET_X);

    }
}