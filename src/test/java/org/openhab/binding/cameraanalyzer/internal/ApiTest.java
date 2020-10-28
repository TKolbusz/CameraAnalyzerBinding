package org.openhab.binding.cameraanalyzer.internal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ApiTest {

    @Test
    public void getStatusTest_ok() {
        Api api = getApi();
        assertEquals(api.getStatus(), CameraStatus.UNKNOWN);
    }

    @Test
    public void turn_on_ok() {
        Api api = getApi();
        assertEquals(api.turnOn(), CameraStatus.OFF);
    }

    @Test
    public void turn_off_ok() {
        Api api = getApi();
        assertEquals(api.turnOn(), CameraStatus.OFF);
    }

    @Test
    public void get_events() {
        Api api = getApi();
        api.getEvents(0);
    }

    private Api getApi(String host) {
        return new Api(host);
    }

    private Api getApi() {
        return new Api("127.0.0.1:5000");
    }
}
