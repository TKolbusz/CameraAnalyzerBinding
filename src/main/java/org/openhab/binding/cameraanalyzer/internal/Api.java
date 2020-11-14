package org.openhab.binding.cameraanalyzer.internal;

import java.io.IOException;
import java.util.List;

public interface Api {
    CameraStatus getStatus();

    List<CameraEvent> getEvents(long dateFrom) throws IOException;

    CameraStatus turnOff();

    CameraStatus turnOn();

    long getTimestamp() throws IOException;
}
