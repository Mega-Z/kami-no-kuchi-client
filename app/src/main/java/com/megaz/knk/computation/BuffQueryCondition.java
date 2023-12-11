package com.megaz.knk.computation;

import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.DamageLabelEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.ElementReactionEnum;
import com.megaz.knk.constant.FightEffectEnum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BuffQueryCondition {
    private Set<FightEffectEnum> buffTypes;
    private Set<AttributeEnum> addedAttributeSet;
    private DamageLabelEnum damageLabel;
    private ElementEnum damageElement;
    private ElementReactionEnum elementReaction;

    public BuffQueryCondition() {
        buffTypes = new HashSet<>();
        addedAttributeSet = new HashSet<>();
        addedAttributeSet.add(AttributeEnum.NULL);
        damageLabel = DamageLabelEnum.NULl;
        damageElement = ElementEnum.NULL;
        elementReaction = ElementReactionEnum.NULL;
    }

    public List<FightEffectEnum> getBuffTypes() {
        return new ArrayList<>(buffTypes);
    }

    public void addBuffTypes(List<FightEffectEnum> buffTypeList) {
        this.buffTypes.addAll(buffTypeList);
    }

    public List<AttributeEnum> getAddedAttributes() {
        return new ArrayList<>(addedAttributeSet);
    }

    public void addAddedAttributes(List<AttributeEnum> addedAttributeList) {
        this.addedAttributeSet.addAll(addedAttributeList);
    }

    public DamageLabelEnum getDamageLabel() {
        return damageLabel;
    }

    public void setDamageLabel(DamageLabelEnum damageLabel) {
        this.damageLabel = damageLabel;
    }

    public ElementEnum getDamageElement() {
        return damageElement;
    }

    public void setDamageElement(ElementEnum damageElement) {
        this.damageElement = damageElement;
    }

    public ElementReactionEnum getElementReaction() {
        return elementReaction;
    }

    public void setElementReaction(ElementReactionEnum elementReaction) {
        this.elementReaction = elementReaction;
    }
}
