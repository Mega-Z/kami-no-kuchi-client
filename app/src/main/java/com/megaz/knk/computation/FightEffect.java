package com.megaz.knk.computation;

import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.EffectBaseAttributeEnum;
import com.megaz.knk.constant.EffectFieldEnum;
import com.megaz.knk.constant.FightEffectEnum;
import com.megaz.knk.entity.FightEffectComputation;
import com.megaz.knk.exception.BuffNoFieldMatchedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class FightEffect extends FightStatus {
    protected String effectId;
    protected String effectDesc;
    protected Boolean percent;
    protected Map<String, BuffEffect> availableBuffEffects;
    private final List<EffectBaseAttributeEnum> baseFieldAttributeList;
    private final List<Double> baseFieldMultiplierList;
    private final Set<AttributeEnum> relatedAttributeSet;
    private final Set<BuffEffect> baseFieldAddendBuffEffects;
    private final Set<BuffEffect> baseFieldMultiplierBuffEffects;

    public FightEffect(FightEffectComputation fightEffectComputation, CharacterAttribute characterAttribute) {
        super(characterAttribute);
        this.effectId = fightEffectComputation.getEffectId();
        this.effectDesc = fightEffectComputation.getDescription();
        this.percent = fightEffectComputation.getPercent();
        this.characterAttributeBase = characterAttribute;
        baseFieldAttributeList = new ArrayList<>();
        baseFieldMultiplierList = new ArrayList<>();
        relatedAttributeSet = new HashSet<>();
        availableBuffEffects = new HashMap<>();
        baseFieldAddendBuffEffects = new HashSet<>();
        baseFieldMultiplierBuffEffects = new HashSet<>();
    }

    public void addBaseFieldMultiplierItem(EffectBaseAttributeEnum attribute, Double multiplier) {
        baseFieldAttributeList.add(attribute);
        baseFieldMultiplierList.add(multiplier);
        if (attribute != null)
            relatedAttributeSet.addAll(attribute.getRelatedAttributes());
    }

    public Double getValue() {
        assert characterAttributeWithBuffs != null;
        Double baseValue = getBaseFieldBaseValue();
        baseValue *= getBaseFieldMultiplier();
        baseValue += getBaseFieldAddend();
        return baseValue;
    }

    public Map<EffectFieldEnum, Double> getFieldDetail() {
        assert characterAttributeWithBuffs != null;
        Map<EffectFieldEnum, Double> fieldDetail = new HashMap<>();
        if (!baseFieldAttributeList.isEmpty()) {
            fieldDetail.put(EffectFieldEnum.BASE, getBaseFieldBaseValue());
        }
        if (!baseFieldMultiplierBuffEffects.isEmpty()) {
            fieldDetail.put(EffectFieldEnum.BASE_MULTIPLE, getBaseFieldMultiplier());
        }
        if (!baseFieldAddendBuffEffects.isEmpty()) {
            fieldDetail.put(EffectFieldEnum.BASE_ADD, getBaseFieldAddend());
        }
        return fieldDetail;
    }

    public BuffQueryCondition getBuffQueryCondition() {
        BuffQueryCondition buffQueryCondition = new BuffQueryCondition();
        buffQueryCondition.addBuffTypes(Arrays.asList(
                FightEffectEnum.ATTRIBUTE_ADD, FightEffectEnum.VALUE_ADD, FightEffectEnum.MULTIPLIER));
        buffQueryCondition.addAddedAttributes(new ArrayList<>(relatedAttributeSet));
        return buffQueryCondition;
    }

    public final void addAvailableBuffEffects(List<BuffEffect> buffEffectList) {
        for (BuffEffect buffEffect : buffEffectList) {
            if (!availableBuffEffects.containsKey(buffEffect.getBuffId())) {
                availableBuffEffects.put(buffEffect.getBuffId(), buffEffect);
            }
        }
    }

    public List<BuffEffect> getEnabledBuffEffects() {
        List<BuffEffect> buffEffectList = super.getEnabledBuffEffects()
                .stream().filter(b -> getBuffQueryCondition().getAddedAttributes().contains(b.getIncreasedAttribute()))
                .collect(Collectors.toList());
        // List<BuffEffect> buffEffectList = super.getEnabledBuffEffects();
        buffEffectList.addAll(baseFieldAddendBuffEffects);
        buffEffectList.addAll(baseFieldMultiplierBuffEffects);
        return buffEffectList;
    }

    public void setEnemyAttribute(EnemyAttribute enemyAttribute) {

    }

    public void enableBuffEffect(BuffEffect buffEffect) {
        prepareToEnableBuff(buffEffect);
        if (buffEffect.getEffectType() == FightEffectEnum.MULTIPLIER) {
            baseFieldMultiplierBuffEffects.add(buffEffect);
        } else if (buffEffect.getEffectType() == FightEffectEnum.VALUE_ADD) {
            baseFieldAddendBuffEffects.add(buffEffect);
        } else {
            super.enableBuffEffect(buffEffect);
        }
    }

    public void disableBuffEffect(BuffEffect buffEffect) {
        if (buffEffect.getEffectType() == FightEffectEnum.MULTIPLIER) {
            baseFieldMultiplierBuffEffects.remove(buffEffect);
        } else if (buffEffect.getEffectType() == FightEffectEnum.VALUE_ADD) {
            baseFieldAddendBuffEffects.remove(buffEffect);
        } else {
            super.disableBuffEffect(buffEffect);
        }
    }

    public final void updateWithEnabledBuffs() {
        updateCharacterAttributeWithBuff();
        fillBuffEffectsParam();
    }

    protected void prepareToEnableBuff(BuffEffect buffEffect) {
        if (buffEffect.getFromSelf())
            relatedAttributeSet.addAll(buffEffect.getRelatedAttributeSet());
    }


    private void fillBuffEffectsParam() {
        // 填充除属性增加之外已启用BUFF的参数
        for (BuffEffect buffEffect : getEnabledBuffEffects()) {
            if (buffEffect.getFromSelf()) {

                buffEffect.fillDefaultInputParam(getAttributeWithBuff());
                buffEffect.fillSelfAttributeParam(getAttributeWithBuff());
            }
        }
    }

    protected Double getBaseFieldBaseValue() {
        Double baseValue = 0.;
        for (int i = 0; i < baseFieldAttributeList.size(); i++) {
            if (baseFieldAttributeList.get(i) != null) {
                baseValue += baseFieldMultiplierList.get(i) *
                        Objects.requireNonNull(getAttributeWithBuff().getEffectBasedAttribute(baseFieldAttributeList.get(i)));
            } else {
                baseValue += baseFieldMultiplierList.get(i);
            }
        }
        return baseValue;
    }

    // MULTIPLIER
    protected Double getBaseFieldMultiplier() {
        Double result = 1.;
        for (BuffEffect buffEffect : baseFieldMultiplierBuffEffects) {
            result *= buffEffect.getValue();
        }
        return result;
    }

    // VALUE_ADD
    protected Double getBaseFieldAddend() {
        Double result = 0.;
        for (BuffEffect buffEffect : baseFieldAddendBuffEffects) {
            result += buffEffect.getValue();
        }
        return result;
    }
}
