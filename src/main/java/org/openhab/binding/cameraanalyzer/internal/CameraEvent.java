package org.openhab.binding.cameraanalyzer.internal;

public class CameraEvent {
    private long id;
    private long timestamp;
    private double confidence;
    private String type;
    private String area;
    private String device;

    public CameraEvent(long id, long timestamp, double confidence, String type, String area, String device) {
        this.id = id;
        this.timestamp = timestamp;
        this.confidence = confidence;
        this.type = type;
        this.area = area;
        this.device = device;
    }

    public CameraEvent() {
    }

    @Override
    public String toString() {
        return device + ";" + area + ';' + type;
    }

    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
