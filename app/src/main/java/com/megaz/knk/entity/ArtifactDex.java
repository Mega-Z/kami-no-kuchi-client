package com.megaz.knk.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.megaz.knk.constant.ArtifactPositionEnum;

@Entity(tableName = "artifact_dex")
public class ArtifactDex extends MetaDataEntity {
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "artifact_name")
    private String artifactName;
    @ColumnInfo(name = "position")
    private ArtifactPositionEnum position;
    @ColumnInfo(name = "set_id")
    private String setId;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "icon")
    private String icon;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}