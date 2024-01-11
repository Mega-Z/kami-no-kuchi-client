package com.megaz.knk.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.common.collect.Lists;
import com.megaz.knk.R;
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
import com.megaz.knk.exception.RequestErrorException;
import com.megaz.knk.fragment.ElementProgressbarFragment;
import com.megaz.knk.fragment.UpdateExceptionFragment;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.utils.MetaDataUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class LaunchActivity extends BaseActivity implements UpdateExceptionFragment.UpdateExceptionFragmentListener {
    private final int ICON_SHARD_SIZE = 10;
    private final float ICON_UPDATING_PROGRESS = 0.94f;
    private final int PAGE_SIZE = 50;
    private Handler resourceUpdateHandler, progressHandler, testHandler;
    private ThreadPoolExecutor threadPoolExecutor;

    private TextView textViewUpdating, textLaunchTitle, textVersion;
    private LinearLayout layoutProgressbarContainer;
    private ElementProgressbarFragment elementProgressbar;
    private UpdateExceptionFragment updateExceptionFragment;


    private Long updateStartTime;

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
    }

    @Override
    protected void setCallback() {
        super.setCallback();
        resourceUpdateHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                tryToUpdateResources();
            }
        };
        progressHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleProgressMessage(msg);
            }
        };
    }

    @Override
    protected void initialize() {
        super.initialize();
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(16);
        updateStartTime = System.currentTimeMillis();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resourceUpdateHandler.sendMessage(new Message());
        }).start();
    }

    private void tryToUpdateResources() {
        elementProgressbar.setProgress(0f);
        updateResources();
    }

    private void updateResources(){
        CompletableFuture<List<String>> completableFutureGetIconList = CompletableFuture.supplyAsync(() -> {
            try {
                return ImageResourceUtils.getIconResourceList(getApplicationContext());
            } catch (RequestErrorException e) {
                e.printStackTrace();
                Message progressMsg = new Message();
                progressMsg.what = 2;
                progressMsg.obj = e.getMessage();
                progressHandler.sendMessage(progressMsg);
                return null;
            }
        }, threadPoolExecutor);

        CompletableFuture<Boolean> completableFutureGetIcons = completableFutureGetIconList.thenApplyAsync(result -> {
            if(result == null) {
                return false;
            }
            List<CompletableFuture<Boolean>> completableFutureGetIconShards = new ArrayList<>();
            for(List<String> iconListShard: Lists.partition(result, ICON_SHARD_SIZE)) {
                completableFutureGetIconShards.add(CompletableFuture.supplyAsync(()-> {
                    try {
                        checkAndUpdateIconResource(iconListShard, result.size());
                        return true;
                    } catch (RequestErrorException e) {
                        e.printStackTrace();
                        Message progressMsg = new Message();
                        progressMsg.what = 2;
                        progressMsg.obj = e.getMessage();
                        progressHandler.sendMessage(progressMsg);
                        return false;
                    }
                }, threadPoolExecutor));
            }
            boolean successFlag = true;
            for(CompletableFuture<Boolean> completableFutureGetIconShard:completableFutureGetIconShards) {
                if(!completableFutureGetIconShard.join()) {
                    successFlag = false;
                    break;
                }
            }
            if(!successFlag) {
                for(CompletableFuture<Boolean> completableFutureGetIconShard:completableFutureGetIconShards) {
                    completableFutureGetIconShard.cancel(true);
                }
            }
            return successFlag;
        }, threadPoolExecutor);

        CompletableFuture<Boolean> completableFutureUpdateMeta = completableFutureGetIcons.thenApplyAsync(result -> {
            if(!result) {
                return false;
            }
            try {
                checkAndUpdateMetaData();
                return true;
            } catch (RuntimeException e) {
                e.printStackTrace();
                Message progressMsg = new Message();
                progressMsg.what = 2;
                progressMsg.obj = e.getMessage();
                progressHandler.sendMessage(progressMsg);
                return false;
            }
        }, threadPoolExecutor);

        completableFutureUpdateMeta.thenAccept(result -> {
            if(result) {
                Message progressMsg = new Message();
                progressMsg.what = 1;
                progressHandler.sendMessage(progressMsg);
            }
        });
    }

    private void handleProgressMessage(Message msg) {
        switch (msg.what) {
            case 0: // add progress
//                Log.d("【线程池状态】", "线程数量："+ threadPoolExecutor.getPoolSize());
//                Log.d("【线程池状态】", "核心线程数量："+ threadPoolExecutor.getCorePoolSize());
//                Log.d("【线程池状态】", "当前活动线程数："+ threadPoolExecutor.getActiveCount());
//                Log.d("【线程池状态】", "已完成的任务数量："+ threadPoolExecutor.getCompletedTaskCount());
//                Log.d("【线程池状态】", "已提交的任务总数："+ threadPoolExecutor.getTaskCount());
//                Log.d("【线程池状态】", "等待任务总数：" + threadPoolExecutor.getQueue().size());
                elementProgressbar.setProgress(elementProgressbar.getProgress() + (float) msg.obj);
                break;
            case 1: // finish
                elementProgressbar.setProgress(1f);
                finishLaunch();
                break;
            case 2: // error
                System.out.println(msg.obj);
                showExceptionDialog();
                break;
        }
    }

    private void finishLaunch() {
        Log.d("完成更新耗时", System.currentTimeMillis()-updateStartTime+"ms");
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        try {
            Thread.sleep(500);
            textViewUpdating.setVisibility(View.INVISIBLE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity(intent);
        finish();
    }

    private void showExceptionDialog() {
        if(updateExceptionFragment == null) {
            updateExceptionFragment = UpdateExceptionFragment.newInstance();
            updateExceptionFragment.setCancelable(false);
        }
        if(!updateExceptionFragment.isAdded())
            updateExceptionFragment.show(getSupportFragmentManager(), "");
    }

    @WorkerThread
    private void checkAndUpdateIconResource(List<String> iconNameList, int iconNum) {
        for(String iconName:iconNameList) {
            ImageResourceUtils.updateIconResource(getApplicationContext(), iconName);
            Message progressMsg = new Message();
            progressMsg.what = 0;
            progressMsg.obj = ICON_UPDATING_PROGRESS / iconNum;
            progressHandler.sendMessage(progressMsg);
        }
    }

    @WorkerThread
    @SuppressWarnings({"unchecked", "rawuse"})
    private void checkAndUpdateMetaData() {
        MetaDatabaseInfoDto metaDatabaseInfo = MetaDataUtils.getMetaDatabaseInfo(getApplicationContext());
        String latestVersion = metaDatabaseInfo.getVersion();
        String currentVersion = sharedPreferences.getString("meta_version", "");

        if (currentVersion.equals(latestVersion)) {
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

        for (MetaDataDao<?> metaDataDao : daoMap.values()) {
            metaDataDao.deleteAll();
        }

        int tableNum = metaDatabaseInfo.getTableSize().size();
        for (Map.Entry<String, Long> entry : metaDatabaseInfo.getTableSize().entrySet()) {
            String tableName = entry.getKey();
            Long tableSize = entry.getValue();
            for (int pageId = 0; pageId <= tableSize / PAGE_SIZE; pageId++) {
                int offset = pageId * PAGE_SIZE;
                ArrayList<? extends MetaDataEntity> metaDataEntities = new ArrayList<>(MetaDataUtils.pageQueryMetaData(
                        getApplicationContext(), entityClassMap.get(tableName), tableName, offset, PAGE_SIZE));
                if (!metaDataEntities.isEmpty()) {
                    if (!daoMap.containsKey(tableName)) {
                        break;
                    }
                    Objects.requireNonNull(daoMap.get(tableName)).batchInsert(
                            metaDataEntities.toArray((MetaDataEntity[]) Array.newInstance(Objects.requireNonNull(entityClassMap.get(tableName)), metaDataEntities.size())));
                }
            }
            Message progressMsg = new Message();
            progressMsg.what = 0;
            progressMsg.obj = (1 - ICON_UPDATING_PROGRESS) / tableNum;
            progressHandler.sendMessage(progressMsg);
        }
        editor.putString("meta_version", latestVersion).commit();
    }

    @Override
    public void onRetryClicked() {
        tryToUpdateResources();
    }

    @Override
    public void onExitClicked() {
        finish();
    }
}