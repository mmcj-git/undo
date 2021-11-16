package cse340.undo.actions;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import cse340.undo.R;
import cse340.undo.app.DrawingView;

/**
 * Reversible action which changes the color of the DrawingView's paint to the background color to erase.
 */
public class EraseAction extends AbstractReversibleAction {

    private final int BACKGROUND_COLOR = 0xFFFFFFFF;

    /** The color that this action changes the current paint from. */
    @ColorInt
    private int mPrev;

    public EraseAction() {    }

    /** @inheritDoc */
    @Override
    public void doAction(DrawingView view) {
        super.doAction(view);
        Paint cur = view.getCurrentPaint();
        mPrev = cur.getColor();
        cur.setColor(BACKGROUND_COLOR);
    }

    /** @inheritDoc */
    @Override
    public void undoAction(DrawingView view) {
        super.undoAction(view);
        view.getCurrentPaint().setColor(mPrev);
    }

    @NonNull
    @Override
    public String toString() {
        return "Change brush to eraser";
    }
}
