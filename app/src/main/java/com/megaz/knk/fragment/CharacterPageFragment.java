package com.megaz.knk.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.megaz.knk.R;
import com.megaz.knk.Utils;
import com.megaz.knk.activity.BaseActivity;
import com.megaz.knk.constant.ProfileRequestErrorEnum;
import com.megaz.knk.dto.PlayerProfileDto;
import com.megaz.knk.exception.ProfileRequestException;
import com.megaz.knk.manager.LocalProfileManager;
import com.megaz.knk.manager.ProfileQueryManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CharacterPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterPageFragment extends BaseFragment {

    private boolean flagQuerying = false;
    private boolean flagError = false;
    private boolean flagProfileShowing = false;

    private RelativeLayout layoutMain;
    private LinearLayout layoutUid, layoutPaimon, layoutProfile;
    private Button buttonLoadProfile, buttonUpdateProfile;
    private EditText editTextUid;

    private PaimonWaitingFragment paimonWaiting;
    private PlayerProfileFragment playerProfileFragment;

    private Handler profileLoadHandler; // 获取本地playerProfileDto成功时，更新playerProfileFragment
    private Handler profileQueryHandler, profileUpdateHandler;

    private ProfileQueryManager profileQueryManager;
    private LocalProfileManager localProfileManager;


    public CharacterPageFragment() {
        // Required empty public constructor
    }

    public static CharacterPageFragment newInstance() {
        return new CharacterPageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character_page, container, false);
    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        layoutMain = view.findViewById(R.id.layout_main);
        layoutUid = view.findViewById(R.id.layout_uid);
        buttonLoadProfile = view.findViewById(R.id.btn_load_profile);
        buttonUpdateProfile = view.findViewById(R.id.btn_update_profile);
        editTextUid = view.findViewById(R.id.edtx_uid);
        editTextUid.setText(sharedPreferences.getString("uid", ""));
        layoutPaimon = view.findViewById(R.id.layout_paimon);
        paimonWaiting = PaimonWaitingFragment.newInstance();
        requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.layout_paimon, paimonWaiting).commit();
        layoutProfile = view.findViewById(R.id.layout_profile);
        profileQueryManager = new ProfileQueryManager(getContext());
        localProfileManager = LocalProfileManager.getInstance(getContext());
        if (sharedPreferences.contains("uid")) {
            doAnimationQueryStart();
            flagQuerying = true;
            new Thread(this::loadProfile).start();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setCallback(@NonNull View view) {
        editTextUid.setOnTouchListener(new BaseActivity.EditTextOnTouchListener((BaseActivity) getActivity()));
        buttonLoadProfile.setOnClickListener(new LoadOnClickListener());
        buttonUpdateProfile.setOnClickListener(new UpdateOnClickListener());
        profileLoadHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                handleProfileLoadMessage(msg);
            }
        };
        profileQueryHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                handleProfileQueryMessage(msg);
            }
        };
        profileUpdateHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                handleProfileUpdateMessage(msg);
            }
        };
    }

    private void handleProfileLoadMessage(Message msg) {
        switch (msg.what) {
            case 0: // success
                PlayerProfileDto playerProfileDto = (PlayerProfileDto) msg.obj;
                if (playerProfileDto == null) {
                    toast.setText("无本地数据，开始从服务端获取");
                    toast.show();
                    new Thread(this::queryProfile).start();
                } else {
                    doAnimationQuerySuccess();
                    refreshProfileView(playerProfileDto);
                    flagQuerying = false;
                }
                break;
            case 1:
                toast.setText((String) msg.obj);
                toast.show();
                paimonWaiting.showError();
                flagQuerying = false;
                flagError = true;
                break;
        }
    }

    private void handleProfileQueryMessage(Message msg) {
        switch (msg.what) {
            case 0: // success
                PlayerProfileDto playerProfileDto = (PlayerProfileDto) msg.obj;
                if (playerProfileDto.getCharacterAvailable() != null && !playerProfileDto.getCharacterAvailable()) {
                    toast.setText("角色面板更新失败，请检查角色详情已开启");
                } else {
                    toast.setText("角色面板查询完成");
                }
                toast.show();
                doAnimationQuerySuccess();
                refreshProfileView(playerProfileDto);
                new Thread(() -> updateLocalProfile(playerProfileDto)).start();
                flagQuerying = false;
                break;
            case 1:
                toast.setText((String) msg.obj);
                toast.show();
                paimonWaiting.showError();
                flagQuerying = false;
                flagError = true;
                break;
        }
    }

    private void handleProfileUpdateMessage(Message msg) {
        switch (msg.what) {
            case 0: // success
                PlayerProfileDto playerProfileDto = (PlayerProfileDto) msg.obj;
                if (playerProfileDto.getCharacterAvailable() != null && !playerProfileDto.getCharacterAvailable()) {
                    toast.setText("角色面板更新失败，请检查角色详情已开启");
                } else {
                    toast.setText("角色面板更新完成");
                }
                toast.show();
                doAnimationQuerySuccess();
                refreshProfileView(playerProfileDto);
                new Thread(() -> updateLocalProfile(playerProfileDto)).start();
                flagQuerying = false;
                break;
            case 1:
                toast.setText((String) msg.obj);
                toast.show();
                paimonWaiting.showError();
                flagQuerying = false;
                flagError = true;
                break;
            case 2: // 数据源错误，转为query
                toast.setText("数据源访问错误，尝试获取服务端数据");
                toast.show();
                new Thread(this::queryProfile).start();
                break;
        }
    }

    private class LoadOnClickListener implements View.OnClickListener {
        private long lastClick = 0;
        @Override
        public void onClick(View v) {
            if (System.currentTimeMillis() - lastClick < Long.parseLong(getString(R.string.click_cold_down_ms)) || flagQuerying) {
                return;
            }
            lastClick = System.currentTimeMillis();
            ((BaseActivity) requireActivity()).hideInputMethod();
            if (!isValidUidInput()) {
                toast.setText("uid格式错误");
                toast.show();
                return;
            }
            editor.putString("uid", editTextUid.getText().toString());
            editor.commit();
            doAnimationQueryStart();
            flagQuerying = true;
            flagError = false;
            new Thread(CharacterPageFragment.this::loadProfile).start();
        }
    }

    private class UpdateOnClickListener implements View.OnClickListener {
        private long lastClick = 0;
        @Override
        public void onClick(View v) {
            if (System.currentTimeMillis() - lastClick < Long.parseLong(getString(R.string.click_cold_down_ms)) || flagQuerying) {
                return;
            }
            lastClick = System.currentTimeMillis();
            ((BaseActivity) requireActivity()).hideInputMethod();
            if (!isValidUidInput()) {
                toast.setText("uid格式错误");
                toast.show();
                return;
            }
            editor.putString("uid", editTextUid.getText().toString());
            editor.commit();
            doAnimationQueryStart();
            flagQuerying = true;
            flagError = false;
            new Thread(CharacterPageFragment.this::updateProfile).start();
        }
    }

    public void loadProfile() {
        try {
            Thread.sleep(1000);
            PlayerProfileDto playerProfileDto = localProfileManager.queryLocalProfile(editTextUid.getText().toString());
            Message message = new Message();
            message.what = 0;
            message.obj = playerProfileDto;
            profileLoadHandler.sendMessage(message);
        } catch (ProfileRequestException e) {
            Message message = new Message();
            message.what = 1;
            message.obj = e.getMessage();
            profileLoadHandler.sendMessage(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateProfile() {
        try {
            Thread.sleep(1000);
            PlayerProfileDto playerProfileDto = profileQueryManager.updatePlayerProfileDto(editTextUid.getText().toString());
            Message message = new Message();
            message.what = 0;
            message.obj = playerProfileDto;
            profileUpdateHandler.sendMessage(message);
        } catch (ProfileRequestException e) {
            Message message = new Message();
            if(e.getType() == ProfileRequestErrorEnum.DATASOURCE_ERROR) {
                message.what = 2;
            } else {
                message.what = 1;
            }
            message.obj = e.getMessage();
            profileUpdateHandler.sendMessage(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void queryProfile() {
        try {
            PlayerProfileDto playerProfileDto = profileQueryManager.queryPlayerProfileDto(editTextUid.getText().toString());
            Message message = new Message();
            message.what = 0;
            message.obj = playerProfileDto;
            profileQueryHandler.sendMessage(message);
        } catch (ProfileRequestException e) {
            Message message = new Message();
            message.what = 1;
            message.obj = e.getMessage();
            profileQueryHandler.sendMessage(message);
        }
    }

    private void updateLocalProfile(PlayerProfileDto playerProfileDto) {
        localProfileManager.updateLocalProfile(playerProfileDto);
    }

    private void doAnimationQueryStart() {
        if (flagError) {// 查询出错状态，不需要移动uid输入框
            paimonWaiting.startWaiting();
            return;
        }
        int dyUid = 0;
        if (flagProfileShowing) { // 查询成功状态
            //dy = y1 - y0;
            //y0 = 20 + uid_h;
            //y1 = main_h  / 2 + (300 + uid_h) / 2 - 300 + 10
            dyUid = layoutMain.getHeight() / 2 - layoutUid.getHeight() / 2
                    - getResources().getDimensionPixelOffset(R.dimen.dp_160); // 160 = 20/2 + 300/2
            ObjectAnimator animatorProfileFadeOut = ObjectAnimator.ofFloat(layoutProfile, "alpha", 1f, 0f);
            animatorProfileFadeOut.setDuration(100);
            animatorProfileFadeOut.start();
        } else { // 查询未开始状态
            dyUid = -1 * getResources().getDimensionPixelOffset(R.dimen.dp_300) / 2;
        }
        ObjectAnimator animatorPaimonFadeIn = ObjectAnimator.ofFloat(layoutPaimon, "alpha", 0f, 1f);
        animatorPaimonFadeIn.setDuration(100);
        ObjectAnimator animatorUidMove = ObjectAnimator.ofFloat(layoutUid, "translationY", 0, dyUid * 3f / 4, dyUid);
        animatorUidMove.setDuration(400);
        animatorUidMove.start();
        animatorUidMove.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                layoutUid.setTranslationY(0f);
                animatorPaimonFadeIn.start();
                layoutPaimon.setVisibility(View.VISIBLE);
                paimonWaiting.startWaiting();
                if (flagProfileShowing) {
                    flagProfileShowing = false;
                    layoutProfile.setVisibility(View.GONE);
                }
            }
        });
    }

    private void doAnimationQuerySuccess() {
        ObjectAnimator animatorPaimonFadeOut = ObjectAnimator.ofFloat(layoutPaimon, "alpha", 1f, 0f);
        animatorPaimonFadeOut.setDuration(200);
        animatorPaimonFadeOut.start();
        int dyUid = layoutUid.getHeight() / 2 - layoutMain.getHeight() / 2
                + getResources().getDimensionPixelOffset(R.dimen.dp_160); // 160 = 20/2 + 300/2
        ObjectAnimator animatorUidMove = ObjectAnimator.ofFloat(layoutUid, "translationY", 0, dyUid * 3f / 4, dyUid);
        animatorUidMove.setDuration(400);
        animatorUidMove.start();
        ObjectAnimator animatorProfileFadeIn = ObjectAnimator.ofFloat(layoutProfile, "alpha", 0f, 1f);
        animatorProfileFadeIn.setDuration(100);
        animatorUidMove.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                paimonWaiting.hide();
                layoutProfile.setVisibility(View.VISIBLE);
                animatorProfileFadeIn.start();
                flagProfileShowing = true;
                layoutUid.setTranslationY(0f);
            }
        });
    }

    private void refreshProfileView(PlayerProfileDto playerProfileDto) {
        if (playerProfileFragment == null) {
            playerProfileFragment = PlayerProfileFragment.newInstance(playerProfileDto);
            requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.layout_profile, playerProfileFragment).commit();
        } else {
            playerProfileFragment.toUpdateProfileView(playerProfileDto);
        }
    }

    private boolean isValidUidInput() {
        String uidInput = editTextUid.getText().toString();
        return uidInput.length() == 9 && Utils.isNumeric(uidInput);
    }
}