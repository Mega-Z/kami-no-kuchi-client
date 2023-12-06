package com.megaz.knk.utils;

import android.content.Context;
import android.util.Log;

import com.megaz.knk.R;
import com.megaz.knk.client.RequestHelper;
import com.megaz.knk.client.ResponseEntity;
import com.megaz.knk.dto.MetaDatabaseInfoDto;
import com.megaz.knk.entity.ArtifactCriterion;
import com.megaz.knk.exception.RequestErrorException;
import com.megaz.knk.vo.PlayerProfileVo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RequestUtils {

    public static PlayerProfileVo queryProfile(Context context, String uid) {
        Log.i("【查询面板】","开始获取uid:"+uid+"的面板数据");
        String url = context.getString(R.string.server) + context.getString(R.string.api_query) + "?uid=" + uid;
        ResponseEntity<PlayerProfileVo> response = RequestHelper.requestSend(url, PlayerProfileVo.class, 3, 5000);
        try{
            checkResponse(response);
            PlayerProfileVo playerProfileVo = response.getBody();
            Log.i("【查询面板】","uid:"+uid+"的面板数据获取成功");
            return playerProfileVo;
        } catch (RequestErrorException e) {
            Log.e("【查询面板】","uid:"+uid+"的面板数据获取失败");
            throw e;
        }
    }

    public static PlayerProfileVo updateProfile(Context context, String uid) {
        Log.i("【更新面板】","开始更新uid:"+uid+"的面板数据");
        String url = context.getString(R.string.server) + context.getString(R.string.api_update) + "?uid=" + uid;
        ResponseEntity<PlayerProfileVo> response = RequestHelper.requestSend(url, PlayerProfileVo.class, 3, 5000);
        try{
            checkResponse(response);
            PlayerProfileVo playerProfileVo = response.getBody();
            Log.i("【更新面板】","uid:"+uid+"的面板数据更新成功");
            return playerProfileVo;
        } catch (RequestErrorException e) {
            Log.e("【更新面板】","uid:"+uid+"的面板数据更新失败");
            throw e;
        }
    }

    public static MetaDatabaseInfoDto getMetaDatabaseInfo(Context context) {
        Log.i("【更新元数据】","开始获取数据库信息");
        String url = context.getString(R.string.server) + context.getString(R.string.api_get_database_info);
        ResponseEntity<MetaDatabaseInfoDto> response = RequestHelper.requestSend(url, MetaDatabaseInfoDto.class, 3, 5000);
        try{
            checkResponse(response);
            MetaDatabaseInfoDto metaDatabaseInfoDto = response.getBody();
            Log.i("【更新元数据】","获取数据库信息成功");
            return metaDatabaseInfoDto;
        } catch (RequestErrorException e) {
            Log.e("【更新元数据】","获取数据库信息失败");
            throw e;
        }
    }

    public static <T> ArrayList<T> pageQueryMetaData(Context context, Class<T> entityClass, String tableName, int offset, int pageSize) {
        Log.i("【更新元数据】","开始同步元数据"+String.format("tableName: %s, offset: %d, pageSize: %d", tableName, offset, pageSize));
        String url = context.getString(R.string.server) + context.getString(R.string.api_page_sync_database)
                + "?tableName=" + tableName +"&offset=" + offset +"&pageSize="+pageSize;
        ResponseEntity<ArrayList<T>> response = RequestHelper.requestSendForList(url, entityClass, 3, 5000);
        try{
            checkResponse(response);
            ArrayList<T> pageData = response.getBody();
            Log.i("【更新元数据】","同步元数据成功");
            return pageData;
        } catch (RequestErrorException e) {
            Log.e("【更新元数据】","同步元数据失败");
            throw e;
        }
    }


    private static <T> void checkResponse(ResponseEntity<T> responseEntity) {
        if(responseEntity == null) {
            throw new RequestErrorException("网络错误，重试超限");
        }
        if(responseEntity.getCode() == 404) {
            throw new RequestErrorException("uid不存在，米哈游说的");
        }
        if(responseEntity.getCode() == 500) {
            throw new RequestErrorException("服务器内部错误");
        }
        if(responseEntity.getCode() == 503) {
            throw new RequestErrorException("数据源访问错误");
        }
        if(responseEntity.getCode() != 200) {
            throw new RequestErrorException("未知错误，状态码："+responseEntity.getCode());
        }
    }
}
