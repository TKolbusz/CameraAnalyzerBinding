package org.openhab.binding.cameraanalyzer.internal;

//@RunWith(JUnit4.class)
public class ApiTest {
//
//    @Test
//    public void getStatusTest_ok() {
//        ApiImpl api = getApi();
//        assertEquals(api.getStatus(), CameraStatus.UNKNOWN);
//    }
//
//    @Test
//    public void turn_on_ok() {
//        ApiImpl api = getApi();
//        assertEquals(api.turnOn(), CameraStatus.OFF);
//    }
//
//    @Test
//    public void turn_off_ok() {
//        ApiImpl api = getApi();
//        assertEquals(api.turnOn(), CameraStatus.OFF);
//    }

//    @Test
//    public void get_events() throws IOException {
//        ApiImpl api = getApi();
//        api.getTimestamp();
//    }

    private ApiImpl getApi(String host) {
        return new ApiImpl(host);
    }

    private ApiImpl getApi() {
        return new ApiImpl("127.0.0.1:5000");
    }
}
