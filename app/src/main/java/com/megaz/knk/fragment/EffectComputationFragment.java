package com.megaz.knk.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.megaz.knk.R;
import com.megaz.knk.dto.CharacterProfileDto;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EffectComputationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EffectComputationFragment extends BaseFragment {

    private CharacterProfileDto characterProfileDto;


    public EffectComputationFragment() {
        // Required empty public constructor
    }
    public static EffectComputationFragment newInstance(CharacterProfileDto characterProfileDto) {
        EffectComputationFragment fragment = new EffectComputationFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_effect_computation, container, false);
    }
}