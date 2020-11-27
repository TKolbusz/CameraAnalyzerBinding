package org.openhab.binding.cameraanalyzer.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelayedDuplicateEventApi implements Api {
    private final Api api;
    private final Map<EventDuplicationData, Long> eventTimestampMap = new HashMap<>();
    private final long delay;

    public DelayedDuplicateEventApi(Api api, long delay) {
        this.api = api;
        this.delay = delay;
    }

    @Override
    public List<CameraEvent> getEvents(long dateFrom) throws IOException {
        List<CameraEvent> events = api.getEvents(dateFrom);
        List<CameraEvent> eventResult = new ArrayList<>();
        for (CameraEvent event : events) {
            EventDuplicationData eventAsDuplicateData = new EventDuplicationData(event.getType(), event.getArea());
            Long lastEventOfType = eventTimestampMap.get(eventAsDuplicateData);
            long currentTime = System.currentTimeMillis();
            if (lastEventOfType != null) {
                if (lastEventOfType + delay < currentTime) {
                    eventResult.add(event);
                    eventTimestampMap.put(eventAsDuplicateData, currentTime);
                }
            } else {
                eventResult.add(event);
                eventTimestampMap.put(eventAsDuplicateData, currentTime);
            }
        }
        return eventResult;
    }

    @Override
    public long getTimestamp() throws IOException {
        return api.getTimestamp();
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

    private static class EventDuplicationData {
        private final String type;
        private final String area;

        public EventDuplicationData(String type, String area) {
            this.type = type;
            this.area = area;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EventDuplicationData that = (EventDuplicationData) o;

            if (type != null ? !type.equals(that.type) : that.type != null) return false;
            return area != null ? area.equals(that.area) : that.area == null;
        }

        @Override
        public int hashCode() {
            int result = type != null ? type.hashCode() : 0;
            result = 31 * result + (area != null ? area.hashCode() : 0);
            return result;
        }
    }
}
