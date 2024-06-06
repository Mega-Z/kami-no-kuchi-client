package com.megaz.knk.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.fragment.app.FragmentTransaction;

import com.megaz.knk.R;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.computation.CharacterOverview;
import com.megaz.knk.computation.EnemyAttribute;
import com.megaz.knk.computation.FightEffect;
import com.megaz.knk.computation.FightStatus;
import com.megaz.knk.constant.CharacterDetailActivityStatusEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.SourceTalentEnum;
import com.megaz.knk.dto.CharacterProfileDto;
import com.megaz.knk.fragment.ArtifactEvaluationFragment;
import com.megaz.knk.fragment.CharacterAttributeFragment;
import com.megaz.knk.fragment.ConstellationFragment;
import com.megaz.knk.fragment.EffectComputationFragment;
import com.megaz.knk.fragment.HistoryProfileSelectionFragment;
import com.megaz.knk.fragment.TalentFragment;
import com.megaz.knk.fragment.VirtualProfileConfigFragment;
import com.megaz.knk.fragment.WeaponFragment;
import com.megaz.knk.manager.CharacterAttributeManager;
import com.megaz.knk.manager.EffectComputationManager;
import com.megaz.knk.manager.ProfileViewManager;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.utils.ProfileConvertUtils;
import com.megaz.knk.vo.CharacterProfileVo;
import com.megaz.knk.vo.ConstellationVo;
import com.megaz.knk.vo.TalentVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CharacterDetailActivity extends ElasticScrollActivity {

    private int ART_OFFSET_X, ART_HEIGHT, TALENT_WIDTH;
    private float SCROLL_STEP_PROGRESS; // 划动动画的中间进度，武器信息缩回开始移动
    private float ART_SCALE_RATIO;
    private CharacterDetailActivityStatusEnum status;

    //    private CharacterAttribute characterBaseAttribute, characterAttributeHistory, characterAttributeVirtual;
    private FightStatus fightStatus, fightStatusHistory, fightStatusVirtual;
    private CharacterOverview characterOverview, characterOverviewHistory, characterOverviewVirtual;
    private CharacterProfileVo characterProfileVo, characterProfileVoVirtual;
    private CharacterProfileDto characterProfileDto, characterProfileDtoHistory;

    private WeaponFragment weaponFragment;
    private Map<SourceTalentEnum, TalentFragment> talentFragmentMap;
    private List<ConstellationFragment> constellationFragmentList;
    private CharacterAttributeFragment characterAttributeFragment;
    private ArtifactEvaluationFragment artifactEvaluationFragment;
    private EffectComputationFragment effectComputationFragment;
    private HistoryProfileSelectionFragment historyProfileSelectionFragment;
    private VirtualProfileConfigFragment virtualProfileConfigFragment;

    private TextView textTitle;
    private FrameLayout layoutArt;
    private LinearLayout layoutConstellation;
    private LinearLayout layoutTalentA, layoutTalentE, layoutTalentQ;
    private LinearLayout layoutWeapon;
    private LinearLayout layoutArtifactEvaluation, layoutEffectComputation;
    private ImageView imageCharacterArt, imageBg;
    private TextView textName, textLevel;
    private ImageView buttonCharacterMenu;
    private TextView buttonCharacterMenuHistory, buttonCharacterMenuVirtual, buttonCharacterMenuReset;
    private LinearLayout layoutCharacterMenu;

    private Handler statusCreateHandler, profileVoCreateHandler;

    private EffectComputationManager effectComputationManager;
    private CharacterAttributeManager characterAttributeManager;
    private ProfileViewManager profileViewManager;

    @Override
    protected void setContent() {
        super.setContent();
        setContentView(R.layout.activity_character_detail);
        effectComputationManager = new EffectComputationManager(getApplicationContext());
        characterAttributeManager = new CharacterAttributeManager(getApplicationContext());
        profileViewManager = ProfileViewManager.getInstance(getApplicationContext());
    }

    @Override
    protected void initView() {
        super.initView();
        characterProfileDto = (CharacterProfileDto) getIntent().getExtras().getSerializable("characterProfileDto");
        characterProfileVo = (CharacterProfileVo) getIntent().getExtras().getSerializable("characterProfileVo");
        characterOverview = ProfileConvertUtils.extractCharacterOverview(characterProfileDto);
        status = CharacterDetailActivityStatusEnum.INITIAL;
        initConstants();
        layoutArt = findViewById(R.id.layout_art);
        buttonCharacterMenu = findViewById(R.id.btn_character_menu);
        layoutCharacterMenu = findViewById(R.id.layout_character_menu);
        layoutCharacterMenu.setVisibility(View.GONE);
        buttonCharacterMenuHistory = findViewById(R.id.btn_character_menu_history);
        buttonCharacterMenuReset = findViewById(R.id.btn_character_menu_reset);
        buttonCharacterMenuVirtual = findViewById(R.id.btn_character_menu_virtual);
        layoutArtifactEvaluation = findViewById(R.id.layout_artifact_evaluation);
        layoutEffectComputation = findViewById(R.id.layout_effect_computation);
        initCharacterBaseInfoViews();
        // updateCharacterBaseInfoByVo(characterProfileVo);
        initCharacterAttributeFragment();
        initArtifactEvaluationFragment();
        setElementStyles(characterProfileVo.getElement());
        disableScrolling();
        // initEffectComputationFragment();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setCallback() {
        super.setCallback();
        statusCreateHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleStatusCreated(msg);
            }
        };
        profileVoCreateHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleProfileVoCreated(msg);
            }
        };
        buttonCharacterMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutCharacterMenu.getVisibility() == View.GONE)
                    layoutCharacterMenu.setVisibility(View.VISIBLE);
                else layoutCharacterMenu.setVisibility(View.GONE);
            }
        });
        buttonCharacterMenu.setOnTouchListener(new MenuOnTouchListener());
        buttonCharacterMenuHistory.setOnClickListener(new MenuHistoryOnClickListener());
        buttonCharacterMenuHistory.setOnTouchListener(new MenuOnTouchListener());
        buttonCharacterMenuVirtual.setOnClickListener(new MenuVirtualOnClickListener());
        buttonCharacterMenuVirtual.setOnTouchListener(new MenuOnTouchListener());
        buttonCharacterMenuReset.setOnClickListener(new MenuResetOnClickListener());
        buttonCharacterMenuReset.setOnTouchListener(new MenuOnTouchListener());
    }

    @Override
    protected void initialize() {
        super.initialize();
        new Thread(() -> createFightStatusFromProfile(false)).start();
    }


    private static class MenuOnTouchListener implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // v.performClick();
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                v.setBackgroundResource(R.drawable.bg_character_menu_press);
            } else {
                v.setBackgroundResource(R.drawable.bg_character_menu);
            }
            return false;
        }
    }

    private class MenuHistoryOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (status == CharacterDetailActivityStatusEnum.VIRTUAL ||
                    status == CharacterDetailActivityStatusEnum.SUBSTITUTION) {
                resetInitialViews();
            }
            historyProfileSelectionFragment = HistoryProfileSelectionFragment.newInstance(characterProfileDto);
            historyProfileSelectionFragment.show(getSupportFragmentManager(), "");
            layoutCharacterMenu.setVisibility(View.GONE);
        }
    }

    private class MenuVirtualOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (status == CharacterDetailActivityStatusEnum.HISTORY) {
                resetInitialViews();
            }
            if (status == CharacterDetailActivityStatusEnum.INITIAL)
                virtualProfileConfigFragment = VirtualProfileConfigFragment.newInstance(
                        characterOverview.copy());
            else if (status == CharacterDetailActivityStatusEnum.VIRTUAL ||
                    status == CharacterDetailActivityStatusEnum.SUBSTITUTION)
                virtualProfileConfigFragment = VirtualProfileConfigFragment.newInstance(
                        characterOverviewVirtual.copy());
            virtualProfileConfigFragment.show(getSupportFragmentManager(), "");
            layoutCharacterMenu.setVisibility(View.GONE);
        }
    }

    private class MenuResetOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            characterProfileDtoHistory = null;
            fightStatusHistory = null;
            characterOverviewVirtual = null;
            fightStatusVirtual = null;
            resetInitialViews();
            layoutCharacterMenu.setVisibility(View.GONE);
        }
    }

    @WorkerThread
    private void createVirtualVo() {
        Message msg = new Message();
        try {
            assert characterOverviewVirtual != null;
            msg.obj = profileViewManager.convertCharacterProfileToVo(characterOverviewVirtual, characterProfileDto.getArtifacts());
            msg.what = 1;
            profileVoCreateHandler.sendMessage(msg);
        } catch (RuntimeException e) {
            e.printStackTrace();
            msg.what = -1;
            profileVoCreateHandler.sendMessage(msg);
        }
    }

    private void handleProfileVoCreated(Message msg) {
        switch (msg.what) {
            case 1:
                characterProfileVoVirtual = (CharacterProfileVo) msg.obj;
                new Thread(this::createFightStatusFromVirtualOverview).start();
                break;
        }
    }

    @WorkerThread
    private void createFightStatusFromProfile(boolean isHistory) {
        Message msg = new Message();
        try {
            CharacterAttribute characterAttribute;
            if (!isHistory) {
                characterAttribute = characterAttributeManager.createCharacterBaseAttribute(
                        characterOverview, characterProfileDto.getArtifacts());
                msg.what = 0;
            } else {
                characterAttribute = characterAttributeManager.createCharacterBaseAttribute(
                        characterOverviewHistory, characterProfileDtoHistory.getArtifacts());
                msg.what = 1;
            }
            msg.obj = characterAttributeManager.getFightStatusByCharacterAttribute(characterAttribute);
            statusCreateHandler.sendMessage(msg);
        } catch (RuntimeException e) {
            e.printStackTrace();
            msg.what = -1;
            statusCreateHandler.sendMessage(msg);
        }
    }

    @WorkerThread
    private void createFightStatusFromVirtualOverview() {
        try {
            Message msg = new Message();
            CharacterAttribute characterAttribute = characterAttributeManager.createCharacterBaseAttribute(
                    characterOverviewVirtual, characterProfileDto.getArtifacts());
            msg.obj = characterAttributeManager.getFightStatusByCharacterAttribute(characterAttribute);
            msg.what = 2;
            statusCreateHandler.sendMessage(msg);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = -1;
            statusCreateHandler.sendMessage(msg);
        }
    }

    private void handleStatusCreated(Message msg) {
        switch (msg.what) {
            case 0: // current attribute created
                fightStatus = (FightStatus) msg.obj;
                initializeViews();
                break;
            case 1: // history status created
                fightStatusHistory = (FightStatus) msg.obj;
                showHistoryViews();
                break;
            case 2: // virtual status created
                fightStatusVirtual = (FightStatus) msg.obj;
                showVirtualViews();
                break;
        }
    }

    @Override
    protected void initScrollParameters() {
        super.initScrollParameters();
        SCROLL_MAX = getResources().getDimensionPixelOffset(R.dimen.dp_170);
        SCROLL_ELASTIC_POSITION = SCROLL_MAX / 2;
        SCROLL_THRESHOLD = 5;
        SCROLL_STEP_PROGRESS = 2f / 3;
        ART_SCALE_RATIO = 0.05f;
    }

    @Override
    protected void setConflictingScrollViews() {
        super.setConflictingScrollViews();
        lockedScrollViews.add(findViewById(R.id.view_character_detail));
        lockingScrollViews.add(findViewById(R.id.view_character_detail));
    }

    @Override
    protected void updateScrollStatus() {
        // total layout
        ViewGroup.LayoutParams layoutArtParams = layoutArt.getLayoutParams();
        layoutArtParams.height = getResources().getDimensionPixelOffset(R.dimen.dp_300) + getScrollY();
        layoutArt.setLayoutParams(layoutArtParams);
        // art
        imageCharacterArt.setScaleX(1 + getScrollProgress() * ART_SCALE_RATIO);
        imageCharacterArt.setScaleY(1 + getScrollProgress() * ART_SCALE_RATIO);
        imageCharacterArt.setTranslationX(ART_OFFSET_X * (1 - getScrollProgress()));
        // constellations
        FrameLayout.LayoutParams layoutConstellationParams = (FrameLayout.LayoutParams) layoutConstellation.getLayoutParams();
        layoutConstellationParams.bottomMargin = getScrollY();
        layoutConstellation.setLayoutParams(layoutConstellationParams);
        // weapon
        FrameLayout.LayoutParams layoutWeaponParams = (FrameLayout.LayoutParams) layoutWeapon.getLayoutParams();
        layoutWeaponParams.bottomMargin = getScrollY();
        if (getScrollProgress() >= SCROLL_STEP_PROGRESS) {
            layoutWeaponParams.rightMargin = getResources().getDimensionPixelOffset(R.dimen.dp_10) +
                    Math.round(getResources().getDimensionPixelOffset(R.dimen.dp_185) * (1 - getScrollProgress()) / (1 - SCROLL_STEP_PROGRESS));
            weaponFragment.setInfoExtend(0);
        } else {
            layoutWeaponParams.rightMargin = getResources().getDimensionPixelOffset(R.dimen.dp_195);
            weaponFragment.setInfoExtend(1 - getScrollProgress() / SCROLL_STEP_PROGRESS);
        }
        layoutWeapon.setLayoutParams(layoutWeaponParams);
        // talents
        FrameLayout.LayoutParams layoutTalentAParams = (FrameLayout.LayoutParams) layoutTalentA.getLayoutParams();
        layoutTalentAParams.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.dp_155) + getScrollY()
                + Math.round(getResources().getDimensionPixelOffset(R.dimen.dp_10) * getScrollProgress());
        layoutTalentA.setLayoutParams(layoutTalentAParams);
        layoutTalentA.setTranslationX(TALENT_WIDTH * 2 * (getScrollProgress() - 1));

        FrameLayout.LayoutParams layoutTalentEParams = (FrameLayout.LayoutParams) layoutTalentE.getLayoutParams();
        layoutTalentEParams.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.dp_155) + getScrollY()
                - Math.round(getResources().getDimensionPixelOffset(R.dimen.dp_40) * getScrollProgress());
        layoutTalentE.setLayoutParams(layoutTalentEParams);
        layoutTalentE.setTranslationX(TALENT_WIDTH * (getScrollProgress() - 1));

        FrameLayout.LayoutParams layoutTalentQParams = (FrameLayout.LayoutParams) layoutTalentQ.getLayoutParams();
        layoutTalentQParams.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.dp_155) + getScrollY()
                - Math.round(getResources().getDimensionPixelOffset(R.dimen.dp_90) * getScrollProgress());
        layoutTalentQ.setLayoutParams(layoutTalentQParams);
        // attribute
        characterAttributeFragment.setExtendProcess((getScrollProgress() - SCROLL_STEP_PROGRESS) / (1 - SCROLL_STEP_PROGRESS));
    }

    public void toShowFightEffectDetail(FightEffect fightEffect, boolean baseline) {
        Intent intent = new Intent(getApplicationContext(), FightEffectDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("fightEffect", fightEffect);
        intent.putExtras(bundle);
        if (!baseline)
            startActivityForResult(intent, 1);
        else
            startActivityForResult(intent, 2);
    }

    public void toUpdateEnemyAttribute(EnemyAttribute enemyAttribute) {
        effectComputationFragment.updateEnemyAttribute(enemyAttribute);
    }

    public void onHistorySelected(CharacterProfileDto characterProfileDtoHistory) {
        Objects.requireNonNull(historyProfileSelectionFragment.getDialog()).cancel();
        this.characterProfileDtoHistory = characterProfileDtoHistory;
        characterOverviewHistory = ProfileConvertUtils.extractCharacterOverview(characterProfileDtoHistory);
        new Thread(() -> createFightStatusFromProfile(true)).start();
    }

    public void onVirtualConfigSet(CharacterOverview characterOverviewVirtual) {
        if (((status == CharacterDetailActivityStatusEnum.INITIAL &&
                characterOverviewVirtual.equals(characterOverview))) ||
                ((status == CharacterDetailActivityStatusEnum.VIRTUAL ||
                        status == CharacterDetailActivityStatusEnum.SUBSTITUTION) &&
                        characterOverviewVirtual.equals(this.characterOverviewVirtual)))
            return;
        this.characterOverviewVirtual = characterOverviewVirtual;
        new Thread(this::createVirtualVo).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                FightEffect fightEffect = (FightEffect) data.getExtras().getSerializable("fightEffect");
                effectComputationFragment.updateByFightEffect(fightEffect, false);
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                FightEffect fightEffect = (FightEffect) data.getExtras().getSerializable("fightEffect");
                effectComputationFragment.updateByFightEffect(fightEffect, true);
            }
        }
    }


    private void initConstants() {
        ART_OFFSET_X = Math.round(-1 * getResources().getDimensionPixelOffset(R.dimen.dp_300) / 5f);
        ART_HEIGHT = getResources().getDimensionPixelOffset(R.dimen.dp_300);
        TALENT_WIDTH = getResources().getDimensionPixelOffset(R.dimen.dp_50);
    }

    private void initCharacterBaseInfoViews() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        textTitle = findViewById(R.id.text_profile_title);
        textTitle.setBackgroundColor(getColor(DynamicStyleUtils.getElementTextColor(characterProfileVo.getElement())));
        setTitleNormal();
        // name&level
        textName = findViewById(R.id.text_character_name);
        textName.setTypeface(typefaceNZBZ);
        textLevel = findViewById(R.id.text_character_level);
        textLevel.setTypeface(typefaceNum);// art
        imageCharacterArt = findViewById(R.id.img_character_art);
        imageCharacterArt.setTranslationX((ART_OFFSET_X));
        // bg
        imageBg = findViewById(R.id.img_element_bg);

        // constellation
        constellationFragmentList = new ArrayList<>();
        for (int c = 1; c <= 6; c++) {
            ConstellationFragment constellationFragment = ConstellationFragment.newInstance(
                    profileViewManager.getConstellationVoFromCharacterProfileVo(characterProfileVo, c));
            fragmentTransaction.add(R.id.layout_constellation, constellationFragment);
            constellationFragmentList.add(constellationFragment);
        }
        layoutConstellation = findViewById(R.id.layout_constellation);
        // talents
        talentFragmentMap = new HashMap<>();
        talentFragmentMap.put(SourceTalentEnum.A, TalentFragment.newInstance(
                profileViewManager.getTalentVoFromCharacterProfileVo(characterProfileVo, SourceTalentEnum.A)));
        fragmentTransaction.add(R.id.layout_talent_A, Objects.requireNonNull(talentFragmentMap.get(SourceTalentEnum.A)));
        talentFragmentMap.put(SourceTalentEnum.E, TalentFragment.newInstance(
                profileViewManager.getTalentVoFromCharacterProfileVo(characterProfileVo, SourceTalentEnum.E)));
        fragmentTransaction.add(R.id.layout_talent_E, Objects.requireNonNull(talentFragmentMap.get(SourceTalentEnum.E)));
        talentFragmentMap.put(SourceTalentEnum.Q, TalentFragment.newInstance(
                profileViewManager.getTalentVoFromCharacterProfileVo(characterProfileVo, SourceTalentEnum.Q)));
        fragmentTransaction.add(R.id.layout_talent_Q, Objects.requireNonNull(talentFragmentMap.get(SourceTalentEnum.Q)));
        layoutTalentA = findViewById(R.id.layout_talent_A);
        layoutTalentE = findViewById(R.id.layout_talent_E);
        layoutTalentQ = findViewById(R.id.layout_talent_Q);
        layoutTalentA.setTranslationX((float) TALENT_WIDTH * -2);
        layoutTalentE.setTranslationX((float) TALENT_WIDTH * -1);
        // weapon
        layoutWeapon = findViewById(R.id.layout_weapon);
        weaponFragment = WeaponFragment.newInstance(characterProfileVo.getWeapon());
        fragmentTransaction.add(R.id.layout_weapon, weaponFragment);

        fragmentTransaction.commit();
    }

    private void setElementStyles(ElementEnum element) {
        findViewById(R.id.text_title_artifact_evaluation)
                .setBackgroundColor(getColor(DynamicStyleUtils.getElementTextColor(element)));
        findViewById(R.id.text_title_fight_effect_computation)
                .setBackgroundColor(getColor(DynamicStyleUtils.getElementTextColor(element)));
        imageBg.setBackgroundColor(getColor(DynamicStyleUtils.getElementBackgroundColor(element)));
        textTitle.setBackgroundColor(getColor(DynamicStyleUtils.getElementTextColor(element)));
        textName.setTextColor(getColor(DynamicStyleUtils.getElementTextColor(element)));
        textLevel.setBackgroundColor(getColor(DynamicStyleUtils.getElementTextColor(element)));
    }

    @SuppressLint("SetTextI18n")
    private void updateCharacterPlainInfoByVo(CharacterProfileVo characterProfileVo) {
        textName.setText(" " + characterProfileVo.getCharacterName() + " ");
        textLevel.setText(getString(R.string.text_level_prefix) + characterProfileVo.getLevel());
        Bitmap bitmapArt = ImageResourceUtils.getIconBitmap(getApplicationContext(), characterProfileVo.getArtIcon());
        Bitmap bitmapArtScaled = Bitmap.createScaledBitmap(bitmapArt,
                ART_HEIGHT * bitmapArt.getWidth() / bitmapArt.getHeight(), ART_HEIGHT, true);
        imageCharacterArt.setImageBitmap(bitmapArtScaled);
    }

    private void updateCharacterInfoFragmentsByVo(CharacterProfileVo characterProfileVo) {
        for (int c = 1; c <= 6; c++) {
            constellationFragmentList.get(c - 1).updateViews(
                    profileViewManager.getConstellationVoFromCharacterProfileVo(characterProfileVo, c));
        }
        Objects.requireNonNull(talentFragmentMap.get(SourceTalentEnum.A)).updateViews(
                profileViewManager.getTalentVoFromCharacterProfileVo(characterProfileVo, SourceTalentEnum.A));
        Objects.requireNonNull(talentFragmentMap.get(SourceTalentEnum.E)).updateViews(
                profileViewManager.getTalentVoFromCharacterProfileVo(characterProfileVo, SourceTalentEnum.E));
        Objects.requireNonNull(talentFragmentMap.get(SourceTalentEnum.Q)).updateViews(
                profileViewManager.getTalentVoFromCharacterProfileVo(characterProfileVo, SourceTalentEnum.Q));
        weaponFragment.updateViews(characterProfileVo.getWeapon());
    }

    private void initCharacterAttributeFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        characterAttributeFragment = CharacterAttributeFragment.newInstance();
        fragmentTransaction.add(R.id.layout_attribute, characterAttributeFragment);
        fragmentTransaction.commit();
    }

    private void initArtifactEvaluationFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        artifactEvaluationFragment = ArtifactEvaluationFragment.newInstance(characterProfileVo);
        fragmentTransaction.add(R.id.layout_artifact_evaluation, artifactEvaluationFragment);
        fragmentTransaction.commit();
    }

    private void initEffectComputationFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        effectComputationFragment = EffectComputationFragment.newInstance(fightStatus.getAttributeBase());
        fragmentTransaction.add(R.id.layout_effect_computation, effectComputationFragment);
        fragmentTransaction.commit();
    }

    private void initializeViews() {
        setTitleNormal();
        updateCharacterPlainInfoByVo(characterProfileVo);
        initEffectComputationFragment();
        weaponFragment.updateViews(fightStatus.getAttributeWithBuff());
        characterAttributeFragment.setCharacterAttribute(fightStatus.getAttributeWithBuff());
    }


    private void resetInitialViews() {
        setTitleNormal();
        setElementStyles(characterProfileVo.getElement());
        updateCharacterPlainInfoByVo(characterProfileVo);
        updateCharacterInfoFragmentsByVo(characterProfileVo);
        if (status == CharacterDetailActivityStatusEnum.HISTORY) {
            characterAttributeFragment.setCharacterAttributeBaseline(null);
            effectComputationFragment.disableComparing();
        } else if (status == CharacterDetailActivityStatusEnum.VIRTUAL) {
            characterAttributeFragment.setCharacterAttribute(fightStatus.getAttributeWithBuff());
            characterAttributeFragment.setCharacterAttributeBaseline(null);
            weaponFragment.updateViews(fightStatus.getAttributeBase());
            effectComputationFragment.resetEffectsByCharacterAttribute(fightStatus.getAttributeBase(), null);
        } else if (status == CharacterDetailActivityStatusEnum.SUBSTITUTION) {
            characterAttributeFragment.setCharacterAttribute(fightStatus.getAttributeWithBuff());
            characterAttributeFragment.setCharacterAttributeBaseline(null);
            weaponFragment.updateViews(fightStatus.getAttributeBase());
            artifactEvaluationFragment.updateViewsByCharacterProfileVo(characterProfileVo);
            artifactEvaluationFragment.hideCriterionSelection();
            effectComputationFragment.resetEffectsByCharacterAttribute(fightStatus.getAttributeBase(), null);
        }
        status = CharacterDetailActivityStatusEnum.INITIAL;
        startScrollingTo(0f);
        disableScrolling();
    }

    private void showHistoryViews() {
        assert characterProfileDtoHistory != null && fightStatusHistory != null;
        setTitleHistory();
        characterAttributeFragment.setCharacterAttributeBaseline(fightStatusHistory.getAttributeWithBuff());
        effectComputationFragment.addEffectsBaselineByCharacterAttribute(fightStatusHistory.getAttributeBase());
        status = CharacterDetailActivityStatusEnum.HISTORY;
        enableScrolling();
        startScrollingTo(1);
    }

    private void showVirtualViews() {
        assert characterProfileVoVirtual != null && fightStatusVirtual != null;
        setTitleVirtual();
        setElementStyles(characterProfileVoVirtual.getElement());
        updateCharacterPlainInfoByVo(characterProfileVoVirtual);
        updateCharacterInfoFragmentsByVo(characterProfileVoVirtual);
        if (characterProfileDto.getCharacterId().equals(fightStatusVirtual.getAttributeBase().getCharacterId())) {
            characterAttributeFragment.setCharacterAttribute(fightStatusVirtual.getAttributeWithBuff());
            characterAttributeFragment.setCharacterAttributeBaseline(fightStatus.getAttributeWithBuff());
            weaponFragment.updateViews(fightStatusVirtual.getAttributeBase());
            effectComputationFragment.resetEffectsByCharacterAttribute(fightStatusVirtual.getAttributeBase(), fightStatus.getAttributeBase());
            status = CharacterDetailActivityStatusEnum.VIRTUAL;
            enableScrolling();
            startScrollingTo(1);
        } else {
            characterAttributeFragment.setCharacterAttribute(fightStatusVirtual.getAttributeWithBuff());
            artifactEvaluationFragment.updateViewsByCharacterProfileVo(characterProfileVoVirtual);
            artifactEvaluationFragment.hideCriterionSelection();
            weaponFragment.updateViews(fightStatusVirtual.getAttributeBase());
            effectComputationFragment.resetEffectsByCharacterAttribute(fightStatusVirtual.getAttributeBase(), null);
            status = CharacterDetailActivityStatusEnum.SUBSTITUTION;
            startScrollingTo(0f);
            disableScrolling();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setTitleNormal() {
        textTitle.setText(getString(R.string.text_uid_prefix) + characterProfileVo.getUid());
    }

    private void setTitleHistory() {
        textTitle.setText(getString(R.string.text_title_history,
                simpleDateFormat.format(characterProfileDtoHistory.getUpdateTime())));
    }

    private void setTitleVirtual() {
        textTitle.setText(getString(R.string.text_title_virtual, characterProfileVoVirtual.getCharacterName()));
    }
}