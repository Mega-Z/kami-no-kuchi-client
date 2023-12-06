package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.entity.Buff;

@Dao
public interface BuffDao extends MetaDataDao<Buff>{

    @Insert
    void batchInsert(Buff... buffs);

    @Query("DELETE FROM buff")
    int deleteAll();
}
