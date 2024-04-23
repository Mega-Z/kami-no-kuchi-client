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
import com.megaz.knk.dto.CharacterProfileDto;
import com.megaz.knk.dto.PlayerProfileDto;
import com.megaz.knk.manager.ProfileViewManager;
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
    private TextView textPlayerName, textUid, textSignature;
    private ImageView imagePlayerAvatar;
    private LinearLayout layoutCharacterList;

    private Handler profileConvertHandler;
    private ProfileViewManager profileViewManager;

    private List<CharacterProfileFolderFragment> characterProfileFolderFragmentList;

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
        layoutCharacterList = view.findViewById(R.id.layout_character_list);
        profileViewManager = ProfileViewManager.getInstance(getContext());
        characterProfileFolderFragmentList = new ArrayList<>();
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
            PlayerProfileVo playerProfileVo = profileViewManager.convertPlayerProfileToVo(playerProfileDto);
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

    public void toUpdateProfileView(PlayerProfileDto playerProfileDto) {
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
        textUid.setText(String.format("%s%s", getString(R.string.text_uid_prefix), playerProfileVo.getUid()));
        textPlayerName.setText(playerProfileVo.getNickname());
        textSignature.setText(playerProfileVo.getSign());
        if(playerProfileVo.getAvatarIcon() != null) {
            imagePlayerAvatar.setImageBitmap(ImageResourceUtils.getIconBitmap(requireContext(), playerProfileVo.getAvatarIcon()));
        }
        // update character list
        layoutCharacterList.removeAllViews();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        for(CharacterProfileFolderFragment characterProfileFolderFragment:characterProfileFolderFragmentList) {
            fragmentTransaction.remove(characterProfileFolderFragment);
        }
        characterProfileFolderFragmentList.clear();

        for(int i=0;i<playerProfileVo.getCharacters().size();i++) {
            CharacterProfileDto characterProfileDto = playerProfileDto.getCharacters().get(i);
            CharacterProfileVo characterProfileVo = playerProfileVo.getCharacters().get(i);
            CharacterProfileFolderFragment characterProfileFolderFragment =
                    CharacterProfileFolderFragment.newInstance(characterProfileDto, characterProfileVo);
            fragmentTransaction.add(layoutCharacterList.getId(), characterProfileFolderFragment);
        }
        fragmentTransaction.commit();
    }
}