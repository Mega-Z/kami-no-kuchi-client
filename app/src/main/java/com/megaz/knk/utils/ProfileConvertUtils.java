package com.megaz.knk.utils;

import com.megaz.knk.computation.CharacterOverview;
import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.constant.SourceTalentEnum;
import com.megaz.knk.dto.ArtifactProfileDto;
import com.megaz.knk.dto.CharacterProfileDto;
import com.megaz.knk.dto.PlayerProfileDto;
import com.megaz.knk.dto.WeaponProfileDto;
import com.megaz.knk.entity.ArtifactInstance;
import com.megaz.knk.entity.CharacterDex;
import com.megaz.knk.entity.CharacterProfile;
import com.megaz.knk.entity.PlayerProfile;
import com.megaz.knk.vo.WeaponProfileVo;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProfileConvertUtils {

    public static PlayerProfile convertPlayerProfileToEntity(PlayerProfileDto playerProfileDto) {
        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.setUid(playerProfileDto.getUid());
        playerProfile.setPlayerName(playerProfileDto.getNickname());
        playerProfile.setSignature(playerProfileDto.getSign());
        playerProfile.setAvatarId(playerProfileDto.getAvatarId());
        playerProfile.setCostumeId(playerProfileDto.getCostumeId());
        playerProfile.setProfilePictureId(playerProfileDto.getProfilePictureId());
        return playerProfile;
    }

    public static CharacterProfile convertCharacterProfileToEntity(CharacterProfileDto characterProfileDto) {
        CharacterProfile characterProfile = new CharacterProfile();
        characterProfile.setUid(characterProfileDto.getUid());
        characterProfile.setCharacterId(characterProfileDto.getCharacterId());
        characterProfile.setCostumeId(characterProfileDto.getCostumeId());
        characterProfile.setLevel(characterProfileDto.getLevel());
        characterProfile.setPhase(characterProfileDto.getPhase());
        characterProfile.setFetter(characterProfileDto.getFetter());
        characterProfile.setConstellation(characterProfileDto.getConstellation());
        characterProfile.setUpdateTime(characterProfileDto.getUpdateTime());

        characterProfile.setAttributeBaseHp(characterProfileDto.getBaseHp());
        characterProfile.setAttributeBaseAtk(characterProfileDto.getBaseAtk());
        characterProfile.setAttributeBaseDef(characterProfileDto.getBaseDef());
        characterProfile.setAttributePlusHp(characterProfileDto.getPlusHp());
        characterProfile.setAttributePlusAtk(characterProfileDto.getPlusAtk());
        characterProfile.setAttributePlusDef(characterProfileDto.getPlusDef());
        characterProfile.setAttributeMastery(characterProfileDto.getMastery());
        characterProfile.setAttributeCritRate(characterProfileDto.getCritRate());
        characterProfile.setAttributeCritDmg(characterProfileDto.getCritDmg());
        characterProfile.setAttributeRecharge(characterProfileDto.getRecharge());
        Map<ElementEnum, Double> dmgUpMap = characterProfileDto.getDmgUp();
        characterProfile.setAttributeDmgPyro(dmgUpMap.get(ElementEnum.PYRO));
        characterProfile.setAttributeDmgHydro(dmgUpMap.get(ElementEnum.HYDRO));
        characterProfile.setAttributeDmgCryo(dmgUpMap.get(ElementEnum.CRYO));
        characterProfile.setAttributeDmgElectro(dmgUpMap.get(ElementEnum.ELECTRO));
        characterProfile.setAttributeDmgAnemo(dmgUpMap.get(ElementEnum.ANEMO));
        characterProfile.setAttributeDmgGeo(dmgUpMap.get(ElementEnum.GEO));
        characterProfile.setAttributeDmgDendro(dmgUpMap.get(ElementEnum.DENDRO));
        characterProfile.setAttributeDmgPhy(dmgUpMap.get(ElementEnum.PHYSICAL));
        characterProfile.setAttributeHeal(characterProfileDto.getHealUp());

        characterProfile.setTalentABase(characterProfileDto.getTalentABaseLevel());
        characterProfile.setTalentEBase(characterProfileDto.getTalentEBaseLevel());
        characterProfile.setTalentQBase(characterProfileDto.getTalentQBaseLevel());
        characterProfile.setTalentAPlus(characterProfileDto.getTalentAPlusLevel());
        characterProfile.setTalentEPlus(characterProfileDto.getTalentEPlusLevel());
        characterProfile.setTalentQPlus(characterProfileDto.getTalentQPlusLevel());

        WeaponProfileDto weaponProfileDto = characterProfileDto.getWeapon();
        characterProfile.setWeaponId(weaponProfileDto.getWeaponId());
        characterProfile.setWeaponLevel(weaponProfileDto.getLevel());
        characterProfile.setWeaponRefineRank(weaponProfileDto.getRefineRank());
        characterProfile.setWeaponBaseAtk(weaponProfileDto.getBaseAtk());
        characterProfile.setWeaponAttribute(weaponProfileDto.getAttribute());
        characterProfile.setWeaponAttributeValue(weaponProfileDto.getAttributeVal());
        characterProfile.setWeaponPhase(weaponProfileDto.getPhase());

        Map<ArtifactPositionEnum, ArtifactProfileDto> artifactInstances = characterProfileDto.getArtifacts();
        if (artifactInstances.containsKey(ArtifactPositionEnum.FLOWER)) {
            characterProfile.setArtifactInstanceFlowerId(Objects.requireNonNull(
                    artifactInstances.get(ArtifactPositionEnum.FLOWER)).getArtifactInstanceId());
        }
        if (artifactInstances.containsKey(ArtifactPositionEnum.PLUME)) {
            characterProfile.setArtifactInstancePlumeId(Objects.requireNonNull(
                    artifactInstances.get(ArtifactPositionEnum.PLUME)).getArtifactInstanceId());
        }
        if (artifactInstances.containsKey(ArtifactPositionEnum.SANDS)) {
            characterProfile.setArtifactInstanceSandsId(Objects.requireNonNull(
                    artifactInstances.get(ArtifactPositionEnum.SANDS)).getArtifactInstanceId());
        }
        if (artifactInstances.containsKey(ArtifactPositionEnum.GOBLET)) {
            characterProfile.setArtifactInstanceGobletId(Objects.requireNonNull(
                    artifactInstances.get(ArtifactPositionEnum.GOBLET)).getArtifactInstanceId());
        }
        if (artifactInstances.containsKey(ArtifactPositionEnum.CIRCLET)) {
            characterProfile.setArtifactInstanceCircletId(Objects.requireNonNull(
                    artifactInstances.get(ArtifactPositionEnum.CIRCLET)).getArtifactInstanceId());
        }

        return characterProfile;
    }

    public static ArtifactInstance convertArtifactProfileToEntity(ArtifactProfileDto artifactProfileDto, CharacterProfileDto characterProfileDto) {
        ArtifactInstance artifactInstance = new ArtifactInstance();
        artifactInstance.setUid(characterProfileDto.getUid());
        artifactInstance.setCharacterId(characterProfileDto.getCharacterId());
        artifactInstance.setArtifactInstanceId(artifactProfileDto.getArtifactInstanceId());
        artifactInstance.setPosition(artifactProfileDto.getPosition());
        artifactInstance.setSetId(artifactProfileDto.getSetId());
        artifactInstance.setStar(artifactProfileDto.getStar());
        artifactInstance.setLevel(artifactProfileDto.getLevel());
        artifactInstance.setMainAttribute(artifactProfileDto.getMainAttribute());
        artifactInstance.setMainAttributeValue(artifactProfileDto.getMainAttributeVal());
        int subAttributeNum = artifactProfileDto.getSubAttributes().size();
        if (subAttributeNum >= 1) {
            artifactInstance.setSubAttribute1(artifactProfileDto.getSubAttributes().get(0));
            artifactInstance.setSubAttribute1Value(artifactProfileDto.getSubAttributesVal().get(0));
            artifactInstance.setSubAttribute1Count(artifactProfileDto.getSubAttributesCnt().get(0));
        }
        if (subAttributeNum >= 2) {
            artifactInstance.setSubAttribute2(artifactProfileDto.getSubAttributes().get(1));
            artifactInstance.setSubAttribute2Value(artifactProfileDto.getSubAttributesVal().get(1));
            artifactInstance.setSubAttribute2Count(artifactProfileDto.getSubAttributesCnt().get(1));
        }
        if (subAttributeNum >= 3) {
            artifactInstance.setSubAttribute3(artifactProfileDto.getSubAttributes().get(2));
            artifactInstance.setSubAttribute3Value(artifactProfileDto.getSubAttributesVal().get(2));
            artifactInstance.setSubAttribute3Count(artifactProfileDto.getSubAttributesCnt().get(2));
        }
        if (subAttributeNum >= 4) {
            artifactInstance.setSubAttribute4(artifactProfileDto.getSubAttributes().get(3));
            artifactInstance.setSubAttribute4Value(artifactProfileDto.getSubAttributesVal().get(3));
            artifactInstance.setSubAttribute4Count(artifactProfileDto.getSubAttributesCnt().get(3));
        }

        return artifactInstance;
    }

    public static PlayerProfileDto convertPlayerProfileToDto(PlayerProfile playerProfile) {
        PlayerProfileDto playerProfileDto = new PlayerProfileDto();

        playerProfileDto.setUid(playerProfile.getUid());
        playerProfileDto.setNickname(playerProfile.getPlayerName());
        playerProfileDto.setSign(playerProfile.getSignature());
        playerProfileDto.setAvatarId(playerProfile.getAvatarId());
        playerProfileDto.setCostumeId(playerProfile.getCostumeId());
        playerProfileDto.setProfilePictureId(playerProfile.getProfilePictureId());

        return playerProfileDto;
    }

    public static CharacterProfileDto convertCharacterProfileToDto(CharacterProfile characterProfile, CharacterDex characterData) {
        CharacterProfileDto characterProfileDto = new CharacterProfileDto();

        characterProfileDto.setUid(characterProfile.getUid());
        characterProfileDto.setCharacterId(characterProfile.getCharacterId());
        characterProfileDto.setUpdateTime(characterProfile.getUpdateTime());
        characterProfileDto.setCostumeId(characterProfile.getCostumeId());
        characterProfileDto.setElement(characterData.getElement());
        characterProfileDto.setLevel(characterProfile.getLevel());
        characterProfileDto.setPhase(characterProfile.getPhase());
        characterProfileDto.setFetter(characterProfile.getFetter());
        characterProfileDto.setConstellation(characterProfile.getConstellation());
        characterProfileDto.setBaseHp(characterProfile.getAttributeBaseHp());
        characterProfileDto.setPlusHp(characterProfile.getAttributePlusHp());
        characterProfileDto.setBaseAtk(characterProfile.getAttributeBaseAtk());
        characterProfileDto.setPlusAtk(characterProfile.getAttributePlusAtk());
        characterProfileDto.setBaseDef(characterProfile.getAttributeBaseDef());
        characterProfileDto.setPlusDef(characterProfile.getAttributePlusDef());
        characterProfileDto.setMastery(characterProfile.getAttributeMastery());
        characterProfileDto.setCritRate(characterProfile.getAttributeCritRate());
        characterProfileDto.setCritDmg(characterProfile.getAttributeCritDmg());
        characterProfileDto.setRecharge(characterProfile.getAttributeRecharge());
        Map<ElementEnum, Double> dmgUpMap = new HashMap<>();
        dmgUpMap.put(ElementEnum.PYRO, characterProfile.getAttributeDmgPyro());
        dmgUpMap.put(ElementEnum.HYDRO, characterProfile.getAttributeDmgHydro());
        dmgUpMap.put(ElementEnum.CRYO, characterProfile.getAttributeDmgCryo());
        dmgUpMap.put(ElementEnum.ELECTRO, characterProfile.getAttributeDmgElectro());
        dmgUpMap.put(ElementEnum.ANEMO, characterProfile.getAttributeDmgAnemo());
        dmgUpMap.put(ElementEnum.GEO, characterProfile.getAttributeDmgGeo());
        dmgUpMap.put(ElementEnum.DENDRO, characterProfile.getAttributeDmgDendro());
        dmgUpMap.put(ElementEnum.PHYSICAL, characterProfile.getAttributeDmgPhy());
        characterProfileDto.setDmgUp(dmgUpMap);
        characterProfileDto.setHealUp(characterProfile.getAttributeHeal());
        characterProfileDto.setTalentABaseLevel(characterProfile.getTalentABase());
        characterProfileDto.setTalentEBaseLevel(characterProfile.getTalentEBase());
        characterProfileDto.setTalentQBaseLevel(characterProfile.getTalentQBase());
        characterProfileDto.setTalentAPlusLevel(characterProfile.getTalentAPlus());
        characterProfileDto.setTalentEPlusLevel(characterProfile.getTalentEPlus());
        characterProfileDto.setTalentQPlusLevel(characterProfile.getTalentQPlus());

        WeaponProfileDto weaponProfileDto = new WeaponProfileDto();

        weaponProfileDto.setWeaponId(characterProfile.getWeaponId());
        weaponProfileDto.setLevel(characterProfile.getWeaponLevel());
        weaponProfileDto.setRefineRank(characterProfile.getWeaponRefineRank());
        weaponProfileDto.setBaseAtk(characterProfile.getWeaponBaseAtk());
        weaponProfileDto.setAttribute(characterProfile.getWeaponAttribute());
        weaponProfileDto.setAttributeVal(characterProfile.getWeaponAttributeValue());
        weaponProfileDto.setPhase(characterProfile.getWeaponPhase());

        characterProfileDto.setWeapon(weaponProfileDto);

        return characterProfileDto;
    }

    public static ArtifactProfileDto convertArtifactProfileToDto(ArtifactInstance artifactInstance) {
        ArtifactProfileDto artifactProfileDto = new ArtifactProfileDto();
        artifactProfileDto.setArtifactInstanceId(artifactInstance.getArtifactInstanceId());
        artifactProfileDto.setPosition(artifactInstance.getPosition());
        artifactProfileDto.setSetId(artifactInstance.getSetId());
        artifactProfileDto.setLevel(artifactInstance.getLevel());
        artifactProfileDto.setStar(artifactInstance.getStar());
        artifactProfileDto.setMainAttribute(artifactInstance.getMainAttribute());
        artifactProfileDto.setMainAttributeVal(artifactInstance.getMainAttributeValue());
        artifactProfileDto.setSubAttributes(Stream.of(
                artifactInstance.getSubAttribute1(), artifactInstance.getSubAttribute2(),
                artifactInstance.getSubAttribute3(), artifactInstance.getSubAttribute4()
        ).filter(Objects::nonNull).collect(Collectors.toList()));
        artifactProfileDto.setSubAttributesVal(Stream.of(
                artifactInstance.getSubAttribute1Value(), artifactInstance.getSubAttribute2Value(),
                artifactInstance.getSubAttribute3Value(), artifactInstance.getSubAttribute4Value()
        ).filter(Objects::nonNull).collect(Collectors.toList()));
        artifactProfileDto.setSubAttributesCnt(Stream.of(
                artifactInstance.getSubAttribute1Count(), artifactInstance.getSubAttribute2Count(),
                artifactInstance.getSubAttribute3Count(), artifactInstance.getSubAttribute4Count()
        ).filter(Objects::nonNull).collect(Collectors.toList()));

        return artifactProfileDto;
    }

    public static boolean isSameCharacterProfile(CharacterProfileDto characterProfile1, CharacterProfileDto characterProfile2) {
        return Objects.equals(characterProfile1.getUid(), characterProfile2.getUid()) &&
                Objects.equals(characterProfile1.getCharacterId(), characterProfile2.getCharacterId()) &&
                Objects.equals(characterProfile1.getUid(), characterProfile2.getUid()) &&
                Objects.equals(characterProfile1.getCharacterId(), characterProfile2.getCharacterId()) &&
                Objects.equals(characterProfile1.getCostumeId(), characterProfile2.getCostumeId()) &&
                Objects.equals(characterProfile1.getLevel(), characterProfile2.getLevel()) &&
                Objects.equals(characterProfile1.getPhase(), characterProfile2.getPhase()) &&
                Objects.equals(characterProfile1.getConstellation(), characterProfile2.getConstellation()) &&
                Objects.equals(characterProfile1.getTalentABaseLevel(), characterProfile2.getTalentABaseLevel()) &&
                Objects.equals(characterProfile1.getTalentAPlusLevel(), characterProfile2.getTalentAPlusLevel()) &&
                Objects.equals(characterProfile1.getTalentEBaseLevel(), characterProfile2.getTalentEBaseLevel()) &&
                Objects.equals(characterProfile1.getTalentEPlusLevel(), characterProfile2.getTalentEPlusLevel()) &&
                Objects.equals(characterProfile1.getTalentQBaseLevel(), characterProfile2.getTalentQBaseLevel()) &&
                Objects.equals(characterProfile1.getTalentQPlusLevel(), characterProfile2.getTalentQPlusLevel()) &&
                Objects.equals(characterProfile1.getWeapon().getWeaponId(), characterProfile2.getWeapon().getWeaponId()) &&
                Objects.equals(characterProfile1.getWeapon().getLevel(), characterProfile2.getWeapon().getLevel()) &&
                Objects.equals(characterProfile1.getWeapon().getPhase(), characterProfile2.getWeapon().getPhase()) &&
                Objects.equals(characterProfile1.getWeapon().getRefineRank(), characterProfile2.getWeapon().getRefineRank()) &&
                Objects.equals(characterProfile1.getBaseAtk(), characterProfile2.getBaseAtk()) &&
                Objects.equals(characterProfile1.getPlusAtk(), characterProfile2.getPlusAtk()) &&
                Objects.equals(characterProfile1.getBaseHp(), characterProfile2.getBaseHp()) &&
                Objects.equals(characterProfile1.getPlusHp(), characterProfile2.getPlusHp()) &&
                Objects.equals(characterProfile1.getBaseDef(), characterProfile2.getBaseDef()) &&
                Objects.equals(characterProfile1.getPlusDef(), characterProfile2.getPlusDef()) &&
                Objects.equals(characterProfile1.getMastery(), characterProfile2.getMastery()) &&
                Objects.equals(characterProfile1.getRecharge(), characterProfile2.getRecharge()) &&
                Objects.equals(characterProfile1.getCritRate(), characterProfile2.getCritRate()) &&
                Objects.equals(characterProfile1.getCritDmg(), characterProfile2.getCritDmg()) &&
                Objects.equals(characterProfile1.getHealUp(), characterProfile2.getHealUp()) &&
                Objects.equals(characterProfile1.getDmgUp().get(ElementEnum.PHYSICAL), characterProfile2.getDmgUp().get(ElementEnum.PHYSICAL)) &&
                Objects.equals(characterProfile1.getDmgUp().get(ElementEnum.PYRO), characterProfile2.getDmgUp().get(ElementEnum.PYRO)) &&
                Objects.equals(characterProfile1.getDmgUp().get(ElementEnum.CRYO), characterProfile2.getDmgUp().get(ElementEnum.CRYO)) &&
                Objects.equals(characterProfile1.getDmgUp().get(ElementEnum.HYDRO), characterProfile2.getDmgUp().get(ElementEnum.HYDRO)) &&
                Objects.equals(characterProfile1.getDmgUp().get(ElementEnum.ELECTRO), characterProfile2.getDmgUp().get(ElementEnum.ELECTRO)) &&
                Objects.equals(characterProfile1.getDmgUp().get(ElementEnum.ANEMO), characterProfile2.getDmgUp().get(ElementEnum.ANEMO)) &&
                Objects.equals(characterProfile1.getDmgUp().get(ElementEnum.GEO), characterProfile2.getDmgUp().get(ElementEnum.GEO)) &&
                Objects.equals(characterProfile1.getDmgUp().get(ElementEnum.DENDRO), characterProfile2.getDmgUp().get(ElementEnum.DENDRO)) &&
                isSameArtifactInstance(characterProfile1.getArtifacts().get(ArtifactPositionEnum.FLOWER),
                        characterProfile2.getArtifacts().get(ArtifactPositionEnum.FLOWER)) &&
                isSameArtifactInstance(characterProfile1.getArtifacts().get(ArtifactPositionEnum.PLUME),
                        characterProfile2.getArtifacts().get(ArtifactPositionEnum.PLUME)) &&
                isSameArtifactInstance(characterProfile1.getArtifacts().get(ArtifactPositionEnum.SANDS),
                        characterProfile2.getArtifacts().get(ArtifactPositionEnum.SANDS)) &&
                isSameArtifactInstance(characterProfile1.getArtifacts().get(ArtifactPositionEnum.GOBLET),
                        characterProfile2.getArtifacts().get(ArtifactPositionEnum.GOBLET)) &&
                isSameArtifactInstance(characterProfile1.getArtifacts().get(ArtifactPositionEnum.CIRCLET),
                        characterProfile2.getArtifacts().get(ArtifactPositionEnum.CIRCLET));
    }

    public static CharacterProfileDto copyCharacterProfile(CharacterProfileDto source) {
        CharacterProfileDto target = new CharacterProfileDto();
        target.setUid(source.getUid());
        target.setCharacterId(source.getCharacterId());
        target.setCostumeId(source.getCostumeId());
        target.setElement(source.getElement());
        target.setLevel(source.getLevel());
        target.setPhase(source.getPhase());
        target.setFetter(source.getFetter());
        target.setConstellation(source.getConstellation());
        target.setNewData(false);
        target.setBaseHp(source.getBaseHp());
        target.setPlusHp(source.getPlusHp());
        target.setBaseAtk(source.getBaseAtk());
        target.setPlusAtk(source.getPlusAtk());
        target.setBaseDef(source.getBaseDef());
        target.setPlusDef(source.getPlusDef());
        target.setMastery(source.getMastery());
        target.setCritRate(source.getCritRate());
        target.setCritDmg(source.getCritDmg());
        target.setRecharge(source.getRecharge());
        Map<ElementEnum, Double> dmgUpMap = new HashMap<>();
        for(ElementEnum element:GenshinConstantMeta.ELEMENT_LIST) {
            dmgUpMap.put(element, source.getDmgUp().get(element));
        }
        target.setDmgUp(dmgUpMap);
        target.setHealUp(source.getHealUp());

        target.setTalentABaseLevel(source.getTalentABaseLevel());
        target.setTalentEBaseLevel(source.getTalentEBaseLevel());
        target.setTalentQBaseLevel(source.getTalentQBaseLevel());
        target.setTalentAPlusLevel(source.getTalentAPlusLevel());
        target.setTalentEPlusLevel(source.getTalentEPlusLevel());
        target.setTalentQPlusLevel(source.getTalentQPlusLevel());

        WeaponProfileDto weaponProfileDto = new WeaponProfileDto();
        weaponProfileDto.setWeaponId(source.getWeapon().getWeaponId());
        weaponProfileDto.setLevel(source.getWeapon().getLevel());
        weaponProfileDto.setRefineRank(source.getWeapon().getRefineRank());
        weaponProfileDto.setBaseAtk(source.getWeapon().getBaseAtk());
        weaponProfileDto.setAttribute(source.getWeapon().getAttribute());
        weaponProfileDto.setAttributeVal(source.getWeapon().getAttributeVal());
        weaponProfileDto.setPhase(source.getWeapon().getPhase());
        target.setWeapon(weaponProfileDto);

        Map<ArtifactPositionEnum, ArtifactProfileDto> artifactProfileDtoMap = new HashMap<>();
        for (ArtifactPositionEnum position : GenshinConstantMeta.ARTIFACT_POSITION_LIST) {
            if (source.getArtifacts().containsKey(position)) {
                artifactProfileDtoMap.put(position, source.getArtifacts().get(position));
            }
        }
        target.setArtifacts(artifactProfileDtoMap);

        return target;
    }

    public static CharacterOverview extractCharacterOverview(CharacterProfileDto characterProfileDto) {
        CharacterOverview characterOverview = new CharacterOverview();
        characterOverview.setCharacterId(characterProfileDto.getCharacterId());
        characterOverview.setElement(characterProfileDto.getElement());
        characterOverview.setLevel(characterProfileDto.getLevel());
        characterOverview.setPhase(characterProfileDto.getPhase());
        characterOverview.setConstellation(characterProfileDto.getConstellation());
        Map<SourceTalentEnum, Integer> talentLevel = new HashMap<>();
        talentLevel.put(SourceTalentEnum.A, characterProfileDto.getTalentABaseLevel());
        talentLevel.put(SourceTalentEnum.E, characterProfileDto.getTalentEBaseLevel());
        talentLevel.put(SourceTalentEnum.Q, characterProfileDto.getTalentQBaseLevel());
        characterOverview.setTalentLevel(talentLevel);
        Map<SourceTalentEnum, Integer> talentLevelPlus = new HashMap<>();
        talentLevelPlus.put(SourceTalentEnum.A, characterProfileDto.getTalentAPlusLevel());
        talentLevelPlus.put(SourceTalentEnum.E, characterProfileDto.getTalentEPlusLevel());
        talentLevelPlus.put(SourceTalentEnum.Q, characterProfileDto.getTalentQPlusLevel());
        characterOverview.setTalentLevelPlus(talentLevelPlus);
        characterOverview.setWeaponId(characterProfileDto.getWeapon().getWeaponId());
        characterOverview.setWeaponLevel(characterProfileDto.getWeapon().getLevel());
        characterOverview.setWeaponPhase(characterProfileDto.getWeapon().getPhase());
        characterOverview.setWeaponRefinement(characterProfileDto.getWeapon().getRefineRank());
        return characterOverview;
    }

    public static boolean isSameArtifactInstance(ArtifactProfileDto artifact1, ArtifactProfileDto artifact2) {
        if (artifact1 == null && artifact2 == null) {
            return true;
        } else if (artifact1 != null && artifact2 != null) {
            return Objects.equals(artifact1.getArtifactInstanceId(), artifact2.getArtifactInstanceId());
        } else {
            return false;
        }
    }

    public static boolean isPromotingLevel(int level) {
        return level == 20 || level == 40 || level == 50 || level == 60 || level == 70 || level == 80;
    }

    public static int getPhaseByLevel(int level, boolean promote) {
        int phase = 0;
        if (level > 20) phase++;
        if (level > 40) phase++;
        if (level > 50) phase++;
        if (level > 60) phase++;
        if (level > 70) phase++;
        if (level > 80) phase++;
        if (isPromotingLevel(level) && promote) phase++;
        return phase;
    }

    public static boolean isPromoted(int level, int phase) {
        if (isPromotingLevel(level)) {
            int lowerPhase = 0;
            if (level > 20) lowerPhase++;
            if (level > 40) lowerPhase++;
            if (level > 50) lowerPhase++;
            if (level > 60) lowerPhase++;
            if (level > 70) lowerPhase++;
            return phase > lowerPhase;
        } else {
            return false;
        }
    }

}
