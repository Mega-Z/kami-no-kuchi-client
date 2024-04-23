package com.megaz.knk.manager;

import android.content.Context;
import android.util.Log;

import com.megaz.knk.R;
import com.megaz.knk.client.RequestHelper;
import com.megaz.knk.client.ResponseEntity;
import com.megaz.knk.constant.ProfileRequestErrorEnum;
import com.megaz.knk.dto.PlayerProfileDto;
import com.megaz.knk.exception.ProfileRequestException;

public class ProfileQueryManager {
    private Context context;

    public ProfileQueryManager(Context context) {
        this.context = context;
    }

    public PlayerProfileDto queryPlayerProfileDto(String uid) {
        Log.i("【查询面板】", "开始获取uid:" + uid + "的面板数据");
        String url = context.getString(R.string.server) + context.getString(R.string.api_query) + "?uid=" + uid;
        try {
            ResponseEntity<PlayerProfileDto> response = RequestHelper.requestSend(url, PlayerProfileDto.class, 3, 5000);
            checkResponse(response);
            PlayerProfileDto playerProfileDto = response.getBody();
            Log.i("【查询面板】", "uid:" + uid + "的面板数据获取成功");
            return playerProfileDto;
        } catch (ProfileRequestException e) {
            Log.e("【查询面板】", "uid:" + uid + "的面板数据获取失败");
            throw e;
        }
    }

    public PlayerProfileDto updatePlayerProfileDto(String uid) {
        Log.i("【更新面板】", "开始获取uid:" + uid + "的面板数据");
        String url = context.getString(R.string.server) + context.getString(R.string.api_update) + "?uid=" + uid;
        try {
            ResponseEntity<PlayerProfileDto> response = RequestHelper.requestSend(url, PlayerProfileDto.class, 3, 5000);
            checkResponse(response);
            PlayerProfileDto playerProfileDto = response.getBody();
            Log.i("【更新面板】", "uid:" + uid + "的面板数据获取成功");
            return playerProfileDto;
        } catch (ProfileRequestException e) {
            Log.e("【更新面板】", "uid:" + uid + "的面板数据获取失败");
            throw e;
        }
    }

    private <T> void checkResponse(ResponseEntity<T> responseEntity) {
        if (responseEntity == null) {
            throw new ProfileRequestException(ProfileRequestErrorEnum.TIMEOUT);
        }
        if (responseEntity.getCode() == 404) {
            throw new ProfileRequestException(ProfileRequestErrorEnum.UID_NOT_FOUND);
        }
        if (responseEntity.getCode() == 500) {
            throw new ProfileRequestException(ProfileRequestErrorEnum.SERVER_ERROR);
        }
        if (responseEntity.getCode() == 501) {
            throw new ProfileRequestException(ProfileRequestErrorEnum.PROFILE_NOT_AVAILABLE);
        }
        if (responseEntity.getCode() == 503) {
            throw new ProfileRequestException(ProfileRequestErrorEnum.DATASOURCE_ERROR);
        }
        if (responseEntity.getCode() != 200) {
            throw new ProfileRequestException("未知错误，状态码：" + responseEntity.getCode());
        }
    }
}
