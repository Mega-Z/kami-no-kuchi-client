package com.megaz.knk.manager;

import android.content.Context;

import androidx.annotation.WorkerThread;

import com.megaz.knk.KnkDatabase;
import com.megaz.knk.bo.ArtifactKey;
import com.megaz.knk.computation.CharacterOverview;
import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.constant.SourceTalentEnum;
import com.megaz.knk.dao.ArtifactDexDao;
import com.megaz.knk.dao.CharacterDexDao;
import com.megaz.knk.dao.CostumeDexDao;
import com.megaz.knk.dao.ProfilePictureDao;
import com.megaz.knk.dao.WeaponDexDao;
import com.megaz.knk.dto.ArtifactProfileDto;
import com.megaz.knk.dto.CharacterProfileDto;
import com.megaz.knk.dto.PlayerProfileDto;
import com.megaz.knk.dto.WeaponProfileDto;
import com.megaz.knk.entity.ArtifactDex;
import com.megaz.knk.entity.CharacterDex;
import com.megaz.knk.entity.CostumeDex;
import com.megaz.knk.entity.ProfilePicture;
import com.megaz.knk.entity.WeaponDex;
import com.megaz.knk.exception.MetaDataQueryException;
import com.megaz.knk.utils.ProfileConvertUtils;
import com.megaz.knk.vo.ArtifactProfileVo;
import com.megaz.knk.vo.CharacterProfileVo;
import com.megaz.knk.vo.ConstellationVo;
import com.megaz.knk.vo.PlayerProfileVo;
import com.megaz.knk.vo.TalentVo;
import com.megaz.knk.vo.WeaponProfileVo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProfileViewManager {
    private static ProfileViewManager instance;
    private final KnkDatabase knkDatabase;
    private final ArtifactEvaluateManager artifactEvaluateManager;
    private final Map<String, CharacterDex> characterDexBuffer;
    private final Map<String, CostumeDex> costumeDexBuffer;
    private final Map<String, WeaponDex> weaponDexBuffer;
    private final Map<ArtifactKey, ArtifactDex> artifactDexBuffer;

    public static synchronized ProfileViewManager getInstance(Context context) {
        if(instance == null) {
            instance = new ProfileViewManager(context);
        }
        return instance;
    }

    private ProfileViewManager(Context context) {
        knkDatabase = KnkDatabase.getKnkDatabase(context);
        artifactEvaluateManager = new ArtifactEvaluateManager(context);
        characterDexBuffer = new HashMap<>();
        costumeDexBuffer = new HashMap<>();
        weaponDexBuffer = new HashMap<>();
        artifactDexBuffer = new HashMap<>();
    }

    @WorkerThread
    public PlayerProfileVo convertPlayerProfileToVo(PlayerProfileDto playerProfileDto) {
        CharacterDexDao characterDexDao = knkDatabase.getCharacterDexDao();
        ProfilePictureDao profilePictureDao = knkDatabase.getProfilePictureDao();

        PlayerProfileVo playerProfileVo = new PlayerProfileVo();
        playerProfileVo.setUid(playerProfileDto.getUid());
        playerProfileVo.setNickname(playerProfileDto.getNickname());
        playerProfileVo.setSign(playerProfileDto.getSign());
        if (playerProfileDto.getCostumeId() != null) { // 优先设置衣装头像
            CostumeDex costumeData = getCostumeDex(playerProfileDto.getCostumeId());
            playerProfileVo.setAvatarIcon(costumeData.getIconAvatar());
        } else if (playerProfileDto.getAvatarId() != null) {
            List<String> avatarIcons = characterDexDao.selectAvatarIconByLikelyCharacterId(playerProfileDto.getAvatarId());
            if (avatarIcons.isEmpty()) {
                throw new MetaDataQueryException("character_dex");
            }
            playerProfileVo.setAvatarIcon(avatarIcons.get(0));
        }
        if (playerProfileDto.getProfilePictureId() != null) { // 新版头像
            List<ProfilePicture> profilePictures = profilePictureDao.selectByProfilePictureId(playerProfileDto.getProfilePictureId());
            if (profilePictures.size() != 1) {
                throw new MetaDataQueryException("profile_picture");
            }
            playerProfileVo.setAvatarIcon(profilePictures.get(0).getIcon());
        }
        List<CharacterProfileVo> characterProfileVoList = new ArrayList<>();
        for (CharacterProfileDto characterProfileDto : playerProfileDto.getCharacters()) {
            characterProfileVoList.add(convertCharacterProfileToVo(characterProfileDto));
        }
        playerProfileVo.setCharacters(characterProfileVoList);
        playerProfileVo.setCharacterAvailable(playerProfileDto.getCharacterAvailable());
        return playerProfileVo;
    }

    @WorkerThread
    public CharacterProfileVo convertCharacterProfileToVo(CharacterOverview characterOverview, Map<ArtifactPositionEnum, ArtifactProfileDto> artifacts) {
        CharacterProfileVo characterProfileVo = new CharacterProfileVo();
        CharacterDex characterData = getCharacterDex(characterOverview.getCharacterId());
        characterProfileVo.setCharacterName(characterData.getCharacterName());
        characterProfileVo.setUid("");
        characterProfileVo.setAvatarIcon(characterData.getIconAvatar());
        characterProfileVo.setArtIcon(characterData.getIconArt());
        characterProfileVo.setCardIcon(characterData.getIconCard());
        characterProfileVo.setElement(characterData.getElement());
        characterProfileVo.setLevel(characterOverview.getLevel());

        characterProfileVo.setTalentAIcon(characterData.getIconTalentA());
        characterProfileVo.setTalentEIcon(characterData.getIconTalentE());
        characterProfileVo.setTalentQIcon(characterData.getIconTalentQ());
        characterProfileVo.setTalentABaseLevel(characterOverview.getTalentLevel().get(SourceTalentEnum.A));
        characterProfileVo.setTalentEBaseLevel(characterOverview.getTalentLevel().get(SourceTalentEnum.E));
        characterProfileVo.setTalentQBaseLevel(characterOverview.getTalentLevel().get(SourceTalentEnum.Q));
        characterProfileVo.setTalentAPlusLevel(characterOverview.getTalentLevelPlus().get(SourceTalentEnum.A));
        characterProfileVo.setTalentEPlusLevel(characterOverview.getTalentLevelPlus().get(SourceTalentEnum.E));
        characterProfileVo.setTalentQPlusLevel(characterOverview.getTalentLevelPlus().get(SourceTalentEnum.Q));

        characterProfileVo.setConsIcons(Arrays.asList(
                characterData.getIconCons1(),
                characterData.getIconCons2(),
                characterData.getIconCons3(),
                characterData.getIconCons4(),
                characterData.getIconCons5(),
                characterData.getIconCons6()
        ));
        characterProfileVo.setConstellation(characterOverview.getConstellation());

        characterProfileVo.setWeapon(convertWeaponProfileToVo(characterOverview));

        Map<ArtifactPositionEnum, ArtifactProfileVo> artifactProfileVoMap = new HashMap<>();

        for (ArtifactPositionEnum position : GenshinConstantMeta.ARTIFACT_POSITION_LIST) {
            if (artifacts.containsKey(position)) {
                artifactProfileVoMap.put(position, convertArtifactProfileToVo(Objects.requireNonNull(artifacts.get(position))));
            }
        }
        characterProfileVo.setArtifacts(artifactProfileVoMap);
        characterProfileVo.setEvaluations(artifactEvaluateManager.evaluateArtifacts(characterOverview, artifacts));
        return characterProfileVo;
    }

    @WorkerThread
    public CharacterProfileVo convertCharacterProfileToVo(CharacterProfileDto characterProfileDto) {
        CharacterProfileVo characterProfileVo = new CharacterProfileVo();
        CharacterDex characterData = getCharacterDex(characterProfileDto.getCharacterId());
        characterProfileVo.setCharacterName(characterData.getCharacterName());
        characterProfileVo.setUid(characterProfileDto.getUid());
        characterProfileVo.setUpdateTime(characterProfileDto.getUpdateTime());
        if (characterProfileDto.getCostumeId() != null) {
            CostumeDex costumeData = getCostumeDex(characterProfileDto.getCostumeId());
            characterProfileVo.setAvatarIcon(costumeData.getIconAvatar());
            characterProfileVo.setArtIcon(costumeData.getIconArt());
            characterProfileVo.setCardIcon(costumeData.getIconCard());
        } else {
            characterProfileVo.setAvatarIcon(characterData.getIconAvatar());
            characterProfileVo.setArtIcon(characterData.getIconArt());
            characterProfileVo.setCardIcon(characterData.getIconCard());
        }
        characterProfileVo.setElement(characterData.getElement());
        characterProfileVo.setLevel(characterProfileDto.getLevel());
        characterProfileVo.setFetter(characterProfileDto.getFetter());
        characterProfileVo.setNewData(characterProfileDto.getNewData());
        characterProfileVo.setUpdateTime(characterProfileDto.getUpdateTime());

//        characterProfileVo.setBaseHp(characterProfileDto.getBaseHp());
//        characterProfileVo.setPlusHp(characterProfileDto.getPlusHp());
//        characterProfileVo.setBaseAtk(characterProfileDto.getBaseAtk());
//        characterProfileVo.setPlusAtk(characterProfileDto.getPlusAtk());
//        characterProfileVo.setBaseDef(characterProfileDto.getBaseDef());
//        characterProfileVo.setPlusDef(characterProfileDto.getPlusDef());
//        characterProfileVo.setMastery(characterProfileDto.getMastery());
//        characterProfileVo.setCritRate(characterProfileDto.getCritRate());
//        characterProfileVo.setCritDmg(characterProfileDto.getCritDmg());
//        characterProfileVo.setRecharge(characterProfileDto.getRecharge());
//        characterProfileVo.setDmgUp(characterProfileDto.getDmgUp());
//        characterProfileVo.setHealUp(characterProfileDto.getHealUp());

        characterProfileVo.setTalentAIcon(characterData.getIconTalentA());
        characterProfileVo.setTalentEIcon(characterData.getIconTalentE());
        characterProfileVo.setTalentQIcon(characterData.getIconTalentQ());
        characterProfileVo.setTalentABaseLevel(characterProfileDto.getTalentABaseLevel());
        characterProfileVo.setTalentEBaseLevel(characterProfileDto.getTalentEBaseLevel());
        characterProfileVo.setTalentQBaseLevel(characterProfileDto.getTalentQBaseLevel());
        characterProfileVo.setTalentAPlusLevel(characterProfileDto.getTalentAPlusLevel());
        characterProfileVo.setTalentEPlusLevel(characterProfileDto.getTalentEPlusLevel());
        characterProfileVo.setTalentQPlusLevel(characterProfileDto.getTalentQPlusLevel());

        characterProfileVo.setConsIcons(Arrays.asList(
                characterData.getIconCons1(),
                characterData.getIconCons2(),
                characterData.getIconCons3(),
                characterData.getIconCons4(),
                characterData.getIconCons5(),
                characterData.getIconCons6()
        ));
        characterProfileVo.setConstellation(characterProfileDto.getConstellation());

        characterProfileVo.setWeapon(convertWeaponProfileToVo(characterProfileDto.getWeapon()));

        Map<ArtifactPositionEnum, ArtifactProfileVo> artifactProfileVoMap = new HashMap<>();

        for (ArtifactPositionEnum position : GenshinConstantMeta.ARTIFACT_POSITION_LIST) {
            if (characterProfileDto.getArtifacts().containsKey(position)) {
                artifactProfileVoMap.put(position, convertArtifactProfileToVo(Objects.requireNonNull(characterProfileDto.getArtifacts().get(position))));
            }
        }
        characterProfileVo.setArtifacts(artifactProfileVoMap);
        characterProfileVo.setEvaluations(artifactEvaluateManager.evaluateArtifacts(
                ProfileConvertUtils.extractCharacterOverview(characterProfileDto),
                characterProfileDto.getArtifacts()));
        return characterProfileVo;
    }

    public ConstellationVo getConstellationVoFromCharacterProfileVo(CharacterProfileVo characterProfileVo, int cons) {
        ConstellationVo constellationVo = new ConstellationVo();
        constellationVo.setElement(characterProfileVo.getElement());
        constellationVo.setActive(characterProfileVo.getConstellation() >= cons);
        constellationVo.setIcon(characterProfileVo.getConsIcons().get(cons - 1));
        return constellationVo;
    }

    public TalentVo getTalentVoFromCharacterProfileVo(CharacterProfileVo characterProfileVo, SourceTalentEnum talent) {
        TalentVo talentVo = new TalentVo();
        talentVo.setElement(characterProfileVo.getElement());
        switch (talent) {
            case A:
                talentVo.setIcon(characterProfileVo.getTalentAIcon());
                talentVo.setBaseLevel(characterProfileVo.getTalentABaseLevel());
                talentVo.setPlusLevel(characterProfileVo.getTalentAPlusLevel());
                break;
            case E:
                talentVo.setIcon(characterProfileVo.getTalentEIcon());
                talentVo.setBaseLevel(characterProfileVo.getTalentEBaseLevel());
                talentVo.setPlusLevel(characterProfileVo.getTalentEPlusLevel());
                break;
            case Q:
                talentVo.setIcon(characterProfileVo.getTalentQIcon());
                talentVo.setBaseLevel(characterProfileVo.getTalentQBaseLevel());
                talentVo.setPlusLevel(characterProfileVo.getTalentQPlusLevel());
                break;
        }
        return talentVo;
    }

    private WeaponProfileVo convertWeaponProfileToVo(CharacterOverview characterOverview) {
        WeaponProfileVo weaponProfileVo = new WeaponProfileVo();
        WeaponDex weaponData = getWeaponDex(characterOverview.getWeaponId());
        weaponProfileVo.setWeaponName(weaponData.getWeaponName());
        if (characterOverview.getWeaponPhase() >= 2) {
            weaponProfileVo.setWeaponIcon(weaponData.getIconAwaken());
        } else {
            weaponProfileVo.setWeaponIcon(weaponData.getIconInitial());
        }
        weaponProfileVo.setLevel(characterOverview.getWeaponLevel());
        weaponProfileVo.setRefineRank(characterOverview.getWeaponRefinement());
        weaponProfileVo.setStar(weaponData.getStar());
        return weaponProfileVo;
    }

    private WeaponProfileVo convertWeaponProfileToVo(WeaponProfileDto weaponProfileDto) {
        WeaponProfileVo weaponProfileVo = new WeaponProfileVo();
        WeaponDex weaponData = getWeaponDex(weaponProfileDto.getWeaponId());
        weaponProfileVo.setWeaponName(weaponData.getWeaponName());
        if (weaponProfileDto.getPhase() >= 2) {
            weaponProfileVo.setWeaponIcon(weaponData.getIconAwaken());
        } else {
            weaponProfileVo.setWeaponIcon(weaponData.getIconInitial());
        }
        weaponProfileVo.setLevel(weaponProfileDto.getLevel());
        weaponProfileVo.setRefineRank(weaponProfileDto.getRefineRank());
        weaponProfileVo.setStar(weaponData.getStar());
        return weaponProfileVo;
    }

    private ArtifactProfileVo convertArtifactProfileToVo(ArtifactProfileDto artifactProfileDto) {
        ArtifactProfileVo artifactProfileVo = new ArtifactProfileVo();
        ArtifactDex artifactData = getArtifactDex(artifactProfileDto.getSetId(), artifactProfileDto.getPosition());
        artifactProfileVo.setArtifactName(artifactData.getArtifactName());
        artifactProfileVo.setIcon(artifactData.getIcon());
        artifactProfileVo.setStar(artifactProfileDto.getStar());
        artifactProfileVo.setLevel(artifactProfileDto.getLevel());
        artifactProfileVo.setMainAttribute(artifactProfileDto.getMainAttribute());
        artifactProfileVo.setMainAttributeVal(artifactProfileDto.getMainAttributeVal());
        artifactProfileVo.setSubAttributes(artifactProfileDto.getSubAttributes());
        artifactProfileVo.setSubAttributesVal(artifactProfileDto.getSubAttributesVal());
        artifactProfileVo.setSubAttributesCnt(artifactProfileDto.getSubAttributesCnt());
        return artifactProfileVo;
    }

    private CharacterDex getCharacterDex(String characterId){
        if(characterDexBuffer.containsKey(characterId)) {
            return characterDexBuffer.get(characterId);
        }
        CharacterDexDao characterDexDao = knkDatabase.getCharacterDexDao();
        List<CharacterDex> characterDataList = characterDexDao.selectByCharacterId(characterId);
        if (characterDataList.size() != 1) {
            throw new MetaDataQueryException("character_dex");
        }
        CharacterDex characterData = characterDataList.get(0);
        characterDexBuffer.put(characterId, characterData);
        return characterData;
    }

    private CostumeDex getCostumeDex(String costumeId){
        if(costumeDexBuffer.containsKey(costumeId)) {
            return costumeDexBuffer.get(costumeId);
        }
        CostumeDexDao costumeDexDao = knkDatabase.getCostumeDexDao();
        List<CostumeDex> costumeDataList = costumeDexDao.selectByCostumeId(costumeId);
        if (costumeDataList.size() != 1) {
            throw new MetaDataQueryException("costume_dex");
        }
        CostumeDex costumeData = costumeDataList.get(0);
        costumeDexBuffer.put(costumeId, costumeData);
        return costumeData;
    }

    private WeaponDex getWeaponDex(String weaponId){
        if(weaponDexBuffer.containsKey(weaponId)) {
            return weaponDexBuffer.get(weaponId);
        }
        WeaponDexDao weaponDexDao = knkDatabase.getWeaponDexDao();
        List<WeaponDex> weaponDataList = weaponDexDao.selectByWeaponId(weaponId);
        if (weaponDataList.size() != 1) {
            throw new MetaDataQueryException("weapon_dex");
        }
        WeaponDex weaponData = weaponDataList.get(0);
        weaponDexBuffer.put(weaponId, weaponData);
        return weaponData;
    }

    private ArtifactDex getArtifactDex(String setId, ArtifactPositionEnum position){
        ArtifactKey key = new ArtifactKey(setId, position);
        if(artifactDexBuffer.containsKey(key)) {
            return artifactDexBuffer.get(key);
        }
        ArtifactDexDao artifactDexDao = knkDatabase.getArtifactDexDao();
        List<ArtifactDex> artifactDataList = artifactDexDao.selectBySetIdAndPosition(key.getSetId(), key.getPosition());
        if (artifactDataList.size() != 1) {
            throw new MetaDataQueryException("artifact_dex");
        }
        ArtifactDex artifactData = artifactDataList.get(0);
        artifactDexBuffer.put(key, artifactData);
        return artifactData;
    }
}
