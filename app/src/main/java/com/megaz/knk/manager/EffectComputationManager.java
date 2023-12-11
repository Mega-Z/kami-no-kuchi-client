package com.megaz.knk.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.megaz.knk.KnkDatabase;
import com.megaz.knk.computation.AmplifiedDamageEffect;
import com.megaz.knk.computation.BuffEffect;
import com.megaz.knk.computation.BuffQueryCondition;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.computation.DirectDamageEffect;
import com.megaz.knk.computation.FightEffect;
import com.megaz.knk.computation.HealEffect;
import com.megaz.knk.computation.QuickenDamageEffect;
import com.megaz.knk.computation.ShieldEffect;
import com.megaz.knk.computation.UpheavalDamageEffect;
import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.BuffSourceEnum;
import com.megaz.knk.constant.EffectFieldEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.ElementReactionEnum;
import com.megaz.knk.constant.FightEffectEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.dao.ArtifactDexDao;
import com.megaz.knk.dao.BuffDao;
import com.megaz.knk.dao.BuffEffectRelationDao;
import com.megaz.knk.dao.CharacterDexDao;
import com.megaz.knk.dao.FightEffectComputationDao;
import com.megaz.knk.dao.RefinementCurveDao;
import com.megaz.knk.dao.TalentCurveDao;
import com.megaz.knk.dao.WeaponDexDao;
import com.megaz.knk.entity.ArtifactDex;
import com.megaz.knk.entity.Buff;
import com.megaz.knk.entity.BuffEffectRelation;
import com.megaz.knk.entity.CharacterDex;
import com.megaz.knk.entity.FightEffectComputation;
import com.megaz.knk.entity.RefinementCurve;
import com.megaz.knk.entity.TalentCurve;
import com.megaz.knk.entity.WeaponDex;
import com.megaz.knk.exception.BuffNoFieldMatchedException;
import com.megaz.knk.exception.MetaDataQueryException;
import com.megaz.knk.vo.BuffVo;
import com.megaz.knk.vo.EffectDetailVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class EffectComputationManager {

    private Context context;
    private KnkDatabase knkDatabase;

    public EffectComputationManager(Context context) {
        this.context = context;
        this.knkDatabase = KnkDatabase.getKnkDatabase(context);
    }

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
            fightEffect.fillAttributeBuffEffectsParam();
            fightEffect.updateCharacterAttributeWithBuff();
            fightEffect.fillBuffEffectsParam();
            //System.out.println(fightEffect.getValue());
        }
        return fightEffects;
    }

    @SuppressLint("DefaultLocale")
    public EffectDetailVo createFightEffectDetail(FightEffect fightEffect) {
        EffectDetailVo effectDetailVo = new EffectDetailVo();
        effectDetailVo.setCanCritical(fightEffect instanceof DirectDamageEffect);
        effectDetailVo.setEffectDesc(fightEffect.getEffectDesc());
        if(fightEffect instanceof DirectDamageEffect) {
            effectDetailVo.setEffectValue(String.format("%d/%d",
                    (int)(((DirectDamageEffect) fightEffect).getCriticalValue().doubleValue()),
                    (int)((((DirectDamageEffect) fightEffect).getAverageValue().doubleValue()))));
        } else {
            if(fightEffect.getPercent()) {
                effectDetailVo.setEffectValue(String.format("%.2f", fightEffect.getValue()*100)+"%");
            } else if (fightEffect.getValue() > 1000){
                effectDetailVo.setEffectValue(String.format("%d", (int)(fightEffect.getValue().doubleValue())));
            } else {
                effectDetailVo.setEffectValue(String.format("%.2f", fightEffect.getValue()));
            }
        }
        effectDetailVo.setFieldDetail(fightEffect.getFieldDetail());
        return effectDetailVo;
    }

    public BuffVo createBuffVo(BuffEffect buffEffect) {
        CharacterDexDao characterDexDao = knkDatabase.getCharacterDexDao();
        WeaponDexDao weaponDexDao = knkDatabase.getWeaponDexDao();
        ArtifactDexDao artifactDexDao = knkDatabase.getArtifactDexDao();

        BuffVo buffVo = new BuffVo();
        buffVo.setBuffTitle(buffEffect.getSourceName()+"："+buffEffect.getBuffName());
        buffVo.setBuffDesc(buffEffect.getDescription());
        buffVo.setSourceType(buffEffect.getSourceType());
        if(buffEffect.getSourceType() == BuffSourceEnum.CHARACTER) {
            List<CharacterDex> characterDexList = characterDexDao.selectByCharacterId(buffEffect.getSourceId());
            if(characterDexList.size() != 1) {
                throw new MetaDataQueryException("character_dex");
            }
            buffVo.setIcon(characterDexList.get(0).getIconAvatar());
        } else if (buffEffect.getSourceType() == BuffSourceEnum.WEAPON) {
            List<WeaponDex> weaponDexList = weaponDexDao.selectByWeaponId(buffEffect.getSourceId());
            if(weaponDexList.size() != 1) {
                throw new MetaDataQueryException("weapon_dex");
            }
            buffVo.setIcon(weaponDexList.get(0).getIconAwaken());
        } else if (buffEffect.getSourceType() == BuffSourceEnum.ARTIFACT_SET) {
            List<ArtifactDex> artifactDexList = artifactDexDao.selectBySetId(buffEffect.getSourceId());
            if(artifactDexList.size() <= 0) {
                throw new MetaDataQueryException("artifact_dex");
            }
            buffVo.setIcon(artifactDexList.get(0).getIcon());
        }
        buffVo.setEnabled(buffEffect.getEnabled());
        buffVo.setPercent(buffEffect.getPercent());
        if(buffEffect.getEnabled()) {
            buffVo.setEffectValue(buffEffect.getValue());
        }
        if(buffEffect.getEffectType() == FightEffectEnum.ATTRIBUTE_ADD ||
                buffEffect.getEffectType() == FightEffectEnum.DAMAGE_UP) {
            if(buffEffect.getIncreasedAttribute() == AttributeEnum.CRIT_RATE) {
                buffVo.setBuffField(EffectFieldEnum.CRIT_RATE);
            } else if (buffEffect.getIncreasedAttribute() == AttributeEnum.CRIT_DMG) {
                buffVo.setBuffField(EffectFieldEnum.CRIT_DMG);
            } else if (buffEffect.getIncreasedAttribute() == AttributeEnum.HEAL ||
                    buffEffect.getIncreasedAttribute() == AttributeEnum.HEALED ||
                    buffEffect.getIncreasedAttribute() == AttributeEnum.SHIELD_EFFECT) {
                buffVo.setBuffField(EffectFieldEnum.UP);
            } else if (buffEffect.getIncreasedAttribute() != null &&
                    buffEffect.getIncreasedAttribute().getElement() != ElementEnum.NULL) {
                buffVo.setBuffField(EffectFieldEnum.DAMAGE_UP);
            } else if (buffEffect.getIncreasedAttribute() != null) {
                buffVo.setBuffField(EffectFieldEnum.BASE);
            } else {
                buffVo.setBuffField(EffectFieldEnum.DAMAGE_UP);
            }
        } else if (buffEffect.getEffectType() == FightEffectEnum.MULTIPLIER) {
            buffVo.setBuffField(EffectFieldEnum.BASE_MULTIPLE);
        } else if (buffEffect.getEffectType() == FightEffectEnum.VALUE_ADD) {
            buffVo.setBuffField(EffectFieldEnum.BASE_ADD);
        } else if (buffEffect.getEffectType() == FightEffectEnum.RESIST_DOWN) {
            buffVo.setBuffField(EffectFieldEnum.RESIST);
        } else if(buffEffect.getEffectType() == FightEffectEnum.DEF_DOWN ||
                buffEffect.getEffectType()==FightEffectEnum.DEF_IGNORE) {
            buffVo.setBuffField(EffectFieldEnum.DEFENCE);
        } else if(buffEffect.getEffectType() == FightEffectEnum.REACTION_UP) {
            buffVo.setBuffField(EffectFieldEnum.REACTION);
        } else {
            throw new BuffNoFieldMatchedException(buffEffect.getBuffName());
        }
        if (buffEffect.getEffectType() == FightEffectEnum.DEF_DOWN ||
                buffEffect.getEffectType()==FightEffectEnum.DEF_IGNORE) {
            buffVo.setEffectText(buffEffect.getEffectType().getDesc());
        } else if (buffVo.getBuffField() == EffectFieldEnum.DAMAGE_UP) {
            buffVo.setEffectText(buffVo.getBuffField().getEffectText());
        } else if (buffEffect.getIncreasedAttribute() != null) {
            buffVo.setEffectText(buffEffect.getIncreasedAttribute().getDesc());
        } else {
            buffVo.setEffectText(buffVo.getBuffField().getEffectText());
        }

        return buffVo;
    }

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
        List<BuffEffect> buffEffectList = queryBuffByCondition(
                characterAttribute, fightEffect.getEffectId(), buffQueryCondition);
        // enable default buff
        List<AttributeEnum> queriedAttributes = buffQueryCondition.getAddedAttributes();
        List<AttributeEnum> additionalAttributes = enableDefaultBuffs(buffEffectList, characterAttribute);
        additionalAttributes.removeAll(queriedAttributes);
        // query additionally
        while(!additionalAttributes.isEmpty()) {
            BuffQueryCondition additionalBuffQueryCondition = new BuffQueryCondition();
            additionalBuffQueryCondition.addBuffTypes(Collections.singletonList(FightEffectEnum.ATTRIBUTE_ADD));
            additionalBuffQueryCondition.setDamageElement(buffQueryCondition.getDamageElement());
            additionalBuffQueryCondition.setDamageLabel(buffQueryCondition.getDamageLabel());
            additionalBuffQueryCondition.setElementReaction(buffQueryCondition.getElementReaction());
            additionalBuffQueryCondition.addAddedAttributes(additionalAttributes);
            queriedAttributes.addAll(additionalAttributes);
            List<BuffEffect> additionalBuffEffectList = queryBuffByCondition(characterAttribute, fightEffect.getEffectId(), additionalBuffQueryCondition);
            buffEffectList.addAll(additionalBuffEffectList);
            // enable default buff again
            additionalAttributes = enableDefaultBuffs(additionalBuffEffectList, characterAttribute);
            additionalAttributes.removeAll(queriedAttributes);
        }
        // add buff effects to fight effect
        fightEffect.addAvailableBuffEffects(buffEffectList);
        for(BuffEffect buffEffect:buffEffectList) {
            if(buffEffect.getEnabled()) {
                fightEffect.enableBuffEffect(buffEffect);
            }
        }
    }

    private List<BuffEffect> queryBuffByCondition(CharacterAttribute characterAttribute,
                                                  String effectId, BuffQueryCondition buffQueryCondition) {
        BuffDao buffDao = knkDatabase.getBuffDao();
        BuffEffectRelationDao buffEffectRelationDao = knkDatabase.getBuffEffectRelationDao();
        assert !buffQueryCondition.getBuffTypes().isEmpty();
        List<Buff> buffs = new ArrayList<>();
        // query effect ranged buffs
        List<BuffEffectRelation> buffEffectRelations = buffEffectRelationDao.selectByEffectId(effectId);
        List<String> buffIdList = buffEffectRelations.stream().map(BuffEffectRelation::getBuffId).collect(Collectors.toList());
        Set<String> forcedBuffIdSet = buffEffectRelations.stream().filter(BuffEffectRelation::getForced).map(BuffEffectRelation::getBuffId).collect(Collectors.toSet());
        if (!buffIdList.isEmpty()) {
            buffs.addAll(buffDao.selectEffectRangedBuffByCondition(
                    buffIdList,
                    characterAttribute.getPhase(), characterAttribute.getConstellation(),
                    buffQueryCondition.getBuffTypes(), buffQueryCondition.getAddedAttributes(),
                    buffQueryCondition.getDamageElement(), buffQueryCondition.getDamageLabel(),
                    buffQueryCondition.getElementReaction()));
        }
        // query character ranged buffs
        buffs.addAll(buffDao.selectCharacterRangedCharacterBuffByCondition(
                characterAttribute.getCharacterId(),
                characterAttribute.getPhase(), characterAttribute.getConstellation(),
                buffQueryCondition.getBuffTypes(), buffQueryCondition.getAddedAttributes(),
                buffQueryCondition.getDamageElement(), buffQueryCondition.getDamageLabel(),
                buffQueryCondition.getElementReaction()));
        buffs.addAll(buffDao.selectCharacterRangedWeaponBuffByCondition(
                characterAttribute.getWeaponId(),
                buffQueryCondition.getBuffTypes(), buffQueryCondition.getAddedAttributes(),
                buffQueryCondition.getDamageElement(), buffQueryCondition.getDamageLabel(),
                buffQueryCondition.getElementReaction()));
        for (Map.Entry<String, Integer> artifactSetEntry : characterAttribute.getArtifactSetCount().entrySet()) {
            buffs.addAll(buffDao.selectCharacterRangedArtifactBuffByCondition(
                    artifactSetEntry.getKey(), artifactSetEntry.getValue(),
                    buffQueryCondition.getBuffTypes(), buffQueryCondition.getAddedAttributes(),
                    buffQueryCondition.getDamageElement(), buffQueryCondition.getDamageLabel(),
                    buffQueryCondition.getElementReaction()));
        }
        // query party ranged buffs
        buffs.addAll(buffDao.selectPartyRangedBuffByCondition(
                buffQueryCondition.getBuffTypes(), buffQueryCondition.getAddedAttributes(),
                buffQueryCondition.getDamageElement(), buffQueryCondition.getDamageLabel(),
                buffQueryCondition.getElementReaction()));
        // query others ranged buffs
        buffs.addAll(buffDao.selectOthersRangedBuffByCondition(
                characterAttribute.getCharacterId(),
                buffQueryCondition.getBuffTypes(), buffQueryCondition.getAddedAttributes(),
                buffQueryCondition.getDamageElement(), buffQueryCondition.getDamageLabel(),
                buffQueryCondition.getElementReaction()));
        // create buff effects
        List<BuffEffect> buffEffectList = new ArrayList<>();
        for(Buff buff:buffs) {
            if(forcedBuffIdSet.contains(buff.getBuffId())) {
                buffEffectList.add(new BuffEffect(buff, true));
            } else {
                buffEffectList.add(new BuffEffect(buff, false));
            }
        }
        return buffEffectList;
    }

    private List<AttributeEnum> enableDefaultBuffs(List<BuffEffect> buffEffectList, CharacterAttribute characterAttribute) {
        Set<AttributeEnum> additionalAttributeSet = new HashSet<>();
        for(BuffEffect buffEffect:buffEffectList) {
            if(!buffEffect.getDefaultEnabled()) {
                continue;
            }
            boolean enableFlag = false;
            if(buffEffect.getSourceType() == BuffSourceEnum.CHARACTER &&
                    buffEffect.getSourceId().equals(characterAttribute.getCharacterId())) {
                if(buffEffect.getPhase() < -1 * characterAttribute.getPhase() ||
                        (buffEffect.getPhase() >= 0 && buffEffect.getPhase() <= characterAttribute.getPhase())) {
                    if(buffEffect.getConstellation() < -1 * characterAttribute.getConstellation() ||
                            (buffEffect.getConstellation() >=0 && buffEffect.getConstellation() <= characterAttribute.getConstellation())){
                        enableFlag = true;
                    }
                }
            }
            if(buffEffect.getSourceType() == BuffSourceEnum.WEAPON &&
                    buffEffect.getSourceId().equals(characterAttribute.getWeaponId())){
                enableFlag = true;
            }
            if(buffEffect.getSourceType() == BuffSourceEnum.ARTIFACT_SET &&
                    characterAttribute.getArtifactSetCount().containsKey(buffEffect.getSourceId())) {
                if(buffEffect.getArtifactNum() <= Objects.requireNonNull(characterAttribute.getArtifactSetCount().get(buffEffect.getSourceId()))) {
                    enableFlag = true;
                }
            }
            if(enableFlag) {
                fillBuffEffectCurveParam(buffEffect, characterAttribute);
                buffEffect.enableBuff();
                if(buffEffect.getBasedAttribute() != null) {
                    additionalAttributeSet.addAll(buffEffect.getBasedAttribute().getRelatedAttributes());
                }
                if(buffEffect.getBasedAttributeSecond() != null) {
                    additionalAttributeSet.addAll(buffEffect.getBasedAttributeSecond().getRelatedAttributes());
                }
                if(buffEffect.getMaxValueBasedAttribute() != null) {
                    additionalAttributeSet.addAll(buffEffect.getMaxValueBasedAttribute().getRelatedAttributes());
                }
            }
        }
        return new ArrayList<>(additionalAttributeSet);
    }

    private void fillBuffEffectCurveParam(BuffEffect buffEffect, CharacterAttribute characterAttribute) {
        TalentCurveDao talentCurveDao = knkDatabase.getTalentCurveDao();
        RefinementCurveDao refinementCurveDao = knkDatabase.getRefinementCurveDao();
        if(buffEffect.getMultiplierTalentCurve() != null) {
            List<TalentCurve> talentCurveList = talentCurveDao.selectByCurveID(buffEffect.getMultiplierTalentCurve());
            if(talentCurveList.size() != 1) {
                throw new MetaDataQueryException("talent_curve");
            }
            buffEffect.setMultiplierTalentCurveValue(talentCurveList.get(0).getValue(
                    Objects.requireNonNull(characterAttribute.getTalentLevel().get(buffEffect.getSourceTalent()))));
        }
        if(buffEffect.getMultiplierRefinementCurve() != null) {
            List<RefinementCurve> refinementCurveList = refinementCurveDao.selectByCurveID(buffEffect.getMultiplierRefinementCurve());
            if(refinementCurveList.size() != 1) {
                throw new MetaDataQueryException("refinement_curve");
            }
            buffEffect.setMultiplierRefinementCurveValue(refinementCurveList.get(0).getValue(
                    Objects.requireNonNull(characterAttribute.getWeaponRefinement())));
        }
        if(buffEffect.getMaxValueRefinementCurveValue() != null) {
            List<RefinementCurve> refinementCurveList = refinementCurveDao.selectByCurveID(buffEffect.getMaxValueRefinementCurve());
            if(refinementCurveList.size() != 1) {
                throw new MetaDataQueryException("refinement_curve");
            }
            buffEffect.setMaxValueRefinementCurveValue(refinementCurveList.get(0).getValue(
                    Objects.requireNonNull(characterAttribute.getWeaponRefinement())));
        }
    }

}
