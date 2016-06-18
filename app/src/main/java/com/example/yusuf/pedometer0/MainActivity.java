package com.example.yusuf.pedometer0;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    SensorManager sensorManager;
    Sensor stepCounterSensor;
    Sensor stepDetectorSensor;

    TextView stepCountTxV;
    TextView stepDetectTxV;

    int currentStepCount;
    int currentStepsDetected;

    int countSteps;
    int stepCounter;
    int newStepCounter;

    int detectSteps;

    boolean startCounting;


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initVars();

        manageViews();

    }

    private void initVars() {

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        //currentStepCount = 0;
        currentStepsDetected = 0;
        stepCounter = 0;
        newStepCounter = 0;

        startCounting = false;

    }

    private void manageViews() {

        stepCountTxV = (TextView)findViewById(R.id.stepCountTxV);
        //stepDetectTxV = (TextView)findViewById(R.id.stepDetectTxV);

        ToggleButton countingToggle = (ToggleButton)findViewById(R.id.countingToggle);
        countingToggle.setHapticFeedbackEnabled(true);
        countingToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startCounting = true;
                    //sensorManager.registerListener(MainActivity.this, stepCounterSensor, 0);
                    //sensorManager.registerListener(MainActivity.this, stepDetectorSensor, 0);
                }
                else if (!isChecked) {
                        resetCounter();
                        startCounting = false;
                }
            }
        });

        BackgroundImageThread backImageThread = new BackgroundImageThread();
        backImageThread.start();

    }


    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);

    }

    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, stepCounterSensor, 0);
        sensorManager.registerListener(this, stepDetectorSensor, 0);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.??? ,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*
        if (item.getItemId() == R.id.???) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }


    public void onSensorChanged(SensorEvent event) {
        if (startCounting) {
            // STEP_COUNTER Sensor.
            // *** Step Counting does not restart until the device is restarted - therefore, an algorithm for restarting the counting must be implemented.
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                countSteps = (int) event.values[0];


                // -The long way of starting a new step counting sequence.-
                /**
                 int tempStepCount = countSteps;
                 int initialStepCount = countSteps - tempStepCount; // Nullify step count - so that the step counting can restart.
                 currentStepCount += initialStepCount; // This variable will be initialised with (0), and will be incremented by itself for every Sensor step counted.
                 stepCountTxV.setText(String.valueOf(currentStepCount));
                 currentStepCount++; // Increment variable by 1 - so that the variable can increase for every Step_Counter event.
                 */


                // -The efficient way of starting a new step counting sequence.-
                /*
                if (stepCounter == 0) { // If the stepCounter is in its initial value, then...
                    stepCounter = (int) event.values[0]; // Assign the StepCounter Sensor event value to it.
                }
                newStepCounter = countSteps - stepCounter; // By subtracting the stepCounter variable from the Sensor event value - We start a new counting sequence from 0. Where the Sensor event value will increase, and stepCounter value will be only initialised once.
                stepCountTxV.setText('"' + String.valueOf(newStepCounter) + '"' + " Steps Counted");
                */

                //setStepCounting();
            }

            /*
            // STEP_DETECTOR Sensor.
            // *** Step Detector: When a step event is detect - "event.values[0]" becomes 1. And stays at 1!
            if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                detectSteps = (int) event.values[0];
                currentStepsDetected += detectSteps; //steps = steps + detectSteps; // This variable will be initialised with the STEP_DETECTOR event value (1), and will be incremented by itself (+1) for as long as steps are detected.
                stepDetectTxV.setText("Steps Detected = " + String.valueOf(currentStepsDetected));
            }
            */

            //
            setStepCounting();

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setStepCounting() {

        if (stepCounter == 0) { // If the stepCounter is in its initial value, then...
            stepCounter = countSteps; // Assign the StepCounter Sensor event value to it.
        }
        newStepCounter = countSteps - stepCounter; // By subtracting the stepCounter variable from the Sensor event value - We start a new counting sequence from 0. Where the Sensor event value will increase, and stepCounter value will be only initialised once.
        stepCountTxV.setText('"' + String.valueOf(newStepCounter) + '"' + " Steps Counted");

        counterAnimation();

    }

    private void resetCounter() {
        stepCounter = 0;
        currentStepsDetected = 0;
    }


    private void counterAnimation() {
        TranslateAnimation translateAnimation = new TranslateAnimation(100,-100,100,-100);
        translateAnimation.setDuration(200);
        translateAnimation.setInterpolator(new LinearOutSlowInInterpolator());
        stepCountTxV.startAnimation(translateAnimation);
    }


    // Because there is a possibility of skipped frames when dealing with the inputstream on the UI thread, it is a good idea to handle it in another thread.
    private class BackgroundImageThread extends Thread {
        @Override
        public void run() {
            // Layout Background Image Management.
            try {
                // Get input stream.
                InputStream inputStream = getAssets().open("background.png");
                // Load image as drawable.
                final Drawable parentDrawable = Drawable.createFromStream(inputStream, null);
                // Set opacity (transparency) of image.
                parentDrawable.setAlpha(245);

                // Update UI on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Retrieve parent relativelayout.
                        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
                        // Set drawable image to imageview.
                        parentLayout.setBackground(parentDrawable);
                    }
                });
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

}
