package com.github.anastr.flattimelib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.github.anastr.flattimelib.colors.Colors;
import com.github.anastr.flattimelib.colors.Themes;
import com.github.anastr.flattimelib.intf.OnClockTick;

import java.util.Calendar;

public class FlatClockView extends View{

    private Path minIndicator, hourIndicator, secIndicator;
    private Path bigMark, smallMark;
    private Paint indicatorPaint, bigMarkPaint, smallMarkPaint, backgroundPaint;
    private int minIndicatorColor = Color.parseColor("#49c3cf")
            , hourIndicatorColor = Color.parseColor("#ec8022")
            , secIndicatorColor = Color.parseColor("#212121");
    private int bigMarkColor = Color.parseColor("#f43c3c")
            , smallMarkColor = Color.parseColor("#b3b600")
            , backgroundCircleColor = Color.parseColor("#fcfca4");
    boolean withBackground = true;

    private Calendar mCalendar;
    private OnClockTick onClockTick = null;
    private long customTimeSet, customTimeStarted;
    private boolean isAttached = false, isCustomTime = false;
    private Runnable updateViewRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();

            if (onClockTick != null)
                onClockTick.onTick();
            if (isAttached)
                postDelayed(this, 1000);
        }
    };

    public FlatClockView(Context context) {
        super(context);
        init();
    }

    public FlatClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttributeSet(context, attrs);
    }

    public FlatClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int size = (width > height) ? height : width;
        setMeasuredDimension(size, size);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FlatClockView, 0, 0);

        minIndicatorColor = a.getColor(R.styleable.FlatClockView_minIndicatorColor, minIndicatorColor);
        hourIndicatorColor = a.getColor(R.styleable.FlatClockView_hourIndicatorColor, hourIndicatorColor);
        secIndicatorColor = a.getColor(R.styleable.FlatClockView_secIndicatorColor, secIndicatorColor);
        bigMarkColor = a.getColor(R.styleable.FlatClockView_bigMarkColor, bigMarkColor);
        smallMarkColor = a.getColor(R.styleable.FlatClockView_smallMarkColor, smallMarkColor);
        backgroundCircleColor = a.getColor(R.styleable.FlatClockView_backgroundCircleColor, backgroundCircleColor);
        withBackground = a.getBoolean(R.styleable.FlatClockView_withBackground, true);
        String time = a.getString(R.styleable.FlatClockView_time);
        a.recycle();
        if (time != null)
            setTime(time);
    }

    private void init() {
        minIndicator = new Path();
        hourIndicator = new Path();
        secIndicator = new Path();
        bigMark = new Path();
        smallMark = new Path();
        indicatorPaint = new Paint();
        bigMarkPaint = new Paint();
        smallMarkPaint = new Paint();
        backgroundPaint = new Paint();

        indicatorPaint.setAntiAlias(true);
        backgroundPaint.setAntiAlias(true);
        bigMarkPaint.setAntiAlias(true);
        bigMarkPaint.setStyle(Paint.Style.STROKE);
        smallMarkPaint.setAntiAlias(true);
        smallMarkPaint.setStyle(Paint.Style.STROKE);
    }

    private void initDraw(){
        bigMarkPaint.setColor(bigMarkColor);
        smallMarkPaint.setColor(smallMarkColor);
        backgroundPaint.setColor(backgroundCircleColor);
        if(isCustomTime){
            mCalendar.setTimeInMillis(customTimeSet + Calendar.getInstance().getTimeInMillis() -customTimeStarted);
        } else
            mCalendar = Calendar.getInstance();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float minW = w/16f;
        float hourW = w/28f;
        float secW = w/75f;
        float bigMarkH = h/16f;
        float smallMarkH = h/24f;

        minIndicator.moveTo(w/2f, 0f);
        minIndicator.lineTo(w/2f -minW, h/2f);
        minIndicator.lineTo(w/2f +minW, h/2f);
        RectF rectF = new RectF(w/2f -minW, h/2f -minW, w/2f +minW, h/2f +minW);
        minIndicator.addArc(rectF, 0f, 180f);
        minIndicator.moveTo(0f, 0f);

        hourIndicator.moveTo(w/2f, h/4f);
        hourIndicator.lineTo(w/2f -hourW, h/2f);
        hourIndicator.lineTo(w/2f +hourW, h/2f);
        rectF.set(w/2f -hourW, h/2f -hourW, w/2f +hourW, h/2f +hourW);
        hourIndicator.addArc(rectF, 0f, 180f);
        hourIndicator.moveTo(0f, 0f);

        secIndicator.moveTo(w/2f, 0f);
        secIndicator.lineTo(w/2f -secW, h/2f);
        secIndicator.lineTo(w/2f +secW, h/2f);
        rectF.set(w/2f -secW, h/2f -secW, w/2f +secW, h/2f +secW);
        secIndicator.addArc(rectF, 0f, 180f);
        secIndicator.moveTo(0f, 0f);

        bigMark.moveTo(w/2f, 0f);
        bigMark.lineTo(w/2f, bigMarkH);
        bigMark.moveTo(0f, 0f);
        bigMarkPaint.setStrokeWidth(bigMarkH/3f);

        smallMark.moveTo(w/2f, 0f);
        smallMark.lineTo(w/2f, smallMarkH);
        smallMark.moveTo(0f, 0f);
        smallMarkPaint.setStrokeWidth(smallMarkH/3f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        if(withBackground)
            canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/2f, backgroundPaint);

        canvas.save();
        canvas.scale(.88f, .88f, getWidth()/2f, getHeight()/2f);

        canvas.save();
        for(float i=30f; i<=360f; i+=30f){
            canvas.rotate(30f, getWidth()/2f, getHeight()/2f);
            if(i%90f == 0f){
                canvas.drawPath(bigMark, bigMarkPaint);
                continue;
            }
            canvas.drawPath(smallMark, smallMarkPaint);
        }
        // restore from rotate
        canvas.restore();

        canvas.save();
        canvas.rotate(mCalendar.get(Calendar.MINUTE) * 6f, getWidth()/2f, getHeight()/2f);
        indicatorPaint.setColor(minIndicatorColor);
        canvas.drawPath(minIndicator, indicatorPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate((mCalendar.get(Calendar.HOUR) + (Calendar.MINUTE/60f)) * 30f, getWidth()/2f, getHeight()/2f);
        indicatorPaint.setColor(hourIndicatorColor);
        canvas.drawPath(hourIndicator, indicatorPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(mCalendar.get(Calendar.SECOND) * 6f, getWidth()/2f, getHeight()/2f);
        indicatorPaint.setColor(secIndicatorColor);
        canvas.drawPath(secIndicator, indicatorPaint);
        canvas.restore();

        // restore from scale
        canvas.restore();
    }

    /**
     * to set custom Time.
     *
     * @param hour should be between {0,11}
     *
     * @throws IllegalArgumentException if one of params out of range.
     */
    public void setTime(int hour){
        setTime(hour, 0, 0);
    }

    /**
     * to set custom Time.
     *
     * @param hour should be between {0,11}
     * @param minute should be between {0,59}
     *
     * @throws IllegalArgumentException if one of params out of range.
     */
    public void setTime(int hour, int minute){
        setTime(hour, minute, 0);
    }

    /**
     * to set custom Time.
     *
     * @param time it is "hh:mm:ss" should be Like "11:30:00".
     *
     * @throws IllegalArgumentException if one of params out of range,
     *             Or {@code time} doesn't Like "hh:mm:ss".
     * @throws NullPointerException if {@code time ==  null}
     * @throws NumberFormatException
     *             if {@code string} cannot be parsed as an integer value.
     */
    public void setTime(String time){
        String [] t = time.split(":");
        if (t.length != 3)
            throw new IllegalArgumentException("time should be Like 10:43:15");
        setTime(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2]));
    }

    /**
     * to set custom Time.
     *
     * @param hour should be between {0,11}
     * @param minute should be between {0,59}
     * @param second should be between {0,59}
     *
     * @throws IllegalArgumentException if one of params out of range
     */
    public void setTime(int hour, int minute, int second){
        if (hour > 11 || hour <0)
            throw new IllegalArgumentException("Hour should be between {0,11}");
        else if(minute > 59 || minute <0)
            throw new IllegalArgumentException("Minute should be between {0,59}");
        else if(second > 59 || second <0)
            throw new IllegalArgumentException("Second should be between {0,59}");
        isCustomTime = true;
        customTimeStarted = Calendar.getInstance().getTimeInMillis();
        mCalendar = Calendar.getInstance();
        mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)
                , hour, minute, second);
        customTimeSet = mCalendar.getTimeInMillis();
        invalidate();
    }

    public void setTimeToNow(){
        isCustomTime = false;
        invalidate();
    }

    public void setTheme(Themes theme){
        Colors colors = theme.colors;
        minIndicatorColor = colors.minIndicatorColor;
        hourIndicatorColor = colors.hourIndicatorColor;
        secIndicatorColor = colors.secIndicatorColor;
        bigMarkColor = colors.bigMarkColor;
        smallMarkColor = colors.smallMarkColor;
        backgroundCircleColor = colors.backgroundCircleColor;
        invalidate();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
        postDelayed(updateViewRunnable, 1000);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }

    public int getMinIndicatorColor() {
        return minIndicatorColor;
    }

    public void setMinIndicatorColor(int minIndicatorColor) {
        this.minIndicatorColor = minIndicatorColor;
        invalidate();
    }

    public int getHourIndicatorColor() {
        return hourIndicatorColor;
    }

    public void setHourIndicatorColor(int hourIndicatorColor) {
        this.hourIndicatorColor = hourIndicatorColor;
        invalidate();
    }

    public int getSecIndicatorColor() {
        return secIndicatorColor;
    }

    public void setSecIndicatorColor(int secIndicatorColor) {
        this.secIndicatorColor = secIndicatorColor;
        invalidate();
    }

    public int getBigMarkColor() {
        return bigMarkColor;
    }

    public void setBigMarkColor(int bigMarkColor) {
        this.bigMarkColor = bigMarkColor;
        invalidate();
    }

    public int getSmallMarkColor() {
        return smallMarkColor;
    }

    public void setSmallMarkColor(int smallMarkColor) {
        this.smallMarkColor = smallMarkColor;
        invalidate();
    }

    public int getBackgroundCircleColor() {
        return backgroundCircleColor;
    }

    public void setBackgroundCircleColor(int backgroundCircleColor) {
        this.backgroundCircleColor = backgroundCircleColor;
        invalidate();
    }

    public boolean isWithBackground() {
        return withBackground;
    }

    public void setWithBackground(boolean withBackground) {
        this.withBackground = withBackground;
        invalidate();
    }

    /**
     * to do something when the clock tick, this will called every second.
     *
     * @param onClockTick The callback that will run.
     */
    public void setOnClockTick(OnClockTick onClockTick) {
        this.onClockTick = onClockTick;
    }
}
