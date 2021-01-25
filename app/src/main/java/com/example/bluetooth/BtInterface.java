package com.example.bluetooth;

/**
 * Created by djamel on 12/02/2017.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import static android.content.ContentValues.TAG;


public class BtInterface {

    private BluetoothDevice device = null;
    private BluetoothSocket socket = null;
    private InputStream receiveStream = null;
    private OutputStream sendStream = null;
    private ReceiverThread receiverThread;
    Handler handler;

    public BtInterface(Handler hstatus, Handler h) {
        Set<BluetoothDevice> setpairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        BluetoothDevice[] pairedDevices = (BluetoothDevice[]) setpairedDevices.toArray(new BluetoothDevice[setpairedDevices.size()]);

        for (int i = 0; i < pairedDevices.length; i++) {
            if (pairedDevices[i].getName().contains("HC-05") || (pairedDevices[i].getName().contains("raspberrypi")) ) {
                device = pairedDevices[i];
                try {
                    socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    receiveStream = socket.getInputStream();
                    sendStream = socket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        handler = hstatus;
        receiverThread = new ReceiverThread(h);
    }

    public void sendData(String data) {
        sendData(data, false);
    }

    public void sendData(String data, boolean deleteScheduledData) {
        try {
            sendStream.write(data.getBytes());
            sendStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        new Thread() {
            @Override
            public void run() {
                try {
                    socket.connect();
                    Message msg = handler.obtainMessage();
                    msg.arg1 = 1;
                    handler.sendMessage(msg);
                    receiverThread.start();
                } catch (IOException e) {
                    Log.v("N", "Connection Failed : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    private class ReceiverThread extends Thread {
        Handler handler;

        ReceiverThread(Handler h) {
            handler = h;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (receiveStream.available() > 0) {
                        byte buffer[] = new byte[100];

                        int k = receiveStream.read(buffer, 0, 100);
                        if (k > 0) {
                            byte rawdata[] = new byte[k];
                            for (int i = 0; i < k; i++)
                                rawdata[i] = buffer[i];
                            String data = new String(rawdata.clone());
                            Message msg = handler.obtainMessage();
                            Bundle b = new Bundle();
                            b.putString("receivedData", data);
                            msg.setData(b);
                            handler.sendMessage(msg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMessage(BluetoothSocket socket, String message) {
        OutputStream outStream;
        try {
            outStream = socket.getOutputStream();
            byte[] byteArray = (message + "").getBytes();
            byteArray[byteArray.length - 1] = 0;
            outStream.write(byteArray);
        } catch (IOException e) {
            Log.e(TAG, "Echec de l'envoi du message.", e);
            //e.printStackTrace();
        }
    }


    private boolean listening = false;

    private void listenForMessage(BluetoothSocket socket, String incoming) {
        listening = true;
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            InputStream instream = socket.getInputStream();
            int bytesRead = -1;
            while (listening) {
                bytesRead = instream.read(buffer);
                if (bytesRead != 1) {
                    String result = "";
                    while ((bytesRead == bufferSize) && (buffer[bufferSize - 1] != 0)) {
                        result = result + new String(buffer, 0, bytesRead - 1);
                        bytesRead = instream.read(buffer);
                    }
                    result = result + new String(buffer, 0, bytesRead - 1);
                    ///incoming.append(result);
                }
            }
        } catch (IOException e) {
            // e.printStackTrace();
            Log.e(TAG, "échec de la réception du message.", e);
        } finally {
            close();
        }


    }
}
