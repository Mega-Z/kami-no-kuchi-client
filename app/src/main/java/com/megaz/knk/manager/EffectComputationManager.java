package com.megaz.knk.manager;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.WorkerThread;

import com.megaz.knk.KnkDatabase;
import com.megaz.knk.computation.AmplifiedDamageEffect;
import com.megaz.knk.computation.BuffEffect;
import com.megaz.knk.computation.BuffInputParam;
import com.megaz.knk.computation.BuffQueryCondition;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.computation.DirectDamageEffect;
import com.megaz.knk.computation.FightEffect;
import com.megaz.knk.computation.HealEffect;
import com.megaz.knk.computation.QuickenDamageEffect;
import com.megaz.knk.computation.ShieldEffect;
import com.megaz.knk.computation.UpheavalDamageEffect;
import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.ElementReactionEnum;
import com.megaz.knk.constant.FightEffectEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.dao.FightEffectComputationDao;
import com.megaz.knk.dao.TalentCurveDao;
import com.megaz.knk.entity.FightEffectComputation;
import com.megaz.knk.entity.TalentCurve;
import com.megaz.knk.exception.MetaDataQueryException;
import com.megaz.knk.vo.EffectDetailVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EffectComputationManager {

    private final KnkDatabase knkDatabase;
    private final BuffManager buffManager;

    public EffectComputationManager(Context context) {
        this.knkDatabase = KnkDatabase.getKnkDatabase(context);
        buffManager = new BuffManager(context);
    }

    @WorkerThread
    public List<FightEffect> getFightEffectsByCharacterAttribute(CharacterAttribute characterAttribute) {
        FightEffectComputationDao fightEffectComputationDao = knkDatabase.getFightEffectComputationDao();
        List<FightEffectComputation> fightEffectComputationList = fightEffectComputationDao
                .selectByCharacterConditions(characterAttribute.getCharacterId(), characterAttribute.getPhase(), characterAttribute.getConstellation());
        Map<String, List<FightEffectComputation>> fightEffectComputationsMap = new HashMap<>();
        for (FightEffectComputation fightEffectComputation : fightEffectComputationList) {
            String effectId = fightEffectComputation.getEffectId();
            List<FightEffectComputation> fightEffectComputations =
                    fightEffectComputationsMap.getOrDefault(effectId, new ArrayList<>());
            assert fightEffectComputations != null;
            fightEffectComputations.add(fightEffectComputation);
            fightEffectComputationsMap.put(effectId, fightEffectComputations);
        }
        List<FightEffect> fightEffects = new ArrayList<>();
        for (Map.Entry<String, List<FightEffectComputation>> entry : fightEffectComputationsMap.entrySet()) {
            FightEffect fightEffect = createFightEffect(entry.getValue(), characterAttribute);
            fightEffects.add(fightEffect);
            initializeBuffs(fightEffect, characterAttribute);
            fightEffect.updateWithEnabledBuffs();
            //System.out.println(fightEffect.getValue());
        }
        return fightEffects;
    }

    @SuppressLint("DefaultLocale")
    public EffectDetailVo createFightEffectDetail(FightEffect fightEffect) {
        EffectDetailVo effectDetailVo = new EffectDetailVo();
        effectDetailVo.setCanCritical(fightEffect instanceof DirectDamageEffect);
        effectDetailVo.setEffectDesc(fightEffect.getEffectDesc());
        effectDetailVo.setIsPercent(fightEffect.getPercent());
        if (fightEffect instanceof DirectDamageEffect) {
            effectDetailVo.setEffectValue(((DirectDamageEffect) fightEffect).getAverageValue());
            effectDetailVo.setEffectValueCritical(((DirectDamageEffect) fightEffect).getCriticalValue());
        } else {
            effectDetailVo.setEffectValue(fightEffect.getValue());
        }
        effectDetailVo.setFieldDetail(fightEffect.getFieldDetail());
        return effectDetailVo;
    }

    public void disableBuffEffect(FightEffect fightEffect, BuffEffect buffEffect) {
        assert fightEffect.getEnabledBuffEffects().contains(buffEffect);
        fightEffect.disableBuffEffect(buffEffect);
        fightEffect.updateWithEnabledBuffs();
    }

    @WorkerThread
    public void enableBuffEffect(FightEffect fightEffect, BuffEffect buffEffect, List<BuffInputParam> buffInputParamList) {
        if (buffEffect.getFromSelf()) {
            // check additional related attributes
            Set<AttributeEnum> attributeSet = buffEffect.getRelatedAttributeSet();
            attributeSet.removeAll(fightEffect.getRelatedAttributeSet());
            if (!attributeSet.isEmpty()) {
                List<BuffEffect> additionalBuffEffectList = checkAdditionalAttributeAddBuffs(fightEffect,
                        new ArrayList<>(attributeSet));
                fightEffect.addAvailableBuffEffects(additionalBuffEffectList);
            }
            buffManager.fillBuffEffectCurveParam(buffEffect,
                    fightEffect.getAttributeBase().getTalentLevel().get(buffEffect.getSourceTalent()),
                    fightEffect.getAttributeBase().getWeaponRefinement());
        }
        if (buffInputParamList != null) {
            buffManager.fillBuffEffectInputParam(buffEffect, buffInputParamList);
        }
        fightEffect.enableBuffEffect(buffEffect);
        fightEffect.updateWithEnabledBuffs();
    }

    private List<BuffEffect> checkAdditionalAttributeAddBuffs(FightEffect fightEffect, List<AttributeEnum> additionalAttributes) {
        BuffQueryCondition baseBuffQueryCondition = fightEffect.getBuffQueryCondition();
        BuffQueryCondition additionalBuffQueryCondition = new BuffQueryCondition();
        additionalBuffQueryCondition.addBuffTypes(Collections.singletonList(FightEffectEnum.ATTRIBUTE_ADD));
        additionalBuffQueryCondition.setDamageElement(baseBuffQueryCondition.getDamageElement());
        additionalBuffQueryCondition.setDamageLabel(baseBuffQueryCondition.getDamageLabel());
        additionalBuffQueryCondition.setElementReaction(baseBuffQueryCondition.getElementReaction());
        additionalBuffQueryCondition.addAddedAttributes(additionalAttributes);
        return buffManager.queryBuffByCondition(fightEffect.getAttributeBase(),
                fightEffect.getEffectId(), additionalBuffQueryCondition);
    }

    @WorkerThread
    private FightEffect createFightEffect(List<FightEffectComputation> fightEffectComputations, CharacterAttribute characterAttribute) {
        TalentCurveDao talentCurveDao = knkDatabase.getTalentCurveDao();
        assert !fightEffectComputations.isEmpty();
        FightEffectEnum effectType = fightEffectComputations.get(0).getEffectType();
        // create instance
        FightEffect fightEffect = null;
        switch (effectType) {
            case DAMAGE:
                ElementReactionEnum reaction = null;
                int quickenCount = 0;
                for (FightEffectComputation fightEffectComputation : fightEffectComputations) {
                    if (GenshinConstantMeta.AMPLIFIED_REACTION_LIST.contains(fightEffectComputation.getElementReaction())) {
                        reaction = fightEffectComputation.getElementReaction();
                        fightEffect = new AmplifiedDamageEffect(fightEffectComputations.get(0), characterAttribute, reaction);
                        break;
                    } else if (GenshinConstantMeta.UPHEAVAL_REACTION_LIST.contains(fightEffectComputation.getElementReaction())) {
                        reaction = fightEffectComputation.getElementReaction();
                        fightEffect = new UpheavalDamageEffect(fightEffectComputations.get(0), characterAttribute, reaction);
                        break;
                    } else if (GenshinConstantMeta.QUICKEN_REACTION_LIST.contains(fightEffectComputation.getElementReaction())) {
                        reaction = fightEffectComputation.getElementReaction();
                        quickenCount++;
                    }
                }
                if (fightEffect == null) {
                    if (reaction != null) {
                        fightEffect = new QuickenDamageEffect(fightEffectComputations.get(0), characterAttribute, reaction, quickenCount);
                    } else {
                        fightEffect = new DirectDamageEffect(fightEffectComputations.get(0), characterAttribute);
                    }
                }
                break;
            case HEAL:
                fightEffect = new HealEffect(fightEffectComputations.get(0), characterAttribute);
                break;
            case SHIELD:
                fightEffect = new ShieldEffect(fightEffectComputations.get(0), characterAttribute);
                break;
            default:
                fightEffect = new FightEffect(fightEffectComputations.get(0), characterAttribute);
                break;
        }
        // set base multiplier
        for (FightEffectComputation fightEffectComputation : fightEffectComputations) {
            Double multiplier = fightEffectComputation.getMultiplierConstant();
            if (fightEffectComputation.getMultiplierTalentCurve() != null) {
                List<TalentCurve> talentCurveList = talentCurveDao.selectByCurveID(fightEffectComputation.getMultiplierTalentCurve());
                if (talentCurveList.size() != 1) {
                    throw new MetaDataQueryException("talent_curve");
                }
                multiplier *= talentCurveList.get(0)
                        .getValue(Objects.requireNonNull(characterAttribute
                                .getTalentLevel().get(fightEffectComputation.getSourceTalent())));
            }
            fightEffect.addBaseFieldMultiplierItem(fightEffectComputation.getBasedAttribute(), multiplier);
        }
        return fightEffect;
    }

    private void initializeBuffs(FightEffect fightEffect, CharacterAttribute characterAttribute) {
        BuffQueryCondition buffQueryCondition = fightEffect.getBuffQueryCondition();
        // query
        List<BuffEffect> buffEffectList = buffManager.queryStaticBuff(characterAttribute);
        buffEffectList.addAll(buffManager.queryBuffByCondition(
                characterAttribute, fightEffect.getEffectId(), buffQueryCondition));
        // enable default buff
        List<AttributeEnum> queriedAttributes = buffQueryCondition.getAddedAttributes();
        List<AttributeEnum> additionalAttributes = buffManager.getRelatedAttributesFromDefaultBuffs(buffEffectList);
        additionalAttributes.removeAll(queriedAttributes);
        // query additionally
        while (!additionalAttributes.isEmpty()) {
            BuffQueryCondition additionalBuffQueryCondition = new BuffQueryCondition();
            additionalBuffQueryCondition.addBuffTypes(Collections.singletonList(FightEffectEnum.ATTRIBUTE_ADD));
            additionalBuffQueryCondition.setDamageElement(buffQueryCondition.getDamageElement());
            additionalBuffQueryCondition.setDamageLabel(buffQueryCondition.getDamageLabel());
            additionalBuffQueryCondition.setElementReaction(buffQueryCondition.getElementReaction());
            additionalBuffQueryCondition.addAddedAttributes(additionalAttributes);
            List<BuffEffect> additionalBuffEffectList = buffManager.queryBuffByCondition(characterAttribute, fightEffect.getEffectId(), additionalBuffQueryCondition);
            buffEffectList.addAll(additionalBuffEffectList);
            additionalAttributes = buffManager.getRelatedAttributesFromDefaultBuffs(additionalBuffEffectList);
            additionalAttributes.removeAll(queriedAttributes);
            queriedAttributes.addAll(additionalAttributes);
        }
        // add buff effects to fight effect and enable default buffs
        fightEffect.addAvailableBuffEffects(buffEffectList);
        for (BuffEffect buffEffect : buffEffectList) {
            if (buffEffect.getDefaultEnabled() && buffEffect.getFromSelf()) {
                buffManager.fillBuffEffectCurveParam(buffEffect,
                        characterAttribute.getTalentLevel().get(buffEffect.getSourceTalent()),
                        characterAttribute.getWeaponRefinement());
                fightEffect.enableBuffEffect(buffEffect);
            }
        }
    }

}
