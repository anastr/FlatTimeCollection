package com.github.anastr.flattimecollection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.anastr.flattimelib.CountDownTimerView;
import com.github.anastr.flattimelib.intf.OnTimeFinish;

public class CountDownTimerActivity extends AppCompatActivity {

    CountDownTimerView mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down_timer);

        mCountDownTimer = (CountDownTimerView) findViewById(R.id.countDownTimerView);
        final EditText editText = (EditText) findViewById(R.id.editText);
        Button pause = (Button) findViewById(R.id.pause);
        final Button continueTime = (Button) findViewById(R.id.continueTime);
        Button start = (Button) findViewById(R.id.start);
        Button stop = (Button) findViewById(R.id.stop);
        Button success = (Button) findViewById(R.id.success);
        Button failed = (Button) findViewById(R.id.failed);
        Button ready = (Button) findViewById(R.id.ready);

        mCountDownTimer.setOnTimeFinish(new OnTimeFinish() {
            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "finish", Toast.LENGTH_SHORT).show();
            }
        });

        assert pause != null;
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.pause();
            }
        });

        assert continueTime != null;
        continueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.resume();
            }
        });

        assert start != null;
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mCountDownTimer.start(Long.parseLong(editText.getText().toString()));
                } catch (Exception e){
                    editText.setError(e.getMessage());
                }
            }
        });

        assert stop != null;
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.stop();
            }
        });

        assert success != null;
        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.success();
            }
        });

        assert failed != null;
        failed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.failed();
            }
        });

        assert ready != null;
        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.ready();
            }
        });
    }
}
