package com.megaz.knk.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "talent_curve")
public class TalentCurve extends MetaDataEntity {
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "curve_id")
    private String curveId;
    @ColumnInfo(name = "value_level_1")
    private Double valueLevel1;
    @ColumnInfo(name = "value_level_2")
    private Double valueLevel2;
    @ColumnInfo(name = "value_level_3")
    private Double valueLevel3;
    @ColumnInfo(name = "value_level_4")
    private Double valueLevel4;
    @ColumnInfo(name = "value_level_5")
    private Double valueLevel5;
    @ColumnInfo(name = "value_level_6")
    private Double valueLevel6;
    @ColumnInfo(name = "value_level_7")
    private Double valueLevel7;
    @ColumnInfo(name = "value_level_8")
    private Double valueLevel8;
    @ColumnInfo(name = "value_level_9")
    private Double valueLevel9;
    @ColumnInfo(name = "value_level_10")
    private Double valueLevel10;
    @ColumnInfo(name = "value_level_11")
    private Double valueLevel11;
    @ColumnInfo(name = "value_level_12")
    private Double valueLevel12;
    @ColumnInfo(name = "value_level_13")
    private Double valueLevel13;
    @ColumnInfo(name = "value_level_14")
    private Double valueLevel14;
    @ColumnInfo(name = "value_level_15")
    private Double valueLevel15;

    public Double getValue(int level){
        switch (level){
            case 1: return valueLevel1;
            case 2: return valueLevel2;
            case 3: return valueLevel3;
            case 4: return valueLevel4;
            case 5: return valueLevel5;
            case 6: return valueLevel6;
            case 7: return valueLevel7;
            case 8: return valueLevel8;
            case 9: return valueLevel9;
            case 10: return valueLevel10;
            case 11: return valueLevel11;
            case 12: return valueLevel12;
            case 13: return valueLevel13;
            case 14: return valueLevel14;
            case 15: return valueLevel15;
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

    public Double getValueLevel1() {
        return valueLevel1;
    }

    public void setValueLevel1(Double valueLevel1) {
        this.valueLevel1 = valueLevel1;
    }

    public Double getValueLevel2() {
        return valueLevel2;
    }

    public void setValueLevel2(Double valueLevel2) {
        this.valueLevel2 = valueLevel2;
    }

    public Double getValueLevel3() {
        return valueLevel3;
    }

    public void setValueLevel3(Double valueLevel3) {
        this.valueLevel3 = valueLevel3;
    }

    public Double getValueLevel4() {
        return valueLevel4;
    }

    public void setValueLevel4(Double valueLevel4) {
        this.valueLevel4 = valueLevel4;
    }

    public Double getValueLevel5() {
        return valueLevel5;
    }

    public void setValueLevel5(Double valueLevel5) {
        this.valueLevel5 = valueLevel5;
    }

    public Double getValueLevel6() {
        return valueLevel6;
    }

    public void setValueLevel6(Double valueLevel6) {
        this.valueLevel6 = valueLevel6;
    }

    public Double getValueLevel7() {
        return valueLevel7;
    }

    public void setValueLevel7(Double valueLevel7) {
        this.valueLevel7 = valueLevel7;
    }

    public Double getValueLevel8() {
        return valueLevel8;
    }

    public void setValueLevel8(Double valueLevel8) {
        this.valueLevel8 = valueLevel8;
    }

    public Double getValueLevel9() {
        return valueLevel9;
    }

    public void setValueLevel9(Double valueLevel9) {
        this.valueLevel9 = valueLevel9;
    }

    public Double getValueLevel10() {
        return valueLevel10;
    }

    public void setValueLevel10(Double valueLevel10) {
        this.valueLevel10 = valueLevel10;
    }

    public Double getValueLevel11() {
        return valueLevel11;
    }

    public void setValueLevel11(Double valueLevel11) {
        this.valueLevel11 = valueLevel11;
    }

    public Double getValueLevel12() {
        return valueLevel12;
    }

    public void setValueLevel12(Double valueLevel12) {
        this.valueLevel12 = valueLevel12;
    }

    public Double getValueLevel13() {
        return valueLevel13;
    }

    public void setValueLevel13(Double valueLevel13) {
        this.valueLevel13 = valueLevel13;
    }

    public Double getValueLevel14() {
        return valueLevel14;
    }

    public void setValueLevel14(Double valueLevel14) {
        this.valueLevel14 = valueLevel14;
    }

    public Double getValueLevel15() {
        return valueLevel15;
    }

    public void setValueLevel15(Double valueLevel15) {
        this.valueLevel15 = valueLevel15;
    }
}