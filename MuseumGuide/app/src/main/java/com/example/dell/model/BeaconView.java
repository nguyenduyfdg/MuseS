package com.example.dell.model;

/**
 * Created by admin on 10/28/2017.
 */

public class BeaconView {
    private String address;
    int rssi;

    public BeaconView() {
    }

    public BeaconView(String address, int rssi) {
        this.address = address;
        this.rssi = rssi;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
