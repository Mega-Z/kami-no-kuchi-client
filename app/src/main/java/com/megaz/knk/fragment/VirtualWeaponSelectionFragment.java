package com.megaz.knk.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.megaz.knk.R;
import com.megaz.knk.entity.WeaponDex;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VirtualWeaponSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VirtualWeaponSelectionFragment extends BaseFragment {

    private WeaponDex weaponDex;

    public VirtualWeaponSelectionFragment() {
        // Required empty public constructor
    }

    public static VirtualWeaponSelectionFragment newInstance(WeaponDex weaponDex) {
        VirtualWeaponSelectionFragment fragment = new VirtualWeaponSelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable("weaponDex", weaponDex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            weaponDex = (WeaponDex) getArguments().getSerializable("weaponDex");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_virtual_weapon_selection, container, false);
    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        ((ImageView)view.findViewById(R.id.img_weapon)).setImageBitmap(
                ImageResourceUtils.getIconBitmap(requireContext(), weaponDex.getIconAwaken()));
        ((ImageView)view.findViewById(R.id.img_weapon)).setBackgroundResource(
                DynamicStyleUtils.getQualityBackground(weaponDex.getStar()));
        ((TextView)view.findViewById(R.id.text_weapon_name)).setText(weaponDex.getWeaponName());
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        view.setOnClickListener(view1 -> {
            Intent returnIntent = new Intent();
            Bundle returnBundle = new Bundle();
            returnBundle.putSerializable("weaponDex", weaponDex);
            returnIntent.putExtras(returnBundle);
            requireActivity().setResult(RESULT_OK, returnIntent);
            requireActivity().finish();
        });
    }
}