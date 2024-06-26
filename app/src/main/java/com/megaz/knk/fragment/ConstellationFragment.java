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
import com.megaz.knk.utils.ImageResourceUtils;
import com.megaz.knk.vo.ConstellationVo;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConstellationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConstellationFragment extends BaseFragment {
    private ConstellationVo constellationVo;

    private ImageView imageConsIcon, imageConsFrame;

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
    protected void initView(@NonNull View view) {
        super.initView(view);
        requireContext();
        imageConsIcon = view.findViewById(R.id.img_cons_icon);
        imageConsFrame = view.findViewById(R.id.img_cons_frame);
        updateViews(constellationVo);
    }

    public void updateViews(@NonNull ConstellationVo constellationVo) {
        this.constellationVo = constellationVo;
        imageConsIcon.setImageBitmap(ImageResourceUtils.getIconBitmap(requireContext(), constellationVo.getIcon()));
        Bitmap bitmapFrame = ImageResourceUtils.getFrameByElement(requireContext(), constellationVo.getElement());
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