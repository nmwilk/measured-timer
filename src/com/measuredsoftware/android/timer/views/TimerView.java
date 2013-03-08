package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import com.measuredsoftware.android.library2.utils.CoordTools;
import com.measuredsoftware.android.library2.utils.MathTools;
import com.measuredsoftware.android.library2.utils.ValueTools;
import com.measuredsoftware.android.timer.ColorFilterTools;
import com.measuredsoftware.android.timer.Colourable;
import com.measuredsoftware.android.timer.Globals;
import com.measuredsoftware.android.timer.R;
import com.measuredsoftware.android.timer.RotatableImageView;
import com.measuredsoftware.android.timer.views.TimerTextView.TextType;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * The rotatable view that starts a timer.
 * 
 * @author neil
 * 
 */
public class TimerView extends RotatableImageView implements Colourable
{
    /** the events generated by the timer */
    public static interface OnEventListener
    {
        /**
         * rotation started
         * 
         * @param seconds
         */
        void started(int seconds);

        /**
         * more rotation occurred
         * 
         * @param seconds
         */
        void valueChanged(int seconds);

        /** called when started called by no value changed. */
        void cancelled();
    }

    protected static final float maxTotalAngle = (360 * 72) - 1;

    protected OnEventListener eventListener;
    protected int currentTimeSecs;

    private boolean countdownAction = false;

    private float prevAngle;
    private float totalAngle; // includes if we've looped around

    private Paint textPaintCountdown;
    private Paint textPaintTarget;

    private String msCountdownTime;
    private String msEndTime;

    private long endTimeMS;

    private boolean settingTime;

    private boolean alarmRinging;

    private int countdownTimePosX;
    private int countdownTimePosY;
    private int endtimePosX;
    private int endtimePosY;

    private final Drawable touchGlow;
    private final int touchGlowWidth;
    private final int touchGlowHeight;
    private float dotProgress = 0f;

    private Drawable innerRing;

    private float cachedHue = -1f;
    
    private ObjectAnimator glowAnimation;

    // private final ColorMatrix hueMatrix = new ColorMatrix();

    /**
     * @param context
     * @param attrs
     */
    public TimerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        eventListener = null;

        setClickable(true);

        super.setIncrement(1);

        endTimeMS = 0;
        currentTimeSecs = 0;
        settingTime = false;
        alarmRinging = false;

        final Drawable back = this.getBackground();
        if (back == null) throw new RuntimeException("Must supply a background to TimerView.");

        countdownAction = false;

        setDigitalTimeTo(0);

        touchGlow = getResources().getDrawable(R.drawable.touch_glow);
        
        if (touchGlow == null) throw new RuntimeException("Failed to load touch glow Bitmap.");
        
        touchGlowWidth = touchGlow.getIntrinsicWidth();
        touchGlowHeight = touchGlow.getIntrinsicHeight();
    }

    /**
     * @param listener
     *            The listener for the events this generates.
     */
    public void setOnSetValueChangedListener(final OnEventListener listener)
    {
        eventListener = listener;
    }

    /**
     * @param colour
     *            ARGB int
     */
    public void setTextColourTarget(final int colour)
    {
        textPaintTarget.setColor(colour);
    }

    @Override
    public void setEnabled(final boolean enabled)
    {
        super.setEnabled(enabled);

        updateVisibleStates();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event)
    {
        if (!isEnabled()) return true;
        
        if (glowAnimation != null) {
            glowAnimation.end();
            glowAnimation = null;
        }

        // don't allow moving of timer if ringing
        if (alarmRinging)
        {
            return super.onTouchEvent(event);
        }

        final boolean b = super.onTouchEvent(event);

        // parent handled it?
        if (b) return true;

        int msgRes = -1;
        int msgValue = -1;

        final int action = event.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                currentTimeSecs = convertAngleToSecs(totalAngle);
                msgRes = 2;
                setCountdownActive(false);
                settingTime = true;
                prevAngle = mAngle;
                break;
            case MotionEvent.ACTION_UP:
                if (!mMultitouchActive)
                {
                    settingTime = false;
                    // start the timer
                    msgRes = 0;

                    // set the end time
                    if (totalAngle > 0)
                    {
                        msgValue = this.currentTimeSecs;
                    }
                    setEndTime(0);
                    currentTimeSecs = 0;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // no change?
                if (prevAngle == mAngle) break;

                // calculate difference since last MOVE (complicated due to
                // passing 359->1)
                final float angleDiff;
                // passed 359->1?
                if (prevAngle > 270 && (mAngle > 0 && mAngle < 90))
                {
                    angleDiff = (mAngle + 360) - prevAngle;
                }
                // passed 1>359?
                else if ((prevAngle >= 0 && prevAngle < 90) && mAngle > 270)
                {
                    angleDiff = mAngle - (prevAngle + 360);
                }
                else
                {
                    angleDiff = mAngle - prevAngle;
                }

                totalAngle += angleDiff;
                if (totalAngle > maxTotalAngle)
                {
                    totalAngle = maxTotalAngle;
                    mAngle = (totalAngle % 360);
                }
                else if (totalAngle < 0)
                {
                    totalAngle = 0;
                    mAngle = 0;
                }

                currentTimeSecs = convertAngleToSecs(totalAngle);
                msgValue = currentTimeSecs;
                msgRes = 1;

                this.setDigitalTimeTo(currentTimeSecs);

                prevAngle = mAngle;
                break;
        }

        if (eventListener != null && msgRes != -1)
        {
            switch (msgRes)
            {
                case 0:
                    eventListener.started(msgValue);
                    break;
                case 1:
                    eventListener.valueChanged(msgValue);
                    break;
                default:
                    eventListener.cancelled();
                    break;
            }
        }

        updateCountdownTimeFilter();

        return true;
    }

    private void updateCountdownTimeFilter()
    {
        textPaintCountdown.setAlpha(settingTime ? 255 : 100);
        final int shadowColour = settingTime ? 0x7F000000 : 0x3F000000;
        TimerTextView.styleShadow(textPaintCountdown, textPaintCountdown.getTextSize(), shadowColour);
        // textPaintCountdown.setColorFilter(settingTime ? null : textDimmer);
    }

    private void setEndTime(final long ms)
    {
        setCountdownActive(ms > 0);

        endTimeMS = ms;
        if (ms == 0)
        {
            setSecsRemaining(0);
        }
        else
        {
            setSecsRemaining((int) (endTimeMS - System.currentTimeMillis()) / 1000);
        }

        if (countdownAction)
        {
            this.setEndClockTo((int) ((endTimeMS - System.currentTimeMillis()) / 1000));
        }

        updateNowTime();
    }

    /** update the clock */
    public void updateNowTime()
    {
        this.setEndClockTo(currentTimeSecs);

        this.invalidate();
    }

    private void setCountdownActive(final boolean b)
    {
        countdownAction = b;
    }

    protected static int convertAngleToSecs(final float angle)
    {
        final int time = MathTools.roundToNearest((int) (angle * 10), 60);
        return time;
    }

    protected static float convertSecsToAngle(final int time)
    {
        return (time / 10);
    }

    @Override
    protected void onDraw(final Canvas canvas)
    {
        super.onDraw(canvas);

        initialiseOnFirstDraw();

        innerRing.draw(canvas);

        updateCountdownTimeFilter();

        // draw digital times
        canvas.drawText(msCountdownTime, countdownTimePosX, countdownTimePosY, textPaintCountdown);
        canvas.drawText(msEndTime, endtimePosX, endtimePosY, textPaintTarget);

        // draw glow
        drawTouchDot(canvas);
    }

    private final static float PROGRESS_FADE_IN = 0.1f;
    private final static float PROGRESS_FADE_OUT = 0.1f;
    private final static float PROGRESS_ANGLE_END = 280f;
    private final Interpolator interpolatorFadeIn = new AccelerateInterpolator(); 
    private final Interpolator interpolatorFadeOut = new AccelerateInterpolator(); 
    private final Interpolator interpolatorMove = new AccelerateDecelerateInterpolator();
    private final PointF position = new PointF();
    
    private void drawTouchDot(final Canvas canvas)
    {
        if (dotProgress != 0f && dotProgress < 1f)
        {
            final float alpha;
            final float angle;

            if (dotProgress > (1f-PROGRESS_FADE_OUT))
            {
                angle = PROGRESS_ANGLE_END;
                final float p = ValueTools.progressInRange(dotProgress, 1f-PROGRESS_FADE_OUT, 1f);
                alpha = 1f-interpolatorFadeOut.getInterpolation(p);
            }
            else
            {
                final float p = ValueTools.progressInRange(dotProgress, 0f, 1f-PROGRESS_FADE_OUT);
                angle = interpolatorMove.getInterpolation(p) * PROGRESS_ANGLE_END;
                if (dotProgress < PROGRESS_FADE_IN)
                {
                    alpha = interpolatorFadeIn.getInterpolation(dotProgress / PROGRESS_FADE_IN);
                }
                else
                {
                    alpha = 1f;
                }
            }


            CoordTools.getVelocityFromAngleAndSpeed(angle, 230f, position);

            touchGlow.setAlpha(Math.round(255 * alpha));
            final int left = ((getWidth() - touchGlowWidth) / 2) + Math.round(position.x);
            final int top = (getHeight() / 2) - (touchGlowHeight / 2) - Math.round(position.y);
            touchGlow.setBounds(left, top, left + touchGlowWidth, top + touchGlowHeight);
            
            touchGlow.draw(canvas);
        }
    }

    private void initialiseOnFirstDraw()
    {
        boolean setColour = false;

        if (innerRing == null)
        {
            innerRing = getResources().getDrawable(R.drawable.dial_inner_ring);

            final int left = (getWidth() - innerRing.getIntrinsicWidth()) / 2;
            final int top = (getHeight() - innerRing.getIntrinsicHeight()) / 2;
            innerRing.setBounds(left, top, left + innerRing.getIntrinsicWidth(), top + innerRing.getIntrinsicHeight());

            if (cachedHue != -1f) setColour = true;
        }

        if (textPaintCountdown == null)
        {
            createPaints();

            if (cachedHue != 0f) setColour = true;
        }

        if (setColour)
        {
            onColourSet(cachedHue);
        }

        countdownTimePosX = getWidth() / 2;
        countdownTimePosY = getHeight() / 2;
        endtimePosX = countdownTimePosX;
        endtimePosY = countdownTimePosY + Math.round(textPaintCountdown.getTextSize());

        updateVisibleStates();
    }

    /**
     * Property to show touch glow.
     * 
     * @param progress
     */
    public void setDotAnimate(final float progress)
    {
        this.dotProgress = progress;
        invalidate();
    }

    private void updateVisibleStates()
    {
        if (innerRing != null)
        {
            getBackground().setAlpha(isEnabled() ? 255 : 100);
        }
    }

    private void createPaints()
    {
        textPaintCountdown = TimerTextView.stylePaint(TextType.COUNTDOWN, getWidth());
        textPaintTarget = TimerTextView.stylePaint(TextType.TARGET, getWidth());
    }

    private void setSecsRemaining(int secs)
    {
        if (secs == 0)
        {
            mAngle = 0;
            totalAngle = 0;
            prevAngle = 0;
        }
        else
        {
            totalAngle = convertSecsToAngle(secs);
            prevAngle = totalAngle;
            mAngle = totalAngle % 360;
        }

        this.setDigitalTimeTo(secs);
    }

    private void setDigitalTimeTo(final int seconds)
    {
        setDigitalCountdown(Globals.getFormattedTimeRemaining(seconds));

        if (!countdownAction)
        {
            setEndClockTo(seconds);
        }
    }

    /**
     * @param b
     */
    public void setAlarmIsRinging(final boolean b)
    {
        alarmRinging = b;
    }

    private void setEndClockTo(final int seconds)
    {
        setDigitalEndTime(Globals.getFormattedTimeEnd(seconds));
    }

    private void setDigitalCountdown(final String format)
    {
        this.msCountdownTime = format;
    }

    private void setDigitalEndTime(final String format)
    {
        this.msEndTime = format;
    }

    @Override
    public void onColourSet(final float colour)
    {
        // not initialised? save value for when it is.
        if (innerRing == null)
        {
            cachedHue = colour;
        }
        else
        {
            final ColorMatrix hueMatrix = new ColorMatrix();
            ColorFilterTools.adjustHue(hueMatrix, Math.round(colour * 360) - 180);
            final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(hueMatrix);
            innerRing.setColorFilter(filter);
            textPaintTarget.setColorFilter(filter);
            touchGlow.setColorFilter(filter);
            invalidate();
        }
    }

    /**
     * The animation to show to touch glow.
     * 
     * @param glowAnimation
     */
    public void setGlowAnimation(final ObjectAnimator glowAnimation)
    {
        this.glowAnimation = glowAnimation;
    }
}
