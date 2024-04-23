package com.megaz.knk.utils;

import android.content.Context;
import android.util.Log;

import com.megaz.knk.R;
import com.megaz.knk.client.RequestHelper;
import com.megaz.knk.client.ResponseEntity;
import com.megaz.knk.dto.MetaDatabaseInfoDto;
import com.megaz.knk.exception.RequestException;

import java.util.ArrayList;

public class MetaDataUtils {

    private static final int RETRY = 3;
    private static final int TIMEOUT = 5000;

    public static MetaDatabaseInfoDto getMetaDatabaseInfo(Context context) {
        Log.i("【更新元数据】","开始获取数据库信息");
        String url = context.getString(R.string.server) + context.getString(R.string.api_get_database_info);
        try{
            ResponseEntity<MetaDatabaseInfoDto> response = RequestHelper.requestSend(url, MetaDatabaseInfoDto.class, RETRY, TIMEOUT);
            MetaDatabaseInfoDto metaDatabaseInfoDto = response.getBody();
            Log.i("【更新元数据】","获取数据库信息成功");
            return metaDatabaseInfoDto;
        } catch (RequestException e) {
            e.printStackTrace();
            Log.e("【更新元数据】","同步元数据失败");
            throw e;
        }
    }

    public static <T> ArrayList<T> pageQueryMetaData(Context context, Class<T> entityClass, String tableName, int offset, int pageSize) {
        Log.i("【更新元数据】","开始同步元数据"+String.format("tableName: %s, offset: %d, pageSize: %d", tableName, offset, pageSize));
        String url = context.getString(R.string.server) + context.getString(R.string.api_page_sync_database)
                + "?tableName=" + tableName +"&offset=" + offset +"&pageSize="+pageSize;
        try{
            ResponseEntity<ArrayList<T>> response = RequestHelper.requestSendForList(url, entityClass, RETRY, TIMEOUT);
            ArrayList<T> pageData = response.getBody();
            Log.i("【更新元数据】","同步元数据成功");
            return pageData;
        } catch (RequestException e) {
            e.printStackTrace();
            Log.e("【更新元数据】","同步元数据失败");
            throw e;
        }
    }
}
