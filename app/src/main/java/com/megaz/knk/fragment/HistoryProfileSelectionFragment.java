package com.megaz.knk.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.megaz.knk.R;
import com.megaz.knk.activity.CharacterDetailActivity;
import com.megaz.knk.dto.CharacterProfileDto;
import com.megaz.knk.manager.LocalProfileManager;
import com.megaz.knk.manager.ProfileViewManager;
import com.megaz.knk.utils.ProfileConvertUtils;
import com.megaz.knk.vo.CharacterProfileVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryProfileSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryProfileSelectionFragment extends BaseDialogFragment {
    private CharacterProfileDto characterProfileDto;
    private String uid, characterId;

    private ImageView imageLoading;
    private ObjectAnimator animatorLoading;
    private TextView textNoHistory;
    private LinearLayout layoutHistoryList;

    private Handler historyQueryHandler, historyConvertHandler;
    private List<ProfileHistoryForCompare> profileHistoryFragmentList;
    private List<CharacterProfileDto> historyProfileDtoList;
    private List<CharacterProfileVo> historyProfileVoList;

    private LocalProfileManager localProfileManager;
    private ProfileViewManager profileViewManager;

    public HistoryProfileSelectionFragment() {
        // Required empty public constructor
    }

    public static HistoryProfileSelectionFragment newInstance(CharacterProfileDto characterProfileDto) {
        HistoryProfileSelectionFragment fragment = new HistoryProfileSelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable("characterProfileDto", characterProfileDto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            characterProfileDto = (CharacterProfileDto) getArguments().getSerializable("characterProfileDto");
            uid = characterProfileDto.getUid();
            characterId = characterProfileDto.getCharacterId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_priofile_selection, container, false);
    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        textNoHistory = view.findViewById(R.id.text_no_history);
        textNoHistory.setVisibility(View.GONE);
        imageLoading = view.findViewById(R.id.img_loading);
        imageLoading.setVisibility(View.VISIBLE);
        layoutHistoryList = view.findViewById(R.id.layout_history_selection);
        layoutHistoryList.setVisibility(View.GONE);

        animatorLoading = ObjectAnimator.ofFloat(imageLoading, "rotation", -360f);
        animatorLoading.setDuration(1000);
        animatorLoading.setRepeatCount(ValueAnimator.INFINITE);
        animatorLoading.start();

        profileHistoryFragmentList = new ArrayList<>();
        historyProfileDtoList = new ArrayList<>();
        historyProfileVoList = new ArrayList<>();

        localProfileManager = LocalProfileManager.getInstance(getContext());
        profileViewManager = ProfileViewManager.getInstance(getContext());
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        view.findViewById(R.id.btn_cancel).setOnClickListener(new CancelOnClickListener());
        historyQueryHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                handleHistoryQuery(msg);
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

    @Override
    protected void initialize(@NonNull View view) {
        super.initialize(view);
        new Thread(this::toQueryProfileHistory).start();
    }

    private void toQueryProfileHistory() {
        try {
            List<CharacterProfileDto> characterProfiles = queryProfileHistory();
            Message msg = new Message();
            msg.what = 0;
            msg.obj = characterProfiles;
            historyQueryHandler.sendMessage(msg);
        } catch (RuntimeException e) {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = e.getMessage();
            historyQueryHandler.sendMessage(msg);
        }
    }

    private List<CharacterProfileDto> queryProfileHistory() {
        List<CharacterProfileDto> characterProfiles = localProfileManager.getCharacterProfilesByCharacterIdAndUid(characterId, uid);
        return characterProfiles.stream().filter(history -> !ProfileConvertUtils.isSameCharacterProfile(characterProfileDto, history)).collect(Collectors.toList());
    }

    private void handleHistoryQuery(Message msg) {
        switch (msg.what) {
            case 0:
                if(isAdded()) {
                    historyProfileDtoList = (List<CharacterProfileDto>) msg.obj;
                    if (historyProfileDtoList.isEmpty()) {
                        imageLoading.setVisibility(View.GONE);
                        textNoHistory.setVisibility(View.VISIBLE);
                    } else {
                        new Thread(this::convertHistoryProfiles).start();
                    }
                }
                break;
            case 1:
                if (isAdded()) {
                    imageLoading.setVisibility(View.GONE);
                    textNoHistory.setVisibility(View.VISIBLE);
                }
                toast.setText((String) msg.obj);
                toast.show();
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
                if (isAdded()) {
                    historyProfileVoList = (List<CharacterProfileVo>) msg.obj;
                    imageLoading.setVisibility(View.GONE);
                    textNoHistory.setVisibility(View.GONE);
                    updateHistoryView();
                }
                break;
            case 1:
                if (isAdded()) {
                    imageLoading.setVisibility(View.GONE);
                    textNoHistory.setVisibility(View.VISIBLE);
                }
                toast.setText((String) msg.obj);
                toast.show();
                break;
        }
    }

    private void updateHistoryView() {
        layoutHistoryList.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        for (int i=0;i<historyProfileDtoList.size();i++) {
            ProfileHistoryForCompare profileHistoryFragment =
                    ProfileHistoryForCompare.newInstance(historyProfileDtoList.get(i), historyProfileVoList.get(i));
            fragmentTransaction.add(R.id.layout_history_selection, profileHistoryFragment);
        }
        fragmentTransaction.commit();
    }

    private class CancelOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Objects.requireNonNull(getDialog()).cancel();
        }
    }

    public static class ProfileHistoryForCompare extends ProfileHistoryFragment {

        public ProfileHistoryForCompare() {

        }

        public static ProfileHistoryForCompare newInstance(CharacterProfileDto characterProfileDto, CharacterProfileVo characterProfileVo) {
            ProfileHistoryForCompare fragment = new ProfileHistoryForCompare();
            Bundle args = new Bundle();
            args.putSerializable("characterProfileDto", characterProfileDto);
            args.putSerializable("characterProfileVo", characterProfileVo);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        protected void onProfileHistoryClicked() {
            ((CharacterDetailActivity)requireActivity()).onHistorySelected(characterProfileDto);
        }
    }

}