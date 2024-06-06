package com.megaz.knk.manager;

import android.content.Context;

import com.megaz.knk.KnkDatabase;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.computation.CharacterOverview;
import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.AttributeLabelEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.dao.ArtifactCriterionDao;
import com.megaz.knk.dto.ArtifactProfileDto;
import com.megaz.knk.dto.CharacterProfileDto;
import com.megaz.knk.entity.ArtifactCriterion;
import com.megaz.knk.utils.ProfileConvertUtils;
import com.megaz.knk.vo.ArtifactEvaluationVo;
import com.megaz.knk.vo.ArtifactProfileVo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ArtifactEvaluateManager {
    private static final List<AttributeEnum> FLOWER_ATTRIBUTE_CHOICES = Collections.singletonList(AttributeEnum.HP_PLUS);
    private static final List<AttributeEnum> PLUME_ATTRIBUTE_CHOICES = Collections.singletonList(AttributeEnum.ATK_PLUS);
    private static final List<AttributeEnum> SANDS_ATTRIBUTE_CHOICES = Arrays.asList(
            AttributeEnum.ATK_PRCT, AttributeEnum.HP_PRCT, AttributeEnum.DEF_PRCT, AttributeEnum.MASTERY, AttributeEnum.RECHARGE);
    private static final List<AttributeEnum> GOBLET_ATTRIBUTE_CHOICES = Arrays.asList(
            AttributeEnum.ATK_PRCT, AttributeEnum.HP_PRCT, AttributeEnum.DEF_PRCT, AttributeEnum.MASTERY,
            AttributeEnum.DMG_PHY, AttributeEnum.DMG_PYRO, AttributeEnum.DMG_ELECTRO, AttributeEnum.DMG_HYDRO,
            AttributeEnum.DMG_DENDRO, AttributeEnum.DMG_ANEMO, AttributeEnum.DMG_GEO, AttributeEnum.DMG_CRYO);
    private static final List<AttributeEnum> CIRCLET_ATTRIBUTE_CHOICES = Arrays.asList(
            AttributeEnum.ATK_PRCT, AttributeEnum.HP_PRCT, AttributeEnum.DEF_PRCT, AttributeEnum.MASTERY,
            AttributeEnum.CRIT_RATE, AttributeEnum.CRIT_DMG, AttributeEnum.HEAL);

    private static final Map<ArtifactPositionEnum, List<AttributeEnum>> MAIN_ATTRIBUTE_CHOICES = new HashMap<>();

    static {
        MAIN_ATTRIBUTE_CHOICES.put(ArtifactPositionEnum.FLOWER, FLOWER_ATTRIBUTE_CHOICES);
        MAIN_ATTRIBUTE_CHOICES.put(ArtifactPositionEnum.PLUME, PLUME_ATTRIBUTE_CHOICES);
        MAIN_ATTRIBUTE_CHOICES.put(ArtifactPositionEnum.SANDS, SANDS_ATTRIBUTE_CHOICES);
        MAIN_ATTRIBUTE_CHOICES.put(ArtifactPositionEnum.GOBLET, GOBLET_ATTRIBUTE_CHOICES);
        MAIN_ATTRIBUTE_CHOICES.put(ArtifactPositionEnum.CIRCLET, CIRCLET_ATTRIBUTE_CHOICES);
    }

    private static final List<AttributeEnum> SUB_ATTRIBUTE_CHOICES = Arrays.asList(
            AttributeEnum.ATK_PRCT, AttributeEnum.HP_PRCT, AttributeEnum.DEF_PRCT,
            AttributeEnum.ATK_PLUS, AttributeEnum.HP_PLUS, AttributeEnum.DEF_PLUS,
            AttributeEnum.MASTERY, AttributeEnum.RECHARGE, AttributeEnum.CRIT_RATE, AttributeEnum.CRIT_DMG);

    private static final List<AttributeEnum> ATTRIBUTE_LIST = Arrays.asList(
            AttributeEnum.ATK_PRCT, AttributeEnum.HP_PRCT, AttributeEnum.DEF_PRCT,
            AttributeEnum.ATK_PLUS, AttributeEnum.HP_PLUS, AttributeEnum.DEF_PLUS, AttributeEnum.HEAL,
            AttributeEnum.MASTERY, AttributeEnum.RECHARGE, AttributeEnum.CRIT_RATE, AttributeEnum.CRIT_DMG,
            AttributeEnum.DMG_PHY, AttributeEnum.DMG_PYRO, AttributeEnum.DMG_ELECTRO, AttributeEnum.DMG_HYDRO,
            AttributeEnum.DMG_DENDRO, AttributeEnum.DMG_ANEMO, AttributeEnum.DMG_GEO, AttributeEnum.DMG_CRYO);
    public static final Double MAX_NORMALIZED_SCORE = 66.6; // 单件最大得分，用于评分归一化
    public static final Double ITEM_SCORE_WEIGHT_100 = 7.77; // 权重为100的词条单词条得分
    public static final Double MAIN_SCORE_FACTOR = 0.25; // 沙杯头主词条得分系数
    public static final Integer MAIN_ITEM_COUNT = 8; // 沙杯头主词条等价副词条数量

    private Context context;
    private KnkDatabase knkDatabase;
    private CharacterAttributeManager characterAttributeManager;

    public ArtifactEvaluateManager(Context context) {
        this.context = context;
        knkDatabase = KnkDatabase.getKnkDatabase(context);
        characterAttributeManager = new CharacterAttributeManager(context);
    }

    public List<ArtifactEvaluationVo> evaluateArtifacts(CharacterOverview characterOverview, Map<ArtifactPositionEnum, ArtifactProfileDto> artifacts) {
        ArtifactCriterionDao artifactCriterionDao = knkDatabase.getArtifactCriterionDao();
        List<ArtifactCriterion> criteria = artifactCriterionDao.selectByCharacterId(characterOverview.getCharacterId());
        List<ArtifactEvaluationVo> evaluationList = new ArrayList<>();
        CharacterAttribute characterAttribute = characterAttributeManager.createCharacterBaseAttribute(characterOverview, artifacts);
        for (ArtifactCriterion criterion : criteria) {
            evaluationList.add(evaluateWithCriterion(
                    characterAttribute,
                    artifacts, criterion));
        }
        return evaluationList;
    }

    @Deprecated
    public List<ArtifactEvaluationVo> evaluateArtifacts(CharacterProfileDto characterProfileDto) {
        ArtifactCriterionDao artifactCriterionDao = knkDatabase.getArtifactCriterionDao();
        List<ArtifactCriterion> criteria = artifactCriterionDao.selectByCharacterId(characterProfileDto.getCharacterId());
        List<ArtifactEvaluationVo> evaluationList = new ArrayList<>();
        for (ArtifactCriterion criterion : criteria) {
            evaluationList.add(evaluateWithCriterion(
                    characterAttributeManager.createCharacterBaseAttribute(
                            ProfileConvertUtils.extractCharacterOverview(characterProfileDto), characterProfileDto.getArtifacts()),
                    characterProfileDto.getArtifacts(), criterion));
        }
        return evaluationList;
    }

    private ArtifactEvaluationVo evaluateWithCriterion(
            CharacterAttribute characterAttribute, Map<ArtifactPositionEnum, ArtifactProfileDto> artifacts,
            ArtifactCriterion criterion) {
        ArtifactEvaluationVo artifactEvaluationVo = new ArtifactEvaluationVo();
        artifactEvaluationVo.setCriterionName(criterion.getCriterionName());
        Map<AttributeLabelEnum, Integer> attributeWeightMap = new HashMap<>();
        attributeWeightMap.put(AttributeLabelEnum.ATK, criterion.getAtkWeight());
        attributeWeightMap.put(AttributeLabelEnum.HP, criterion.getHpWeight());
        attributeWeightMap.put(AttributeLabelEnum.DEF, criterion.getDefWeight());
        attributeWeightMap.put(AttributeLabelEnum.MASTERY, criterion.getMasteryWeight());
        attributeWeightMap.put(AttributeLabelEnum.CR, criterion.getCrWeight());
        attributeWeightMap.put(AttributeLabelEnum.CD, criterion.getCdWeight());
        attributeWeightMap.put(AttributeLabelEnum.RECHARGE, criterion.getRechargeWeight());
        attributeWeightMap.put(AttributeLabelEnum.DMG, criterion.getDmgWeight());
        attributeWeightMap.put(AttributeLabelEnum.PHY, criterion.getPhyWeight());
        attributeWeightMap.put(AttributeLabelEnum.HEAL, criterion.getHealWeight());
        artifactEvaluationVo.setAttributeWeight(attributeWeightMap);

        double totalScore = 0.;
        // 计算单位词条得分
        Map<AttributeEnum, Double> scorePerItem = new HashMap<>();
        for (AttributeEnum attribute : ATTRIBUTE_LIST) {
            Integer attributeWeight = artifactEvaluationVo.getAttributeWeight().get(attribute.getLabel());
            assert attributeWeight != null;
            if (attribute.getLabel() == AttributeLabelEnum.DMG) {
                if (attribute.getElement() == characterAttribute.getElement()) {
                    scorePerItem.put(attribute, ITEM_SCORE_WEIGHT_100 * attributeWeight / 100);
                } else {
                    scorePerItem.put(attribute, 0.);
                }
            } else if (attribute == AttributeEnum.ATK_PLUS) {
                scorePerItem.put(attribute, attribute.getUnitValue() / characterAttribute.getBaseAtk()
                        / AttributeEnum.ATK_PRCT.getUnitValue() * ITEM_SCORE_WEIGHT_100 * attributeWeight / 100);
            } else if (attribute == AttributeEnum.HP_PLUS) {
                scorePerItem.put(attribute, attribute.getUnitValue() / characterAttribute.getBaseHp()
                        / AttributeEnum.HP_PRCT.getUnitValue() * ITEM_SCORE_WEIGHT_100 * attributeWeight / 100);
            } else if (attribute == AttributeEnum.DEF_PLUS) {
                scorePerItem.put(attribute, attribute.getUnitValue() / characterAttribute.getBaseDef()
                        / AttributeEnum.DEF_PRCT.getUnitValue() * ITEM_SCORE_WEIGHT_100 * attributeWeight / 100);
            } else {
                scorePerItem.put(attribute, ITEM_SCORE_WEIGHT_100 * attributeWeight / 100);
            }
        }
        // 按照单位词条收益排序
        List<AttributeEnum> sortedSubAttributeList = new ArrayList<>(SUB_ATTRIBUTE_CHOICES);
        sortedSubAttributeList.sort(new Comparator<AttributeEnum>() {
            @Override
            public int compare(AttributeEnum o1, AttributeEnum o2) {
                return Objects.requireNonNull(scorePerItem.get(o2)).compareTo(Objects.requireNonNull(scorePerItem.get(o1)));
            }
        });
        artifactEvaluationVo.setArtifactsScore(new HashMap<>());
        artifactEvaluationVo.setArtifactsMainAttrScore(new HashMap<>());
        artifactEvaluationVo.setArtifactsSubAttrScore(new HashMap<>());
        for (ArtifactPositionEnum position : GenshinConstantMeta.ARTIFACT_POSITION_LIST) {
            if (!artifacts.containsKey(position)) {
                continue;
            }
            ArtifactProfileDto artifactProfileDto = artifacts.get(position);
            assert artifactProfileDto != null;
            // 计算部件最大分数
            double maxScore = 0.;
            for (AttributeEnum mainAttribute : Objects.requireNonNull(MAIN_ATTRIBUTE_CHOICES.get(position))) {
                double choiceScore = 0.;
                if (position == ArtifactPositionEnum.SANDS || position == ArtifactPositionEnum.GOBLET || position == ArtifactPositionEnum.CIRCLET) {
                    choiceScore += MAIN_SCORE_FACTOR * MAIN_ITEM_COUNT * Objects.requireNonNull(scorePerItem.get(mainAttribute));
                }
                int subItemCount = 4;
                for (AttributeEnum subAttribute : sortedSubAttributeList) {
                    if (subAttribute == mainAttribute) {
                        continue;
                    }
                    if (subItemCount <= 0) {
                        break;
                    }
                    if (subItemCount == 4) {
                        choiceScore += 6 * Objects.requireNonNull(scorePerItem.get(subAttribute));
                    } else {
                        choiceScore += Objects.requireNonNull(scorePerItem.get(subAttribute));
                    }
                    subItemCount--;
                }
                maxScore = Math.max(maxScore, choiceScore);
            }
            assert maxScore > 0;
            // 计算部件实际分数
            double pieceScore = 0.;
            double mainAttributeScore = 0.;
            if (position == ArtifactPositionEnum.SANDS || position == ArtifactPositionEnum.GOBLET || position == ArtifactPositionEnum.CIRCLET) {
                AttributeEnum mainAttribute = artifactProfileDto.getMainAttribute();
                double mainAttributeValue = artifactProfileDto.getMainAttributeVal();
                mainAttributeScore = MAIN_SCORE_FACTOR * Objects.requireNonNull(scorePerItem.get(mainAttribute)) * mainAttributeValue / mainAttribute.getUnitValue();
                pieceScore += mainAttributeScore;
            }
            List<Double> subAttributesScore = new ArrayList<>();
            for (int i = 0; i < artifactProfileDto.getSubAttributes().size(); i++) {
                AttributeEnum subAttribute = artifactProfileDto.getSubAttributes().get(i);
                double subAttributeValue = artifactProfileDto.getSubAttributesVal().get(i);
                double subAttributeScore = Objects.requireNonNull(scorePerItem.get(subAttribute)) * subAttributeValue / subAttribute.getUnitValue();
                subAttributesScore.add(subAttributeScore);
                pieceScore += subAttributeScore;
            }
            pieceScore = pieceScore / maxScore * MAX_NORMALIZED_SCORE; // 归一化
            artifactEvaluationVo.getArtifactsScore().put(position, pieceScore);
            artifactEvaluationVo.getArtifactsMainAttrScore().put(position, mainAttributeScore);
            artifactEvaluationVo.getArtifactsSubAttrScore().put(position, subAttributesScore);
            totalScore += pieceScore;
        }
        //
        artifactEvaluationVo.setTotalScore(totalScore);
        return artifactEvaluationVo;
    }

    @Deprecated
    private ArtifactEvaluationVo evaluateWithCriterion(CharacterProfileDto characterProfileDto, ArtifactCriterion criterion) {
        ArtifactEvaluationVo artifactEvaluationVo = new ArtifactEvaluationVo();
        artifactEvaluationVo.setCriterionName(criterion.getCriterionName());
        Map<AttributeLabelEnum, Integer> attributeWeightMap = new HashMap<>();
        attributeWeightMap.put(AttributeLabelEnum.ATK, criterion.getAtkWeight());
        attributeWeightMap.put(AttributeLabelEnum.HP, criterion.getHpWeight());
        attributeWeightMap.put(AttributeLabelEnum.DEF, criterion.getDefWeight());
        attributeWeightMap.put(AttributeLabelEnum.MASTERY, criterion.getMasteryWeight());
        attributeWeightMap.put(AttributeLabelEnum.CR, criterion.getCrWeight());
        attributeWeightMap.put(AttributeLabelEnum.CD, criterion.getCdWeight());
        attributeWeightMap.put(AttributeLabelEnum.RECHARGE, criterion.getRechargeWeight());
        attributeWeightMap.put(AttributeLabelEnum.DMG, criterion.getDmgWeight());
        attributeWeightMap.put(AttributeLabelEnum.PHY, criterion.getPhyWeight());
        attributeWeightMap.put(AttributeLabelEnum.HEAL, criterion.getHealWeight());
        artifactEvaluationVo.setAttributeWeight(attributeWeightMap);

        double totalScore = 0.;
        // 计算单位词条得分
        Map<AttributeEnum, Double> scorePerItem = new HashMap<>();
        for (AttributeEnum attribute : ATTRIBUTE_LIST) {
            Integer attributeWeight = artifactEvaluationVo.getAttributeWeight().get(attribute.getLabel());
            assert attributeWeight != null;
            if (attribute.getLabel() == AttributeLabelEnum.DMG) {
                if (attribute.getElement() == characterProfileDto.getElement()) {
                    scorePerItem.put(attribute, ITEM_SCORE_WEIGHT_100 * attributeWeight / 100);
                } else {
                    scorePerItem.put(attribute, 0.);
                }
            } else if (attribute == AttributeEnum.ATK_PLUS) {
                scorePerItem.put(attribute, attribute.getUnitValue() / characterProfileDto.getBaseAtk()
                        / AttributeEnum.ATK_PRCT.getUnitValue() * ITEM_SCORE_WEIGHT_100 * attributeWeight / 100);
            } else if (attribute == AttributeEnum.HP_PLUS) {
                scorePerItem.put(attribute, attribute.getUnitValue() / characterProfileDto.getBaseHp()
                        / AttributeEnum.HP_PRCT.getUnitValue() * ITEM_SCORE_WEIGHT_100 * attributeWeight / 100);
            } else if (attribute == AttributeEnum.DEF_PLUS) {
                scorePerItem.put(attribute, attribute.getUnitValue() / characterProfileDto.getBaseDef()
                        / AttributeEnum.DEF_PRCT.getUnitValue() * ITEM_SCORE_WEIGHT_100 * attributeWeight / 100);
            } else {
                scorePerItem.put(attribute, ITEM_SCORE_WEIGHT_100 * attributeWeight / 100);
            }
        }
        // 按照单位词条收益排序
        List<AttributeEnum> sortedSubAttributeList = new ArrayList<>(SUB_ATTRIBUTE_CHOICES);
        sortedSubAttributeList.sort(new Comparator<AttributeEnum>() {
            @Override
            public int compare(AttributeEnum o1, AttributeEnum o2) {
                return Objects.requireNonNull(scorePerItem.get(o2)).compareTo(Objects.requireNonNull(scorePerItem.get(o1)));
            }
        });
        artifactEvaluationVo.setArtifactsScore(new HashMap<>());
        artifactEvaluationVo.setArtifactsMainAttrScore(new HashMap<>());
        artifactEvaluationVo.setArtifactsSubAttrScore(new HashMap<>());
        for (ArtifactPositionEnum position : GenshinConstantMeta.ARTIFACT_POSITION_LIST) {
            if (!characterProfileDto.getArtifacts().containsKey(position)) {
                continue;
            }
            ArtifactProfileDto artifactProfileDto = characterProfileDto.getArtifacts().get(position);
            assert artifactProfileDto != null;
            // 计算部件最大分数
            double maxScore = 0.;
            for (AttributeEnum mainAttribute : Objects.requireNonNull(MAIN_ATTRIBUTE_CHOICES.get(position))) {
                double choiceScore = 0.;
                if (position == ArtifactPositionEnum.SANDS || position == ArtifactPositionEnum.GOBLET || position == ArtifactPositionEnum.CIRCLET) {
                    choiceScore += MAIN_SCORE_FACTOR * MAIN_ITEM_COUNT * Objects.requireNonNull(scorePerItem.get(mainAttribute));
                }
                int subItemCount = 4;
                for (AttributeEnum subAttribute : sortedSubAttributeList) {
                    if (subAttribute == mainAttribute) {
                        continue;
                    }
                    if (subItemCount <= 0) {
                        break;
                    }
                    if (subItemCount == 4) {
                        choiceScore += 6 * Objects.requireNonNull(scorePerItem.get(subAttribute));
                    } else {
                        choiceScore += Objects.requireNonNull(scorePerItem.get(subAttribute));
                    }
                    subItemCount--;
                }
                maxScore = Math.max(maxScore, choiceScore);
            }
            assert maxScore > 0;
            // 计算部件实际分数
            double pieceScore = 0.;
            double mainAttributeScore = 0.;
            if (position == ArtifactPositionEnum.SANDS || position == ArtifactPositionEnum.GOBLET || position == ArtifactPositionEnum.CIRCLET) {
                AttributeEnum mainAttribute = artifactProfileDto.getMainAttribute();
                double mainAttributeValue = artifactProfileDto.getMainAttributeVal();
                mainAttributeScore = MAIN_SCORE_FACTOR * Objects.requireNonNull(scorePerItem.get(mainAttribute)) * mainAttributeValue / mainAttribute.getUnitValue();
                pieceScore += mainAttributeScore;
            }
            List<Double> subAttributesScore = new ArrayList<>();
            for (int i = 0; i < artifactProfileDto.getSubAttributes().size(); i++) {
                AttributeEnum subAttribute = artifactProfileDto.getSubAttributes().get(i);
                double subAttributeValue = artifactProfileDto.getSubAttributesVal().get(i);
                double subAttributeScore = Objects.requireNonNull(scorePerItem.get(subAttribute)) * subAttributeValue / subAttribute.getUnitValue();
                subAttributesScore.add(subAttributeScore);
                pieceScore += subAttributeScore;
            }
            pieceScore = pieceScore / maxScore * MAX_NORMALIZED_SCORE; // 归一化
            artifactEvaluationVo.getArtifactsScore().put(position, pieceScore);
            artifactEvaluationVo.getArtifactsMainAttrScore().put(position, mainAttributeScore);
            artifactEvaluationVo.getArtifactsSubAttrScore().put(position, subAttributesScore);
            totalScore += pieceScore;
        }
        //
        artifactEvaluationVo.setTotalScore(totalScore);
        return artifactEvaluationVo;
    }
}
