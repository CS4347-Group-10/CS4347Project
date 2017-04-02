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

/**
 * Created by Brendan on 3/29/2017.
 */

public class DrumMode extends AppCompatActivity {
    /*int samplingRate = 44100;
    AudioTrack audioTrack;
    int buffsize;*/

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
}
