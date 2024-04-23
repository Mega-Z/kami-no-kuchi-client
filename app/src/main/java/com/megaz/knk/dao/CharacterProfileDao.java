package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.entity.CharacterProfile;

import java.util.List;

@Dao
public interface CharacterProfileDao {
    @Insert
    void batchInsert(CharacterProfile... characterProfiles);

    @Query("SELECT * FROM character_profile WHERE uid = :uid " +
            "ORDER BY character_id, update_time DESC")
    List<CharacterProfile> selectByUid(String uid);

    @Query("SELECT * FROM character_profile WHERE character_id = :characterId " +
            "ORDER BY uid, update_time DESC")
    List<CharacterProfile> selectByCharacterId(String characterId);

    @Query("SELECT * FROM character_profile WHERE uid = :uid AND character_id = :characterId " +
            "ORDER BY update_time DESC")
    List<CharacterProfile> selectByUidAndCharacterId(String uid, String characterId);

    @Query("SELECT c.* FROM character_profile c JOIN " +
            "(SELECT uid, character_id, MAX(update_time) latest_time FROM character_profile " +
            "WHERE uid=:uid GROUP BY uid, character_id) l " +
            "ON c.uid = l.uid AND c.character_id = l.character_id AND c.update_time = l.latest_time")
    List<CharacterProfile> selectLatestByUid(String uid);
}
