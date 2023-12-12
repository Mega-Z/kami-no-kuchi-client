package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.megaz.knk.R;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.BuffVo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuffSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuffSelectionFragment extends BaseFragment {
    private BuffVo buffVo;

    private TextView textBuffTitle, textBuffEffect;
    private ImageView imageSourceIcon;


    public BuffSelectionFragment() {
        // Required empty public constructor
    }

    public static BuffSelectionFragment newInstance(BuffVo buffVo) {
        BuffSelectionFragment fragment = new BuffSelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable("buffVo", buffVo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            buffVo = (BuffVo) getArguments().getSerializable("buffVo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buff_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
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
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        view.setOnTouchListener(new BuffOnTouchListener());
        view.setOnClickListener(new BuffOnClickListener());
    }


    private class BuffOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            BuffEnableFragment buffEnableFragment = BuffEnableFragment.newInstance(buffVo);
            buffEnableFragment.show(getParentFragmentManager(), "");
        }
    }

    private static class BuffOnTouchListener implements View.OnTouchListener{

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // v.performClick();
            if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
                v.setBackgroundResource(R.drawable.bg_enabled_buff_pressed);
            }else{
                v.setBackgroundResource(R.drawable.bg_enabled_buff);
            }
            return false;
        }
    }
}