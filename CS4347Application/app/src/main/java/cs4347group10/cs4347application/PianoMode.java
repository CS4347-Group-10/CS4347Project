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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano_mode);

        Button recordingBtn = (Button) findViewById(R.id.record_button);
        Button pianoKey1 = (Button) findViewById(R.id.pianoKey1);
        Button pianoKey2 = (Button) findViewById(R.id.pianoKey2);
        Button pianoKey3 = (Button) findViewById(R.id.pianoKey3);
        ImageButton pianoKey4 = (ImageButton) findViewById(R.id.pianoKey4);
        ImageButton pianoKey5 = (ImageButton) findViewById(R.id.pianoKey5);
        ImageButton pianoKey6 = (ImageButton) findViewById(R.id.pianoKey6);
        ImageButton pianoKey7 = (ImageButton) findViewById(R.id.pianoKey7);

        recordingBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                    case MotionEvent.ACTION_UP:

                }
                return true;
            }
        });
        pianoKey1.setOnTouchListener(this.onTouchListener);
        pianoKey2.setOnTouchListener(this.onTouchListener);
        pianoKey3.setOnTouchListener(this.onTouchListener);
        pianoKey4.setOnTouchListener(this.onTouchListener);
        pianoKey5.setOnTouchListener(this.onTouchListener);
        pianoKey6.setOnTouchListener(this.onTouchListener);
        pianoKey7.setOnTouchListener(this.onTouchListener);

        audioThread = new Thread() {
            public void run() {
                // set process priority
                setPriority(Thread.MAX_PRIORITY);
                initAudio();
                playAudio();
            }
        };
        audioThread.start();
    }

    public View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isRunning = true;
                case MotionEvent.ACTION_UP:
                    isRunning = false;
            }
            return true;
        }
    };

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
            for(int i=0; i < buffsize; i++){
                // Get sound
            }
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
