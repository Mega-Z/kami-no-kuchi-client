package com.megaz.knk.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.megaz.knk.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaimonWaitingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaimonWaitingFragment extends Fragment {

    private ObjectAnimator animatorLeft, animatorRight;
    private ImageView imgPaimon, imgLeftEye, imgRightEye;

    public PaimonWaitingFragment() {
    }

    public static PaimonWaitingFragment newInstance() {
        return new PaimonWaitingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_paimon_waiting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgPaimon = view.findViewById(R.id.img_paimon);
        imgLeftEye = view.findViewById(R.id.paimon_eye_l);
        imgRightEye = view.findViewById(R.id.paimon_eye_r);
        animatorLeft = ObjectAnimator.ofFloat(imgLeftEye, "rotation", 360f);
        animatorLeft.setDuration(1000);
        animatorLeft.setRepeatCount(ValueAnimator.INFINITE);
        animatorRight = ObjectAnimator.ofFloat(imgRightEye, "rotation", -360f);
        animatorRight.setDuration(1000);
        animatorRight.setRepeatCount(ValueAnimator.INFINITE);
    }

    public void startWaiting() {
        imgPaimon.setImageResource(R.drawable.paimon_waiting);
        imgPaimon.setVisibility(View.VISIBLE);
        imgLeftEye.setVisibility(View.VISIBLE);
        imgRightEye.setVisibility(View.VISIBLE);
        animatorLeft.start();
        animatorRight.start();
    }

    public void showError() {
        animatorLeft.cancel();
        animatorRight.cancel();
        imgLeftEye.setVisibility(View.INVISIBLE);
        imgRightEye.setVisibility(View.INVISIBLE);
        imgPaimon.setImageResource(R.drawable.paimon_error);
        imgPaimon.setVisibility(View.VISIBLE);
    }

    public void hide() {
        animatorLeft.cancel();
        animatorRight.cancel();
        imgLeftEye.setVisibility(View.INVISIBLE);
        imgRightEye.setVisibility(View.INVISIBLE);
        imgPaimon.setVisibility(View.INVISIBLE);

    }
}