package com.megaz.knk.computation;

import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.EffectBaseAttributeEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.constant.ShownAttributeEnum;
import com.megaz.knk.constant.SourceTalentEnum;
import com.megaz.knk.dto.CharacterProfileDto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;


@Getter
public class CharacterAttribute implements Serializable {
    private final String characterId;
    private ElementEnum element;
    private Integer level;
    private Integer phase;
    private Integer constellation;
    @Setter
    private Double baseHp;
    private Double plusHp;
    @Setter
    private Double baseAtk;
    private Double plusAtk;
    @Setter
    private Double baseDef;
    private Double plusDef;
    private Double mastery;
    private Double critRate;
    private Double critDmg;
    private Double recharge;
    private Map<ElementEnum, Double> dmgUp;
    private Double healUp;
    private Double healedUp;
    private Double shieldStrength;
    private Map<SourceTalentEnum, Integer> talentLevel;
    private String weaponId;
    private Integer weaponRefinement;
    private Map<String, Integer> artifactSetCount;

    public CharacterAttribute(CharacterProfileDto characterProfileDto) {
        characterId = characterProfileDto.getCharacterId();
        element = characterProfileDto.getElement();
        level = characterProfileDto.getLevel();
        phase = characterProfileDto.getPhase();
        constellation = characterProfileDto.getConstellation();

        baseHp = 0.;
        plusHp = 0.;
        baseAtk = 0.;
        plusAtk = 0.;
        baseDef = 0.;
        plusDef = 0.;
        recharge = 1.;
        mastery = 0.;
        critRate = 0.05;
        critDmg = 0.5;
        dmgUp = new HashMap<>();
        for(ElementEnum element:GenshinConstantMeta.ELEMENT_LIST) {
            dmgUp.put(element, 0.);
        }
        healUp = 0.;
        healedUp = 0.;
        shieldStrength = 0.;
        talentLevel = new HashMap<>();
        talentLevel.put(SourceTalentEnum.A,
                characterProfileDto.getTalentABaseLevel()+characterProfileDto.getTalentAPlusLevel());
        talentLevel.put(SourceTalentEnum.E,
                characterProfileDto.getTalentEBaseLevel()+characterProfileDto.getTalentEPlusLevel());
        talentLevel.put(SourceTalentEnum.Q,
                characterProfileDto.getTalentQBaseLevel()+characterProfileDto.getTalentQPlusLevel());
        weaponId = characterProfileDto.getWeapon().getWeaponId();
        weaponRefinement = characterProfileDto.getWeapon().getRefineRank();
        artifactSetCount = new HashMap<>();
        for(ArtifactPositionEnum position: GenshinConstantMeta.ARTIFACT_POSITION_LIST) {
            if(!characterProfileDto.getArtifacts().containsKey(position)) {
                continue;
            }
            String setId = Objects.requireNonNull(characterProfileDto.getArtifacts().get(position)).getSetId();
            artifactSetCount.put(setId,
                    Objects.requireNonNull(artifactSetCount.getOrDefault(setId, 0)) + 1);
        }
    }

    public CharacterAttribute(CharacterAttribute another) {
        characterId = another.getCharacterId();
        element = another.getElement();
        level = another.getLevel();
        phase = another.getPhase();
        constellation = another.getConstellation();
        baseHp = another.getBaseHp();
        plusHp = another.getPlusHp();
        baseAtk = another.getBaseAtk();
        plusAtk = another.getPlusAtk();
        baseDef = another.getBaseDef();
        plusDef = another.getPlusDef();
        recharge = another.getRecharge();
        mastery = another.getMastery();
        critRate = another.getCritRate();
        critDmg = another.getCritDmg();
        dmgUp = new HashMap<>();
        for(ElementEnum element:GenshinConstantMeta.ELEMENT_LIST) {
            dmgUp.put(element, another.getDmgUp().get(element));
        }
        healUp = another.getHealUp();
        healedUp = 0.;
        shieldStrength = 0.;
        talentLevel = new HashMap<>();
        talentLevel.put(SourceTalentEnum.A, another.getTalentLevel().get(SourceTalentEnum.A));
        talentLevel.put(SourceTalentEnum.E, another.getTalentLevel().get(SourceTalentEnum.E));
        talentLevel.put(SourceTalentEnum.Q, another.getTalentLevel().get(SourceTalentEnum.Q));
        weaponId = another.getWeaponId();
        weaponRefinement = another.getWeaponRefinement();
        artifactSetCount = new HashMap<>();
        for(Map.Entry<String, Integer> entry:another.getArtifactSetCount().entrySet()) {
            artifactSetCount.put(entry.getKey(), entry.getValue());
        }
    }

    public Double getEffectBasedAttribute(EffectBaseAttributeEnum attribute) {
        switch (attribute) {
            case HP: return baseHp + plusHp;
            case ATK: return baseAtk + plusAtk;
            case DEF: return baseDef + plusDef;
            case MASTERY: return mastery;
            case RECHARGE: return recharge;
            case BASE_ATK: return baseAtk;
            case CRIT_RATE: return critRate;
            case HEAL: return healUp;
            default: return null;
        }
    }

    public Double getShownAttribute(ShownAttributeEnum attribute, ElementEnum element) {
        switch (attribute) {
            case HP: return baseHp + plusHp;
            case ATK: return baseAtk + plusAtk;
            case DEF: return baseDef + plusDef;
            case MASTERY: return mastery;
            case RECHARGE: return recharge;
            case CRIT_RATE: return critRate;
            case CRIT_DMG: return critDmg;
            case DMG:
                assert element != null;
                return dmgUp.get(element);
            case HEAL: return healUp;
            default: return null;
        }
    }

    public void addAttributeValue(AttributeEnum attribute, Double value) {
        switch (attribute) {
            case ATK:
                plusAtk += baseAtk * value;
                break;
            case HP:
                plusHp += baseHp * value;
                break;
            case DEF:
                plusDef += baseDef * value;
                break;
            case ATK_PLUS:
                plusAtk += value;
                break;
            case HP_PLUS:
                plusHp += value;
                break;
            case DEF_PLUS:
                plusDef += value;
                break;
            case RECHARGE:
                recharge += value;
                break;
            case MASTERY:
                mastery += value;
                break;
            case CRIT_RATE:
                critRate += value;
                break;
            case CRIT_DMG:
                critDmg += value;
                break;
            case HEAL:
                healUp += value;
                break;
            case HEALED:
                healedUp += value;
                break;
            case SHIELD_EFFECT:
                shieldStrength += value;
                break;
            default:
                dmgUp.put(attribute.getElement(), Objects.requireNonNull(dmgUp.get(attribute.getElement())) + value);
                break;

        }
    }
}
