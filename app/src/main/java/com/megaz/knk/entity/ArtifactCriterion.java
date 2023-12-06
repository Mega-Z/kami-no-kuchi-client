package com.megaz.knk.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "artifact_criterion")
public class ArtifactCriterion extends MetaDataEntity {
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "criterion_id")
    private String criterionId;
    @ColumnInfo(name = "criterion_name")
    private String criterionName;
    @ColumnInfo(name = "character_id")
    private String characterId;
    @ColumnInfo(name = "hp_weight")
    private Integer hpWeight;
    @ColumnInfo(name = "atk_weight")
    private Integer atkWeight;
    @ColumnInfo(name = "def_weight")
    private Integer defWeight;
    @ColumnInfo(name = "cr_weight")
    private Integer crWeight;
    @ColumnInfo(name = "cd_weight")
    private Integer cdWeight;
    @ColumnInfo(name = "mastery_weight")
    private Integer masteryWeight;
    @ColumnInfo(name = "recharge_weight")
    private Integer rechargeWeight;
    @ColumnInfo(name = "dmg_weight")
    private Integer dmgWeight;
    @ColumnInfo(name = "phy_weight")
    private Integer phyWeight;
    @ColumnInfo(name = "heal_weight")
    private Integer healWeight;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCriterionId() {
        return criterionId;
    }

    public void setCriterionId(String criterionId) {
        this.criterionId = criterionId;
    }

    public String getCriterionName() {
        return criterionName;
    }

    public void setCriterionName(String criterionName) {
        this.criterionName = criterionName;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public Integer getHpWeight() {
        return hpWeight;
    }

    public void setHpWeight(Integer hpWeight) {
        this.hpWeight = hpWeight;
    }

    public Integer getAtkWeight() {
        return atkWeight;
    }

    public void setAtkWeight(Integer atkWeight) {
        this.atkWeight = atkWeight;
    }

    public Integer getDefWeight() {
        return defWeight;
    }

    public void setDefWeight(Integer defWeight) {
        this.defWeight = defWeight;
    }

    public Integer getCrWeight() {
        return crWeight;
    }

    public void setCrWeight(Integer crWeight) {
        this.crWeight = crWeight;
    }

    public Integer getCdWeight() {
        return cdWeight;
    }

    public void setCdWeight(Integer cdWeight) {
        this.cdWeight = cdWeight;
    }

    public Integer getMasteryWeight() {
        return masteryWeight;
    }

    public void setMasteryWeight(Integer masteryWeight) {
        this.masteryWeight = masteryWeight;
    }

    public Integer getRechargeWeight() {
        return rechargeWeight;
    }

    public void setRechargeWeight(Integer rechargeWeight) {
        this.rechargeWeight = rechargeWeight;
    }

    public Integer getDmgWeight() {
        return dmgWeight;
    }

    public void setDmgWeight(Integer dmgWeight) {
        this.dmgWeight = dmgWeight;
    }

    public Integer getPhyWeight() {
        return phyWeight;
    }

    public void setPhyWeight(Integer phyWeight) {
        this.phyWeight = phyWeight;
    }

    public Integer getHealWeight() {
        return healWeight;
    }

    public void setHealWeight(Integer healWeight) {
        this.healWeight = healWeight;
    }
}
