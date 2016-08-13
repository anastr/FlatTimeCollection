package com.github.anastr.flattimelib;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.github.anastr.flattimelib.intf.OnTimeFinish;

public class CountDownTimerView extends View{

    private Path indicator;
    private Paint paint, strokePaint;
    private RectF rectCenter;
    private int indicatorColor = Color.parseColor("#5952ff")
            , remainingTimeColor = Color.parseColor("#5af960")
            , strokeColor = Color.parseColor("#302d2d")
            , elapsedTimeColor = Color.parseColor("#ff5c5c");
    private float strokeWidth = 10f;
    private ValueAnimator valueAnimator;
    /** in MilliSecond */
    private long fullTime = 1000L;
    /** in MilliSecond */
    private float elapsedTime = 0f;
    private boolean isFinished = false;
    private boolean mPaused = false;
    private OnTimeFinish onTimeFinish;
    private OnTimeFinish onEndAnimationFinish;

    private AnimatorSet animatorSet;
    private int finishMode = FinishMode.Default.mode;
    boolean drawFinish = false;

    public CountDownTimerView(Context context) {
        super(context);
        init();
    }

    public CountDownTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttributeSet(context, attrs);
    }

    public CountDownTimerView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CountDownTimerView, 0, 0);

        indicatorColor = a.getColor(R.styleable.CountDownTimerView_indicatorColor, indicatorColor);
        remainingTimeColor = a.getColor(R.styleable.CountDownTimerView_remainingTimeColor, remainingTimeColor);
        strokeColor = a.getColor(R.styleable.CountDownTimerView_strokeColor, strokeColor);
        elapsedTimeColor = a.getColor(R.styleable.CountDownTimerView_elapsedTimeColor, elapsedTimeColor);
        strokeWidth = a.getFloat(R.styleable.CountDownTimerView_strokeWidth, strokeWidth);
        a.recycle();
    }

    private void init() {
        indicator = new Path();
        paint = new Paint();
        strokePaint = new Paint();
        rectCenter = new RectF();

        paint.setAntiAlias(true);
        strokePaint.setAntiAlias(true);
        strokePaint.setStyle(Paint.Style.STROKE);
        if(isInEditMode())
            elapsedTime = 250f;

        // these two line just to make valueAnimator and animatorSet != null
        valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        animatorSet = new AnimatorSet();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float risk = strokeWidth/2f;
        rectCenter.set(0f +risk, 0f +risk, w -risk, h -risk);

        float indW = w/16f;

        indicator.moveTo(w/2f, 0f);
        indicator.lineTo(w/2f -indW, h/2f);
        indicator.lineTo(w/2f +indW, h/2f);
        RectF rectF = new RectF(w/2f -indW, h/2f -indW, w/2f +indW, h/2f +indW);
        indicator.addArc(rectF, 0f, 180f);
        indicator.moveTo(0f, 0f);
    }

    private void initDraw(){
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        if(!drawFinish) {
            paint.setColor(remainingTimeColor);
            canvas.drawOval(rectCenter, paint);

            paint.setColor(elapsedTimeColor);
            canvas.drawArc(rectCenter, -90f, elapsedTime * 360f / fullTime, true, paint);

            canvas.drawArc(rectCenter, 0f, 360f, false, strokePaint);

            canvas.save();
            canvas.rotate(elapsedTime * 360f / fullTime, getWidth() / 2f, getHeight() / 2f);
            paint.setColor(indicatorColor);
            canvas.drawPath(indicator, paint);
            canvas.restore();
        }
        else {
            drawFinish = false;
            canvas.drawBitmap(getBitmapFinish(), 0f, 0f, new Paint());
        }
    }

    private void startValueAnimator(float startSec, float endSec, long timeMilliSec){
        valueAnimator = ValueAnimator.ofFloat(startSec, endSec);
        valueAnimator.setDuration(timeMilliSec);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                elapsedTime = (float) valueAnimator.getAnimatedValue()*1000;
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
                if (!isFinished && finishMode != FinishMode.NoAnimation.mode)
                    startEndAnimation();
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
     * start the CountDownTimer.
     * this well stop CountDownTimer if it was running.
     * @param timeInMillisecond time in Millisecond.
     */
    public void start(long timeInMillisecond) {
        stop();
        fullTime = timeInMillisecond;
        startValueAnimator(0f, (float)timeInMillisecond/1000f, fullTime);
    }

    /**
     * Ends the CountDownTimer. this will not call
     * {@link OnTimeFinish#onFinish()} method on
     * its listeners.
     */
    public void stop(){
        mPaused = false;
        isFinished = true;
        animatorSet.end();
        valueAnimator.end();
        isFinished = false;
        invalidate();
    }

    /**
     * Pauses a running CountDownTimer. This method should only be called on the same thread on
     * which the CountDownTimer was started. If the CountDownTimer has not yet been {@link
     * #start(long)} started} or has since ended, then the call is ignored. Paused
     * CountDownTimer can be resumed by calling {@link #resume()}.
     *
     * @see #resume()
     */
    public void pause(){
        if (!valueAnimator.isRunning() || mPaused)
            return;
        mPaused = true;
        isFinished = true;
        valueAnimator.cancel();
        isFinished = false;
    }

    /**
     * Resumes a paused CountDownTimer, causing the CountDownTimer to pick up where it left off
     * when it was paused. This method should only be called on the same thread on
     * which the CountDownTimer was started. Calls to resume() on an CountDownTimer that is
     * not currently paused will be ignored.
     *
     * @see #pause()
     */
    public void resume() {
        if(mPaused && !valueAnimator.isRunning()) {
            mPaused = false;
            startValueAnimator(elapsedTime / 1000f, fullTime / 1000f, (long) (fullTime - elapsedTime));
        }
    }

    /**
     * stop the CountDownTimer and start success Animation.
     * this will called {@link OnTimeFinish#onFinish()} method on
     * its listeners which set By {@link #setOnEndAnimationFinish(OnTimeFinish)}.
     *
     * <p>this method will change finish mode to Success.</p>
     * @see #setFinishMode(FinishMode)
     */
    public void success(){
        finishMode = FinishMode.Success.mode;
        startEndAnimation();
    }

    /**
     * @deprecated Use {@link #failure()}.
     */
    @Deprecated
    public void failed(){
        failure();
    }

    /**
     * stop the CountDownTimer and start failure Animation.
     * this will called {@link OnTimeFinish#onFinish()} method on
     * its listeners which set By {@link #setOnEndAnimationFinish(OnTimeFinish)}.
     *
     * <p>this method will change finish mode to Failure.</p>
     * @see #setFinishMode(FinishMode)
     */
    public void failure(){
        finishMode = FinishMode.Failure.mode;
        startEndAnimation();
    }

    public void ready(){
        stop();
        elapsedTime = 0f;
        invalidate();
    }

    private void startEndAnimation() {
        pause();
        mPaused = false;
        animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0f);
        scaleX.setDuration(250);
        scaleY.setDuration(250);

        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(this, "scaleX", 1f);
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(this, "scaleY", 1f);
        scaleX2.setDuration(250);
        scaleY2.setDuration(250);
        scaleX2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                drawFinish = true;
                postInvalidate();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isFinished && onEndAnimationFinish != null)
                    onEndAnimationFinish.onFinish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.play(scaleX2).with(scaleY2).after(scaleX).after(scaleY);
        animatorSet.start();
    }

    private Bitmap getBitmapFinish(){
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        Paint p = new Paint();
        p.setAntiAlias(true);
        switch (finishMode){
            case -1:
                p.setColor(Color.parseColor("#ff5c5c"));
                c.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/2f, p);
                p.setColor(Color.WHITE);
                p.setStrokeWidth(7f);
                c.save();
                c.scale(.6f, .6f, getWidth()/2f, getHeight()/2f);
                c.drawLine(0f, 0f, getWidth(), getHeight(), p);
                c.drawLine(getWidth(), 0f, 0f, getHeight(), p);
                c.restore();
                break;
            case 0:
                p.setColor(Color.parseColor("#dcc61c"));
                c.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/2f, p);
                p.setColor(Color.WHITE);
                c.drawCircle(getWidth()/2f, getHeight()*4f/5f, 4f, p);
                p.setStrokeWidth(8f);
                c.drawLine(getWidth()/2f, getHeight()/14f, getWidth()/2f, getHeight()*2f/3f, p);
                break;
            case 1:
                p.setColor(Color.parseColor("#49e64f"));
                c.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/2f, p);
                p.setColor(Color.WHITE);
                p.setStrokeWidth(7f);
                c.save();
                c.rotate(45f, getWidth()/2.3f, getHeight()*2f/3f);
                c.drawLine(getWidth()/2.3f, getHeight()*2f/3f, getWidth()/2.3f, getHeight()/8f, p);
                c.rotate(-88f, getWidth()/2.3f, getHeight()*2f/3f);
                c.drawLine(getWidth()/2.3f +3.5f, getHeight()*2f/3f, getWidth()/2.3f, getHeight()/2.5f, p);
                c.restore();
                break;
        }
        return bitmap;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    public boolean isCountDownTimerRunning() {
        return valueAnimator.isRunning();
    }

    /**
     * this will called just when time finished.
     *
     * @see #setOnEndAnimationFinish(OnTimeFinish)
     */
    public void setOnTimeFinish(OnTimeFinish onTimeFinish) {
        this.onTimeFinish = onTimeFinish;
    }

    /**
     * this will called after End Animation finished.
     * (Default, Success, Failure).
     */
    public void setOnEndAnimationFinish(OnTimeFinish onTimeFinish) {
        this.onEndAnimationFinish = onTimeFinish;
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
     * Or default: 1000.
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

    public int getFinishMode() {
        return finishMode;
    }

    /**
     * to change End Animation, Once implemented the time.
     *
     * @param finish enum value {@link FinishMode}.
     */
    public void setFinishMode(FinishMode finish) {
        this.finishMode = finish.mode;
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public int getRemainingTimeColor() {
        return remainingTimeColor;
    }

    public void setRemainingTimeColor(int remainingTimeColor) {
        this.remainingTimeColor = remainingTimeColor;
        invalidate();
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        invalidate();
    }

    public int getElapsedTimeColor() {
        return elapsedTimeColor;
    }

    public void setElapsedTimeColor(int elapsedTimeColor) {
        this.elapsedTimeColor = elapsedTimeColor;
        invalidate();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        invalidate();
    }

    /**
     * Returns whether this CountDownTimer is currently in a paused state.
     *
     * @return True if the CountDownTimer is currently paused, false otherwise.
     *
     * @see #pause()
     * @see #resume()
     */
    public boolean isPaused() {
        return mPaused;
    }

    public enum FinishMode {
        NoAnimation(-2),
        Failure(-1),
        Default(0),
        Success(1);

        int mode;
        FinishMode(int mode){
            this.mode = mode;
        }
    }
}
