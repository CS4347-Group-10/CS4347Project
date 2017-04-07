package cs4347group10.cs4347application;

/**
 * Created by jefferson on 3/3/2017.
 */



        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.location.Location;


/**
 * Listener that detects shake gesture.
 */
public class ShakeEventListener implements SensorEventListener {

    private long InitalDirectionChangeTime = 0;
    private long LastDirectionChangeTime;
    private int NoOfChangeDirection = 0;

    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;

    private OnShakeListener mShakeListener;


    public interface OnShakeListener {
        void onShake();
    }

    public void setOnShakeListener(OnShakeListener listener) {
        mShakeListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent se) {// get sensor data

        float x = se.values[0];
        float y = se.values[1];
        float z = se.values[2];


        if (Math.abs(x + y + z - lastX - lastY - lastZ) > 5) { // force

            // get time
            long current = System.currentTimeMillis();

            // store first movement time
            if (InitalDirectionChangeTime == 0) {
                InitalDirectionChangeTime = current;
                LastDirectionChangeTime = current;
            }

            // check if the last movement was not long ago
            long lastChangeWasAgo = current - LastDirectionChangeTime;
            if (lastChangeWasAgo < 400) { //delay time between direction change

                // store movement data
                LastDirectionChangeTime = current;
                NoOfChangeDirection++;

                // store last sensor data
                lastX = x;
                lastY = y;
                lastZ = z;

                // check how many movements are so far
                if (NoOfChangeDirection >= 2) { //changing direction

                    // check total duration
                    long totalDuration = current - InitalDirectionChangeTime;
                    if (totalDuration < 400) {
                        mShakeListener.onShake();
                        InitalDirectionChangeTime = 0;
                        NoOfChangeDirection = 0;
                        LastDirectionChangeTime = 0;
                        lastX = 0;
                        lastY = 0;
                        lastZ = 0;
                    }
                }

            } else {
                InitalDirectionChangeTime = 0;
                NoOfChangeDirection = 0;
                LastDirectionChangeTime = 0;
                lastX = 0;
                lastY = 0;
                lastZ = 0;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


}
