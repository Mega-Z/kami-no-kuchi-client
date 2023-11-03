package com.megaz.knk.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.megaz.knk.R;
import com.megaz.knk.activity.ProfileQueryActivity;
import com.megaz.knk.manager.ImageResourceManager;
import com.megaz.knk.vo.CharacterProfileVo;
import com.megaz.knk.vo.PlayerProfileVo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerProfileFragment extends Fragment {

    private static final String ARG_PLAYER_PROFILE_VO = "playerProfileVo";

    private PlayerProfileVo playerProfileVo;
    private Map<Integer, CharacterProfileVo> viewIdCharacterProfileVoMap;

    private boolean flagUpdating;

    private ObjectAnimator animatorUpdate;
    private TextView textPlayerName, textUid, textSignature;
    private ImageView buttonUpdateProfile, imagePlayerAvatar;
    private GridLayout layoutCharacterList;

    public PlayerProfileFragment() {
    }

    public static PlayerProfileFragment newInstance(PlayerProfileVo playerProfileVo) {
        PlayerProfileFragment fragment = new PlayerProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLAYER_PROFILE_VO, playerProfileVo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerProfileVo = (PlayerProfileVo) getArguments().getSerializable(ARG_PLAYER_PROFILE_VO);
            viewIdCharacterProfileVoMap = new HashMap<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textPlayerName = view.findViewById(R.id.text_player_name);
        textUid = view.findViewById(R.id.text_uid);
        textSignature = view.findViewById(R.id.text_sign);
        imagePlayerAvatar = view.findViewById(R.id.img_player_avatar);
        buttonUpdateProfile = view.findViewById(R.id.btn_update);
        buttonUpdateProfile.setOnClickListener(new UpdateOnClickListener());
        layoutCharacterList = view.findViewById(R.id.layout_character_list);
        animatorUpdate = ObjectAnimator.ofFloat(buttonUpdateProfile, "rotation", 360f);
        animatorUpdate.setDuration(1000);
        animatorUpdate.setRepeatCount(ValueAnimator.INFINITE);
        updateProfileView();
    }

    private class UpdateOnClickListener implements View.OnClickListener {


        private long lastClick = 0;

        @Override
        public void onClick(View v) {
            if(System.currentTimeMillis() - lastClick < Long.parseLong(getString(R.string.click_cold_down_ms)) || flagUpdating) {
                return;
            }
            lastClick = System.currentTimeMillis();
            flagUpdating = true;
            doAnimationStartUpdating();
            ((ProfileQueryActivity) Objects.requireNonNull(getActivity())).toUpdateProfile();
        }
    }

    public void toUpdateProfileView(PlayerProfileVo playerProfileVo) {
        flagUpdating = false;
        doAnimationEndUpdating();
        if(playerProfileVo != null) {
            this.playerProfileVo = playerProfileVo;
        }
        viewIdCharacterProfileVoMap = new HashMap<>();
        updateProfileView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void updateProfileView() {
        if(this.playerProfileVo == null) {
            return;
        }
        textUid.setText(String.format("%s%s", getString(R.string.text_uid_prefix), playerProfileVo.getUid()));
        textPlayerName.setText(playerProfileVo.getNickname());
        textSignature.setText(playerProfileVo.getSign());
        if(playerProfileVo.getAvatarIcon() != null) {
            imagePlayerAvatar.setImageBitmap(ImageResourceManager.getIconBitmap(Objects.requireNonNull(getContext()), playerProfileVo.getAvatarIcon()));
        }
        // update character list
        layoutCharacterList.removeAllViews();
        int idx = 0;
        for(;idx<playerProfileVo.getCharacters().size();idx++){
            CharacterProfileVo characterProfileVo = playerProfileVo.getCharacters().get(idx);
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setBackgroundResource(R.drawable.bg_char_profile);
            linearLayout.setOnClickListener(new CharacterProfileOnClickListener());
            linearLayout.setOnTouchListener(new CharacterProfileOnTouchListener());
            int newViewId = View.generateViewId();
            linearLayout.setId(newViewId);
            viewIdCharacterProfileVoMap.put(newViewId, characterProfileVo);
            CharacterProfileFragment characterProfileFragment = CharacterProfileFragment.newInstance(characterProfileVo);
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                    .add(newViewId, characterProfileFragment).commitAllowingStateLoss();
            //使用Spec定义子控件的位置和比重
            GridLayout.Spec rowSpec = GridLayout.spec(idx / 2);
            GridLayout.Spec columnSpec = GridLayout.spec(idx % 2, 1f);
            //将Spec传入GridLayout.LayoutParams并设置宽高为0，必须设置宽高，否则视图异常
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
            layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.dp_70);
            //layoutParams.height = 0;
            layoutParams.width = 0;
            layoutParams.setGravity(Gravity.CENTER_VERTICAL);
            layoutParams.setMargins(getResources().getDimensionPixelOffset(R.dimen.dp_5),
                    getResources().getDimensionPixelOffset(R.dimen.dp_5),
                    getResources().getDimensionPixelOffset(R.dimen.dp_5),
                    getResources().getDimensionPixelOffset(R.dimen.dp_5));
            layoutCharacterList.addView(linearLayout, layoutParams);
        }
        // 如果角色总数是奇数，再塞一个linearlayout
        if(playerProfileVo.getCharacters().size() % 2 != 0){
            LinearLayout linearLayout = new LinearLayout(getContext());
            GridLayout.Spec rowSpec = GridLayout.spec(idx / 2);
            GridLayout.Spec columnSpec = GridLayout.spec(idx % 2, 1f);
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
            layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.dp_70);;
            layoutParams.width = 0;
            layoutCharacterList.addView(linearLayout, layoutParams);
        }
    }

    private class CharacterProfileOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            CharacterProfileVo characterProfileVo = viewIdCharacterProfileVoMap.get(v.getId());
            assert characterProfileVo != null;
            characterProfileVo.setNewData(false);
            ((ProfileQueryActivity) Objects.requireNonNull(getActivity())).showCharacterDetail(characterProfileVo);
        }
    }

    private static class CharacterProfileOnTouchListener implements View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // v.performClick();
            if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
                v.setBackgroundResource(R.drawable.bg_char_profile_press);
            }else{
                v.setBackgroundResource(R.drawable.bg_char_profile);
            }
            return false;
        }
    }


    private void doAnimationStartUpdating() {
        buttonUpdateProfile.setImageResource(R.drawable.updating);
        animatorUpdate.start();
    }

    private void doAnimationEndUpdating() {
        animatorUpdate.cancel();
        buttonUpdateProfile.setImageResource(R.drawable.update_profile);
        buttonUpdateProfile.setRotation(0f);
    }
}