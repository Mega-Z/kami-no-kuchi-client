package com.megaz.knk.computation;

import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.EffectFieldEnum;
import com.megaz.knk.curve.ElementCoefficient;
import com.megaz.knk.constant.ElementReactionEnum;
import com.megaz.knk.constant.FightEffectEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.entity.FightEffectComputation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UpheavalDamageEffect extends DamageEffect{

    public UpheavalDamageEffect(FightEffectComputation fightEffectComputation, CharacterAttribute characterAttribute,
                                ElementReactionEnum reaction) {
        super(fightEffectComputation, characterAttribute);
        this.reaction = reaction;
    }

    @Override
    public Double getValue() {
        assert characterAttributeWithBuffs != null;
        Double superValue = super.getValue();
        Double levelCoefficient = ElementCoefficient.getElementCoefficientByLevel(characterAttributeWithBuffs.getLevel());
        return superValue * levelCoefficient * reaction.getCoefficient() *
                (1+getMasteryFieldValue()+getReactionFieldValue());
    }

    @Override
    public Map<EffectFieldEnum, Double> getFieldDetail() {
        assert characterAttributeWithBuffs != null;
        Map<EffectFieldEnum, Double> fieldDetail = super.getFieldDetail();
        fieldDetail.put(EffectFieldEnum.MASTERY, getMasteryFieldValue());
        return fieldDetail;
    }

    @Override
    public BuffQueryCondition getBuffQueryCondition() {
        BuffQueryCondition buffQueryCondition = super.getBuffQueryCondition();
        buffQueryCondition.addBuffTypes(Collections.singletonList(FightEffectEnum.REACTION_UP));
        buffQueryCondition.addAddedAttributes(Collections.singletonList(AttributeEnum.MASTERY));
        buffQueryCondition.setElementReaction(reaction);
        return buffQueryCondition;
    }

    @Override
    protected Double getMasteryFieldValue() {
        return characterAttributeWithBuffs.getMastery()*16/(characterAttributeWithBuffs.getMastery()+2000);
    }
}
