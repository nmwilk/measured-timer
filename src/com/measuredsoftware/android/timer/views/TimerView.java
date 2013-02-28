package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.measuredsoftware.android.library2.utils.MathTools;
import com.measuredsoftware.android.timer.Globals;
import com.measuredsoftware.android.timer.R;
import com.measuredsoftware.android.timer.RotatableImageView;

/**
 * The rotatable view that starts a timer.
 * 
 * @author neil
 * 
 */
public class TimerView extends RotatableImageView
{
    /** the events generated by the timer */
    public static interface OnEventListener
    {
        /**
         * rotation started
         * 
         * @param millisecs
         */
        void started(int millisecs);

        /**
         * more rotation occurred
         * 
         * @param millisecs
         */
        void valueChanged(int millisecs);

        /** called when started called by no value changed. */
        void cancelled();
    }

    protected static final float mMaxTotalAngle = (360 * 72) - 1;

    private static final int TEXT_COLOUR_DEFAULT_COUNTDOWN = 0xFFFFFFFF;
    private static final int TEXT_COLOUR_DEFAULT_ENDTIME = 0xFFFFFFFF;

    protected OnEventListener mListener;
    protected int mCurrentTimeSecs;

    // for formatting
    private Time mTimeLeft;
    private Time mEndTime;

    private boolean mCountdownActive = false;

    private float mPrevAngle;
    private float mTotalAngle; // includes if we've looped around

    private Paint mTextPaintCountdown;
    private Paint mTextPaintTarget;

    private String msCountdownTime;
    private String msEndTime;

    private long mEndTimeMS;
    private int mSecsLeftPrev;

    private boolean mSettingTime;

    private boolean mAlarmRinging;

    private int countdownTimePosX;
    private int countdownTimePosY;
    private int endtimePosX;
    private int endtimePosY;

    private Drawable innerRing;

    /**
     * @param context
     * @param attrs
     */
    public TimerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mListener = null;

        super.setIncrement(1);

        mEndTimeMS = 0;
        mSecsLeftPrev = -1;
        mCurrentTimeSecs = 0;
        mSettingTime = false;
        mAlarmRinging = false;

        final Drawable back = this.getBackground();
        if (back == null) throw new RuntimeException("Must supply a background to TimerView.");

        mTimeLeft = new Time();
        mEndTime = new Time();

        mCountdownActive = false;

        setDigitalTimeTo(0);
    }

    /**
     * @param listener
     *            The listener for the events this generates.
     */
    public void setOnSetValueChangedListener(final OnEventListener listener)
    {
        mListener = listener;
    }

    /**
     * @param colour
     *            ARGB int
     */
    public void setTextColourCountdown(final int colour)
    {
        mTextPaintCountdown.setColor(colour);
    }

    /**
     * @param colour
     *            ARGB int
     */
    public void setTextColourTarget(final int colour)
    {
        mTextPaintTarget.setColor(colour);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // don't allow moving of timer if ringing
        if (mAlarmRinging) return true;

        final boolean b = super.onTouchEvent(event);
        // parent handled it?
        if (b) return true;

        int msgRes = -1;
        int msgValue = -1;

        final int action = event.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                mCurrentTimeSecs = convertAngleToSecs(mTotalAngle);
                msgRes = 2;
                setCountdownActive(false);
                mSettingTime = true;
                mPrevAngle = mAngle;
                break;
            case MotionEvent.ACTION_UP:
                if (!mMultitouchActive)
                {
                    mSettingTime = false;
                    // start the timer
                    msgRes = 0;

                    long endTime = 0;
                    // set the end time
                    if (mTotalAngle > 0)
                    {
                        msgValue = this.mCurrentTimeSecs;
                        endTime = System.currentTimeMillis() + (msgValue * 1000);
                    }
                    setEndTime(endTime);
                    mCurrentTimeSecs = 0;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // no change?
                if (mPrevAngle == mAngle) break;

                // calculate difference since last MOVE (complicated due to
                // passing 359->1)
                final float angleDiff;
                // passed 359->1?
                if (mPrevAngle > 270 && (mAngle > 0 && mAngle < 90))
                {
                    angleDiff = (mAngle + 360) - mPrevAngle;
                }
                // passed 1>359?
                else if ((mPrevAngle >= 0 && mPrevAngle < 90) && mAngle > 270)
                {
                    angleDiff = mAngle - (mPrevAngle + 360);
                }
                else
                {
                    angleDiff = mAngle - mPrevAngle;
                }

                mTotalAngle += angleDiff;
                if (mTotalAngle > mMaxTotalAngle)
                {
                    mTotalAngle = mMaxTotalAngle;
                    mAngle = (mTotalAngle % 360);
                }
                else if (mTotalAngle < 0)
                {
                    mTotalAngle = 0;
                    mAngle = 0;
                }

                mCurrentTimeSecs = convertAngleToSecs(mTotalAngle);
                msgValue = mCurrentTimeSecs;
                msgRes = 1;

                this.setDigitalTimeTo(mCurrentTimeSecs);

                mPrevAngle = mAngle;
                break;
        }

        if (mListener != null && msgRes != -1)
        {
            switch (msgRes)
            {
                case 0:
                    mListener.started(msgValue);
                    break;
                case 1:
                    mListener.valueChanged(msgValue);
                    break;
                default:
                    mListener.cancelled();
                    break;
            }
        }

        return true;
    }

    /**
     * @param endTimeMS
     */
    public void setEndTime(long endTimeMS)
    {
        setCountdownActive(endTimeMS > 0);

        mEndTimeMS = endTimeMS;
        if (endTimeMS == 0)
        {
            mSecsLeftPrev = -1;
            setSecsRemaining(0);
        }
        else
        {
            setSecsRemaining((int) (mEndTimeMS - System.currentTimeMillis()) / 1000);
        }

        if (mCountdownActive)
        {
            this.setEndClockTo((int) ((mEndTimeMS - System.currentTimeMillis()) / 1000));
        }

        updateTime();
    }

    /** recalc time and invalidate if changed. */
    public void updateTime()
    {
        if (!mCountdownActive)
        {
            this.setEndClockTo(mCurrentTimeSecs);
        }

        if (!mSettingTime)
        {
            int secsLeft = 0;
            if (mEndTimeMS > 0) secsLeft = (int) ((mEndTimeMS - System.currentTimeMillis()) / 1000);

            if (secsLeft < 0)
            {
                secsLeft = 0;
                mEndTimeMS = 0;
            }

            if (secsLeft != mSecsLeftPrev)
            {
                mSecsLeftPrev = secsLeft;
                setSecsRemaining(secsLeft);
            }
        }

        this.invalidate();
    }

    private void setCountdownActive(final boolean b)
    {
        mCountdownActive = b;
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
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        initialiseOnFirstDraw();

        innerRing.draw(canvas);

        // draw digital times
        canvas.drawText(msCountdownTime, countdownTimePosX, countdownTimePosY, mTextPaintCountdown);
        canvas.drawText(msEndTime, endtimePosX, endtimePosY, mTextPaintTarget);
    }

    private void initialiseOnFirstDraw()
    {
        if (innerRing == null)
        {
            innerRing = getResources().getDrawable(R.drawable.dial_inner_ring);

            final int left = (getWidth() - innerRing.getIntrinsicWidth()) / 2;
            final int top = (getHeight() - innerRing.getIntrinsicHeight()) / 2;

            innerRing.setBounds(left, top, left + innerRing.getIntrinsicWidth(), top + innerRing.getIntrinsicHeight());
        }

        if (mTextPaintCountdown == null) createPaints();

        countdownTimePosX = getWidth() / 2;
        countdownTimePosY = getHeight() / 2;
        endtimePosX = countdownTimePosX;
        endtimePosY = countdownTimePosY + Math.round(mTextPaintCountdown.getTextSize());
    }

    private void createPaints()
    {
        final float textSizeCountdown = Math.round((float) getWidth() / 9f);
        final float textSizeTarget = Math.round((float) getWidth() / 11f);

        mTextPaintCountdown = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaintCountdown.setTypeface(Globals.getFont());
        mTextPaintCountdown.setTextSize(textSizeCountdown);
        mTextPaintCountdown.setTextAlign(Align.CENTER);
        mTextPaintCountdown.setColor(TEXT_COLOUR_DEFAULT_COUNTDOWN);
        mTextPaintCountdown.setShadowLayer(textSizeCountdown / 18f, 0, textSizeCountdown / 20f, 0x7F000000);

        mTextPaintTarget = new Paint();
        mTextPaintTarget.setTypeface(Globals.getFont());
        mTextPaintTarget.setTextSize(textSizeTarget);
        mTextPaintTarget.setColor(TEXT_COLOUR_DEFAULT_ENDTIME);
        mTextPaintTarget.setAntiAlias(true);
        mTextPaintTarget.setTextAlign(Align.CENTER);
        mTextPaintTarget.setShadowLayer(textSizeTarget / 18f, 0, textSizeTarget / 20f, 0x7F000000);
    }

    private void setSecsRemaining(int secs)
    {
        if (secs == 0)
        {
            mAngle = 0;
            mTotalAngle = 0;
            mPrevAngle = 0;
        }
        else
        {
            mTotalAngle = convertSecsToAngle(secs);
            mPrevAngle = mTotalAngle;
            mAngle = mTotalAngle % 360;
        }

        this.setDigitalTimeTo(secs);
    }

    private void setDigitalTimeTo(int value)
    {
        if (value < 0) value = 0;
        final int secs = (value % 60);
        final int mins = (value / 60) % 60;
        final int hours = (value / 3600);
        mTimeLeft.set(secs, mins, hours, 1, 1, 1999);
        setDigitalCountdown(mTimeLeft.format(Globals.TIME_FORMAT));

        if (!mCountdownActive)
        {
            mEndTime.set(System.currentTimeMillis() + (value * 1000));
            setDigitalEndTime(mEndTime.format(Globals.TIME_FORMAT));
        }
    }

    /**
     * @param b
     */
    public void setAlarmIsRinging(boolean b)
    {
        mAlarmRinging = b;
    }

    private void setEndClockTo(int value)
    {
        mEndTime.set(System.currentTimeMillis() + (value * 1000));
        setDigitalEndTime(mEndTime.format(Globals.TIME_FORMAT));
    }

    private void setDigitalCountdown(String format)
    {
        this.msCountdownTime = format;
    }

    private void setDigitalEndTime(String format)
    {
        this.msEndTime = format;
    }
}
