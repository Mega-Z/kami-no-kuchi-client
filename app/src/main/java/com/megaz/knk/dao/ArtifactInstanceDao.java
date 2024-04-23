package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.entity.ArtifactInstance;

import java.util.List;

@Dao
public interface ArtifactInstanceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void batchInsert(ArtifactInstance... artifactInstances);

    @Query("SELECT * FROM artifact_instance WHERE uid=:uid AND character_id=:characterId AND artifact_instance_id = :artifactInstanceId")
    ArtifactInstance selectArtifactInstance(String uid, String characterId, String artifactInstanceId);

    @Query("SELECT * FROM artifact_instance WHERE uid=:uid AND position=:position")
    List<ArtifactInstance> selectByUidAndPosition(String uid, ArtifactPositionEnum position);

}
