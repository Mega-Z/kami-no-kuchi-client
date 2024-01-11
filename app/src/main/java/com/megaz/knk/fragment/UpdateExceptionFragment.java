package com.megaz.knk.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.megaz.knk.R;
import com.megaz.knk.activity.FightEffectDetailActivity;
import com.megaz.knk.computation.BuffInputParam;
import com.megaz.knk.utils.DynamicStyleUtils;
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.BuffVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateExceptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateExceptionFragment extends DialogFragment {
    private Button buttonRetry, buttonExit;
    private UpdateExceptionFragmentListener listener;

    public UpdateExceptionFragment() {
        // Required empty public constructor
    }

    public static UpdateExceptionFragment newInstance() {
        return new UpdateExceptionFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (UpdateExceptionFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(requireActivity().toString() + " must implement UpdateExceptionFragmentListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_exception, container, false);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonRetry = view.findViewById(R.id.btn_retry);
        buttonRetry.setOnClickListener(new RetryOnClickListener());
        buttonExit = view.findViewById(R.id.btn_exit);
        buttonExit.setOnClickListener(new ExitOnClickListener());
        Objects.requireNonNull(getDialog()).requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
    }

    private class RetryOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Objects.requireNonNull(getDialog()).dismiss();
            if (listener != null)
                listener.onRetryClicked();
        }
    }

    private class ExitOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Objects.requireNonNull(getDialog()).dismiss();
            if (listener != null)
                listener.onExitClicked();
        }
    }

    public interface UpdateExceptionFragmentListener {
        void onRetryClicked();
        void onExitClicked();
    }
}