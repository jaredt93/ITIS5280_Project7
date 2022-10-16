package com.group3.itis5280_project7;

import android.bluetooth.BluetoothDevice;

import java.util.Objects;

public class Device {
    String name, uuid;
    Boolean connected;
    BluetoothDevice device;

    public Device() {
        // empty
    }

    public Device(String name, BluetoothDevice device) {
        this.name = name;
        this.device = device;
        uuid = "1";
        connected = false;
    }

    public Device(String name, String uuid, Boolean connected) {
        this.name = name;
        this.uuid = uuid;
        this.connected = connected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return name.equals(device.name);
        //return name.equals(device.name) && uuid.equals(device.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Device{" +
                "name='" + name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", connected=" + connected +
                '}';
    }
}
