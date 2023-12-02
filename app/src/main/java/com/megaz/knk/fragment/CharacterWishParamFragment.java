package com.megaz.knk.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.megaz.knk.R;
import com.megaz.knk.activity.WishCalculatorActivity;
import com.megaz.knk.utils.ViewUtils;
import com.megaz.knk.view.CheckBoxView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CharacterWishParamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterWishParamFragment extends BaseFragment {
    private SeekBar seekBarTarget, seekBarConsumed;
    private CheckBoxView checkBoxBigGuarantee;
    private TextView textSeekBarTarget, textSeekBarConsumed, textBigGuarantee;

    public CharacterWishParamFragment() {
        // Required empty public constructor
    }
    public static CharacterWishParamFragment newInstance() {
        return new CharacterWishParamFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character_wish_param, container, false);
    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        seekBarTarget = view.findViewById(R.id.seekbar_target);
        seekBarConsumed = view.findViewById(R.id.seekbar_consumed);
        checkBoxBigGuarantee = view.findViewById(R.id.checkbox_big_guarantee);
        checkBoxBigGuarantee.setChecked(false);
        textSeekBarTarget = view.findViewById(R.id.text_seekbar_target);
        textSeekBarConsumed = view.findViewById(R.id.text_seekbar_consumed);
        textBigGuarantee = view.findViewById(R.id.text_big_guarantee);
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);

        seekBarTarget.setOnSeekBarChangeListener(new OnSeekbarChangeTextUpdateListener(textSeekBarTarget));
        seekBarConsumed.setOnSeekBarChangeListener(new OnSeekbarChangeTextUpdateListener(textSeekBarConsumed));
        checkBoxBigGuarantee.setOnCheckedChangeListener(new OnCheckedChangeTextUpdateListener(textBigGuarantee));

    }

    private class OnSeekbarChangeTextUpdateListener implements SeekBar.OnSeekBarChangeListener {
        private TextView textView;

        public OnSeekbarChangeTextUpdateListener(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            ViewUtils.updateSeekbarText(seekBar, textView, progress, "");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private class OnCheckedChangeTextUpdateListener implements CheckBoxView.OnCheckedChangeListener {

        private TextView textView;

        public OnCheckedChangeTextUpdateListener(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void onCheckedChanged(CheckBoxView buttonView, boolean isChecked) {
            if(isChecked) {
                textView.setText(getString(R.string.text_yep));
            } else {
                textView.setText(getString(R.string.text_nope));
            }
        }
    }

    public int getTarget() {
        return seekBarTarget.getProgress();
    }

    public int getConsumed() {
        return seekBarConsumed.getProgress();
    }

    public boolean getBigGuarantee() {
        return checkBoxBigGuarantee.isChecked();
    }
}