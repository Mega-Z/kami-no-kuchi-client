package com.megaz.knk.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Looper;
import android.os.Message;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.megaz.knk.R;
import com.megaz.knk.fragment.CharacterPageFragment;
import com.megaz.knk.fragment.ConfigPageFragment;
import com.megaz.knk.fragment.WishPageFragment;

import java.util.Arrays;
import java.util.List;

public class HomeActivity extends BaseActivity {
    private boolean flagQuitWaiting;

    private ViewPager viewPager;
    private BottomNavigationView navigationView;
    private ImageView imagePageCharacter, imagePageWish, imagePageConfig;

    private CharacterPageFragment characterPageFragment;
    private WishPageFragment wishPageFragment;
    private ConfigPageFragment configPageFragment;

    private Handler quitHandler;


    @Override
    protected void setContent() {
        super.setContent();
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void initView() {
        super.initView();
        viewPager = findViewById(R.id.viewpager);
        viewPager.addOnPageChangeListener(new HomePagerOnPageChangeListener());
        imagePageCharacter = findViewById(R.id.img_page_character);
        imagePageCharacter.setOnClickListener(new PageSelectOnClickListener(0));
        imagePageWish = findViewById(R.id.img_page_wish);
        imagePageWish.setOnClickListener(new PageSelectOnClickListener(1));
        imagePageConfig = findViewById(R.id.img_page_config);
        imagePageConfig.setOnClickListener(new PageSelectOnClickListener(2));
        characterPageFragment = CharacterPageFragment.newInstance();
        wishPageFragment = WishPageFragment.newInstance();
        configPageFragment = ConfigPageFragment.newInstance();

        List<Fragment> fragmentList = Arrays.asList(
                characterPageFragment,
                wishPageFragment,
                configPageFragment);
        viewPager.setAdapter(new KnkFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.setCurrentItem(0);

        quitHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleQuitMessage(msg);
            }
        };

    }
    private class PageSelectOnClickListener implements View.OnClickListener {
        private int item;

        PageSelectOnClickListener(int item) {
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(item, true);
        }
    }

    private class HomePagerOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(position == 0) {
                imagePageCharacter.setImageResource(R.drawable.page_character_highlight);
            } else {
                imagePageCharacter.setImageResource(R.drawable.page_character_normal);
            }
            if(position == 1) {
                imagePageWish.setImageResource(R.drawable.page_wish_highlight);
            } else {
                imagePageWish.setImageResource(R.drawable.page_wish_normal);
            }
            if(position == 2) {
                imagePageConfig.setImageResource(R.drawable.page_config_highlight);
            } else {
                imagePageConfig.setImageResource(R.drawable.page_config_normal);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    public void onBackPressed() {
        if(flagQuitWaiting) {
            finish();
        } else {
            flagQuitWaiting = true;
            toast.setText("再按一次关闭神之嘴");
            toast.show();
            new Thread(this::startQuitWaiting).start();
        }
    }

    private void handleQuitMessage(Message msg) {
        flagQuitWaiting = false;
    }

    private void startQuitWaiting() {
        try {
            Thread.sleep(1000);
            quitHandler.sendMessage(new Message());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void toUpdateProfile() {
        characterPageFragment.toUpdateProfile();
    }
}