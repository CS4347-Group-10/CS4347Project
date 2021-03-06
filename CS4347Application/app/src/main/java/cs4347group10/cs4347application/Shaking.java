package cs4347group10.cs4347application;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.content.Context;
import android.widget.Button;
import android.content.Intent;
import android.media.SoundPool;
import android.media.AudioManager;
import java.util.*;


public class Shaking extends AppCompatActivity implements SensorEventListener {

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
        setContentView(R.layout.activity_shaking);
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

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
            @Override
            public void onShake() {
                Toast.makeText(Shaking.this, String.valueOf(shake_speed), Toast.LENGTH_SHORT).show();
                //mySound.play(Shake_id,1,1,1,0,1);
            }
        });

    }

    @Override
    public  void onSensorChanged(SensorEvent event){
        long curTime = System.currentTimeMillis();

        if((curTime - lastUpdate)> 200) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
            shake_speed = speed;

            float volume = (30-(int)(speed / 100))%30;

            volume = (float)(volume / 30.0) * 5;

            if(volume <= 1){
                volume = 1;
            }
            else if (volume > 1 && volume <= 2){
                volume = 1;
            }
            else if(volume > 2 && volume <= 3){
                volume = 3;
            }
            else if(volume > 3 && volume <= 4){
                volume = 4;
            }
            else if(volume > 4 && volume <= 5){
                volume = 5;
            }
            else {
                mySound.play(Shake_id2,1,1,1,0,1);
            }

            System.out.println("volume = " + volume);
            System.out.println("speed = " + speed);
            if(shake_speed > 800) {
                mySound.play(Shake_id,1/volume ,1/volume, 1, 0, 1);
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    public void playMusic(View view) {
        mySound.play(Shake_id,1,1,1,0,1);}

    public void pauseMusic(View view) {
        mySound.stop(Shake_id);
    }

    protected void volumeSounds(){
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        curVolume = (float)audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float)audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = curVolume / maxVolume;
    }

    public void onClick(View v){
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
    }


}

