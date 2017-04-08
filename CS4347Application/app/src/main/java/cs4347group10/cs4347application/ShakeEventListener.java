package cs4347group10.cs4347application;

/**
 * Created by jefferson on 3/3/2017.
 */



        import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;


/**
 * Listener that detects shake gesture.
 */
public class ShakeEventListener implements SensorEventListener {

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
    public void onSensorChanged(SensorEvent event) {// get sensor data
        long InitalDirectionChangeTime = 0;
        long LastDirectionChangeTime =0;
        int NoOfChangeDirection = 0;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];


        if (Math.abs(x + y + z - lastX - lastY - lastZ) > 5) { // total change in distance

            // get current time
            long current = System.currentTimeMillis();

            // store first movement time
            if (InitalDirectionChangeTime == 0) {
                InitalDirectionChangeTime = current;
                LastDirectionChangeTime = current;
            }

            if (current - LastDirectionChangeTime < 400) { //delay time between direction change

                LastDirectionChangeTime = current;
                NoOfChangeDirection++;
                lastX = x;
                lastY = y;
                lastZ = z;

                if (NoOfChangeDirection >= 2) { //changing direction

                    if (current - InitalDirectionChangeTime < 400) {
                        mShakeListener.onShake();
                        lastX = 0;
                        lastY = 0;
                        lastZ = 0;
                    }
                }

            } else {
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
