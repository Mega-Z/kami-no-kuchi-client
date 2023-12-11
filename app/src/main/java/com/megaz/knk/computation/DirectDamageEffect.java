package com.megaz.knk.computation;

import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.DamageLabelEnum;
import com.megaz.knk.constant.EffectFieldEnum;
import com.megaz.knk.constant.FightEffectEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.entity.Buff;
import com.megaz.knk.entity.FightEffectComputation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DirectDamageEffect extends DamageEffect {
    private DamageLabelEnum label;
    private Set<BuffEffect> damageUpFieldBuffEffects;
    private Set<BuffEffect> criticalFieldBuffEffects;
    private Set<BuffEffect> defenceFieldBuffEffects;
    private Integer targetLevel = 91;

    public DirectDamageEffect(FightEffectComputation fightEffectComputation, CharacterAttribute characterAttribute) {
        super(fightEffectComputation, characterAttribute);
        this.label = fightEffectComputation.getDamageLabel();
        damageUpFieldBuffEffects = new HashSet<>();
        criticalFieldBuffEffects = new HashSet<>();
        defenceFieldBuffEffects = new HashSet<>();
    }

    @Override
    public void setTargetAttribute(Double baseResist) {
        //TODO set enemy level
        super.setTargetAttribute(baseResist);
    }

    @Override
    public Double getValue() {
        assert characterAttributeWithBuffs != null;
        Double superValue = super.getValue();
        return superValue * (1+getDamageUpFieldValue()) * getDefenceFieldValue();
    }

    @Override
    public Map<EffectFieldEnum, Double> getFieldDetail() {
        assert characterAttributeWithBuffs != null;
        Map<EffectFieldEnum, Double> fieldDetail = super.getFieldDetail();
        fieldDetail.put(EffectFieldEnum.DAMAGE_UP, getDamageUpFieldValue());
        if(!defenceFieldBuffEffects.isEmpty()) {
            fieldDetail.put(EffectFieldEnum.DEFENCE, getDefenceFieldValue());
        }
        fieldDetail.put(EffectFieldEnum.CRIT_RATE, getCritRate());
        fieldDetail.put(EffectFieldEnum.CRIT_DMG, getCritDmg());
        return fieldDetail;
    }

    public Double getCriticalValue() {
        Double baseValue = getValue();
        return baseValue * (1 + getCritDmg());
    }

    public Double getAverageValue() {
        Double baseValue = getValue();
        return baseValue * (1 + getCritDmg() * getCritRate());
    }

    @Override
    public BuffQueryCondition getBuffQueryCondition() {
        BuffQueryCondition buffQueryCondition = super.getBuffQueryCondition();
        buffQueryCondition.addBuffTypes(Arrays.asList(
                FightEffectEnum.DEF_DOWN, FightEffectEnum.DEF_IGNORE, FightEffectEnum.DAMAGE_UP));
        buffQueryCondition.setDamageLabel(label);
        buffQueryCondition.addAddedAttributes(Arrays.asList(AttributeEnum.CRIT_RATE, AttributeEnum.CRIT_DMG,
                GenshinConstantMeta.ELEMENT_DAMAGE_UP_ATTRIBUTE_MAP.get(element)));
        return buffQueryCondition;
    }

    @Override
    public List<BuffEffect> getEnabledBuffEffects() {
        List<BuffEffect> buffEffectList = super.getEnabledBuffEffects();
        buffEffectList.addAll(damageUpFieldBuffEffects);
        buffEffectList.addAll(criticalFieldBuffEffects);
        buffEffectList.addAll(defenceFieldBuffEffects);
        return buffEffectList;
    }

    @Override
    public void enableBuffEffect(BuffEffect buffEffect) {
        assert availableBuffEffects.containsKey(buffEffect.getBuffId());
        buffEffect.enableBuff();
        characterAttributeWithBuffs = null;
        if (buffEffect.getEffectType() == FightEffectEnum.DAMAGE_UP && buffEffect.getIncreasedAttribute() == null &&
                (buffEffect.getIncreasedDamageLabel() == label || buffEffect.getIncreasedDamageLabel() == null)) {
            damageUpFieldBuffEffects.add(buffEffect);
        } else if (buffEffect.getEffectType() == FightEffectEnum.DAMAGE_UP &&
                (buffEffect.getIncreasedAttribute() == AttributeEnum.CRIT_RATE || buffEffect.getIncreasedAttribute() == AttributeEnum.CRIT_DMG)) {
            criticalFieldBuffEffects.add(buffEffect);
        } else if (buffEffect.getEffectType() == FightEffectEnum.DEF_DOWN || buffEffect.getEffectType() == FightEffectEnum.DEF_IGNORE) {
            defenceFieldBuffEffects.add(buffEffect);
        } else {
            super.enableBuffEffect(buffEffect);
        }
    }

    @Override
    public void disableBuffEffect(BuffEffect buffEffect) {
        assert availableBuffEffects.containsKey(buffEffect.getBuffId());
        buffEffect.disableBuff();
        characterAttributeWithBuffs = null;
        if (buffEffect.getEffectType() == FightEffectEnum.DAMAGE_UP && buffEffect.getIncreasedAttribute() == null &&
                (buffEffect.getIncreasedDamageLabel() == label || buffEffect.getIncreasedDamageLabel() == null)) {
            damageUpFieldBuffEffects.remove(buffEffect);
        } else if (buffEffect.getEffectType() == FightEffectEnum.DAMAGE_UP &&
                (buffEffect.getIncreasedAttribute() == AttributeEnum.CRIT_RATE || buffEffect.getIncreasedAttribute() == AttributeEnum.CRIT_DMG)) {
            criticalFieldBuffEffects.remove(buffEffect);
        } else if (buffEffect.getEffectType() == FightEffectEnum.DEF_DOWN || buffEffect.getEffectType() == FightEffectEnum.DEF_IGNORE) {
            defenceFieldBuffEffects.remove(buffEffect);
        } else {
            super.disableBuffEffect(buffEffect);
        }
    }

    @Override
    protected Double getMasteryFieldValue() {
        return 0.;
    }

    private Double getDamageUpFieldValue() {
        Double damageUpFieldValue = Objects.requireNonNull(characterAttributeWithBuffs.getDmgUp().get(element));
        for (BuffEffect buffEffect : damageUpFieldBuffEffects) {
            damageUpFieldValue += buffEffect.getValue();
        }
        return damageUpFieldValue;
    }

    private Double getDefenceFieldValue() {
        Double defenceDown = 0.;
        Double defenceIgnore = 0.;
        for (BuffEffect buffEffect : defenceFieldBuffEffects) {
            if (buffEffect.getEffectType() == FightEffectEnum.DEF_DOWN) {
                defenceDown += buffEffect.getValue();
            } else if (buffEffect.getEffectType() == FightEffectEnum.DEF_IGNORE) {
                defenceIgnore += buffEffect.getValue();
            }
        }
        Double defenceFieldValue = (double) (characterAttributeWithBuffs.getLevel() + 100) /
                (characterAttributeWithBuffs.getLevel() + 100 +
                        (targetLevel + 100) * (1 - defenceDown) * (1 - defenceIgnore));
        return defenceFieldValue;
    }

    private Double getCritRate() {
        Double critRate = characterAttributeWithBuffs.getCritRate();
        for (BuffEffect buffEffect : criticalFieldBuffEffects) {
            if (buffEffect.getIncreasedAttribute() == AttributeEnum.CRIT_RATE) {
                critRate += buffEffect.getValue();
            }
        }
        critRate = Math.max(0, Math.min(1, critRate));
        return critRate;
    }

    private Double getCritDmg() {
        Double critDmg = characterAttributeWithBuffs.getCritDmg();
        for (BuffEffect buffEffect : criticalFieldBuffEffects) {
            if (buffEffect.getIncreasedAttribute() == AttributeEnum.CRIT_DMG) {
                critDmg += buffEffect.getValue();
            }
        }
        return critDmg;
    }
}
