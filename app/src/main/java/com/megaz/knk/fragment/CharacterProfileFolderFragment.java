package com.megaz.knk.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.megaz.knk.R;
import com.megaz.knk.activity.CharacterDetailActivity;
import com.megaz.knk.dto.CharacterProfileDto;
import com.megaz.knk.exception.ProfileQueryException;
import com.megaz.knk.manager.LocalProfileManager;
import com.megaz.knk.manager.ProfileViewManager;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.utils.ProfileConvertUtils;
import com.megaz.knk.vo.CharacterProfileVo;

import java.util.ArrayList;
import java.util.List;

public class CharacterProfileFolderFragment extends BaseFragment {

    private CharacterProfileDto characterProfileDto;
    private CharacterProfileVo characterProfileVo;
    private String uid, characterId;

    private TextView textCharacterName, textCharacterLevel, textCharacterCons;
    private ImageView imageCharacterCard, imageIsNew, imageFolderAngle, imageLoading;
    private ObjectAnimator animatorLoading;
    private LinearLayout layoutHistoryList, layoutContainer;
    private int containerId;
    private Handler historyLoadHandler, historyConvertHandler;

    private List<ProfileHistoryForShowDetail> profileHistoryFragmentList;
    private List<CharacterProfileDto> historyProfileDtoList;
    private List<CharacterProfileVo> historyProfileVoList;
    private boolean flagLoading, flagShowHistories;

    private LocalProfileManager localProfileManager;
    private ProfileViewManager profileViewManager;

    public CharacterProfileFolderFragment() {
        // Required empty public constructor
    }

    public static CharacterProfileFolderFragment newInstance(CharacterProfileDto characterProfileDto, CharacterProfileVo characterProfileVo) {
        CharacterProfileFolderFragment fragment = new CharacterProfileFolderFragment();
        Bundle args = new Bundle();
        args.putSerializable("characterProfileDto", characterProfileDto);
        args.putSerializable("characterProfileVo", characterProfileVo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            characterProfileVo = (CharacterProfileVo) getArguments().getSerializable("characterProfileVo");
            characterProfileDto = (CharacterProfileDto) getArguments().getSerializable("characterProfileDto");
            uid = characterProfileDto.getUid();
            characterId = characterProfileDto.getCharacterId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character_profile_folder, container, false);
    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        textCharacterName = view.findViewById(R.id.text_character_name);
        textCharacterCons = view.findViewById(R.id.text_character_cons);
        textCharacterLevel = view.findViewById(R.id.text_character_level);
        imageCharacterCard = view.findViewById(R.id.img_character_card);
        imageIsNew = view.findViewById(R.id.img_is_new);
        imageFolderAngle = view.findViewById(R.id.img_folder_angle);
        imageLoading = view.findViewById(R.id.img_loading);
        layoutHistoryList = view.findViewById(R.id.layout_history_list);

        animatorLoading = ObjectAnimator.ofFloat(imageLoading, "rotation", -360f);
        animatorLoading.setDuration(1000);
        animatorLoading.setRepeatCount(ValueAnimator.INFINITE);
        animatorLoading.start();

        profileHistoryFragmentList = new ArrayList<>();
        historyProfileDtoList = new ArrayList<>();
        historyProfileVoList = new ArrayList<>();

        localProfileManager = LocalProfileManager.getInstance(getContext());
        profileViewManager = ProfileViewManager.getInstance(getContext());

        resetFolder(characterProfileDto, characterProfileVo);
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        view.setOnTouchListener(new CharacterProfileOnTouchListener());
        view.setOnClickListener(new CharacterProfileOnClickListener());
        historyLoadHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                handleHistoryLoad(msg);
            }
        };
        historyConvertHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                handleHistoryConvert(msg);
            }
        };
    }

    @SuppressLint("SetTextI18n")
    public void resetFolder(CharacterProfileDto characterProfileDto, CharacterProfileVo characterProfileVo) {
        this.characterProfileDto = characterProfileDto;
        this.characterProfileVo = characterProfileVo;

        textCharacterName.setText(characterProfileVo.getCharacterName());
        textCharacterLevel.setText(getString(R.string.text_level_prefix) + characterProfileVo.getLevel());
        textCharacterCons.setText(characterProfileVo.getConstellation() + getString(R.string.text_constellation_suffix));
        textCharacterCons.setBackgroundColor(requireContext()
                .getColor(DynamicStyleUtils.getConstellationColor(characterProfileVo.getConstellation())));

        if (characterProfileVo.getNewData()) {
            imageIsNew.setVisibility(View.VISIBLE);
        } else {
            imageIsNew.setVisibility(View.INVISIBLE);
        }
        imageCharacterCard.setImageBitmap(ImageResourceUtils.getIconBitmap(requireContext(), characterProfileVo.getCardIcon()));

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        for (ProfileHistoryFragment profileHistoryFragment : profileHistoryFragmentList) {
            fragmentTransaction.remove(profileHistoryFragment);
        }
        fragmentTransaction.commit();
        profileHistoryFragmentList.clear();
        historyProfileDtoList.clear();
        historyProfileVoList.clear();
        flagLoading = false;
        flagShowHistories = false;

        foldHistories();
    }


    private class CharacterProfileOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            imageIsNew.setVisibility(View.INVISIBLE);
            if (flagShowHistories) {
                foldHistories();
            } else {
                unfoldHistories();
            }
        }
    }

    private class CharacterProfileOnTouchListener implements View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // v.performClick();
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                v.setBackgroundResource(R.drawable.bg_character_profile_press);
            } else {
                v.setBackgroundResource(R.drawable.bg_character_profile);
            }
            return false;
        }
    }

    private void foldHistories() {
        imageLoading.setVisibility(View.GONE);
        layoutHistoryList.setVisibility(View.GONE);
        imageFolderAngle.setRotation(0f);
        flagShowHistories = false;
    }

    private void unfoldHistories() {
        imageFolderAngle.setRotation(90f);
        if (profileHistoryFragmentList.isEmpty() && !flagLoading) {
            imageLoading.setVisibility(View.VISIBLE);
            flagLoading = true;
            new Thread(this::toLoadProfileHistory).start();
        } else if (flagLoading) {
            imageLoading.setVisibility(View.VISIBLE);
        } else {
            layoutHistoryList.setVisibility(View.VISIBLE);
        }
        flagShowHistories = true;
    }

    private void toLoadProfileHistory() {
        try {
            List<CharacterProfileDto> characterProfiles = loadProfileHistory();
            Message msg = new Message();
            msg.what = 0;
            msg.obj = characterProfiles;
            historyLoadHandler.sendMessage(msg);
        } catch (RuntimeException e) {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = e.getMessage();
            historyLoadHandler.sendMessage(msg);
        }
    }

    private List<CharacterProfileDto> loadProfileHistory() {
        List<CharacterProfileDto> characterProfiles = localProfileManager.getCharacterProfilesByCharacterIdAndUid(characterId, uid);
        if (characterProfiles.isEmpty() || !ProfileConvertUtils.isSameCharacterProfile(characterProfileDto, characterProfiles.get(0))) {
            throw new ProfileQueryException("角色最新面板不一致");
        }
        return characterProfiles;
    }

    private void handleHistoryLoad(Message msg) {
        switch (msg.what) {
            case 0:
                if (flagLoading && isAdded()) {
                    historyProfileDtoList = (List<CharacterProfileDto>) msg.obj;
                    new Thread(this::convertHistoryProfiles).start();
                }
                break;
            case 1:
                toast.setText((String) msg.obj);
                toast.show();
                flagLoading = false;
                foldHistories();
                break;
        }
    }

    private void convertHistoryProfiles() {
        try {
            List<CharacterProfileVo> characterProfileVoList = new ArrayList<>();
            for (CharacterProfileDto characterProfileDto : historyProfileDtoList) {
                characterProfileVoList.add(profileViewManager.convertCharacterProfileToVo(characterProfileDto));
            }
            Message msg = new Message();
            msg.what = 0;
            msg.obj = characterProfileVoList;
            historyConvertHandler.sendMessage(msg);
        } catch (RuntimeException e) {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = e.getMessage();
            historyConvertHandler.sendMessage(msg);
        }
    }

    private void handleHistoryConvert(Message msg) {
        switch (msg.what) {
            case 0:
                if (flagLoading && isAdded()) {
                    historyProfileVoList = (List<CharacterProfileVo>) msg.obj;
                    updateHistoryView();
                    flagLoading = false;
                }
                break;
            case 1:
                toast.setText((String) msg.obj);
                toast.show();
                flagLoading = false;
                foldHistories();
                break;
        }
    }

    private void updateHistoryView() {
        imageLoading.setVisibility(View.GONE);
        layoutHistoryList.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        for (int i = 0; i < historyProfileDtoList.size(); i++) {
            ProfileHistoryForShowDetail profileHistoryFragment =
                    ProfileHistoryForShowDetail.newInstance(historyProfileDtoList.get(i), historyProfileVoList.get(i));
            if(layoutContainer == null) {
                layoutContainer = new LinearLayout(getContext());
                layoutContainer.setOrientation(LinearLayout.VERTICAL);
                containerId = View.generateViewId();
                layoutContainer.setId(containerId);
                layoutHistoryList.addView(layoutContainer);
            }
            fragmentTransaction.add(containerId, profileHistoryFragment);
            profileHistoryFragmentList.add(profileHistoryFragment);
        }
        fragmentTransaction.commit();
    }

    public static class ProfileHistoryForShowDetail extends ProfileHistoryFragment {

        public ProfileHistoryForShowDetail() {

        }

        public static ProfileHistoryForShowDetail newInstance(CharacterProfileDto characterProfileDto, CharacterProfileVo characterProfileVo) {
            ProfileHistoryForShowDetail fragment = new ProfileHistoryForShowDetail();
            Bundle args = new Bundle();
            args.putSerializable("characterProfileDto", characterProfileDto);
            args.putSerializable("characterProfileVo", characterProfileVo);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        protected void onProfileHistoryClicked() {
            showCharacterDetail();
        }

        private void showCharacterDetail() {
            Intent intent = new Intent(requireActivity().getApplicationContext(), CharacterDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("characterProfileVo", characterProfileVo);
            bundle.putSerializable("characterProfileDto", characterProfileDto);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

}
