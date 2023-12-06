package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.entity.ArtifactDex;

import java.util.List;

@Dao
public interface ArtifactDexDao extends MetaDataDao<ArtifactDex>{
    @Insert
    void batchInsert(ArtifactDex... artifacts);

    @Query("DELETE FROM artifact_dex")
    int deleteAll();

    @Query("SELECT * FROM artifact_dex WHERE set_id=:setId AND position=:position")
    List<ArtifactDex> selectBySetIdAndPosition(String setId, ArtifactPositionEnum position);
}
