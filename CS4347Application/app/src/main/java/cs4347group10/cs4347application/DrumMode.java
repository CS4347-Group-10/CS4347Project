package cs4347group10.cs4347application;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.Button;
import android.view.View;
import android.widget.ImageButton;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;
import java.util.*;

/**
 * Created by Brendan on 3/29/2017.
 */


public class DrumMode extends AppCompatActivity implements SensorEventListener {
    private static final int SHAKETHRESHOLD = 800;
    /*int samplingRate = 44100;
    AudioTrack audioTrack;
    int buffsize;*/

    private Sensor accelerometer;
    private long curTime, lastUpdate;
    private float x,y,z,last_x,last_y,last_z, shake_speed;

    SoundPool mySound;
    int Shake_id, Shake_id2;
    private SensorManager mSensorManager;

    private ShakeEventListener mSensorListener;
    //setVolumeControlStream(AudioManager.STREAM_MUSIC);

    AudioManager audioManager;
    float curVolume, maxVolume, volume;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drum_mode);

        ImageButton recordingBtn = (ImageButton) findViewById(R.id.record_button);
        recordingBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //Start recording
                    case MotionEvent.ACTION_UP:
                        //Stop recording and process sound
                }
                return true;
            }
        });

        mySound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        Shake_id = mySound.load(this, R.raw.drumsound, 1);
        Shake_id2 = mySound.load(this, R.raw.shake,1);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this,accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        curTime = lastUpdate = (long)0.0;
        x = y = z = last_x = last_y = last_z = (float)0.0;
        mSensorListener = new ShakeEventListener();

        volumeSounds();



    }

    // Not working - will change to an animation
    public void drumHitAnimation(View view){
        // Rotate drumsticks
        view.findViewById(R.id.drumstick1).setRotation(160);
        view.findViewById(R.id.drumstick2).setRotation(150);
        // Make hit effect visible
        view.findViewById(R.id.hit_effect1).setVisibility(View.VISIBLE);
        view.findViewById(R.id.hit_effect2).setVisibility(View.VISIBLE);
    }

    /*public void initAudio() {
        buffsize = AudioTrack.getMinBufferSize(samplingRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, samplingRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffsize,
                AudioTrack.MODE_STATIC);
    }*/

    @Override
    public  void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis();

        if ((curTime - lastUpdate) > 200) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
            shake_speed = speed;

            float volume = (30 - (int) (speed / 100)) % 30;

            volume = (float) (volume / 30.0) * 5;

            if (volume <= 1) {
                volume = 1;
            } else if (volume > 1 && volume <= 2) {
                volume = 1;
            } else if (volume > 2 && volume <= 3) {
                volume = 3;
            } else if (volume > 3 && volume <= 4) {
                volume = 5;
            } else if (volume > 4 && volume <= 5) {
                volume = 5;
            } else {
                mySound.play(Shake_id2, 1, 1, 1, 0, 1);
            }

            System.out.println("volume = " + volume);
            System.out.println("speed = " + speed);

            if (shake_speed > SHAKETHRESHOLD) {
                mySound.play(Shake_id, 1 / volume, 1 / volume, 1, 0, 1);
            }

        }
    }

    protected void volumeSounds(){
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        curVolume = (float)audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float)audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = curVolume / maxVolume;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
