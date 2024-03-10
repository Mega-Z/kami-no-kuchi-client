package com.megaz.knk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.megaz.knk.R;
import com.megaz.knk.client.RequestHelper;
import com.megaz.knk.client.ResponseEntity;
import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.constant.ElementEnum;
import com.megaz.knk.exception.RequestErrorException;

import org.checkerframework.checker.units.qual.A;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImageResourceUtils {

    private static final int RETRY = 2;
    private static final int TIMEOUT = 3000;

    public static List<String> getIconResourceList(Context context) {
        Log.i("【资源更新】", "开始获取资源列表");
        String url = context.getString(R.string.server) + context.getString(R.string.api_get_icon_list);
        try {
            return RequestHelper.getIconList(url, RETRY, TIMEOUT);
        } catch (RequestErrorException e) {
            e.printStackTrace();
            Log.e("【资源更新】", "资源列表获取失败");
            throw e;
        }
    }

    public static List<String> checkMissingIcons(Context context, List<String> iconNameList) {
        Path iconDir = Paths.get(context.getFilesDir().toURI()).resolve(context.getString(R.string.dir_icon));
        List<String> missingIconNameList = new ArrayList<>();
        for (String iconName : iconNameList) {
            Path iconPath = iconDir.resolve(iconName);
            if(!Files.exists(iconPath)) {
                missingIconNameList.add(iconName);
            }
        }
        return missingIconNameList;
    }

    public static void getIconBatch(Context context, List<String> iconNameList) {
        String url = context.getString(R.string.server) + context.getString(R.string.api_get_icon_zip);
        Path iconDir = Paths.get(context.getFilesDir().toURI()).resolve(context.getString(R.string.dir_icon));
        if (!Files.exists(iconDir)) {
            try {
                Files.createDirectories(iconDir);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RequestErrorException(e.getMessage());
            }
        }

        try {
            RequestHelper.getIconZipAndSave(url, iconDir, iconNameList, RETRY, TIMEOUT* iconNameList.size());
            Log.d("【资源更新】", "获取成功：" + iconNameList.toString());
        } catch (RequestErrorException e) {
            e.printStackTrace();
            Log.e("【资源更新】", "获取失败：" + iconNameList.toString());
            throw e;
        }

    }

    public static void updateIconResource(Context context, String iconName) {
        String url = context.getString(R.string.server) + context.getString(R.string.api_get_icon) + "?iconName=" + iconName;
        Path iconPath = Paths.get(context.getFilesDir().toURI()).resolve(context.getString(R.string.dir_icon)).resolve(iconName);
        if (Files.exists(iconPath)) {
            Log.d("【资源更新】", "已经存在：" + iconName);
            return;
        }
        Log.d("【资源更新】", "开始获取：" + iconName);
        if (!Files.exists(iconPath.getParent())) {
            try {
                Files.createDirectories(iconPath.getParent());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RequestErrorException(e.getMessage());
            }
        }
        try {
            RequestHelper.getIconAndSave(url, iconPath, RETRY, TIMEOUT);
            Log.d("【资源更新】", "获取成功：" + iconName);
        } catch (RequestErrorException e) {
            e.printStackTrace();
            Log.e("【资源更新】", "获取失败：" + iconName);
            throw e;
        }
    }

    public static Bitmap getIconBitmap(Context context, String iconName) {
        Path iconPath = Paths.get(context.getFilesDir().toURI()).resolve(context.getString(R.string.dir_icon)).resolve(iconName);
        Bitmap bitmap = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(iconPath.toFile());
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            Log.e("【图标获取】", "获取失败：" + iconName);
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
