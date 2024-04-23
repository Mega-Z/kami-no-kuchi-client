package com.megaz.knk.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.megaz.knk.constant.AttributeEnum;

import java.util.Date;


@Entity(tableName = "character_profile")
public class CharacterProfile {
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "uid")
    private String uid;
    @ColumnInfo(name = "character_id")
    private String characterId;
    @ColumnInfo(name = "costume_id")
    private String costumeId;
    private Integer level;
    private Integer phase;
    private Integer fetter;
    @ColumnInfo(name = "attribute_base_hp")
    private Double attributeBaseHp;
    @ColumnInfo(name = "attribute_plus_hp")
    private Double attributePlusHp;
    @ColumnInfo(name = "attribute_base_atk")
    private Double attributeBaseAtk;
    @ColumnInfo(name = "attribute_plus_atk")
    private Double attributePlusAtk;
    @ColumnInfo(name = "attribute_base_def")
    private Double attributeBaseDef;
    @ColumnInfo(name = "attribute_plus_def")
    private Double attributePlusDef;
    @ColumnInfo(name = "attribute_mastery")
    private Double attributeMastery;
    @ColumnInfo(name = "attribute_crit_rate")
    private Double attributeCritRate;
    @ColumnInfo(name = "attribute_crit_dmg")
    private Double attributeCritDmg;
    @ColumnInfo(name = "attribute_recharge")
    private Double attributeRecharge;
    @ColumnInfo(name = "attribute_dmg_pyro")
    private Double attributeDmgPyro;
    @ColumnInfo(name = "attribute_dmg_electro")
    private Double attributeDmgElectro;
    @ColumnInfo(name = "attribute_dmg_hydro")
    private Double attributeDmgHydro;
    @ColumnInfo(name = "attribute_dmg_dendro")
    private Double attributeDmgDendro;
    @ColumnInfo(name = "attribute_dmg_anemo")
    private Double attributeDmgAnemo;
    @ColumnInfo(name = "attribute_dmg_geo")
    private Double attributeDmgGeo;
    @ColumnInfo(name = "attribute_dmg_cryo")
    private Double attributeDmgCryo;
    @ColumnInfo(name = "attribute_dmg_phy")
    private Double attributeDmgPhy;
    @ColumnInfo(name = "attribute_heal")
    private Double attributeHeal;
    @ColumnInfo(name = "talent_a_base")
    private Integer talentABase;
    @ColumnInfo(name = "talent_a_plus")
    private Integer talentAPlus;
    @ColumnInfo(name = "talent_e_base")
    private Integer talentEBase;
    @ColumnInfo(name = "talent_e_plus")
    private Integer talentEPlus;
    @ColumnInfo(name = "talent_q_base")
    private Integer talentQBase;
    @ColumnInfo(name = "talent_q_plus")
    private Integer talentQPlus;
    private Integer constellation;
    @ColumnInfo(name = "weapon_id")
    private String weaponId;
    @ColumnInfo(name = "weapon_level")
    private Integer weaponLevel;
    @ColumnInfo(name = "weapon_phase")
    private Integer weaponPhase;
    @ColumnInfo(name = "weapon_refine_rank")
    private Integer weaponRefineRank;
    @ColumnInfo(name = "weapon_base_atk")
    private Double weaponBaseAtk;
    @ColumnInfo(name = "weapon_attribute")
    private AttributeEnum weaponAttribute;
    @ColumnInfo(name = "weapon_attribute_value")
    private Double weaponAttributeValue;
    @ColumnInfo(name = "artifact_instance_flower_id")
    private String artifactInstanceFlowerId;
    @ColumnInfo(name = "artifact_instance_plume_id")
    private String artifactInstancePlumeId;
    @ColumnInfo(name = "artifact_instance_sands_id")
    private String artifactInstanceSandsId;
    @ColumnInfo(name = "artifact_instance_goblet_id")
    private String artifactInstanceGobletId;
    @ColumnInfo(name = "artifact_instance_circlet_id")
    private String artifactInstanceCircletId;
    @ColumnInfo(name = "update_time")
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public String getCostumeId() {
        return costumeId;
    }

    public void setCostumeId(String costumeId) {
        this.costumeId = costumeId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getPhase() {
        return phase;
    }

    public void setPhase(Integer phase) {
        this.phase = phase;
    }

    public Integer getFetter() {
        return fetter;
    }

    public void setFetter(Integer fetter) {
        this.fetter = fetter;
    }

    public Double getAttributeBaseHp() {
        return attributeBaseHp;
    }

    public void setAttributeBaseHp(Double attributeBaseHp) {
        this.attributeBaseHp = attributeBaseHp;
    }

    public Double getAttributePlusHp() {
        return attributePlusHp;
    }

    public void setAttributePlusHp(Double attributePlusHp) {
        this.attributePlusHp = attributePlusHp;
    }

    public Double getAttributeBaseAtk() {
        return attributeBaseAtk;
    }

    public void setAttributeBaseAtk(Double attributeBaseAtk) {
        this.attributeBaseAtk = attributeBaseAtk;
    }

    public Double getAttributePlusAtk() {
        return attributePlusAtk;
    }

    public void setAttributePlusAtk(Double attributePlusAtk) {
        this.attributePlusAtk = attributePlusAtk;
    }

    public Double getAttributeBaseDef() {
        return attributeBaseDef;
    }

    public void setAttributeBaseDef(Double attributeBaseDef) {
        this.attributeBaseDef = attributeBaseDef;
    }

    public Double getAttributePlusDef() {
        return attributePlusDef;
    }

    public void setAttributePlusDef(Double attributePlusDef) {
        this.attributePlusDef = attributePlusDef;
    }

    public Double getAttributeMastery() {
        return attributeMastery;
    }

    public void setAttributeMastery(Double attributeMastery) {
        this.attributeMastery = attributeMastery;
    }

    public Double getAttributeCritRate() {
        return attributeCritRate;
    }

    public void setAttributeCritRate(Double attributeCritRate) {
        this.attributeCritRate = attributeCritRate;
    }

    public Double getAttributeCritDmg() {
        return attributeCritDmg;
    }

    public void setAttributeCritDmg(Double attributeCritDmg) {
        this.attributeCritDmg = attributeCritDmg;
    }

    public Double getAttributeRecharge() {
        return attributeRecharge;
    }

    public void setAttributeRecharge(Double attributeRecharge) {
        this.attributeRecharge = attributeRecharge;
    }

    public Double getAttributeDmgPyro() {
        return attributeDmgPyro;
    }

    public void setAttributeDmgPyro(Double attributeDmgPyro) {
        this.attributeDmgPyro = attributeDmgPyro;
    }

    public Double getAttributeDmgElectro() {
        return attributeDmgElectro;
    }

    public void setAttributeDmgElectro(Double attributeDmgElectro) {
        this.attributeDmgElectro = attributeDmgElectro;
    }

    public Double getAttributeDmgHydro() {
        return attributeDmgHydro;
    }

    public void setAttributeDmgHydro(Double attributeDmgHydro) {
        this.attributeDmgHydro = attributeDmgHydro;
    }

    public Double getAttributeDmgDendro() {
        return attributeDmgDendro;
    }

    public void setAttributeDmgDendro(Double attributeDmgDendro) {
        this.attributeDmgDendro = attributeDmgDendro;
    }

    public Double getAttributeDmgAnemo() {
        return attributeDmgAnemo;
    }

    public void setAttributeDmgAnemo(Double attributeDmgAnemo) {
        this.attributeDmgAnemo = attributeDmgAnemo;
    }

    public Double getAttributeDmgGeo() {
        return attributeDmgGeo;
    }

    public void setAttributeDmgGeo(Double attributeDmgGeo) {
        this.attributeDmgGeo = attributeDmgGeo;
    }

    public Double getAttributeDmgCryo() {
        return attributeDmgCryo;
    }

    public void setAttributeDmgCryo(Double attributeDmgCryo) {
        this.attributeDmgCryo = attributeDmgCryo;
    }

    public Double getAttributeDmgPhy() {
        return attributeDmgPhy;
    }

    public void setAttributeDmgPhy(Double attributeDmgPhy) {
        this.attributeDmgPhy = attributeDmgPhy;
    }

    public Double getAttributeHeal() {
        return attributeHeal;
    }

    public void setAttributeHeal(Double attributeHeal) {
        this.attributeHeal = attributeHeal;
    }

    public Integer getTalentABase() {
        return talentABase;
    }

    public void setTalentABase(Integer talentABase) {
        this.talentABase = talentABase;
    }

    public Integer getTalentAPlus() {
        return talentAPlus;
    }

    public void setTalentAPlus(Integer talentAPlus) {
        this.talentAPlus = talentAPlus;
    }

    public Integer getTalentEBase() {
        return talentEBase;
    }

    public void setTalentEBase(Integer talentEBase) {
        this.talentEBase = talentEBase;
    }

    public Integer getTalentEPlus() {
        return talentEPlus;
    }

    public void setTalentEPlus(Integer talentEPlus) {
        this.talentEPlus = talentEPlus;
    }

    public Integer getTalentQBase() {
        return talentQBase;
    }

    public void setTalentQBase(Integer talentQBase) {
        this.talentQBase = talentQBase;
    }

    public Integer getTalentQPlus() {
        return talentQPlus;
    }

    public void setTalentQPlus(Integer talentQPlus) {
        this.talentQPlus = talentQPlus;
    }

    public Integer getConstellation() {
        return constellation;
    }

    public void setConstellation(Integer constellation) {
        this.constellation = constellation;
    }

    public String getWeaponId() {
        return weaponId;
    }

    public void setWeaponId(String weaponId) {
        this.weaponId = weaponId;
    }

    public Integer getWeaponLevel() {
        return weaponLevel;
    }

    public void setWeaponLevel(Integer weaponLevel) {
        this.weaponLevel = weaponLevel;
    }

    public Integer getWeaponPhase() {
        return weaponPhase;
    }

    public void setWeaponPhase(Integer weaponPhase) {
        this.weaponPhase = weaponPhase;
    }

    public Integer getWeaponRefineRank() {
        return weaponRefineRank;
    }

    public void setWeaponRefineRank(Integer weaponRefineRank) {
        this.weaponRefineRank = weaponRefineRank;
    }

    public Double getWeaponBaseAtk() {
        return weaponBaseAtk;
    }

    public void setWeaponBaseAtk(Double weaponBaseAtk) {
        this.weaponBaseAtk = weaponBaseAtk;
    }

    public AttributeEnum getWeaponAttribute() {
        return weaponAttribute;
    }

    public void setWeaponAttribute(AttributeEnum weaponAttribute) {
        this.weaponAttribute = weaponAttribute;
    }

    public Double getWeaponAttributeValue() {
        return weaponAttributeValue;
    }

    public void setWeaponAttributeValue(Double weaponAttributeValue) {
        this.weaponAttributeValue = weaponAttributeValue;
    }

    public String getArtifactInstanceFlowerId() {
        return artifactInstanceFlowerId;
    }

    public void setArtifactInstanceFlowerId(String artifactInstanceFlowerId) {
        this.artifactInstanceFlowerId = artifactInstanceFlowerId;
    }

    public String getArtifactInstancePlumeId() {
        return artifactInstancePlumeId;
    }

    public void setArtifactInstancePlumeId(String artifactInstancePlumeId) {
        this.artifactInstancePlumeId = artifactInstancePlumeId;
    }

    public String getArtifactInstanceSandsId() {
        return artifactInstanceSandsId;
    }

    public void setArtifactInstanceSandsId(String artifactInstanceSandsId) {
        this.artifactInstanceSandsId = artifactInstanceSandsId;
    }

    public String getArtifactInstanceGobletId() {
        return artifactInstanceGobletId;
    }

    public void setArtifactInstanceGobletId(String artifactInstanceGobletId) {
        this.artifactInstanceGobletId = artifactInstanceGobletId;
    }

    public String getArtifactInstanceCircletId() {
        return artifactInstanceCircletId;
    }

    public void setArtifactInstanceCircletId(String artifactInstanceCircletId) {
        this.artifactInstanceCircletId = artifactInstanceCircletId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
