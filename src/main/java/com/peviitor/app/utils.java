package com.peviitor.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class utils {

    public static String get(String urlString, JSONObject... header) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        for (JSONObject h : header) {
            for (String key : h.keySet()) {
                con.setRequestProperty(key, h.getString(key));
            }
        }

        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuffer content = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        con.disconnect();

        return content.toString();
    }
    
    public static String post(String urlString, String data, JSONObject... header) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        String requestBody = data;

        for (JSONObject h : header) {
            for (String key : h.keySet()) {
                con.setRequestProperty(key, h.getString(key));
                if (h.getString(key).equals("application/x-www-form-urlencoded")) {
                    Map<String, String> dataHash = new HashMap<>();

                    JSONObject dataJSON = new JSONObject(data);
                    for (String k : dataJSON.keySet()) {
                        dataHash.put(k, dataJSON.getString(k));
                    }
                    requestBody = dataHash.keySet().stream()
                            .map(key2 -> key2 + "=" + dataHash.get(key2))
                            .collect(Collectors.joining("&"));
                }
            }
        }

        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.getOutputStream().write(requestBody.getBytes(StandardCharsets.UTF_8));

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuffer content = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        con.disconnect();

        return content.toString();
    }
}
