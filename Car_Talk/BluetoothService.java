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

package com.yangproject.embeddedproject.Others;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothService {
    private static BluetoothService ourInstance = new BluetoothService();
    public static BluetoothService getInstance(Handler handler) {
        mHandler = handler;
        return ourInstance;
    }

    private BluetoothService() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mAdapter;
    }

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private static Handler mHandler;
    private ConnectThread mConnectThread;
    private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        mState = state;
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop(String message) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, message);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectThread#write(byte[])
     */
    public void write(byte[] out, Activity activity) {
        // Create temporary object
        ConnectThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectThread;
        }
        // Perform the write unsynchronized
        r.write(out, activity);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;
        private final OutputStream mmOutStream;
        private boolean running;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
            } catch (IOException e) { }
            mmSocket = tmp;

            OutputStream tmpOut = null;
            try {
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) { }

            mmOutStream = tmpOut;
        }

        public void run() {
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) { }
                connectionFailed();
                return;
            }

            /*// Send the name of the connected device back to the UI Activity
            Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.DEVICE_NAME, mmDevice.getName());
            msg.setData(bundle);
            mHandler.sendMessage(msg);*/
            setState(STATE_CONNECTED);

            running = true;
            while(running) { }
        }

        public void write(byte[] buffer, Activity activity) {
            try {
                mmOutStream.write(buffer);
                // Send a message back to the Activity for success
                Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.TOAST, "Your message is transmitted successfully");
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            } catch (IOException e) {
                connectionLost();
                activity.finish();
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
            running = false;
        }
    }
}
