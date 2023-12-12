package com.megaz.knk.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.megaz.knk.R;
import com.megaz.knk.computation.BuffInputParam;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.BuffVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuffEnableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuffEnableFragment extends DialogFragment {
    private BuffVo buffVo;

    private TextView textBuffTitle, textBuffEffect, textBuffDesc;
    private ImageView imageSourceIcon;
    private LinearLayout layoutBuffParam;
    private Button buttonEnable, buttonCancel;
    private List<BuffParamInputFragment> buffParamInputFragmentList;

    public BuffEnableFragment() {
        // Required empty public constructor
    }

    public static BuffEnableFragment newInstance(BuffVo buffVo) {
        BuffEnableFragment fragment = new BuffEnableFragment();
        Bundle args = new Bundle();
        args.putSerializable("buffVo", buffVo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buffVo = (BuffVo) getArguments().getSerializable("buffVo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buff_enable, container, false);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textBuffTitle = view.findViewById(R.id.text_buff_title);
        textBuffTitle.setText(buffVo.getBuffTitle());
        textBuffEffect = view.findViewById(R.id.text_buff_effect);
        textBuffEffect.setBackgroundColor(requireContext()
                .getColor(DynamicStyleUtils.getBuffFieldColor(buffVo.getBuffField())));
        textBuffEffect.setText(buffVo.getEffectText());

        imageSourceIcon = view.findViewById(R.id.img_source_icon);
        if(buffVo.getIcon() != null) {
            imageSourceIcon.setImageBitmap(ImageResourceUtils.getIconBitmap(requireContext(), buffVo.getIcon()));
        } else {
            imageSourceIcon.setImageResource(R.drawable.icon_buff_default);
        }
        textBuffDesc = view.findViewById(R.id.text_buff_desc);
        textBuffDesc.setText(buffVo.getBuffDesc()+"\n");
        layoutBuffParam = view.findViewById(R.id.layout_buff_param);
        buttonEnable = view.findViewById(R.id.btn_enable);
        buttonEnable.setOnClickListener(new EnableOnClickListener());
        buttonCancel = view.findViewById(R.id.btn_cancel);
        buttonCancel.setOnClickListener(new CancelOnClickListener());

        buffParamInputFragmentList = new ArrayList<>();
        addParamInput();

        Objects.requireNonNull(getDialog()).requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
    }

    private class CancelOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Objects.requireNonNull(getDialog()).cancel();
        }
    }

    private class EnableOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(checkInput()) {
                returnWithBuffVo(buffVo);
            } else {
                Toast toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
                toast.setText("请正确填入BUFF参数");
                toast.show();
            }
        }
    }

    private Boolean checkInput() {
        if(buffVo.getBuffInputParamList() == null || buffVo.getBuffInputParamList().isEmpty()) {
            return true;
        }
        for(int i=0;i<buffVo.getBuffInputParamList().size();i++) {
            assert buffParamInputFragmentList.size() > i;
            if(buffParamInputFragmentList.get(i).getValue() == null) {
                return false;
            } else {
                buffVo.getBuffInputParamList().get(i).setInputValue(buffParamInputFragmentList.get(i).getValue());
            }
        }
        return true;
    }


    public void returnWithBuffVo(BuffVo buffVo) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("buffVo", buffVo);
        requireActivity().setResult(RESULT_OK, returnIntent);
        requireActivity().finish();
    }

    private void addParamInput() {
        if(buffVo.getBuffInputParamList() == null || buffVo.getBuffInputParamList().isEmpty()) {
            return;
        }
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        for(BuffInputParam buffInputParam:buffVo.getBuffInputParamList()) {
            BuffParamInputFragment buffParamInputFragment = BuffParamInputFragment.newInstance(buffInputParam);
            fragmentTransaction.add(R.id.layout_buff_param, buffParamInputFragment);
            buffParamInputFragmentList.add(buffParamInputFragment);
        }
        fragmentTransaction.commit();
    }
}