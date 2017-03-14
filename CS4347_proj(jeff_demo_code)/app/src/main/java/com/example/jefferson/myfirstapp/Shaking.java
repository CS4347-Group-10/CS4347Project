package com.example.jefferson.myfirstapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
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





public class Shaking extends AppCompatActivity {
    SoundPool mySound;
    int Shake_id;
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
        Shake_id = mySound.load(this, R.raw.shake, 1);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        volumeSounds();

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
            @Override
            public void onShake() {
                Toast.makeText(Shaking.this, "Shake!", Toast.LENGTH_SHORT).show();
                mySound.play(Shake_id,1,1,1,0,1);
            }
        });

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

