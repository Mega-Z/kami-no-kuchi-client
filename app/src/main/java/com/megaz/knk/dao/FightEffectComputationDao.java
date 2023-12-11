package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.entity.FightEffectComputation;

import java.util.List;

@Dao
public interface FightEffectComputationDao extends MetaDataDao<FightEffectComputation> {
    @Insert
    void batchInsert(FightEffectComputation... fightEffectComputations);

    @Query("DELETE FROM fight_effect_computation")
    int deleteAll();

    @Query("SELECT * FROM fight_effect_computation WHERE character_id=:characterId " +
            "AND (phase < -1 * :phase OR phase >= 0 AND phase <= :phase ) " +
            "AND (constellation < -1 * :constellation OR constellation >= 0 AND constellation <= :constellation)")
    List<FightEffectComputation> selectByCharacterConditions(String characterId, int phase, int constellation);
}
