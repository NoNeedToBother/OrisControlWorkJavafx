package ru.kpfu.itis.paramonov.oriscontrolworkjavafx.http_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpClientImpl implements HttpClient {
    private String token;

    public HttpClientImpl() {
        this(null);
    }
    public HttpClientImpl(String token) {
        this.token = token;
    }


    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String get(String url, Map<String, String> params) {
        String res = null;
        try {
            HttpURLConnection connection;
            if (!params.isEmpty()) {
                String newUrl = addParamsToUrl(url, params);
                connection = getConnection(newUrl, "GET");
            } else {
                connection = getConnection(url, "GET");
            }
            res = getInfo(connection);
        } catch (MalformedURLException e) {
            System.out.println("The URL is invalid");
        } catch (IOException e) {}

        return res;
    }

    @Override
    public String post(String url, Map<String, String> params) {
        String res = null;
        try {
            HttpURLConnection connection = getConnection(url, "POST");
            connection.setDoOutput(true);
            String json = getJson(params);
            writeContent(json, connection);
            res = getInfo(connection);
            connection.disconnect();
        } catch (MalformedURLException e) {
            System.out.println("The URL is invalid");
        } catch (IOException e) {}
        return res;
    }

    @Override
    public String put(String url, Map<String, String> params) {
        String res = null;
        try {
            HttpURLConnection connection = getConnection(url, "PUT");
            connection.setDoOutput(true);
            String json = getJson(params);
            writeContent(json, connection);
            res = getInfo(connection);
            connection.disconnect();
        } catch (MalformedURLException e) {
            System.out.println("The URL is invalid");
        } catch (IOException e) {}

        return res;
    }

    @Override
    public String delete(String url, Map<String, String> params) {
        String res = null;
        try {
            HttpURLConnection connection;
            if (!params.isEmpty()) {
                String newUrl = addParamsToUrl(url, params);
                connection = getConnection(newUrl, "DELETE");
            } else {
                connection = getConnection(url, "DELETE");
            }
            res = getInfo(connection);
        } catch (MalformedURLException e) {
            System.out.println("The URL is invalid");
        } catch (IOException e) {
        }

        return res;
    }

    private String addParamsToUrl(String url, Map<String, String> params) {
        StringBuilder res = new StringBuilder(url);
        res.append("?");
        for (String key : params.keySet()) {
            res.append(key).append("=").append(params.get(key)).append("&");
        }
        return res.substring(0, res.length() - 1);
    }

    private String getJson(Map<String, String> params) {
        StringBuilder json = new StringBuilder("{");
        for (String key: params.keySet()) {
            addKeyToJson(json, key);
            addValueToJson(json, params.get(key));
        }
        json.setCharAt(json.length() - 1, '}');
        return json.toString();
    }

    private void addKeyToJson(StringBuilder target, String key) {
        target.append("\"").append(key).append("\"").append(":");
    }
    private void addValueToJson(StringBuilder target, String value) {
        target.append("\"").append(value).append("\"").append(",");
    }

    private void writeContent(String inputString, HttpURLConnection connection) throws IOException {
        try(OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = inputString.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }
    }

    private String readContent(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String input;
            while ((input = reader.readLine()) != null) {
                content.append(input);
            }
            return content.toString();
        }
    }

    private String getInfo(HttpURLConnection connection) throws IOException{
        StringBuilder info = new StringBuilder();
        info.append(readContent(connection)).append("\n");
        return info.toString();
    }

    private HttpURLConnection getConnection(String urlString, String method)
            throws MalformedURLException, IOException {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {throw e;}
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);

        connection.setRequestMethod(method.toUpperCase());
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", String.format("Bearer %s", token));

        return connection;

    }
}
