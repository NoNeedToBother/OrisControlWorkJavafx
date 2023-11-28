package ru.kpfu.itis.paramonov.oriscontrolworkjavafx.http_client;

import java.util.Map;

public interface HttpClient {
    String get(String url, Map<String, String> params);
    String post(String url, Map<String, String> params);
    String put(String url, Map<String, String> params);
    String delete(String url, Map<String, String> params);
}