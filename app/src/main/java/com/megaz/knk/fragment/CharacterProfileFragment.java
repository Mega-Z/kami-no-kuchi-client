package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.megaz.knk.R;
import com.megaz.knk.activity.CharacterDetailActivity;
import com.megaz.knk.computation.CharacterAttribute;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.CharacterProfileVo;

public class CharacterProfileFragment extends Fragment {

    private CharacterAttribute characterAttribute;
    private CharacterProfileVo characterProfileVo;

    private TextView textCharacterName, textCharacterLevel, textCharacterCons;
    private ImageView imageCharacterCard, imageIsNew;

    public CharacterProfileFragment() {
        // Required empty public constructor
    }

    public static CharacterProfileFragment newInstance(CharacterAttribute characterAttribute, CharacterProfileVo characterProfileVo) {
        CharacterProfileFragment fragment = new CharacterProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("characterAttribute", characterAttribute);
        args.putSerializable("characterProfileVo", characterProfileVo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            characterProfileVo = (CharacterProfileVo) getArguments().getSerializable("characterProfileVo");
            characterAttribute = (CharacterAttribute) getArguments().getSerializable("characterAttribute");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character_profile, container, false);
    }

    @SuppressLint({"ResourceAsColor", "ResourceType", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textCharacterName = view.findViewById(R.id.text_character_name);
        textCharacterCons = view.findViewById(R.id.text_character_cons);
        textCharacterLevel = view.findViewById(R.id.text_character_level);
        imageCharacterCard = view.findViewById(R.id.img_character_card);
        imageIsNew = view.findViewById(R.id.img_is_new);

        textCharacterName.setText(characterProfileVo.getCharacterName());
        textCharacterLevel.setText(getString(R.string.text_level_prefix)+characterProfileVo.getLevel());
        textCharacterCons.setText(characterProfileVo.getConstellation()+getString(R.string.text_constellation_suffix));
        textCharacterCons.setBackgroundColor(requireContext()
                .getColor(DynamicStyleUtils.getConstellationColor(characterProfileVo.getConstellation())));

        if(characterProfileVo.getNewData()){
            imageIsNew.setVisibility(View.VISIBLE);
        }else{
            imageIsNew.setVisibility(View.INVISIBLE);
        }
        imageCharacterCard.setImageBitmap(ImageResourceUtils.getIconBitmap(requireContext(), characterProfileVo.getCardIcon()));
        view.setOnTouchListener(new CharacterProfileOnTouchListener());
        view.setOnClickListener(new CharacterProfileOnClickListener());
    }

    private class CharacterProfileOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            imageIsNew.setVisibility(View.INVISIBLE);
            characterProfileVo.setNewData(false);
            showCharacterDetail();
        }
    }

    private class CharacterProfileOnTouchListener implements View.OnTouchListener {
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

    private void showCharacterDetail() {
        Intent intent = new Intent(requireActivity().getApplicationContext(), CharacterDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("characterProfileVo", characterProfileVo);
        bundle.putSerializable("characterAttribute", characterAttribute);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
