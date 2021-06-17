package com.example.flight_simulator.models;

import android.util.Log;


import com.example.flight_simulator.controllers.SimulatorController;

import java.io.IOException;
import java.util.Observable;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;


public class SimulatorModel extends Observable implements ISimulatorModel {
    private volatile boolean stop;
    private Thread t;
    private BlockingQueue<String> commands = new LinkedBlockingDeque<>();
    private Socket socket;

    public SimulatorModel()
    {
        this.stop = false;
        this.t = null;
    }

    @Override
    public void haltConnection() {
        stop = true;

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        return socket != null;
    }

    @Override
    public void sendMessage(String msg) {
        try {
            this.commands.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void connectToFlightGear(String ip, String port) {
        if (this.t != null) {
            this.haltConnection();
        }

        this.t = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket fg = null;
                PrintWriter out = null;
                String newline = "\r\n";
                synchronized (this) {
                    socket = null;
                }
                try {
                    fg = new Socket(ip, Integer.parseInt(port));
                    out = new PrintWriter(fg.getOutputStream(), true);

                    synchronized (this) {
                        socket = fg;
                        setChanged();
                        notifyObservers("socket");
                    }
                    while (!stop) {
                        synchronized (this) {
                            if (!commands.isEmpty()) {
                                out.print(commands.take() + newline);
                                out.flush();
                            }
                        }
                    }
                } catch (Exception e) {

                    synchronized (this) {
                        socket = null;
                        setChanged();
                        notifyObservers("socket");
                    }

                    if (fg != null) {
                        try {
                            fg.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                    if (out != null) {
                        out.close();
                    }
                    e.printStackTrace();
                }
            }
        });

        t.start();
    }

}
