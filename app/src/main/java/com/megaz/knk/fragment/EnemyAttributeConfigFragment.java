package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.megaz.knk.R;
import com.megaz.knk.activity.CharacterDetailActivity;
import com.megaz.knk.computation.EnemyAttribute;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.constant.GenshinConstantMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnemyAttributeConfigFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnemyAttributeConfigFragment extends BaseDialogFragment {
    private EnemyAttribute enemyAttribute;
    private EditText editTextLevel;
    private Map<ElementEnum, EditText> editTextResist;

    public EnemyAttributeConfigFragment() {
        // Required empty public constructor
    }

    public static EnemyAttributeConfigFragment newInstance(EnemyAttribute enemyAttribute) {
        EnemyAttributeConfigFragment fragment = new EnemyAttributeConfigFragment();
        Bundle args = new Bundle();
        args.putSerializable("enemyAttribute", enemyAttribute);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            enemyAttribute = (EnemyAttribute) getArguments().getSerializable("enemyAttribute");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enemy_attribute_config, container, false);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Typeface typefaceNum = Typeface.createFromAsset(requireActivity().getAssets(), "fonts/tttgbnumber.ttf");
        // static typeface
        ((TextView)view.findViewById(R.id.text_level_prefix)).setTypeface(typefaceNum);
        ((TextView)view.findViewById(R.id.text_resist_pyro_suffix)).setTypeface(typefaceNum);
        ((TextView)view.findViewById(R.id.text_resist_cryo_suffix)).setTypeface(typefaceNum);
        ((TextView)view.findViewById(R.id.text_resist_hydro_suffix)).setTypeface(typefaceNum);
        ((TextView)view.findViewById(R.id.text_resist_electro_suffix)).setTypeface(typefaceNum);
        ((TextView)view.findViewById(R.id.text_resist_anemo_suffix)).setTypeface(typefaceNum);
        ((TextView)view.findViewById(R.id.text_resist_geo_suffix)).setTypeface(typefaceNum);
        ((TextView)view.findViewById(R.id.text_resist_dendro_suffix)).setTypeface(typefaceNum);
        ((TextView)view.findViewById(R.id.text_resist_phy_suffix)).setTypeface(typefaceNum);
        // level
        editTextLevel = view.findViewById(R.id.edtx_level);
        editTextLevel.setTypeface(typefaceNum);
        // resist
        editTextResist = new HashMap<>();
        editTextResist.put(ElementEnum.PYRO, view.findViewById(R.id.edtx_resist_pyro));
        editTextResist.put(ElementEnum.CRYO, view.findViewById(R.id.edtx_resist_cryo));
        editTextResist.put(ElementEnum.HYDRO, view.findViewById(R.id.edtx_resist_hydro));
        editTextResist.put(ElementEnum.ELECTRO, view.findViewById(R.id.edtx_resist_electro));
        editTextResist.put(ElementEnum.ANEMO, view.findViewById(R.id.edtx_resist_anemo));
        editTextResist.put(ElementEnum.GEO, view.findViewById(R.id.edtx_resist_geo));
        editTextResist.put(ElementEnum.DENDRO, view.findViewById(R.id.edtx_resist_dendro));
        editTextResist.put(ElementEnum.PHYSICAL, view.findViewById(R.id.edtx_resist_phy));
        for(EditText editText:editTextResist.values()) {
            editText.setTypeface(typefaceNum);
        }
        initAttributeView();
        view.findViewById(R.id.btn_confirm).setOnClickListener(new ConfirmOnClickListener());
        view.findViewById(R.id.btn_cancel).setOnClickListener(new CancelOnClickListener());
    }

    private class CancelOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Objects.requireNonNull(getDialog()).cancel();
        }
    }

    private class ConfirmOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(checkAndSetAttribute()) {
                ((CharacterDetailActivity) requireActivity()).toUpdateEnemyAttribute(enemyAttribute);
                Objects.requireNonNull(getDialog()).cancel();
            } else {
                Toast toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
                toast.setText("请正确填入属性");
                toast.show();
            }
        }
    }

    private Boolean checkAndSetAttribute() {
        if(editTextLevel.getText().toString().isEmpty()) {
            return false;
        } else {
            enemyAttribute.setLevel(Integer.valueOf(editTextLevel.getText().toString()));
        }
        for(ElementEnum element: GenshinConstantMeta.ELEMENT_LIST) {
            if(Objects.requireNonNull(editTextResist.get(element)).getText().toString().isEmpty()) {
                return false;
            } else {
                enemyAttribute.setResist(element,
                        Double.parseDouble(Objects.requireNonNull(editTextResist.get(element)).getText().toString()) / 100);
            }
        }
        return true;
    }

    @SuppressLint("DefaultLocale")
    private void initAttributeView() {
        editTextLevel.setText(String.format("%d", enemyAttribute.getLevel()));
        for(ElementEnum element: GenshinConstantMeta.ELEMENT_LIST) {
            Objects.requireNonNull(editTextResist.get(element)).setText(
                    String.format("%d", Math.round(100 * enemyAttribute.getResist(element))));
        }
    }


}