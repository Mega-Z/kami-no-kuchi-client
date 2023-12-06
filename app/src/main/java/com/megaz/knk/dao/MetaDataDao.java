package com.megaz.knk.dao;

import com.megaz.knk.entity.MetaDataEntity;

public interface MetaDataDao <T extends MetaDataEntity>{

    void batchInsert(T[] ts);

    int deleteAll();
}
