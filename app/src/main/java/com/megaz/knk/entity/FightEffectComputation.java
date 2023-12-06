package com.megaz.knk.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.megaz.knk.constant.DamageLabelEnum;
import com.megaz.knk.constant.EffectBaseAttributeEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.ElementReactionEnum;
import com.megaz.knk.constant.FightEffectEnum;
import com.megaz.knk.constant.SourceTalentEnum;

@Entity(tableName = "fight_effect_computation")
public class FightEffectComputation extends MetaDataEntity {
    @PrimaryKey()
    private Integer id;
    @ColumnInfo(name = "effect_id")
    private String effectId;
    @ColumnInfo(name = "character_id")
    private String characterId;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "phase")
    private Integer phase;
    @ColumnInfo(name = "constellation")
    private Integer constellation;
    @ColumnInfo(name = "effect_type")
    private FightEffectEnum effectType;
    @ColumnInfo(name = "percent")
    private Boolean percent;
    @ColumnInfo(name = "max_value")
    private Double maxValue;
    @ColumnInfo(name = "based_attribute")
    private EffectBaseAttributeEnum basedAttribute;
    @ColumnInfo(name = "multiplier_constant")
    private Double multiplierConstant;
    @ColumnInfo(name = "source_talent")
    private SourceTalentEnum sourceTalent;
    @ColumnInfo(name = "multiplier_talent_curve")
    private String multiplierTalentCurve;
    @ColumnInfo(name = "element")
    private ElementEnum element;
    @ColumnInfo(name = "element_reaction")
    private ElementReactionEnum elementReaction;
    @ColumnInfo(name = "damage_label")
    private DamageLabelEnum damageLabel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEffectId() {
        return effectId;
    }

    public void setEffectId(String effectId) {
        this.effectId = effectId;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPhase() {
        return phase;
    }

    public void setPhase(Integer phase) {
        this.phase = phase;
    }

    public Integer getConstellation() {
        return constellation;
    }

    public void setConstellation(Integer constellation) {
        this.constellation = constellation;
    }

    public FightEffectEnum getEffectType() {
        return effectType;
    }

    public void setEffectType(FightEffectEnum effectType) {
        this.effectType = effectType;
    }

    public Boolean getPercent() {
        return percent;
    }

    public void setPercent(Boolean percent) {
        this.percent = percent;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public EffectBaseAttributeEnum getBasedAttribute() {
        return basedAttribute;
    }

    public void setBasedAttribute(EffectBaseAttributeEnum basedAttribute) {
        this.basedAttribute = basedAttribute;
    }

    public Double getMultiplierConstant() {
        return multiplierConstant;
    }

    public void setMultiplierConstant(Double multiplierConstant) {
        this.multiplierConstant = multiplierConstant;
    }

    public SourceTalentEnum getSourceTalent() {
        return sourceTalent;
    }

    public void setSourceTalent(SourceTalentEnum sourceTalent) {
        this.sourceTalent = sourceTalent;
    }

    public String getMultiplierTalentCurve() {
        return multiplierTalentCurve;
    }

    public void setMultiplierTalentCurve(String multiplierTalentCurve) {
        this.multiplierTalentCurve = multiplierTalentCurve;
    }

    public ElementEnum getElement() {
        return element;
    }

    public void setElement(ElementEnum element) {
        this.element = element;
    }

    public ElementReactionEnum getElementReaction() {
        return elementReaction;
    }

    public void setElementReaction(ElementReactionEnum elementReaction) {
        this.elementReaction = elementReaction;
    }

    public DamageLabelEnum getDamageLabel() {
        return damageLabel;
    }

    public void setDamageLabel(DamageLabelEnum damageLabel) {
        this.damageLabel = damageLabel;
    }
}
