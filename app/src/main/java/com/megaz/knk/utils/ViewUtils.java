package com.megaz.knk.utils;

import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class ViewUtils {

    public static boolean isTouchView(View view, MotionEvent event){
        if (view == null || event == null){
            return false;
        }
        int[] leftTop = {0, 0};
        view.getLocationInWindow(leftTop);
        int left = leftTop[0];
        int top = leftTop[1];
        int bottom = top + view.getHeight();
        int right = left + view.getWidth();
        return event.getRawX() > left && event.getRawX() < right
                && event.getRawY() > top && event.getRawY() < bottom;
    }

    public static void updateSeekbarText(SeekBar sb, TextView tv, int progress, String suffix){
        String prevText = (String) tv.getText();
        String newText = progress+suffix;
        float w = (float)tv.getWidth()*newText.length()/prevText.length();
        tv.setText(newText);
        float thumbWidth = sb.getThumb().getBounds().width();
        float thumbLeft = sb.getThumb().getBounds().left;
        float sbLeft = sb.getLeft();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tv.getLayoutParams();
        int marginStart = (int) (sbLeft + thumbLeft + thumbWidth / 2 - w / 2);
        layoutParams.setMarginStart(marginStart);
        tv.setLayoutParams(layoutParams);
    }
}
