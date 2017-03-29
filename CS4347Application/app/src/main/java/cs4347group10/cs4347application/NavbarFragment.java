package cs4347group10.cs4347application;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

/**
 * Created by Brendan on 3/29/2017.
 */

public class NavbarFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navbar, container, false);

        ToggleButton modeLockBtn = (ToggleButton) view.findViewById(R.id.mode_lock_button);
        Button pianoModeBtn = (Button) view.findViewById(R.id.piano_mode_button);
        Button drumModeBtn = (Button) view.findViewById(R.id.drum_mode_button);

        modeLockBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // If it is checked, do not switch mode when tilting
            }
        });
        pianoModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), PianoMode.class );
                startActivity(myIntent);
            }
        });
        drumModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), DrumMode.class );
                startActivity(myIntent);
            }
        });

        return view;
    }

}
