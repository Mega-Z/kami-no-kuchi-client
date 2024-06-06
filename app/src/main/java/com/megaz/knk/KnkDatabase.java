package com.megaz.knk;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.megaz.knk.dao.ArtifactCriterionDao;
import com.megaz.knk.dao.ArtifactDexDao;
import com.megaz.knk.dao.ArtifactInstanceDao;
import com.megaz.knk.dao.BuffDao;
import com.megaz.knk.dao.BuffEffectRelationDao;
import com.megaz.knk.dao.CharacterDexDao;
import com.megaz.knk.dao.CharacterProfileDao;
import com.megaz.knk.dao.CostumeDexDao;
import com.megaz.knk.dao.FightEffectComputationDao;
import com.megaz.knk.dao.PlayerProfileDao;
import com.megaz.knk.dao.ProfilePictureDao;
import com.megaz.knk.dao.PromoteAttributeDao;
import com.megaz.knk.dao.RefinementCurveDao;
import com.megaz.knk.dao.TalentCurveDao;
import com.megaz.knk.dao.WeaponDexDao;
import com.megaz.knk.entity.ArtifactCriterion;
import com.megaz.knk.entity.ArtifactDex;
import com.megaz.knk.entity.ArtifactInstance;
import com.megaz.knk.entity.Buff;
import com.megaz.knk.entity.BuffEffectRelation;
import com.megaz.knk.entity.CharacterDex;
import com.megaz.knk.entity.CharacterProfile;
import com.megaz.knk.entity.CostumeDex;
import com.megaz.knk.entity.FightEffectComputation;
import com.megaz.knk.entity.PlayerProfile;
import com.megaz.knk.entity.ProfilePicture;
import com.megaz.knk.entity.PromoteAttribute;
import com.megaz.knk.entity.RefinementCurve;
import com.megaz.knk.entity.TalentCurve;
import com.megaz.knk.entity.WeaponDex;

@Database(entities = {
        CharacterDex.class,
        WeaponDex.class,
        ArtifactDex.class,
        CostumeDex.class,
        ProfilePicture.class,
        ArtifactCriterion.class,
        FightEffectComputation.class,
        Buff.class,
        BuffEffectRelation.class,
        TalentCurve.class,
        RefinementCurve.class,
        PromoteAttribute.class,
        PlayerProfile.class,
        CharacterProfile.class,
        ArtifactInstance.class
}, version = 5,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class KnkDatabase extends RoomDatabase {
    private static KnkDatabase knkDatabaseInstance;
    public static synchronized KnkDatabase getKnkDatabase(Context context) {
        if(knkDatabaseInstance == null) {
            knkDatabaseInstance = Room.databaseBuilder(context, KnkDatabase.class, "kami_no_kuchi")
                    .fallbackToDestructiveMigration().build();
        }
        return knkDatabaseInstance;
    }
    public abstract CharacterDexDao getCharacterDexDao();
    public abstract WeaponDexDao getWeaponDexDao();
    public abstract ArtifactDexDao getArtifactDexDao();
    public abstract CostumeDexDao getCostumeDexDao();
    public abstract ProfilePictureDao getProfilePictureDao();
    public abstract ArtifactCriterionDao getArtifactCriterionDao();
    public abstract FightEffectComputationDao getFightEffectComputationDao();
    public abstract BuffDao getBuffDao();
    public abstract BuffEffectRelationDao getBuffEffectRelationDao();
    public abstract TalentCurveDao getTalentCurveDao();
    public abstract RefinementCurveDao getRefinementCurveDao();
    public abstract PromoteAttributeDao getPromoteAttributeDao();
    public abstract PlayerProfileDao getPlayerProfileDao();
    public abstract CharacterProfileDao getCharacterProfileDao();
    public abstract ArtifactInstanceDao getArtifactInstanceDao();
}
