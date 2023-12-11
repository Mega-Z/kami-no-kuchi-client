package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.entity.ProfilePicture;
import com.megaz.knk.entity.TalentCurve;

import java.util.List;

@Dao
public interface TalentCurveDao extends MetaDataDao<TalentCurve> {
    @Insert
    void batchInsert(TalentCurve... talentCurves);

    @Query("DELETE FROM talent_curve")
    int deleteAll();

    @Query("SELECT * FROM talent_curve WHERE curve_id=:curveId")
    List<TalentCurve> selectByCurveID(String curveId);
}
