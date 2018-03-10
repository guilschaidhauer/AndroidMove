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


public class MainActivity extends AppCompatActivity{

    SensorManager sensorManager;


    Sensor gyroscopeSensor;

    Sensor rotationVectorSensor;

    float[] orientations;

    float acceleration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);

        gyroscopeSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        rotationVectorSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        // Create listener
        SensorEventListener rvListener2 = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                acceleration = sensorEvent.values[0];

                if(sensorEvent.values[0] > 0.5f) { // anticlockwise
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                } else if(sensorEvent.values[0] < -0.5f) { // clockwise
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                }
            }



            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        // Register it
        sensorManager.registerListener(rvListener2,
                gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Create listener
        SensorEventListener rvListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                //getWindow().getDecorView().setBackgroundColor(Color.BLUE);

                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(
                        rotationMatrix, sensorEvent.values);

                // Remap coordinate system
                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedRotationMatrix);

                // Convert to orientations
                orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);

                for(int i = 0; i < 3; i++) {
                    orientations[i] = (float)(Math.toDegrees(orientations[i]));
                }

                orientations[0] = acceleration;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        // Register it
        sensorManager.registerListener(rvListener,
                rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);

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