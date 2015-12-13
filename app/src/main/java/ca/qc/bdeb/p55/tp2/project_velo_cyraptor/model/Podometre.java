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

    public Podometre(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    /**
     * Est appeler a chaque nouveau pas et incrémente le noubre de pas lorsqu'on l'écoute
     * @param event l'évènnement du sensor
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (ecouter) {
            nombrePas++;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Démarre l'écoute du sensor
     */
    public void start() {
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * Redémarre l'écoute
     */
    public void resume() {
        ecouter = true;
    }

    /**
     * Arrête l'écoute
     */
    public void pause() {
        ecouter = false;
    }

    /**
     * Arrête l'écoute du sensor et réinitialize le nombre de pas
     */
    public void stop() {
        sensorManager.unregisterListener(this, stepCounterSensor);
        nombrePas = 0;
    }

    public int getNombrePas() {
        return nombrePas;
    }
}
