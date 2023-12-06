package com.megaz.knk.utils;

import com.megaz.knk.R;
import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.vo.ArtifactEvaluationVo;

public class DynamicStyleUtils {
    public static int getElementTextColor(ElementEnum element) {
        switch (element){
            case PYRO: return R.color.element_text_pyro;
            case HYDRO: return R.color.element_text_hydro;
            case CRYO: return R.color.element_text_cryo;
            case ELECTRO: return R.color.element_text_electro;
            case ANEMO: return R.color.element_text_anemo;
            case GEO: return R.color.element_text_geo;
            case DENDRO: return R.color.element_text_dendro;
            default: return R.color.black;
        }
    }

    public static int getElementBackgroundColor(ElementEnum element) {
        switch (element){
            case PYRO: return R.color.element_bg_pyro;
            case HYDRO: return R.color.element_bg_hydro;
            case CRYO: return R.color.element_bg_cryo;
            case ELECTRO: return R.color.element_bg_electro;
            case ANEMO: return R.color.element_bg_anemo;
            case GEO: return R.color.element_bg_geo;
            case DENDRO: return R.color.element_bg_dendro;
            default: return R.color.white;
        }
    }

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
        Integer weight = artifactEvaluationVo.getAttributeWeight().get(attributeEnum.getLabel());
        assert  weight != null;
        return getWeightColor(weight);
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
