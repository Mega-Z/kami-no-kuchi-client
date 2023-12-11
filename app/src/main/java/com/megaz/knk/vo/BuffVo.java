package com.megaz.knk.vo;

import com.megaz.knk.constant.BuffSourceEnum;
import com.megaz.knk.constant.EffectFieldEnum;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuffVo implements Serializable {
    private String buffTitle;
    private String buffDesc;
    private BuffSourceEnum sourceType;
    private String icon;
    private Boolean enabled;
    private String effectText;
    private Boolean percent;
    private Double effectValue;
    private EffectFieldEnum buffField;

}
