package com.megaz.knk.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.megaz.knk.R;
import com.megaz.knk.computation.BuffEffect;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.computation.FightEffect;
import com.megaz.knk.constant.EffectFieldEnum;
import com.megaz.knk.fragment.EnabledBuffFragment;
import com.megaz.knk.manager.EffectComputationManager;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.vo.BuffVo;
import com.megaz.knk.vo.EffectDetailVo;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FightEffectDetailActivity extends BaseActivity{

    private CharacterAttribute characterAttribute;
    private FightEffect fightEffect;
    private BuffVo buffVoToEnable, buffVoToDisable;

    private EffectComputationManager effectComputationManager;
    private Handler buffVoCreateHandler, fightEffectUpdateHandler;

    private TextView textEffectDesc, textEffectNumber, textCritOrNot,
            textFieldBase, textFieldUp, textFieldDamageUp, textFieldResist, textFieldDefence,
            textFieldCrit, textFieldMastery, textFieldReaction;
    private LinearLayout layoutRoot, layoutEnabledBuffs,
            layoutFieldBase, layoutFieldUp, layoutFieldDamageUp, layoutFieldResist, layoutFieldDefence,
            layoutFieldCrit, layoutFieldMastery, layoutFieldReaction;
    private ImageView buttonBuffAdd;
    private ScrollView viewEnabledBuffs;

    @Override
    protected void setContent() {
        setContentView(R.layout.activity_fight_effect_detail);
        characterAttribute = (CharacterAttribute) getIntent().getExtras().getSerializable("characterAttribute");
        fightEffect = (FightEffect) getIntent().getExtras().getSerializable("fightEffect");
        effectComputationManager = new EffectComputationManager(getApplicationContext());
    }

    @Override
    protected void initView() {
        super.initView();

        textEffectDesc = findViewById(R.id.text_effect_desc);
        textEffectNumber = findViewById(R.id.text_effect_number);
        textEffectNumber.setTypeface(typefaceNum);
        textEffectNumber.setTextColor(getColor(DynamicStyleUtils.getFightEffectColor(fightEffect, R.color.element_text_null)));
        textCritOrNot = findViewById(R.id.text_crit_or_not);
        layoutRoot = findViewById(R.id.layout_root);
        layoutRoot.setBackgroundColor(getColor(DynamicStyleUtils.getElementBackgroundColor(characterAttribute.getElement())));
        layoutEnabledBuffs = findViewById(R.id.layout_enabled_buffs);
        viewEnabledBuffs = findViewById(R.id.view_enabled_buffs);
        viewEnabledBuffs.setBackgroundResource(DynamicStyleUtils.getElementBackgroundFrame(characterAttribute.getElement()));
        buttonBuffAdd = findViewById(R.id.btn_buff_add);

        layoutFieldBase = findViewById(R.id.layout_field_base);
        textFieldBase = findViewById(R.id.text_field_base);
        textFieldBase.setTypeface(typefaceNum);
        layoutFieldUp = findViewById(R.id.layout_field_up);
        textFieldUp = findViewById(R.id.text_field_up);
        textFieldUp.setTypeface(typefaceNum);
        layoutFieldDamageUp = findViewById(R.id.layout_field_damage_up);
        textFieldDamageUp = findViewById(R.id.text_field_damage_up);
        textFieldDamageUp.setTypeface(typefaceNum);
        layoutFieldResist = findViewById(R.id.layout_field_resist);
        textFieldResist = findViewById(R.id.text_field_resist);
        textFieldResist.setTypeface(typefaceNum);
        layoutFieldDefence = findViewById(R.id.layout_field_defence);
        textFieldDefence = findViewById(R.id.text_field_defence);
        textFieldDefence.setTypeface(typefaceNum);
        layoutFieldCrit = findViewById(R.id.layout_field_crit);
        textFieldCrit = findViewById(R.id.text_field_crit);
        textFieldCrit.setTypeface(typefaceNum);
        layoutFieldMastery = findViewById(R.id.layout_field_mastery);
        textFieldMastery = findViewById(R.id.text_field_mastery);
        textFieldMastery.setTypeface(typefaceNum);
        layoutFieldReaction = findViewById(R.id.layout_field_reaction);
        textFieldReaction = findViewById(R.id.text_field_reaction);
        textFieldReaction.setTypeface(typefaceNum);

        updateView();
    }

    @Override
    protected void setCallback() {
        super.setCallback();
        buttonBuffAdd.setOnClickListener(new BuffAddOnClickListener());
        buffVoCreateHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleBuffVoCreateMessage(msg);
            }
        };
        fightEffectUpdateHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleFightEffectUpdateMessage(msg);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(this::createEnabledBuffVo).start();
    }

    private class BuffAddOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(FightEffectDetailActivity.this, BuffBrowseActivity.class);
            intent.putExtra("fightEffect", fightEffect);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0) {
            if(resultCode == RESULT_OK) {
                assert data != null;
                buffVoToEnable = (BuffVo) data.getExtras().getSerializable("buffVo");
                new Thread(this::enableBuffByBuffVo).start();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("fightEffect", fightEffect);
        setResult(RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();
    }


    public void toDisableBuff(BuffVo buffVo) {
        buffVoToDisable = buffVo;
        new Thread(this::disableBuffByBuffVo).start();
    }

    private void handleBuffVoCreateMessage(Message msg) {
        switch (msg.what) {
            case 0:
                List<BuffVo> buffVoList = (List<BuffVo>) msg.obj;
                updateEnabledBuffList(buffVoList);
        }
    }

    private void handleFightEffectUpdateMessage(Message msg) {
        switch (msg.what) {
            case 0:
                updateView();
                new Thread(this::createEnabledBuffVo).start();
        }
    }

    private void enableBuffByBuffVo() {
        String buffId = buffVoToEnable.getBuffId();
        BuffEffect buffEffect = fightEffect.getAvailableBuffEffects().get(buffId);
        try {
            assert buffEffect != null;
            if (buffEffect.getFromSelf()) {
                effectComputationManager.enableBuffEffect(fightEffect, buffEffect, null);
            } else {
                effectComputationManager.enableBuffEffect(fightEffect, buffEffect, buffVoToEnable.getBuffInputParamList());
            }
            Message msg = new Message();
            msg.what = 0;
            fightEffectUpdateHandler.sendMessage(msg);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void disableBuffByBuffVo() {
        String buffId = buffVoToDisable.getBuffId();
        BuffEffect buffEffect = fightEffect.getAvailableBuffEffects().get(buffId);
        try {
            assert buffEffect != null;
            effectComputationManager.disableBuffEffect(fightEffect, buffEffect);
            Message msg = new Message();
            msg.what = 0;
            fightEffectUpdateHandler.sendMessage(msg);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void createEnabledBuffVo() {
        try {
            List<BuffEffect> enabledBuffEffectList = fightEffect.getEnabledBuffEffects();
            List<BuffVo> buffVoList = enabledBuffEffectList.stream()
                    .map(buffEffect -> effectComputationManager.createBuffVo(buffEffect)).collect(Collectors.toList());
            Message msg = new Message();
            msg.what = 0;
            msg.obj = buffVoList;
            buffVoCreateHandler.sendMessage(msg);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 1;
            msg.obj = e.getMessage();
            buffVoCreateHandler.sendMessage(msg);
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateView() {
        EffectDetailVo effectDetailVo = effectComputationManager.createFightEffectDetail(fightEffect);

        textEffectDesc.setText(effectDetailVo.getEffectDesc());
        if(effectDetailVo.getCanCritical()) {
            textCritOrNot.setVisibility(View.VISIBLE);
        } else {
            textCritOrNot.setVisibility(View.GONE);
        }
        textEffectNumber.setText(effectDetailVo.getEffectValue());

        if(effectDetailVo.getFieldDetail().containsKey(EffectFieldEnum.BASE)) {
            layoutFieldBase.setVisibility(View.VISIBLE);
            double baseValue = Objects.requireNonNull(effectDetailVo.getFieldDetail().get(EffectFieldEnum.BASE));
            String text = "";
            if(baseValue > 1000) {
                text += String.format("%d", (int)baseValue);
            } else {
                text += String.format("%.2f", baseValue);
            }
            if(effectDetailVo.getFieldDetail().containsKey(EffectFieldEnum.BASE_MULTIPLE)) {
                double multiplier = Objects.requireNonNull(effectDetailVo.getFieldDetail().get(EffectFieldEnum.BASE_MULTIPLE));
                text += "×"+String.format("%.2f", multiplier);
            }
            if(effectDetailVo.getFieldDetail().containsKey(EffectFieldEnum.BASE_ADD)) {
                double addend = Objects.requireNonNull(effectDetailVo.getFieldDetail().get(EffectFieldEnum.BASE_ADD));
                text += "×"+String.format("%d", (int)addend);
            }
            textFieldBase.setText(text);
        } else {
            layoutFieldBase.setVisibility(View.GONE);
        }
        if(effectDetailVo.getFieldDetail().containsKey(EffectFieldEnum.UP)) {
            layoutFieldUp.setVisibility(View.VISIBLE);
            double value = Objects.requireNonNull(effectDetailVo.getFieldDetail().get(EffectFieldEnum.UP));
            textFieldUp.setText(String.format("%.2f", value*100)+"%");
        } else {
            layoutFieldUp.setVisibility(View.GONE);
        }
        if(effectDetailVo.getFieldDetail().containsKey(EffectFieldEnum.DAMAGE_UP)) {
            layoutFieldDamageUp.setVisibility(View.VISIBLE);
            double value = Objects.requireNonNull(effectDetailVo.getFieldDetail().get(EffectFieldEnum.DAMAGE_UP));
            textFieldDamageUp.setText(String.format("%.2f", value*100)+"%");
        } else {
            layoutFieldDamageUp.setVisibility(View.GONE);
        }
        if(effectDetailVo.getFieldDetail().containsKey(EffectFieldEnum.RESIST)) {
            layoutFieldResist.setVisibility(View.VISIBLE);
            double value = Objects.requireNonNull(effectDetailVo.getFieldDetail().get(EffectFieldEnum.RESIST));
            textFieldResist.setText(String.format("%.2f", value*100)+"%");
        } else {
            layoutFieldResist.setVisibility(View.GONE);
        }
        if(effectDetailVo.getFieldDetail().containsKey(EffectFieldEnum.DEFENCE)) {
            layoutFieldDefence.setVisibility(View.VISIBLE);
            double value = Objects.requireNonNull(effectDetailVo.getFieldDetail().get(EffectFieldEnum.DEFENCE));
            textFieldDefence.setText(String.format("%.2f", value*100)+"%");
        } else {
            layoutFieldDefence.setVisibility(View.GONE);
        }
        if(effectDetailVo.getFieldDetail().containsKey(EffectFieldEnum.CRIT_RATE) &&
                effectDetailVo.getFieldDetail().containsKey(EffectFieldEnum.CRIT_DMG)) {
            layoutFieldCrit.setVisibility(View.VISIBLE);
            double critRate = Objects.requireNonNull(effectDetailVo.getFieldDetail().get(EffectFieldEnum.CRIT_RATE));
            double critDmg = Objects.requireNonNull(effectDetailVo.getFieldDetail().get(EffectFieldEnum.CRIT_DMG));
            textFieldCrit.setText(String.format("%.2f", critRate*100)+"%/"+String.format("%.2f",critDmg*100)+"%");
        } else {
            layoutFieldCrit.setVisibility(View.GONE);
        }
        if(effectDetailVo.getFieldDetail().containsKey(EffectFieldEnum.MASTERY)) {
            layoutFieldMastery.setVisibility(View.VISIBLE);
            double value = Objects.requireNonNull(effectDetailVo.getFieldDetail().get(EffectFieldEnum.MASTERY));
            textFieldMastery.setText(String.format("%.2f", value*100)+"%");
        } else {
            layoutFieldMastery.setVisibility(View.GONE);
        }
        if(effectDetailVo.getFieldDetail().containsKey(EffectFieldEnum.REACTION)) {
            layoutFieldReaction.setVisibility(View.VISIBLE);
            double value = Objects.requireNonNull(effectDetailVo.getFieldDetail().get(EffectFieldEnum.REACTION));
            textFieldReaction.setText(String.format("%.2f", value*100)+"%");
        } else {
            layoutFieldReaction.setVisibility(View.GONE);
        }

    }

    private void updateEnabledBuffList(List<BuffVo> buffVoList) {
        buffVoList.sort(Comparator.comparing(BuffVo::getBuffId));
        layoutEnabledBuffs.removeAllViews();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for(int i=0;i< buffVoList.size();i++) {
            LinearLayout layoutContainer = new LinearLayout(getApplicationContext());
            int id = View.generateViewId();
            layoutContainer.setId(id);
            EnabledBuffFragment enabledBuffFragment = EnabledBuffFragment.newInstance(buffVoList.get(i));
            fragmentTransaction.add(id, enabledBuffFragment);
            layoutEnabledBuffs.addView(layoutContainer);
        }
        fragmentTransaction.commit();
    }

}