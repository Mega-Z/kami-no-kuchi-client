package com.megaz.knk.computation;

import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.EffectFieldEnum;
import com.megaz.knk.entity.Buff;
import com.megaz.knk.entity.FightEffectComputation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HealEffect extends FightEffect{
    public HealEffect(FightEffectComputation fightEffectComputation, CharacterAttribute characterAttribute) {
        super(fightEffectComputation, characterAttribute);
    }

    @Override
    public Double getValue() {
        assert characterAttributeWithBuffs != null;
        Double superValue = super.getValue();
        Double healUpMultiplier = 1 + getUpFieldValue();
        return superValue * healUpMultiplier;
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
        buffQueryCondition.addAddedAttributes(Arrays.asList(
                AttributeEnum.HEAL, AttributeEnum.HEALED));
        return buffQueryCondition;
    }

    private Double getUpFieldValue() {
        return characterAttributeWithBuffs.getHealUp()+characterAttributeWithBuffs.getHealedUp();
    }
}
