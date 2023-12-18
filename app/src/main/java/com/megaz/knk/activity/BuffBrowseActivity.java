package com.megaz.knk.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.megaz.knk.R;
import com.megaz.knk.computation.BuffEffect;
import com.megaz.knk.computation.FightEffect;
import com.megaz.knk.constant.BuffSourceEnum;
import com.megaz.knk.constant.GenshinConstantMeta;
import com.megaz.knk.fragment.BuffFolderFragment;
import com.megaz.knk.fragment.EnabledBuffFragment;
import com.megaz.knk.manager.EffectComputationManager;
import com.megaz.knk.vo.BuffVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BuffBrowseActivity extends BaseActivity {
    private FightEffect fightEffect;

    private EffectComputationManager effectComputationManager;
    private Handler buffVoCreateHandler;

    private Map<BuffSourceEnum, BuffFolderFragment> buffFolderFragments;

//    private ImageView imageLoading;
//    private ObjectAnimator animatorLoading;

    @Override
    protected void setContent() {
        setContentView(R.layout.activity_buff_browse);
        fightEffect = (FightEffect) getIntent().getExtras().getSerializable("fightEffect");
        effectComputationManager = new EffectComputationManager(getApplicationContext());
        buffFolderFragments = new HashMap<>();
    }

    @Override
    protected void initView() {
        super.initView();
//        imageLoading = findViewById(R.id.img_loading);
//        animatorLoading = ObjectAnimator.ofFloat(imageLoading, "rotation", -360f);
//        animatorLoading.setDuration(1000);
//        animatorLoading.setRepeatCount(ValueAnimator.INFINITE);
    }

    @Override
    protected void setCallback() {
        super.setCallback();
        buffVoCreateHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                handlerBuffVoCreateMessage(msg);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(this::createEnabledBuffVo).start();
//        animatorLoading.start();
    }


    private void createEnabledBuffVo() {
        try {
            List<BuffEffect> availableBuffEffectList = new ArrayList<>(fightEffect.getAvailableBuffEffects().values());
            availableBuffEffectList.removeAll(fightEffect.getEnabledBuffEffects());
            List<BuffVo> buffVoList = availableBuffEffectList.stream()
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

    private void handlerBuffVoCreateMessage(Message msg) {
        switch (msg.what) {
            case 0:
                List<BuffVo> buffVoList = (List<BuffVo>) msg.obj;
                updateAvailableBuffList(buffVoList);
        }
    }

    private void updateAvailableBuffList(List<BuffVo> buffVoList) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Map<BuffSourceEnum, ArrayList<BuffVo>> buffVoMap = new HashMap<>();
        for (BuffSourceEnum source : GenshinConstantMeta.BUFF_SOURCE_LIST) {
            buffVoMap.put(source, new ArrayList<>());
            if (buffFolderFragments.containsKey(source)) {
                fragmentTransaction.remove(Objects.requireNonNull(buffFolderFragments.get(source)));
            }
        }
        for (BuffVo buffVo : buffVoList) {
            Objects.requireNonNull(buffVoMap.get(buffVo.getSourceType())).add(buffVo);
        }

        for (BuffSourceEnum source : GenshinConstantMeta.BUFF_SOURCE_LIST) {
            if (buffVoMap.get(source) != null &&
                    !Objects.requireNonNull(buffVoMap.get(source)).isEmpty()) {
                BuffFolderFragment buffFolderFragment = BuffFolderFragment.newInstance(
                        source, buffVoMap.get(source));
                fragmentTransaction.add(R.id.layout_buff_folders, buffFolderFragment);
                buffFolderFragments.put(source, buffFolderFragment);
            }
        }
        fragmentTransaction.commit();
    }
}