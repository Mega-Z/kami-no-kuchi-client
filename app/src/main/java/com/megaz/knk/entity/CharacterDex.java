package com.megaz.knk.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.WeaponTypeEnum;

@Entity(tableName = "character_dex")
public class CharacterDex extends MetaDataEntity {
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "character_id")
    private String characterId;
    @ColumnInfo(name = "character_name")
    private String characterName;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "element")
    private ElementEnum element;
    @ColumnInfo(name = "weapon_type")
    private WeaponTypeEnum weaponType;
    @ColumnInfo(name = "star")
    private Integer star;
    @ColumnInfo(name = "base_hp")
    private Double baseHp;
    @ColumnInfo(name = "base_atk")
    private Double baseAtk;
    @ColumnInfo(name = "base_def")
    private Double baseDef;
    @ColumnInfo(name = "curve_base_hp")
    private String curveBaseHp;
    @ColumnInfo(name = "curve_base_atk")
    private String curveBaseAtk;
    @ColumnInfo(name = "curve_base_def")
    private String curveBaseDef;
    @ColumnInfo(name = "promote_id")
    private String promoteId;
    @ColumnInfo(name = "talent_a_id")
    private Integer talentAId;
    @ColumnInfo(name = "talent_e_id")
    private Integer talentEId;
    @ColumnInfo(name = "talent_q_id")
    private Integer talentQId;
    @ColumnInfo(name = "talent_a_up")
    private Integer talentAUp;
    @ColumnInfo(name = "talent_e_up")
    private Integer talentEUp;
    @ColumnInfo(name = "talent_q_up")
    private Integer talentQUp;
    @ColumnInfo(name = "talent_a_up_cons")
    private Integer talentAUpCons;
    @ColumnInfo(name = "talent_e_up_cons")
    private Integer talentEUpCons;
    @ColumnInfo(name = "talent_q_up_cons")
    private Integer talentQUpCons;
    @ColumnInfo(name = "icon_art")
    private String iconArt;
    @ColumnInfo(name = "icon_avatar")
    private String iconAvatar;
    @ColumnInfo(name = "icon_card")
    private String iconCard;
    @ColumnInfo(name = "icon_side")
    private String iconSide;
    @ColumnInfo(name = "icon_talent_a")
    private String iconTalentA;
    @ColumnInfo(name = "icon_talent_e")
    private String iconTalentE;
    @ColumnInfo(name = "icon_talent_q")
    private String iconTalentQ;
    @ColumnInfo(name = "icon_cons_1")
    private String iconCons1;
    @ColumnInfo(name = "icon_cons_2")
    private String iconCons2;
    @ColumnInfo(name = "icon_cons_3")
    private String iconCons3;
    @ColumnInfo(name = "icon_cons_4")
    private String iconCons4;
    @ColumnInfo(name = "icon_cons_5")
    private String iconCons5;
    @ColumnInfo(name = "icon_cons_6")
    private String iconCons6;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ElementEnum getElement() {
        return element;
    }

    public void setElement(ElementEnum element) {
        this.element = element;
    }

    public WeaponTypeEnum getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(WeaponTypeEnum weaponType) {
        this.weaponType = weaponType;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public Double getBaseHp() {
        return baseHp;
    }

    public void setBaseHp(Double baseHp) {
        this.baseHp = baseHp;
    }

    public Double getBaseAtk() {
        return baseAtk;
    }

    public void setBaseAtk(Double baseAtk) {
        this.baseAtk = baseAtk;
    }

    public Double getBaseDef() {
        return baseDef;
    }

    public void setBaseDef(Double baseDef) {
        this.baseDef = baseDef;
    }

    public String getCurveBaseHp() {
        return curveBaseHp;
    }

    public void setCurveBaseHp(String curveBaseHp) {
        this.curveBaseHp = curveBaseHp;
    }

    public String getCurveBaseAtk() {
        return curveBaseAtk;
    }

    public void setCurveBaseAtk(String curveBaseAtk) {
        this.curveBaseAtk = curveBaseAtk;
    }

    public String getCurveBaseDef() {
        return curveBaseDef;
    }

    public void setCurveBaseDef(String curveBaseDef) {
        this.curveBaseDef = curveBaseDef;
    }

    public String getPromoteId() {
        return promoteId;
    }

    public void setPromoteId(String promoteId) {
        this.promoteId = promoteId;
    }

    public Integer getTalentAId() {
        return talentAId;
    }

    public void setTalentAId(Integer talentAId) {
        this.talentAId = talentAId;
    }

    public Integer getTalentEId() {
        return talentEId;
    }

    public void setTalentEId(Integer talentEId) {
        this.talentEId = talentEId;
    }

    public Integer getTalentQId() {
        return talentQId;
    }

    public void setTalentQId(Integer talentQId) {
        this.talentQId = talentQId;
    }

    public Integer getTalentAUp() {
        return talentAUp;
    }

    public void setTalentAUp(Integer talentAUp) {
        this.talentAUp = talentAUp;
    }

    public Integer getTalentEUp() {
        return talentEUp;
    }

    public void setTalentEUp(Integer talentEUp) {
        this.talentEUp = talentEUp;
    }

    public Integer getTalentQUp() {
        return talentQUp;
    }

    public void setTalentQUp(Integer talentQUp) {
        this.talentQUp = talentQUp;
    }

    public Integer getTalentAUpCons() {
        return talentAUpCons;
    }

    public void setTalentAUpCons(Integer talentAUpCons) {
        this.talentAUpCons = talentAUpCons;
    }

    public Integer getTalentEUpCons() {
        return talentEUpCons;
    }

    public void setTalentEUpCons(Integer talentEUpCons) {
        this.talentEUpCons = talentEUpCons;
    }

    public Integer getTalentQUpCons() {
        return talentQUpCons;
    }

    public void setTalentQUpCons(Integer talentQUpCons) {
        this.talentQUpCons = talentQUpCons;
    }

    public String getIconArt() {
        return iconArt;
    }

    public void setIconArt(String iconArt) {
        this.iconArt = iconArt;
    }

    public String getIconAvatar() {
        return iconAvatar;
    }

    public void setIconAvatar(String iconAvatar) {
        this.iconAvatar = iconAvatar;
    }

    public String getIconCard() {
        return iconCard;
    }

    public void setIconCard(String iconCard) {
        this.iconCard = iconCard;
    }

    public String getIconSide() {
        return iconSide;
    }

    public void setIconSide(String iconSide) {
        this.iconSide = iconSide;
    }

    public String getIconTalentA() {
        return iconTalentA;
    }

    public void setIconTalentA(String iconTalentA) {
        this.iconTalentA = iconTalentA;
    }

    public String getIconTalentE() {
        return iconTalentE;
    }

    public void setIconTalentE(String iconTalentE) {
        this.iconTalentE = iconTalentE;
    }

    public String getIconTalentQ() {
        return iconTalentQ;
    }

    public void setIconTalentQ(String iconTalentQ) {
        this.iconTalentQ = iconTalentQ;
    }

    public String getIconCons1() {
        return iconCons1;
    }

    public void setIconCons1(String iconCons1) {
        this.iconCons1 = iconCons1;
    }

    public String getIconCons2() {
        return iconCons2;
    }

    public void setIconCons2(String iconCons2) {
        this.iconCons2 = iconCons2;
    }

    public String getIconCons3() {
        return iconCons3;
    }

    public void setIconCons3(String iconCons3) {
        this.iconCons3 = iconCons3;
    }

    public String getIconCons4() {
        return iconCons4;
    }

    public void setIconCons4(String iconCons4) {
        this.iconCons4 = iconCons4;
    }

    public String getIconCons5() {
        return iconCons5;
    }

    public void setIconCons5(String iconCons5) {
        this.iconCons5 = iconCons5;
    }

    public String getIconCons6() {
        return iconCons6;
    }

    public void setIconCons6(String iconCons6) {
        this.iconCons6 = iconCons6;
    }
}