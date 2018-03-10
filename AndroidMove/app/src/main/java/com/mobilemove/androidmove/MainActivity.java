package com.mobilemove.androidmove;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.view.WindowManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;
import android.view.WindowManager;



public class MainActivity extends AppCompatActivity{

    float[] orientations;

    public interface Listener {
        void onOrientationChanged(float pitch, float roll);
    }

    private static final int SENSOR_DELAY_MICROS = 50 * 1000; // 50ms

    private final SensorManager mSensorManager;
    private final Sensor mRotationSensor;
    private final WindowManager mWindowManager;

    private int mLastAccuracy;
    private Listener mListener;

    public MainActivity(SensorManager sensorManager, WindowManager windowManager) {
        mSensorManager = sensorManager;
        mWindowManager = windowManager;

        // Can be null if the sensor hardware is not available
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void startListening(Listener listener) {
        if (mListener == listener) {
            return;
        }
        mListener = listener;
        if (mRotationSensor == null) {
            //Log.w("Rotation vector sensor not available; will not provide orientation data.");
            return;
        }

        mSensorManager.registerListener((SensorEventListener) this, mRotationSensor, SENSOR_DELAY_MICROS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                sendMessage();
                handler.postDelayed(this, 70);
            }
        };

        handler.postDelayed(r, 15);
    }

    protected void sendMessage() {
        //getWindow().getDecorView().setBackgroundColor(Color.GREEN);
        try {
            //String messageStr = "Hello World";
            String messageStr = Float.toString(orientations[0]) + "|";
            messageStr += Float.toString(orientations[1]) + "|";
            messageStr += Float.toString(orientations[2]);

            //String messageStr = "99|99|99";
            int server_port = 8888;
            InetAddress local = InetAddress.getByName("192.168.25.11");
            //InetAddress local = InetAddress.getByName("192.168.43.227");
            int msg_length = messageStr.length();
            byte[] message = messageStr.getBytes();


            DatagramSocket s = new DatagramSocket();
            //

            DatagramPacket p = new DatagramPacket(message, msg_length, local, server_port);
            s.send(p);//properly able to send data. i receive data to server
            //getWindow().getDecorView().setBackgroundColor(Color.CYAN);
        }
        catch(Exception ex)
        {
            //getWindow().getDecorView().setBackgroundColor(Color.RED);
            ex.printStackTrace();
        }
    }
}