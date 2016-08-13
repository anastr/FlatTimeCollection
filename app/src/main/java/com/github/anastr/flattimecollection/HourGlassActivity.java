package com.github.anastr.flattimecollection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.anastr.flattimelib.HourGlassView;
import com.github.anastr.flattimelib.intf.OnTimeFinish;

public class HourGlassActivity extends AppCompatActivity {

    HourGlassView mHourGlass;
    EditText time;
    Button start, stop, flip, ready;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hour_glass);

        mHourGlass = (HourGlassView) findViewById(R.id.mHourGlass);
        time = (EditText) findViewById(R.id.time);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        flip = (Button) findViewById(R.id.flip);
        ready = (Button) findViewById(R.id.ready);

        mHourGlass.setOnTimeFinish(new OnTimeFinish() {
            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "finished", Toast.LENGTH_SHORT).show();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    mHourGlass.start(Long.parseLong(time.getText().toString()) );
                } catch (Exception e) {
                    time.setError(e.getMessage());
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHourGlass.stop();
            }
        });

        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHourGlass.canFlip())
                    mHourGlass.flip();
            }
        });

        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHourGlass.ready();
            }
        });
    }
}
