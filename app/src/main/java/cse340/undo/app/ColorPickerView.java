package cse340.undo.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


/**
 * This is a subclass of AbstractColorPickerView, that is, this View implements a ColorPicker.
 *
 * There are several class fields, enums, callback classes, and helper functions which have
 * been implemented for you.
 *
 * PLEASE READ AbstractColorPickerView.java to learn about these.
 */
public class ColorPickerView extends AbstractColorPickerView {
    /* ********************************************************************************************** *
     * All of your applications state (the model) and methods that directly manipulate it are here    *
     * This does not include mState which is the literal state of your PPS, which is inherited
     * ********************************************************************************************** */

    /**
     * The current color selected in the ColorPicker. Not necessarily the last
     * color that was sent to the listeners.
     */
    @ColorInt
    protected int mCurrentColor, mPrevColor;

    @Override
    public void setColor(@ColorInt int newColor) {
        mCurrentColor = newColor;
        invalidate();
    }

    private void updateModel(float x, float y) {
        // hint: we give you a very helpful function to call
        if (mState == State.START) {
            mPrevColor = mCurrentColor;
        }
        setColor(getColorFromAngle(getTouchAngle(x, y)));
    }

    private void resetColor() {
        setColor(mPrevColor);
        thumbBrush.setAlpha((int)(1f*255));
    }

    /* ********************************************************************************************** *
     *                               <End of model declarations />
     * ********************************************************************************************** */

    /* ********************************************************************************************** *
     * You may create any constants you wish here.                                                     *
     * You may also create any fields you want, that are not necessary for the state but allow       *
     * for better optimized or cleaner code                                                           *
     * ********************************************************************************************** */

    Paint circleBrush, thumbBrush;
    int thumbRadius, thumbCenterX, thumbCenterY;

    /* ********************************************************************************************** *
     *                               <End of other fields and constants declarations />
     * ********************************************************************************************** */

    /**
     * Constructor of the ColorPicker View
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view. This value may be null.
     */
    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //circleBrush = new Paint();
        mCurrentColor = DEFAULT_COLOR;
        thumbBrush = new Paint();
        thumbBrush.setColor(Color.WHITE);
        thumbRadius = (int)(mRadius*RADIUS_TO_THUMB_RATIO);
        thumbCenterX = 0;
        thumbCenterY = 0;
        mState = State.START;

    }

    /**
     * Draw the ColorPicker on the Canvas
     * @param canvas the canvas that is drawn upon
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //circleBrush.setColor(mCurrentColor);
        //canvas.drawCircle(mCenterX, mCenterY, mRadius, circleBrush);
        super.onDraw(canvas);
        //thumbRadius = (int)(mRadius*RADIUS_TO_THUMB_RATIO);
        thumbCenterX = (int)(mCenterX + (mRadius - thumbRadius)*Math.cos(getAngleFromColor(mCurrentColor)));
        thumbCenterY = (int)(mCenterY + (mRadius - thumbRadius)*Math.sin(getAngleFromColor(mCurrentColor)));
        if (mState == State.INSIDE) {
            thumbBrush.setAlpha((int)(.5f*255));
        } else {
            thumbBrush.setAlpha((int)(1f*255));
        }
        canvas.drawCircle(thumbCenterX, thumbCenterY, thumbRadius, thumbBrush);
    }

    /**
     * Called when this view should assign a size and position to all of its children.
     * @param changed This is a new size or position for this view
     * @param left Left position, relative to parent
     * @param top Top position, relative to parent
     * @param right Right position, relative to parent
     * @param bottom Bottom position, relative to parent
     */
    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mRadius = Math.min(this.getWidth(), this.getHeight()) / (float)2.0;
            mCenterX = this.getWidth() / (float)2.0;
            mCenterY = this.getHeight() / (float)2.0;
            thumbRadius = (int)(mRadius*RADIUS_TO_THUMB_RATIO);
        }
    }

    /**
     * Calculate the essential geometry given an event.
     *
     * @param event Motion event to compute geometry for, most likely a touch.
     * @return EssentialGeometry value.
     */
    @Override
    protected EssentialGeometry essentialGeometry(MotionEvent event) {
        double length = Math.sqrt(Math.pow(event.getX() - mCenterX, 2) + Math.pow(event.getY() - mCenterY, 2));
        if (length <= mRadius && length >= (mRadius - thumbRadius*2)) {
            Log.i(getClass().getSimpleName(), "Essential Geom: wheel");
            return EssentialGeometry.WHEEL;
        }
        Log.i(getClass().getSimpleName(), "Essential Geom: offwheel");
        return EssentialGeometry.OFFWHEEL;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        EssentialGeometry geometry = essentialGeometry(event);

        // make sure to make calls to updateModel, invalidate, and invokeColorChangeListeners

        switch (mState) {
            case START:
                if (event.getAction() == MotionEvent.ACTION_DOWN && geometry == EssentialGeometry.WHEEL) {
                    updateModel(event.getX(), event.getY());
                    mState = State.INSIDE;
                    invalidate();
                    return true;
                }
                break;
            case INSIDE:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mState = State.START;
                    if (geometry == EssentialGeometry.WHEEL) {
                        invokeColorChangeListeners(mCurrentColor);
                        invalidate();
                        return true;
                    } else {
                        resetColor();
                        invalidate();
                        return true;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (geometry == EssentialGeometry.WHEEL) {
                        updateModel(event.getX(), event.getY());
                        invalidate();
                        return true;
                    } else {  //outside the wheel
                        //handle it but don't do anything
                        return true;
                    }
                }
                break;
            default:
                break;
        }

        return false;
    }


    /**
     * Converts from a color to angle on the wheel.
     *
     * @param color RGB color as integer.
     * @return Position of this color on the wheel in radians.
     */
    public static float getAngleFromColor(int color) {
        float[] components = new float[3];
        Color.colorToHSV(color, components);
        return (float)(Math.toRadians((components[0] - 90 - 360) % 360));
    }

}
