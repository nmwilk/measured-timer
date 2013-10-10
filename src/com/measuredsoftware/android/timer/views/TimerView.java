package com.measuredsoftware.android.timer.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import com.measuredsoftware.android.library2.utils.CoordTools;
import com.measuredsoftware.android.library2.utils.MathTools;
import com.measuredsoftware.android.library2.utils.ValueTools;
import com.measuredsoftware.android.timer.*;
import com.measuredsoftware.android.timer.views.TimerTextView.TextType;

/**
 * The rotatable view that starts a timer.
 *
 * @author neil
 */
public class TimerView extends TouchRotatableView implements Colourable
{
    /**
     * the events generated by the timer
     */
    public static interface OnEventListener
    {

        /**
         * rotation started
         */
        void started(int seconds);
        /**
         * more rotation occurred
         */
        void valueChanged(int seconds);

        /**
         * called when started called by no value changed.
         */
        void cancelled();

    }

    protected static final float MAX_TOTAL_ANGLE = (360 * 72) - 1;

    protected OnEventListener eventListener;

    protected int currentTimeSecs;
    private boolean countdownAction = false;

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
    private float cachedHue = -1f;

    private float touchGlowHyp;

    private final int size;

    private final Drawable innerRing;
    private final Drawable bezel;
    private final Drawable spinner;

    private ObjectAnimator glowAnimation;

    public TimerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        setBackgroundColor(getResources().getColor(android.R.color.transparent));

        bezel = getResources().getDrawable(R.drawable.dial_main);
        innerRing = getResources().getDrawable(R.drawable.dial_inner_ring);
        spinner = getResources().getDrawable(R.drawable.dial_spinner);
        touchGlow = getResources().getDrawable(R.drawable.touch_glow);

        touchGlowHyp = (bezel.getIntrinsicWidth() - touchGlow.getIntrinsicWidth()) / 2;
        size = Math.round(bezel.getIntrinsicWidth() + touchGlow.getIntrinsicWidth() / 2);

        eventListener = null;

        setClickable(true);

        setIncrement(1);
        setMinimumTotalAngle(0);
        setMaximumTotalAngle(MAX_TOTAL_ANGLE);

        endTimeMS = 0;
        currentTimeSecs = 0;
        settingTime = false;
        alarmRinging = false;

        final Drawable back = this.getBackground();
        if (back == null)
        {
            throw new RuntimeException("Must supply a background to TimerView.");
        }

        countdownAction = false;

        setDigitalTimeTo(0);

        touchGlowWidth = touchGlow.getIntrinsicWidth();
        touchGlowHeight = touchGlow.getIntrinsicHeight();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
    {
        setMeasuredDimension(size, size - touchGlow.getIntrinsicHeight() / 2);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        boolean setColour = false;

        setDrawableBoundsCentred(innerRing);
        setDrawableBoundsCentred(bezel);
        setDrawableBoundsCentred(spinner);

        if (cachedHue != -1f)
        {
            setColour = true;
        }

        createPaints();

        if (cachedHue != 0f)
        {
            setColour = true;
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

    private void setDrawableBoundsCentred(final Drawable drawable)
    {
        final int left = (getWidth() - drawable.getIntrinsicWidth()) / 2;
        final int top = (getHeight() - drawable.getIntrinsicHeight()) / 2;
        drawable.setBounds(left, top, left + drawable.getIntrinsicWidth(), top + drawable.getIntrinsicHeight());
    }

    /**
     * @param listener The listener for the events this generates.
     */
    public void setOnSetValueChangedListener(final OnEventListener listener)
    {
        eventListener = listener;
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
        if (!isEnabled())
        {
            return true;
        }

        if (glowAnimation != null)
        {
            glowAnimation.end();
            glowAnimation = null;
        }

        final boolean superHandled = super.onTouchEvent(event);

        if (!alarmRinging && !superHandled)
        {
            int msgRes = -1;
            int msgValue = -1;

            final int action = event.getAction();
            switch (action)
            {
                case MotionEvent.ACTION_DOWN:
                    currentTimeSecs = convertAngleToSecs(getTotalAngle());
                    msgRes = 2;
                    setCountdownActive(false);
                    settingTime = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (!multitouchActive)
                    {
                        settingTime = false;
                        // start the timer
                        msgRes = 0;

                        // set the end time
                        if (getTotalAngle() > 0)
                        {
                            msgValue = this.currentTimeSecs;
                        }
                        setEndTime(0);
                        currentTimeSecs = 0;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:

                    currentTimeSecs = convertAngleToSecs(getTotalAngle());
                    msgValue = currentTimeSecs;
                    msgRes = 1;

                    this.setDigitalTimeTo(currentTimeSecs);
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
        }

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

    /**
     * update the clock
     */
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
        return MathTools.roundToNearest((int) (angle * 10), 60);
    }

    protected static float convertSecsToAngle(final int time)
    {
        return (time / 10);
    }

    @Override
    protected void onDraw(final Canvas canvas)
    {
        super.onDraw(canvas);

        bezel.draw(canvas);
        innerRing.draw(canvas);

        canvas.save();

        canvas.rotate(getDisplayAngle(), getTouchPivotX(), getTouchPivotY());

        spinner.draw(canvas);

        canvas.restore();

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

            if (dotProgress > (1f - PROGRESS_FADE_OUT))
            {
                angle = PROGRESS_ANGLE_END;
                final float p = ValueTools.progressInRange(dotProgress, 1f - PROGRESS_FADE_OUT, 1f);
                alpha = 1f - interpolatorFadeOut.getInterpolation(p);
            }
            else
            {
                final float p = ValueTools.progressInRange(dotProgress, 0f, 1f - PROGRESS_FADE_OUT);
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


            CoordTools.getVelocityFromAngleAndSpeed(angle, touchGlowHyp, position);

            touchGlow.setAlpha(Math.round(255 * alpha));
            final int left = ((getWidth() - touchGlowWidth) / 2) + Math.round(position.x);
            final int top = (getHeight() / 2) - (touchGlowHeight / 2) - Math.round(position.y);
            touchGlow.setBounds(left, top, left + touchGlowWidth, top + touchGlowHeight);

            touchGlow.draw(canvas);
        }
    }

    /**
     * Property to show touch glow.
     */
    @SuppressWarnings("UnusedDeclaration") // used via property animator.
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
        setAngle(secs == 0 ? 0 : convertSecsToAngle(secs));

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
        if (getMeasuredWidth() == 0)
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
     */
    public void setGlowAnimation(final ObjectAnimator glowAnimation)
    {
        this.glowAnimation = glowAnimation;
    }
}
