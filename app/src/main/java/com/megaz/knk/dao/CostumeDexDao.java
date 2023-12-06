package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.entity.CostumeDex;

import java.util.List;

@Dao
public interface CostumeDexDao extends MetaDataDao<CostumeDex>{
    @Insert
    void batchInsert(CostumeDex... costumes);

    @Query("DELETE FROM costume_dex")
    int deleteAll();

    @Query("SELECT * FROM costume_dex WHERE costume_id=:costumeId")
    List<CostumeDex> selectByCostumeId(String costumeId);
}
