package com.megaz.knk.utils;

import com.megaz.knk.R;
import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.vo.ArtifactEvaluationVo;

public class ArtifactUtils {
    public static int getRankColor(String rank){
        if(rank.contains("ACE"))
            return R.color.rank_ace;
        else if("SSS".contains(rank))
            return R.color.rank_s;
        else
            return R.color.rank_a_d;
    }

    public static int getWeightColor(int weight){
        if(weight >= 95)
            return R.color.attr_optimal;
        else if(weight >= 50)
            return R.color.attr_useful;
        else
            return R.color.attr_useless;
    }

    public static int getAttributeColor(AttributeEnum attributeEnum, ArtifactEvaluationVo artifactEvaluationVo) {
        switch (attributeEnum.getWeightLabel()) {
            case "atk":
                return getWeightColor(artifactEvaluationVo.getAtkWeight());
            case "hp":
                return getWeightColor(artifactEvaluationVo.getHpWeight());
            case "def":
                return getWeightColor(artifactEvaluationVo.getDefWeight());
            case "mastery":
                return getWeightColor(artifactEvaluationVo.getMasteryWeight());
            case "recharge":
                return getWeightColor(artifactEvaluationVo.getRechargeWeight());
            case "cr":
                return getWeightColor(artifactEvaluationVo.getCrWeight());
            case "cd":
                return getWeightColor(artifactEvaluationVo.getCdWeight());
            case "phy":
                return getWeightColor(artifactEvaluationVo.getPhyWeight());
            case "dmg":
                return getWeightColor(artifactEvaluationVo.getDmgWeight());
            case "heal":
                return getWeightColor(artifactEvaluationVo.getHealWeight());
        }
        return getWeightColor(0);
    }

    public static String getRank(double score){
        if(score > 56.1)
            return "ACE²";
        else if(score > 49.5)
            return "ACE";
        else if(score > 42.9)
            return "SSS";
        else if(score > 36.3)
            return "SS";
        else if(score > 29.7)
            return "S";
        else if(score > 23.1)
            return "A";
        else if(score > 16.5)
            return "B";
        else if(score > 10)
            return "C";
        else
            return "D";
    }

    public static String getCircledNum(int num) {
        switch (num) {
            case 1:
                return "①";
            case 2:
                return "②";
            case 3:
                return "③";
            case 4:
                return "④";
            case 5:
                return "⑤";
            case 6:
                return "⑥";
        }
        return "";
    }
}
