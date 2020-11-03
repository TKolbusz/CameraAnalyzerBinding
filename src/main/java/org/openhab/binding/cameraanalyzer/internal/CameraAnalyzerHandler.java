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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
            this.api = new DelayedDuplicateEventApi(new ApiImpl(config.api_url), 5000);
            if (runningThread == null) {
                runningThread = new Thread(() -> {
                    long refresh = getRefresh();
                    try {
                        Thread.sleep(refresh);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    stop.set(false);
                    long lastRequestAt = System.currentTimeMillis();
                    while (!stop.get()) {
                        try {
                            long requestAt = System.currentTimeMillis();
                            List<CameraEvent> events = api.getEvents(lastRequestAt);
                            for (CameraEvent event : events) {
                                updateState(EVENT_CHANNEL, new StringType(event.toString()));
                            }
                            long diff = refresh - (System.currentTimeMillis() - requestAt);
                            long sleepTime = diff;
                            lastRequestAt = requestAt;
                            if (sleepTime > 0)
                                Thread.sleep(sleepTime);
                        } catch (Exception e) {
                            updateStatus(ThingStatus.OFFLINE);
                        }
                    }
                });
                runningThread.start();
            }
        }
    }

    @Override
    public void dispose() {
        stop.set(true);
        runningThread = null;
    }

    private long getRefresh() {
        return config.refresh != null ? config.refresh : 1000;
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
}
