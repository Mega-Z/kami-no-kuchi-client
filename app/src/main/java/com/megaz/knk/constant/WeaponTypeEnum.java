package com.megaz.knk.constant;

import lombok.Getter;

@Getter
public enum WeaponTypeEnum {

    SWORD("sword", "单手剑"),
    CLAYMORE("claymore", "双手剑"),
    POLEARM("polearm", "长柄武器"),
    BOW("bow", "弓"),
    CATALYST("catalyst", "法器"),
    ;

    private final String val;
    private final String desc;

    WeaponTypeEnum(String val, String desc) {
        this.val = val;
        this.desc = desc;
    }
}
