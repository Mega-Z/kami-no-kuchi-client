package com.megaz.knk.utils;

import com.megaz.knk.R;

import java.util.*;

public class WishCalculator {
    private static final List<Double> CHARACTER_PROP = new ArrayList<>();
    private static final Map<Integer, List<Double>> CHARACTER_WISH_CNT_PROP_MAP = new HashMap<>();
    private static final Double CHARACTER_UP_HIT = 0.5;

    private static final List<Double> WEAPON_PROP = new ArrayList<>();
    private static final Map<Integer, List<Double>> WEAPON_WISH_CNT_PROP_MAP = new HashMap<>();
    private static final Double WEAPON_UP_HIT = 0.75;
    private static final List<Double> WEAPON_GOLDEN_NUM_PROP_0_N = new ArrayList<>();
    private static final List<Double> WEAPON_GOLDEN_NUM_PROP_0_Y = new ArrayList<>();
    private static final List<Double> WEAPON_GOLDEN_NUM_PROP_1_N = new ArrayList<>();
    private static final List<Double> WEAPON_GOLDEN_NUM_PROP_1_Y = new ArrayList<>();
    private static final List<Double> WEAPON_GOLDEN_NUM_PROP_2 = new ArrayList<>();

    static {
        // initial character prop table
        for (int i = 0; i < 73; i++)
            CHARACTER_PROP.add(0.006);
        CHARACTER_PROP.addAll(Arrays.asList(0.066, 0.126, 0.186, 0.246, 0.306, 0.366, 0.426, 0.486,
                0.546, 0.606, 0.666, 0.726, 0.786, 0.846, 0.906, 0.966, 1.000));
        // initial weapon prop table
        for (int i = 0; i < 62; i++)
            WEAPON_PROP.add(0.007);
        WEAPON_PROP.addAll(Arrays.asList(0.077, 0.147, 0.217, 0.287, 0.357, 0.427, 0.497, 0.567,
                0.637, 0.707, 0.777, 0.812, 0.847, 0.882, 0.917, 0.952, 0.987, 1.000));
        // destined:0, no big guarantee
        WEAPON_GOLDEN_NUM_PROP_0_N.addAll(Arrays.asList(
                WEAPON_UP_HIT / 2,
                WEAPON_UP_HIT / 2 * WEAPON_UP_HIT / 2 + (1 - WEAPON_UP_HIT) * 1. / 2,
                WEAPON_UP_HIT / 2 * WEAPON_UP_HIT / 2 + WEAPON_UP_HIT / 2 * (1 - WEAPON_UP_HIT) +
                        (1 - WEAPON_UP_HIT) * 1. / 2
        ));
        // destined:0, with big guarantee
        WEAPON_GOLDEN_NUM_PROP_0_Y.addAll(Arrays.asList(
                1. / 2,
                1. / 2 * WEAPON_UP_HIT / 2,
                1. / 2 * (1 - WEAPON_UP_HIT) + 1. / 2 * WEAPON_UP_HIT / 2
        ));
        // destined:1, no big guarantee
        WEAPON_GOLDEN_NUM_PROP_1_N.addAll(Arrays.asList(
                WEAPON_UP_HIT / 2,
                1 - WEAPON_UP_HIT + WEAPON_UP_HIT / 2
        ));
        // destined:1, with big guarantee
        WEAPON_GOLDEN_NUM_PROP_1_Y.addAll(Arrays.asList(
                1. / 2,
                1. / 2
        ));
        // destined:2
        WEAPON_GOLDEN_NUM_PROP_2.add(1.);
    }

    public static List<Double> getTargetWeaponWishCntPropTable(int targetNum, int consumed, boolean bigGuarantee, int destined) {
        if(targetNum <= 0) {
            return new ArrayList<>();
        }
        assert destined <= 2 && destined >= 0;
        List<Double> goldenCntPropTable;
        if (destined == 0 && !bigGuarantee) {
            goldenCntPropTable = WEAPON_GOLDEN_NUM_PROP_0_N;
        } else if (destined == 0 && bigGuarantee) {
            goldenCntPropTable = WEAPON_GOLDEN_NUM_PROP_0_Y;
        } else if (destined == 1 && !bigGuarantee) {
            goldenCntPropTable = WEAPON_GOLDEN_NUM_PROP_1_N;
        } else if (destined == 1 && bigGuarantee) {
            goldenCntPropTable = WEAPON_GOLDEN_NUM_PROP_1_Y;
        } else {
            goldenCntPropTable = WEAPON_GOLDEN_NUM_PROP_2;
        }
        for (int currentNum = 1; currentNum < targetNum; currentNum++) {
            goldenCntPropTable = convolutionOfPropTables(goldenCntPropTable, WEAPON_GOLDEN_NUM_PROP_0_N);
        }
        List<Double> targetWishCntPropTable = new ArrayList<>();
        for (int goldenNum = 1; goldenNum <= goldenCntPropTable.size(); goldenNum++) {
            if(goldenCntPropTable.get(goldenNum-1) <= 0) {
                continue;
            }
            double prop = goldenCntPropTable.get(goldenNum-1);
            targetWishCntPropTable = combinationOfPropTables(targetWishCntPropTable,
                    getWishCntPropTable(goldenNum, consumed, WEAPON_PROP, WEAPON_WISH_CNT_PROP_MAP), 1, prop);
        }
        return targetWishCntPropTable;

    }


    public static List<Double> getUpCharacterWishCntPropTable(int targetNum, int consumed, boolean bigGuarantee) {
        if(targetNum <= 0) {
            return new ArrayList<>();
        }
        int maxMissNum = bigGuarantee ? targetNum - 1 : targetNum;
        List<Double> upWishCntPropTable = new ArrayList<>();
        for (int missNum = 0; missNum <= maxMissNum; missNum++) {
            int goldenNum = missNum + targetNum;
            double prop = Math.pow(CHARACTER_UP_HIT, maxMissNum - missNum) * Math.pow(1 - CHARACTER_UP_HIT, missNum) *
                    combination(maxMissNum, missNum);
            upWishCntPropTable = combinationOfPropTables(upWishCntPropTable,
                    getWishCntPropTable(goldenNum, consumed, CHARACTER_PROP, CHARACTER_WISH_CNT_PROP_MAP), 1, prop);
        }
        return upWishCntPropTable;
    }


    /**
     * 根据金数和水位计算出货抽数概率
     *
     * @param targetGoldenNum     目标金数
     * @param consumed            水位
     * @param goldenPropByCurrent 卡池概率表
     * @param cacheWishCntPropMap 恰出货抽数概率表缓存
     * @return
     */
    private static List<Double> getWishCntPropTable(int targetGoldenNum, int consumed,
                                                    List<Double> goldenPropByCurrent, Map<Integer, List<Double>> cacheWishCntPropMap) {
        assert consumed < goldenPropByCurrent.size();
        List<Double> singleGoldenWishCntProp = new ArrayList<>(); // 恰好x抽出金概率
        double accumulateFailProp = 1;
        for (int i = consumed; i < goldenPropByCurrent.size(); i++) {
            singleGoldenWishCntProp.add(accumulateFailProp * goldenPropByCurrent.get(i));
            accumulateFailProp *= (1 - goldenPropByCurrent.get(i));
        }
        if (targetGoldenNum == 1) {
            return singleGoldenWishCntProp;
        } else {
            List<Double> othersGoldenWishCntProp;
            if (cacheWishCntPropMap.containsKey(targetGoldenNum - 1)) {
                othersGoldenWishCntProp = cacheWishCntPropMap.get(targetGoldenNum - 1);
            } else {
                othersGoldenWishCntProp = getWishCntPropTable(targetGoldenNum - 1, 0,
                        goldenPropByCurrent, cacheWishCntPropMap);
                cacheWishCntPropMap.put(targetGoldenNum - 1, othersGoldenWishCntProp);
            }
            return convolutionOfPropTables(singleGoldenWishCntProp, othersGoldenWishCntProp);
        }
    }

    public static List<Double> convolutionOfPropTables(List<Double> propTable1, List<Double> propTable2) {
        List<Double> convolutionProp = new ArrayList<>();
        if(propTable1.size() == 0) {
            return propTable2;
        }
        if(propTable2.size() == 0) {
            return propTable1;
        }
        for (int i = 0; i < propTable1.size() + propTable2.size(); i++) {
            double convProp = 0.;
            // c1 + c2 = i+1
            for (int c1 = 1; c1 < i + 1; c1++) {
                int c2 = i + 1 - c1;
                if (c1 <= propTable1.size() && c2 <= propTable2.size()) {
                    convProp += propTable1.get(c1 - 1) * propTable2.get(c2 - 1);
                }
            }
            convolutionProp.add(convProp);
        }
        return convolutionProp;
    }

    private static List<Double> combinationOfPropTables(List<Double> propTable1, List<Double> propTable2, double prop1, double prop2) {
        List<Double> combinationProp = new ArrayList<>();
        for (int i = 0; i < Math.max(propTable1.size(), propTable2.size()); i++) {
            double combinedProp = 0.;
            if (i < propTable1.size()) {
                combinedProp += prop1 * propTable1.get(i);
            }
            if (i < propTable2.size()) {
                combinedProp += prop2 * propTable2.get(i);
            }
            combinationProp.add(combinedProp);
        }
        return combinationProp;
    }

    private static int combination(int m, int n) {
        if (n == 1) {
            return m;
        } else if (n == m || n == 0) {
            return 1;
        } else {
            return combination(m - 1, n) + combination(m - 1, n - 1);
        }
    }

    public static int getPropColor(double prop) {
        if (prop >= 0.75)
            return R.color.prop_high;
        else if (prop >= 0.50)
            return R.color.prop_mid_high;
        else if (prop >= 0.25)
            return R.color.prop_mid_low;
        else
            return R.color.prop_low;
    }


}
