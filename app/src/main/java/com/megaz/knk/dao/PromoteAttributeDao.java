package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.entity.PromoteAttribute;
import com.megaz.knk.entity.TalentCurve;

import java.util.List;

@Dao
public interface PromoteAttributeDao extends MetaDataDao<PromoteAttribute> {
    @Insert
    void batchInsert(PromoteAttribute... promoteAttributes);

    @Query("DELETE FROM promote_attribute")
    int deleteAll();

    @Query("SELECT * FROM promote_attribute WHERE promote_id=:curveId AND phase=:phase")
    List<PromoteAttribute> selectByPromoteIdAndPhase(String curveId, Integer phase);
}
