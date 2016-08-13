package com.github.anastr.flattimelib;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.github.anastr.flattimelib.intf.OnTimeFinish;

import java.util.Random;

public class HourGlassView extends View {

    private Paint hourGlassPaint, linePaint, sandPaint, sandLinePaint;
    private Path hourGlassPath;
    private RectF rectTopSand, rectBottomSand;
    private float strokeWidth;
    private int hourGlassColor = Color.DKGRAY
            , sandColor = Color.parseColor("#967117");

    private ValueAnimator valueAnimator, animatorStart;
    /** in MilliSecond */
    private long fullTime = 1000L;
    /** in MilliSecond */
    private float elapsedTime = 0f;
    private OnTimeFinish onTimeFinish;
    private boolean isFinished = false;

    private float lineSandHeight = 0f;
    private float widthMad = 0f;

    private ValueAnimator flipAnimator;
    private boolean flipping = false, paused = false;
    private float flipValue;

    public HourGlassView(Context context) {
        super(context);
        init();
    }

    public HourGlassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttributeSet(context, attrs);
    }

    public HourGlassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

    private void init() {
        hourGlassPaint = new Paint();
        linePaint = new Paint();
        sandPaint = new Paint();
        sandLinePaint = new Paint();
        hourGlassPath = new Path();

        hourGlassPaint.setAntiAlias(true);
        hourGlassPaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        sandPaint.setAntiAlias(true);
        sandLinePaint.setAntiAlias(true);

        // these two line just to make animatorStart and valueAnimator != null
        animatorStart  = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        flipAnimator = ValueAnimator.ofFloat(0f, 1f);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HourGlassView, 0, 0);

        hourGlassColor = a.getColor(R.styleable.HourGlassView_hourGlassColor, hourGlassColor);
        sandColor = a.getColor(R.styleable.HourGlassView_sandColor, sandColor);
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        strokeWidth = h/20f;
        hourGlassPaint.setStrokeWidth(strokeWidth);
        linePaint.setStrokeWidth(strokeWidth);
        sandLinePaint.setStrokeWidth(strokeWidth/2f);

        RectF rectTopGlass = new RectF(strokeWidth / 2f +(w/4f), strokeWidth + (strokeWidth / 2f)
                , w - (strokeWidth / 2f) -(w/4f), h / 2f);
        RectF rectBottomGlass = new RectF(strokeWidth / 2f +(w/4f), rectTopGlass.bottom
                , w - (strokeWidth / 2f) -(w/4f), h - strokeWidth - (strokeWidth / 2f));

        rectTopSand = new RectF(rectTopGlass.left +strokeWidth/2f, rectTopGlass.top +strokeWidth/2f
                , rectTopGlass.right -strokeWidth/2f, rectTopGlass.bottom -strokeWidth/2f);

        rectBottomSand = new RectF(rectBottomGlass.left +strokeWidth/2f, rectBottomGlass.top +strokeWidth/2f
                , rectBottomGlass.right -strokeWidth/2f, rectBottomGlass.bottom -strokeWidth/2f);

        hourGlassPath.addArc(rectTopGlass, 87f, -354f);
        hourGlassPath.addArc(rectBottomGlass, -87f, 354f);
    }

    private void initDraw() {
        hourGlassPaint.setColor(hourGlassColor);
        linePaint.setColor(hourGlassColor);
        sandPaint.setColor(sandColor);
        sandLinePaint.setColor(sandColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        float percent = 135f*elapsedTime /(float)fullTime;
        float w = getWidth();

        if (flipping) {
            canvas.save();
            canvas.rotate(flipValue *180f, w/2f, getHeight()/2f);
            canvas.scale(getFlipScale(), getFlipScale(), w/2f, getHeight()/2f);
            canvas.save();
            canvas.rotate(-flipValue *180f, rectTopSand.centerX(), rectTopSand.centerY());
        }
        canvas.drawArc(rectTopSand, -45f +percent, 270f -(percent*2), false, sandPaint);
        if (flipping) {
            canvas.restore();
            canvas.save();
            canvas.rotate(-flipValue *180f, rectBottomSand.centerX(), rectBottomSand.centerY());
        }
        canvas.drawArc(rectBottomSand, 90f -percent, (percent*2), false, sandPaint);
        if (flipping)
            canvas.restore();
        canvas.drawLine(w/2f, rectTopSand.bottom, w/2f +widthMad, rectTopSand.bottom + lineSandHeight, sandLinePaint);

        canvas.drawPath(hourGlassPath, hourGlassPaint);

        canvas.drawLine(strokeWidth/2f +(w/4f), strokeWidth/2f
                , w -(strokeWidth/2f) -(w/4f), strokeWidth/2f, linePaint);
        canvas.drawLine(strokeWidth/2f +(w/4f), getHeight() -(strokeWidth/2f)
                , w -(strokeWidth/2f) -(w/4f), getHeight() -(strokeWidth/2f), linePaint);

        if (flipping)
            canvas.restore();
    }

    private float getFlipScale() {
        float flipValue = Math.abs(this.flipValue - .5f);
        return .5f + flipValue *.5f *2;
    }

    private void startValueAnimator(float startSec, float endSec, long timeMilliSec){
        paused = false;
        final Random mad = new Random();
        valueAnimator = ValueAnimator.ofFloat(startSec, endSec);
        valueAnimator.setDuration(timeMilliSec);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                elapsedTime = (float) valueAnimator.getAnimatedValue()*1000;
                widthMad = mad.nextFloat()*2f -1f;
                postInvalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isFinished && onTimeFinish != null)
                    onTimeFinish.onFinish();
                widthMad = 0f;
                lineSandHeight = 0f;
                postInvalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.start();
    }

    /**
     * start the HourGlass.
     * this well stop HourGlass if it was running.
     * @param timeInMillisecond time in Millisecond.
     */
    public void start(long timeInMillisecond) {
        stop();
        fullTime = timeInMillisecond ;
        elapsedTime = 0f;
        playAnimationStart();
    }

    /**
     * Ends the HourGlass. this will not call
     * {@link OnTimeFinish#onFinish()} method on
     * its listeners.
     */
    public void stop(){
        paused = false;
        isFinished = true;
        animatorStart.end();
        flipAnimator.end();
        valueAnimator.end();
        isFinished = false;
        invalidate();
    }

    private void pause() {
        if (!valueAnimator.isRunning() || paused)
            return;
        paused = true;
        isFinished = true;
        animatorStart.end();
        valueAnimator.cancel();
        isFinished = false;
    }

    public void ready(){
        stop();
        elapsedTime = 0f;
        invalidate();
    }

    public void flip() {
        if (flipping || elapsedTime == 0f)
            return;
        pause();
        flipping = true;
        flipAnimator = ValueAnimator.ofFloat(0f, 1f);
        flipAnimator.setDuration(1000);
        flipAnimator.setInterpolator(new LinearInterpolator());
        flipAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                flipValue = (float) flipAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        flipAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                flipping = false;
                if(!isFinished) {
                    elapsedTime = (float) fullTime - elapsedTime;
                    playAnimationStart();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        flipAnimator.start();
    }

    private void playAnimationStart() {
        animatorStart  = ValueAnimator.ofFloat(0f, rectBottomSand.bottom -rectTopSand.bottom);
        animatorStart.setDuration(250);
        animatorStart.setInterpolator(new LinearInterpolator());
        animatorStart.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lineSandHeight = (float) animatorStart.getAnimatedValue();
                postInvalidate();
            }
        });
        animatorStart.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isFinished) {
                    if (paused)
                        startValueAnimator(elapsedTime /1000f, (float)fullTime /1000f, (long)(fullTime - elapsedTime));
                    else
                        startValueAnimator(0f, (float) fullTime / 1000f, fullTime);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorStart.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    public void setOnTimeFinish(OnTimeFinish onTimeFinish) {
        this.onTimeFinish = onTimeFinish;
    }

    /**
     * The time that has elapsed.
     *
     * @return ElapsedTime in millisecond.
     */
    public long getElapsedTime() {
        return (long) elapsedTime;
    }

    /**
     * The full time that you set by {@link #start(long)} method.
     *
     * @return FullTime in millisecond.
     */
    public long getFullTime() {
        return fullTime;
    }

    /**
     * Time that remains.
     *
     * @return RemainingTime in millisecond.
     */
    public long getRemainingTime() {
        return fullTime - (long)elapsedTime;
    }

    /**
     * use if you need to check if HourGlass can flip or not.
     *
     * @return {@code true} if HourGlass doesn't flipping
     * or doesn't started yet, {@code false} otherwise.
     */
    public boolean canFlip() {
        return (!flipping && elapsedTime != 0f);
    }

    /**
     * @return {@code true} if HourGlass doing flip, {@code false} otherwise.
     */
    public boolean isFlipping() {
        return flipping;
    }

    public int getHourGlassColor() {
        return hourGlassColor;
    }

    public void setHourGlassColor(int hourGlassColor) {
        this.hourGlassColor = hourGlassColor;
        invalidate();
    }

    public int getSandColor() {
        return sandColor;
    }

    public void setSandColor(int sandColor) {
        this.sandColor = sandColor;
        invalidate();
    }
}
