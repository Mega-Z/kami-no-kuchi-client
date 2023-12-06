package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.entity.FightEffectComputation;

@Dao
public interface FightEffectComputationDao extends MetaDataDao<FightEffectComputation> {
    @Insert
    void batchInsert(FightEffectComputation... fightEffectComputations);

    @Query("DELETE FROM fight_effect_computation")
    int deleteAll();
}
