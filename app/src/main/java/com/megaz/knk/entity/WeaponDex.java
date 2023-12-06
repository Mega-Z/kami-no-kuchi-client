package com.megaz.knk.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.WeaponTypeEnum;

@Entity(tableName = "weapon_dex")
public class WeaponDex extends MetaDataEntity {
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "weapon_id")
    private String weaponId;
    @ColumnInfo(name = "weapon_name")
    private String weaponName;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "type")
    private WeaponTypeEnum type;
    @ColumnInfo(name = "star")
    private Integer star;
    @ColumnInfo(name = "base_atk")
    private Double baseAtk;
    @ColumnInfo(name = "attribute")
    private AttributeEnum attribute;
    @ColumnInfo(name = "attribute_value")
    private Double attributeValue;
    @ColumnInfo(name = "curve_base_atk")
    private String curveBaseAtk;
    @ColumnInfo(name = "curve_attribute")
    private String curveAttribute;
    @ColumnInfo(name = "promote_id")
    private String promoteId;
    @ColumnInfo(name = "icon_initial")
    private String iconInitial;
    @ColumnInfo(name = "icon_awaken")
    private String iconAwaken;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWeaponId() {
        return weaponId;
    }

    public void setWeaponId(String weaponId) {
        this.weaponId = weaponId;
    }

    public String getWeaponName() {
        return weaponName;
    }

    public void setWeaponName(String weaponName) {
        this.weaponName = weaponName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WeaponTypeEnum getType() {
        return type;
    }

    public void setType(WeaponTypeEnum type) {
        this.type = type;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public Double getBaseAtk() {
        return baseAtk;
    }

    public void setBaseAtk(Double baseAtk) {
        this.baseAtk = baseAtk;
    }

    public AttributeEnum getAttribute() {
        return attribute;
    }

    public void setAttribute(AttributeEnum attribute) {
        this.attribute = attribute;
    }

    public Double getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(Double attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getCurveBaseAtk() {
        return curveBaseAtk;
    }

    public void setCurveBaseAtk(String curveBaseAtk) {
        this.curveBaseAtk = curveBaseAtk;
    }

    public String getCurveAttribute() {
        return curveAttribute;
    }

    public void setCurveAttribute(String curveAttribute) {
        this.curveAttribute = curveAttribute;
    }

    public String getPromoteId() {
        return promoteId;
    }

    public void setPromoteId(String promoteId) {
        this.promoteId = promoteId;
    }

    public String getIconInitial() {
        return iconInitial;
    }

    public void setIconInitial(String iconInitial) {
        this.iconInitial = iconInitial;
    }

    public String getIconAwaken() {
        return iconAwaken;
    }

    public void setIconAwaken(String iconAwaken) {
        this.iconAwaken = iconAwaken;
    }
}