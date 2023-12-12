package com.megaz.knk.utils;

import com.megaz.knk.R;
import com.megaz.knk.computation.DamageEffect;
import com.megaz.knk.computation.FightEffect;
import com.megaz.knk.computation.HealEffect;
import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.constant.EffectFieldEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.ElementReactionEnum;
import com.megaz.knk.entity.RefinementCurve;
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
    public static int getElementBackgroundFrame(ElementEnum element) {
        switch (element){
            case PYRO: return R.drawable.frame_element_pyro;
            case HYDRO: return R.drawable.frame_element_hydro;
            case CRYO: return R.drawable.frame_element_cryo;
            case ELECTRO: return R.drawable.frame_element_electro;
            case ANEMO: return R.drawable.frame_element_anemo;
            case GEO: return R.drawable.frame_element_geo;
            case DENDRO: return R.drawable.frame_element_dendro;
            default: return R.drawable.frame_element_null;
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

    public static int getFightEffectColor(FightEffect fightEffect, int defaultColor) {
        if(fightEffect instanceof HealEffect) {
            return R.color.number_heal;
        } else if (fightEffect instanceof DamageEffect) {
            if(((DamageEffect) fightEffect).getReaction() == ElementReactionEnum.OVERLOADED) {
                return R.color.number_overload;
            } else {
                switch (((DamageEffect) fightEffect).getElement()) {
                    case PYRO: return R.color.number_dmg_pyro;
                    case HYDRO: return R.color.number_dmg_hydro;
                    case CRYO: return R.color.number_dmg_cryo;
                    case ELECTRO: return R.color.number_dmg_electro;
                    case DENDRO: return R.color.number_dmg_dendro;
                    case ANEMO: return R.color.number_dmg_anemo;
                    case GEO: return R.color.number_dmg_geo;
                }
            }
        }
        return defaultColor;
    }

    public static int getFightEffectBackgroundColor(FightEffect fightEffect) {
        if (fightEffect instanceof DamageEffect) {
            switch (((DamageEffect) fightEffect).getElement()) {
                case PYRO: return R.color.element_bg_pyro;
                case HYDRO: return R.color.element_bg_hydro;
                case CRYO: return R.color.element_bg_cryo;
                case ELECTRO: return R.color.element_bg_electro;
                case DENDRO: return R.color.element_bg_dendro;
                case ANEMO: return R.color.element_bg_anemo;
                case GEO: return R.color.element_bg_geo;
            }
        }
        return R.color.element_bg_null;
    }

    public static int getBuffFieldColor(EffectFieldEnum field) {
        switch (field) {
            case BASE:
            case BASE_ADD:
            case BASE_MULTIPLE:
                return R.color.field_base;
            case UP:
            case DAMAGE_UP:
                return R.color.field_up;
            case RESIST: return R.color.field_resist;
            case DEFENCE: return R.color.field_defence;
            case CRIT_RATE:
            case CRIT_DMG:
                return R.color.field_crit;
            case MASTERY:
                return R.color.field_mastery;
            case REACTION:
                return R.color.field_reaction;
        }
        return R.color.gray_black;
    }
}
