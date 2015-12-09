package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.util;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by gabriel on 2015-11-27.
 */
public class CustomChronometer extends TextView {

    private static final long TEMPS_REPETITION_TIMER = 1000;
    private boolean running;
    private long elapsedTimeEnMilliSec;
    private Timer timer;
    private Handler handler;

    public CustomChronometer(Context context) {
        super(context);
        initialiserMyChronometer();
    }

    public CustomChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiserMyChronometer();
    }

    public CustomChronometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiserMyChronometer();
    }

    private void initialiserMyChronometer() {
        handler = new Handler();
        this.running = false;
        elapsedTimeEnMilliSec = 0;
    }

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                elapsedTimeEnMilliSec += TEMPS_REPETITION_TIMER;
                updateView();
            }
        }, TEMPS_REPETITION_TIMER, TEMPS_REPETITION_TIMER);
    }

    private void stopTimer() {
        timer.cancel();
        timer.purge();
    }

    public void start() {
        running = true;
        startTimer();
    }

    public void reset() {
        this.stop();
        elapsedTimeEnMilliSec = 0;
        updateView();
    }

    public void stop() {
        running = false;
        stopTimer();
    }

    public boolean isRunning() {
        return running;
    }

    private synchronized void updateView() {
        long heures = TimeUnit.MILLISECONDS.toHours(elapsedTimeEnMilliSec);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeEnMilliSec) -
                TimeUnit.HOURS.toMinutes(heures);
        long secondes = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeEnMilliSec) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedTimeEnMilliSec));

        final String TEMPS_FORMATTE = String.format("%02d:%02d:%02d", heures, minutes, secondes);
        handler.post(new Runnable() {
            public void run() {
                CustomChronometer.this.setText(TEMPS_FORMATTE);
            }
        });
    }

    public long getElapsedTimeInMillis() {
        return elapsedTimeEnMilliSec;
    }

    public double getElapsedTimeInHours() {
        double secondes = elapsedTimeEnMilliSec / 1000;
        double minutes = secondes / 60;
        double heures = minutes / 60;
        return heures;
    }

    public double getElapsedTimeEnMin() {
        double secondes = elapsedTimeEnMilliSec / 1000;
        double minutes = secondes / 60;
        return minutes;
    }
}
