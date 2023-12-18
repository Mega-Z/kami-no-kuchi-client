package com.megaz.knk.vo;

import com.megaz.knk.computation.BuffInputParam;
import com.megaz.knk.constant.BuffSourceEnum;
import com.megaz.knk.constant.EffectFieldEnum;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuffVo implements Serializable {
    private String buffId;
    private String buffTitle;
    private String buffDesc;
    private BuffSourceEnum sourceType;
    private Integer constellation;
    private String icon;
    private Boolean forced;
    private Boolean enabled;
    private String effectText;
    private Boolean percent;
    private Double effectValue;
    private EffectFieldEnum buffField;
    private List<BuffInputParam> buffInputParamList;
}
