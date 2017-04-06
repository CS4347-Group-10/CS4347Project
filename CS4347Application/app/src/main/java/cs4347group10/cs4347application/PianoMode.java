package cs4347group10.cs4347application;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by Brendan on 3/29/2017.
 */

public class PianoMode  extends AppCompatActivity {
    Thread audioThread;
    int samplingRate = 44100;
    boolean isRunning = true;
    AudioTrack audioTrack;
    int buffsize;
    byte[] soundBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano_mode);

        ImageButton recordingBtn = (ImageButton) findViewById(R.id.record_button);
        ImageButton pianoKey1 = (ImageButton) findViewById(R.id.pianoKey1);
        ImageButton pianoKey2 = (ImageButton) findViewById(R.id.pianoKey2);
        ImageButton pianoKey3 = (ImageButton) findViewById(R.id.pianoKey3);
        ImageButton pianoKey4 = (ImageButton) findViewById(R.id.pianoKey4);
        ImageButton pianoKey5 = (ImageButton) findViewById(R.id.pianoKey5);
        ImageButton pianoKey6 = (ImageButton) findViewById(R.id.pianoKey6);
        ImageButton pianoKey7 = (ImageButton) findViewById(R.id.pianoKey7);

        recordingBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setPressed(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setPressed(false);
                        findViewById(R.id.animation_loading).setVisibility(View.VISIBLE);
                        break;
                }
                return true;
            }
        });
        pianoKey1.setOnTouchListener(new PianoOnTouchListener(0));
        pianoKey2.setOnTouchListener(new PianoOnTouchListener(1));
        pianoKey3.setOnTouchListener(new PianoOnTouchListener(2));
        pianoKey4.setOnTouchListener(new PianoOnTouchListener(3));
        pianoKey5.setOnTouchListener(new PianoOnTouchListener(4));
        pianoKey6.setOnTouchListener(new PianoOnTouchListener(5));
        pianoKey7.setOnTouchListener(new PianoOnTouchListener(6));

        audioThread = new Thread() {
            public void run() {
                // set process priority
                setPriority(Thread.MAX_PRIORITY);
                initAudio();
                soundBuffer = new byte[buffsize];
                playAudio();
            }
        };
        audioThread.start();
    }

    public class PianoOnTouchListener implements View.OnTouchListener {
        int soundIndex;

        public PianoOnTouchListener(int i) {
            soundIndex = i;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    view.setPressed(true);
                    isRunning = true;
                    // Assign soundBuffer here
                    // soundBuffer = ;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    view.setPressed(false);
                    isRunning = false;
                    break;
            }
            return true;
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

    public void playAudio() {
        audioTrack.play();
        byte[] sound = new byte[buffsize];
        while(isRunning){
            audioTrack.write(sound, 0, buffsize);
        }
        audioTrack.stop();
    }

    public void onDestroy(){
        super.onDestroy();
        isRunning = false;
        try {
            audioThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        audioThread = null;
    }

}
