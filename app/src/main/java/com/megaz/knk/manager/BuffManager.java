package com.megaz.knk.manager;

import android.content.Context;

import androidx.annotation.WorkerThread;

import com.megaz.knk.KnkDatabase;
import com.megaz.knk.computation.BuffEffect;
import com.megaz.knk.computation.BuffInputParam;
import com.megaz.knk.computation.BuffQueryCondition;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.BuffRangeEnum;
import com.megaz.knk.constant.BuffSourceEnum;
import com.megaz.knk.constant.EffectBaseAttributeEnum;
import com.megaz.knk.constant.EffectFieldEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.FightEffectEnum;
import com.megaz.knk.dao.ArtifactDexDao;
import com.megaz.knk.dao.BuffDao;
import com.megaz.knk.dao.BuffEffectRelationDao;
import com.megaz.knk.dao.CharacterDexDao;
import com.megaz.knk.dao.RefinementCurveDao;
import com.megaz.knk.dao.TalentCurveDao;
import com.megaz.knk.dao.WeaponDexDao;
import com.megaz.knk.entity.ArtifactDex;
import com.megaz.knk.entity.Buff;
import com.megaz.knk.entity.BuffEffectRelation;
import com.megaz.knk.entity.CharacterDex;
import com.megaz.knk.entity.RefinementCurve;
import com.megaz.knk.entity.TalentCurve;
import com.megaz.knk.entity.WeaponDex;
import com.megaz.knk.exception.BuffNoFieldMatchedException;
import com.megaz.knk.exception.MetaDataQueryException;
import com.megaz.knk.utils.MetaDataUtils;
import com.megaz.knk.vo.BuffVo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BuffManager {

    private final KnkDatabase knkDatabase;

    private Set<String> variableSelfBuffIdSet;

    public BuffManager(Context context) {
        knkDatabase = KnkDatabase.getKnkDatabase(context);
    }

    @WorkerThread
    public BuffVo createBuffVo(BuffEffect buffEffect, boolean enabled) {
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
            CharacterDex characterDex = MetaDataUtils.queryCharacterDex(characterDexDao, buffEffect.getSourceId());
            buffVo.setIcon(characterDex.getIconAvatar());
            if (buffEffect.getConstellation() != null && buffEffect.getConstellation() > 0) {
                buffVo.setConstellation(buffEffect.getConstellation());
            }
        } else if (buffEffect.getSourceType() == BuffSourceEnum.WEAPON) {
            WeaponDex weaponDex = MetaDataUtils.queryWeaponDex(weaponDexDao, buffEffect.getSourceId());
            buffVo.setIcon(weaponDex.getIconAwaken());
        } else if (buffEffect.getSourceType() == BuffSourceEnum.ARTIFACT_SET) {
            List<ArtifactDex> artifactDexList = artifactDexDao.selectBySetId(buffEffect.getSourceId());
            if (artifactDexList.size() <= 0) {
                throw new MetaDataQueryException("artifact_dex");
            }
            buffVo.setIcon(artifactDexList.get(0).getIcon());
        }
        buffVo.setPercent(buffEffect.getPercent());
        if (enabled) {
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

    @WorkerThread
    public List<BuffEffect> queryStaticBuff(CharacterAttribute characterAttribute) {
        BuffDao buffDao = knkDatabase.getBuffDao();
        List<Buff> buffs = new ArrayList<>();
        buffs.addAll(buffDao.selectStaticCharacterBuffByCondition(
                characterAttribute.getCharacterId(),
                characterAttribute.getPhase(), characterAttribute.getConstellation()));
        buffs.addAll(buffDao.selectStaticWeaponBuffByCondition(characterAttribute.getWeaponId()));
        for (Map.Entry<String, Integer> artifactSetEntry : characterAttribute.getArtifactSetCount().entrySet()) {
            buffs.addAll(buffDao.selectStaticArtifactBuffByCondition(
                    artifactSetEntry.getKey(), artifactSetEntry.getValue()));
        }
        // create buff effects
        List<BuffEffect> buffEffectList = new ArrayList<>();
        for (Buff buff : buffs) {
            buffEffectList.add(createBuffEffect(buff, true, characterAttribute));
        }
        return buffEffectList;
    }

    @WorkerThread
    public List<BuffEffect> queryBuffByCondition(CharacterAttribute characterAttribute,
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

    public List<AttributeEnum> getRelatedAttributesFromDefaultBuffs(List<BuffEffect> buffEffectList) {
        Set<AttributeEnum> additionalAttributeSet = new HashSet<>();
        for (BuffEffect buffEffect : buffEffectList) {
            if (buffEffect.getDefaultEnabled() && buffEffect.getFromSelf()) {
                additionalAttributeSet.addAll(buffEffect.getRelatedAttributeSet());
            }
        }
        return new ArrayList<>(additionalAttributeSet);
    }

    public void fillBuffEffectDefaultInputParam(BuffEffect buffEffect, CharacterAttribute characterAttribute) {
        double basedAttributeValue = 0.;
        switch (buffEffect.getBuffId()) {
            case "BC10000052-1":  // 神变·恶曜开眼
                basedAttributeValue = 90.;
                break;
            case "BC10000073-5":  // 净善摄受明论
                basedAttributeValue = characterAttribute.getMastery();
                break;
            case "BC10000096-2": // 红死之宴
                basedAttributeValue = 145.;
                break;
            case "BC10000095-4": // 细致入微的诊疗
                basedAttributeValue = 10000.;
                break;
            case "BC10000098-6": // 「铭记泪，生命与仁爱」
                basedAttributeValue = 100.;
                break;
            case "BW12416-1":
            case "BW13416-1":
            case "BW15416-1":  // 驭浪的海祇民
                basedAttributeValue = 320.;
                break;
        }
        buffEffect.setBasedAttributeValue(basedAttributeValue);
    }

    public void fillBuffEffectAttributeParam(BuffEffect buffEffect, CharacterAttribute characterAttribute) {
        if (buffEffect.getBasedAttribute() != null && buffEffect.getBasedAttribute() != EffectBaseAttributeEnum.INPUT) {
            buffEffect.setBasedAttributeValue(characterAttribute.getEffectBasedAttribute(buffEffect.getBasedAttribute()));
        }
        if (buffEffect.getBasedAttributeSecond() != null && buffEffect.getBasedAttribute() != EffectBaseAttributeEnum.INPUT) {
            buffEffect.setBasedAttributeSecondValue(characterAttribute.getEffectBasedAttribute(buffEffect.getBasedAttributeSecond()));
        }
        if (buffEffect.getMaxValueBasedAttribute() != null && buffEffect.getMaxValueBasedAttribute() != EffectBaseAttributeEnum.INPUT) {
            buffEffect.setMaxValueBasedAttributeValue(characterAttribute.getEffectBasedAttribute(buffEffect.getMaxValueBasedAttribute()));
        }
    }

    public List<BuffInputParam> getInputParamOfBuff(BuffEffect buffEffect) {
        List<BuffInputParam> buffInputParamList = new ArrayList<>();
        // 获取第一属性输入参数
        // 目前只支持特殊输入作为第一属性参数的情况
        if (buffEffect.getBasedAttribute() != null) {
            if (buffEffect.getBasedAttribute() == EffectBaseAttributeEnum.INPUT &&
                    (!buffEffect.getFromSelf() || hasVariableInput(buffEffect.getBuffId()))) { // 即使是来源自身的buff，特殊输入的参数也可能需要修改
                buffInputParamList.add(new BuffInputParam(
                        buffEffect.getSpecialInput(), buffEffect.getBasedAttributeValue(),
                        false, true));
            } else if (!buffEffect.getFromSelf() && buffEffect.getBasedAttribute() != EffectBaseAttributeEnum.INPUT){
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
        // 获取第二属性输入参数
        if (!buffEffect.getFromSelf() && buffEffect.getBasedAttributeSecond() != null) {
            String hint;
            if (buffEffect.getSourceType() == BuffSourceEnum.CHARACTER) {
                hint = buffEffect.getSourceName() + "的" + buffEffect.getBasedAttributeSecond().getDesc();
            } else {
                hint = "装备者的" + buffEffect.getBasedAttributeSecond().getDesc();
            }
            buffInputParamList.add(new BuffInputParam(hint, buffEffect.getBasedAttributeSecondValue(),
                    buffEffect.getBasedAttributeSecond().getPercent(), true));
        }
        // 获取上限值第一属性输入参数
        if (!buffEffect.getFromSelf() && buffEffect.getMaxValueBasedAttribute() != null) {
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
        // 获取天赋等级输入参数
        if (!buffEffect.getFromSelf() && buffEffect.getMultiplierTalentCurve() != null) {
            buffInputParamList.add(new BuffInputParam(
                    buffEffect.getSourceName() + "的" + buffEffect.getSourceTalent().getDesc() + "等级",
                    null, false, false, 15.));
        }
        // 获取精炼等阶输入参数
        if (!buffEffect.getFromSelf() && buffEffect.getMultiplierRefinementCurve() != null ||
                buffEffect.getMaxValueRefinementCurve() != null) {
            buffInputParamList.add(new BuffInputParam(
                    buffEffect.getSourceName() + "的精炼等级",
                    null, false, false, 5.));
        }
        return buffInputParamList;
    }

    @WorkerThread
    public void fillBuffEffectInputParam(BuffEffect buffEffect, List<BuffInputParam> buffInputParamList) {
        int cursor = 0;
        if (buffEffect.getBasedAttribute() != null &&
                (!buffEffect.getFromSelf() || hasVariableInput(buffEffect.getBuffId()))) { // 即使是来源自身的buff，特殊输入的参数也可能需要修改
            buffEffect.setBasedAttributeValue(buffInputParamList.get(cursor).getInputValue());
            cursor++;
        }
        if (!buffEffect.getFromSelf() && buffEffect.getBasedAttributeSecond() != null) {
            buffEffect.setBasedAttributeSecondValue(buffInputParamList.get(cursor).getInputValue());
            cursor++;
        }
        if (!buffEffect.getFromSelf() && buffEffect.getMaxValueBasedAttribute() != null) {
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

    @WorkerThread
    public void fillBuffEffectCurveParam(BuffEffect buffEffect, Integer talentLevel, Integer refinementLevel) {
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

    private synchronized boolean hasVariableInput(String buffId) {
        if (variableSelfBuffIdSet == null) {
            variableSelfBuffIdSet = new HashSet<>(Arrays.asList(
                    // "BC10000052-1":  // 神变·恶曜开眼不需要修改参数
                    "BC10000073-5",  // 净善摄受明论
                    "BC10000096-2",  // 红死之宴
                    "BC10000095-4",  // 细致入微的诊疗
                    "BC10000098-6", // 「铭记泪，生命与仁爱」
                    "BW12416-1",
                    "BW13416-1",
                    "BW15416-1"  // 驭浪的海祇民
            ));
        }
        return variableSelfBuffIdSet.contains(buffId);
    }

}
