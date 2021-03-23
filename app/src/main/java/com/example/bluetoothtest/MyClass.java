package com.example.bluetoothtest;

import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.net.Socket;


public class MyClass extends Application {
    private Handler handler = null;
    BluetoothSocket socket = null;
    String mac = null;

    public void setHandler(Handler handler1) {
        this.handler = handler1;
    }

    public Handler getHandler() {
        return handler;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }

    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}



