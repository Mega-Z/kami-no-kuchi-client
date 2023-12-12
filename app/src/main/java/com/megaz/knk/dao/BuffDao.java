package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.BuffRangeEnum;
import com.megaz.knk.constant.BuffSourceEnum;
import com.megaz.knk.constant.DamageLabelEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.ElementReactionEnum;
import com.megaz.knk.constant.FightEffectEnum;
import com.megaz.knk.entity.Buff;

import java.util.List;

@Dao
public interface BuffDao extends MetaDataDao<Buff> {

    @Insert
    void batchInsert(Buff... buffs);

    @Query("DELETE FROM buff")
    int deleteAll();

    @Query("SELECT * FROM buff WHERE buff_id IN (:buffIds) AND buff_range='EFFECT'" +
            "AND (phase < -1 * :phase OR phase >= 0 AND phase <= :phase ) " +
            "AND (constellation < -1 * :constellation OR constellation >= 0 AND constellation <= :constellation) " +
            "AND effect_type IN (:effectTypes) " +
            "AND (increased_attribute IN (:increasedAttributes) OR increased_attribute IS NULL) " +
            "AND (element=:element OR element IS NULL) " +
            "AND (increased_damage_label=:damageLabel OR increased_damage_label IS NULL) " +
            "AND (element_reaction=:elementReaction OR element_reaction IS NULL)")
    List<Buff> selectEffectRangedBuffByCondition(List<String> buffIds,
                                                 Integer phase, Integer constellation,
                                                 List<FightEffectEnum> effectTypes,
                                                 List<AttributeEnum> increasedAttributes,
                                                 ElementEnum element, DamageLabelEnum damageLabel,
                                                 ElementReactionEnum elementReaction);

    @Query("SELECT * FROM buff WHERE source_type='CHARACTER' AND source_id=:sourceId AND buff_range='CHARACTER'" +
            "AND (phase < -1 * :phase OR phase >= 0 AND phase <= :phase ) " +
            "AND (constellation < -1 * :constellation OR constellation >= 0 AND constellation <= :constellation) " +
            "AND effect_type IN (:effectTypes) " +
            "AND (increased_attribute IN (:increasedAttributes) OR increased_attribute IS NULL) " +
            "AND (element=:element OR element IS NULL) " +
            "AND (increased_damage_label=:damageLabel OR increased_damage_label IS NULL) " +
            "AND (element_reaction=:elementReaction OR element_reaction IS NULL)")
    List<Buff> selectCharacterRangedCharacterBuffByCondition(String sourceId,
                                                             Integer phase, Integer constellation,
                                                             List<FightEffectEnum> effectTypes,
                                                             List<AttributeEnum> increasedAttributes,
                                                             ElementEnum element, DamageLabelEnum damageLabel,
                                                             ElementReactionEnum elementReaction);

    @Query("SELECT * FROM buff WHERE source_type='WEAPON' AND source_id=:sourceId AND buff_range='CHARACTER'" +
            "AND effect_type IN (:effectTypes) " +
            "AND (increased_attribute IN (:increasedAttributes) OR increased_attribute IS NULL) " +
            "AND (element=:element OR element IS NULL) " +
            "AND (increased_damage_label=:damageLabel OR increased_damage_label IS NULL) " +
            "AND (element_reaction=:elementReaction OR element_reaction IS NULL)")
    List<Buff> selectCharacterRangedWeaponBuffByCondition(String sourceId,
                                                          List<FightEffectEnum> effectTypes,
                                                          List<AttributeEnum> increasedAttributes,
                                                          ElementEnum element, DamageLabelEnum damageLabel,
                                                          ElementReactionEnum elementReaction);

    @Query("SELECT * FROM buff WHERE source_type='ARTIFACT_SET' AND source_id=:sourceId AND buff_range='CHARACTER'" +
            "AND (artifact_num <= :artifactNum ) " +
            "AND effect_type IN (:effectTypes) " +
            "AND (increased_attribute IN (:increasedAttributes) OR increased_attribute IS NULL) " +
            "AND (element=:element OR element IS NULL) " +
            "AND (increased_damage_label=:damageLabel OR increased_damage_label IS NULL) " +
            "AND (element_reaction=:elementReaction OR element_reaction IS NULL)")
    List<Buff> selectCharacterRangedArtifactBuffByCondition(String sourceId, Integer artifactNum,
                                                            List<FightEffectEnum> effectTypes,
                                                            List<AttributeEnum> increasedAttributes,
                                                            ElementEnum element, DamageLabelEnum damageLabel,
                                                            ElementReactionEnum elementReaction);

    // 相同角色但命座或者等阶不符合条件的buff应排除
    @Query("SELECT * FROM buff WHERE buff_range='PARTY'" +
            "AND ((NOT (source_type='CHARACTER' AND source_id=:characterId)) " +
            "OR ((phase < -1 * :phase OR phase >= 0 AND phase <= :phase ) " +
            "AND (constellation < -1 * :constellation OR constellation >= 0 AND constellation <= :constellation)))" +
            "AND effect_type IN (:effectTypes) " +
            "AND (increased_attribute IN (:increasedAttributes) OR increased_attribute IS NULL) " +
            "AND (element=:element OR element IS NULL) " +
            "AND (increased_damage_label=:damageLabel OR increased_damage_label IS NULL) " +
            "AND (element_reaction=:elementReaction OR element_reaction IS NULL)")
    List<Buff> selectPartyRangedBuffByCondition(String characterId,
                                                Integer phase, Integer constellation,
                                                List<FightEffectEnum> effectTypes,
                                                List<AttributeEnum> increasedAttributes,
                                                ElementEnum element, DamageLabelEnum damageLabel,
                                                ElementReactionEnum elementReaction);

    @Query("SELECT * FROM buff WHERE (NOT (source_type='CHARACTER' AND source_id=:characterId)) AND buff_range='OTHERS'" +
            "AND effect_type IN (:effectTypes) " +
            "AND (increased_attribute IN (:increasedAttributes) OR increased_attribute IS NULL) " +
            "AND (element=:element OR element IS NULL) " +
            "AND (increased_damage_label=:damageLabel OR increased_damage_label IS NULL) " +
            "AND (element_reaction=:elementReaction OR element_reaction IS NULL)")
    List<Buff> selectOthersRangedBuffByCondition(String characterId,
                                                List<FightEffectEnum> effectTypes,
                                                List<AttributeEnum> increasedAttributes,
                                                ElementEnum element, DamageLabelEnum damageLabel,
                                                ElementReactionEnum elementReaction);
}
