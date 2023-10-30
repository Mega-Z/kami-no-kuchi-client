package com.megaz.knk.vo;

import com.megaz.knk.constant.AttributeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ArtifactProfileVo implements Serializable {
    private String artifactName;
    private String icon;
    private Integer level;
    private AttributeEnum mainAttribute;
    private Double mainAttributeVal;
    private List<AttributeEnum> subAttributes;
    private List<Double> subAttributesVal;
    private List<Integer> subAttributesCnt;
}
