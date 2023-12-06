package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.entity.BuffEffectRelation;

@Dao
public interface BuffEffectRelationDao extends MetaDataDao<BuffEffectRelation>{
    @Insert
    void batchInsert(BuffEffectRelation... buffEffectRelations);

    @Query("DELETE FROM buff_effect_relation")
    int deleteAll();
}
