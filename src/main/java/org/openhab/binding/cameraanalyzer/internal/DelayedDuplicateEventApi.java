package org.openhab.binding.cameraanalyzer.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelayedDuplicateEventApi implements Api {
    private final Api api;
    private final Map<String, Long> eventTimestampMap = new HashMap<>();
    private final long delay;

    public DelayedDuplicateEventApi(Api api, long delay) {
        this.api = api;
        this.delay = delay;
    }

    @Override
    public List<CameraEvent> getEvents(long timestamp) {
        List<CameraEvent> events = api.getEvents(timestamp);
        List<CameraEvent> eventResult = new ArrayList<>();
        for (CameraEvent event : events) {
            Long lastEventOfType = eventTimestampMap.get(event.getType());
            long currentTime = System.currentTimeMillis();
            if (lastEventOfType != null) {
                if (lastEventOfType + delay < currentTime) {
                    eventResult.add(event);
                    eventTimestampMap.put(event.getType(), currentTime);
                }
            } else {
                eventResult.add(event);
                eventTimestampMap.put(event.getType(), currentTime);
            }
        }
        return eventResult;
    }

    @Override
    public CameraStatus getStatus() {
        return api.getStatus();
    }

    @Override
    public CameraStatus turnOff() {
        return api.turnOff();
    }

    @Override
    public CameraStatus turnOn() {
        return api.turnOn();
    }
}
