package com.megaz.knk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.megaz.knk.R;
import com.megaz.knk.client.RequestHelper;
import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.constant.ElementEnum;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ImageResourceUtils {

    public static List<String> getIconResourceList(Context context){
        String url = context.getString(R.string.server) + context.getString(R.string.api_get_icon_list);
        return RequestHelper.getIconList(url);
    }


    public static boolean updateIconResource(Context context, List<String> iconNameList){
        for(String iconName:iconNameList) {
            String url = context.getString(R.string.server) + context.getString(R.string.api_get_icon) + "?iconName=" + iconName;
            Path iconPath = Paths.get(context.getFilesDir().toURI()).resolve(context.getString(R.string.dir_icon)).resolve(iconName);
            if(Files.exists(iconPath)) {
                Log.d("【资源更新】", "已经存在："+iconName);
                continue;
            }
            if(!Files.exists(iconPath.getParent())) {
                try {
                    Files.createDirectories(iconPath.getParent());
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            if(!RequestHelper.getIconAndSave(url, iconPath, 3)){
                Log.e("【资源更新】", "获取失败："+iconName);
                return false;
            }
            Log.d("【资源更新】", "获取成功："+iconName);
        }
        return true;
    }

    public static Bitmap getIconBitmap(Context context, String iconName) {
        Path iconPath = Paths.get(context.getFilesDir().toURI()).resolve(context.getString(R.string.dir_icon)).resolve(iconName);
        Bitmap bitmap = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(iconPath.toFile());
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            Log.e("【图标获取】", "获取失败："+iconName);
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getBackgroundByElement(Context context, ElementEnum element) {
        String id = "bg_" + element.getVal();
        InputStream inputStream;
        inputStream = context.getResources().openRawResource(context.getResources().getIdentifier(id, "drawable", context.getPackageName()));
        return BitmapFactory.decodeStream(inputStream);
    }

    public static Bitmap getFrameByElement(Context context, ElementEnum element) {
        String id = "frame_" + element.getVal();
        InputStream inputStream;
        inputStream = context.getResources().openRawResource(context.getResources().getIdentifier(id, "drawable", context.getPackageName()));
        return BitmapFactory.decodeStream(inputStream);
    }

    public static Bitmap getElementIcon(Context context, ElementEnum element) {
        String id = "ic_" + element.getVal();
        InputStream inputStream;
        inputStream = context.getResources().openRawResource(context.getResources().getIdentifier(id, "drawable", context.getPackageName()));
        return BitmapFactory.decodeStream(inputStream);
    }

    public static Bitmap getArtifactPositionIcon(Context context, ArtifactPositionEnum position) {
        String id = "ic_" + position.getVal();
        InputStream inputStream;
        inputStream = context.getResources().openRawResource(context.getResources().getIdentifier(id, "drawable", context.getPackageName()));
        return BitmapFactory.decodeStream(inputStream);
    }

}
