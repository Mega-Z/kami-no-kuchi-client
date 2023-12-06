package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
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
import com.megaz.knk.constant.AttributeLabelEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.ArtifactEvaluationVo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CriterionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CriterionFragment extends BaseFragment {
    private ElementEnum element;
    private ArtifactEvaluationVo artifactEvaluationVo;
    private TextView textCriterionName, textScore;
    private Map<AttributeLabelEnum, TextView> textAttributeWeights;
    private ImageView imageElement;


    public CriterionFragment() {
        // Required empty public constructor
    }

    public static CriterionFragment newInstance(ArtifactEvaluationVo artifactEvaluationVo, ElementEnum element) {
        CriterionFragment fragment = new CriterionFragment();
        Bundle args = new Bundle();
        args.putSerializable("artifactEvaluationVo", artifactEvaluationVo);
        args.putSerializable("element", element);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            artifactEvaluationVo = (ArtifactEvaluationVo) getArguments().getSerializable("artifactEvaluationVo");
            element = (ElementEnum) getArguments().getSerializable("element");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_criterion, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textCriterionName = view.findViewById(R.id.text_criterion_name);
        textScore = view.findViewById(R.id.text_score);
        textScore.setTypeface(typefaceNum);
        imageElement = view.findViewById(R.id.img_criterion_element);
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

        imageElement.setImageBitmap(ImageResourceUtils.getElementIcon(Objects.requireNonNull(getContext()), element));
        textCriterionName.setText(artifactEvaluationVo.getCriterionName());
        textScore.setText(String.format("%.1f", artifactEvaluationVo.getTotalScore()));
        textScore.setTextColor(getContext().getColor(
                DynamicStyleUtils.getRankColor(DynamicStyleUtils.getRank(artifactEvaluationVo.getTotalScore()/5))));

        for(Map.Entry<AttributeLabelEnum, TextView> entry:textAttributeWeights.entrySet()) {
            Integer weight = artifactEvaluationVo.getAttributeWeight().get(entry.getKey());
            assert weight != null;
            entry.getValue().setText(String.format("%d", weight));
            entry.getValue().setTextColor(
                    getResources().getColor(DynamicStyleUtils.getWeightColor(weight), getContext().getTheme()));
        }
    }
}