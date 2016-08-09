package com.github.anastr.flattimecollection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.anastr.flattimelib.FlatClockView;
import com.github.anastr.flattimelib.colors.Themes;
import com.github.anastr.flattimelib.intf.OnClockTick;

public class FlatClockActivity extends AppCompatActivity {

    FlatClockView clock;
    EditText et_time;
    Button setTime, setTimeToNow, setLightTheme, setDefaultTheme, setDarkTheme;
    TextView tv_tick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flat_clock);

        clock = (FlatClockView) findViewById(R.id.flatClock);
        et_time = (EditText) findViewById(R.id.time);

        setTime = (Button) findViewById(R.id.seTime);
        setTimeToNow = (Button) findViewById(R.id.setTimeToNow);
        setLightTheme = (Button) findViewById(R.id.setLightTheme);
        setDarkTheme = (Button) findViewById(R.id.setDarkTheme);
        setDefaultTheme = (Button) findViewById(R.id.setDefaultTheme);

        tv_tick = (TextView) findViewById(R.id.tv_tick);

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    clock.setTime(et_time.getText().toString());
                    et_time.setText("");
                } catch (Exception e){
                    et_time.setError(e.getMessage());
                }
            }
        });

        setTimeToNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clock.setTimeToNow();
            }
        });

        setLightTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clock.setTheme(Themes.LightTheme);
            }
        });

        setDefaultTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clock.setTheme(Themes.DefaultTheme);
            }
        });

        setDarkTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clock.setTheme(Themes.DarkTheme);
            }
        });

        clock.setOnClockTick(new OnClockTick() {
            @Override
            public void onTick() {
                tv_tick.append(".");
            }
        });
    }
}
