package com.megaz.knk.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.megaz.knk.exception.RequestException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RequestHelper {

    @SuppressWarnings("unchecked")
    public static List<String> getIconList(String urlString, int retry, int timeout) {
        while (retry > 0) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(timeout);
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder resString = new StringBuilder();
                    while ((inputLine = bufferedReader.readLine()) != null) {
                        resString.append(inputLine);
                    }
                    Gson gson = new Gson();
                    return gson.fromJson(resString.toString(), ArrayList.class);
                }  else {
                    throw new RequestException(connection.getResponseMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                if(retry > 1) {
                    retry--;
                } else {
                    throw new RequestException(e.getMessage());
                }
            }
        }
        throw new RequestException("重试超限");
    }

    public static void getIconAndSave(String urlString, Path path, int retry, int timeout) {
        while (retry > 0) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(timeout);
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
                return;
            } catch (IOException e) {
                e.printStackTrace();
                if(retry > 1) {
                    retry--;
                } else {
                    throw new RequestException(e.getMessage());
                }
            }
        }
        throw new RequestException("重试超限");
    }


    public static void getIconZipAndSave(String urlString, Path iconDir, List<String> iconNameList, int retry, int timeout) {
        while (retry > 0) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setConnectTimeout(timeout);
                connection.connect();

                String jsonStringIconNameList = new Gson().toJson(iconNameList);
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(jsonStringIconNameList);
                dataOutputStream.close();

                InputStream inputStream = connection.getInputStream();
                ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                ZipEntry zipEntry;
                while((zipEntry = zipInputStream.getNextEntry()) != null) {
                    String iconName = zipEntry.getName();
                    OutputStream outputStream = new FileOutputStream(iconDir.resolve(iconName).toFile());
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    zipInputStream.closeEntry();
                }
                zipInputStream.close();
                inputStream.close();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                if(retry > 1) {
                    retry--;
                } else {
                    throw new RequestException(e.getMessage());
                }
            }
        }
        throw new RequestException("重试超限");
    }

    public static <T> ResponseEntity<T> requestSend
            (String urlString, Class<T> classOfResponse, int retry, int timeout)  {
        while (retry > 0) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(timeout);
                connection.connect();

                if (connection.getResponseCode() == 200) {
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

            } catch (Exception e) {
                e.printStackTrace();
                if(retry > 1) {
                    retry--;
                } else {
                    throw new RequestException(e.getMessage());
                }
            }
        }
        throw new RequestException("重试超限");
    }

    public static <T> ResponseEntity<ArrayList<T>> requestSendForList
            (String urlString, Class<T> classOfResponse, int retry, int timeout) {
        while (retry > 0) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(timeout);
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder resString = new StringBuilder();
                    while ((inputLine = bufferedReader.readLine()) != null) {
                        resString.append(inputLine);
                    }
                    Type type = new TypeToken<ArrayList<JsonObject>>() {
                    }.getType();
                    Gson gson = new Gson();

                    ArrayList<JsonObject> jsonObjectArrayList = gson.fromJson(resString.toString(), type);
                    ArrayList<T> body = new ArrayList<>();
                    for (JsonObject jsonObject : jsonObjectArrayList) {
                        body.add(gson.fromJson(jsonObject, classOfResponse));
                    }
                    return new ResponseEntity<>(connection.getResponseCode(), body);
                } else {
                    return new ResponseEntity<>(connection.getResponseCode());
                }

            } catch (Exception e) {
                e.printStackTrace();
                if(retry > 1) {
                    retry--;
                } else {
                    throw new RequestException(e.getMessage());
                }
            }
        }
        throw new RequestException("重试超限");
    }


}
