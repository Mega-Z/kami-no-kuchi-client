package com.megaz.knk.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.WorkerThread;

import com.megaz.knk.R;
import com.megaz.knk.bo.ArtifactKey;
import com.megaz.knk.client.RequestHelper;
import com.megaz.knk.client.ResponseEntity;
import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.constant.AttributeEnum;
import com.megaz.knk.dao.ArtifactDexDao;
import com.megaz.knk.dao.CharacterDexDao;
import com.megaz.knk.dao.PromoteAttributeDao;
import com.megaz.knk.dao.WeaponDexDao;
import com.megaz.knk.dto.MetaDatabaseInfoDto;
import com.megaz.knk.entity.ArtifactDex;
import com.megaz.knk.entity.CharacterDex;
import com.megaz.knk.entity.PromoteAttribute;
import com.megaz.knk.entity.WeaponDex;
import com.megaz.knk.exception.MetaDataQueryException;
import com.megaz.knk.exception.RequestException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @WorkerThread
    public static CharacterDex queryCharacterDex(CharacterDexDao characterDexDao, String characterId) {
        List<CharacterDex> characterDexList = characterDexDao.selectByCharacterId(characterId);
        if (characterDexList.size() != 1) {
            throw new MetaDataQueryException("character_dex");
        }
        return characterDexList.get(0);
    }

    @WorkerThread
    public static WeaponDex queryWeaponDex(WeaponDexDao weaponDexDao, String weaponId) {
        List<WeaponDex> weaponDexList = weaponDexDao.selectByWeaponId(weaponId);
        if (weaponDexList.size() != 1) {
            throw new MetaDataQueryException("weapon_dex");
        }
        return weaponDexList.get(0);
    }

    @WorkerThread
    public static Map<AttributeEnum, Double> queryPromoteAttribute(PromoteAttributeDao promoteAttributeDao, String promoteId, Integer phase) {
        Map<AttributeEnum, Double> attributeMap = new HashMap<>();
        List<PromoteAttribute> promoteAttributeList = promoteAttributeDao.selectByPromoteIdAndPhase(promoteId, phase);
        for(PromoteAttribute promoteAttribute:promoteAttributeList) {
            attributeMap.put(promoteAttribute.getAttribute(), promoteAttribute.getValue());
        }
        return attributeMap;
    }

}
