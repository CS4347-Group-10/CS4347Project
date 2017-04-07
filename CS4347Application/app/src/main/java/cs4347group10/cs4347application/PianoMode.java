package cs4347group10.cs4347application;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorEventListener;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cs4347group10.cs4347application.Libraries.DspLib;
import cs4347group10.cs4347application.Libraries.ShiftData;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.content.Context;

/**
 * Created by Brendan on 3/29/2017.
 */

public class PianoMode  extends AppCompatActivity implements SensorEventListener {
    Thread audioThread;
    int samplingRate = 44100;
    boolean isRunning = false;
    int duration = 5;
    AudioTrack audioTrack;
    int buffsize;
    short[] soundBuffer = null;
    int btn = -1;
    int octave = 1;
    ShiftData sf = null;
    Set<Integer> buttons = new HashSet<>();

    private MediaRecorder mediaRecorder;
    private File RecordFile;

    //************* Sensor data variables *******************************//

    private Sensor accelerometer;
    private long curTime, lastUpdate;
    private float x,y,z,last_x,last_y,last_z, shake_speed;
    private SensorManager mSensorManager;

    private ShakeEventListener mSensorListener;

    //******************************************************************//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano_mode);
        Context context = getApplicationContext();
        InputStream in = context.getResources().openRawResource(R.raw.test1);
        DataInputStream dis = new DataInputStream(in);

        byte[] temp = new byte[88244];
        try {
            dis.read(temp, 0, 88244);
        }catch (IOException e){
            e.printStackTrace();
        }
        sf=new ShiftData(DspLib.floatToDouble(DspLib.shortToFloat(DspLib.byteToShort(temp))),null);

        Button octaveChange = (Button) findViewById(R.id.octave_button);
        octaveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (octave == 1) {
                    octave = 0;
                    TextView text = (TextView) findViewById(R.id.octave_number);
                    text.setText("" + octave);
                } else {
                    octave = 1;
                    TextView text = (TextView) findViewById(R.id.octave_number);
                    text.setText("" + octave);
                }
            }
        });
        //*********************** Sensor initialisation *************************//
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this,accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        curTime = lastUpdate = (long)0.0;
        x = y = z = last_x = last_y = last_z = (float)0.0;
        mSensorListener = new ShakeEventListener();
        //***********************************************************************//

        ImageButton recordingBtn = (ImageButton) findViewById(R.id.record_button);
        final RelativeLayout pianoLayout = (RelativeLayout) findViewById(R.id.piano_layout);
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
                        break;
                    case MotionEvent.ACTION_UP:
                        mediaRecorder.stop();
                        mediaRecorder.release();
                        mediaRecorder = null;
                        view.setPressed(false);
                        findViewById(R.id.animation_loading).setVisibility(View.INVISIBLE);
                        break;
                }
                return true;
            }
        });

        RecordFile = new File(Environment.getExternalStorageDirectory(), "piano_sound.wav");

        pianoLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float posX;
                float posY;
                float width = pianoLayout.getWidth();
                float height = pianoLayout.getHeight();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        posX = event.getX();
                        posY = event.getY();
                        buttons.add(getButtonIndex(width, height, posX, posY));
                        btn = getButtonIndex(width, height, posX, posY);
                        setBtnPressed(btn, true);
                        isRunning = true;
                        playSound();
                        //Log.d("DEBUG", "X: " + posX);
                        //Log.d("DEBUG", "Y: " + posY);
                        //Log.d("DEBUG", "Button is down");
                        //Log.d("DEBUG", "isRunning = " + isRunning);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        setBtnPressed(btn, false);
                        isRunning = false;
                        //Log.d("DEBUG", "Button is up");
                        //Log.d("DEBUG", "isRunning = " + isRunning);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        posX = event.getX();
                        posY = event.getY();
                        //Log.d("DEBUG", "X: " + posX);
                        //Log.d("DEBUG", "Y: " + posY);
                        int current = getButtonIndex(width, height, posX, posY);
                        if(current >= 0) {
                            if(btn != current) {
                                setBtnPressed(btn, false);
                                setBtnPressed(current, true);
                                buttons.remove(btn);
                                buttons.add(current);
                                btn = current;
                            }
                            //playSound(btn);
                        } else {
                            setBtnPressed(btn, false);
                            isRunning = false;
                        }
                        break;
                }
                return true;
            }
        });
        initAudio();
        soundBuffer = new short[buffsize];
    }

    public int getButtonIndex(float width, float height, float x, float y) {
        int i = -1;
        float hFrac = y/height;
        float wFrac = x/width;
        if(hFrac > 0.4){
            i = (int)(wFrac * 7);
            if(i == 7){
                i--;
            } else if(i < 0){
                i = 0;
            }
        }
        return i;
    }

    public void setBtnPressed(int index, boolean isPressed){
        switch(index){
            case 0:
                findViewById(R.id.pianoKey1).setPressed(isPressed);
                break;
            case 1:
                findViewById(R.id.pianoKey2).setPressed(isPressed);
                break;
            case 2:
                findViewById(R.id.pianoKey3).setPressed(isPressed);
                break;
            case 3:
                findViewById(R.id.pianoKey4).setPressed(isPressed);
                break;
            case 4:
                findViewById(R.id.pianoKey5).setPressed(isPressed);
                break;
            case 5:
                findViewById(R.id.pianoKey6).setPressed(isPressed);
                break;
            case 6:
                findViewById(R.id.pianoKey7).setPressed(isPressed);
                break;
            default:
                break;
        }
    }

    public void initAudio() {
        buffsize = AudioTrack.getMinBufferSize(samplingRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, samplingRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffsize,
                AudioTrack.MODE_STREAM);
    }

    public short[] getSound() {
        short[] res = new short[buffsize];
        int amp = 10000;
        double twopi = 8.*Math.atan(1.);
        double fr = 440 + 440 * btn/7.0;
        double ph = 0.0;
        for(int i = 0; i < buffsize; i++) {
            res[i] = (short) (amp*Math.sin(ph));
            ph += twopi*fr/samplingRate;
        }
        return res;
    }


    public void playSound() {
        Thread thread = new Thread() {
            public void run() {
                // set process priority
                setPriority(Thread.MAX_PRIORITY);
                // start audio
                audioTrack.play();
                // synthesis loop
                while(isRunning){
                    //soundBuffer = getSound();
                    int btnNum = btn - 7 + octave * 7;
                    Log.d("DEBUG", "Button: " + btnNum);
                    soundBuffer = sf.getFullNote(btnNum);
                    short currentSound[] = new short[soundBuffer.length];
                    for(int i=0; i<currentSound.length; i++) {
                        currentSound[i] = soundBuffer[i];
                    }
                    audioTrack.write(currentSound, audioTrack.getPlaybackHeadPosition(), soundBuffer.length);
                }
                audioTrack.pause();
                //audioTrack.pause();
                audioTrack.flush();
            }
        };
        thread.start();
    }

    private void resetRecorder() {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        mediaRecorder.setAudioEncodingBitRate(16);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setOutputFile(RecordFile.getAbsolutePath());

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //***************************** Sensor changed methods **********************//

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

            System.out.println("x = " + x + "y = " + y + "z = " + z);

            //check for shake and distort note
            if(speed > 800){
                System.out.println("Distort note");
            }


        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }




}
