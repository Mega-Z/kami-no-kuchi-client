package com.megaz.knk.computation;

import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.EffectFieldEnum;
import com.megaz.knk.entity.FightEffectComputation;

import java.util.Collections;
import java.util.Map;

public class ShieldEffect extends FightEffect{
    public ShieldEffect(FightEffectComputation fightEffectComputation, CharacterAttribute characterAttribute) {
        super(fightEffectComputation, characterAttribute);
    }

    @Override
    public Double getValue() {
        assert characterAttributeWithBuffs != null;
        Double superValue = super.getValue();
        Double shieldUpValue = 1 + getUpFieldValue();
        return superValue * shieldUpValue;
    }

    @Override
    public Map<EffectFieldEnum, Double> getFieldDetail() {
        assert characterAttributeWithBuffs != null;
        Map<EffectFieldEnum, Double> fieldDetail = super.getFieldDetail();
        if(!getUpFieldValue().equals(0.)) {
            fieldDetail.put(EffectFieldEnum.UP, getUpFieldValue());
        }
        return fieldDetail;
    }

    @Override
    public BuffQueryCondition getBuffQueryCondition() {
        BuffQueryCondition buffQueryCondition = super.getBuffQueryCondition();
        buffQueryCondition.addAddedAttributes(Collections.singletonList(AttributeEnum.SHIELD_EFFECT));
        return buffQueryCondition;
    }

    private Double getUpFieldValue() {
        return characterAttributeWithBuffs.getShieldStrength();
    }
}
