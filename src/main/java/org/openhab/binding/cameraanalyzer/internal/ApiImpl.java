package org.openhab.binding.cameraanalyzer.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApiImpl implements Api {
    private final String host;

    public ApiImpl(String host) {
        if (host != null && !host.contains("http"))
            host = "http://" + host;
        this.host = host;
    }

    public CameraStatus getStatus() {
        String urlText = String.format("%s/state", host);
        try {
            String response = execute(urlText, "GET");
            String responseLowerCase = response.toLowerCase();
            if (responseLowerCase.contains("on"))
                return CameraStatus.ON;
        } catch (Exception ignored) {
        }
        return CameraStatus.UNKNOWN;
    }

    @Override
    public long getTimestamp() throws IOException {
        String urlText = String.format("%s/timestamp", host);
        try {
            String result = execute(urlText, "GET");
            return Long.parseLong(result.substring(1).split("\\.")[0]);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public CameraStatus turnOn() {
        String urlText = String.format("%s/on", host);
        try {
            String response = execute(urlText, "POST");
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return CameraStatus.OFF;
        }
        return CameraStatus.ON;
    }

    public CameraStatus turnOff() {
        String urlText = String.format("%s/off", host);
        try {
            String response = execute(urlText, "POST");
        } catch (Exception ignored) {
            return CameraStatus.ON;
        }
        return CameraStatus.OFF;
    }

    public List<CameraEvent> getEvents(long dateFrom) throws IOException {
        String urlText = String.format("%s/events?date_from=%d", host, dateFrom);
        List<CameraEvent> results = new ArrayList<>();
        try {
            String response = execute(urlText, "GET");
            JsonParser jsonParser = new JsonParser();
            for (JsonElement jsonElement : jsonParser.parse(response).getAsJsonArray()) {
                JsonObject object = jsonElement.getAsJsonObject();
                JsonElement typeElement = object.get("type");
                if (typeElement.isJsonNull())
                    continue;
                String type = typeElement.getAsString();
                long id = object.get("id").getAsLong();
                long timestamp = object.get("timestamp").getAsLong();
                String device = object.get("camera").isJsonNull() ? null : object.get("camera").getAsString();
                double confidence = object.get("confidence").isJsonNull() ? 1 : object.get("confidence").getAsDouble();
                String area = object.get("area").isJsonNull() ? "A" : object.get("area").getAsString();
                results.add(new CameraEvent(
                        id,
                        timestamp,
                        confidence,
                        type,
                        area,
                        device
                ));
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
        return results;
    }

    private String execute(String url, String requestMethod) {
        try {
            URL urlForGetRequest = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
            connection.setRequestMethod(requestMethod);
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            String s = responseMessage.toLowerCase();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer response = new StringBuffer();
                String readLine;
                while ((readLine = in.readLine()) != null) {
                    response.append(readLine);
                }
                in.close();
                return response.toString();
            } else
                throw new RuntimeException("response code: " + responseCode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
