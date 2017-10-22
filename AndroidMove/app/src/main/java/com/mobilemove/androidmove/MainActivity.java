package com.mobilemove.androidmove;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity {

    SensorManager sensorManager;
    Sensor rotationVectorSensor;

    float[] orientations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);

        rotationVectorSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

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

                if(orientations[2] > 45) {
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                } else if(orientations[2] < -45) {
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                } else if(Math.abs(orientations[2]) < 10) {
                    getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                }

                sendMessage();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        // Register it
        sensorManager.registerListener(rvListener,
                rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void sendMessage() {
        try {
            //String messageStr = "Hello World";
            Integer[] orInt = new Integer[3];

            orInt[0] = (int)orientations[0];
            orInt[1] = (int)orientations[2];
            orInt[2] = (int)orientations[2];

            String messageStr = Integer.toString(orInt[0]) + "|";
            messageStr += Integer.toString(orInt[1]) + "|";
            messageStr += Integer.toString(orInt[2]);
            int server_port = 7777;
            InetAddress local = InetAddress.getByName("192.168.2.193");
            int msg_length = messageStr.length();
            byte[] message = messageStr.getBytes();


            DatagramSocket s = new DatagramSocket();
            //

            DatagramPacket p = new DatagramPacket(message, msg_length, local, server_port);
            s.send(p);//properly able to send data. i receive data to server
        }
        catch(Exception ex)
        {
            getWindow().getDecorView().setBackgroundColor(Color.RED);
            ex.printStackTrace();
        }
    }
}