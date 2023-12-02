package com.megaz.knk.vo;

import com.megaz.knk.constant.ArtifactPositionEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ArtifactEvaluationVo implements Serializable {
    private String criterionName;
    private Integer hpWeight;
    private Integer atkWeight;
    private Integer defWeight;
    private Integer crWeight;
    private Integer cdWeight;
    private Integer masteryWeight;
    private Integer rechargeWeight;
    private Integer dmgWeight;
    private Integer phyWeight;
    private Integer healWeight;
    private Double totalScore;
    private Map<ArtifactPositionEnum, Double> artifactsScore;
    private Map<ArtifactPositionEnum, Double> artifactsMainAttrScore;
    private Map<ArtifactPositionEnum, List<Double>> artifactsSubAttrScore;
}
