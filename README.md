# FlatTimeCollection
Amazing Dynamic Time UI :clock1030: :hourglass: for Android To help you design your Layout.
it is Not just a UI, But it contains a CountDownTimer with `pause()` and `resume()` methods :wink: .

`minSdkVersion=11`

Library Size ~ 10 Kb.

<img src="/images/all.png" width="40%" /><br/>
<img src="/images/HourGlassView.gif" width="5%" /><br/>
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-FlatTimeCollection-green.svg?style=true)](https://android-arsenal.com/details/1/4104)<br/><br/>

**note:** this Library doesn't use any drawable file (png, jpge ...), it just uses [Path](https://developer.android.com/reference/android/graphics/Path.html) class to draw canvas.

# dependencies 

**add this line to** `build.gradle`

```gradle

dependencies {
	    compile 'com.github.anastr:flattimelib:1.0.0'
}

```

## FlatClockView
Clock View with Dynamic colors, themes, and time -see correct time in preview-.
The time can be adjusted By using `setTime()` method.
And You can change the color of everything as well.

<img src="/images/FlatClockView.gif" width="30%" />

**in layout**

```xml

<com.github.anastr.flattimelib.FlatClockView
        android:id="@+id/mFlatClock"
        android:layout_width="150dp"
        android:layout_height="150dp"
        app:hourIndicatorColor="#ef2f2f" />

```

control in your code
```java
// you can change Theme in your code By this simple line :
// enum values (Themes.DefaultTheme, Themes.LightTheme, Themes.DarkTheme)
mFlatClock.setTheme(Themes.DarkTheme);

// set your custom time (hh:mm:ss)
mFlatClock.setTime(5, 30, 0);
mFlatClock.setTime("1:00:40");

// Listener to be called every second
mFlatClock.setOnClockTick(new OnClockTick() {
            @Override
            public void onTick() {
                // TODO do something evry second.
            }
        });

/*
 * and also see setHourIndicatorColor(), getSecIndicatorColor(), getBigMarkColor(), setWithBackground() .......
 **/
```

### Attributes
```xml
minIndicatorColor=""
hourIndicatorColor=""
secIndicatorColor=""
bigMarkColor=""
smallMarkColor=""
backgroundCircleColor=""
time="11:30:00"
withBackground="true"
```

## CountDownTimerView
it is UI CountDownTimer with `pause()` and `resume()` methods, also with Success and Failure Animations.
You can change the color of everything.

<img src="/images/CountDownTimerView.gif" width="40%" />

add view to layout
```xml

<com.github.anastr.flattimelib.CountDownTimerView
        android:id="@+id/mCountDownTimer"
        android:layout_width="150dp"
        android:layout_height="150dp" />

```

control in your code
```java
// to start CountDownTimer "time in millisecond"
mCountDownTimer.start(11000);

// to pause CountDownTimer
mCountDownTimer.pause();

// Resumes a paused CountDownTimer
mCountDownTimer.resume();

// Success
mCountDownTimer.success();

// Failed
mCountDownTimer.failed();

// on time finish
mCountDownTimer.setOnTimeFinish(new OnTimeFinish() {
            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "finish", Toast.LENGTH_SHORT).show();
            }
        });

// on (success, failed) animation finish
mCountDownTimer.setOnEndAnimationFinish(new OnTimeFinish() {
            @Override
            public void onFinish() {
                // ---
            }
        }); 

```

### Attributes
```xml
indicatorColor=""
remainingTimeColor=""
strokeColor=""
elapsedTimeColor=""
strokeWidth="10"
```

## HourGlassView
Coming Soon .....

<img src="/images/HourGlassView.gif" width="15%" />


# LICENSE
```

Copyright 2016 Anas ALtair

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```
