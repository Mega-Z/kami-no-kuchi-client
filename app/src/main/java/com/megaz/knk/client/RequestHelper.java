package com.megaz.knk.client;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RequestHelper {

    @SuppressWarnings("unchecked")
    public static List<String> getIconList(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder resString = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null) {
                resString.append(inputLine);
            }
            Gson gson = new Gson();
            List<String> iconList = gson.fromJson(resString.toString(), ArrayList.class);
            return iconList;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static void getIconAndSave(String iconName, String path) {

    }
}
