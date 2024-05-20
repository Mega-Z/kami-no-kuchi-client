package com.megaz.knk.manager;

import android.content.Context;

import androidx.annotation.WorkerThread;

import com.megaz.knk.KnkDatabase;
import com.megaz.knk.computation.BuffEffect;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.computation.FightStatus;
import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
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

public class CharacterAttributeManager {

    private final KnkDatabase knkDatabase;
    private final BuffManager buffManager;

    public CharacterAttributeManager(Context context) {
        this.knkDatabase = KnkDatabase.getKnkDatabase(context);
        buffManager = new BuffManager(context);
    }

    @WorkerThread
    public CharacterAttribute createCharacterBaseAttribute(CharacterProfileDto characterProfileDto) {
        CharacterDexDao characterDexDao = knkDatabase.getCharacterDexDao();
        WeaponDexDao weaponDexDao = knkDatabase.getWeaponDexDao();
        PromoteAttributeDao promoteAttributeDao = knkDatabase.getPromoteAttributeDao();

        CharacterDex characterDex = MetaDataUtils.queryCharacterDex(characterDexDao, characterProfileDto.getCharacterId());
        WeaponProfileDto weaponProfileDto = characterProfileDto.getWeapon();
        WeaponDex weaponDex = MetaDataUtils.queryWeaponDex(weaponDexDao, weaponProfileDto.getWeaponId());

        CharacterAttribute characterAttribute = new CharacterAttribute(characterProfileDto);
        Map<AttributeEnum, String> baseCurveNames = new HashMap<>();
        baseCurveNames.put(AttributeEnum.BASE_ATK, characterDex.getCurveBaseAtk());
        baseCurveNames.put(AttributeEnum.BASE_HP, characterDex.getCurveBaseHp());
        baseCurveNames.put(AttributeEnum.BASE_DEF, characterDex.getCurveBaseDef());
        Map<AttributeEnum, Double> characterBaseAttributeMulti =
                CharacterBaseAttribute.getBaseAttributeMultiByLevel(characterProfileDto.getLevel(), baseCurveNames);
        Map<AttributeEnum, Double> characterBaseAttributeAdd =
                MetaDataUtils.queryPromoteAttribute(promoteAttributeDao, characterDex.getPromoteId(), characterProfileDto.getPhase());
        Map<AttributeEnum, Double> weaponBaseAttributeMulti =
                WeaponBaseAttribute.getBaseAttributeMultiByLevel(weaponProfileDto.getLevel(), weaponDex.getCurveBaseAtk(), weaponDex.getCurveAttribute());
        Map<AttributeEnum, Double> weaponBaseAttributeAdd =
                MetaDataUtils.queryPromoteAttribute(promoteAttributeDao, weaponDex.getPromoteId(), weaponProfileDto.getPhase());

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
            if (characterProfileDto.getArtifacts().containsKey(position)) {
                ArtifactProfileDto artifactProfileDto = characterProfileDto.getArtifacts().get(position);
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
