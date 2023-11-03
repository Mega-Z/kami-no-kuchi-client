package com.megaz.knk.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.megaz.knk.R;
import com.megaz.knk.manager.ImageResourceManager;
import com.megaz.knk.vo.ConstellationVo;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConstellationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConstellationFragment extends Fragment {

    private ConstellationVo constellationVo;

    public ConstellationFragment() {
        // Required empty public constructor
    }

    public static ConstellationFragment newInstance(ConstellationVo constellationVo) {
        ConstellationFragment fragment = new ConstellationFragment();
        Bundle args = new Bundle();
        args.putSerializable("constellationVo", constellationVo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            constellationVo = (ConstellationVo) getArguments().getSerializable("constellationVo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_constellation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageConsIcon = view.findViewById(R.id.img_cons_icon);
        imageConsIcon.setImageBitmap(ImageResourceManager.getIconBitmap(Objects.requireNonNull(getContext()), constellationVo.getIcon()));
        ImageView imageConsFrame = view.findViewById(R.id.img_cons_frame);
        Bitmap bitmapFrame = ImageResourceManager.getFrameByElement(Objects.requireNonNull(getContext()), constellationVo.getElement());
        imageConsFrame.setImageBitmap(bitmapFrame);
        if(constellationVo.getActive()) {
            imageConsIcon.setAlpha(1f);
            imageConsFrame.setAlpha(1f);
        } else {
            imageConsIcon.setAlpha(0.5f);
            imageConsFrame.setAlpha(0.5f);
        }

    }
}