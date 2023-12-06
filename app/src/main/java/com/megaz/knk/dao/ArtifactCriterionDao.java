package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.entity.ArtifactCriterion;

import java.util.List;

@Dao
public interface ArtifactCriterionDao extends MetaDataDao<ArtifactCriterion>{
    @Insert
    void batchInsert(ArtifactCriterion... artifactCriteria);

    @Query("DELETE FROM artifact_criterion")
    int deleteAll();

    @Query("SELECT * FROM artifact_criterion WHERE character_id=:characterId")
    List<ArtifactCriterion> selectByCharacterId(String characterId);
}
