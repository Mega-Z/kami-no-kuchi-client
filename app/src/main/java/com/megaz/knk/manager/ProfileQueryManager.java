package com.megaz.knk.manager;

import android.content.Context;
import android.util.Log;

import com.megaz.knk.KnkDatabase;
import com.megaz.knk.R;
import com.megaz.knk.client.RequestHelper;
import com.megaz.knk.client.ResponseEntity;
import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
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
import com.megaz.knk.exception.RequestErrorException;
import com.megaz.knk.vo.ArtifactProfileVo;
import com.megaz.knk.vo.CharacterProfileVo;
import com.megaz.knk.vo.PlayerProfileVo;
import com.megaz.knk.vo.WeaponProfileVo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProfileQueryManager {
    private Context context;
    private KnkDatabase knkDatabase;
    private ArtifactEvaluateManager artifactEvaluateManager;

    public ProfileQueryManager(Context context) {
        this.context = context;
        knkDatabase = KnkDatabase.getKnkDatabase(context);
        artifactEvaluateManager = new ArtifactEvaluateManager(context);
    }

    @Deprecated
    public PlayerProfileVo queryPlayerProfile(String uid) {
        PlayerProfileDto playerProfileDto = queryPlayerProfileDto(uid);
        return convertPlayerProfileToVo(playerProfileDto);
    }

    public PlayerProfileDto queryPlayerProfileDto(String uid) {
        Log.i("【查询面板】","开始获取uid:"+uid+"的面板数据");
        String url = context.getString(R.string.server) + context.getString(R.string.api_query) + "?uid=" + uid;
        ResponseEntity<PlayerProfileDto> response = RequestHelper.requestSend(url, PlayerProfileDto.class, 3, 5000);
        try{
            checkResponse(response);
            PlayerProfileDto playerProfileDto = response.getBody();
            Log.i("【查询面板】","uid:"+uid+"的面板数据获取成功");
            return playerProfileDto;
        } catch (RequestErrorException e) {
            Log.e("【查询面板】","uid:"+uid+"的面板数据获取失败");
            throw e;
        }
    }

    public PlayerProfileDto updatePlayerProfileDto(String uid) {
        Log.i("【更新面板】","开始获取uid:"+uid+"的面板数据");
        String url = context.getString(R.string.server) + context.getString(R.string.api_update) + "?uid=" + uid;
        ResponseEntity<PlayerProfileDto> response = RequestHelper.requestSend(url, PlayerProfileDto.class, 3, 5000);
        try{
            checkResponse(response);
            PlayerProfileDto playerProfileDto = response.getBody();
            Log.i("【更新面板】","uid:"+uid+"的面板数据获取成功");
            return playerProfileDto;
        } catch (RequestErrorException e) {
            Log.e("【更新面板】","uid:"+uid+"的面板数据获取失败");
            throw e;
        }
    }

    public PlayerProfileVo convertPlayerProfileToVo(PlayerProfileDto playerProfileDto) {
        CharacterDexDao characterDexDao = knkDatabase.getCharacterDexDao();
        CostumeDexDao costumeDexDao = knkDatabase.getCostumeDexDao();
        ProfilePictureDao profilePictureDao = knkDatabase.getProfilePictureDao();

        PlayerProfileVo playerProfileVo = new PlayerProfileVo();
        playerProfileVo.setUid(playerProfileDto.getUid());
        playerProfileVo.setNickname(playerProfileDto.getNickname());
        playerProfileVo.setSign(playerProfileDto.getSign());
        if (playerProfileDto.getCostumeId() != null) { // 优先设置衣装头像
            List<CostumeDex> costumeData = costumeDexDao.selectByCostumeId(playerProfileDto.getCostumeId());
            if(costumeData.size() != 1){
                throw new MetaDataQueryException("costume_dex");
            }
            playerProfileVo.setAvatarIcon(costumeData.get(0).getIconAvatar());
        } else if (playerProfileDto.getAvatarId() != null) {
            List<String> avatarIcons = characterDexDao.selectAvatarIconByLikelyCharacterId(playerProfileDto.getAvatarId());
            if(avatarIcons.isEmpty()) {
                throw new MetaDataQueryException("character_dex");
            }
            playerProfileVo.setAvatarIcon(avatarIcons.get(0));
        }
        if (playerProfileDto.getProfilePictureId() != null) { // 新版头像
            List<ProfilePicture> profilePictures = profilePictureDao.selectByProfilePictureId(playerProfileDto.getProfilePictureId());
            if(profilePictures.size() != 1) {
                throw new MetaDataQueryException("profile_picture");
            }
            playerProfileVo.setAvatarIcon(profilePictures.get(0).getIcon());
        }
        List<CharacterProfileVo> characterProfileVoList = new ArrayList<>();
        for(CharacterProfileDto characterProfileDto : playerProfileDto.getCharacters()) {
            characterProfileVoList.add(convertCharacterProfileToVo(characterProfileDto));
        }
        playerProfileVo.setCharacters(characterProfileVoList);
        playerProfileVo.setCharacterAvailable(playerProfileDto.getCharacterAvailable());
        return playerProfileVo;
    }

    private CharacterProfileVo convertCharacterProfileToVo(CharacterProfileDto characterProfileDto) {
        CharacterDexDao characterDexDao = knkDatabase.getCharacterDexDao();
        CostumeDexDao costumeDexDao = knkDatabase.getCostumeDexDao();

        CharacterProfileVo characterProfileVo = new CharacterProfileVo();
        List<CharacterDex> characterDataList = characterDexDao.selectByCharacterId(characterProfileDto.getCharacterId());
        if(characterDataList.size() != 1) {
            throw new MetaDataQueryException("character_dex");
        }
        CharacterDex characterData = characterDataList.get(0);
        characterProfileVo.setCharacterName(characterData.getCharacterName());
        characterProfileVo.setUid(characterProfileDto.getUid());
        if(characterProfileDto.getCostumeId() != null) {
            List<CostumeDex> costumeDataList = costumeDexDao.selectByCostumeId(characterProfileDto.getCostumeId());
            if(costumeDataList.size() != 1) {
                throw new MetaDataQueryException("costume_dex");
            }
            characterProfileVo.setAvatarIcon(costumeDataList.get(0).getIconAvatar());
            characterProfileVo.setArtIcon(costumeDataList.get(0).getIconArt());
            characterProfileVo.setCardIcon(costumeDataList.get(0).getIconCard());
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

        characterProfileVo.setBaseHp(characterProfileDto.getBaseHp());
        characterProfileVo.setPlusHp(characterProfileDto.getPlusHp());
        characterProfileVo.setBaseAtk(characterProfileDto.getBaseAtk());
        characterProfileVo.setPlusAtk(characterProfileDto.getPlusAtk());
        characterProfileVo.setBaseDef(characterProfileDto.getBaseDef());
        characterProfileVo.setPlusDef(characterProfileDto.getPlusDef());
        characterProfileVo.setMastery(characterProfileDto.getMastery());
        characterProfileVo.setCritRate(characterProfileDto.getCritRate());
        characterProfileVo.setCritDmg(characterProfileDto.getCritDmg());
        characterProfileVo.setRecharge(characterProfileDto.getRecharge());
        characterProfileVo.setDmgUp(characterProfileDto.getDmgUp());
        characterProfileVo.setHealUp(characterProfileDto.getHealUp());

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
        characterProfileVo.setEvaluations(artifactEvaluateManager.evaluateArtifacts(characterProfileDto));
        return characterProfileVo;
    }

    private WeaponProfileVo convertWeaponProfileToVo(WeaponProfileDto weaponProfileDto) {
        WeaponDexDao weaponDexDao = knkDatabase.getWeaponDexDao();
        WeaponProfileVo weaponProfileVo = new WeaponProfileVo();
        List<WeaponDex> weaponDataList = weaponDexDao.selectByWeaponId(weaponProfileDto.getWeaponId());
        if(weaponDataList.size() != 1) {
            throw new MetaDataQueryException("weapon_dex");
        }
        WeaponDex weaponData = weaponDataList.get(0);
        weaponProfileVo.setWeaponName(weaponData.getWeaponName());
        if (weaponProfileDto.getPhase() >= 2) {
            weaponProfileVo.setWeaponIcon(weaponData.getIconAwaken());
        } else {
            weaponProfileVo.setWeaponIcon(weaponData.getIconInitial());
        }
        weaponProfileVo.setLevel(weaponProfileDto.getLevel());
        weaponProfileVo.setRefineRank(weaponProfileDto.getRefineRank());
        weaponProfileVo.setBaseAtk(weaponProfileDto.getBaseAtk());
        weaponProfileVo.setAttribute(weaponProfileDto.getAttribute());
        weaponProfileVo.setAttributeVal(weaponProfileDto.getAttributeVal());
        return weaponProfileVo;
    }

    private ArtifactProfileVo convertArtifactProfileToVo(ArtifactProfileDto artifactProfileDto) {
        ArtifactDexDao artifactDexDao = knkDatabase.getArtifactDexDao();
        ArtifactProfileVo artifactProfileVo = new ArtifactProfileVo();
        List<ArtifactDex> artifactDataList = artifactDexDao.selectBySetIdAndPosition(artifactProfileDto.getSetId(), artifactProfileDto.getPosition());
        if(artifactDataList.size() != 1) {
            throw new MetaDataQueryException("artifact_dex");
        }
        ArtifactDex artifactData = artifactDataList.get(0);
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

    private <T> void checkResponse(ResponseEntity<T> responseEntity) {
        if(responseEntity == null) {
            throw new RequestErrorException("网络错误，重试超限");
        }
        if(responseEntity.getCode() == 404) {
            throw new RequestErrorException("uid不存在，米哈游说的");
        }
        if(responseEntity.getCode() == 500) {
            throw new RequestErrorException("服务器内部错误");
        }
        if(responseEntity.getCode() == 503) {
            throw new RequestErrorException("数据源访问错误");
        }
        if(responseEntity.getCode() != 200) {
            throw new RequestErrorException("未知错误，状态码："+responseEntity.getCode());
        }
    }
}
