package com.megaz.knk.vo;

import com.megaz.knk.constant.EffectFieldEnum;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EffectDetailVo {
    private String effectDesc;
    private String effectValue;
    private Boolean canCritical;
    private Map<EffectFieldEnum, Double> fieldDetail;

}
