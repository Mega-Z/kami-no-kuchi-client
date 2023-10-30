package com.megaz.knk.constant;

import lombok.Getter;

@Getter
public enum ArtifactPositionEnum {

    FLOWER("flower", "生之花"),
    PLUME("plume", "死之羽"),
    SANDS("sands", "时之沙"),
    GOBLET("goblet", "空之杯"),
    CIRCLET("circlet", "理之冠"),
    ;

    private final String val;
    private final String desc;

    ArtifactPositionEnum(String val, String desc) {
        this.val = val;
        this.desc = desc;
    }

}
