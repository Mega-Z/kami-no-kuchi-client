package com.megaz.knk.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.megaz.knk.R;
import com.megaz.knk.activity.HomeActivity;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.dto.CharacterProfileDto;
import com.megaz.knk.dto.PlayerProfileDto;
import com.megaz.knk.manager.ProfileQueryManager;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.CharacterProfileVo;
import com.megaz.knk.vo.PlayerProfileVo;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerProfileFragment extends BaseFragment {

    private PlayerProfileVo playerProfileVo;
    private PlayerProfileDto playerProfileDto;

    private boolean flagUpdating;

    private ObjectAnimator animatorUpdate;
    private TextView textPlayerName, textUid, textSignature;
    private ImageView buttonUpdateProfile, imagePlayerAvatar;
    private GridLayout layoutCharacterList;

    private Handler profileConvertHandler;
    private ProfileQueryManager profileQueryManager;

    private List<CharacterProfileFragment> characterProfileFragmentList;

    public PlayerProfileFragment() {
    }

    public static PlayerProfileFragment newInstance(PlayerProfileDto playerProfileDto) {
        PlayerProfileFragment fragment = new PlayerProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("playerProfileDto", playerProfileDto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerProfileDto = (PlayerProfileDto) getArguments().getSerializable("playerProfileDto");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player_profile, container, false);

    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
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
        profileQueryManager = new ProfileQueryManager(getContext());
        new Thread(this::convertProfile).start();
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        profileConvertHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleProfileConvert(msg);
            }
        };
    }

    private void convertProfile() {
        try{
            PlayerProfileVo playerProfileVo = profileQueryManager.convertPlayerProfileToVo(playerProfileDto);
            Message msg = new Message();
            msg.what = 0;
            msg.obj = playerProfileVo;
            profileConvertHandler.sendMessage(msg);
        } catch (RuntimeException e) {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = e.getMessage();
            profileConvertHandler.sendMessage(msg);
        }
    }

    private void handleProfileConvert(Message msg) {
        switch (msg.what) {
            case 0:
                playerProfileVo = (PlayerProfileVo) msg.obj;
                if(isAdded()) updateProfileView();
                break;
            case 1:
                toast.setText((String) msg.obj);
                toast.show();
                break;
        }
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
            ((HomeActivity) requireActivity()).toUpdateProfile();
        }
    }

    public void toUpdateProfileView(PlayerProfileDto playerProfileDto) {
        flagUpdating = false;
        doAnimationEndUpdating();
        if(playerProfileDto != null) {
            this.playerProfileDto = playerProfileDto;
        }
        new Thread(this::convertProfile).start();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void updateProfileView() {
        if(this.playerProfileVo == null) {
            return;
        }
        if(characterProfileFragmentList == null) {
            characterProfileFragmentList = new ArrayList<>();
        }
        textUid.setText(String.format("%s%s", getString(R.string.text_uid_prefix), playerProfileVo.getUid()));
        textPlayerName.setText(playerProfileVo.getNickname());
        textSignature.setText(playerProfileVo.getSign());
        if(playerProfileVo.getAvatarIcon() != null) {
            imagePlayerAvatar.setImageBitmap(ImageResourceUtils.getIconBitmap(requireContext(), playerProfileVo.getAvatarIcon()));
        }
        // update character list
        layoutCharacterList.removeAllViews();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        for(CharacterProfileFragment characterProfileFragment:characterProfileFragmentList) {
            fragmentTransaction.remove(characterProfileFragment);
        }
        characterProfileFragmentList.clear();
        int idx = 0;
        for(;idx<playerProfileVo.getCharacters().size();idx++){
            CharacterProfileDto characterProfileDto = playerProfileDto.getCharacters().get(idx);
            CharacterProfileVo characterProfileVo = playerProfileVo.getCharacters().get(idx);
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setBackgroundResource(R.drawable.bg_char_profile);
            int newViewId = View.generateViewId();
            linearLayout.setId(newViewId);
            CharacterProfileFragment characterProfileFragment = CharacterProfileFragment.newInstance(
                    new CharacterAttribute(characterProfileDto), characterProfileVo);
            characterProfileFragmentList.add(characterProfileFragment);
            fragmentTransaction.add(newViewId, characterProfileFragment);
            //使用Spec定义子控件的位置和比重
            GridLayout.Spec rowSpec = GridLayout.spec(idx / 2);
            GridLayout.Spec columnSpec = GridLayout.spec(idx % 2, 1f);
            //将Spec传入GridLayout.LayoutParams并设置宽高为0，必须设置宽高，否则视图异常
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
            layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.dp_70);
            layoutParams.width = 0;
            layoutParams.setGravity(Gravity.CENTER_VERTICAL);
            layoutParams.setMargins(getResources().getDimensionPixelOffset(R.dimen.dp_5),
                    getResources().getDimensionPixelOffset(R.dimen.dp_5),
                    getResources().getDimensionPixelOffset(R.dimen.dp_5),
                    getResources().getDimensionPixelOffset(R.dimen.dp_5));
            layoutCharacterList.addView(linearLayout, layoutParams);
        }
        fragmentTransaction.commit();
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