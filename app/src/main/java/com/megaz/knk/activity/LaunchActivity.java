package com.megaz.knk.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.megaz.knk.R;
import com.megaz.knk.manager.ImageResourceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LaunchActivity extends AppCompatActivity {

    private Handler iconUpdateHandler;
    private TextView textViewIconUpdating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        textViewIconUpdating = findViewById(R.id.text_icon_updating);

        iconUpdateHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleIconUpdateMessage(msg);
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
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                break;
            case 1: // fail
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkAndUpdateIconResource() {
        List<String> iconList = ImageResourceManager.getIconResourceList(getApplicationContext());

        int partitionSize = Math.max(1, iconList.size() / 100);
        AtomicInteger counter = new AtomicInteger(0);
        List<List<String>> shardedIconLists = iconList.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / partitionSize))
                .values()
                .stream()
                .map(ArrayList::new)
                .collect(Collectors.toList());
        for(List<String> shardedIconList:shardedIconLists) {
            ImageResourceManager.updateIconResource(getApplicationContext(), shardedIconList);
        }
        Message msg = new Message();
        msg.what = 0;
        iconUpdateHandler.handleMessage(msg);
    }
}