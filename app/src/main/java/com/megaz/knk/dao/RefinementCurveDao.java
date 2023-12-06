package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.entity.RefinementCurve;

@Dao
public interface RefinementCurveDao extends MetaDataDao<RefinementCurve> {
    @Insert
    void batchInsert(RefinementCurve... refinementCurves);

    @Query("DELETE FROM refinement_curve")
    int deleteAll();
}
