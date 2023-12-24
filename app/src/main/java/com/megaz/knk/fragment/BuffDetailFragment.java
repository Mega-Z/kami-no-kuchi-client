package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.megaz.knk.R;
import com.megaz.knk.activity.FightEffectDetailActivity;
import com.megaz.knk.computation.BuffInputParam;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.BuffVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuffDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuffDetailFragment extends DialogFragment {
    private BuffVo buffVo;

    private TextView textBuffTitle, textBuffEffect, textBuffNumber, textBuffDesc;
    private ImageView imageSourceIcon;
    private Button buttonModify;
    private LinearLayout layoutModify;

    private List<BuffParamInputFragment> buffParamInputFragmentList;

    public BuffDetailFragment() {
        // Required empty public constructor
    }

    public static BuffDetailFragment newInstance(BuffVo buffVo) {
        BuffDetailFragment fragment = new BuffDetailFragment();
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
        return inflater.inflate(R.layout.fragment_buff_detail, container, false);
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
        textBuffNumber = view.findViewById(R.id.text_buff_number);
        textBuffNumber.setTypeface(Typeface.createFromAsset(requireActivity().getAssets(), "fonts/tttgbnumber.ttf"));
        textBuffNumber.setTextColor(requireContext()
                .getColor(DynamicStyleUtils.getBuffFieldColor(buffVo.getBuffField())));
        if(buffVo.getPercent()) {
            textBuffNumber.setText(String.format("%.2f", buffVo.getEffectValue() * 100) + "%");
        } else if (buffVo.getEffectValue() >= 1000) {
            textBuffNumber.setText(String.format("%d", Math.round(buffVo.getEffectValue())));
        } else {
            textBuffNumber.setText(String.format("%.2f", buffVo.getEffectValue()));
        }
        imageSourceIcon = view.findViewById(R.id.img_source_icon);
        if(buffVo.getIcon() != null) {
            imageSourceIcon.setImageBitmap(ImageResourceUtils.getIconBitmap(requireContext(), buffVo.getIcon()));
        } else {
            imageSourceIcon.setImageResource(R.drawable.icon_buff_default);
        }
        textBuffDesc = view.findViewById(R.id.text_buff_desc);
        textBuffDesc.setText(buffVo.getBuffDesc()+"\n");

        layoutModify = view.findViewById(R.id.layout_modify);
        buttonModify = view.findViewById(R.id.btn_modify);
        buttonModify.setOnClickListener(new ModifyOnClickListener());

        buffParamInputFragmentList = new ArrayList<>();
        setParamInput();

        Objects.requireNonNull(getDialog()).requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
    }

    private class ModifyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(checkInput()) {
                ((FightEffectDetailActivity)requireActivity()).toModifyBuff(buffVo);
                Objects.requireNonNull(getDialog()).dismiss();
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

    private void setParamInput() {
        if(buffVo.getBuffInputParamList() == null || buffVo.getBuffInputParamList().isEmpty()) {
            layoutModify.setVisibility(View.GONE);
            return;
        }
        layoutModify.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        for(BuffInputParam buffInputParam:buffVo.getBuffInputParamList()) {
            BuffParamInputFragment buffParamInputFragment = BuffParamInputFragment.newInstance(buffInputParam);
            fragmentTransaction.add(R.id.layout_buff_param, buffParamInputFragment);
            buffParamInputFragmentList.add(buffParamInputFragment);
        }
        fragmentTransaction.commit();
    }
}