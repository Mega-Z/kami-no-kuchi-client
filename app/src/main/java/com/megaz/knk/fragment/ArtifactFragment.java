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
import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.ArtifactEvaluationVo;
import com.megaz.knk.vo.ArtifactProfileVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArtifactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtifactFragment extends BaseFragment {
    private ArtifactProfileVo artifactProfileVo;
    private ArtifactEvaluationVo artifactEvaluationVo;
    private ArtifactPositionEnum position;

    private ImageView imageArtifact, imageNullArtifact;
    private TextView textArtifactName, textArtifactLevel, textArtifactScore, textMainAttribute, textMainAttributeValue;
    private List<TextView> textSubAttributes, textSubAttributesValue;

    public ArtifactFragment() {
        // Required empty public constructor
    }

    public static ArtifactFragment newInstance(ArtifactProfileVo artifactProfileVo,
                                               ArtifactEvaluationVo artifactEvaluationVo,
                                               ArtifactPositionEnum position) {
        ArtifactFragment fragment = new ArtifactFragment();
        Bundle args = new Bundle();
        args.putSerializable("artifactProfileVo", artifactProfileVo);
        args.putSerializable("artifactEvaluationVo", artifactEvaluationVo);
        args.putSerializable("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = (ArtifactPositionEnum) getArguments().getSerializable("position");
            artifactEvaluationVo = (ArtifactEvaluationVo) getArguments().getSerializable("artifactEvaluationVo");
            artifactProfileVo = (ArtifactProfileVo) getArguments().getSerializable("artifactProfileVo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artifact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initArtifactView(view);
        updateArtifactView(null);
    }

    private void initArtifactView(View view) {
        imageArtifact = view.findViewById(R.id.img_artifact);
        imageNullArtifact = view.findViewById(R.id.img_artifact_null);
        textArtifactName = view.findViewById(R.id.text_artifact_name);
        textArtifactScore = view.findViewById(R.id.text_artifact_score);
        textArtifactScore.setTypeface(typefaceNum);
        textArtifactLevel = view.findViewById(R.id.text_artifact_level);
        textArtifactLevel.setTypeface(typefaceNum);
        textMainAttribute = view.findViewById(R.id.text_artifact_main_attribute);
        textMainAttributeValue = view.findViewById(R.id.text_artifact_main_attribute_value);
        textMainAttributeValue.setTypeface(typefaceNum);
        textSubAttributes = new ArrayList<>();
        textSubAttributes.add(view.findViewById(R.id.text_artifact_sub_attribute_1));
        textSubAttributes.add(view.findViewById(R.id.text_artifact_sub_attribute_2));
        textSubAttributes.add(view.findViewById(R.id.text_artifact_sub_attribute_3));
        textSubAttributes.add(view.findViewById(R.id.text_artifact_sub_attribute_4));
        textSubAttributesValue = new ArrayList<>();
        textSubAttributesValue.add(view.findViewById(R.id.text_artifact_sub_attribute_1_value));
        textSubAttributesValue.add(view.findViewById(R.id.text_artifact_sub_attribute_2_value));
        textSubAttributesValue.add(view.findViewById(R.id.text_artifact_sub_attribute_3_value));
        textSubAttributesValue.add(view.findViewById(R.id.text_artifact_sub_attribute_4_value));
        for(TextView text:textSubAttributesValue) {
            text.setTypeface(typefaceNum);
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void updateArtifactView(ArtifactEvaluationVo newArtifactEvaluationVo) {
        if(artifactProfileVo == null) {
            imageNullArtifact.setImageBitmap(ImageResourceUtils.getArtifactPositionIcon(requireContext(), position));
            imageNullArtifact.setVisibility(View.VISIBLE);
            imageArtifact.setVisibility(View.INVISIBLE);
            textArtifactName.setVisibility(View.INVISIBLE);
            textArtifactLevel.setVisibility(View.INVISIBLE);
            textArtifactScore.setVisibility(View.INVISIBLE);
            return;
        }
        if(newArtifactEvaluationVo != null) {
            artifactEvaluationVo = newArtifactEvaluationVo;
        }
        imageNullArtifact.setVisibility(View.INVISIBLE);
        imageArtifact.setVisibility(View.VISIBLE);
        imageArtifact.setImageBitmap(ImageResourceUtils.getIconBitmap(requireContext(), artifactProfileVo.getIcon()));
        textArtifactName.setVisibility(View.VISIBLE);
        textArtifactName.setText(artifactProfileVo.getArtifactName());
        textArtifactLevel.setVisibility(View.VISIBLE);
        textArtifactLevel.setText(getString(R.string.text_artifact_level_prefix)+artifactProfileVo.getLevel());
        textArtifactScore.setVisibility(View.VISIBLE);
        Double score = artifactEvaluationVo.getArtifactsScore().get(position);
        String rank = DynamicStyleUtils.getRank(Objects.requireNonNull(score));
        textArtifactScore.setText(String.format("%.1f-%s", score, rank));
        textArtifactScore.setTextColor(getResources().getColor(DynamicStyleUtils.getRankColor(rank), getContext().getTheme()));

        AttributeEnum mainAttribute = artifactProfileVo.getMainAttribute();
        int mainAttributeColor = getResources().getColor(
                DynamicStyleUtils.getAttributeColor(mainAttribute, artifactEvaluationVo), getContext().getTheme());
        textMainAttribute.setText(mainAttribute.getDesc());
        textMainAttribute.setTextColor(mainAttributeColor);
        String mainAttributeValueString = mainAttribute.isPercent() ?
                String.format("+%.1f", artifactProfileVo.getMainAttributeVal() * 100)+"%" :
                String.format("+%d", Math.round(artifactProfileVo.getMainAttributeVal()));
        textMainAttributeValue.setText(mainAttributeValueString);
        textMainAttributeValue.setTextColor(mainAttributeColor);

        List<AttributeEnum> subAttributes = artifactProfileVo.getSubAttributes();
        List<Double> subAttributesVal = artifactProfileVo.getSubAttributesVal();
        List<Integer> subAttributesCnt = artifactProfileVo.getSubAttributesCnt();

        for(int i=0;i<textSubAttributes.size();i++) {
            if(i<subAttributes.size()) {
                AttributeEnum subAttribute = subAttributes.get(i);
                int subAttributeColor =getResources().getColor(
                        DynamicStyleUtils.getAttributeColor(subAttribute, artifactEvaluationVo), getContext().getTheme());

                String subAttributeString = subAttribute.getDesc();
                textSubAttributes.get(i).setText(subAttributeString+ DynamicStyleUtils.getCircledNum(subAttributesCnt.get(i)));
                textSubAttributes.get(i).setTextColor(subAttributeColor);
                String subAttributeValueString = subAttribute.isPercent() ?
                        String.format("+%.1f", subAttributesVal.get(i) * 100)+"%" :
                        String.format("+%d", Math.round(subAttributesVal.get(i)));
                textSubAttributesValue.get(i).setText(subAttributeValueString);
                textSubAttributesValue.get(i).setTextColor(subAttributeColor);

            } else {
                textSubAttributes.get(i).setText("");
                textSubAttributesValue.get(i).setText("");
            }
        }

    }
}