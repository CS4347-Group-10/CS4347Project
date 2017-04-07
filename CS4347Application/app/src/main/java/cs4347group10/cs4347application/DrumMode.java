package cs4347group10.cs4347application;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
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
import android.media.AudioRecord;
import android.media.AudioTrack;
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
import android.media.SoundPool.OnLoadCompleteListener;
import android.view.animation.Interpolator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator;
import android.widget.Button;
import java.io.File;
import java.io.IOException;
import android.util.Base64;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import android.os.ParcelFileDescriptor;
import java.io.InputStream;

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
    int Shake_id, Shake_id2, Shake_id3;
    private SensorManager mSensorManager;

    private ShakeEventListener mSensorListener;
    //setVolumeControlStream(AudioManager.STREAM_MUSIC);

    AudioManager audioManager;
    float curVolume, maxVolume, volume;

    private MediaRecorder mediaRecorder;
    private File RecordFile;
    private MediaPlayer mPlayer = null;
    byte [] temp =null;
    private static String Filename = null;

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
                        view.setPressed(true);
                        mediaRecorder = new MediaRecorder();
                        resetRecorder();
                        mediaRecorder.start();
                        findViewById(R.id.animation_loading).setVisibility(View.VISIBLE);
                        //Start recording
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mediaRecorder.stop();
                        mediaRecorder.release();
                        mediaRecorder = null;
                        view.setPressed(false);
                        findViewById(R.id.animation_loading).setVisibility(View.INVISIBLE);
                        //Stop recording and process sound
                        break;
                }
                return true;
            }
        });

        Filename = getCacheDir().getAbsolutePath();
        Filename +="drum_sound.wav";

        //RecordFile = new File(Environment.getDataDirectory(),"drum_sound.wav");


        mySound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        Shake_id = mySound.load(this, R.raw.drumsound, 1);
        Shake_id2 = mySound.load(this, R.raw.shake, 1);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        curTime = lastUpdate = (long) 0.0;
        x = y = z = last_x = last_y = last_z = (float) 0.0;
        mSensorListener = new ShakeEventListener();


        volumeSounds();


    }

    private void resetRecorder() {
        /*ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ParcelFileDescriptor[] descriptors = new ParcelFileDescriptor[0];
        try {
            descriptors = ParcelFileDescriptor.createPipe();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ParcelFileDescriptor parcelRead = new ParcelFileDescriptor(descriptors[0]);
        ParcelFileDescriptor parcelWrite = new ParcelFileDescriptor(descriptors[1]);

        InputStream inputStream = new ParcelFileDescriptor.AutoCloseInputStream(parcelRead);*/

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        mediaRecorder.setAudioEncodingBitRate(16);
        mediaRecorder.setAudioSamplingRate(44100);
        //mediaRecorder.setOutputFile(RecordFile.getAbsolutePath());
        mediaRecorder.setOutputFile(Filename);
       // mediaRecorder.setOutputFile(parcelWrite.getFileDescriptor());

       try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*mediaRecorder.start();
        int read;
        byte[] data = new byte[16384];

        try {
            while ((read = inputStream.read(data, 0, data.length)) != -1) {
                byteArrayOutputStream.write(data, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            byteArrayOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(Arrays.toString(data));*/

    }

    public class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat -1f);
        }
    }

    public void drumHitAnimation() {
        final View dStick1 = findViewById(R.id.drumstick1);
        final View dStick2 = findViewById(R.id.drumstick2);
        final View hEffect1 = findViewById(R.id.hit_effect1);
        final View hEffect2 = findViewById(R.id.hit_effect2);
        // Rotate drumsticks
        dStick1.animate()
                .setStartDelay(0)
                .setDuration(200)
                .rotationBy(-40);
        dStick2.animate()
                .setStartDelay(0)
                .setDuration(200)
                .rotationBy(-60);
        // Make hit effect visible
        hEffect1.setVisibility(View.VISIBLE);
        hEffect2.setVisibility(View.VISIBLE);
        hEffect1.animate()
                .setStartDelay(200)
                .setDuration(100)
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        dStick1.animate().setInterpolator(new ReverseInterpolator());
                        dStick2.animate().setInterpolator(new ReverseInterpolator());
                        dStick1.setRotation(200);
                        dStick2.setRotation(210);
                        hEffect1.setVisibility(View.GONE);
                        hEffect1.animate().setListener(null);
                    }
                });
        hEffect2.animate()
                .setStartDelay(200)
                .setDuration(100)
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        hEffect2.setVisibility(View.GONE);
                        hEffect2.animate().setListener(null);
                    }
                });

    }

    /*private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(RecordFile.getPath());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            System.out.print("\"Prepare\"");

        }
    }*/

    public void loadSound (boolean loaded, int stream, float volume) {
        mySound.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
            }
        });
        if (loaded == true) {
            mySound.play(stream, volume, volume, 1, 0, 1f);
        }
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
    public  void onSensorChanged(SensorEvent event){
        long curTime = System.currentTimeMillis();

        if ((curTime - lastUpdate)> 200) {
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
                volume = 5;
            }
            else if(volume > 4 && volume <= 5){
                volume = 5;
            }
            else {
                mySound.play(Shake_id2,1,1,1,0,1);
            }

            System.out.println("volume = " + volume);
            System.out.println("speed = " + speed);


            if (shake_speed > SHAKETHRESHOLD) {
                //mySound.play(Shake_id, 1 / volume, 1 / volume, 1, 0, 1);
                int stream= mySound.load(Filename, 1);
                //loadSound(loaded, stream, (1 / volume));


                mySound.play(stream, 1 / volume, 1 / volume, 1, 0, 1);
                drumHitAnimation();
                //startPlaying();
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
