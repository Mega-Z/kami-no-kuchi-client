package com.megaz.knk.vo;

import com.megaz.knk.constant.ElementEnum;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TalentVo implements Serializable {
    private ElementEnum element;
    private Integer baseLevel;
    private Integer plusLevel;
    private String icon;
}
