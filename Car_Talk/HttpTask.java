package com.yangproject.embeddedproject.Utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 우민 on 2016-03-15.
 */
public class HttpTask {
    private static HttpTask ourInstance = new HttpTask();
    public static HttpTask getInstance() {
        return ourInstance;
    }
    private HttpTask() { }

    public String getHttpPOSTResult(String urlStr, String condition) {
        String result = null;
        URL url;
        BufferedReader reader;
        StringBuilder stringBuilder = null;

        try {
            url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            OutputStream os = connection.getOutputStream();
            os.write(condition.getBytes("UTF-8"));
            os.flush();
            os.close();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
            result =  stringBuilder.toString();
        }
        catch (Exception exc) { return "{\"status\":\"FAILURE\"}"; }
        return result;
    }
}
