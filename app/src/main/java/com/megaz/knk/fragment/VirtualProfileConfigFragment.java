package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.fragment.app.Fragment;

import com.megaz.knk.KnkDatabase;
import com.megaz.knk.R;
import com.megaz.knk.dao.CharacterDexDao;
import com.megaz.knk.dao.WeaponDexDao;
import com.megaz.knk.dto.CharacterProfileDto;
import com.megaz.knk.entity.CharacterDex;
import com.megaz.knk.entity.WeaponDex;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.utils.MetaDataUtils;
import com.megaz.knk.utils.ProfileConvertUtils;
import com.megaz.knk.view.CheckBoxView;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VirtualProfileConfigFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VirtualProfileConfigFragment extends BaseDialogFragment {
    private CharacterProfileDto characterProfileDto;
    private CharacterDex characterDex;
    private WeaponDex weaponDex;

    private ImageView imageCharacter, imageWeapon;
    private TextView textCharacterName, textCharacterCons, textCharacterPromote, textWeaponPromote,
            textTalentALevel, textTalentELevel, textTalentQLevel, textWeaponName, textWeaponRefine;
    private EditText editTextCharacterLevel, editTextWeaponLevel;
    private SeekBar seekBarCons, seekBarTalentA, seekBarTalentE, seekBarTalentQ, seekBarRefine;
    private CheckBoxView checkBoxCharacterPromote, checkBoxWeaponPromote;

    private Handler dexQueryHandler;
    private CharacterDexDao characterDexDao;
    private WeaponDexDao weaponDexDao;

    public VirtualProfileConfigFragment() {
        // Required empty public constructor
    }

    public static VirtualProfileConfigFragment newInstance(CharacterProfileDto characterProfileDto) {
        VirtualProfileConfigFragment fragment = new VirtualProfileConfigFragment();
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
        return inflater.inflate(R.layout.fragment_virtual_profile_config, container, false);
    }

    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        // static typeface
        ((TextView) view.findViewById(R.id.text_character_level_prefix)).setTypeface(typefaceNum);
        ((TextView) view.findViewById(R.id.text_weapon_level_prefix)).setTypeface(typefaceNum);
        // character
        imageCharacter = view.findViewById(R.id.img_character_avatar);
        textCharacterName = view.findViewById(R.id.text_character_name);
        textCharacterCons = view.findViewById(R.id.text_character_cons);
        editTextCharacterLevel = view.findViewById(R.id.edtx_character_level);
        editTextCharacterLevel.setTypeface(typefaceNum);
        checkBoxCharacterPromote = view.findViewById(R.id.checkbox_character_promote);
        textCharacterPromote = view.findViewById(R.id.text_character_promote);
        textTalentALevel = view.findViewById(R.id.text_talent_a_level);
        textTalentALevel.setTypeface(typefaceNum);
        textTalentELevel = view.findViewById(R.id.text_talent_e_level);
        textTalentELevel.setTypeface(typefaceNum);
        textTalentQLevel = view.findViewById(R.id.text_talent_q_level);
        textTalentQLevel.setTypeface(typefaceNum);
        seekBarCons = view.findViewById(R.id.seekbar_cons);
        seekBarTalentA = view.findViewById(R.id.seekbar_talent_a);
        seekBarTalentE = view.findViewById(R.id.seekbar_talent_e);
        seekBarTalentQ = view.findViewById(R.id.seekbar_talent_q);
        // weapon
        imageWeapon = view.findViewById(R.id.img_weapon);
        textWeaponName = view.findViewById(R.id.text_weapon_name);
        textWeaponRefine = view.findViewById(R.id.text_weapon_refine);
        seekBarRefine = view.findViewById(R.id.seekbar_refine);
        editTextWeaponLevel = view.findViewById(R.id.edtx_weapon_level);
        editTextWeaponLevel.setTypeface(typefaceNum);
        checkBoxWeaponPromote = view.findViewById(R.id.checkbox_weapon_promote);
        textWeaponPromote = view.findViewById(R.id.text_weapon_promote);
    }

    @Override
    protected void setCallback(@NonNull View view) {
        super.setCallback(view);
        view.findViewById(R.id.btn_confirm).setOnClickListener(new ConfirmOnClickListener());
        view.findViewById(R.id.btn_cancel).setOnClickListener(new CancelOnClickListener());
        dexQueryHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                handleDexQuery(msg);
            }
        };
        editTextCharacterLevel.addTextChangedListener(new LevelEditListener(
                editTextCharacterLevel, checkBoxCharacterPromote, textCharacterPromote));
        editTextWeaponLevel.addTextChangedListener(new LevelEditListener(
                editTextWeaponLevel, checkBoxWeaponPromote, textWeaponPromote));
        checkBoxCharacterPromote.setOnCheckedChangeListener(new PromoteCheckListener(textCharacterPromote));
        checkBoxWeaponPromote.setOnCheckedChangeListener(new PromoteCheckListener(textWeaponPromote));
        seekBarCons.setOnSeekBarChangeListener(new CharacterConsChangeListener());
        seekBarRefine.setOnSeekBarChangeListener(new WeaponRefineChangeListener());
        seekBarTalentA.setOnSeekBarChangeListener(new TalentLevelChangeListener(textTalentALevel));
        seekBarTalentE.setOnSeekBarChangeListener(new TalentLevelChangeListener(textTalentELevel));
        seekBarTalentQ.setOnSeekBarChangeListener(new TalentLevelChangeListener(textTalentQLevel));
    }

    @Override
    protected void initialize(@NonNull View view) {
        super.initialize(view);
        characterDexDao = KnkDatabase.getKnkDatabase(requireContext()).getCharacterDexDao();
        weaponDexDao = KnkDatabase.getKnkDatabase(requireContext()).getWeaponDexDao();
        initEditViewsByProfile();
        updateViewsByProfile();
        new Thread(this::queryDex).start();
    }

    private class CancelOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Objects.requireNonNull(getDialog()).cancel();
        }
    }

    private class ConfirmOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (checkAndSetLevel()) {
                //TODO: return virtual profile
                Objects.requireNonNull(getDialog()).cancel();
            } else {
                toast.setText("请正确填入等级");
                toast.show();
            }
        }
    }

    private static class LevelEditListener implements TextWatcher {
        private final EditText editTextLevel;
        private final CheckBoxView checkBoxPromote;
        private final TextView textPromote;

        public LevelEditListener(EditText editTextLevel, CheckBoxView checkBoxPromote, TextView textPromote) {
            this.editTextLevel = editTextLevel;
            this.checkBoxPromote = checkBoxPromote;
            this.textPromote = textPromote;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            try {
                int level = Integer.parseInt(editTextLevel.getText().toString());
                if (ProfileConvertUtils.isPromotingLevel(level)) {
                    textPromote.setVisibility(View.VISIBLE);
                    checkBoxPromote.setVisibility(View.VISIBLE);
                } else {
                    textPromote.setVisibility(View.GONE);
                    checkBoxPromote.setVisibility(View.GONE);
                }
            } catch (RuntimeException e){
                textPromote.setVisibility(View.GONE);
                checkBoxPromote.setVisibility(View.GONE);
            }
        }
    }

    private class PromoteCheckListener implements CheckBoxView.OnCheckedChangeListener {
        private final TextView textPromote;

        public PromoteCheckListener(TextView textPromote) {
            this.textPromote = textPromote;
        }

        @Override
        public void onCheckedChanged(CheckBoxView buttonView, boolean isChecked) {
            if(isChecked) {
                textPromote.setText(getString(R.string.text_promote_true));
            } else {
                textPromote.setText(getString(R.string.text_promote_false));
            }
        }
    }

    private class CharacterConsChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int cons = Math.max(0, Math.min(i, 6));
            textCharacterCons.setBackgroundColor(requireContext().getColor(DynamicStyleUtils.getConstellationColor(cons)));
            textCharacterCons.setText(getString(R.string.text_constellation, cons));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private class TalentLevelChangeListener implements SeekBar.OnSeekBarChangeListener{
        private final TextView textLevel;

        public TalentLevelChangeListener(TextView textLevel) {
            this.textLevel = textLevel;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int level = Math.max(0, Math.min(15, i));
            textLevel.setText(getString(R.string.text_level, level));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private class WeaponRefineChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int refine = Math.max(1, Math.min(5, i));
            textWeaponRefine.setText(getString(R.string.text_refine, refine));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private void handleDexQuery(Message message) {
        switch (message.what) {
            case 0:
                characterDex = (CharacterDex) message.obj;
                updateCharacterStaticViews();
                break;
            case 1:
                weaponDex = (WeaponDex) message.obj;
                updateWeaponStaticViews();
                break;
            case 2:
                toast.setText((String) message.obj);
                toast.show();
                break;
        }
    }

    @WorkerThread
    private void queryDex() {
        try {
            CharacterDex characterDex = MetaDataUtils.queryCharacterDex(characterDexDao, characterProfileDto.getCharacterId());
            Message messageCharacter = new Message();
            messageCharacter.what = 0;
            messageCharacter.obj = characterDex;
            dexQueryHandler.sendMessage(messageCharacter);
            WeaponDex weaponDex = MetaDataUtils.queryWeaponDex(weaponDexDao, characterProfileDto.getWeapon().getWeaponId());
            Message messageWeapon = new Message();
            messageWeapon.what = 1;
            messageWeapon.obj = weaponDex;
            dexQueryHandler.sendMessage(messageWeapon);
        } catch (Exception e) {
            Message message = new Message();
            message.what = 2;
            message.obj = e.getMessage();
            dexQueryHandler.sendMessage(message);
        }
    }

    private Boolean checkAndSetLevel() {
        if (editTextCharacterLevel.getText().toString().isEmpty()) {
            return false;
        } else {
            int levelCharacter = Integer.parseInt(editTextCharacterLevel.getText().toString());
            if (levelCharacter >= 1 && levelCharacter <= 90) {
                characterProfileDto.setLevel(levelCharacter);
                characterProfileDto.setPhase(
                        ProfileConvertUtils.getPhaseByLevel(levelCharacter, checkBoxCharacterPromote.isChecked()));
            } else {
                return false;
            }
        }

        if (editTextWeaponLevel.getText().toString().isEmpty()) {
            return false;
        } else {
            int levelWeapon = Integer.parseInt(editTextWeaponLevel.getText().toString());
            if (levelWeapon >= 1 && levelWeapon <= 90) {
                characterProfileDto.getWeapon().setLevel(levelWeapon);
                characterProfileDto.getWeapon().setPhase(
                        ProfileConvertUtils.getPhaseByLevel(levelWeapon, checkBoxWeaponPromote.isChecked()));
            } else {
                return false;
            }
        }
        return true;
    }

    @SuppressLint("DefaultLocale")
    private void initEditViewsByProfile() {
        editTextCharacterLevel.setText(String.format("%d", characterProfileDto.getLevel()));
        if(ProfileConvertUtils.isPromoted(characterProfileDto.getLevel(), characterProfileDto.getPhase())) {
            checkBoxCharacterPromote.setChecked(true);
            textCharacterPromote.setText(getString(R.string.text_promote_true));
        } else {
            checkBoxCharacterPromote.setChecked(false);
            textCharacterPromote.setText(getString(R.string.text_promote_false));
        }
        if(ProfileConvertUtils.isPromotingLevel(characterProfileDto.getLevel())){
            checkBoxCharacterPromote.setVisibility(View.VISIBLE);
            textCharacterPromote.setVisibility(View.VISIBLE);
        } else {
            checkBoxCharacterPromote.setVisibility(View.GONE);
            textCharacterPromote.setVisibility(View.GONE);
        }
        textTalentALevel.setText(getString(R.string.text_level,
                characterProfileDto.getTalentABaseLevel()+characterProfileDto.getTalentAPlusLevel()));
        seekBarTalentA.setProgress(characterProfileDto.getTalentABaseLevel()+characterProfileDto.getTalentAPlusLevel());
        textTalentELevel.setText(getString(R.string.text_level,
                characterProfileDto.getTalentEBaseLevel()+characterProfileDto.getTalentEPlusLevel()));
        seekBarTalentE.setProgress(characterProfileDto.getTalentEBaseLevel()+characterProfileDto.getTalentEPlusLevel());
        textTalentQLevel.setText(getString(R.string.text_level,
                characterProfileDto.getTalentQBaseLevel()+characterProfileDto.getTalentQPlusLevel()));
        seekBarTalentQ.setProgress(characterProfileDto.getTalentQBaseLevel()+characterProfileDto.getTalentQPlusLevel());

        editTextWeaponLevel.setText(String.format("%d", characterProfileDto.getWeapon().getLevel()));
        if(ProfileConvertUtils.isPromoted(characterProfileDto.getWeapon().getLevel(), characterProfileDto.getWeapon().getPhase())) {
            checkBoxWeaponPromote.setChecked(true);
            textWeaponPromote.setText(getString(R.string.text_promote_true));
        } else {
            checkBoxWeaponPromote.setChecked(false);
            textWeaponPromote.setText(getString(R.string.text_promote_false));
        }
        if(ProfileConvertUtils.isPromotingLevel(characterProfileDto.getWeapon().getLevel())){
            checkBoxWeaponPromote.setVisibility(View.VISIBLE);
            textWeaponPromote.setVisibility(View.VISIBLE);
        } else {
            checkBoxWeaponPromote.setVisibility(View.GONE);
            textWeaponPromote.setVisibility(View.GONE);
        }
        seekBarRefine.setProgress(characterProfileDto.getWeapon().getRefineRank());
    }

    @SuppressLint({"DefaultLocale"})
    private void updateViewsByProfile() {
        textCharacterCons.setText(getString(R.string.text_constellation, characterProfileDto.getConstellation()));
        textCharacterCons.setBackgroundColor(requireContext()
                .getColor(DynamicStyleUtils.getConstellationColor(characterProfileDto.getConstellation())));
        textWeaponRefine.setText(getString(R.string.text_refine, characterProfileDto.getWeapon().getRefineRank()));
    }

    private void updateCharacterStaticViews() {
        if(characterDex == null) {
            return;
        }
        imageCharacter.setImageBitmap(ImageResourceUtils.getIconBitmap(requireContext(), characterDex.getIconAvatar()));
        imageCharacter.setBackgroundColor(requireContext().getColor(DynamicStyleUtils.getElementAvatarBackgroundColor(characterDex.getElement())));
        textCharacterName.setText(characterDex.getCharacterName());
    }

    private void updateWeaponStaticViews() {
        if(weaponDex == null) {
            return;
        }
        imageWeapon.setImageBitmap(ImageResourceUtils.getIconBitmap(requireContext(), weaponDex.getIconAwaken()));
        imageWeapon.setBackgroundResource(DynamicStyleUtils.getQualityBackground(weaponDex.getStar()));
        textWeaponName.setText(weaponDex.getWeaponName());
    }
}