package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by gabriel on 2015-12-02.
 */
public class Podometre implements SensorEventListener {

    private int nombrePas = 0;
    private boolean ecouter = true;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private Sensor stepDetectorSensor;

    public Podometre(Context context) {
        sensorManager = (SensorManager)
                context.getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetectorSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            Log.d("PédophileMeter", "Type Step Detected : " + value);
        } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            // For test only. Only allowed value is 1.0 i.e. for step taken
            Log.d("PédophileMeter", "Step Detector Detected : " + value);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void start() {
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void resume() {
        ecouter = true;
    }

    public void pause() {
        ecouter = false;
    }

    public void stop() {
        sensorManager.unregisterListener(this, stepCounterSensor);
        sensorManager.unregisterListener(this, stepDetectorSensor);
        nombrePas = 0;
    }

    public int getNombrePas() {
        return nombrePas;
    }
}
