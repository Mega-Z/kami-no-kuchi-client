package com.megaz.knk.constant;

import lombok.Getter;

@Getter
public enum ShownAttributeEnum {
    ATK("攻击力", false),
    DEF("防御力", false),
    HP("生命值", false),
    CRIT_RATE("暴击率", true),
    CRIT_DMG("暴击伤害", true),
    MASTERY("元素精通", false),
    RECHARGE("元素充能效率", true),
    DMG("伤害加成", true),
    HEAL("治疗加成", true);

    private final String desc;
    private final boolean percent;

    ShownAttributeEnum(String desc, boolean percent) {
        this.desc = desc;
        this.percent = percent;
    }
}
