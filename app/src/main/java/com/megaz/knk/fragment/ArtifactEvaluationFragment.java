package com.megaz.knk.fragment;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.megaz.knk.R;
import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.constant.AttributeLabelEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.ArtifactEvaluationVo;
import com.megaz.knk.vo.CharacterProfileVo;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArtifactEvaluationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtifactEvaluationFragment extends BaseFragment {
    
    private CharacterProfileVo characterProfileVo;
    private ArtifactEvaluationVo currentEvaluation;
    private Map<Integer, ArtifactEvaluationVo> evaluationMap;

    private TextView textArtifactsScore, textArtifactsRank, textCriterionName;
    private Map<AttributeLabelEnum, TextView> textAttributeWeights;
    private LinearLayout layoutCriterionSelect, layoutArtifactOverview;
    private Map<ArtifactPositionEnum, ArtifactFragment> artifactFragmentMap;
    private int CRITERION_SELECTION_HEIGHT;

    private ValueAnimator animatorCriteriaExtend, animatorCriteriaRetract;


    public ArtifactEvaluationFragment() {
        // Required empty public constructor
    }
    public static ArtifactEvaluationFragment newInstance(CharacterProfileVo characterProfileVo) {
        ArtifactEvaluationFragment fragment = new ArtifactEvaluationFragment();
        Bundle args = new Bundle();
        args.putSerializable("characterProfileVo", characterProfileVo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            characterProfileVo = (CharacterProfileVo) getArguments().getSerializable("characterProfileVo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artifact_evaluation, container, false);
    }


    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        initEvaluationViews(view);
        addCriteriaFragments();
        updateEvaluationViews(null);
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        animatorCriteriaExtend = ValueAnimator.ofFloat(0,1);
        animatorCriteriaExtend.setDuration(200);
        animatorCriteriaExtend.addUpdateListener(new CriterionAnimatorUpdateListener());
        animatorCriteriaRetract = ValueAnimator.ofFloat(1,0);
        animatorCriteriaRetract.setDuration(200);
        animatorCriteriaRetract.addUpdateListener(new CriterionAnimatorUpdateListener());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvaluationViews(@NonNull View view) {
        ((ImageView) view.findViewById(R.id.img_criterion_element)).setImageBitmap(
                ImageResourceUtils.getElementIcon(requireContext(), characterProfileVo.getElement()));
        textArtifactsScore = view.findViewById(R.id.text_artifacts_score);
        textArtifactsRank = view.findViewById(R.id.text_artifacts_rank);
        textCriterionName = view.findViewById(R.id.text_criterion_name);
        textAttributeWeights = new HashMap<>();
        textAttributeWeights.put(AttributeLabelEnum.HP, view.findViewById(R.id.text_weight_hp));
        textAttributeWeights.put(AttributeLabelEnum.ATK, view.findViewById(R.id.text_weight_atk));
        textAttributeWeights.put(AttributeLabelEnum.DEF, view.findViewById(R.id.text_weight_def));
        textAttributeWeights.put(AttributeLabelEnum.MASTERY, view.findViewById(R.id.text_weight_mastery));
        textAttributeWeights.put(AttributeLabelEnum.RECHARGE, view.findViewById(R.id.text_weight_recharge));
        textAttributeWeights.put(AttributeLabelEnum.CR, view.findViewById(R.id.text_weight_crit_rate));
        textAttributeWeights.put(AttributeLabelEnum.CD, view.findViewById(R.id.text_weight_crit_dmg));
        textAttributeWeights.put(AttributeLabelEnum.PHY, view.findViewById(R.id.text_weight_phy));
        textAttributeWeights.put(AttributeLabelEnum.DMG, view.findViewById(R.id.text_weight_dmg));
        textAttributeWeights.put(AttributeLabelEnum.HEAL, view.findViewById(R.id.text_weight_heal));
        for(TextView textAttributeWeight: textAttributeWeights.values()) {
            textAttributeWeight.setTypeface(typefaceNum);
        }
        textArtifactsScore.setTypeface(typefaceNum);
        textArtifactsRank.setTypeface(typefaceNum);

        layoutCriterionSelect = view.findViewById(R.id.layout_criterion_select);
        layoutArtifactOverview = view.findViewById(R.id.layout_artifact_overview);
        layoutArtifactOverview.setOnClickListener(new ArtifactOverviewOnClickListener());
        layoutArtifactOverview.setOnTouchListener(new ArtifactOverviewOnTouchListener());

        if(characterProfileVo.getEvaluations().size() > 0) {
            characterProfileVo.getEvaluations().sort(new Comparator<ArtifactEvaluationVo>() {
                @Override
                public int compare(ArtifactEvaluationVo o1, ArtifactEvaluationVo o2) {
                    return o2.getTotalScore().compareTo(o1.getTotalScore());
                }
            });
            currentEvaluation = characterProfileVo.getEvaluations().get(0);
            CRITERION_SELECTION_HEIGHT = getResources().getDimensionPixelOffset(R.dimen.dp_9) +
                    characterProfileVo.getEvaluations().size() * getResources().getDimensionPixelOffset(R.dimen.dp_65);
        } else {
            CRITERION_SELECTION_HEIGHT = 0;
            return;
        }
        artifactFragmentMap = new HashMap<>();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        for(ArtifactPositionEnum position : GenshinConstantMeta.ARTIFACT_POSITION_LIST) {
            ArtifactFragment artifactFragment = ArtifactFragment.newInstance(
                    characterProfileVo.getArtifacts().get(position),
                    currentEvaluation,
                    position);
            String layoutId = "layout_artifact_"+position.getVal();
            fragmentTransaction.add(getContext().getResources().getIdentifier(
                    layoutId,"id", getContext().getPackageName()), artifactFragment);
            artifactFragmentMap.put(position, artifactFragment);
        }
        fragmentTransaction.commit();

    }

    private static class ArtifactOverviewOnTouchListener implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
                v.setBackgroundResource(R.drawable.bg_artifact_overview_press);
            }else{
                v.setBackgroundResource(R.drawable.bg_artifact_overview);
            }
            return false;
        }
    }

    private class ArtifactOverviewOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(layoutCriterionSelect.getVisibility() == View.VISIBLE) {
                hideCriterionSelection();
                return;
            }
            showCriterionSelection();
        }
    }

    private class CriterionOnTouchListener implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
                v.setBackgroundColor(requireContext().getColor(R.color.criterion_press));
            }else{
                v.setBackgroundColor(requireContext().getColor(R.color.transparent));
            }
            return false;
        }
    }

    private class CriterionOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            currentEvaluation = evaluationMap.get(v.getId());
            hideCriterionSelection();
            updateEvaluationViews(currentEvaluation);
        }
    }

    private class CriterionAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layoutCriterionSelect.getLayoutParams();
            layoutParams.height = Math.round(CRITERION_SELECTION_HEIGHT * (float) animation.getAnimatedValue());
            layoutCriterionSelect.setLayoutParams(layoutParams);
            if((float)animation.getAnimatedValue() <= 0){
                layoutCriterionSelect.setVisibility(View.GONE);
            } else {
                layoutCriterionSelect.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showCriterionSelection() {
        if(animatorCriteriaExtend != null) {
            animatorCriteriaExtend.cancel();
            animatorCriteriaExtend.start();
        }
    }


    private void hideCriterionSelection() {
        if(animatorCriteriaRetract != null) {
            animatorCriteriaRetract.cancel();
            animatorCriteriaRetract.start();
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void addCriteriaFragments() {
        evaluationMap = new HashMap<>();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        for(int i=0;i<characterProfileVo.getEvaluations().size();i++) {
            if(i>0) {
                View viewDividingLine = new View(getContext());
                viewDividingLine.setBackgroundResource(R.drawable.bg_div_gr_black);
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.dp_1));
                viewDividingLine.setLayoutParams(lineParams);
                layoutCriterionSelect.addView(viewDividingLine);
            }
            LinearLayout layoutContainer = new LinearLayout(getContext());
            layoutContainer.setOnClickListener(new CriterionOnClickListener());
            layoutContainer.setOnTouchListener(new CriterionOnTouchListener());
            int id = View.generateViewId();
            layoutContainer.setId(id);
            evaluationMap.put(id, characterProfileVo.getEvaluations().get(i));
            CriterionFragment criterionFragment = CriterionFragment.newInstance(
                    characterProfileVo.getEvaluations().get(i), characterProfileVo.getElement());
            fragmentTransaction.add(id, criterionFragment);
            layoutCriterionSelect.addView(layoutContainer);
        }
        fragmentTransaction.commit();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateEvaluationViews(ArtifactEvaluationVo newArtifactEvaluationVo) {
        if(currentEvaluation == null) {
            return;
        }
        if(newArtifactEvaluationVo != null) {
            currentEvaluation = newArtifactEvaluationVo;
        }
        Double score = currentEvaluation.getTotalScore();
        String rank = DynamicStyleUtils.getRank(score/5);
        textArtifactsScore.setText(String.format("%.1f", score));
        textArtifactsRank.setText(rank);
        textArtifactsRank.setTextColor(requireContext().getColor(DynamicStyleUtils.getRankColor(rank)));
        textCriterionName.setText(
                getString(R.string.text_criterion_name_prefix) + currentEvaluation.getCriterionName());
        for(Map.Entry<AttributeLabelEnum, TextView> entry:textAttributeWeights.entrySet()) {
            Integer weight = currentEvaluation.getAttributeWeight().get(entry.getKey());
            assert weight != null;
            entry.getValue().setText(String.format("%d", weight));
            entry.getValue().setTextColor(requireContext().getColor(DynamicStyleUtils.getWeightColor(weight)));
        }
        if(newArtifactEvaluationVo != null) { // update artifact fragments
            for(ArtifactPositionEnum position : GenshinConstantMeta.ARTIFACT_POSITION_LIST) {
                ArtifactFragment artifactFragment = artifactFragmentMap.get(position);
                assert artifactFragment != null;
                artifactFragment.updateArtifactView(newArtifactEvaluationVo);
            }
        }
    }
}