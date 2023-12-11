package com.megaz.knk.activity;

import android.annotation.SuppressLint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.ChartHighlighter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.megaz.knk.R;
import com.megaz.knk.fragment.CharacterWishParamFragment;
import com.megaz.knk.fragment.WeaponWishParamFragment;
import com.megaz.knk.utils.WishCalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WishCalculatorActivity extends BaseActivity {
    private CharacterWishParamFragment characterWishParamFragment;
    private WeaponWishParamFragment weaponWishParamFragment;

    private LinearLayout layoutCharacterWishNormal, layoutCharacterWishSelected, layoutWeaponWishNormal, layoutWeaponWishSelected;
    private TextView textCharacterWishNum, textWeaponWishNum, textExpectation, textProp;
    private ViewPager viewPagerWishSwitch;
    private EditText edtxBudget;
    private Button btnComputeExpectation, btnGetProp;
    private FrameLayout layoutProp;
    private LineChart chartWishProp;
    private View viewSlideLine;

    private int LINE_WIDTH, SLIDE_MARGIN, SLIDE_LEFT_X, SLIDE_RIGHT_X;

    private int targetNumCharacter = 0;
    private int targetNumWeapon = 0;
    private int consumedCharacter = 0;
    private int consumedWeapon = 0;
    private boolean bigGuaranteeCharacter = false;
    private boolean bigGuaranteeWeapon = false;
    private int destined = 0;
    private List<Double> propTable;

    @Override
    protected void setContent() {
        setContentView(R.layout.activity_wish_calculator);
    }

    @Override
    protected void initView() {
        super.initView();
        initConstants();
        layoutCharacterWishNormal = findViewById(R.id.layout_character_wish_normal);
        layoutCharacterWishSelected = findViewById(R.id.layout_character_wish_selected);
        layoutWeaponWishNormal = findViewById(R.id.layout_weapon_wish_normal);
        layoutWeaponWishSelected = findViewById(R.id.layout_weapon_wish_selected);
        viewSlideLine = findViewById(R.id.view_slide_line);
        viewSlideLine.setTranslationX(SLIDE_LEFT_X);
        textCharacterWishNum = findViewById(R.id.text_character_wish_num);
        textWeaponWishNum = findViewById(R.id.text_weapon_wish_num);
        viewPagerWishSwitch = findViewById(R.id.viewpager_wish_switch);
        textExpectation = findViewById(R.id.text_expectation);
        textExpectation.setTypeface(typefaceNum);
        layoutProp = findViewById(R.id.layout_prop);
        textProp = findViewById(R.id.text_prop);
        textProp.setTypeface(typefaceNum);
        edtxBudget = findViewById(R.id.edtx_budget);
        edtxBudget.setTypeface(typefaceNum);
        btnComputeExpectation = findViewById(R.id.btn_compute_expectation);
        btnGetProp = findViewById(R.id.btn_get_prop);

        chartWishProp = findViewById(R.id.chart_wish_prop);
        initChartStyle();

        characterWishParamFragment = CharacterWishParamFragment.newInstance();
        weaponWishParamFragment = WeaponWishParamFragment.newInstance();
        viewPagerWishSwitch.setAdapter(new KnkFragmentPagerAdapter(getSupportFragmentManager(),
                Arrays.asList(characterWishParamFragment, weaponWishParamFragment)));

    }

    private void initConstants() {
        LINE_WIDTH = getResources().getDimensionPixelOffset(R.dimen.dp_100);
        SLIDE_MARGIN = getResources().getDimensionPixelOffset(R.dimen.dp_30);
        SLIDE_LEFT_X = ((displayMetrics.widthPixels / 2 - SLIDE_MARGIN) - LINE_WIDTH) / 2 + SLIDE_MARGIN;
        SLIDE_RIGHT_X = displayMetrics.widthPixels - SLIDE_LEFT_X - LINE_WIDTH;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setCallback() {
        super.setCallback();
        btnComputeExpectation.setOnClickListener(new ComputeExpectationOnClickListener());
        btnGetProp.setOnClickListener(new getPropOnclickListener());
        edtxBudget.setOnTouchListener(new EditTextOnTouchListener(this));
        viewPagerWishSwitch.addOnPageChangeListener(new WishSwitchOnPageChangeListener());
        layoutCharacterWishNormal.setOnClickListener(new WishSwitchOnClickListener(0));
        layoutWeaponWishNormal.setOnClickListener(new WishSwitchOnClickListener(1));
    }

    private class WishSwitchOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if(positionOffset != 0) {
                viewSlideLine.setTranslationX(SLIDE_LEFT_X + positionOffset * (SLIDE_RIGHT_X - SLIDE_LEFT_X));
            } else if(position == 0){
                viewSlideLine.setTranslationX(SLIDE_LEFT_X);
            } else {
                viewSlideLine.setTranslationX(SLIDE_RIGHT_X);
            }

        }

        @Override
        public void onPageSelected(int position) {
            if(position == 0) {
                layoutCharacterWishSelected.setVisibility(View.VISIBLE);
                layoutCharacterWishNormal.setVisibility(View.GONE);
                layoutWeaponWishSelected.setVisibility(View.GONE);
                layoutWeaponWishNormal.setVisibility(View.VISIBLE);
                textWeaponWishNum.setText(String.valueOf(weaponWishParamFragment.getTarget()));
            } else {
                layoutCharacterWishSelected.setVisibility(View.GONE);
                layoutCharacterWishNormal.setVisibility(View.VISIBLE);
                layoutWeaponWishSelected.setVisibility(View.VISIBLE);
                layoutWeaponWishNormal.setVisibility(View.GONE);
                textCharacterWishNum.setText(String.valueOf(characterWishParamFragment.getTarget()));
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private class WishSwitchOnClickListener implements View.OnClickListener {
        private int item;

        WishSwitchOnClickListener(int item) {
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            viewPagerWishSwitch.setCurrentItem(item, true);
        }
    }


    private class ComputeExpectationOnClickListener implements View.OnClickListener {

        @SuppressLint("DefaultLocale")
        @Override
        public void onClick(View v) {
            if (characterWishParamFragment.getTarget() + weaponWishParamFragment.getTarget() == 0) {
                toast.setText("请设置祈愿目标");
                toast.show();
                return;
            }
            updateWishParameters();
            computeExpectationAndUpdateView();
        }
    }

    @SuppressLint("DefaultLocale")
    private void computeExpectationAndUpdateView() {
        List<Double> propTableCharacter = WishCalculator.getUpCharacterWishCntPropTable(targetNumCharacter, consumedCharacter, bigGuaranteeCharacter);
        List<Double> propTableWeapon = WishCalculator.getTargetWeaponWishCntPropTable(targetNumWeapon, consumedWeapon, bigGuaranteeWeapon, destined);
        propTable = WishCalculator.convolutionOfPropTables(propTableCharacter, propTableWeapon);
        updatePropChart(propTable);
        double expectation = 0.;
        for (int cnt = 1; cnt <= propTable.size(); cnt++) {
            expectation += cnt * propTable.get(cnt - 1);
        }
        textExpectation.setText(String.format("%.2f", expectation));
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void computePropAndUpdateView() {
        double prop = 0;
        int budget = Integer.parseInt(edtxBudget.getText().toString());
        List<Double> propList = new ArrayList<>();
        if (budget >= propTable.size()) {
            prop = 1;
            propList = propTable;
        } else {
            for (int cnt = 1; cnt <= budget; cnt++) {
                prop += propTable.get(cnt - 1);
                propList.add(propTable.get(cnt - 1));
            }
        }
        updateBudgetChart(propList);
        textProp.setText(String.format("%.2f", prop * 100) + "%");
        textProp.setTextColor(getColor(WishCalculator.getPropColor(prop)));
        layoutProp.setVisibility(View.VISIBLE);
    }

    private boolean updateWishParameters() {
        boolean updatedFlag = false;
        if (targetNumCharacter != characterWishParamFragment.getTarget()) {
            updatedFlag = true;
            targetNumCharacter = characterWishParamFragment.getTarget();
        }
        if (consumedCharacter != characterWishParamFragment.getConsumed()) {
            updatedFlag = true;
            consumedCharacter = characterWishParamFragment.getConsumed();
        }
        if (bigGuaranteeCharacter != characterWishParamFragment.getBigGuarantee()) {
            updatedFlag = true;
            bigGuaranteeCharacter = characterWishParamFragment.getBigGuarantee();
        }
        if (targetNumWeapon != weaponWishParamFragment.getTarget()) {
            updatedFlag = true;
            targetNumWeapon = weaponWishParamFragment.getTarget();
        }
        if (consumedWeapon != weaponWishParamFragment.getConsumed()) {
            updatedFlag = true;
            consumedWeapon = weaponWishParamFragment.getConsumed();
        }
        if (bigGuaranteeWeapon != weaponWishParamFragment.getBigGuarantee()) {
            updatedFlag = true;
            bigGuaranteeWeapon = weaponWishParamFragment.getBigGuarantee();
        }
        if (destined != weaponWishParamFragment.getDestined()) {
            updatedFlag = true;
            destined = weaponWishParamFragment.getDestined();
        }
        return updatedFlag;
    }

    private class getPropOnclickListener implements View.OnClickListener {

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void onClick(View v) {
            if (edtxBudget.getText().toString().isEmpty()) {
                toast.setText("请输入预算");
                toast.show();
                return;
            }
            if (characterWishParamFragment.getTarget() + weaponWishParamFragment.getTarget() == 0) {
                toast.setText("请设置祈愿目标");
                toast.show();
                return;
            }
            if (updateWishParameters() || propTable == null) {
                computeExpectationAndUpdateView();
            }
            computePropAndUpdateView();
        }
    }

    private void initChartStyle() {
        chartWishProp.getDescription().setEnabled(false);
        chartWishProp.setDefaultFocusHighlightEnabled(false);
        chartWishProp.setDrawBorders(false);
        chartWishProp.setNoDataText("请输入祈愿参数计算出货抽数概率分布");
        chartWishProp.setNoDataTextColor(getColor(R.color.gray_black));
        chartWishProp.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chartWishProp.getXAxis().setDrawGridLines(false);
        chartWishProp.getXAxis().setAxisLineWidth(2);
        chartWishProp.getAxisLeft().setAxisLineWidth(2);
        chartWishProp.getAxisLeft().setDrawGridLines(false);
        chartWishProp.getAxisLeft().setAxisMinimum(0);
        chartWishProp.getAxisRight().setEnabled(false);
        chartWishProp.getLegend().setEnabled(false);
        chartWishProp.setTouchEnabled(true);
        chartWishProp.setHighlightPerTapEnabled(true);
        chartWishProp.setBackgroundColor(getColor(R.color.chart_bg));


    }

    private void updatePropChart(List<Double> doubleList) {
        List<Entry> entryList = new ArrayList<>();

        for (int i = 0; i < doubleList.size(); i++) {
            entryList.add(new Entry(i + 1, doubleList.get(i).floatValue()));
        }
        LineDataSet lineDataSet = new LineDataSet(entryList, "");
        lineDataSet.setValueTextSize(0);
        lineDataSet.setCircleRadius(1);
        lineDataSet.setLineWidth(3f);
        lineDataSet.setHighLightColor(getColor(R.color.black));
        LineData lineData = new LineData(lineDataSet);
        chartWishProp.setData(lineData);
        chartWishProp.postInvalidate();
    }

    private void updateBudgetChart(List<Double> doubleList) {
        List<Entry> entryList = new ArrayList<>();

        for (int i = 0; i < doubleList.size(); i++) {
            entryList.add(new Entry(i + 1, doubleList.get(i).floatValue()));
        }
        LineDataSet lineDataSet = new LineDataSet(entryList, "");
        lineDataSet.setValueTextSize(0);
        lineDataSet.setCircleRadius(1);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setFillColor(getColor(R.color.budget_fill));
        lineDataSet.setDrawFilled(true);
        lineDataSet.setHighLightColor(getColor(R.color.black));
        if (chartWishProp.getData().getDataSetCount() > 1) {
            chartWishProp.getData().removeDataSet(1);
        }
        chartWishProp.getData().addDataSet(lineDataSet);
        chartWishProp.postInvalidate();
    }
}