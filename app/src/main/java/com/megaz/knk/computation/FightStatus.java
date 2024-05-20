package com.megaz.knk.computation;

import com.megaz.knk.constant.BuffStageEnum;
import com.megaz.knk.constant.FightEffectEnum;
import com.megaz.knk.exception.BuffNoFieldMatchedException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FightStatus implements Serializable {

    protected CharacterAttribute characterAttributeBase;
    protected CharacterAttribute characterAttributeWithBuffs;
    private final Set<BuffEffect> attributeAddBuffEffects;

    public FightStatus(CharacterAttribute characterAttributeBase) {
        this.characterAttributeBase = characterAttributeBase;
        attributeAddBuffEffects = new HashSet<>();
    }

    public List<BuffEffect> getEnabledBuffEffects() {
        return new ArrayList<>(attributeAddBuffEffects);
    }

    public void enableBuffEffect(BuffEffect buffEffect) {
        if (buffEffect.getEffectType() == FightEffectEnum.ATTRIBUTE_ADD) {
            characterAttributeWithBuffs = null;
            attributeAddBuffEffects.add(buffEffect);
        }  else {
            throw new BuffNoFieldMatchedException(buffEffect.getBuffName());
        }
    }

    public void disableBuffEffect(BuffEffect buffEffect) {
        if (buffEffect.getEffectType() == FightEffectEnum.ATTRIBUTE_ADD) {
            characterAttributeWithBuffs = null;
            attributeAddBuffEffects.remove(buffEffect);
        }  else {
            throw new BuffNoFieldMatchedException(buffEffect.getBuffName());
        }
    }

    public CharacterAttribute getAttributeBase() {
        return characterAttributeBase;
    }

    public CharacterAttribute getAttributeWithBuff() {
        if (characterAttributeWithBuffs == null) {
            updateCharacterAttributeWithBuff();
        }
        return characterAttributeWithBuffs;
    }

    protected void updateCharacterAttributeWithBuff() {
        // 提瓦特的法则：基于某种属性带来的另一种属性的提升，该提升不可用于计算其他同类提升
        // 先算不基于属性的属性提升
        CharacterAttribute characterAttributeWithNoBaseBuffs = new CharacterAttribute(characterAttributeBase);
        for(BuffEffect buffEffect:attributeAddBuffEffects) {
            if(buffEffect.getStage() == BuffStageEnum.ATTRIBUTE_OVER_CONSTANT) {
                if(buffEffect.getFromSelf()) {
                    buffEffect.fillDefaultInputParam(characterAttributeBase);
                    buffEffect.fillSelfAttributeParam(characterAttributeBase);
                }
                characterAttributeWithNoBaseBuffs.addAttributeValue(buffEffect.getIncreasedAttribute(), buffEffect.getValue());
            }
        }
        // 再算基于上述提升后属性的属性提升
        characterAttributeWithBuffs = new CharacterAttribute(characterAttributeWithNoBaseBuffs);
        for(BuffEffect buffEffect:attributeAddBuffEffects) {
            if(buffEffect.getStage() == BuffStageEnum.ATTRIBUTE_OVER_ATTRIBUTE) {
                if (buffEffect.getFromSelf()) {
                    buffEffect.fillDefaultInputParam(characterAttributeWithNoBaseBuffs);
                    buffEffect.fillSelfAttributeParam(characterAttributeWithNoBaseBuffs);
                }
                characterAttributeWithBuffs.addAttributeValue(buffEffect.getIncreasedAttribute(), buffEffect.getValue());
            }
        }
    }
}
