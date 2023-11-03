package com.megaz.knk.vo;

import com.megaz.knk.constant.ElementEnum;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConstellationVo implements Serializable {
    private ElementEnum element;
    private Boolean active;
    private String icon;
}
