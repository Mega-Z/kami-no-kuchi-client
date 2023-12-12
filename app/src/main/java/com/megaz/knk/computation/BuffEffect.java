package com.megaz.knk.computation;

import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.BuffRangeEnum;
import com.megaz.knk.constant.BuffSourceEnum;
import com.megaz.knk.constant.DamageLabelEnum;
import com.megaz.knk.constant.EffectBaseAttributeEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.ElementReactionEnum;
import com.megaz.knk.constant.FightEffectEnum;
import com.megaz.knk.constant.SourceTalentEnum;
import com.megaz.knk.entity.Buff;
import com.megaz.knk.exception.BuffParamNotFilledException;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuffEffect implements Serializable {
    private String buffId;
    private String buffName;
    private String description;
    private String sourceName;
    private BuffSourceEnum sourceType;
    private String sourceId;
    private BuffRangeEnum buffRange;
    private FightEffectEnum effectType;
    private Boolean percent;
    private SourceTalentEnum sourceTalent;
    private String specialInput;
    private Boolean forced;
    private Boolean fromSelf;
    private Boolean enabled;
    private Boolean defaultEnabled;
    // constant
    private Double multiplierConstant;
    private Double addendConstant;
    private Double maxValueConstant;
    private Double basedAttributeThreshold;
    // param
    private EffectBaseAttributeEnum maxValueBasedAttribute;
    private EffectBaseAttributeEnum basedAttribute;
    private EffectBaseAttributeEnum basedAttributeSecond;
    private String multiplierTalentCurve;
    private String multiplierRefinementCurve;
    private String maxValueRefinementCurve;
    // param values
    private Double maxValueBasedAttributeValue = null;
    private Double basedAttributeValue = null;
    private Double basedAttributeSecondValue = null;
    private Double multiplierTalentCurveValue = null;
    private Double multiplierRefinementCurveValue = null;
    private Double maxValueRefinementCurveValue = null;
    // conditions
    private ElementEnum element;
    private ElementReactionEnum elementReaction;
    private AttributeEnum increasedAttribute;
    private DamageLabelEnum increasedDamageLabel;
    private Integer phase;
    private Integer constellation;
    private Integer artifactNum;


    public BuffEffect(Buff buff) {
        buffId = buff.getBuffId();
        buffName = buff.getBuffName();
        description = buff.getDescription();
        sourceName = buff.getSourceName();
        sourceType = buff.getSourceType();
        sourceId = buff.getSourceId();
        buffRange = buff.getBuffRange();
        effectType = buff.getEffectType();
        increasedAttribute = buff.getIncreasedAttribute();
        percent = buff.getPercent();
        sourceTalent = buff.getSourceTalent();
        specialInput = buff.getSpecialInput();
        enabled = false;
        defaultEnabled = buff.getDefaultEnabled();
        multiplierConstant = buff.getMultiplierConstant();
        addendConstant = buff.getAddendConstant();
        maxValueConstant = buff.getMaxValue();
        basedAttributeThreshold = buff.getBasedAttributeThreshold();
        maxValueBasedAttribute = buff.getMaxValueBasedAttribute();
        basedAttribute = buff.getBasedAttribute();
        basedAttributeSecond = buff.getBasedAttributeSecond();
        multiplierTalentCurve = buff.getMultiplierTalentCurve();
        multiplierRefinementCurve = buff.getMultiplierRefinementCurve();
        maxValueRefinementCurve = buff.getMaxValueRefinementCurve();
        element = buff.getElement();
        elementReaction = buff.getElementReaction();
        increasedAttribute = buff.getIncreasedAttribute();
        increasedDamageLabel = buff.getIncreasedDamageLabel();
        phase = buff.getPhase();
        constellation = buff.getConstellation();
        artifactNum = buff.getArtifactNum();
    }

    public Double getValue() {
        checkParam();
        if (!enabled) {
            return 0.;
        }
        Double addend = addendConstant;
        Double multiplier = multiplierConstant;
        if (basedAttribute != null) {
            multiplier *= basedAttributeValue - basedAttributeThreshold;
        }
        if (basedAttributeSecond != null) {
            multiplier *= basedAttributeSecondValue;
        }
        if (multiplierTalentCurve != null) {
            multiplier *= multiplierTalentCurveValue;
        }
        if (multiplierRefinementCurve != null) {
            multiplier *= multiplierRefinementCurveValue;
        }

        Double maxValue = Double.MAX_VALUE;
        if (maxValueConstant != null) {
            maxValue = maxValueConstant;
        }
        if(maxValueBasedAttribute != null) {
            maxValue *= maxValueBasedAttributeValue;
        }
        if (maxValueRefinementCurve != null) {
            maxValue *= maxValueRefinementCurveValue;
        }

        return Math.min(maxValue, addend + multiplier);
    }

    public void enableBuff() {
        enabled = true;
    }

    public void disableBuff() {
        enabled = false;
        maxValueBasedAttributeValue = null;
        basedAttributeValue = null;
        basedAttributeSecondValue = null;
        multiplierTalentCurveValue = null;
        multiplierRefinementCurveValue = null;
        maxValueRefinementCurveValue = null;
    }

    public void fillAttributeParam(CharacterAttribute characterAttribute) {
        if(buffId.equals("BC10000052-1")) { //神变·恶曜开眼
            basedAttributeValue = 90.;
            return;
        }
        if (basedAttribute != null) {
            basedAttributeValue = characterAttribute.getEffectBasedAttribute(basedAttribute);
        }
        if (basedAttributeSecond != null) {
            basedAttributeSecondValue = characterAttribute.getEffectBasedAttribute(basedAttributeSecond);
        }
        if (maxValueBasedAttribute != null) {
            maxValueBasedAttributeValue = characterAttribute.getEffectBasedAttribute(maxValueBasedAttribute);
        }
    }


    public Set<AttributeEnum> getRelatedAttributeSet() {
        Set<AttributeEnum> attributeSet = new HashSet<>();
        if(basedAttribute != null) {
            attributeSet.addAll(basedAttribute.getRelatedAttributes());
        }
        if(basedAttributeSecond != null) {
            attributeSet.addAll(basedAttributeSecond.getRelatedAttributes());
        }
        if(maxValueBasedAttribute != null) {
            attributeSet.addAll(maxValueBasedAttribute.getRelatedAttributes());
        }
        return attributeSet;
    }

    public void checkParam() {
        if (basedAttribute != null && basedAttributeValue == null) {
            throw new BuffParamNotFilledException(specialInput == null ? basedAttribute.getDesc() : specialInput);
        }
        if (basedAttributeSecond != null && basedAttributeSecondValue == null) {
            throw new BuffParamNotFilledException(basedAttributeSecond.getDesc());
        }
        if (multiplierTalentCurve != null && multiplierTalentCurveValue == null) {
            throw new BuffParamNotFilledException(sourceTalent.getDesc() + "天赋等级");
        }
        if (multiplierRefinementCurve != null && multiplierRefinementCurveValue == null) {
            throw new BuffParamNotFilledException("精炼等级");
        }
        if (maxValueBasedAttribute != null && maxValueBasedAttributeValue == null) {
            throw new BuffParamNotFilledException(maxValueBasedAttribute.getDesc());
        }
        if(maxValueRefinementCurve != null && maxValueRefinementCurveValue == null) {
            throw new BuffParamNotFilledException("精炼等级");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuffEffect that = (BuffEffect) o;
        return buffId.equals(that.buffId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(buffId);
    }
}
