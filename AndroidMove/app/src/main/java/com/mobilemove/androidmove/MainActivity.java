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
import android.view.View;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity{

    SensorManager sensorManager;
    Sensor rotationVectorSensor;

    private View root=null;

    float[] orientations;
    float[] gyro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root=findViewById(android.R.id.content);

        sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);

        rotationVectorSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        // Create listener
        SensorEventListener rvListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                //getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                gyro = new float[4];
                gyro = sensorEvent.values;
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
            Integer[] orInt = new Integer[4];

            //orInt[0] = (int)orientations[0];
            //orInt[1] = (int)orientations[1];
            //orInt[2] = (int)orientations[2];

            //orInt[0] = (int)(gyro[0] * 1000000);
            //orInt[1] = (int)(gyro[1] * 1000000);
            //orInt[2] = (int)(gyro[2] * 1000000);
            //orInt[3] = (int)(gyro[3] * 1000000);

            String messageStr = Float.toString(gyro[0]) + "|";
            messageStr += Float.toString(gyro[1]) + "|";
            messageStr += Float.toString(gyro[2]) + "|";
            messageStr += Float.toString(gyro[3]);

            /*String messageStr = Float.toString(gyro[0]) + "|";
            messageStr += Float.toString(gyro[1]) + "|";
            messageStr += Float.toString(gyro[2]) + "|";
            messageStr += Float.toString(gyro[3]);*/

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