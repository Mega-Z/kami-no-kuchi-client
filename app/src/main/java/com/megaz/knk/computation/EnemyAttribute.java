package com.megaz.knk.computation;

import com.megaz.knk.constant.ElementEnum;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;


public class EnemyAttribute implements Serializable {
    @Getter
    @Setter
    private Integer level;
    private final Map<ElementEnum, Double> resist;

    public EnemyAttribute() {
        level = 91;
        resist = new HashMap<>();
        resist.put(ElementEnum.PYRO, 0.1);
        resist.put(ElementEnum.CRYO, 0.1);
        resist.put(ElementEnum.HYDRO, 0.1);
        resist.put(ElementEnum.ELECTRO, 0.1);
        resist.put(ElementEnum.ANEMO, 0.1);
        resist.put(ElementEnum.GEO, 0.1);
        resist.put(ElementEnum.DENDRO, 0.1);
        resist.put(ElementEnum.PHYSICAL, 0.1);
    }

    public Double getResist(ElementEnum element) {
        return resist.get(element);
    }

    public void setResist(ElementEnum element, Double resistValue) {
        resist.put(element, resistValue);
    }
}
