package com.megaz.knk.constant;

public enum CharacterDetailActivityStatusEnum {
    INITIAL("初始面板"),
    HISTORY("历史面板"),
    VIRTUAL("虚构配置"),
    SUBSTITUTION("虚构角色");

    private String desc;

    CharacterDetailActivityStatusEnum(String desc) {
        this.desc = desc;
    }
}
