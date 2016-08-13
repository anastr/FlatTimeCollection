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
    Button pause,continueTime,start,stop,success, failure,ready,noAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down_timer);

        mCountDownTimer = (CountDownTimerView) findViewById(R.id.countDownTimerView);
        final EditText editText = (EditText) findViewById(R.id.editText);
        pause = (Button) findViewById(R.id.pause);
        continueTime = (Button) findViewById(R.id.continueTime);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        success = (Button) findViewById(R.id.success);
        failure = (Button) findViewById(R.id.failure);
        ready = (Button) findViewById(R.id.ready);
        noAnimation = (Button) findViewById(R.id.noAnimation);

        mCountDownTimer.setOnTimeFinish(new OnTimeFinish() {
            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "finished", Toast.LENGTH_SHORT).show();
            }
        });
//        mCountDownTimer.setOnEndAnimationFinish(new OnTimeFinish() {
//            @Override
//            public void onFinish() {
//                mCountDownTimer.start(mCountDownTimer.getFullTime());
//            }
//        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.pause();
            }
        });

        continueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.resume();
            }
        });

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

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.stop();
            }
        });

        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.success();
            }
        });

        failure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.failure();
            }
        });

        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.ready();
            }
        });

        noAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.setFinishMode(CountDownTimerView.FinishMode.NoAnimation);
            }
        });
    }
}
