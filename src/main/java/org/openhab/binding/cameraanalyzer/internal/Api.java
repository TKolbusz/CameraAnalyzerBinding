package org.openhab.binding.cameraanalyzer.internal;

import java.util.List;

public interface Api {
    CameraStatus getStatus();

    List<CameraEvent> getEvents(long timestamp);

    CameraStatus turnOff();

    CameraStatus turnOn();
}
