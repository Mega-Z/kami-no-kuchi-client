package com.megaz.knk.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.megaz.knk.R;
import com.megaz.knk.fragment.ConstellationFragment;
import com.megaz.knk.fragment.TalentFragment;
import com.megaz.knk.fragment.WeaponFragment;
import com.megaz.knk.manager.ImageResourceManager;
import com.megaz.knk.vo.CharacterProfileVo;
import com.megaz.knk.vo.ConstellationVo;
import com.megaz.knk.vo.TalentVo;

import java.security.PrivilegedAction;

public class CharacterDetailActivity extends BaseActivity {

    private int ART_OFFSET_X, ART_HEIGHT, TALENT_WIDTH;
    
    private CharacterProfileVo characterProfileVo;

    private WeaponFragment weaponFragment;

    private LinearLayout layoutTalentA, layoutTalentE, layoutTalentQ, layoutWeapon;
    private ImageView imageCharacterArt;


    @Override
    protected void initView(){
        setContentView(R.layout.activity_character_detail);
        characterProfileVo = (CharacterProfileVo) getIntent().getExtras().getSerializable("characterProfileVo");
        initConstants();
        initCharacterBaseInfo();
        initWeaponInfo();
        initCharacterAttribute();
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
        textName.setTypeface(typefaceCn);
        textName.setText(characterProfileVo.getCharacterName()+" ");
        TextView textLevel = findViewById(R.id.text_character_level);
        textLevel.setTypeface(typefaceNum);
        textLevel.setText(getString(R.string.text_level_prefix) + characterProfileVo.getLevel());
        // art
        imageCharacterArt = findViewById(R.id.img_character_art);
        Bitmap bitmapArt = ImageResourceManager.getIconBitmap(getApplicationContext(), characterProfileVo.getArtIcon());
        imageCharacterArt.setImageBitmap(bitmapArt);
        float artScale = ART_HEIGHT/(float)bitmapArt.getHeight();
        imageCharacterArt.setScaleX(artScale);
        imageCharacterArt.setScaleY(artScale);
        imageCharacterArt.setX((float)(ART_OFFSET_X));
        // bg
        ImageView imageBg = findViewById(R.id.img_element_bg);
        imageBg.setImageBitmap(ImageResourceManager.getBackgroundByElement(getApplicationContext(), characterProfileVo.getElement()));
        // constellation
        for(int c=1;c<=6;c++) {
            ConstellationVo constellationVo = new ConstellationVo();
            constellationVo.setElement(characterProfileVo.getElement());
            constellationVo.setActive(characterProfileVo.getConstellation() >= c);
            constellationVo.setIcon(characterProfileVo.getConsIcons().get(c-1));
            ConstellationFragment constellationFragment = ConstellationFragment.newInstance(constellationVo);
            fragmentTransaction.add(R.id.layout_constellation, constellationFragment);
        }
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
        layoutTalentA.setX((float)TALENT_WIDTH*-2);
        layoutTalentE.setX((float)TALENT_WIDTH*-1);

        fragmentTransaction.commit();
    }

    private void initWeaponInfo() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        weaponFragment = WeaponFragment.newInstance(characterProfileVo.getWeapon());
        fragmentTransaction.add(R.id.layout_weapon, weaponFragment);
        fragmentTransaction.commit();
    }

    private void initCharacterAttribute() {
    }
}