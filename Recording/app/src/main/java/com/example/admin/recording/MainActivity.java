package com.example.admin.recording;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

import java.io.File;
import android.view.MotionEvent;
import android.os.Environment;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Button RecordButton;
    private Button StopRecButton;
    private Button playRecAudio;
    private Button stopPlayRecAudio;
    private MediaRecorder mediaRecorder;
    private File RecordFile;
    private MediaPlayer mPlayer = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecordButton = (Button) findViewById(R.id.button);
        RecordButton.setOnClickListener(this);
        RecordButton.setEnabled(true);
        RecordButton.setText("rec");

        StopRecButton = (Button) findViewById(R.id.button1);
        StopRecButton.setOnClickListener(this);
        StopRecButton.setEnabled(false);
        StopRecButton.setText("stop rec");

        playRecAudio = (Button) findViewById(R.id.button2);
        playRecAudio.setOnClickListener(this);
        playRecAudio.setEnabled(true);
        playRecAudio.setText("play audio");

        stopPlayRecAudio = (Button) findViewById(R.id.button3);
        stopPlayRecAudio.setOnClickListener(this);
        stopPlayRecAudio.setEnabled(true);
        stopPlayRecAudio.setText("stop audio");

        RecordFile = new File(Environment.getExternalStorageDirectory(),
                "audiotest.wav");
    }

    // this process must be done prior to the start of recording
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

    @Override


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                mediaRecorder = new MediaRecorder();
                resetRecorder();
                mediaRecorder.start();

                RecordButton.setEnabled(false);
                StopRecButton.setEnabled(true);
                break;
            case R.id.button1:
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                RecordButton.setEnabled(true);
                StopRecButton.setEnabled(false);
                break;
            case R.id.button2:
                startPlaying();
                break;
            case R.id.button3:
                stopPlaying();
                break;
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(RecordFile.getPath());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            System.out.print("\"Prepare\"");

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
}
