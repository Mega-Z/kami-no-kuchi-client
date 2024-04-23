package com.megaz.knk.manager;

import android.content.Context;

import com.megaz.knk.KnkDatabase;
import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.dao.ArtifactInstanceDao;
import com.megaz.knk.dao.CharacterDexDao;
import com.megaz.knk.dao.CharacterProfileDao;
import com.megaz.knk.dao.PlayerProfileDao;
import com.megaz.knk.dto.ArtifactProfileDto;
import com.megaz.knk.dto.CharacterProfileDto;
import com.megaz.knk.dto.PlayerProfileDto;
import com.megaz.knk.entity.ArtifactInstance;
import com.megaz.knk.entity.CharacterDex;
import com.megaz.knk.entity.CharacterProfile;
import com.megaz.knk.entity.PlayerProfile;
import com.megaz.knk.exception.MetaDataQueryException;
import com.megaz.knk.exception.ProfileQueryException;
import com.megaz.knk.utils.ProfileConvertUtils;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalProfileManager {
    private static LocalProfileManager instance;
    private final KnkDatabase knkDatabase;

    public static synchronized LocalProfileManager getInstance(Context context) {
        if (instance == null) {
            instance = new LocalProfileManager(context);
        }
        return instance;
    }

    private LocalProfileManager(Context context) {
        knkDatabase = KnkDatabase.getKnkDatabase(context);
    }

    public PlayerProfileDto queryLocalProfile(String uid) {
        PlayerProfileDao playerProfileDao = knkDatabase.getPlayerProfileDao();
        PlayerProfile playerProfile = playerProfileDao.selectByUid(uid);
        if (playerProfile == null) {
            return null;
        }

        PlayerProfileDto playerProfileDto = ProfileConvertUtils.convertPlayerProfileToDto(playerProfile);
        playerProfileDto.setCharacterAvailable(true);
        Map<String, CharacterProfileDto> latestCharacterProfileMap = getLatestCharacterProfilesByUid(uid);
        playerProfileDto.setCharacters(new ArrayList<>(latestCharacterProfileMap.values()));
        return playerProfileDto;
    }

    public void updateLocalProfile(PlayerProfileDto playerProfileDto) {
        PlayerProfileDao playerProfileDao = knkDatabase.getPlayerProfileDao();
        CharacterProfileDao characterProfileDao = knkDatabase.getCharacterProfileDao();
        ArtifactInstanceDao artifactInstanceDao = knkDatabase.getArtifactInstanceDao();

        playerProfileDao.insert(ProfileConvertUtils.convertPlayerProfileToEntity(playerProfileDto));
        String uid = playerProfileDto.getUid();
        Map<String, CharacterProfileDto> latestCharacters = getLatestCharacterProfilesByUid(uid);
        List<CharacterProfile> characterProfileInsertList = new ArrayList<>();
        List<ArtifactInstance> artifactInstanceInsertList = new ArrayList<>();
        for (CharacterProfileDto characterProfileDto : playerProfileDto.getCharacters()) {
            if (latestCharacters.containsKey(characterProfileDto.getCharacterId()) &&
                    ProfileConvertUtils.isSameCharacterProfile(characterProfileDto,
                            Objects.requireNonNull(latestCharacters.get(characterProfileDto.getCharacterId())))) {
                continue;
            }
            characterProfileInsertList.add(ProfileConvertUtils.convertCharacterProfileToEntity(characterProfileDto));
            for (ArtifactProfileDto artifactProfileDto : characterProfileDto.getArtifacts().values()) {
                artifactInstanceInsertList.add(ProfileConvertUtils.convertArtifactProfileToEntity(artifactProfileDto, characterProfileDto));
            }
        }

        characterProfileDao.batchInsert(characterProfileInsertList.toArray(new CharacterProfile[0]));
        artifactInstanceDao.batchInsert(artifactInstanceInsertList.toArray(new ArtifactInstance[0]));
    }

    public Map<String, CharacterProfileDto> getLatestCharacterProfilesByUid(String uid) {
        CharacterProfileDao characterProfileDao = knkDatabase.getCharacterProfileDao();
        CharacterDexDao characterDexDao = knkDatabase.getCharacterDexDao();
        List<CharacterProfile> latestCharacterProfileList = characterProfileDao.selectLatestByUid(uid);
        Map<String, CharacterProfileDto> latestCharacters = new HashMap<>();
        for (CharacterProfile characterProfile : latestCharacterProfileList) {
            List<CharacterDex> characterDataList =
                    characterDexDao.selectByCharacterId(characterProfile.getCharacterId());
            if (characterDataList.size() != 1) {
                throw new MetaDataQueryException("character_dex");
            }
            CharacterProfileDto characterProfileDto = ProfileConvertUtils.convertCharacterProfileToDto(characterProfile, characterDataList.get((0)));
            addArtifactProfileToCharacter(characterProfileDto, characterProfile);
            characterProfileDto.setNewData(false);
            latestCharacters.put(characterProfile.getCharacterId(), characterProfileDto);
        }
        return latestCharacters;
    }

    public List<CharacterProfileDto> getCharacterProfilesByCharacterIdAndUid(String characterId, String uid) {
        CharacterProfileDao characterProfileDao = knkDatabase.getCharacterProfileDao();
        CharacterDexDao characterDexDao = knkDatabase.getCharacterDexDao();

        List<CharacterDex> characterDataList =
                characterDexDao.selectByCharacterId(characterId);
        if (characterDataList.size() != 1) {
            throw new MetaDataQueryException("character_dex");
        }

        List<CharacterProfile> characterProfiles = characterProfileDao.selectByUidAndCharacterId(uid, characterId);
        List<CharacterProfileDto> characterProfileDtoList = new ArrayList<>();
        for (CharacterProfile characterProfile : characterProfiles) {
            CharacterProfileDto characterProfileDto = ProfileConvertUtils.convertCharacterProfileToDto(characterProfile, characterDataList.get((0)));
            addArtifactProfileToCharacter(characterProfileDto, characterProfile);
            characterProfileDto.setNewData(false);
            characterProfileDtoList.add(characterProfileDto);
        }

        return characterProfileDtoList;
    }

    private void addArtifactProfileToCharacter(CharacterProfileDto characterProfileDto, CharacterProfile characterProfile) {
        ArtifactInstanceDao artifactInstanceDao = knkDatabase.getArtifactInstanceDao();

        Map<ArtifactPositionEnum, ArtifactProfileDto> artifactProfileMap = new HashMap<>();
        List<String> artifactInstanceIdList = Stream.of(characterProfile.getArtifactInstanceFlowerId(),
                characterProfile.getArtifactInstancePlumeId(),
                characterProfile.getArtifactInstanceSandsId(),
                characterProfile.getArtifactInstanceGobletId(),
                characterProfile.getArtifactInstanceCircletId())
                .filter(Objects::nonNull).collect(Collectors.toList());
        for (String artifactInstanceId : artifactInstanceIdList) {
            ArtifactInstance artifactInstance = artifactInstanceDao.selectArtifactInstance(
                    characterProfile.getUid(), characterProfile.getCharacterId(), artifactInstanceId);
            if (artifactInstance == null) {
                throw new ProfileQueryException("圣遗物实例id不存在");
            }
            artifactProfileMap.put(artifactInstance.getPosition(),
                    ProfileConvertUtils.convertArtifactProfileToDto(artifactInstance));
        }
        characterProfileDto.setArtifacts(artifactProfileMap);
    }
}
