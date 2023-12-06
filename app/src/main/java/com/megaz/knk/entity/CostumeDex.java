package com.megaz.knk.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "costume_dex")
public class CostumeDex extends MetaDataEntity {
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "character_id")
    private String characterId;
    @ColumnInfo(name = "costume_id")
    private String costumeId;
    @ColumnInfo(name = "icon_art")
    private String iconArt;
    @ColumnInfo(name = "icon_avatar")
    private String iconAvatar;
    @ColumnInfo(name = "icon_card")
    private String iconCard;
    @ColumnInfo(name = "icon_side")
    private String iconSide;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public String getCostumeId() {
        return costumeId;
    }

    public void setCostumeId(String costumeId) {
        this.costumeId = costumeId;
    }

    public String getIconArt() {
        return iconArt;
    }

    public void setIconArt(String iconArt) {
        this.iconArt = iconArt;
    }

    public String getIconAvatar() {
        return iconAvatar;
    }

    public void setIconAvatar(String iconAvatar) {
        this.iconAvatar = iconAvatar;
    }

    public String getIconCard() {
        return iconCard;
    }

    public void setIconCard(String iconCard) {
        this.iconCard = iconCard;
    }

    public String getIconSide() {
        return iconSide;
    }

    public void setIconSide(String iconSide) {
        this.iconSide = iconSide;
    }
}