package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.fragment.app.Fragment;

import com.megaz.knk.KnkDatabase;
import com.megaz.knk.R;
import com.megaz.knk.activity.CharacterDetailActivity;
import com.megaz.knk.activity.VirtualCharacterBrowseActivity;
import com.megaz.knk.activity.VirtualWeaponBrowseActivity;
import com.megaz.knk.computation.CharacterOverview;
import com.megaz.knk.constant.SourceTalentEnum;
import com.megaz.knk.dao.CharacterDexDao;
import com.megaz.knk.dao.WeaponDexDao;
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
    private String originalCharacterId;
    private CharacterOverview characterOverview;
    private CharacterDex characterDex;
    private WeaponDex weaponDex;

    private ImageView imageCharacter, imageWeapon;
    private TextView textCharacterName, textCharacterCons, textCharacterPromote, textWeaponPromote,
            textTalentALevel, textTalentELevel, textTalentQLevel, textWeaponName, textWeaponRefine,
            textWeaponNotMatch;
    private EditText editTextCharacterLevel, editTextWeaponLevel;
    private SeekBar seekBarCons, seekBarTalentA, seekBarTalentE, seekBarTalentQ, seekBarRefine;
    private CheckBoxView checkBoxCharacterPromote, checkBoxWeaponPromote;

    private Handler dexQueryHandler;
    private CharacterDexDao characterDexDao;
    private WeaponDexDao weaponDexDao;

    private ActivityResultLauncher<Intent> virtualCharacterBrowseActivityResultLauncher, virtualWeaponBrowseActivityResultLauncher;

    public VirtualProfileConfigFragment() {
        // Required empty public constructor
    }

    public static VirtualProfileConfigFragment newInstance(CharacterOverview characterOverview) {
        VirtualProfileConfigFragment fragment = new VirtualProfileConfigFragment();
        Bundle args = new Bundle();
        args.putSerializable("characterOverview", characterOverview);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            characterOverview = (CharacterOverview) getArguments().getSerializable("characterOverview");
            originalCharacterId = characterOverview.getCharacterId();
        }
        virtualCharacterBrowseActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Bundle returnBundle = data.getExtras();
                        characterDex = (CharacterDex) returnBundle.getSerializable("characterDex");
                        updateCharacterStaticViews();
                    }
                }
        );
        virtualWeaponBrowseActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Bundle returnBundle = data.getExtras();
                        weaponDex = (WeaponDex) returnBundle.getSerializable("weaponDex");
                        updateWeaponStaticViews();
                    }
                }
        );
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
        textWeaponNotMatch = view.findViewById(R.id.text_weapon_not_match);
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
        imageCharacter.setOnClickListener(new CharacterOnClickListener());
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
        imageWeapon.setOnClickListener(new WeaponOnClickListener());
    }

    @Override
    protected void initialize(@NonNull View view) {
        super.initialize(view);
        characterDexDao = KnkDatabase.getKnkDatabase(requireContext()).getCharacterDexDao();
        weaponDexDao = KnkDatabase.getKnkDatabase(requireContext()).getWeaponDexDao();
        initViewsByOverview();
        new Thread(this::queryDex).start();
    }


    private class CharacterOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(requireContext(), VirtualCharacterBrowseActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("characterId", originalCharacterId);
            intent.putExtras(bundle);
            virtualCharacterBrowseActivityResultLauncher.launch(intent);
        }
    }

    private class WeaponOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(requireContext(), VirtualWeaponBrowseActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("type", characterDex.getWeaponType());
            intent.putExtras(bundle);
            virtualWeaponBrowseActivityResultLauncher.launch(intent);
        }
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
            if (checkAndUpdateOverview()) {
                ((CharacterDetailActivity) requireActivity()).onVirtualConfigSet(characterOverview);
                Objects.requireNonNull(getDialog()).cancel();
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
            } catch (RuntimeException e) {
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
            if (isChecked) {
                textPromote.setText(getString(R.string.text_promote_true));
            } else {
                textPromote.setText(getString(R.string.text_promote_false));
            }
        }
    }

    private class CharacterConsChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int cons = Math.max(0, Math.min(i, 6));
            textCharacterCons.setBackgroundColor(requireContext().getColor(DynamicStyleUtils.getConstellationColor(cons)));
            textCharacterCons.setText(getString(R.string.text_constellation, cons));
            if (characterDex != null) updateTalentLevelViews(cons);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private class TalentLevelChangeListener implements SeekBar.OnSeekBarChangeListener {
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

    private class WeaponRefineChangeListener implements SeekBar.OnSeekBarChangeListener {

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
                updateTalentLevelViews(characterOverview.getConstellation());
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
            CharacterDex characterDex = MetaDataUtils.queryCharacterDex(characterDexDao, characterOverview.getCharacterId());
            Message messageCharacter = new Message();
            messageCharacter.what = 0;
            messageCharacter.obj = characterDex;
            dexQueryHandler.sendMessage(messageCharacter);
            WeaponDex weaponDex = MetaDataUtils.queryWeaponDex(weaponDexDao, characterOverview.getWeaponId());
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

    private Boolean checkAndUpdateOverview() {
        if (!checkAndSetLevel()) {
            toast.setText("请正确填入等级");
            toast.show();
            return false;
        }
        if (characterDex.getWeaponType() != weaponDex.getType()) {
            toast.setText("请选择类型正确的武器");
            toast.show();
            return false;
        }
        characterOverview.setCharacterId(characterDex.getCharacterId());
        characterOverview.setElement(characterDex.getElement());
        characterOverview.setConstellation(seekBarCons.getProgress());
        characterOverview.setWeaponId(weaponDex.getWeaponId());
        characterOverview.setWeaponRefinement(seekBarRefine.getProgress());
        characterOverview.getTalentLevel().put(SourceTalentEnum.A, seekBarTalentA.getProgress());
        characterOverview.getTalentLevel().put(SourceTalentEnum.E, seekBarTalentE.getProgress());
        characterOverview.getTalentLevel().put(SourceTalentEnum.Q, seekBarTalentQ.getProgress());
        int cons = seekBarCons.getProgress();
        if (characterDex.getTalentAUpCons() != null && cons >= characterDex.getTalentAUpCons())
            characterOverview.getTalentLevelPlus().put(SourceTalentEnum.A, 3);
        else
            characterOverview.getTalentLevelPlus().put(SourceTalentEnum.A, 0);
        if (characterDex.getTalentEUpCons() != null && cons >= characterDex.getTalentEUpCons())
            characterOverview.getTalentLevelPlus().put(SourceTalentEnum.E, 3);
        else
            characterOverview.getTalentLevelPlus().put(SourceTalentEnum.E, 0);
        if (characterDex.getTalentQUpCons() != null && cons >= characterDex.getTalentQUpCons())
            characterOverview.getTalentLevelPlus().put(SourceTalentEnum.Q, 3);
        else
            characterOverview.getTalentLevelPlus().put(SourceTalentEnum.Q, 0);
        return true;
    }

    private Boolean checkAndSetLevel() {
        if (editTextCharacterLevel.getText().toString().isEmpty()) {
            return false;
        } else {
            int levelCharacter = Integer.parseInt(editTextCharacterLevel.getText().toString());
            if (levelCharacter >= 1 && levelCharacter <= 90) {
                characterOverview.setLevel(levelCharacter);
                characterOverview.setPhase(
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
                characterOverview.setWeaponLevel(levelWeapon);
                characterOverview.setWeaponPhase(
                        ProfileConvertUtils.getPhaseByLevel(levelWeapon, checkBoxWeaponPromote.isChecked()));
            } else {
                return false;
            }
        }
        return true;
    }

    @SuppressLint("DefaultLocale")
    private void initViewsByOverview() {
        seekBarCons.setProgress(characterOverview.getConstellation());
        editTextCharacterLevel.setText(String.format("%d", characterOverview.getLevel()));
        if (ProfileConvertUtils.isPromoted(characterOverview.getLevel(), characterOverview.getPhase())) {
            checkBoxCharacterPromote.setChecked(true);
            textCharacterPromote.setText(getString(R.string.text_promote_true));
        } else {
            checkBoxCharacterPromote.setChecked(false);
            textCharacterPromote.setText(getString(R.string.text_promote_false));
        }
        if (ProfileConvertUtils.isPromotingLevel(characterOverview.getLevel())) {
            checkBoxCharacterPromote.setVisibility(View.VISIBLE);
            textCharacterPromote.setVisibility(View.VISIBLE);
        } else {
            checkBoxCharacterPromote.setVisibility(View.GONE);
            textCharacterPromote.setVisibility(View.GONE);
        }
        textTalentALevel.setText(getString(R.string.text_level, characterOverview.getTalentLevel().get(SourceTalentEnum.A)));
        seekBarTalentA.setProgress(Objects.requireNonNull(characterOverview.getTalentLevel().get(SourceTalentEnum.A)));
        textTalentELevel.setText(getString(R.string.text_level, characterOverview.getTalentLevel().get(SourceTalentEnum.E)));
        seekBarTalentE.setProgress(Objects.requireNonNull(characterOverview.getTalentLevel().get(SourceTalentEnum.E)));
        textTalentQLevel.setText(getString(R.string.text_level, characterOverview.getTalentLevel().get(SourceTalentEnum.Q)));
        seekBarTalentQ.setProgress(Objects.requireNonNull(characterOverview.getTalentLevel().get(SourceTalentEnum.Q)));

        editTextWeaponLevel.setText(String.format("%d", characterOverview.getWeaponLevel()));
        if (ProfileConvertUtils.isPromoted(characterOverview.getWeaponLevel(), characterOverview.getWeaponPhase())) {
            checkBoxWeaponPromote.setChecked(true);
            textWeaponPromote.setText(getString(R.string.text_promote_true));
        } else {
            checkBoxWeaponPromote.setChecked(false);
            textWeaponPromote.setText(getString(R.string.text_promote_false));
        }
        if (ProfileConvertUtils.isPromotingLevel(characterOverview.getWeaponLevel())) {
            checkBoxWeaponPromote.setVisibility(View.VISIBLE);
            textWeaponPromote.setVisibility(View.VISIBLE);
        } else {
            checkBoxWeaponPromote.setVisibility(View.GONE);
            textWeaponPromote.setVisibility(View.GONE);
        }
        seekBarRefine.setProgress(characterOverview.getWeaponRefinement());

        textCharacterCons.setText(getString(R.string.text_constellation, characterOverview.getConstellation()));
        textCharacterCons.setBackgroundColor(requireContext()
                .getColor(DynamicStyleUtils.getConstellationColor(characterOverview.getConstellation())));
        textWeaponRefine.setText(getString(R.string.text_refine, characterOverview.getWeaponRefinement()));
    }

    private void updateTalentLevelViews(int cons) {
        assert characterDex != null;
        if (characterDex.getTalentAUpCons() != null && cons >= characterDex.getTalentAUpCons()) {
            textTalentALevel.setTextColor(requireContext().getColor(R.color.talent_blue));
            textTalentALevel.setText(getString(R.string.text_level, seekBarTalentA.getProgress() + 3));
        } else {
            textTalentALevel.setTextColor(requireContext().getColor(R.color.white));
            textTalentALevel.setText(getString(R.string.text_level, seekBarTalentA.getProgress()));
        }
        if (characterDex.getTalentEUpCons() != null && cons >= characterDex.getTalentEUpCons()) {
            textTalentELevel.setTextColor(requireContext().getColor(R.color.talent_blue));
            textTalentELevel.setText(getString(R.string.text_level, seekBarTalentE.getProgress() + 3));
        } else {
            textTalentELevel.setTextColor(requireContext().getColor(R.color.white));
            textTalentELevel.setText(getString(R.string.text_level, seekBarTalentE.getProgress()));
        }
        if (characterDex.getTalentQUpCons() != null && cons >= characterDex.getTalentQUpCons()) {
            textTalentQLevel.setTextColor(requireContext().getColor(R.color.talent_blue));
            textTalentQLevel.setText(getString(R.string.text_level, seekBarTalentQ.getProgress() + 3));
        } else {
            textTalentQLevel.setTextColor(requireContext().getColor(R.color.white));
            textTalentQLevel.setText(getString(R.string.text_level, seekBarTalentQ.getProgress()));
        }
    }

    private void updateCharacterStaticViews() {
        if (characterDex == null) {
            return;
        }
        imageCharacter.setImageBitmap(ImageResourceUtils.getIconBitmap(requireContext(), characterDex.getIconAvatar()));
        imageCharacter.setBackgroundColor(requireContext().getColor(DynamicStyleUtils.getElementAvatarBackgroundColor(characterDex.getElement())));
        textCharacterName.setText(characterDex.getCharacterName());
        if (weaponDex == null || characterDex.getWeaponType() != weaponDex.getType()) {
            textWeaponNotMatch.setVisibility(View.VISIBLE);
        } else {
            textWeaponNotMatch.setVisibility(View.GONE);
        }
    }

    private void updateWeaponStaticViews() {
        if (weaponDex == null) {
            return;
        }
        imageWeapon.setImageBitmap(ImageResourceUtils.getIconBitmap(requireContext(), weaponDex.getIconAwaken()));
        imageWeapon.setBackgroundResource(DynamicStyleUtils.getQualityBackground(weaponDex.getStar()));
        textWeaponName.setText(weaponDex.getWeaponName());
        if (characterDex == null || characterDex.getWeaponType() != weaponDex.getType()) {
            textWeaponNotMatch.setVisibility(View.VISIBLE);
        } else {
            textWeaponNotMatch.setVisibility(View.GONE);
        }
    }
}