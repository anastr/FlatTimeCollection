package com.github.anastr.flattimecollection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button b_flatClock, b_countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b_flatClock = (Button) findViewById(R.id.FlatClock);
        b_countDownTimer = (Button) findViewById(R.id.CountDownTimer);

        b_flatClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FlatClockActivity.class);
                startActivity(intent);
            }
        });

        b_countDownTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CountDownTimerActivity.class);
                startActivity(intent);
            }
        });
    }
}
