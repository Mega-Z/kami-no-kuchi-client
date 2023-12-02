package com.megaz.knk.constant;

import lombok.Getter;

@Getter
public enum ElementEnum {
    ANEMO("anemo", "风"),
    GEO("geo", "岩"),
    ELECTRO("electro", "雷"),
    DENDRO("dendro", "草"),
    HYDRO("hydro", "水"),
    PYRO("pyro", "火"),
    CRYO("cryo", "冰"),
    NULL("phy", "物理"),
    ;

    private final String val;
    private final String desc;

    ElementEnum(String val, String desc) {
        this.val = val;
        this.desc = desc;
    }
}
