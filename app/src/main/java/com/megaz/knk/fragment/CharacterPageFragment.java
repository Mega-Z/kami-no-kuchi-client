package com.megaz.knk.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
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
import com.megaz.knk.exception.RequestErrorException;
import com.megaz.knk.utils.ProfileRequestUtils;
import com.megaz.knk.vo.PlayerProfileVo;

import java.util.Objects;

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
    private Button buttonQueryProfile;
    private EditText editTextUid;

    private PaimonWaitingFragment paimonWaiting;
    private PlayerProfileFragment playerProfileFragment;

    private Handler queryProfileHandler, updateProfileHandler;


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
    protected void initView(@NonNull View view){
        super.initView(view);
        layoutMain = view.findViewById(R.id.layout_main);
        layoutUid = view.findViewById(R.id.layout_uid);
        buttonQueryProfile = view.findViewById(R.id.btn_query_profile);
        editTextUid = view.findViewById(R.id.edtx_uid);
        editTextUid.setText(sharedPreferences.getString("uid", ""));
        layoutPaimon = view.findViewById(R.id.layout_paimon);
        paimonWaiting = PaimonWaitingFragment.newInstance();
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.layout_paimon, paimonWaiting).commit();
        layoutProfile = view.findViewById(R.id.layout_profile);
    }

    @Override
    protected void setCallback(@NonNull View view) {
        editTextUid.setOnTouchListener(new BaseActivity.EditTextOnTouchListener((BaseActivity) getActivity()));
        buttonQueryProfile.setOnClickListener(new QueryOnClickListener());
        queryProfileHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleQueryProfileMessage(msg);
            }
        };
        updateProfileHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleUpdateProfileMessage(msg);
            }
        };
    }

    private void handleQueryProfileMessage(Message msg) {
        switch (msg.what) {
            case 0: // success
                PlayerProfileVo playerProfileVo = (PlayerProfileVo) msg.obj;
                if(playerProfileVo.getCharacterAvailable() != null && !playerProfileVo.getCharacterAvailable()){
                    toast.setText("角色面板更新失败，请检查角色详情已开启");
                    toast.show();
                }
                doAnimationQuerySuccess();
                refreshProfileView(playerProfileVo);
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

    private void handleUpdateProfileMessage(Message msg) {
        switch (msg.what) {
            case 0: // success
                PlayerProfileVo playerProfileVo = (PlayerProfileVo) msg.obj;
                if(playerProfileVo.getCharacterAvailable() != null && !playerProfileVo.getCharacterAvailable()){
                    toast.setText("角色面板更新失败，请检查角色详情已开启");
                }else{
                    toast.setText("角色面板更新完成");
                }
                toast.show();
                playerProfileFragment.toUpdateProfileView(playerProfileVo);
                break;
            case 1:
                toast.setText((String) msg.obj);
                toast.show();
                playerProfileFragment.toUpdateProfileView(null);
                break;
        }
    }

    private class QueryOnClickListener implements View.OnClickListener{
        private long lastClick = 0;

        @Override
        public void onClick(View v) {
            if(System.currentTimeMillis() - lastClick < Long.parseLong(getString(R.string.click_cold_down_ms)) || flagQuerying) {
                return;
            }
            lastClick = System.currentTimeMillis();
            ((BaseActivity) Objects.requireNonNull(getActivity())).hideInputMethod();
            if(!isValidUidInput()) {
                toast.setText("uid格式错误");
                toast.show();
                return;
            }
            editor.putString("uid", editTextUid.getText().toString());
            editor.commit();
            doAnimationQueryStart();
            flagQuerying = true;
            flagError = false;
            new Thread(CharacterPageFragment.this::queryProfile).start();
        }
    }

    public void toUpdateProfile() {
        new Thread(this::updateProfile).start();
    }

    private void updateProfile() {
        try {
            PlayerProfileVo playerProfileVo = ProfileRequestUtils.updateProfile(
                    Objects.requireNonNull(getActivity()).getApplicationContext(), editTextUid.getText().toString());
            Message msg = new Message();
            msg.what = 0;
            msg.obj = playerProfileVo;
            updateProfileHandler.sendMessage(msg);
        } catch (RequestErrorException e) {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = e.getMessage();
            updateProfileHandler.sendMessage(msg);
        }
    }

    private void queryProfile() {
        try {
            Thread.sleep(500);
            PlayerProfileVo playerProfileVo = ProfileRequestUtils.queryProfile(
                    Objects.requireNonNull(getActivity()).getApplicationContext(), editTextUid.getText().toString());
            Message msg = new Message();
            msg.what = 0;
            msg.obj = playerProfileVo;
            queryProfileHandler.sendMessage(msg);
        } catch (RequestErrorException e) {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = e.getMessage();
            queryProfileHandler.sendMessage(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doAnimationQueryStart() {
        if(flagError) {// 查询出错状态，不需要移动uid输入框
            paimonWaiting.startWaiting();
            return;
        }
        int dyUid = 0;
        if(flagProfileShowing){ // 查询成功状态
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
        ObjectAnimator animatorPaimonFadeIn = ObjectAnimator.ofFloat(layoutPaimon,"alpha", 0f, 1f);
        animatorPaimonFadeIn.setDuration(100);
        ObjectAnimator animatorUidMove = ObjectAnimator.ofFloat(layoutUid, "translationY",0, dyUid*3f/4,dyUid);
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
                if(flagProfileShowing) {
                    flagProfileShowing = false;
                    layoutProfile.setVisibility(View.GONE);
                }
            }
        });
    }

    private void doAnimationQuerySuccess() {
        ObjectAnimator animatorPaimonFadeOut = ObjectAnimator.ofFloat(layoutPaimon,"alpha", 1f, 0f);
        animatorPaimonFadeOut.setDuration(200);
        animatorPaimonFadeOut.start();
        int dyUid = layoutUid.getHeight() / 2 - layoutMain.getHeight() / 2
                + getResources().getDimensionPixelOffset(R.dimen.dp_160); // 160 = 20/2 + 300/2
        ObjectAnimator animatorUidMove = ObjectAnimator.ofFloat(layoutUid, "translationY",0, dyUid*3f/4,dyUid);
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

    private void refreshProfileView(PlayerProfileVo playerProfileVo) {
        if(playerProfileFragment == null) {
            playerProfileFragment = PlayerProfileFragment.newInstance(playerProfileVo);
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.layout_profile, playerProfileFragment).commit();
        } else {
            playerProfileFragment.toUpdateProfileView(playerProfileVo);
        }
    }

    private boolean isValidUidInput() {
        String uidInput = editTextUid.getText().toString();
        return uidInput.length() == 9 && Utils.isNumeric(uidInput);
    }


}