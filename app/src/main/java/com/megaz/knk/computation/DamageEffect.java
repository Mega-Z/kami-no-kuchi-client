package com.megaz.knk.computation;

import com.megaz.knk.constant.EffectFieldEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.ElementReactionEnum;
import com.megaz.knk.constant.FightEffectEnum;
import com.megaz.knk.entity.FightEffectComputation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

@Getter
public abstract class DamageEffect extends FightEffect{
    protected ElementEnum element;
    protected ElementReactionEnum reaction;
    private Set<BuffEffect> resistFieldBuffEffects;
    private Set<BuffEffect> reactionFieldBuffEffects;
    private Double baseResist = 0.1;

    public DamageEffect(FightEffectComputation fightEffectComputation, CharacterAttribute characterAttribute) {
        super(fightEffectComputation, characterAttribute);
        this.element = fightEffectComputation.getElement();
        resistFieldBuffEffects = new HashSet<>();
        reactionFieldBuffEffects = new HashSet<>();
    }

    @Override
    public void setEnemyAttribute(EnemyAttribute enemyAttribute) {
        this.baseResist = enemyAttribute.getResist(element);
    }

    @Override
    public Double getValue() {
        Double superValue = super.getValue();
        return superValue * getResistFieldValue();
    }

    @Override
    public Map<EffectFieldEnum, Double> getFieldDetail() {
        Map<EffectFieldEnum, Double> fieldDetail = super.getFieldDetail();
        fieldDetail.put(EffectFieldEnum.RESIST, getResistFieldValue());
        if(!reactionFieldBuffEffects.isEmpty()) {
            fieldDetail.put(EffectFieldEnum.REACTION, getReactionFieldValue());
        }
        return fieldDetail;
    }

    @Override
    public BuffQueryCondition getBuffQueryCondition() {
        BuffQueryCondition buffQueryCondition = super.getBuffQueryCondition();
        buffQueryCondition.addBuffTypes(Collections.singletonList(FightEffectEnum.RESIST_DOWN));
        buffQueryCondition.setDamageElement(element);
        return buffQueryCondition;
    }

    @Override
    public List<BuffEffect> getEnabledBuffEffects() {
        List<BuffEffect> buffEffectList = super.getEnabledBuffEffects();
        buffEffectList.addAll(resistFieldBuffEffects);
        return buffEffectList;
    }

    @Override
    public void enableBuffEffect(BuffEffect buffEffect) {
        prepareToEnableBuff(buffEffect);
        if(buffEffect.getEffectType() == FightEffectEnum.RESIST_DOWN &&
                buffEffect.getElement() ==element) {
            resistFieldBuffEffects.add(buffEffect);
        } else if(buffEffect.getEffectType() == FightEffectEnum.REACTION_UP &&
                buffEffect.getElementReaction() == reaction) {
            reactionFieldBuffEffects.add(buffEffect);
        } else {
            super.enableBuffEffect(buffEffect);
        }
    }

    @Override
    public void disableBuffEffect(BuffEffect buffEffect) {
        assert availableBuffEffects.containsKey(buffEffect.getBuffId());
        buffEffect.disableBuff();
        characterAttributeWithBuffs = null;
        if(buffEffect.getEffectType() == FightEffectEnum.RESIST_DOWN &&
                buffEffect.getElement() ==element) {
            resistFieldBuffEffects.remove(buffEffect);
        } else if(buffEffect.getEffectType() == FightEffectEnum.REACTION_UP &&
                buffEffect.getElementReaction() == reaction) {
            reactionFieldBuffEffects.remove(buffEffect);
        } else {
            super.disableBuffEffect(buffEffect);
        }
    }

    protected Double getReactionFieldValue() {
        Double reactionFieldValue = 0.;
        for(BuffEffect buffEffect:reactionFieldBuffEffects) {
            reactionFieldValue += buffEffect.getValue();
        }
        return reactionFieldValue;
    }

    protected abstract Double getMasteryFieldValue();

    private Double getResistFieldValue() {
        Double resist = baseResist;
        for(BuffEffect buffEffect:resistFieldBuffEffects) {
            resist -= buffEffect.getValue();
        }
        double resistFieldValue;
        if(resist < 0) {
            resistFieldValue = 1-resist/2;
        } else if (resist >=0 && resist <= 0.75) {
            resistFieldValue = 1-resist;
        } else {
            resistFieldValue = 1/(1+resist*4);
        }
        return resistFieldValue;
    }
}
