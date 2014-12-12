/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.midi;

import java.io.FileInputStream;

/**
 * This class is used for receiving data to a port on a MIDI device
 *
 * @hide
 */
public final class MidiOutputPort extends MidiPort implements MidiSender {

    private final MidiDevice mDevice;

  /* package */ MidiOutputPort(MidiDevice device, int portNumber) {
        super(portNumber);
        mDevice = device;
    }

    /**
     * Connects a {@link MidiReceiver} to the output port to allow receiving
     * MIDI messages from the port.
     *
     * @param receiver the receiver to connect
     */
    public void connect(MidiReceiver receiver) {
        mDevice.connect(receiver, getPortNumber());
    }

    /**
     * Disconnects a {@link MidiReceiver} from the output port.
     *
     * @param receiver the receiver to connect
     */
    public void disconnect(MidiReceiver receiver) {
        mDevice.disconnect(receiver, getPortNumber());
    }
}