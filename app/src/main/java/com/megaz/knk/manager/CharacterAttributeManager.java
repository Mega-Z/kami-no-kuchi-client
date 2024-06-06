package com.megaz.knk.manager;

import android.content.Context;

import androidx.annotation.WorkerThread;

import com.megaz.knk.KnkDatabase;
import com.megaz.knk.computation.BuffEffect;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.computation.CharacterOverview;
import com.megaz.knk.computation.FightStatus;
import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.constant.SourceTalentEnum;
import com.megaz.knk.curve.CharacterBaseAttribute;
import com.megaz.knk.curve.WeaponBaseAttribute;
import com.megaz.knk.dao.CharacterDexDao;
import com.megaz.knk.dao.PromoteAttributeDao;
import com.megaz.knk.dao.WeaponDexDao;
import com.megaz.knk.dto.ArtifactProfileDto;
import com.megaz.knk.dto.CharacterProfileDto;
import com.megaz.knk.dto.WeaponProfileDto;
import com.megaz.knk.entity.CharacterDex;
import com.megaz.knk.entity.WeaponDex;
import com.megaz.knk.exception.MetaDataQueryException;
import com.megaz.knk.utils.MetaDataUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.transform.Source;

public class CharacterAttributeManager {

    private final KnkDatabase knkDatabase;
    private final BuffManager buffManager;

    public CharacterAttributeManager(Context context) {
        this.knkDatabase = KnkDatabase.getKnkDatabase(context);
        buffManager = new BuffManager(context);
    }

    @Deprecated
    public CharacterOverview createCharacterOverview(CharacterProfileDto characterProfileDto) {
        CharacterOverview characterOverview = new CharacterOverview();
        characterOverview.setCharacterId(characterProfileDto.getCharacterId());
        characterOverview.setElement(characterProfileDto.getElement());
        characterOverview.setLevel(characterProfileDto.getLevel());
        characterOverview.setPhase(characterProfileDto.getPhase());
        characterOverview.setConstellation(characterProfileDto.getConstellation());
        Map<SourceTalentEnum, Integer> talentLevel = new HashMap<>();
        talentLevel.put(SourceTalentEnum.A,
                characterProfileDto.getTalentABaseLevel() + characterProfileDto.getTalentAPlusLevel());
        talentLevel.put(SourceTalentEnum.E,
                characterProfileDto.getTalentEBaseLevel() + characterProfileDto.getTalentEPlusLevel());
        talentLevel.put(SourceTalentEnum.Q,
                characterProfileDto.getTalentQBaseLevel() + characterProfileDto.getTalentQPlusLevel());
        characterOverview.setTalentLevel(talentLevel);
        characterOverview.setWeaponId(characterProfileDto.getWeapon().getWeaponId());
        characterOverview.setWeaponLevel(characterProfileDto.getWeapon().getLevel());
        characterOverview.setWeaponPhase(characterProfileDto.getWeapon().getPhase());
        characterOverview.setWeaponRefinement(characterProfileDto.getWeapon().getRefineRank());
        return characterOverview;
    }

    @WorkerThread
    public CharacterAttribute createCharacterBaseAttribute(
            CharacterOverview characterOverview, Map<ArtifactPositionEnum, ArtifactProfileDto> artifacts) {
        CharacterDexDao characterDexDao = knkDatabase.getCharacterDexDao();
        WeaponDexDao weaponDexDao = knkDatabase.getWeaponDexDao();
        PromoteAttributeDao promoteAttributeDao = knkDatabase.getPromoteAttributeDao();

        CharacterDex characterDex = MetaDataUtils.queryCharacterDex(characterDexDao, characterOverview.getCharacterId());
        WeaponDex weaponDex = MetaDataUtils.queryWeaponDex(weaponDexDao, characterOverview.getWeaponId());

        CharacterAttribute characterAttribute = new CharacterAttribute();
        characterAttribute.setCharacterId(characterOverview.getCharacterId());
        characterAttribute.setElement(characterOverview.getElement());
        characterAttribute.setLevel(characterOverview.getLevel());
        characterAttribute.setPhase(characterOverview.getPhase());
        characterAttribute.setConstellation(characterOverview.getConstellation());
        characterAttribute.getTalentLevel().put(SourceTalentEnum.A,
                Objects.requireNonNull(characterOverview.getTalentLevel().get(SourceTalentEnum.A)) +
                        Objects.requireNonNull(characterOverview.getTalentLevelPlus().get(SourceTalentEnum.A)));
        characterAttribute.getTalentLevel().put(SourceTalentEnum.E,
                Objects.requireNonNull(characterOverview.getTalentLevel().get(SourceTalentEnum.E)) +
                        Objects.requireNonNull(characterOverview.getTalentLevelPlus().get(SourceTalentEnum.E)));
        characterAttribute.getTalentLevel().put(SourceTalentEnum.Q,
                Objects.requireNonNull(characterOverview.getTalentLevel().get(SourceTalentEnum.Q)) +
                        Objects.requireNonNull(characterOverview.getTalentLevelPlus().get(SourceTalentEnum.Q)));
        characterAttribute.setWeaponId(characterOverview.getWeaponId());
        characterAttribute.setWeaponRefinement(characterOverview.getWeaponRefinement());
        for (ArtifactPositionEnum position : GenshinConstantMeta.ARTIFACT_POSITION_LIST) {
            if (!artifacts.containsKey(position)) {
                continue;
            }
            String setId = Objects.requireNonNull(artifacts.get(position)).getSetId();
            characterAttribute.getArtifactSetCount().put(setId,
                    Objects.requireNonNull(characterAttribute.getArtifactSetCount().getOrDefault(setId, 0)) + 1);
        }

        Map<AttributeEnum, String> baseCurveNames = new HashMap<>();
        baseCurveNames.put(AttributeEnum.BASE_ATK, characterDex.getCurveBaseAtk());
        baseCurveNames.put(AttributeEnum.BASE_HP, characterDex.getCurveBaseHp());
        baseCurveNames.put(AttributeEnum.BASE_DEF, characterDex.getCurveBaseDef());
        Map<AttributeEnum, Double> characterBaseAttributeMulti =
                CharacterBaseAttribute.getBaseAttributeMultiByLevel(characterOverview.getLevel(), baseCurveNames);
        Map<AttributeEnum, Double> characterBaseAttributeAdd =
                MetaDataUtils.queryPromoteAttribute(promoteAttributeDao, characterDex.getPromoteId(), characterOverview.getPhase());
        Map<AttributeEnum, Double> weaponBaseAttributeMulti =
                WeaponBaseAttribute.getBaseAttributeMultiByLevel(characterOverview.getWeaponLevel(), weaponDex.getCurveBaseAtk(), weaponDex.getCurveAttribute());
        Map<AttributeEnum, Double> weaponBaseAttributeAdd =
                MetaDataUtils.queryPromoteAttribute(promoteAttributeDao, weaponDex.getPromoteId(), characterOverview.getWeaponPhase());

        // set base (white) attribute
        characterAttribute.setBaseAtk(
                characterDex.getBaseAtk() * Objects.requireNonNull(characterBaseAttributeMulti.get(AttributeEnum.BASE_ATK))
                        + Objects.requireNonNull(characterBaseAttributeAdd.getOrDefault(AttributeEnum.BASE_ATK, 0.))
                        + weaponDex.getBaseAtk() * Objects.requireNonNull(weaponBaseAttributeMulti.get(AttributeEnum.BASE_ATK))
                        + Objects.requireNonNull(weaponBaseAttributeAdd.getOrDefault(AttributeEnum.BASE_ATK, 0.))
        );
        characterAttribute.setBaseHp(
                characterDex.getBaseHp() * Objects.requireNonNull(characterBaseAttributeMulti.get(AttributeEnum.BASE_HP))
                        + Objects.requireNonNull(characterBaseAttributeAdd.getOrDefault(AttributeEnum.BASE_HP, 0.))
        );
        characterAttribute.setBaseDef(
                characterDex.getBaseDef() * Objects.requireNonNull(characterBaseAttributeMulti.get(AttributeEnum.BASE_DEF))
                        + Objects.requireNonNull(characterBaseAttributeAdd.getOrDefault(AttributeEnum.BASE_DEF, 0.))
        );
        characterAttribute.setWeaponBaseAtk(
                weaponDex.getBaseAtk() * Objects.requireNonNull(weaponBaseAttributeMulti.get(AttributeEnum.BASE_ATK))
                        + Objects.requireNonNull(weaponBaseAttributeAdd.getOrDefault(AttributeEnum.BASE_ATK, 0.)));
        // add character attribute
        for (AttributeEnum attribute : characterBaseAttributeAdd.keySet()) {
            if (attribute != AttributeEnum.BASE_ATK && attribute != AttributeEnum.BASE_HP && attribute != AttributeEnum.BASE_DEF) {
                characterAttribute.addAttributeValue(attribute, Objects.requireNonNull(characterBaseAttributeAdd.get(attribute)));
            }
        }
        // add weapon attribute
        if (weaponDex.getAttribute() != null && weaponBaseAttributeMulti.containsKey(AttributeEnum.NULL)) {
            characterAttribute.addAttributeValue(weaponDex.getAttribute(),
                    weaponDex.getAttributeValue() * Objects.requireNonNull(weaponBaseAttributeMulti.get(AttributeEnum.NULL)));
            characterAttribute.setWeaponAttribute(weaponDex.getAttribute());
            characterAttribute.setWeaponAttributeValue(weaponDex.getAttributeValue() * Objects.requireNonNull(weaponBaseAttributeMulti.get(AttributeEnum.NULL)));
        } else {
            if (!(weaponDex.getAttribute() == null && !weaponBaseAttributeMulti.containsKey(AttributeEnum.NULL))) {
                throw new MetaDataQueryException("weapon_dex");
            }
        }
        for (AttributeEnum attribute : weaponBaseAttributeAdd.keySet()) {
            if (attribute != AttributeEnum.BASE_ATK) {
                characterAttribute.addAttributeValue(attribute, Objects.requireNonNull(weaponBaseAttributeAdd.get(attribute)));
            }
        }
        // add artifacts attribute
        for (ArtifactPositionEnum position : GenshinConstantMeta.ARTIFACT_POSITION_LIST) {
            if (artifacts.containsKey(position)) {
                ArtifactProfileDto artifactProfileDto = artifacts.get(position);
                assert artifactProfileDto != null;
                characterAttribute.addAttributeValue(
                        artifactProfileDto.getMainAttribute(), artifactProfileDto.getMainAttributeVal()
                );
                for (int i = 0; i < artifactProfileDto.getSubAttributes().size(); i++) {
                    characterAttribute.addAttributeValue(
                            artifactProfileDto.getSubAttributes().get(i),
                            artifactProfileDto.getSubAttributesVal().get(i)
                    );
                }
            }
        }

        return characterAttribute;
    }

    @WorkerThread
    public FightStatus getFightStatusByCharacterAttribute(CharacterAttribute characterAttributeBase) {
        FightStatus fightStatus = new FightStatus(characterAttributeBase);
        List<BuffEffect> buffEffectList = buffManager.queryStaticBuff(characterAttributeBase);
        for (BuffEffect buffEffect : buffEffectList) {
            if (buffEffect.getDefaultEnabled() && buffEffect.getFromSelf()) {
                buffManager.fillBuffEffectCurveParam(buffEffect,
                        characterAttributeBase.getTalentLevel().get(buffEffect.getSourceTalent()),
                        characterAttributeBase.getWeaponRefinement());
                fightStatus.enableBuffEffect(buffEffect);
            }
        }
        return fightStatus;
    }


}
