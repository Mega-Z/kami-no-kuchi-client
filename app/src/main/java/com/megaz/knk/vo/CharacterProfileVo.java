package com.megaz.knk.vo;

import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.constant.ElementEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CharacterProfileVo implements Serializable {
    private String characterName;
    private String artIcon;
    private String cardIcon;
    private ElementEnum element;
    private Integer level;
    private Integer fetter;
    private Boolean newData;
    private Date updateTime;
    // attributes
    private Double baseHp;
    private Double plusHp;
    private Double baseAtk;
    private Double plusAtk;
    private Double baseDef;
    private Double plusDef;
    private Double mastery;
    private Double critRate;
    private Double critDmg;
    private Double recharge;
    private Double dmgPyro;
    private Double dmgElectro;
    private Double dmgHydro;
    private Double dmgDendro;
    private Double dmgAnemo;
    private Double dmgGeo;
    private Double dmgCryo;
    private Double dmgPhy;
    private Double heal;
    // talents
    private String talentAIcon;
    private String talentEIcon;
    private String talentQIcon;
    private Integer talentABaseLevel;
    private Integer talentAPlusLevel;
    private Integer talentEBaseLevel;
    private Integer talentEPlusLevel;
    private Integer talentQBaseLevel;
    private Integer talentQPlusLevel;
    // constellation
    private Integer constellation;
    private List<String> consIcons;
    // equipments
    private WeaponProfileVo weapon;
    private Map<ArtifactPositionEnum, ArtifactProfileVo> artifacts;
    private List<ArtifactEvaluationVo> evaluations;
}
