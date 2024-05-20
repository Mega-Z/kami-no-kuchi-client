package com.megaz.knk.vo;

import android.annotation.SuppressLint;

import com.megaz.knk.constant.EffectFieldEnum;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EffectDetailVo {
    private String effectDesc;
    private Double effectValue;
    private Double effectValueCritical;
    private Boolean canCritical; // 是否可暴击
    private Boolean isPercent;
    private Map<EffectFieldEnum, Double> fieldDetail;

    public String getNumberWithCritical() {
        return getNumberCritical()+"/"+getNumber();
    }

    @SuppressLint("DefaultLocale")
    public String getNumber() {
        if(effectValue == null) {
            return  "NaN";
        } else if (isPercent) {
            return String.format("%.2f", effectValue * 100) + "%";
        } else if (effectValue > 1000) {
            return String.format("%d", (int) (effectValue.doubleValue()));
        } else {
            return String.format("%.2f", effectValue);
        }
    }

    @SuppressLint("DefaultLocale")
    public String getNumberCritical() {
        if(effectValueCritical == null) {
            return  "NaN";
        } else if (effectValueCritical > 1000) {
            return String.format("%d", (int) (effectValueCritical.doubleValue()));
        } else {
            return String.format("%.2f", effectValueCritical);
        }
    }

    public int compareTo(EffectDetailVo that) {
        return this.effectValue.compareTo(that.effectValue);
    }
}
