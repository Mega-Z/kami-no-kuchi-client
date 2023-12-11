package com.megaz.knk.constant;

import lombok.Getter;

@Getter
public enum EffectFieldEnum {
    BASE("基础倍率区", ""),
    BASE_MULTIPLE("基础倍率区", "基础倍率乘"),
    BASE_ADD("基础倍率区", "基础倍率加"),
    UP("通用加成区", ""),
    RESIST("抗性区", "抗性降低"),
    DAMAGE_UP("增伤区", "伤害加成"),
    DEFENCE("防御区", ""),
    CRIT_RATE("暴击率", ""),
    CRIT_DMG("暴击伤害", ""),
    MASTERY("精通加成", ""),
    REACTION("元素反应加成", "元素反应加成");

    private String desc;
    private String effectText;

    EffectFieldEnum(String desc, String effectText) {
        this.desc = desc;
        this.effectText = effectText;
    }
}
