package com.megaz.knk.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class PlayerProfileVo implements Serializable {
    private String nickname;
    private String uid;
    private String sign;
    private String avatarIcon;
    private List<CharacterProfileVo> characters;
}
