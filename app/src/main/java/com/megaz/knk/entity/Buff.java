package com.megaz.knk.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.BuffRangeEnum;
import com.megaz.knk.constant.BuffSourceEnum;
import com.megaz.knk.constant.DamageLabelEnum;
import com.megaz.knk.constant.EffectBaseAttributeEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.ElementReactionEnum;
import com.megaz.knk.constant.FightEffectEnum;
import com.megaz.knk.constant.SourceTalentEnum;
import com.megaz.knk.dao.MetaDataDao;

@Entity(tableName = "buff")
public class Buff extends MetaDataEntity {
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "buff_id")
    private String buffId;
    @ColumnInfo(name = "buff_name")
    private String buffName;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "source_name")
    private String sourceName;
    @ColumnInfo(name = "source_type")
    private BuffSourceEnum sourceType;
    @ColumnInfo(name = "source_id")
    private String sourceId;
    @ColumnInfo(name = "effect_type")
    private FightEffectEnum effectType;
    @ColumnInfo(name = "buff_range")
    private BuffRangeEnum buffRange;
    @ColumnInfo(name = "percent")
    private Boolean percent;
    @ColumnInfo(name = "max_value")
    private Double maxValue;
    @ColumnInfo(name = "max_value_based_attribute")
    private EffectBaseAttributeEnum maxValueBasedAttribute;
    @ColumnInfo(name = "default_enable")
    private Boolean defaultEnabled;
    @ColumnInfo(name = "based_attribute")
    private EffectBaseAttributeEnum basedAttribute;
    @ColumnInfo(name = "basedAttributeThreshold")
    private Double basedAttributeThreshold;
    @ColumnInfo(name = "basedAttributeSecond")
    private EffectBaseAttributeEnum basedAttributeSecond;
    @ColumnInfo(name = "special_input")
    private String specialInput;
    @ColumnInfo(name = "multiplier_constant")
    private Double multiplierConstant;
    @ColumnInfo(name = "addend_constant")
    private Double addendConstant;
    @ColumnInfo(name = "source_talent")
    private SourceTalentEnum sourceTalent;
    @ColumnInfo(name = "multiplier_talent_curve")
    private String multiplierTalentCurve;
    @ColumnInfo(name = "multiplier_refinement_curve")
    private String multiplierRefinementCurve;
    @ColumnInfo(name = "max_value_refinement_curve")
    private String maxValueRefinementCurve;
    @ColumnInfo(name = "element")
    private ElementEnum element;
    @ColumnInfo(name = "element_reaction")
    private ElementReactionEnum elementReaction;
    @ColumnInfo(name = "increased_attribute")
    private AttributeEnum increasedAttribute;
    @ColumnInfo(name = "increased_damage_label")
    private DamageLabelEnum increasedDamageLabel;
    @ColumnInfo(name = "phase")
    private Integer phase;
    @ColumnInfo(name = "constellation")
    private Integer constellation;
    @ColumnInfo(name = "artifact_num")
    private Integer artifactNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBuffId() {
        return buffId;
    }

    public void setBuffId(String buffId) {
        this.buffId = buffId;
    }

    public String getBuffName() {
        return buffName;
    }

    public void setBuffName(String buffName) {
        this.buffName = buffName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public BuffSourceEnum getSourceType() {
        return sourceType;
    }

    public void setSourceType(BuffSourceEnum sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public FightEffectEnum getEffectType() {
        return effectType;
    }

    public void setEffectType(FightEffectEnum effectType) {
        this.effectType = effectType;
    }

    public BuffRangeEnum getBuffRange() {
        return buffRange;
    }

    public void setBuffRange(BuffRangeEnum buffRange) {
        this.buffRange = buffRange;
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

    public EffectBaseAttributeEnum getMaxValueBasedAttribute() {
        return maxValueBasedAttribute;
    }

    public void setMaxValueBasedAttribute(EffectBaseAttributeEnum maxValueBasedAttribute) {
        this.maxValueBasedAttribute = maxValueBasedAttribute;
    }

    public Boolean getDefaultEnabled() {
        return defaultEnabled;
    }

    public void setDefaultEnabled(Boolean defaultEnabled) {
        this.defaultEnabled = defaultEnabled;
    }

    public EffectBaseAttributeEnum getBasedAttribute() {
        return basedAttribute;
    }

    public void setBasedAttribute(EffectBaseAttributeEnum basedAttribute) {
        this.basedAttribute = basedAttribute;
    }

    public Double getBasedAttributeThreshold() {
        return basedAttributeThreshold;
    }

    public void setBasedAttributeThreshold(Double basedAttributeThreshold) {
        this.basedAttributeThreshold = basedAttributeThreshold;
    }

    public EffectBaseAttributeEnum getBasedAttributeSecond() {
        return basedAttributeSecond;
    }

    public void setBasedAttributeSecond(EffectBaseAttributeEnum basedAttributeSecond) {
        this.basedAttributeSecond = basedAttributeSecond;
    }

    public String getSpecialInput() {
        return specialInput;
    }

    public void setSpecialInput(String specialInput) {
        this.specialInput = specialInput;
    }

    public Double getMultiplierConstant() {
        return multiplierConstant;
    }

    public void setMultiplierConstant(Double multiplierConstant) {
        this.multiplierConstant = multiplierConstant;
    }

    public Double getAddendConstant() {
        return addendConstant;
    }

    public void setAddendConstant(Double addendConstant) {
        this.addendConstant = addendConstant;
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

    public String getMultiplierRefinementCurve() {
        return multiplierRefinementCurve;
    }

    public void setMultiplierRefinementCurve(String multiplierRefinementCurve) {
        this.multiplierRefinementCurve = multiplierRefinementCurve;
    }

    public String getMaxValueRefinementCurve() {
        return maxValueRefinementCurve;
    }

    public void setMaxValueRefinementCurve(String maxValueRefinementCurve) {
        this.maxValueRefinementCurve = maxValueRefinementCurve;
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

    public AttributeEnum getIncreasedAttribute() {
        return increasedAttribute;
    }

    public void setIncreasedAttribute(AttributeEnum increasedAttribute) {
        this.increasedAttribute = increasedAttribute;
    }

    public DamageLabelEnum getIncreasedDamageLabel() {
        return increasedDamageLabel;
    }

    public void setIncreasedDamageLabel(DamageLabelEnum increasedDamageLabel) {
        this.increasedDamageLabel = increasedDamageLabel;
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

    public Integer getArtifactNum() {
        return artifactNum;
    }

    public void setArtifactNum(Integer artifactNum) {
        this.artifactNum = artifactNum;
    }
}
