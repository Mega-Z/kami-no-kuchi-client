package com.megaz.knk.client;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.megaz.knk.vo.PlayerProfileVo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
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

    public static <T> ResponseEntity<T> requestSend(String urlString, Class<T> classOfResponse, int retry, int timeout) {
        while(retry > 0) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(timeout);
                connection.connect();

                if(connection.getResponseCode() == 200) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder resString = new StringBuilder();
                    while ((inputLine = bufferedReader.readLine()) != null) {
                        resString.append(inputLine);
                    }
                    Gson gson = new Gson();
                    T body = gson.fromJson(resString.toString(), classOfResponse);
                    return new ResponseEntity<>(connection.getResponseCode(), body);
                } else {
                    return new ResponseEntity<>(connection.getResponseCode());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            retry--;
        }
        return null;
    }

    public static <T> ResponseEntity<ArrayList<T>> requestSendForList(String urlString, Class<T> classOfResponse, int retry, int timeout) {
        while(retry > 0) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(timeout);
                connection.connect();

                if(connection.getResponseCode() == 200) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder resString = new StringBuilder();
                    while ((inputLine = bufferedReader.readLine()) != null) {
                        resString.append(inputLine);
                    }
                    Type type = new TypeToken<ArrayList<JsonObject>>(){}.getType();
                    Gson gson = new Gson();
                    ArrayList<JsonObject> jsonObjectArrayList = gson.fromJson(resString.toString(), type);
                    ArrayList<T> body = new ArrayList<>();
                    for(JsonObject jsonObject:jsonObjectArrayList) {
                        body.add(gson.fromJson(jsonObject, classOfResponse));
                    }
                    return new ResponseEntity<>(connection.getResponseCode(), body);
                } else {
                    return new ResponseEntity<>(connection.getResponseCode());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            retry--;
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean getIconAndSave(String urlString, Path path, int retry) {
        while(retry>0) {
            try {
                URL url = new URL(urlString);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                OutputStream outputStream = new FileOutputStream(path.toFile());
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                inputStream.close();
                return true;

            } catch (IOException e) {
                e.printStackTrace();
                retry--;
            }
        }
        return false;
    }
}
