package com.megaz.knk.computation;

import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.SourceTalentEnum;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CharacterOverview implements Serializable {
    private String characterId;
    private ElementEnum element;
    private Integer level;
    private Integer phase;
    private Integer constellation;
    private Map<SourceTalentEnum, Integer> talentLevel;
    private Map<SourceTalentEnum, Integer> talentLevelPlus;
    private String weaponId;
    private Integer weaponLevel;
    private Integer weaponPhase;
    private Integer weaponRefinement;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterOverview that = (CharacterOverview) o;
        boolean talentEqualFlag = Objects.equals(talentLevel.get(SourceTalentEnum.A), that.getTalentLevel().get(SourceTalentEnum.A)) &&
                Objects.equals(talentLevel.get(SourceTalentEnum.E), that.getTalentLevel().get(SourceTalentEnum.E)) &&
                Objects.equals(talentLevel.get(SourceTalentEnum.Q), that.getTalentLevel().get(SourceTalentEnum.Q)) &&
                Objects.equals(talentLevelPlus.get(SourceTalentEnum.A), that.getTalentLevel().get(SourceTalentEnum.A)) &&
                Objects.equals(talentLevelPlus.get(SourceTalentEnum.E), that.getTalentLevel().get(SourceTalentEnum.E)) &&
                Objects.equals(talentLevelPlus.get(SourceTalentEnum.Q), that.getTalentLevel().get(SourceTalentEnum.Q));
        return talentEqualFlag && Objects.equals(characterId, that.characterId)  && Objects.equals(level, that.level) &&
                Objects.equals(phase, that.phase) && Objects.equals(constellation, that.constellation) &&
                Objects.equals(weaponId, that.weaponId) && Objects.equals(weaponLevel, that.weaponLevel) &&
                Objects.equals(weaponPhase, that.weaponPhase) && Objects.equals(weaponRefinement, that.weaponRefinement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterId, element, level, phase, constellation, talentLevel, talentLevelPlus, weaponId, weaponLevel, weaponPhase, weaponRefinement);
    }

    public CharacterOverview copy(){
        CharacterOverview copy = new CharacterOverview();
        copy.setCharacterId(characterId);
        copy.setElement(element);
        copy.setLevel(level);
        copy.setPhase(phase);
        copy.setConstellation(constellation);
        Map<SourceTalentEnum, Integer> talentLevel = new HashMap<>();
        talentLevel.put(SourceTalentEnum.A, this.talentLevel.get(SourceTalentEnum.A));
        talentLevel.put(SourceTalentEnum.E, this.talentLevel.get(SourceTalentEnum.E));
        talentLevel.put(SourceTalentEnum.Q, this.talentLevel.get(SourceTalentEnum.Q));
        copy.setTalentLevel(talentLevel);
        Map<SourceTalentEnum, Integer> talentLevelPlus = new HashMap<>();
        talentLevelPlus.put(SourceTalentEnum.A, this.talentLevelPlus.get(SourceTalentEnum.A));
        talentLevelPlus.put(SourceTalentEnum.E, this.talentLevelPlus.get(SourceTalentEnum.E));
        talentLevelPlus.put(SourceTalentEnum.Q, this.talentLevelPlus.get(SourceTalentEnum.Q));
        copy.setTalentLevelPlus(talentLevelPlus);
        copy.setWeaponId(weaponId);
        copy.setWeaponLevel(weaponLevel);
        copy.setWeaponPhase(weaponPhase);
        copy.setWeaponRefinement(weaponRefinement);

        return copy;
    }
}
