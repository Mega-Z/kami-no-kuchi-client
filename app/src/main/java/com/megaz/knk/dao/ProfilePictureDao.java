package com.megaz.knk.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.megaz.knk.entity.ProfilePicture;

import java.util.List;

@Dao
public interface ProfilePictureDao extends MetaDataDao<ProfilePicture> {
    @Insert
    void batchInsert(ProfilePicture... profilePictures);

    @Query("DELETE FROM profile_picture")
    int deleteAll();

    @Query("SELECT * FROM profile_picture WHERE profile_picture_id=:profilePictureId")
    List<ProfilePicture> selectByProfilePictureId(String profilePictureId);
}
