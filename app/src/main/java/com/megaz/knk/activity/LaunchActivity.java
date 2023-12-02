package com.megaz.knk.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.megaz.knk.R;
import com.megaz.knk.fragment.ElementProgressbarFragment;
import com.megaz.knk.utils.ImageResourceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LaunchActivity extends BaseActivity {

    private Handler iconUpdateHandler, progressHandler;
    private TextView textViewIconUpdating, textLaunchTitle;
    private LinearLayout layoutProgressbarContainer;
    private ElementProgressbarFragment elementProgressbar;

    @Override
    protected void setContent() {
        super.setContent();
        setContentView(R.layout.activity_launch);
    }

    @Override
    protected void initView() {
        super.initView();
        textLaunchTitle = findViewById(R.id.text_launch_title);
        textLaunchTitle.setTypeface(typefaceFZFYKS);
        textViewIconUpdating = findViewById(R.id.text_icon_updating);
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
        new Thread(this::checkAndUpdateIconResource).start();

    }


    private void handleIconUpdateMessage(Message msg) {
        switch (msg.what) {
            case 0: // success
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                try{
                    Thread.sleep(500);
                    textViewIconUpdating.setVisibility(View.INVISIBLE);
                    layoutProgressbarContainer.setVisibility(View.INVISIBLE);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                finish();
                break;
            case 1: // fail
                break;
        }
    }

    private void handleProgressMessage(Message msg) {
        elementProgressbar.setProgress((float)msg.obj);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
            progressMsg.obj = (float)id/shardedIconLists.size();
            progressHandler.sendMessage(progressMsg);
        }
        Message msg = new Message();
        msg.what = 0;
        iconUpdateHandler.handleMessage(msg);
    }
}