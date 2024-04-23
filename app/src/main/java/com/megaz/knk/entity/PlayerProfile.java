package com.megaz.knk.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(tableName = "player_profile", indices = {@Index(value = "uid",unique = true)})
public class PlayerProfile {
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "uid")
    private String uid;
    @ColumnInfo(name = "player_name")
    private String playerName;
    @ColumnInfo(name = "signature")
    private String signature;
    @ColumnInfo(name = "avatar_id")
    private String avatarId;
    @ColumnInfo(name = "costume_id")
    private String costumeId;
    @ColumnInfo(name = "profile_picture_id")
    private String profilePictureId;
    @ColumnInfo(name = "adventure_rank")
    private Integer adventureRank;

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

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public String getCostumeId() {
        return costumeId;
    }

    public void setCostumeId(String costumeId) {
        this.costumeId = costumeId;
    }

    public String getProfilePictureId() {
        return profilePictureId;
    }

    public void setProfilePictureId(String profilePictureId) {
        this.profilePictureId = profilePictureId;
    }

    public Integer getAdventureRank() {
        return adventureRank;
    }

    public void setAdventureRank(Integer adventureRank) {
        this.adventureRank = adventureRank;
    }
}
