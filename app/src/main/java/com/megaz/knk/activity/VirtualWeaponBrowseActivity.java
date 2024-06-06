package com.megaz.knk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.megaz.knk.R;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.WeaponTypeEnum;
import com.megaz.knk.dao.WeaponDexDao;
import com.megaz.knk.dao.WeaponDexDao;
import com.megaz.knk.entity.WeaponDex;
import com.megaz.knk.fragment.VirtualWeaponSelectionFragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VirtualWeaponBrowseActivity extends BaseActivity {
    private WeaponTypeEnum type;
    private List<WeaponDex> weaponDexList;

    private GridLayout layoutWeaponList;

    private Handler dexQueryHandler, dexViewAppendHandler;
    private final int DELAY_MS = 50;

    @Override
    protected void setContent() {
        setContentView(R.layout.activity_virtual_weapon_browse);
    }

    @Override
    protected void initView() {
        super.initView();
        type = (WeaponTypeEnum) getIntent().getExtras().get("type");
        layoutWeaponList = findViewById(R.id.layout_weapon_list);
    }

    @Override
    protected void setCallback() {
        super.setCallback();
        dexQueryHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                handlerDexQueryMessage(msg);
            }
        };
        dexViewAppendHandler = new Handler();
    }

    @Override
    protected void initialize() {
        super.initialize();
        weaponDexList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(this::queryWeaponDex).start();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
        super.onBackPressed();
    }

    @WorkerThread
    private void queryWeaponDex() {
        try {
            WeaponDexDao weaponDexDao = knkDatabase.getWeaponDexDao();
            Message msg = new Message();
            msg.what = 0;
            msg.obj = weaponDexDao.selectByWeaponType(type);
            dexQueryHandler.sendMessage(msg);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 1;
            msg.obj = e.getMessage();
            dexQueryHandler.sendMessage(msg);
        }
    }

    private void handlerDexQueryMessage(Message msg) {
        switch (msg.what) {
            case 0:
                weaponDexList = (List<WeaponDex>) msg.obj;
                weaponDexList.sort(Comparator.comparing(WeaponDex::getWeaponId));
                dexViewAppendHandler.postDelayed(this::appendDex, DELAY_MS);
                break;
            case 1:
                toast.setText((String) msg.obj);
                toast.show();
                break;
        }
    }

    private void appendDex() {
        int currentLen = layoutWeaponList.getChildCount();
        if (currentLen >= weaponDexList.size()) {
            return;
        }
        VirtualWeaponSelectionFragment virtualWeaponSelectionFragment =
                VirtualWeaponSelectionFragment.newInstance(weaponDexList.get(currentLen));
        RelativeLayout layoutContainer = new RelativeLayout(this);
        // layoutContainer.setBackgroundColor(getColor(R.color.black));
        int new_id = View.generateViewId();
        layoutContainer.setId(new_id);
        getSupportFragmentManager().beginTransaction().add(new_id, virtualWeaponSelectionFragment).commitAllowingStateLoss();
        //使用Spec定义子控件的位置和比重
        GridLayout.Spec rowSpec = GridLayout.spec(currentLen / 2);
        GridLayout.Spec columnSpec = GridLayout.spec(currentLen % 2, 1f);
        //将Spec传入GridLayout.LayoutParams并设置宽高为0，必须设置宽高，否则视图异常
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
        layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.dp_80);
        //layoutParams.height = 0;
        layoutParams.width = 0;
        layoutParams.setGravity(Gravity.CENTER_VERTICAL);
        layoutParams.setMargins(getResources().getDimensionPixelOffset(R.dimen.dp_5),
                getResources().getDimensionPixelOffset(R.dimen.dp_5),
                getResources().getDimensionPixelOffset(R.dimen.dp_5),
                getResources().getDimensionPixelOffset(R.dimen.dp_5));
        layoutWeaponList.addView(layoutContainer, layoutParams);
        dexViewAppendHandler.postDelayed(this::appendDex, DELAY_MS);
    }
}