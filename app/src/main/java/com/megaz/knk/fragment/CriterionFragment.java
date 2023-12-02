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
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.utils.ArtifactUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.ArtifactEvaluationVo;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CriterionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CriterionFragment extends BaseFragment {
    private ElementEnum element;
    private ArtifactEvaluationVo artifactEvaluationVo;
    private TextView textCriterionName, textScore, textWeightHp, textWeightAtk, textWeightDef,
            textWeightMastery, textWeightRecharge, textWeightCr, textWeightCd,
            textWeightPhy, textWeightDmg, textWeightHeal;
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
        textWeightHp = view.findViewById(R.id.text_weight_hp);
        textWeightHp.setTypeface(typefaceNum);
        textWeightAtk = view.findViewById(R.id.text_weight_atk);
        textWeightAtk.setTypeface(typefaceNum);
        textWeightDef = view.findViewById(R.id.text_weight_def);
        textWeightDef.setTypeface(typefaceNum);
        textWeightMastery = view.findViewById(R.id.text_weight_mastery);
        textWeightMastery.setTypeface(typefaceNum);
        textWeightRecharge = view.findViewById(R.id.text_weight_recharge);
        textWeightRecharge.setTypeface(typefaceNum);
        textWeightCr = view.findViewById(R.id.text_weight_crit_rate);
        textWeightCr.setTypeface(typefaceNum);
        textWeightCd = view.findViewById(R.id.text_weight_crit_dmg);
        textWeightCd.setTypeface(typefaceNum);
        textWeightPhy = view.findViewById(R.id.text_weight_phy);
        textWeightPhy.setTypeface(typefaceNum);
        textWeightDmg = view.findViewById(R.id.text_weight_dmg);
        textWeightDmg.setTypeface(typefaceNum);
        textWeightHeal = view.findViewById(R.id.text_weight_heal);
        textWeightHeal.setTypeface(typefaceNum);

        imageElement.setImageBitmap(ImageResourceUtils.getElementIcon(Objects.requireNonNull(getContext()), element));
        textCriterionName.setText(artifactEvaluationVo.getCriterionName());
        textScore.setText(String.format("%.1f", artifactEvaluationVo.getTotalScore()));
        textScore.setTextColor(getContext().getColor(
                ArtifactUtils.getRankColor(ArtifactUtils.getRank(artifactEvaluationVo.getTotalScore()/5))));
        textWeightHp.setText(String.valueOf(artifactEvaluationVo.getHpWeight()));
        textWeightHp.setTextColor(getContext().getColor(ArtifactUtils.getWeightColor(artifactEvaluationVo.getHpWeight())));
        textWeightAtk.setText(String.valueOf(artifactEvaluationVo.getAtkWeight()));
        textWeightAtk.setTextColor(getContext().getColor(ArtifactUtils.getWeightColor(artifactEvaluationVo.getAtkWeight())));
        textWeightDef.setText(String.valueOf(artifactEvaluationVo.getDefWeight()));
        textWeightDef.setTextColor(getContext().getColor(ArtifactUtils.getWeightColor(artifactEvaluationVo.getDefWeight())));
        textWeightMastery.setText(String.valueOf(artifactEvaluationVo.getMasteryWeight()));
        textWeightMastery.setTextColor(getContext().getColor(ArtifactUtils.getWeightColor(artifactEvaluationVo.getMasteryWeight())));
        textWeightRecharge.setText(String.valueOf(artifactEvaluationVo.getRechargeWeight()));
        textWeightRecharge.setTextColor(getContext().getColor(ArtifactUtils.getWeightColor(artifactEvaluationVo.getRechargeWeight())));
        textWeightCr.setText(String.valueOf(artifactEvaluationVo.getCrWeight()));
        textWeightCr.setTextColor(getContext().getColor(ArtifactUtils.getWeightColor(artifactEvaluationVo.getCrWeight())));
        textWeightCd.setText(String.valueOf(artifactEvaluationVo.getCdWeight()));
        textWeightCd.setTextColor(getContext().getColor(ArtifactUtils.getWeightColor(artifactEvaluationVo.getCdWeight())));
        textWeightPhy.setText(String.valueOf(artifactEvaluationVo.getPhyWeight()));
        textWeightPhy.setTextColor(getContext().getColor(ArtifactUtils.getWeightColor(artifactEvaluationVo.getPhyWeight())));
        textWeightDmg.setText(String.valueOf(artifactEvaluationVo.getDmgWeight()));
        textWeightDmg.setTextColor(getContext().getColor(ArtifactUtils.getWeightColor(artifactEvaluationVo.getDmgWeight())));
        textWeightHeal.setText(String.valueOf(artifactEvaluationVo.getHealWeight()));
        textWeightHeal.setTextColor(getContext().getColor(ArtifactUtils.getWeightColor(artifactEvaluationVo.getHealWeight())));
    }
}