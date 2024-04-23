package com.megaz.knk.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.constant.AttributeEnum;

@Entity(tableName = "artifact_instance", indices = {@Index(value =
        {"uid", "character_id", "artifact_instance_id"}, unique = true)})
public class ArtifactInstance {
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "uid")
    private String uid;
    @ColumnInfo(name = "character_id")
    private String characterId;
    @ColumnInfo(name = "artifact_instance_id")
    private String artifactInstanceId;
    @ColumnInfo(name = "position")
    private ArtifactPositionEnum position;
    @ColumnInfo(name = "set_id")
    private String setId;
    private Integer star;
    private Integer level;
    @ColumnInfo(name = "main_attribute")
    private AttributeEnum mainAttribute;
    @ColumnInfo(name = "main_attribute_value")
    private Double mainAttributeValue;
    @ColumnInfo(name = "sub_attribute_1")
    private AttributeEnum subAttribute1;
    @ColumnInfo(name = "sub_attribute_1_value")
    private Double subAttribute1Value;
    @ColumnInfo(name = "sub_attribute_1_count")
    private Integer subAttribute1Count;
    @ColumnInfo(name = "sub_attribute_2")
    private AttributeEnum subAttribute2;
    @ColumnInfo(name = "sub_attribute_2_value")
    private Double subAttribute2Value;
    @ColumnInfo(name = "sub_attribute_2_count")
    private Integer subAttribute2Count;
    @ColumnInfo(name = "sub_attribute_3")
    private AttributeEnum subAttribute3;
    @ColumnInfo(name = "sub_attribute_3_value")
    private Double subAttribute3Value;
    @ColumnInfo(name = "sub_attribute_3_count")
    private Integer subAttribute3Count;
    @ColumnInfo(name = "sub_attribute_4")
    private AttributeEnum subAttribute4;
    @ColumnInfo(name = "sub_attribute_4_value")
    private Double subAttribute4Value;
    @ColumnInfo(name = "sub_attribute_4_count")
    private Integer subAttribute4Count;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public String getArtifactInstanceId() {
        return artifactInstanceId;
    }

    public void setArtifactInstanceId(String artifactInstanceId) {
        this.artifactInstanceId = artifactInstanceId;
    }

    public ArtifactPositionEnum getPosition() {
        return position;
    }

    public void setPosition(ArtifactPositionEnum position) {
        this.position = position;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public AttributeEnum getMainAttribute() {
        return mainAttribute;
    }

    public void setMainAttribute(AttributeEnum mainAttribute) {
        this.mainAttribute = mainAttribute;
    }

    public Double getMainAttributeValue() {
        return mainAttributeValue;
    }

    public void setMainAttributeValue(Double mainAttributeValue) {
        this.mainAttributeValue = mainAttributeValue;
    }

    public AttributeEnum getSubAttribute1() {
        return subAttribute1;
    }

    public void setSubAttribute1(AttributeEnum subAttribute1) {
        this.subAttribute1 = subAttribute1;
    }

    public Double getSubAttribute1Value() {
        return subAttribute1Value;
    }

    public void setSubAttribute1Value(Double subAttribute1Value) {
        this.subAttribute1Value = subAttribute1Value;
    }

    public Integer getSubAttribute1Count() {
        return subAttribute1Count;
    }

    public void setSubAttribute1Count(Integer subAttribute1Count) {
        this.subAttribute1Count = subAttribute1Count;
    }

    public AttributeEnum getSubAttribute2() {
        return subAttribute2;
    }

    public void setSubAttribute2(AttributeEnum subAttribute2) {
        this.subAttribute2 = subAttribute2;
    }

    public Double getSubAttribute2Value() {
        return subAttribute2Value;
    }

    public void setSubAttribute2Value(Double subAttribute2Value) {
        this.subAttribute2Value = subAttribute2Value;
    }

    public Integer getSubAttribute2Count() {
        return subAttribute2Count;
    }

    public void setSubAttribute2Count(Integer subAttribute2Count) {
        this.subAttribute2Count = subAttribute2Count;
    }

    public AttributeEnum getSubAttribute3() {
        return subAttribute3;
    }

    public void setSubAttribute3(AttributeEnum subAttribute3) {
        this.subAttribute3 = subAttribute3;
    }

    public Double getSubAttribute3Value() {
        return subAttribute3Value;
    }

    public void setSubAttribute3Value(Double subAttribute3Value) {
        this.subAttribute3Value = subAttribute3Value;
    }

    public Integer getSubAttribute3Count() {
        return subAttribute3Count;
    }

    public void setSubAttribute3Count(Integer subAttribute3Count) {
        this.subAttribute3Count = subAttribute3Count;
    }

    public AttributeEnum getSubAttribute4() {
        return subAttribute4;
    }

    public void setSubAttribute4(AttributeEnum subAttribute4) {
        this.subAttribute4 = subAttribute4;
    }

    public Double getSubAttribute4Value() {
        return subAttribute4Value;
    }

    public void setSubAttribute4Value(Double subAttribute4Value) {
        this.subAttribute4Value = subAttribute4Value;
    }

    public Integer getSubAttribute4Count() {
        return subAttribute4Count;
    }

    public void setSubAttribute4Count(Integer subAttribute4Count) {
        this.subAttribute4Count = subAttribute4Count;
    }
}
