package com.andrewsosa.simon;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    TextView max;
    LinearLayout background;

    final int FACING_UP = 0;
    final int FACING_FRONT = 1;
    final int FACING_BACK = 2;
    final int FACING_LEFT = 3;
    final int FACING_RIGHT = 4;

    boolean ready = false;


    ArrayList<Integer> targetStates = new ArrayList<>();
    ArrayList<Integer> recordStates = new ArrayList<>();



    long timestamp = System.currentTimeMillis();
    int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        max = (TextView) findViewById(R.id.max);
        background = (LinearLayout) findViewById(R.id.colors);

    }

    public void onSensorChanged(SensorEvent event){
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        final float alpha = (float) 0.8;
        float linear_acceleration[] = new float[3];

        // Remove the gravity contribution with the high-pass filter.
        float x = linear_acceleration[0] = event.values[0];// - gravity[0];
        float y = linear_acceleration[1] = event.values[1];// - gravity[1];
        float z = linear_acceleration[2] = event.values[2];// - gravity[2];

        int newState = -1;

        if(event.timestamp - timestamp > 1000000000) {

            timestamp = event.timestamp;

            if (isExtreme(z,x,y) && z > 0) {
                Log.d("Simon", "phone facing up");
                newState = FACING_UP;
            } else
            if (isExtreme(x,y,z) && x < 0) {
                Log.d("Simon", "phone facing front");
                newState = FACING_FRONT;
            } else
            if (isExtreme(x,y,z) && x > 0) {
                Log.d("Simon", "phone facing back");
                newState = FACING_BACK;
            } else
            if (isExtreme(y,x,z) && y < 0) {
                Log.d("Simon", "phone facing right");
                newState = FACING_RIGHT;
            } else
            if (isExtreme(y,x,z) && y > 0) {
                Log.d("Simon", "phone facing left");
                newState = FACING_LEFT;
            }

            checkState(newState);


        }

    }

    public void checkState(int newState) {

        Random rand = new Random();

        if(targetStates.isEmpty()) {
            // generate new states
            recordStates.add(rand.nextInt(6));
            targetStates.addAll(recordStates);

            ready = false;
            // play the new color pattern


        }

        // don't check unless ready
        if (!ready) return;

        // check if new state matches
        if(newState == targetStates.get(0)) {
            // got it
            targetStates.remove(0);

        }

        int color = Color.WHITE;

        if(targetStates.isEmpty()) {
            max.setText("Good job! Generating new");

        } else {
            max.setText(String.valueOf(targetStates.get(0)));

            switch (targetStates.get(0)) {
                case FACING_FRONT: color = Color.RED;    break;
                case FACING_BACK:  color = Color.CYAN;   break;
                case FACING_LEFT:  color = Color.GREEN;  break;
                case FACING_RIGHT: color = Color.YELLOW; break;
            }

        }

        background.setBackgroundColor(color);

    }

    public boolean isExtreme(float a, float b, float c) {
        return (Math.abs(a) > Math.abs(b) && Math.abs(a) > Math.abs(c));
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
