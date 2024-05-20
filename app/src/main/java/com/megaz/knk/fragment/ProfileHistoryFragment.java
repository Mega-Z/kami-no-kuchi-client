package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.megaz.knk.R;
import com.megaz.knk.activity.CharacterDetailActivity;
import com.megaz.knk.dto.CharacterProfileDto;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.ArtifactEvaluationVo;
import com.megaz.knk.vo.CharacterProfileVo;

import org.w3c.dom.Text;

import java.util.List;

public abstract class ProfileHistoryFragment extends BaseFragment {

    protected CharacterProfileDto characterProfileDto;
    protected CharacterProfileVo characterProfileVo;


    public ProfileHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            characterProfileVo = (CharacterProfileVo) getArguments().getSerializable("characterProfileVo");
            characterProfileDto = (CharacterProfileDto) getArguments().getSerializable("characterProfileDto");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_history, container, false);
    }

    @SuppressLint({"ResourceAsColor", "ResourceType", "SetTextI18n", "DefaultLocale"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.text_date)).setText(simpleDateFormat.format(characterProfileVo.getUpdateTime()));
        ((TextView) view.findViewById(R.id.text_cons)).setText(characterProfileVo.getConstellation() + getString(R.string.text_constellation_suffix));
        ((TextView) view.findViewById(R.id.text_cons)).setBackgroundColor(requireContext()
                .getColor(DynamicStyleUtils.getConstellationColor(characterProfileVo.getConstellation())));
        ((TextView) view.findViewById(R.id.text_character_level)).setTypeface(typefaceNum);
        ((TextView) view.findViewById(R.id.text_character_level)).setText(getString(R.string.text_level_prefix) + characterProfileVo.getLevel());
        ((ImageView) view.findViewById(R.id.img_weapon)).setImageBitmap(
                ImageResourceUtils.getIconBitmap(requireContext(), characterProfileVo.getWeapon().getWeaponIcon()));
        ((ImageView) view.findViewById(R.id.img_weapon)).setBackgroundResource(DynamicStyleUtils.getQualityBackground(characterProfileVo.getWeapon().getStar()));
        ((TextView) view.findViewById(R.id.text_weapon_level)).setText(getString(R.string.text_level_prefix) + characterProfileVo.getWeapon().getLevel());
        ((TextView) view.findViewById(R.id.text_weapon_level)).setTypeface(typefaceNum);
        ((TextView) view.findViewById(R.id.text_weapon_refine)).setText(getString(R.string.text_refine, characterProfileVo.getWeapon().getRefineRank()));
        List<ArtifactEvaluationVo> artifactEvaluationVoList = characterProfileVo.getEvaluations();
        if(artifactEvaluationVoList != null && !artifactEvaluationVoList.isEmpty()) {
            ArtifactEvaluationVo artifactEvaluationVo = artifactEvaluationVoList.get(0);
            ((TextView) view.findViewById(R.id.text_criterion_name)).setText(artifactEvaluationVo.getCriterionName());
            Double score = artifactEvaluationVo.getTotalScore();
            String rank = DynamicStyleUtils.getRank(score/5);
            ((TextView) view.findViewById(R.id.text_artifacts_score)).setText(String.format("%.1f", score));
            ((TextView) view.findViewById(R.id.text_artifacts_score)).setTypeface(typefaceNum);
            ((TextView) view.findViewById(R.id.text_artifacts_rank)).setText(rank);
            ((TextView) view.findViewById(R.id.text_artifacts_rank)).setTypeface(typefaceNum);
            ((TextView) view.findViewById(R.id.text_artifacts_rank)).setTextColor(requireContext().getColor(DynamicStyleUtils.getRankColor(rank)));
            view.findViewById(R.id.layout_criterion).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.layout_criterion).setVisibility(View.GONE);
        }

        view.setOnTouchListener(new CharacterProfileHistoryOnTouchListener());
        view.setOnClickListener(new CharacterProfileHistoryOnClickListener());
    }

    private class CharacterProfileHistoryOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            onProfileHistoryClicked();
        }
    }

    private class CharacterProfileHistoryOnTouchListener implements View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // v.performClick();
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                v.setBackgroundResource(R.drawable.bg_profile_history_pressed);
            } else {
                v.setBackgroundResource(R.drawable.bg_profile_history);
            }
            return false;
        }
    }

    protected abstract void onProfileHistoryClicked();


}
