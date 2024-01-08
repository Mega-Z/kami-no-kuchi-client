package com.megaz.knk.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.megaz.knk.R;
import com.megaz.knk.dao.ArtifactCriterionDao;
import com.megaz.knk.dao.BuffDao;
import com.megaz.knk.dao.BuffEffectRelationDao;
import com.megaz.knk.dao.FightEffectComputationDao;
import com.megaz.knk.dao.MetaDataDao;
import com.megaz.knk.dto.MetaDatabaseInfoDto;
import com.megaz.knk.entity.ArtifactCriterion;
import com.megaz.knk.entity.ArtifactDex;
import com.megaz.knk.entity.Buff;
import com.megaz.knk.entity.BuffEffectRelation;
import com.megaz.knk.entity.CharacterDex;
import com.megaz.knk.entity.CostumeDex;
import com.megaz.knk.entity.FightEffectComputation;
import com.megaz.knk.entity.MetaDataEntity;
import com.megaz.knk.entity.ProfilePicture;
import com.megaz.knk.entity.PromoteAttribute;
import com.megaz.knk.entity.RefinementCurve;
import com.megaz.knk.entity.TalentCurve;
import com.megaz.knk.entity.WeaponDex;
import com.megaz.knk.fragment.ElementProgressbarFragment;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.utils.RequestUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LaunchActivity extends BaseActivity {
    private final float ICON_UPDATING_PROGRESS = 0.94f;
    private final int PAGE_SIZE = 50;
    private Handler iconUpdateHandler, progressHandler, metaUpdateHandler;
    private TextView textViewUpdating, textLaunchTitle, textVersion;
    private LinearLayout layoutProgressbarContainer;
    private ElementProgressbarFragment elementProgressbar;

    @Override
    protected void setContent() {
        setContentView(R.layout.activity_launch);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        super.initView();
        textLaunchTitle = findViewById(R.id.text_launch_title);
        textLaunchTitle.setTypeface(typefaceFZFYKS);
        textViewUpdating = findViewById(R.id.text_updating);
        textVersion = findViewById(R.id.text_version);
        try {
            textVersion.setText("Ver." + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        layoutProgressbarContainer = findViewById(R.id.layout_progressbar_container);
        elementProgressbar = ElementProgressbarFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.layout_progressbar_container, elementProgressbar).commit();
        iconUpdateHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleIconUpdateMessage(msg);
            }
        };
        progressHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleProgressMessage(msg);
            }
        };
        metaUpdateHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleMetaUpdateMessage(msg);
            }
        };
        new Thread(this::checkAndUpdateIconResource).start();

    }

    private void finishLaunch() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        try{
            Thread.sleep(500);
            textViewUpdating.setVisibility(View.INVISIBLE);
            // layoutProgressbarContainer.setVisibility(View.INVISIBLE);
            // Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity(intent);
        finish();
    }


    private void handleIconUpdateMessage(Message msg) {
        switch (msg.what) {
            case 0: // success
                new Thread(this::checkAndUpdateMetaData).start();
                break;
            case 1: // fail
                break;
        }
    }

    private void handleMetaUpdateMessage(Message msg) {
        switch (msg.what) {
            case 0: // success
                finishLaunch();
                break;
            case 1: // fail
                break;
        }
    }

    private void handleProgressMessage(Message msg) {
        elementProgressbar.setProgress((float)msg.obj);
    }

    private void checkAndUpdateIconResource() {
        List<String> iconList = ImageResourceUtils.getIconResourceList(getApplicationContext());

        int partitionSize = Math.max(1, iconList.size() / 100);
        AtomicInteger counter = new AtomicInteger(0);
        List<List<String>> shardedIconLists = iconList.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / partitionSize))
                .values()
                .stream()
                .map(ArrayList::new)
                .collect(Collectors.toList());
        for(int id=1;id<=shardedIconLists.size();id++) {
            ImageResourceUtils.updateIconResource(getApplicationContext(), shardedIconLists.get(id-1));
            Message progressMsg = new Message();
            progressMsg.obj = (float)id/shardedIconLists.size()*ICON_UPDATING_PROGRESS;
            progressHandler.sendMessage(progressMsg);
        }
        Message msg = new Message();
        msg.what = 0;
        iconUpdateHandler.handleMessage(msg);
    }
    @SuppressWarnings({"unchecked", "rawuse"})
    private void checkAndUpdateMetaData() {
        MetaDatabaseInfoDto metaDatabaseInfo = RequestUtils.getMetaDatabaseInfo(getApplicationContext());
        String latestVersion = metaDatabaseInfo.getVersion();
        String currentVersion = sharedPreferences.getString("meta_version", "");

        if(currentVersion.equals(latestVersion)) {
            Message progressMsg = new Message();
            progressMsg.obj = 1f;
            progressHandler.sendMessage(progressMsg);

            Message msg = new Message();
            msg.what = 0;
            metaUpdateHandler.handleMessage(msg);
            return;
        }

        Map<String, MetaDataDao> daoMap = new HashMap<>();
        daoMap.put("character_dex", knkDatabase.getCharacterDexDao());
        daoMap.put("weapon_dex", knkDatabase.getWeaponDexDao());
        daoMap.put("artifact_dex", knkDatabase.getArtifactDexDao());
        daoMap.put("costume_dex", knkDatabase.getCostumeDexDao());
        daoMap.put("profile_picture", knkDatabase.getProfilePictureDao());
        daoMap.put("artifact_criterion", knkDatabase.getArtifactCriterionDao());
        daoMap.put("fight_effect_computation", knkDatabase.getFightEffectComputationDao());
        daoMap.put("buff", knkDatabase.getBuffDao());
        daoMap.put("buff_effect_relation", knkDatabase.getBuffEffectRelationDao());
        daoMap.put("talent_curve", knkDatabase.getTalentCurveDao());
        daoMap.put("refinement_curve", knkDatabase.getRefinementCurveDao());
        daoMap.put("promote_attribute", knkDatabase.getPromoteAttributeDao());

        Map<String, Class<? extends MetaDataEntity>> entityClassMap = new HashMap<>();
        entityClassMap.put("character_dex", CharacterDex.class);
        entityClassMap.put("weapon_dex", WeaponDex.class);
        entityClassMap.put("artifact_dex", ArtifactDex.class);
        entityClassMap.put("costume_dex", CostumeDex.class);
        entityClassMap.put("profile_picture", ProfilePicture.class);
        entityClassMap.put("artifact_criterion", ArtifactCriterion.class);
        entityClassMap.put("fight_effect_computation", FightEffectComputation.class);
        entityClassMap.put("buff", Buff.class);
        entityClassMap.put("buff_effect_relation", BuffEffectRelation.class);
        entityClassMap.put("talent_curve", TalentCurve.class);
        entityClassMap.put("refinement_curve", RefinementCurve.class);
        entityClassMap.put("promote_attribute", PromoteAttribute.class);

        for(MetaDataDao<?> metaDataDao:daoMap.values()) {
            metaDataDao.deleteAll();
        }

        int tableNum = metaDatabaseInfo.getTableSize().size();
        int tableCnt = 0;
        for(Map.Entry<String, Long> entry:metaDatabaseInfo.getTableSize().entrySet()) {
            String tableName = entry.getKey();
            Long tableSize = entry.getValue();
            for(int pageId=0;pageId <= tableSize / PAGE_SIZE;pageId++) {
                int offset = pageId * PAGE_SIZE;
                ArrayList<? extends MetaDataEntity> metaDataEntities = new ArrayList<>(RequestUtils.pageQueryMetaData(
                        getApplicationContext(), entityClassMap.get(tableName), tableName, offset, PAGE_SIZE));
                if(!metaDataEntities.isEmpty()){
                    if(!daoMap.containsKey(tableName)){
                        break;
                    }
                    Objects.requireNonNull(daoMap.get(tableName)).batchInsert(
                            metaDataEntities.toArray((MetaDataEntity[])Array.newInstance(Objects.requireNonNull(entityClassMap.get(tableName)),metaDataEntities.size())));
                }
            }
            tableCnt++;
            Message progressMsg = new Message();
            progressMsg.obj = ICON_UPDATING_PROGRESS + (1-ICON_UPDATING_PROGRESS) * tableCnt / tableNum;
            progressHandler.sendMessage(progressMsg);
        }
        editor.putString("meta_version", latestVersion).commit();
        Message msg = new Message();
        msg.what = 0;
        metaUpdateHandler.handleMessage(msg);
    }
}