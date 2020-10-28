package org.openhab.binding.cameraanalyzer.internal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class Api {
    private final String host;

    public Api(String host) {
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
            ignored.printStackTrace();
        }
        return CameraStatus.UNKNOWN;
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
            return CameraStatus.OFF;
        }
        return CameraStatus.ON;
    }

    public List<CameraEvent> getEvents(long timestamp) {
        String urlText = String.format("%s/events?from=%s", host, timestamp);
        try {
            String response = execute(urlText, "GET");
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return Arrays.asList(new CameraEvent(0, 0.44, "car", "A", "192.168.1.110"));
    }

    private String execute(String url, String requestMethod) {
        try {
            URL urlForGetRequest = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
            connection.setRequestMethod(requestMethod);
            int responseCode = connection.getResponseCode();
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
