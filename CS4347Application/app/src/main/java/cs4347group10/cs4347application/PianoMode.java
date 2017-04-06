package cs4347group10.cs4347application;

import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Brendan on 3/29/2017.
 */

public class PianoMode  extends AppCompatActivity {
    Thread audioThread;
    int samplingRate = 44100;
    boolean isRunning = false;
    int duration = 5;
    AudioTrack audioTrack;
    int buffsize;
    short[] soundBuffer = null;
    int btn = -1;
    Set<Integer> buttons = new HashSet<>();

    private MediaRecorder mediaRecorder;
    private File RecordFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano_mode);

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

    public void initAudio() {
        buffsize = AudioTrack.getMinBufferSize(samplingRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, samplingRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffsize,
                AudioTrack.MODE_STREAM);
    }

    public void playSound() {
        audioThread = new Thread() {
            public void run() {
                // set process priority
                //setPriority(Thread.MAX_PRIORITY);
                short samples[] = new short[buffsize];
                int amp = 10000;
                double twopi = 8.*Math.atan(1.);
                double fr = 440.f;
                double ph = 0.0;
                // start audio
                audioTrack.play();
                // synthesis loop
                while(isRunning){
                    fr =  440 + 440*btn/7.0;
                    for(int i=0; i < buffsize; i++){
                        samples[i] = (short) (amp*Math.sin(ph));
                        ph += twopi*fr/samplingRate;
                    }
                    samples = getSound();
                    audioTrack.write(samples, 0, buffsize);
                }
                audioTrack.pause();
                audioTrack.flush();
            }
        };
        audioThread.start();
    }

    public void onDestroy(){
        super.onDestroy();
        isRunning = false;
        try {
            if(audioThread != null) {
                audioThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        audioThread = null;
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

}
