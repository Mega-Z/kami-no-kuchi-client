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
import androidx.fragment.app.FragmentTransaction;

import com.megaz.knk.R;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.computation.EnemyAttribute;
import com.megaz.knk.computation.FightEffect;
import com.megaz.knk.computation.FightStatus;
import com.megaz.knk.constant.CharacterDetailActivityStatusEnum;
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

import java.util.Objects;

public class CharacterDetailActivity extends ElasticScrollActivity {

    private int ART_OFFSET_X, ART_HEIGHT, TALENT_WIDTH;
    private float SCROLL_STEP_PROGRESS; // 划动动画的中间进度，武器信息缩回开始移动
    private float ART_SCALE_RATIO;
    private CharacterDetailActivityStatusEnum status;

//    private CharacterAttribute characterBaseAttribute, characterAttributeHistory, characterAttributeVirtual;
    private FightStatus fightStatus, fightStatusHistory, fightStatusVirtual;
    private CharacterProfileVo characterProfileVo, characterProfileVoVirtual;
    private CharacterProfileDto characterProfileDto, characterProfileDtoHistory, characterProfileDtoVirtual;

    private WeaponFragment weaponFragment;
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
    private ImageView imageCharacterArt;
    private ImageView buttonCharacterMenu;
    private TextView buttonCharacterMenuHistory, buttonCharacterMenuVirtual, buttonCharacterMenuReset;
    private LinearLayout layoutCharacterMenu;

    private Handler statusCreateHandler;

    private EffectComputationManager effectComputationManager;
    private CharacterAttributeManager characterAttributeManager;
    private ProfileViewManager profileViewManager;

    @Override
    protected void setContent() {
        super.setContent();
        setContentView(R.layout.activity_character_detail);
        effectComputationManager = new EffectComputationManager(getApplicationContext());
        characterAttributeManager = new CharacterAttributeManager(getApplicationContext());
    }

    @Override
    protected void initView() {
        super.initView();
        characterProfileDto = (CharacterProfileDto) getIntent().getExtras().getSerializable("characterProfileDto");
        characterProfileVo = (CharacterProfileVo) getIntent().getExtras().getSerializable("characterProfileVo");
        status = CharacterDetailActivityStatusEnum.INITIAL;
        initConstants();
        layoutArt = findViewById(R.id.layout_art);
        findViewById(R.id.text_title_artifact_evaluation)
                .setBackgroundColor(getColor(DynamicStyleUtils.getElementTextColor(characterProfileDto.getElement())));
        findViewById(R.id.text_title_fight_effect_computation)
                .setBackgroundColor(getColor(DynamicStyleUtils.getElementTextColor(characterProfileDto.getElement())));
        buttonCharacterMenu = findViewById(R.id.btn_character_menu);
        layoutCharacterMenu = findViewById(R.id.layout_character_menu);
        layoutCharacterMenu.setVisibility(View.GONE);
        buttonCharacterMenuHistory = findViewById(R.id.btn_character_menu_history);
        buttonCharacterMenuReset = findViewById(R.id.btn_character_menu_reset);
        buttonCharacterMenuVirtual = findViewById(R.id.btn_character_menu_virtual);
        layoutArtifactEvaluation = findViewById(R.id.layout_artifact_evaluation);
        layoutEffectComputation = findViewById(R.id.layout_effect_computation);
        initCharacterBaseInfo();
        initWeaponInfo();
        initCharacterAttributeFragment();
        initArtifactEvaluationFragment();
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
        new Thread(() -> createCharacterAttributeWithoutBuff(false)).start();
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
            historyProfileSelectionFragment = HistoryProfileSelectionFragment.newInstance(characterProfileDto);
            historyProfileSelectionFragment.show(getSupportFragmentManager(), "");
            layoutCharacterMenu.setVisibility(View.GONE);
        }
    }

    private class MenuVirtualOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            CharacterProfileDto copy = ProfileConvertUtils.copyCharacterProfile(characterProfileDto);
            virtualProfileConfigFragment = VirtualProfileConfigFragment.newInstance(copy);
            virtualProfileConfigFragment.show(getSupportFragmentManager(), "");
            layoutCharacterMenu.setVisibility(View.GONE);
        }
    }

    private class MenuResetOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            characterProfileDtoHistory = null;
            fightStatusHistory = null;
            resetInitialViews();
            startScrollingTo(0f);
            disableScrolling();
            layoutCharacterMenu.setVisibility(View.GONE);
        }
    }

    private void createCharacterAttributeWithoutBuff(boolean isBaseline) {
        try {
            CharacterAttribute characterAttribute;
            Message msg = new Message();
            if (!isBaseline) {
                characterAttribute = characterAttributeManager.createCharacterBaseAttribute(characterProfileDto);
                msg.what = 0;
            } else {
                characterAttribute = characterAttributeManager.createCharacterBaseAttribute(characterProfileDtoHistory);
                msg.what = 1;
            }
            msg.obj = characterAttributeManager.getFightStatusByCharacterAttribute(characterAttribute);
            statusCreateHandler.sendMessage(msg);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 2;
            statusCreateHandler.sendMessage(msg);
        }
    }

    private void handleStatusCreated(Message msg) {
        switch (msg.what) {
            case 0: // current attribute created
                fightStatus = (FightStatus) msg.obj;
                initEffectComputationFragment();
                characterAttributeFragment.setCharacterAttribute(fightStatus.getAttributeWithBuff());
                break;
            case 1:
                fightStatusHistory = (FightStatus) msg.obj;
                showHistoryViews();
                enableScrolling();
                startScrollingTo(1);
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
        if(!baseline)
            startActivityForResult(intent, 1);
        else
            startActivityForResult(intent, 2);
    }

    public void toUpdateEnemyAttribute(EnemyAttribute enemyAttribute) {
        effectComputationFragment.updateEnemyAttribute(enemyAttribute);
    }

    public void onHistorySelected(CharacterProfileDto characterProfileHistory) {
        Objects.requireNonNull(historyProfileSelectionFragment.getDialog()).cancel();
        characterProfileDtoHistory = characterProfileHistory;
        new Thread(() -> createCharacterAttributeWithoutBuff(true)).start();
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


    @SuppressLint("SetTextI18n")
    private void initCharacterBaseInfo() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        textTitle = findViewById(R.id.text_profile_title);
        textTitle.setBackgroundColor(getColor(DynamicStyleUtils.getElementTextColor(characterProfileVo.getElement())));
        setTitleNormal();
        // name&level
        TextView textName = findViewById(R.id.text_character_name);
        textName.setTypeface(typefaceNZBZ);
        textName.setText(" " + characterProfileVo.getCharacterName() + " ");
        textName.setTextColor(getColor(DynamicStyleUtils.getElementTextColor(characterProfileVo.getElement())));
        TextView textLevel = findViewById(R.id.text_character_level);
        textLevel.setTypeface(typefaceNum);
        textLevel.setText(getString(R.string.text_level_prefix) + characterProfileVo.getLevel());
        textLevel.setBackgroundColor(getColor(DynamicStyleUtils.getElementTextColor(characterProfileVo.getElement())));
        // art
        imageCharacterArt = findViewById(R.id.img_character_art);
        Bitmap bitmapArt = ImageResourceUtils.getIconBitmap(getApplicationContext(), characterProfileVo.getArtIcon());
        Bitmap bitmapArtScaled = Bitmap.createScaledBitmap(bitmapArt,
                ART_HEIGHT * bitmapArt.getWidth() / bitmapArt.getHeight(), ART_HEIGHT, true);
        imageCharacterArt.setImageBitmap(bitmapArtScaled);
        imageCharacterArt.setTranslationX((ART_OFFSET_X));
        // bg
        ImageView imageBg = findViewById(R.id.img_element_bg);
        imageBg.setBackgroundColor(getColor(DynamicStyleUtils.getElementBackgroundColor(characterProfileVo.getElement())));
//        imageBg.setImageBitmap(ImageResourceUtils.getBackgroundByElement(getApplicationContext(), characterProfileVo.getElement()));
        // constellation
        for (int c = 1; c <= 6; c++) {
            ConstellationVo constellationVo = new ConstellationVo();
            constellationVo.setElement(characterProfileVo.getElement());
            constellationVo.setActive(characterProfileVo.getConstellation() >= c);
            constellationVo.setIcon(characterProfileVo.getConsIcons().get(c - 1));
            ConstellationFragment constellationFragment = ConstellationFragment.newInstance(constellationVo);
            fragmentTransaction.add(R.id.layout_constellation, constellationFragment);
        }
        layoutConstellation = findViewById(R.id.layout_constellation);
        // talents
        TalentVo talentVoA = new TalentVo();
        talentVoA.setElement(characterProfileVo.getElement());
        talentVoA.setIcon(characterProfileVo.getTalentAIcon());
        talentVoA.setBaseLevel(characterProfileVo.getTalentABaseLevel());
        talentVoA.setPlusLevel(characterProfileVo.getTalentAPlusLevel());
        TalentFragment talentFragmentA = TalentFragment.newInstance(talentVoA);
        fragmentTransaction.add(R.id.layout_talent_A, talentFragmentA);

        TalentVo talentVoE = new TalentVo();
        talentVoE.setElement(characterProfileVo.getElement());
        talentVoE.setIcon(characterProfileVo.getTalentEIcon());
        talentVoE.setBaseLevel(characterProfileVo.getTalentEBaseLevel());
        talentVoE.setPlusLevel(characterProfileVo.getTalentEPlusLevel());
        TalentFragment talentFragmentE = TalentFragment.newInstance(talentVoE);
        fragmentTransaction.add(R.id.layout_talent_E, talentFragmentE);

        TalentVo talentVoQ = new TalentVo();
        talentVoQ.setElement(characterProfileVo.getElement());
        talentVoQ.setIcon(characterProfileVo.getTalentQIcon());
        talentVoQ.setBaseLevel(characterProfileVo.getTalentQBaseLevel());
        talentVoQ.setPlusLevel(characterProfileVo.getTalentQPlusLevel());
        TalentFragment talentFragmentQ = TalentFragment.newInstance(talentVoQ);
        fragmentTransaction.add(R.id.layout_talent_Q, talentFragmentQ);

        layoutTalentA = findViewById(R.id.layout_talent_A);
        layoutTalentE = findViewById(R.id.layout_talent_E);
        layoutTalentQ = findViewById(R.id.layout_talent_Q);
        layoutTalentA.setTranslationX((float) TALENT_WIDTH * -2);
        layoutTalentE.setTranslationX((float) TALENT_WIDTH * -1);

        fragmentTransaction.commit();
    }

    private void initWeaponInfo() {
        layoutWeapon = findViewById(R.id.layout_weapon);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        weaponFragment = WeaponFragment.newInstance(characterProfileVo.getWeapon());
        fragmentTransaction.add(R.id.layout_weapon, weaponFragment);
        fragmentTransaction.commit();
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

    private void resetInitialViews() {
        setTitleNormal();
        if(status == CharacterDetailActivityStatusEnum.HISTORY) {
            characterAttributeFragment.setCharacterAttributeBaseline(null);
            effectComputationFragment.disableComparing();
        }
        status = CharacterDetailActivityStatusEnum.INITIAL;
    }

    private void showHistoryViews() {
        assert characterProfileDtoHistory != null && fightStatusHistory != null;
        setTitleHistory();
        characterAttributeFragment.setCharacterAttributeBaseline(fightStatusHistory.getAttributeWithBuff());
        effectComputationFragment.enableComparing(null, fightStatusHistory.getAttributeBase());
        status = CharacterDetailActivityStatusEnum.HISTORY;
    }

    @SuppressLint("SetTextI18n")
    private void setTitleNormal() {
        textTitle.setText(getString(R.string.text_uid_prefix) + characterProfileVo.getUid());
    }

    @SuppressLint("SetTextI18n")
    private void setTitleHistory() {
        textTitle.setText(getString(R.string.text_title_history_prefix) +
                simpleDateFormat.format(characterProfileDtoHistory.getUpdateTime()));
    }

}