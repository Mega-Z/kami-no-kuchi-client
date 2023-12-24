package com.megaz.knk.constant;

import lombok.Getter;

@Getter
public enum BuffStageEnum {
    ATTRIBUTE_OVER_CONSTANT("不基于属性改变属性"),
    ATTRIBUTE_OVER_ATTRIBUTE("基于属性改变属性"),
    ATTRIBUTE_UNCHANGED("不会改变属性");

    private final String desc;

    BuffStageEnum(String desc) {
        this.desc = desc;
    }
}
