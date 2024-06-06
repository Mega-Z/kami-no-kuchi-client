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
import android.widget.ImageView;
import android.widget.TextView;

import com.megaz.knk.R;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.TalentVo;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TalentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TalentFragment extends BaseFragment {
    private TalentVo talentVo;

    private ImageView imageTalentIcon, imageTalentFrame;
    private TextView textTalentLevel;

    public TalentFragment() {
        // Required empty public constructor
    }

    public static TalentFragment newInstance(TalentVo talentVo) {
        TalentFragment fragment = new TalentFragment();
        Bundle args = new Bundle();
        args.putSerializable("talentVo", talentVo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            talentVo = (TalentVo) getArguments().getSerializable("talentVo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_talent, container, false);
    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        imageTalentIcon = view.findViewById(R.id.img_talent_icon);
        imageTalentFrame = view.findViewById(R.id.img_talent_frame);
        textTalentLevel = view.findViewById(R.id.text_talent_level);
        textTalentLevel.setTypeface(typefaceNum);
        updateViews(talentVo);
    }

    public void updateViews(@NonNull TalentVo talentVo) {
        this.talentVo = talentVo;
        imageTalentIcon.setImageBitmap(ImageResourceUtils.getIconBitmap(Objects.requireNonNull(getContext()), talentVo.getIcon()));
        Bitmap bitmapFrame = ImageResourceUtils.getFrameByElement(Objects.requireNonNull(getContext()), talentVo.getElement());
        imageTalentFrame.setImageBitmap(bitmapFrame);
        int level = talentVo.getBaseLevel() + talentVo.getPlusLevel();
        textTalentLevel.setText(getString(R.string.text_level, level));
        if(talentVo.getPlusLevel() != 0) {
            textTalentLevel.setTextColor(getResources().getColor(R.color.talent_blue, getContext().getTheme()));
        } else {
            textTalentLevel.setTextColor(getResources().getColor(R.color.white, getContext().getTheme()));
        }

    }
}