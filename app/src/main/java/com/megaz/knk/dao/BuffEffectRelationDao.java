package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.entity.BuffEffectRelation;

import java.util.List;

@Dao
public interface BuffEffectRelationDao extends MetaDataDao<BuffEffectRelation>{
    @Insert
    void batchInsert(BuffEffectRelation... buffEffectRelations);

    @Query("DELETE FROM buff_effect_relation")
    int deleteAll();

    @Query("SELECT * FROM buff_effect_relation WHERE effect_id=:effectId")
    List<BuffEffectRelation> selectByEffectId(String effectId);
}
