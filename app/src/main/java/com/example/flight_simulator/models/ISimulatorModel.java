package com.example.flight_simulator.models;

public interface ISimulatorModel {
    public void sendMessage(String msg);
    public void connectToFlightGear(String ip, String port);
    public void haltConnection();
    public boolean isConnected();
}
