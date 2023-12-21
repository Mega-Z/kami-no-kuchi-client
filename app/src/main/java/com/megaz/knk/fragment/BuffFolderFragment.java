package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.megaz.knk.R;
import com.megaz.knk.constant.BuffSourceEnum;
import com.megaz.knk.entity.Buff;
import com.megaz.knk.vo.BuffVo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuffFolderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuffFolderFragment extends BaseFragment {
    private BuffSourceEnum source;
    private List<BuffVo> buffVoList;
    private Boolean showBuffs;

    private Handler buffViewAddHandler;

    private TextView textSourceTitle;
    private ImageView imageFolderAngle;
    private LinearLayout layoutBuffList;

    public BuffFolderFragment() {
        // Required empty public constructor
    }

    public static BuffFolderFragment newInstance(BuffSourceEnum source, ArrayList<BuffVo> buffVoList) {
        BuffFolderFragment fragment = new BuffFolderFragment();
        Bundle args = new Bundle();
        args.putSerializable("source", source);
        args.putSerializable("buffVoList", buffVoList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            source = (BuffSourceEnum) getArguments().getSerializable("source");
            buffVoList = (List<BuffVo>) getArguments().getSerializable("buffVoList");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        switch (source) {
            case CHARACTER:
                return inflater.inflate(R.layout.fragment_buff_folder_character, container, false);
            case WEAPON:
                return inflater.inflate(R.layout.fragment_buff_folder_weapon, container, false);
            case ARTIFACT_SET:
                return inflater.inflate(R.layout.fragment_buff_folder_artifact_set, container, false);
            case ELEMENT:
                return inflater.inflate(R.layout.fragment_buff_folder_element, container, false);
        }
        return inflater.inflate(R.layout.fragment_buff_folder_element, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        textSourceTitle = view.findViewById(R.id.text_source_title);
        textSourceTitle.setText("来源：" + source.getDesc());
        imageFolderAngle = view.findViewById(R.id.img_folder_angle);
        switch (source) {
            case CHARACTER:
                layoutBuffList = view.findViewById(R.id.layout_buff_list_character);
                break;
            case WEAPON:
                layoutBuffList = view.findViewById(R.id.layout_buff_list_weapon);
                break;
            case ARTIFACT_SET:
                layoutBuffList = view.findViewById(R.id.layout_buff_list_artifact_set);
                break;
            case ELEMENT:
                layoutBuffList = view.findViewById(R.id.layout_buff_list_element);
                break;
        }
        unfoldBuffs();
        new Thread(this::addBuffViewsAsync).start();
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        view.findViewById(R.id.layout_folder).setOnClickListener(new FolderOnCLickListener());
        buffViewAddHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                handleBuffViewAddMessage(msg);
            }
        };
    }

    private class FolderOnCLickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (showBuffs) {
                foldBuffs();
            } else {
                unfoldBuffs();
            }
        }
    }

    private void unfoldBuffs() {
        showBuffs = true;
        imageFolderAngle.setRotation(90);
        layoutBuffList.setVisibility(View.VISIBLE);
    }

    private void foldBuffs() {
        showBuffs = false;
        imageFolderAngle.setRotation(0);
        layoutBuffList.setVisibility(View.GONE);
    }

    private void addBuffViewsAsync() {
        buffVoList.sort(Comparator.comparing(BuffVo::getBuffId));
        for (BuffVo buffVo : buffVoList) {
            try{
                Thread.sleep(100);
                Message msg = new Message();
                msg.obj = buffVo;
                msg.what = 0;
                buffViewAddHandler.sendMessage(msg);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleBuffViewAddMessage(Message msg) {
        switch (msg.what) {
            case 0:
                BuffVo buffVo = (BuffVo) msg.obj;
                if(isAdded()) addBuffView(buffVo);
        }
    }

    private void addBuffView(BuffVo buffVo) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        BuffSelectionFragment buffSelectionFragment = BuffSelectionFragment.newInstance(buffVo);
        fragmentTransaction.add(layoutBuffList.getId(), buffSelectionFragment);
        fragmentTransaction.commit();
    }
}