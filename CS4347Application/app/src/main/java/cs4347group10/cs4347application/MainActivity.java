package cs4347group10.cs4347application;

import android.app.Activity;
import android.provider.MediaStore;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;

import android.media.*;

//public class MainActivity extends AppCompatActivity implements SensorEventListener
public class MainActivity extends AppCompatActivity {

    private TextView xText, yText, zText;
    private Sensor mySensor;
    private SensorManager SM;
    private int able;
    private int maxVolume = 50;

    MediaPlayer mySound1, mySound2, mySound3, mySound4,mySound5, mySound6, mySound7, mySound8,mySound9, mySound10, mySound11, mySound12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }
        /*able = 1;

        Button Act1Button = (Button)findViewById(R.id.GoToShakeMode);
        Button Act3Button = (Button)findViewById(R.id.button3);
        Button Act4Button = (Button)findViewById(R.id.button4);

        Act1Button.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Intent myIntent = new Intent(v.getContext(), Shaking.class );
                        startActivity(myIntent);
                    }
                }
        );

        Act3Button.setOnClickListener(              //Pausing the program
                new Button.OnClickListener(){
                    public void onClick(View v){
                        able = 0;
                    }
                }
        );

        Act4Button.setOnClickListener(              //Resuming the program
                new Button.OnClickListener(){
                    public void onClick(View v){
                        able = 1;
                    }
                }
        );

        mySound1 = MediaPlayer.create(this, R.raw.c_1);
        mySound2 = MediaPlayer.create(this, R.raw.cs_2);
        mySound3 = MediaPlayer.create(this, R.raw.d_3);
        mySound4 = MediaPlayer.create(this, R.raw.ds_4);
        mySound5 = MediaPlayer.create(this, R.raw.e_5);
        mySound6 = MediaPlayer.create(this, R.raw.f_6);
        mySound7 = MediaPlayer.create(this, R.raw.fs_7);
        mySound8 = MediaPlayer.create(this, R.raw.g_8);
        mySound9 = MediaPlayer.create(this, R.raw.gs_9);
        mySound10 = MediaPlayer.create(this, R.raw.a_10);
        mySound11 = MediaPlayer.create(this, R.raw.as_11);
        mySound12 = MediaPlayer.create(this, R.raw.b_12);

        mySound1.setVolume(1,maxVolume);
        mySound2.setVolume(1,maxVolume);
        mySound3.setVolume(1,maxVolume);
        mySound4.setVolume(1,maxVolume);
        mySound5.setVolume(1,maxVolume);
        mySound6.setVolume(1,maxVolume);
        mySound7.setVolume(1,maxVolume);
        mySound8.setVolume(1,maxVolume);
        mySound9.setVolume(1,maxVolume);
        mySound10.setVolume(1,maxVolume);
        mySound11.setVolume(1,maxVolume);
        mySound12.setVolume(1,maxVolume);


        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.activity_main){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
        //Not in use
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        xText.setText("X: " + event.values[0]);
        yText.setText("Y: " + event.values[1]);
        zText.setText("Z: " + event.values[2]);
        if(able == 1) {
            if (Math.abs(event.values[0]) < 1) {
                mySound1.start();
            }
            if (Math.abs(event.values[0]) > 1 && Math.abs(event.values[0]) < 2) {
                mySound2.start();
            }
            if (Math.abs(event.values[0]) > 2 && Math.abs(event.values[0]) < 3) {
                mySound3.start();
            }
            if (Math.abs(event.values[0]) > 3 && Math.abs(event.values[0]) < 4) {
                mySound4.start();
            }
            if (Math.abs(event.values[0]) > 4 && Math.abs(event.values[0]) < 5) {
                mySound5.start();
            }
            if (Math.abs(event.values[0]) > 5 && Math.abs(event.values[0]) < 6) {
                mySound6.start();
            }
            if (Math.abs(event.values[0]) > 6 && Math.abs(event.values[0]) < 7) {
                mySound7.start();
            }
            if (Math.abs(event.values[0]) > 7 && Math.abs(event.values[0]) < 8) {
                mySound8.start();
            }
            if (Math.abs(event.values[0]) > 8 && Math.abs(event.values[0]) < 9) {
                mySound9.start();
            }
            if (Math.abs(event.values[0]) > 9 && Math.abs(event.values[0]) < 10) {
                mySound10.start();
            }
            if (Math.abs(event.values[0]) > 10 && Math.abs(event.values[0]) < 11) {
                mySound11.start();
            }
            if (Math.abs(event.values[0]) > 11 && Math.abs(event.values[0]) < 12) {
                mySound12.start();
            }
        }
    }

    public void onPause() {
        super.onPause();
    }
    */
}
