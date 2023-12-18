package com.megaz.knk.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.megaz.knk.R;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.computation.EnemyAttribute;
import com.megaz.knk.computation.FightEffect;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.fragment.ArtifactEvaluationFragment;
import com.megaz.knk.fragment.CharacterAttributeFragment;
import com.megaz.knk.fragment.ConstellationFragment;
import com.megaz.knk.fragment.EffectComputationFragment;
import com.megaz.knk.fragment.TalentFragment;
import com.megaz.knk.fragment.WeaponFragment;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.BuffVo;
import com.megaz.knk.vo.CharacterProfileVo;
import com.megaz.knk.vo.ConstellationVo;
import com.megaz.knk.vo.TalentVo;

public class CharacterDetailActivity extends ElasticScrollActivity {

    private int ART_OFFSET_X, ART_HEIGHT, TALENT_WIDTH;
    private float SCROLL_STEP_PROGRESS; // 划动动画的中间进度，武器信息缩回开始移动
    private float ART_SCALE_RATIO;

    private CharacterAttribute characterAttribute;
    private CharacterProfileVo characterProfileVo;

    private WeaponFragment weaponFragment;
    private CharacterAttributeFragment characterAttributeFragment;
    private ArtifactEvaluationFragment artifactEvaluationFragment;
    private EffectComputationFragment effectComputationFragment;

    private FrameLayout layoutArt;
    private LinearLayout layoutConstellation;
    private LinearLayout layoutTalentA;
    private LinearLayout layoutTalentE;
    private LinearLayout layoutTalentQ;
    private LinearLayout layoutWeapon;
    private ImageView imageCharacterArt;

    @Override
    protected void setContent() {
        super.setContent();
        setContentView(R.layout.activity_character_detail);
    }

    @Override
    protected void initView(){
        super.initView();
        characterAttribute = (CharacterAttribute) getIntent().getExtras().getSerializable("characterAttribute");
        characterProfileVo = (CharacterProfileVo) getIntent().getExtras().getSerializable("characterProfileVo");
        initConstants();
        layoutArt = findViewById(R.id.layout_art);
        findViewById(R.id.text_title_artifact_evaluation)
                .setBackgroundColor(getColor(DynamicStyleUtils.getElementTextColor(characterAttribute.getElement())));
        findViewById(R.id.text_title_fight_effect_computation)
                .setBackgroundColor(getColor(DynamicStyleUtils.getElementTextColor(characterAttribute.getElement())));
        initCharacterBaseInfo();
        initWeaponInfo();
        initCharacterAttribute();
        initArtifactEvaluation();
        initEffectComputation();
    }

    @Override
    protected void initScrollParameters() {
        super.initScrollParameters();
        SCROLL_MAX = getResources().getDimensionPixelOffset(R.dimen.dp_170);
        SCROLL_ELASTIC_POSITION = SCROLL_MAX / 2;
        SCROLL_THRESHOLD = 5;
        SCROLL_STEP_PROGRESS = 2f/3;
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
        imageCharacterArt.setScaleX(1+getScrollProgress()*ART_SCALE_RATIO);
        imageCharacterArt.setScaleY(1+getScrollProgress()*ART_SCALE_RATIO);
        imageCharacterArt.setTranslationX(ART_OFFSET_X*(1-getScrollProgress()));
        // constellations
        FrameLayout.LayoutParams layoutConstellationParams = (FrameLayout.LayoutParams) layoutConstellation.getLayoutParams();
        layoutConstellationParams.bottomMargin = getScrollY();
        layoutConstellation.setLayoutParams(layoutConstellationParams);
        // weapon
        FrameLayout.LayoutParams layoutWeaponParams = (FrameLayout.LayoutParams) layoutWeapon.getLayoutParams();
        layoutWeaponParams.bottomMargin = getScrollY();
        if(getScrollProgress() >= SCROLL_STEP_PROGRESS) {
            layoutWeaponParams.rightMargin = getResources().getDimensionPixelOffset(R.dimen.dp_10) +
                    Math.round(getResources().getDimensionPixelOffset(R.dimen.dp_185) * (1-getScrollProgress())/(1-SCROLL_STEP_PROGRESS));
            weaponFragment.setInfoExtend(0);
        }else {
            layoutWeaponParams.rightMargin = getResources().getDimensionPixelOffset(R.dimen.dp_195);
            weaponFragment.setInfoExtend(1-getScrollProgress()/SCROLL_STEP_PROGRESS);
        }
        layoutWeapon.setLayoutParams(layoutWeaponParams);
        // talents
        FrameLayout.LayoutParams layoutTalentAParams = (FrameLayout.LayoutParams) layoutTalentA.getLayoutParams();
        layoutTalentAParams.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.dp_155) + getScrollY()
                + Math.round(getResources().getDimensionPixelOffset(R.dimen.dp_10) * getScrollProgress());
        layoutTalentA.setLayoutParams(layoutTalentAParams);
        layoutTalentA.setTranslationX(TALENT_WIDTH * 2 * (getScrollProgress()-1));

        FrameLayout.LayoutParams layoutTalentEParams = (FrameLayout.LayoutParams) layoutTalentE.getLayoutParams();
        layoutTalentEParams.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.dp_155) + getScrollY()
                - Math.round(getResources().getDimensionPixelOffset(R.dimen.dp_40) * getScrollProgress());
        layoutTalentE.setLayoutParams(layoutTalentEParams);
        layoutTalentE.setTranslationX(TALENT_WIDTH * (getScrollProgress()-1));

        FrameLayout.LayoutParams layoutTalentQParams = (FrameLayout.LayoutParams) layoutTalentQ.getLayoutParams();
        layoutTalentQParams.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.dp_155) + getScrollY()
                - Math.round(getResources().getDimensionPixelOffset(R.dimen.dp_90) * getScrollProgress());
        layoutTalentQ.setLayoutParams(layoutTalentQParams);

    }

    public void toShowFightEffectDetail(FightEffect fightEffect) {
        Intent intent = new Intent(getApplicationContext(), FightEffectDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("fightEffect", fightEffect);
        bundle.putSerializable("characterAttribute", characterAttribute);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }

    public void toUpdateEnemyAttribute(EnemyAttribute enemyAttribute) {
        effectComputationFragment.updateEnemyAttribute(enemyAttribute);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                assert data != null;
                FightEffect fightEffect = (FightEffect) data.getExtras().getSerializable("fightEffect");
                effectComputationFragment.updateFightEffect(fightEffect);
            }
        }
    }


    private void initConstants() {
        ART_OFFSET_X = Math.round(-1*getResources().getDimensionPixelOffset(R.dimen.dp_300)/5f);
        ART_HEIGHT = getResources().getDimensionPixelOffset(R.dimen.dp_300);
        TALENT_WIDTH = getResources().getDimensionPixelOffset(R.dimen.dp_50);
    }


    @SuppressLint("SetTextI18n")
    private void initCharacterBaseInfo() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // name&level
        TextView textName = findViewById(R.id.text_character_name);
        textName.setTypeface(typefaceNZBZ);
        textName.setText(characterProfileVo.getCharacterName()+" ");
        textName.setTextColor(getColor(DynamicStyleUtils.getElementTextColor(characterProfileVo.getElement())));
        TextView textLevel = findViewById(R.id.text_character_level);
        textLevel.setTypeface(typefaceNum);
        textLevel.setText(getString(R.string.text_level_prefix) + characterProfileVo.getLevel());
        textLevel.setBackgroundColor(getColor(DynamicStyleUtils.getElementTextColor(characterProfileVo.getElement())));
        // art
        imageCharacterArt = findViewById(R.id.img_character_art);
        Bitmap bitmapArt = ImageResourceUtils.getIconBitmap(getApplicationContext(), characterProfileVo.getArtIcon());
        Bitmap bitmapArtScaled = Bitmap.createScaledBitmap(bitmapArt,
                ART_HEIGHT*bitmapArt.getWidth()/bitmapArt.getHeight(), ART_HEIGHT, true);
        imageCharacterArt.setImageBitmap(bitmapArtScaled);
        imageCharacterArt.setTranslationX((ART_OFFSET_X));
        // bg
        ImageView imageBg = findViewById(R.id.img_element_bg);
        imageBg.setBackgroundColor(getColor(DynamicStyleUtils.getElementBackgroundColor(characterProfileVo.getElement())));
//        imageBg.setImageBitmap(ImageResourceUtils.getBackgroundByElement(getApplicationContext(), characterProfileVo.getElement()));
        // constellation
        for(int c=1;c<=6;c++) {
            ConstellationVo constellationVo = new ConstellationVo();
            constellationVo.setElement(characterProfileVo.getElement());
            constellationVo.setActive(characterProfileVo.getConstellation() >= c);
            constellationVo.setIcon(characterProfileVo.getConsIcons().get(c-1));
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
        layoutTalentA.setTranslationX((float)TALENT_WIDTH*-2);
        layoutTalentE.setTranslationX((float)TALENT_WIDTH*-1);

        fragmentTransaction.commit();
    }

    private void initWeaponInfo() {
        layoutWeapon = findViewById(R.id.layout_weapon);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        weaponFragment = WeaponFragment.newInstance(characterProfileVo.getWeapon());
        fragmentTransaction.add(R.id.layout_weapon, weaponFragment);
        fragmentTransaction.commit();
    }

    private void initCharacterAttribute() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        characterAttributeFragment = CharacterAttributeFragment.newInstance(characterProfileVo);
        fragmentTransaction.add(R.id.layout_attribute, characterAttributeFragment);
        fragmentTransaction.commit();
    }

    private void initArtifactEvaluation() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        artifactEvaluationFragment = ArtifactEvaluationFragment.newInstance(characterProfileVo);
        fragmentTransaction.add(R.id.layout_artifact_evaluation, artifactEvaluationFragment);
        fragmentTransaction.commit();
    }

    private void initEffectComputation() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        effectComputationFragment = EffectComputationFragment.newInstance(characterAttribute);
        fragmentTransaction.add(R.id.layout_effect_computation, effectComputationFragment);
        fragmentTransaction.commit();
    }
}