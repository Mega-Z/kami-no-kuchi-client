package com.megaz.knk.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.megaz.knk.R;
import com.megaz.knk.manager.IconResourceManager;

import java.util.List;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        new Thread(this::checkAndUpdateIconResource).start();
    }

    private void checkAndUpdateIconResource() {
        List<String> iconList = IconResourceManager.getIconResourceList(getApplicationContext());
    }
}