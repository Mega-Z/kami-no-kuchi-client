package com.megaz.knk.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.megaz.knk.R;
import com.megaz.knk.entity.CharacterDex;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.TalentVo;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VirtualCharacterSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VirtualCharacterSelectionFragment extends BaseFragment {

    private CharacterDex characterDex;

    public VirtualCharacterSelectionFragment() {
        // Required empty public constructor
    }

    public static VirtualCharacterSelectionFragment newInstance(CharacterDex characterDex) {
        VirtualCharacterSelectionFragment fragment = new VirtualCharacterSelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable("characterDex", characterDex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            characterDex = (CharacterDex) getArguments().getSerializable("characterDex");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_virtual_character_selection, container, false);
    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        ((ImageView)view.findViewById(R.id.img_character_avatar)).setImageBitmap(
                ImageResourceUtils.getIconBitmap(requireContext(), characterDex.getIconAvatar()));
        ((ImageView)view.findViewById(R.id.img_character_avatar)).setBackgroundColor(
                requireContext().getColor(DynamicStyleUtils.getElementAvatarBackgroundColor(characterDex.getElement())));
        ((TextView)view.findViewById(R.id.text_character_name)).setText(characterDex.getCharacterName());
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        view.setOnClickListener(view1 -> {
            Intent returnIntent = new Intent();
            Bundle returnBundle = new Bundle();
            returnBundle.putSerializable("characterDex", characterDex);
            returnIntent.putExtras(returnBundle);
            requireActivity().setResult(RESULT_OK, returnIntent);
            requireActivity().finish();
        });
    }
}