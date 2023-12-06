package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.entity.ProfilePicture;
import com.megaz.knk.entity.TalentCurve;

@Dao
public interface TalentCurveDao extends MetaDataDao<TalentCurve> {
    @Insert
    void batchInsert(TalentCurve... talentCurves);

    @Query("DELETE FROM talent_curve")
    int deleteAll();
}
