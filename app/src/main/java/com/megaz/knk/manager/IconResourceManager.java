package com.megaz.knk.manager;

import android.content.Context;

import com.megaz.knk.R;
import com.megaz.knk.client.RequestHelper;

import java.util.List;

public class IconResourceManager {

    public static List<String> getIconResourceList(Context context){
        String url = context.getString(R.string.server) + context.getString(R.string.api_get_icon_list);
        return RequestHelper.getIconList(url);
    }

}
