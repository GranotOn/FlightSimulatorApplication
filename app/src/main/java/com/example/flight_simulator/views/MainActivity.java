package com.example.flight_simulator.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.flight_simulator.R;
import com.example.flight_simulator.controllers.SimulatorController;
import com.example.flight_simulator.models.SimulatorModel;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity implements IMainActivityView {

    Context mContext;
    EditText ip, port;
    SeekBar rudder, throttle;
    Button connectButton;
    SimulatorController controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        SimulatorModel sm = new SimulatorModel();
        // Set up controller
        this.controller = new SimulatorController(this, sm);
        sm.addObserver(this.controller);

        // Get connection items
        connectButton = (Button) findViewById(R.id.connectButton);
        ip = (EditText) findViewById(R.id.ip);
        port = (EditText) findViewById(R.id.port);

        rudder = (SeekBar) findViewById(R.id.rudder);
        throttle = (SeekBar) findViewById(R.id.throttle);

        JoystickView joystick = (JoystickView) findViewById(R.id.joystick);



        // Connect event listener
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.onConnect(ip.getText().toString(), port.getText().toString());
            }
        });


        throttle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                double updatedThrottle = (float) i / 100; // Normalize to [0,1]
                controller.updateValue("throttle", updatedThrottle);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rudder.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                double updatedRudder = (float) i / 50.0 - 1; // Normalize to [-1,1]
                controller.updateValue("rudder", updatedRudder);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                controller.updateValue("heading", angle);
            }
        });

    }

    @Override
    public void onConnectSuccess(String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(mContext, "Flight Gear Connected", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public void onConnectError(String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(mContext, "Connection failed/finished", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}