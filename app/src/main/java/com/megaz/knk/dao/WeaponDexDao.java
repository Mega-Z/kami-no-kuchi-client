package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.constant.WeaponTypeEnum;
import com.megaz.knk.entity.CharacterDex;
import com.megaz.knk.entity.WeaponDex;

import java.util.List;

@Dao
public interface WeaponDexDao extends MetaDataDao<WeaponDex>{
    @Insert
    void batchInsert(WeaponDex... weapons);

    @Query("DELETE FROM weapon_dex")
    int deleteAll();

    @Query("SELECT * FROM weapon_dex WHERE weapon_id=:weaponId")
    List<WeaponDex> selectByWeaponId(String weaponId);

    @Query("SELECT * FROM weapon_dex WHERE type=:type")
    List<WeaponDex> selectByWeaponType(WeaponTypeEnum type);
}
