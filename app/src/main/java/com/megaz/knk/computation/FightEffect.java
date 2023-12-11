package com.megaz.knk.computation;

import com.megaz.knk.constant.EffectBaseAttributeEnum;
import com.megaz.knk.constant.EffectFieldEnum;
import com.megaz.knk.constant.FightEffectEnum;
import com.megaz.knk.entity.FightEffectComputation;
import com.megaz.knk.exception.BuffNoFieldMatchedException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
public class FightEffect implements Serializable {
    protected String effectId;
    protected String effectDesc;
    protected Boolean percent;
    protected CharacterAttribute characterAttribute;
    protected CharacterAttribute characterAttributeWithBuffs;
    protected Map<String, BuffEffect> availableBuffEffects;
    private List<EffectBaseAttributeEnum> baseFieldAttributeList;
    private List<Double> baseFieldMultiplierList;
    private Set<EffectBaseAttributeEnum> basedAttributeSet;
    private Set<BuffEffect> attributeAddBuffEffects;
    private Set<BuffEffect> baseFieldAddendBuffEffects;
    private Set<BuffEffect> baseFieldMultiplierBuffEffects;

    public FightEffect(FightEffectComputation fightEffectComputation, CharacterAttribute characterAttribute) {
        this.effectId = fightEffectComputation.getEffectId();
        this.effectDesc = fightEffectComputation.getDescription();
        this.percent = fightEffectComputation.getPercent();
        this.characterAttribute = characterAttribute;
        baseFieldAttributeList = new ArrayList<>();
        baseFieldMultiplierList = new ArrayList<>();
        basedAttributeSet = new HashSet<>();
        availableBuffEffects = new HashMap<>();
        attributeAddBuffEffects = new HashSet<>();
        baseFieldAddendBuffEffects = new HashSet<>();
        baseFieldMultiplierBuffEffects = new HashSet<>();
    }

    public void addBaseFieldMultiplierItem(EffectBaseAttributeEnum attribute, Double multiplier) {
        baseFieldAttributeList.add(attribute);
        baseFieldMultiplierList.add(multiplier);
        basedAttributeSet.add(attribute);
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
        if(!baseFieldAttributeList.isEmpty()) {
            fieldDetail.put(EffectFieldEnum.BASE, getBaseFieldBaseValue());
        }
        if(!baseFieldMultiplierBuffEffects.isEmpty()) {
            fieldDetail.put(EffectFieldEnum.BASE_MULTIPLE, getBaseFieldMultiplier());
        }
        if(!baseFieldAddendBuffEffects.isEmpty()) {
            fieldDetail.put(EffectFieldEnum.BASE_ADD, getBaseFieldAddend());
        }
        return fieldDetail;
    }

    public BuffQueryCondition getBuffQueryCondition() {
        BuffQueryCondition buffQueryCondition = new BuffQueryCondition();
        buffQueryCondition.addBuffTypes(Arrays.asList(
                FightEffectEnum.ATTRIBUTE_ADD, FightEffectEnum.VALUE_ADD, FightEffectEnum.MULTIPLIER));
        for (EffectBaseAttributeEnum effectBaseAttribute : basedAttributeSet) {
            if (effectBaseAttribute != null)
                buffQueryCondition.addAddedAttributes(effectBaseAttribute.getRelatedAttributes());
        }
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
        List<BuffEffect> buffEffectList = new ArrayList<>();
        buffEffectList.addAll(attributeAddBuffEffects);
        buffEffectList.addAll(baseFieldAddendBuffEffects);
        buffEffectList.addAll(baseFieldMultiplierBuffEffects);
        return buffEffectList;
    }

    public void enableBuffEffect(BuffEffect buffEffect) {
        assert availableBuffEffects.containsKey(buffEffect.getBuffId());
        buffEffect.enableBuff();
        characterAttributeWithBuffs = null;
        if (buffEffect.getEffectType() == FightEffectEnum.ATTRIBUTE_ADD) {
            attributeAddBuffEffects.add(buffEffect);
        } else if (buffEffect.getEffectType() == FightEffectEnum.MULTIPLIER) {
            baseFieldMultiplierBuffEffects.add(buffEffect);
        } else if (buffEffect.getEffectType() == FightEffectEnum.VALUE_ADD) {
            baseFieldAddendBuffEffects.add(buffEffect);
        } else {
            throw new BuffNoFieldMatchedException(buffEffect.getBuffName());
        }
    }

    public void disableBuffEffect(BuffEffect buffEffect) {
        assert availableBuffEffects.containsKey(buffEffect.getBuffId());
        buffEffect.disableBuff();
        characterAttributeWithBuffs = null;
        if (buffEffect.getEffectType() == FightEffectEnum.ATTRIBUTE_ADD) {
            attributeAddBuffEffects.remove(buffEffect);
        } else if (buffEffect.getEffectType() == FightEffectEnum.MULTIPLIER) {
            baseFieldMultiplierBuffEffects.remove(buffEffect);
        } else if (buffEffect.getEffectType() == FightEffectEnum.VALUE_ADD) {
            baseFieldAddendBuffEffects.remove(buffEffect);
        } else {
            throw new BuffNoFieldMatchedException(buffEffect.getBuffName());
        }
    }

    public final void fillAttributeBuffEffectsParam() {
        for(BuffEffect buffEffect:attributeAddBuffEffects){
            buffEffect.fillAttributeParam(characterAttribute);
        }
    }

    public final void fillBuffEffectsParam() {
        assert characterAttributeWithBuffs != null;
        for(BuffEffect buffEffect: getEnabledBuffEffects()) {
            buffEffect.fillAttributeParam(characterAttributeWithBuffs);
        }
    }

    // ATTRIBUTE_ADD
    public final void updateCharacterAttributeWithBuff() {
        characterAttributeWithBuffs = new CharacterAttribute(characterAttribute);
        for(BuffEffect buffEffect:attributeAddBuffEffects) {
            characterAttributeWithBuffs.addAttributeValue(buffEffect.getIncreasedAttribute(), buffEffect.getValue());
        }
    }

    protected Double getBaseFieldBaseValue() {
        Double baseValue = 0.;
        for (int i = 0; i < baseFieldAttributeList.size(); i++) {
            if (baseFieldAttributeList.get(i) != null) {
                baseValue += baseFieldMultiplierList.get(i) *
                        Objects.requireNonNull(characterAttributeWithBuffs.getEffectBasedAttribute(baseFieldAttributeList.get(i)));
            } else {
                baseValue += baseFieldMultiplierList.get(i);
            }
        }
        return baseValue;
    }

    // MULTIPLIER
    protected Double getBaseFieldMultiplier() {
        Double result = 1.;
        for(BuffEffect buffEffect:baseFieldMultiplierBuffEffects) {
            result *= buffEffect.getValue();
        }
        return result;
    }

    // VALUE_ADD
    protected Double getBaseFieldAddend() {
        Double result = 0.;
        for(BuffEffect buffEffect:baseFieldAddendBuffEffects) {
            result += buffEffect.getValue();
        }
        return result;
    }
}
