package com.megaz.knk.vo;

import com.megaz.knk.constant.AttributeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class WeaponProfileVo implements Serializable {
    private String weaponName;
    private String weaponIcon;
    private Integer level;
    private Integer refineRank;
    private Integer star;
    /***
    private Double baseAtk;
    private AttributeEnum attribute;
    private Double attributeVal;***/
}
