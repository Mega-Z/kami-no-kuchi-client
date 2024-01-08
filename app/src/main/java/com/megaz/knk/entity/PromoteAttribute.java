package com.megaz.knk.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.megaz.knk.constant.AttributeEnum;

@Entity(tableName = "promote_attribute")
public class PromoteAttribute extends MetaDataEntity {
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "promote_id")
    private String promoteId;
    @ColumnInfo(name = "phase")
    private String phase;
    @ColumnInfo(name = "attribute")
    private AttributeEnum attribute;
    @ColumnInfo(name = "value")
    private Double value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPromoteId() {
        return promoteId;
    }

    public void setPromoteId(String promoteId) {
        this.promoteId = promoteId;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public AttributeEnum getAttribute() {
        return attribute;
    }

    public void setAttribute(AttributeEnum attribute) {
        this.attribute = attribute;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}