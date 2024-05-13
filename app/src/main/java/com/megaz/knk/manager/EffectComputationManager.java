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
import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.BuffRangeEnum;
import com.megaz.knk.constant.BuffSourceEnum;
import com.megaz.knk.curve.CharacterBaseAttribute;
import com.megaz.knk.constant.EffectBaseAttributeEnum;
import com.megaz.knk.constant.EffectFieldEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.ElementReactionEnum;
import com.megaz.knk.constant.FightEffectEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.curve.WeaponBaseAttribute;
import com.megaz.knk.dao.ArtifactDexDao;
import com.megaz.knk.dao.BuffDao;
import com.megaz.knk.dao.BuffEffectRelationDao;
import com.megaz.knk.dao.CharacterDexDao;
import com.megaz.knk.dao.FightEffectComputationDao;
import com.megaz.knk.dao.PromoteAttributeDao;
import com.megaz.knk.dao.RefinementCurveDao;
import com.megaz.knk.dao.TalentCurveDao;
import com.megaz.knk.dao.WeaponDexDao;
import com.megaz.knk.dto.ArtifactProfileDto;
import com.megaz.knk.dto.CharacterProfileDto;
import com.megaz.knk.dto.WeaponProfileDto;
import com.megaz.knk.entity.ArtifactDex;
import com.megaz.knk.entity.Buff;
import com.megaz.knk.entity.BuffEffectRelation;
import com.megaz.knk.entity.CharacterDex;
import com.megaz.knk.entity.FightEffectComputation;
import com.megaz.knk.entity.PromoteAttribute;
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

    private final KnkDatabase knkDatabase;

    public EffectComputationManager(Context context) {
        this.knkDatabase = KnkDatabase.getKnkDatabase(context);
    }

    @WorkerThread
    public CharacterAttribute createCharacterBaseAttribute(CharacterProfileDto characterProfileDto) {
        CharacterDexDao characterDexDao = knkDatabase.getCharacterDexDao();
        WeaponDexDao weaponDexDao = knkDatabase.getWeaponDexDao();
        CharacterDex characterDex = queryCharacterDex(characterDexDao, characterProfileDto.getCharacterId());
        WeaponProfileDto weaponProfileDto = characterProfileDto.getWeapon();
        WeaponDex weaponDex = queryWeaponDex(weaponDexDao, weaponProfileDto.getWeaponId());

        CharacterAttribute characterAttribute = new CharacterAttribute(characterProfileDto);
        Map<AttributeEnum, String> baseCurveNames = new HashMap<>();
        baseCurveNames.put(AttributeEnum.BASE_ATK, characterDex.getCurveBaseAtk());
        baseCurveNames.put(AttributeEnum.BASE_HP, characterDex.getCurveBaseHp());
        baseCurveNames.put(AttributeEnum.BASE_DEF, characterDex.getCurveBaseDef());
        Map<AttributeEnum, Double> characterBaseAttributeMulti = CharacterBaseAttribute.getBaseAttributeMultiByLevel(characterProfileDto.getLevel(), baseCurveNames);
        Map<AttributeEnum, Double> characterBaseAttributeAdd = queryPromoteAttribute(characterDex.getPromoteId(), characterProfileDto.getPhase());
        Map<AttributeEnum, Double> weaponBaseAttributeMulti = WeaponBaseAttribute.getBaseAttributeMultiByLevel(weaponProfileDto.getLevel(), weaponDex.getCurveBaseAtk(), weaponDex.getCurveAttribute());
        Map<AttributeEnum, Double> weaponBaseAttributeAdd = queryPromoteAttribute(weaponDex.getPromoteId(), weaponProfileDto.getPhase());

        // set base (white) attribute
        characterAttribute.setBaseAtk(
                characterDex.getBaseAtk() * Objects.requireNonNull(characterBaseAttributeMulti.get(AttributeEnum.BASE_ATK))
                        + Objects.requireNonNull(characterBaseAttributeAdd.getOrDefault(AttributeEnum.BASE_ATK, 0.))
                        + weaponDex.getBaseAtk() * Objects.requireNonNull(weaponBaseAttributeMulti.get(AttributeEnum.BASE_ATK))
                        + Objects.requireNonNull(weaponBaseAttributeAdd.getOrDefault(AttributeEnum.BASE_ATK, 0.))
        );
        characterAttribute.setBaseHp(
                characterDex.getBaseHp() * Objects.requireNonNull(characterBaseAttributeMulti.get(AttributeEnum.BASE_HP))
                        + Objects.requireNonNull(characterBaseAttributeAdd.getOrDefault(AttributeEnum.BASE_HP, 0.))
        );
        characterAttribute.setBaseDef(
                characterDex.getBaseDef() * Objects.requireNonNull(characterBaseAttributeMulti.get(AttributeEnum.BASE_DEF))
                        + Objects.requireNonNull(characterBaseAttributeAdd.getOrDefault(AttributeEnum.BASE_DEF, 0.))
        );
        // add character attribute
        for (AttributeEnum attribute : characterBaseAttributeAdd.keySet()) {
            if (attribute != AttributeEnum.BASE_ATK && attribute != AttributeEnum.BASE_HP && attribute != AttributeEnum.BASE_DEF) {
                characterAttribute.addAttributeValue(attribute, Objects.requireNonNull(characterBaseAttributeAdd.get(attribute)));
            }
        }
        // add weapon attribute
        if (weaponDex.getAttribute() != null && weaponBaseAttributeMulti.containsKey(AttributeEnum.NULL)) {
            characterAttribute.addAttributeValue(weaponDex.getAttribute(),
                    weaponDex.getAttributeValue() * Objects.requireNonNull(weaponBaseAttributeMulti.get(AttributeEnum.NULL)));
        } else {
            if (!(weaponDex.getAttribute() == null && !weaponBaseAttributeMulti.containsKey(AttributeEnum.NULL))) {
                throw new MetaDataQueryException("weapon_dex");
            }
        }
        for (AttributeEnum attribute : weaponBaseAttributeAdd.keySet()) {
            if (attribute != AttributeEnum.BASE_ATK) {
                characterAttribute.addAttributeValue(attribute, Objects.requireNonNull(weaponBaseAttributeAdd.get(attribute)));
            }
        }
        // add artifacts attribute
        for (ArtifactPositionEnum position : GenshinConstantMeta.ARTIFACT_POSITION_LIST) {
            if (characterProfileDto.getArtifacts().containsKey(position)) {
                ArtifactProfileDto artifactProfileDto = characterProfileDto.getArtifacts().get(position);
                assert artifactProfileDto != null;
                characterAttribute.addAttributeValue(
                        artifactProfileDto.getMainAttribute(), artifactProfileDto.getMainAttributeVal()
                );
                for (int i = 0; i < artifactProfileDto.getSubAttributes().size(); i++) {
                    characterAttribute.addAttributeValue(
                            artifactProfileDto.getSubAttributes().get(i),
                            artifactProfileDto.getSubAttributesVal().get(i)
                    );
                }
            }
        }

        return characterAttribute;
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
        if (fightEffect instanceof DirectDamageEffect) {
            effectDetailVo.setEffectValue(String.format("%d/%d",
                    (int) (((DirectDamageEffect) fightEffect).getCriticalValue().doubleValue()),
                    (int) ((((DirectDamageEffect) fightEffect).getAverageValue().doubleValue()))));
        } else {
            if (fightEffect.getPercent()) {
                effectDetailVo.setEffectValue(String.format("%.2f", fightEffect.getValue() * 100) + "%");
            } else if (fightEffect.getValue() > 1000) {
                effectDetailVo.setEffectValue(String.format("%d", (int) (fightEffect.getValue().doubleValue())));
            } else {
                effectDetailVo.setEffectValue(String.format("%.2f", fightEffect.getValue()));
            }
        }
        effectDetailVo.setFieldDetail(fightEffect.getFieldDetail());
        return effectDetailVo;
    }

    @WorkerThread
    public BuffVo createBuffVo(BuffEffect buffEffect) {
        CharacterDexDao characterDexDao = knkDatabase.getCharacterDexDao();
        WeaponDexDao weaponDexDao = knkDatabase.getWeaponDexDao();
        ArtifactDexDao artifactDexDao = knkDatabase.getArtifactDexDao();
        BuffVo buffVo = new BuffVo();
        buffVo.setBuffId(buffEffect.getBuffId());
        buffVo.setBuffTitle(buffEffect.getSourceName() + "：" + buffEffect.getBuffName());
        buffVo.setBuffDesc(buffEffect.getDescription());
        buffVo.setSourceType(buffEffect.getSourceType());
        buffVo.setForced(buffEffect.getForced());
        if (buffEffect.getSourceType() == BuffSourceEnum.CHARACTER) {
            CharacterDex characterDex = queryCharacterDex(characterDexDao, buffEffect.getSourceId());
            buffVo.setIcon(characterDex.getIconAvatar());
            if (buffEffect.getConstellation() != null && buffEffect.getConstellation() > 0) {
                buffVo.setConstellation(buffEffect.getConstellation());
            }
        } else if (buffEffect.getSourceType() == BuffSourceEnum.WEAPON) {
            WeaponDex weaponDex = queryWeaponDex(weaponDexDao, buffEffect.getSourceId());
            buffVo.setIcon(weaponDex.getIconAwaken());
        } else if (buffEffect.getSourceType() == BuffSourceEnum.ARTIFACT_SET) {
            List<ArtifactDex> artifactDexList = artifactDexDao.selectBySetId(buffEffect.getSourceId());
            if (artifactDexList.size() <= 0) {
                throw new MetaDataQueryException("artifact_dex");
            }
            buffVo.setIcon(artifactDexList.get(0).getIcon());
        }
        buffVo.setEnabled(buffEffect.getEnabled());
        buffVo.setPercent(buffEffect.getPercent());
        if (buffEffect.getEnabled()) {
            buffVo.setEffectValue(buffEffect.getValue());
        }
        if (buffEffect.getEffectType() == FightEffectEnum.ATTRIBUTE_ADD ||
                buffEffect.getEffectType() == FightEffectEnum.DAMAGE_UP) {
            if (buffEffect.getIncreasedAttribute() == AttributeEnum.CRIT_RATE) {
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
        } else if (buffEffect.getEffectType() == FightEffectEnum.DEF_DOWN ||
                buffEffect.getEffectType() == FightEffectEnum.DEF_IGNORE) {
            buffVo.setBuffField(EffectFieldEnum.DEFENCE);
        } else if (buffEffect.getEffectType() == FightEffectEnum.REACTION_UP) {
            buffVo.setBuffField(EffectFieldEnum.REACTION);
        } else {
            throw new BuffNoFieldMatchedException(buffEffect.getBuffName());
        }
        if (buffEffect.getEffectType() == FightEffectEnum.DEF_DOWN ||
                buffEffect.getEffectType() == FightEffectEnum.DEF_IGNORE) {
            buffVo.setEffectText(buffEffect.getEffectType().getDesc());
        } else if (buffVo.getBuffField() == EffectFieldEnum.DAMAGE_UP) {
            buffVo.setEffectText(buffVo.getBuffField().getEffectText());
        } else if (buffEffect.getIncreasedAttribute() != null) {
            buffVo.setEffectText(buffEffect.getIncreasedAttribute().getDesc());
        } else {
            buffVo.setEffectText(buffVo.getBuffField().getEffectText());
        }
        buffVo.setBuffInputParamList(getInputParamOfBuff(buffEffect));
        return buffVo;
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
            fillBuffEffectCurveParam(buffEffect,
                    fightEffect.getCharacterAttribute().getTalentLevel().get(buffEffect.getSourceTalent()),
                    fightEffect.getCharacterAttribute().getWeaponRefinement());
        }
        if (buffInputParamList != null) {
            fillBuffEffectInputParam(buffEffect, buffInputParamList);
        }
        fightEffect.enableBuffEffect(buffEffect);
        fightEffect.updateWithEnabledBuffs();
    }

    public List<BuffInputParam> getInputParamOfBuff(BuffEffect buffEffect) {
        List<BuffInputParam> buffInputParamList = new ArrayList<>();
        if (buffEffect.getFromSelf()) {
            switch (buffEffect.getBuffId()) {
                // case "BC10000052-1":  // 神变·恶曜开眼不需要修改参数
                case "BC10000073-5":  // 净善摄受明论
                case "BC10000096-2": // 红死之宴
                case "BW12416-1":
                case "BW13416-1":
                case "BW15416-1":  // 驭浪的海祇民
                    buffInputParamList.add(new BuffInputParam(
                            buffEffect.getSpecialInput(), buffEffect.getBasedAttributeValue(),
                            false, true));
                    break;
            }
            return buffInputParamList;
        }
        if (buffEffect.getBasedAttribute() != null) {
            if (buffEffect.getBasedAttribute() == EffectBaseAttributeEnum.INPUT) {
                buffInputParamList.add(new BuffInputParam(
                        buffEffect.getSpecialInput(), buffEffect.getBasedAttributeValue(),
                        false, true));
            } else {
                String hint;
                if (buffEffect.getSourceType() == BuffSourceEnum.CHARACTER) {
                    hint = buffEffect.getSourceName() + "的" + buffEffect.getBasedAttribute().getDesc();
                } else {
                    hint = "装备者的" + buffEffect.getBasedAttribute().getDesc();
                }
                buffInputParamList.add(new BuffInputParam(hint, buffEffect.getBasedAttributeValue(),
                        buffEffect.getBasedAttribute().getPercent(), true));
            }
        }
        if (buffEffect.getBasedAttributeSecond() != null) {
            String hint;
            if (buffEffect.getSourceType() == BuffSourceEnum.CHARACTER) {
                hint = buffEffect.getSourceName() + "的" + buffEffect.getBasedAttributeSecond().getDesc();
            } else {
                hint = "装备者的" + buffEffect.getBasedAttributeSecond().getDesc();
            }
            buffInputParamList.add(new BuffInputParam(hint, buffEffect.getBasedAttributeSecondValue(),
                    buffEffect.getBasedAttributeSecond().getPercent(), true));
        }
        if (buffEffect.getMaxValueBasedAttribute() != null) {
            if (buffEffect.getMaxValueBasedAttribute() != buffEffect.getBasedAttribute() &&
                    buffEffect.getMaxValueBasedAttribute() != buffEffect.getBasedAttributeSecond()) {
                String hint;
                if (buffEffect.getSourceType() == BuffSourceEnum.CHARACTER) {
                    hint = buffEffect.getSourceName() + "的" + buffEffect.getMaxValueBasedAttribute().getDesc();
                } else {
                    hint = "装备者的" + buffEffect.getMaxValueBasedAttribute().getDesc();
                }
                buffInputParamList.add(new BuffInputParam(hint, buffEffect.getMaxValueBasedAttributeValue(),
                        buffEffect.getMaxValueBasedAttribute().getPercent(), true));
            }
        }
        if (buffEffect.getMultiplierTalentCurve() != null) {
            buffInputParamList.add(new BuffInputParam(
                    buffEffect.getSourceName() + "的" + buffEffect.getSourceTalent().getDesc() + "等级",
                    null, false, false, 15.));
        }
        if (buffEffect.getMultiplierRefinementCurve() != null ||
                buffEffect.getMaxValueRefinementCurve() != null) {
            buffInputParamList.add(new BuffInputParam(
                    buffEffect.getSourceName() + "的精炼等级",
                    null, false, false, 5.));
        }
        return buffInputParamList;
    }

    @WorkerThread
    private Map<AttributeEnum, Double> queryPromoteAttribute(String promoteId, Integer phase) {
        PromoteAttributeDao promoteAttributeDao = knkDatabase.getPromoteAttributeDao();

        Map<AttributeEnum, Double> attributeMap = new HashMap<>();
        List<PromoteAttribute> promoteAttributeList = promoteAttributeDao.selectByPromoteIdAndPhase(promoteId, phase);
        for(PromoteAttribute promoteAttribute:promoteAttributeList) {
            attributeMap.put(promoteAttribute.getAttribute(), promoteAttribute.getValue());
        }
        return attributeMap;
    }

    private void fillBuffEffectInputParam(BuffEffect buffEffect, List<BuffInputParam> buffInputParamList) {
        int cursor = 0;
        if (buffEffect.getBasedAttribute() != null &&
                (!buffEffect.getFromSelf() || buffEffect.getBasedAttribute() == EffectBaseAttributeEnum.INPUT)) {
            buffEffect.setBasedAttributeValue(buffInputParamList.get(cursor).getInputValue());
            cursor++;
        }
        if (buffEffect.getBasedAttributeSecond() != null  &&
                (!buffEffect.getFromSelf() || buffEffect.getBasedAttributeSecond() == EffectBaseAttributeEnum.INPUT)) {
            buffEffect.setBasedAttributeSecondValue(buffInputParamList.get(cursor).getInputValue());
            cursor++;
        }
        if (buffEffect.getMaxValueBasedAttribute() != null) {
            if (buffEffect.getMaxValueBasedAttribute() == buffEffect.getBasedAttribute()) {
                buffEffect.setMaxValueBasedAttributeValue(buffEffect.getBasedAttributeValue());
            } else if (buffEffect.getMaxValueBasedAttribute() == buffEffect.getBasedAttributeSecond()) {
                buffEffect.setMaxValueBasedAttributeValue(buffEffect.getBasedAttributeSecondValue());
            } else if (!buffEffect.getFromSelf() || buffEffect.getMaxValueBasedAttribute() == EffectBaseAttributeEnum.INPUT) {
                buffEffect.setMaxValueBasedAttributeValue(buffInputParamList.get(cursor).getInputValue());
                cursor++;
            }
        }
        Integer talentLevel = null;
        Integer refinementLevel = null;
        if (buffEffect.getMultiplierTalentCurve() != null && !buffEffect.getFromSelf()) {
            talentLevel = buffInputParamList.get(cursor).getInputValue().intValue();
            fillBuffEffectCurveParam(buffEffect, talentLevel, refinementLevel);
            cursor++;
        }
        if ((buffEffect.getMultiplierRefinementCurve() != null || buffEffect.getMaxValueRefinementCurve() != null)
                && !buffEffect.getFromSelf()) {
            refinementLevel = buffInputParamList.get(cursor).getInputValue().intValue();
            fillBuffEffectCurveParam(buffEffect, talentLevel, refinementLevel);
        }
    }

    private List<BuffEffect> checkAdditionalAttributeAddBuffs(FightEffect fightEffect, List<AttributeEnum> additionalAttributes) {
        BuffQueryCondition baseBuffQueryCondition = fightEffect.getBuffQueryCondition();
        BuffQueryCondition additionalBuffQueryCondition = new BuffQueryCondition();
        additionalBuffQueryCondition.addBuffTypes(Collections.singletonList(FightEffectEnum.ATTRIBUTE_ADD));
        additionalBuffQueryCondition.setDamageElement(baseBuffQueryCondition.getDamageElement());
        additionalBuffQueryCondition.setDamageLabel(baseBuffQueryCondition.getDamageLabel());
        additionalBuffQueryCondition.setElementReaction(baseBuffQueryCondition.getElementReaction());
        additionalBuffQueryCondition.addAddedAttributes(additionalAttributes);
        return queryBuffByCondition(fightEffect.getCharacterAttribute(),
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
        List<BuffEffect> buffEffectList = queryBuffByCondition(
                characterAttribute, fightEffect.getEffectId(), buffQueryCondition);
        // enable default buff
        List<AttributeEnum> queriedAttributes = buffQueryCondition.getAddedAttributes();
        List<AttributeEnum> additionalAttributes = getRelatedAttributesFromDefaultBuffs(buffEffectList);
        additionalAttributes.removeAll(queriedAttributes);
        // query additionally
        while (!additionalAttributes.isEmpty()) {
            BuffQueryCondition additionalBuffQueryCondition = new BuffQueryCondition();
            additionalBuffQueryCondition.addBuffTypes(Collections.singletonList(FightEffectEnum.ATTRIBUTE_ADD));
            additionalBuffQueryCondition.setDamageElement(buffQueryCondition.getDamageElement());
            additionalBuffQueryCondition.setDamageLabel(buffQueryCondition.getDamageLabel());
            additionalBuffQueryCondition.setElementReaction(buffQueryCondition.getElementReaction());
            additionalBuffQueryCondition.addAddedAttributes(additionalAttributes);
            List<BuffEffect> additionalBuffEffectList = queryBuffByCondition(characterAttribute, fightEffect.getEffectId(), additionalBuffQueryCondition);
            buffEffectList.addAll(additionalBuffEffectList);
            additionalAttributes = getRelatedAttributesFromDefaultBuffs(additionalBuffEffectList);
            additionalAttributes.removeAll(queriedAttributes);
            queriedAttributes.addAll(additionalAttributes);
        }
        // add buff effects to fight effect and enable default buffs
        fightEffect.addAvailableBuffEffects(buffEffectList);
        for (BuffEffect buffEffect : buffEffectList) {
            if (buffEffect.getDefaultEnabled() && buffEffect.getFromSelf()) {
                fillBuffEffectCurveParam(buffEffect,
                        characterAttribute.getTalentLevel().get(buffEffect.getSourceTalent()),
                        characterAttribute.getWeaponRefinement());
                fightEffect.enableBuffEffect(buffEffect);
            }
        }
    }

    @WorkerThread
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
                characterAttribute.getCharacterId(),
                characterAttribute.getPhase(), characterAttribute.getConstellation(),
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
        for (Buff buff : buffs) {
            if (forcedBuffIdSet.contains(buff.getBuffId())) {
                buffEffectList.add(createBuffEffect(buff, true, characterAttribute));
            } else {
                buffEffectList.add(createBuffEffect(buff, false, characterAttribute));
            }
        }
        return buffEffectList;
    }

    private BuffEffect createBuffEffect(Buff buff, Boolean force, CharacterAttribute characterAttribute) {
        BuffEffect buffEffect = new BuffEffect(buff);
        buffEffect.setForced(force);
        boolean selfFlag = false;
        if (buffEffect.getSourceType() == BuffSourceEnum.CHARACTER &&
                buffEffect.getSourceId().equals(characterAttribute.getCharacterId())) {
            if (buffEffect.getPhase() < -1 * characterAttribute.getPhase() ||
                    (buffEffect.getPhase() >= 0 && buffEffect.getPhase() <= characterAttribute.getPhase())) {
                if (buffEffect.getConstellation() < -1 * characterAttribute.getConstellation() ||
                        (buffEffect.getConstellation() >= 0 && buffEffect.getConstellation() <= characterAttribute.getConstellation())) {
                    selfFlag = true;
                }
            }
        }
        if (buffEffect.getSourceType() == BuffSourceEnum.WEAPON &&
                buffEffect.getSourceId().equals(characterAttribute.getWeaponId())) {
            selfFlag = true;
        }
        if (buffEffect.getSourceType() == BuffSourceEnum.ARTIFACT_SET &&
                characterAttribute.getArtifactSetCount().containsKey(buffEffect.getSourceId())) {
            if (buffEffect.getArtifactNum() <= Objects.requireNonNull(characterAttribute.getArtifactSetCount().get(buffEffect.getSourceId()))) {
                selfFlag = true;
            }
        }
        if (buffEffect.getBuffRange() == BuffRangeEnum.OTHERS) {
            selfFlag = false;
        }
        buffEffect.setFromSelf(selfFlag);
        return buffEffect;
    }

    private List<AttributeEnum> getRelatedAttributesFromDefaultBuffs(List<BuffEffect> buffEffectList) {
        Set<AttributeEnum> additionalAttributeSet = new HashSet<>();
        for (BuffEffect buffEffect : buffEffectList) {
            if (buffEffect.getDefaultEnabled() && buffEffect.getFromSelf()) {
                additionalAttributeSet.addAll(buffEffect.getRelatedAttributeSet());
            }
        }
        return new ArrayList<>(additionalAttributeSet);
    }

    @WorkerThread
    private void fillBuffEffectCurveParam(BuffEffect buffEffect, Integer talentLevel, Integer refinementLevel) {
        TalentCurveDao talentCurveDao = knkDatabase.getTalentCurveDao();
        RefinementCurveDao refinementCurveDao = knkDatabase.getRefinementCurveDao();
        if (buffEffect.getMultiplierTalentCurve() != null) {
            List<TalentCurve> talentCurveList = talentCurveDao.selectByCurveID(buffEffect.getMultiplierTalentCurve());
            if (talentCurveList.size() != 1) {
                throw new MetaDataQueryException("talent_curve");
            }
            buffEffect.setMultiplierTalentCurveValue(talentCurveList.get(0).getValue(
                    Objects.requireNonNull(talentLevel)));
        }
        if (buffEffect.getMultiplierRefinementCurve() != null) {
            List<RefinementCurve> refinementCurveList = refinementCurveDao.selectByCurveID(buffEffect.getMultiplierRefinementCurve());
            if (refinementCurveList.size() != 1) {
                throw new MetaDataQueryException("refinement_curve");
            }
            buffEffect.setMultiplierRefinementCurveValue(refinementCurveList.get(0).getValue(
                    Objects.requireNonNull(refinementLevel)));
        }
        if (buffEffect.getMaxValueRefinementCurve() != null) {
            List<RefinementCurve> refinementCurveList = refinementCurveDao.selectByCurveID(buffEffect.getMaxValueRefinementCurve());
            if (refinementCurveList.size() != 1) {
                throw new MetaDataQueryException("refinement_curve");
            }
            buffEffect.setMaxValueRefinementCurveValue(refinementCurveList.get(0).getValue(
                    Objects.requireNonNull(refinementLevel)));
        }
    }

    private CharacterDex queryCharacterDex(CharacterDexDao characterDexDao, String characterId) {
        List<CharacterDex> characterDexList = characterDexDao.selectByCharacterId(characterId);
        if (characterDexList.size() != 1) {
            throw new MetaDataQueryException("character_dex");
        }
        return characterDexList.get(0);
    }

    private WeaponDex queryWeaponDex(WeaponDexDao weaponDexDao, String weaponId) {
        List<WeaponDex> weaponDexList = weaponDexDao.selectByWeaponId(weaponId);
        if (weaponDexList.size() != 1) {
            throw new MetaDataQueryException("weapon_dex");
        }
        return weaponDexList.get(0);
    }

}
