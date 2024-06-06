package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.entity.ArtifactDex;
import com.megaz.knk.entity.CharacterDex;

import java.util.List;

@Dao
public interface CharacterDexDao extends MetaDataDao<CharacterDex>{
    @Insert
    void batchInsert(CharacterDex... characters);

    @Query("DELETE FROM character_dex")
    int deleteAll();

    @Query("SELECT * FROM character_dex WHERE character_id=:characterId")
    List<CharacterDex> selectByCharacterId(String characterId);

    @Query("SELECT * FROM character_dex")
    List<CharacterDex> selectAll();

    @Query("SELECT icon_avatar FROM character_dex WHERE character_id LIKE :characterId || '%' ")
    List<String> selectAvatarIconByLikelyCharacterId(String characterId);
}
