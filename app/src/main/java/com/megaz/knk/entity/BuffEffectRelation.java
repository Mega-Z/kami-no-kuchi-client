package com.megaz.knk.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "buff_effect_relation")
public class BuffEffectRelation extends MetaDataEntity{
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    @ColumnInfo(name = "buff_id")
    private String buffId;
    @ColumnInfo(name = "effect_id")
    private String effectId;
    @ColumnInfo(name = "forced")
    private Boolean forced;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBuffId() {
        return buffId;
    }

    public void setBuffId(String buffId) {
        this.buffId = buffId;
    }

    public String getEffectId() {
        return effectId;
    }

    public void setEffectId(String effectId) {
        this.effectId = effectId;
    }

    public Boolean getForced() {
        return forced;
    }

    public void setForced(Boolean forced) {
        this.forced = forced;
    }
}
