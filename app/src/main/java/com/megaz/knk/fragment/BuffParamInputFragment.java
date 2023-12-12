package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.megaz.knk.R;
import com.megaz.knk.computation.BuffInputParam;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.BuffVo;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuffParamInputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuffParamInputFragment extends BaseFragment {
    private BuffInputParam buffInputParam;

    private TextView textParamHint, textPercent;
    private EditText editTextParamValue;

    public BuffParamInputFragment() {
        // Required empty public constructor
    }

    public static BuffParamInputFragment newInstance(BuffInputParam buffInputParam) {
        BuffParamInputFragment fragment = new BuffParamInputFragment();
        Bundle args = new Bundle();
        args.putSerializable("buffInputParam", buffInputParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buffInputParam = (BuffInputParam) getArguments().getSerializable("buffInputParam");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buff_param_input, container, false);
    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        textParamHint = view.findViewById(R.id.text_param_hint);
        textParamHint.setText(buffInputParam.getInputHint());
        textPercent = view.findViewById(R.id.text_percent);
        if(buffInputParam.getPercent()) {
            textPercent.setVisibility(View.VISIBLE);
        } else {
            textPercent.setVisibility(View.GONE);
        }
        editTextParamValue = view.findViewById(R.id.edtx_param_value);
        if(buffInputParam.getDecimal()) {
            editTextParamValue.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else {
            editTextParamValue.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    public Double getValue() {
        try {
            Double value = Double.parseDouble(editTextParamValue.getText().toString());
            if(buffInputParam.getPercent()) {
                value /= 100;
            }
            if(buffInputParam.getMaxValue() == null || value <= buffInputParam.getMaxValue()) {
                return value;
            } else {
                return null;
            }
        } catch (RuntimeException e) {
            return null;
        }
    }

}