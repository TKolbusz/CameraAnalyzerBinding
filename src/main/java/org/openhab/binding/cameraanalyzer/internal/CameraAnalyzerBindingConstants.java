/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.cameraanalyzer.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link CameraAnalyzerBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Markiewi - Initial contribution
 */
@NonNullByDefault
public class CameraAnalyzerBindingConstants {

    private static final String BINDING_ID = "cameraanalyzer";

    // List of all Thing Type UIDs
    public static final ThingTypeUID CAMERAANALYZER_THING = new ThingTypeUID(BINDING_ID, "cameraanalyzer");

    // List of all Channel ids
    public static final String EVENT_CHANNEL = "events";
    public static final String POWER_CHANNEL = "power";
}
