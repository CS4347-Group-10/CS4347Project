package cs4347group10.cs4347application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.media.MediaPlayer;
import android.widget.Button;
import android.view.View.OnTouchListener;

public class MainActivity extends AppCompatActivity {

    // default media player
    final MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create onTouch event for the button with id 'sound_button'
        Button button = (Button) findViewById(R.id.sound_button);
        button.setOnTouchListener(oTL);
    }

    // Function that is called when the button is touched
    // which can be used for both single sound and continuous sound
    public OnTouchListener oTL = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_BUTTON_PRESS:
                    break;
                case MotionEvent.ACTION_BUTTON_RELEASE:
                    break;
            }
            return true;
        }
    };

}
