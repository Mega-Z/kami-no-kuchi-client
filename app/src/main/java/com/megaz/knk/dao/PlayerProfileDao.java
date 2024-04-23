package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.megaz.knk.entity.PlayerProfile;


@Dao
public interface PlayerProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PlayerProfile playerProfile);

    @Query("SELECT * FROM player_profile WHERE uid = :uid")
    PlayerProfile selectByUid(String uid);
}
