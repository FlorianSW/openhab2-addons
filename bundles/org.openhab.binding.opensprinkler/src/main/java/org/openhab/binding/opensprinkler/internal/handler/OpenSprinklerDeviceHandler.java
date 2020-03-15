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
package org.openhab.binding.opensprinkler.internal.handler;

import static org.eclipse.smarthome.core.library.unit.MetricPrefix.MILLI;
import static org.openhab.binding.opensprinkler.internal.OpenSprinklerBindingConstants.*;

import javax.measure.quantity.ElectricCurrent;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.opensprinkler.internal.api.exception.CommunicationApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Florian Schmidt - Refactoring
 */
@NonNullByDefault
public class OpenSprinklerDeviceHandler extends OpenSprinklerBaseHandler {
    private final Logger logger = LoggerFactory.getLogger(OpenSprinklerDeviceHandler.class);

    public OpenSprinklerDeviceHandler(Thing thing) {
        super(thing);
    }

    @Override
    protected void updateChannel(ChannelUID channel) {
        try {
            switch (channel.getIdWithoutGroup()) {
                case SENSOR_RAIN:
                    if (getApi().isRainDetected()) {
                        updateState(channel, OnOffType.ON);
                    } else {
                        updateState(channel, OnOffType.OFF);
                    }
                    break;
                case SENSOR_CURRENT_DRAW:
                    updateState(channel, new QuantityType<ElectricCurrent>(new Integer(getApi().currentDraw()),
                            MILLI(SmartHomeUnits.AMPERE)));
                    break;
                default:
                    logger.debug("Not updating unknown channel {}", channel);
            }
        } catch (CommunicationApiException e) {
            logger.debug("Could not update " + channel.getAsString(), e);
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // nothing to do here
    }

}
