package com.megaz.knk.constant;

import lombok.Getter;

@Getter
public enum AttributeEnum {
    ATK("攻击力", true, 5.83, "atk"),
    ATK_PLUS("攻击力", false, 19.45, "atk"),
    DEF("防御力", true, 7.29, "def"),
    DEF_PLUS("防御力", false, 23.15, "def"),
    HP("生命值", true, 5.83, "hp"),
    HP_PLUS("生命值", false, 298.75, "hp"),
    CRIT_RATE("暴击率", true, 3.89, "cr"),
    CRIT_DMG("暴击伤害", true, 7.77, "cd"),
    MASTERY("元素精通", false, 23.31, "mastery"),
    RECHARGE("元素充能效率", true, 6.48, "recharge"),
    DMG_PYRO("火元素伤害加成", true, 5.83, "dmg", ElementEnum.PYRO),
    DMG_ELECTRO("雷元素伤害加成", true, 5.83, "dmg", ElementEnum.ELECTRO),
    DMG_HYDRO("水元素伤害加成", true, 5.83, "dmg", ElementEnum.HYDRO),
    DMG_DENDRO("草元素伤害加成", true, 5.83, "dmg", ElementEnum.DENDRO),
    DMG_ANEMO("风元素伤害加成", true, 5.83, "dmg", ElementEnum.ANEMO),
    DMG_GEO("岩元素伤害加成", true, 5.83, "dmg", ElementEnum.GEO),
    DMG_CRYO("冰元素伤害加成", true, 5.83, "dmg", ElementEnum.CRYO),
    DMG_PHY("物理伤害加成", true, 7.29, "phy"),
    HEAL("治疗加成", true, 4.49, "heal");


    private final String desc;
    private final boolean percent;
    private final double unitValue;
    private final String weightLabel;
    private final ElementEnum element;

    AttributeEnum(String desc, boolean percent, double unitValue, String weightLabel) {
        this.desc = desc;
        this.percent = percent;
        this.unitValue = unitValue;
        this.weightLabel = weightLabel;
        this.element = ElementEnum.NULL;
    }

    AttributeEnum(String desc, boolean percent, double unitValue, String weightLabel, ElementEnum element) {
        this.desc = desc;
        this.percent = percent;
        this.unitValue = unitValue;
        this.weightLabel = weightLabel;
        this.element = element;
    }
}
