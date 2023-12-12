package com.megaz.knk.computation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuffInputParam implements Serializable {
    private String inputHint;
    private Double inputValue;
    private Double maxValue;
    private Boolean percent;
    private Boolean decimal;

    public BuffInputParam(String inputHint, Boolean percent, Boolean decimal) {
        this.inputHint = inputHint;
        this.percent = percent;
        this.decimal = decimal;
    }

    public BuffInputParam(String inputHint, Boolean percent, Boolean decimal, Double maxValue) {
        this.inputHint = inputHint;
        this.percent = percent;
        this.decimal = decimal;
        this.maxValue = maxValue;
    }
}
