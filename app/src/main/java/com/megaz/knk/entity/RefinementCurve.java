package com.megaz.knk.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "refinement_curve")
public class RefinementCurve extends MetaDataEntity {
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "curve_id")
    private String curveId;
    @ColumnInfo(name = "value_refinement_1")
    private Double valueRefinement1;
    @ColumnInfo(name = "value_refinement_2")
    private Double valueRefinement2;
    @ColumnInfo(name = "value_refinement_3")
    private Double valueRefinement3;
    @ColumnInfo(name = "value_refinement_4")
    private Double valueRefinement4;
    @ColumnInfo(name = "value_refinement_5")
    private Double valueRefinement5;

    public Double getValue(int level) {
        switch (level) {
            case 1: return valueRefinement1;
            case 2: return valueRefinement2;
            case 3: return valueRefinement3;
            case 4: return valueRefinement4;
            case 5: return valueRefinement5;
            default: return 0.;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCurveId() {
        return curveId;
    }

    public void setCurveId(String curveId) {
        this.curveId = curveId;
    }

    public Double getValueRefinement1() {
        return valueRefinement1;
    }

    public void setValueRefinement1(Double valueRefinement1) {
        this.valueRefinement1 = valueRefinement1;
    }

    public Double getValueRefinement2() {
        return valueRefinement2;
    }

    public void setValueRefinement2(Double valueRefinement2) {
        this.valueRefinement2 = valueRefinement2;
    }

    public Double getValueRefinement3() {
        return valueRefinement3;
    }

    public void setValueRefinement3(Double valueRefinement3) {
        this.valueRefinement3 = valueRefinement3;
    }

    public Double getValueRefinement4() {
        return valueRefinement4;
    }

    public void setValueRefinement4(Double valueRefinement4) {
        this.valueRefinement4 = valueRefinement4;
    }

    public Double getValueRefinement5() {
        return valueRefinement5;
    }

    public void setValueRefinement5(Double valueRefinement5) {
        this.valueRefinement5 = valueRefinement5;
    }
}