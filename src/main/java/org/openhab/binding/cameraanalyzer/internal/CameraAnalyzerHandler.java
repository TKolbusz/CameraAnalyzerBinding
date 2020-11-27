/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 * <p>
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.cameraanalyzer.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Collections.emptyList;
import static org.openhab.binding.cameraanalyzer.internal.CameraAnalyzerBindingConstants.EVENT_CHANNEL;
import static org.openhab.binding.cameraanalyzer.internal.CameraAnalyzerBindingConstants.POWER_CHANNEL;

/**
 * The {@link CameraAnalyzerHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Markiewi - Initial contribution
 */
@NonNullByDefault
public class CameraAnalyzerHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(CameraAnalyzerHandler.class);

    private @Nullable CameraAnalyzerConfiguration config;
    private Api api = new NotInitializedApi();
    private AtomicBoolean stop = new AtomicBoolean(false);
    private @Nullable Thread runningThread = null;

    public CameraAnalyzerHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        stop.set(true);
        logger.debug("Initialing Camera Analyzer");
        config = getConfigAs(CameraAnalyzerConfiguration.class);

        updateStatus(ThingStatus.UNKNOWN);
        if (config.api_url != null && !config.api_url.isEmpty()) {
            this.api = createApi();
            if (runningThread == null) {
                runningThread = new Thread(() -> {
                    long refresh = getRefresh();
                    sleep(refresh);
                    stop.set(false);
                    Long timeDiffBetweenServer = null;
                    Long lastRequestAt = null;
                    while (!stop.get()) {
                        while (timeDiffBetweenServer == null && !stop.get()) {
                            try {
                                long localTimeStart = System.currentTimeMillis();
                                long serverTimeStart = getServerTime();
                                timeDiffBetweenServer = localTimeStart - serverTimeStart + 1000;
                            } catch (IOException e) {
                                sleep(5000);
                            }
                        }
                        if (stop.get())
                            break;
                        if (lastRequestAt == null)
                            lastRequestAt = System.currentTimeMillis();
                        long requestAt = System.currentTimeMillis();
                        try {
                            List<CameraEvent> events = api.getEvents(lastRequestAt - timeDiffBetweenServer);
                            updateState(events, null);
                        } catch (IOException exception) {
                            api = createApi();
                            timeDiffBetweenServer = null;
                            lastRequestAt = null;
                            updateState(emptyList(), exception);
                            continue;
                        }
                        long sleepTime = refresh - (System.currentTimeMillis() - requestAt);
                        lastRequestAt = System.currentTimeMillis();
                        if (sleepTime > 0)
                            sleep(sleepTime);
                    }
                });
                runningThread.start();
            }
        }
    }

    private Api createApi() {
        return new DistinctEventApiImpl(new DelayedDuplicateEventApi(new ApiImpl(config.api_url), getSameEventsDelay()));
    }

    private long getServerTime() throws IOException {
        return api.getTimestamp();
    }

    private void updateState(List<CameraEvent> events, @Nullable Exception e) {
        if (e == null) {
            for (CameraEvent event : events) {
                updateState(EVENT_CHANNEL, new StringType(event.toString()));
            }
            updateStatus(ThingStatus.ONLINE);
        } else {
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    @Override
    public void handleRemoval() {
        super.handleRemoval();
        stop.set(true);
        runningThread = null;
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        super.handleConfigurationUpdate(configurationParameters);
        stop.set(true);
        runningThread = null;
    }

    @Override
    public void dispose() {
        stop.set(true);
        runningThread = null;
    }

    private long getRefresh() {
        return config.refresh != null ? config.refresh : 1000;
    }

    private long getSameEventsDelay() {
        return config.duplicate_delay != null ? config.duplicate_delay : 5000;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (POWER_CHANNEL.equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                if (command == OnOffType.ON) {
                    updateStatus(api.turnOn());
                } else if (command == OnOffType.OFF)
                    updateStatus(api.turnOff());
            }
        }
        if (EVENT_CHANNEL.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                updateStatus(api.getStatus());
            }
        }
    }

    private void updateStatus(CameraStatus status) {
        if (status == CameraStatus.ON) {
            updateState(POWER_CHANNEL, OnOffType.from(true));
            updateStatus(ThingStatus.ONLINE);
        } else if (status == CameraStatus.OFF) {
            updateState(POWER_CHANNEL, OnOffType.from(false));
            updateStatus(ThingStatus.OFFLINE);
        } else {
            updateState(POWER_CHANNEL, OnOffType.from(false));
            updateStatus(ThingStatus.UNKNOWN);
        }
    }

    private void sleep(long length) {
        try {
            Thread.sleep(length);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
