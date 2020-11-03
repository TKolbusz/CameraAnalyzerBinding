package org.openhab.binding.cameraanalyzer.internal;

import java.util.Collections;
import java.util.List;

public class NotInitializedApi implements Api {
    public NotInitializedApi() {
    }

    @Override
    public CameraStatus getStatus() {
        return CameraStatus.UNKNOWN;
    }

    @Override
    public List<CameraEvent> getEvents(long timestamp) {
        return Collections.emptyList();
    }

    @Override
    public CameraStatus turnOff() {
        return CameraStatus.UNKNOWN;
    }

    @Override
    public CameraStatus turnOn() {
        return CameraStatus.UNKNOWN;
    }
}
