package org.openhab.binding.cameraanalyzer.internal;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DistinctEventApiImpl implements Api {
    private final Api api;
    private long maxEventId;

    public DistinctEventApiImpl(Api api) {
        this.api = api;
    }

    @Override
    public CameraStatus getStatus() {
        return api.getStatus();
    }

    @Override
    public List<CameraEvent> getEvents(long dateFrom) throws IOException {
        List<CameraEvent> events = api.getEvents(dateFrom);
        List<CameraEvent> eventsFiltered = events.stream()
                .filter(e -> e.getId() > maxEventId)
                .collect(Collectors.toList());
        maxEventId = eventsFiltered.stream().map(CameraEvent::getId).max(Comparator.comparing(id -> id)).orElse(maxEventId);
        return eventsFiltered;
    }

    @Override
    public CameraStatus turnOff() {
        return api.turnOff();
    }

    @Override
    public CameraStatus turnOn() {
        return api.turnOn();
    }

    @Override
    public long getTimestamp() throws IOException {
        return api.getTimestamp();
    }
}
