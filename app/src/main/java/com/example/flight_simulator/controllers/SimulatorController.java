package com.example.flight_simulator.controllers;

import android.util.Log;

import com.example.flight_simulator.models.ISimulatorModel;
import com.example.flight_simulator.views.IMainActivityView;

import java.util.Observable;
import java.util.Observer;
 Observ
public class SimulatorController implementser {
    IMainActivityView view;
    ISimulatorModel model;


    public SimulatorController(IMainActivityView view, ISimulatorModel model) {
        this.view = view;
        this.model = model;
    }

    public void onConnect(String ip, String port) {
        model.connectToFlightGear(ip, port);
    }

    public void updateValue(String variable, double val) {
        if (!model.isConnected()) {
            return;
        }

        switch (variable) {
            case "heading":
                double radians = Math.toRadians(val);
                double v_x = Math.cos(radians);
                double v_y = Math.sin(radians);
                model.sendMessage("set /controls/flight/aileron " + v_x);
                model.sendMessage("set /controls/flight/elevator " + v_y);
                break;
            case "throttle":
                model.sendMessage("set /controls/engines/current-engine/throttle " + val);
                break;
            case "rudder":
                model.sendMessage("set /controls/flight/rudder " + val);
                break;
            default:
                return;
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        String event = (String) o;
        switch (event) {
            case "socket":
                if (model.isConnected()) {
                    view.onConnectSuccess("Flight Gear Connected");
                } else {
                    view.onConnectError("Socket ended connection");
                }
                break;
            default:
                return;
        }
    }
}
