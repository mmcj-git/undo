package cse340.undo.app;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.constraint.ConstraintSet;
import android.view.View;
import android.view.ViewGroup;

import cse340.undo.R;
import cse340.undo.actions.ChangeColorAction;
import cse340.undo.actions.ChangeThicknessAction;
import cse340.undo.actions.AbstractReversibleAction;
import cse340.undo.actions.EraseAction;

public class ReversibleDrawingActivity extends AbstractReversibleDrawingActivity implements AbstractColorPickerView.ColorChangeListener {
    private static final int DEFAULT_COLOR = Color.RED;
    private static final int DEFAULT_THICKNESS = 10;

    /** List of menu item FABs for thickness menu. */
    @IdRes
    private static final int[] THICKNESS_MENU_ITEMS = {
        R.id.fab_thickness_0, R.id.fab_thickness_10, R.id.fab_thickness_20, R.id.fab_thickness_30
    };

    /** List of menu item FABs for color menu. */
    @IdRes
    private static final int[] COLOR_MENU_ITEMS = {
        R.id.color_wheel
    };

    /** State variables used to track whether menus are open. */
    private boolean isThicknessMenuOpen;
    private boolean isColorMenuOpen;
    private boolean isEraseOn = false;

    @SuppressLint("PrivateResource")
    private int mMiniFabSize;

    /** Place to stort ColorPickerView */
    protected AbstractColorPickerView mColorPickerView;

    protected ViewGroup mErase;

    /**
     * Creates a new AbstractReversibleDrawingActivity with the default history limit.
     */
    public ReversibleDrawingActivity() {
        super();
    }

    /**
     * Creates a new AbstractReversibleDrawingActivity with the given history limit.
     *
     * @param history Maximum number of history items to maintain.
     */
    public ReversibleDrawingActivity(int history) {
        super(history);
    }

    @Override
    @SuppressLint("PrivateResource")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We are providing draw with a default thickness and color for the first line
        Paint p = mDrawingView.getCurrentPaint();
        p.setColor(DEFAULT_COLOR);
        p.setStrokeWidth(DEFAULT_THICKNESS);
        mDrawingView.setCurrentPaint(p);
        mMiniFabSize = getResources().getDimensionPixelSize(R.dimen.design_fab_size_mini);

        // TODO: initialize color picker and register color change listener

        // Add thickness and color menus to the ConstraintLayout. Pass in onColorMenuSelected
        // and onThicknessMenuSelected as the listeners for these menus
        addCollapsableMenu(R.layout.color_menu, ConstraintSet.BOTTOM, ConstraintSet.END, COLOR_MENU_ITEMS, this::onColorMenuSelected);
        mColorPickerView = findViewById(R.id.color_wheel);
        mColorPickerView.addColorChangeListener(this::onColorSelected);
        // TODO: you may have to edit this after integrating the color picker
        findViewById(R.id.fab_color).setOnClickListener((v) -> {
            enableCollapsibleMenu(R.id.fab_thickness, THICKNESS_MENU_ITEMS, isColorMenuOpen);
            enableFAB(R.id.fab_erase, isColorMenuOpen);
            isColorMenuOpen = toggleMenu(COLOR_MENU_ITEMS, isColorMenuOpen);
        });

        // Only draw a stroke when none of the collapsible menus are open
        mDrawingView.setOnTouchListener((view, event) -> {
            if (isThicknessMenuOpen) {
                isThicknessMenuOpen = toggleMenu(THICKNESS_MENU_ITEMS, isThicknessMenuOpen);
                enableCollapsibleMenu(R.id.fab_color, COLOR_MENU_ITEMS, !isThicknessMenuOpen);
                return true;
            } else if (isColorMenuOpen) {
                isColorMenuOpen = toggleMenu(COLOR_MENU_ITEMS, isColorMenuOpen);
                enableCollapsibleMenu(R.id.fab_thickness, THICKNESS_MENU_ITEMS, !isColorMenuOpen);
                return true;
            } else {
                return mDrawingView.onTouchEvent(event);
            }
        });

        registerActionListener(this::onAction);
        registerActionUndoListener(this::onActionUndo);

        addCollapsableMenu(R.layout.thickness_menu, ConstraintSet.BOTTOM, ConstraintSet.END, THICKNESS_MENU_ITEMS, this::onThicknessMenuSelected);
        findViewById(R.id.fab_thickness).setOnClickListener((v) ->{
            enableCollapsibleMenu(R.id.fab_color, COLOR_MENU_ITEMS, isThicknessMenuOpen);
            enableFAB(R.id.fab_erase, isThicknessMenuOpen);
            isThicknessMenuOpen = toggleMenu(THICKNESS_MENU_ITEMS, isThicknessMenuOpen);
        });

        mErase = (ViewGroup) getLayoutInflater().inflate(R.layout.erase_menu, mLayout, false);
        addMenu(mErase, ConstraintSet.BOTTOM, ConstraintSet.END);
        findViewById(R.id.fab_erase).setOnClickListener((v) -> {
            isEraseOn = !isEraseOn;
            enableFAB(R.id.fab_erase, !isEraseOn);
            onEraseSelected();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deregisterActionListener(this::onAction);
        deregisterActionUndoListener(this::onActionUndo);
        // TODO: deregister the color change listener
        ((ColorPickerView)findViewById(R.id.color_wheel)).removeColorChangeListener(this::onColorSelected);
    }

    private void onAction(AbstractReversibleAction action) {
        if (action instanceof ChangeColorAction) {
            @ColorInt int currColor = mDrawingView.getCurrentPaint().getColor();
            // TODO: update the color of the color picker if needed
            ((ColorPickerView)findViewById(R.id.color_wheel)).setColor(currColor);
        } else if (action instanceof EraseAction) {
            isEraseOn = !isEraseOn;
            enableFAB(R.id.fab_erase, isEraseOn);
        }
    }

    private void onActionUndo(AbstractReversibleAction action) {
        if (action instanceof ChangeColorAction) {
            @ColorInt int currColor = mDrawingView.getCurrentPaint().getColor();
            // TODO: update the color of the color picker if needed
            ((ColorPickerView)findViewById(R.id.color_wheel)).setColor(currColor);
        } else if (action instanceof EraseAction) {
            isEraseOn = !isEraseOn;
            enableFAB(R.id.fab_erase, isEraseOn);
        }
    }

    /**
     * Callback for creating an AbstractAction when the user changes the color.
     *
     * @param view The FAB the user clicked on
     */
    private void onColorMenuSelected(View view) {
        switch (view.getId()) {
            case R.id.fab_red:
                doAction(new ChangeColorAction(Color.RED));
                break;
            case R.id.fab_blue:
                doAction(new ChangeColorAction(Color.BLUE));
                break;
            case R.id.fab_green:
                doAction(new ChangeColorAction(Color.GREEN));
                break;
        }

        // Close the menu.
        isColorMenuOpen = toggleMenu(COLOR_MENU_ITEMS, isColorMenuOpen);
        enableCollapsibleMenu(R.id.fab_thickness, THICKNESS_MENU_ITEMS, !isColorMenuOpen);
        enableFAB(R.id.fab_erase, !isColorMenuOpen);
    }

    /**
     * Callback for creating an action when the user changes the thickness.
     *
     * TODO: You will need to modify this to add a new thickness FAB
     * @param view The FAB the user clicked on.
     */
    private void onThicknessMenuSelected(View view) {
        switch (view.getId()) {
            case R.id.fab_thickness_0:
                doAction(new ChangeThicknessAction(0));
                break;
            case R.id.fab_thickness_10:
                doAction(new ChangeThicknessAction(10));
                break;
            case R.id.fab_thickness_20:
                doAction(new ChangeThicknessAction(20));
                break;
            case R.id.fab_thickness_30:
                doAction(new ChangeThicknessAction(30));
                break;
        }

        // Close the menu.
        isThicknessMenuOpen = toggleMenu(THICKNESS_MENU_ITEMS, isThicknessMenuOpen);
        enableCollapsibleMenu(R.id.fab_color, COLOR_MENU_ITEMS, !isThicknessMenuOpen);
        enableFAB(R.id.fab_erase, !isThicknessMenuOpen);
    }

    private void onEraseSelected() {
        doAction(new EraseAction());
    }

    /**
     * Toggles a collapsible menu. That is, if it's open, it closes it. If it's closed, it opens it.
     *
     * @param items List of IDs of items in the menu, all FABs.
     * @param open  Current state of the menu.
     * @return True if the menu is now open, false otherwise.
     */
    private boolean toggleMenu(@IdRes int[] items, boolean open) {
        enableFAB(R.id.fab_undo, open);
        enableFAB(R.id.fab_redo, open);
        if (!open) {
            for (int i = 0; i < items.length; i++) {
                View view = findViewById(items[i]);
                view.animate()
                    .translationY(-3 * mMiniFabSize * (i + 1.5f) / 2.5f)
                    .alpha(1)
                    .withEndAction(() -> view.setClickable(true));
            }
            return true;
        } else {
            for (int item : items) {
                View view = findViewById(item);
                view.setClickable(false);
                view.animate().translationY(0).alpha(0);
            }
            return false;
        }
    }

    /**
     * Disables and enables collapsible menu FABs
     *
     * @param menuId The resID of the menu activation FAB
     * @param menuItems An array of resIDs for the menu item FABs
     * @param enabled true if the menu should be enabled, false if the menu should be disabled
     */
    private void enableCollapsibleMenu(@IdRes int menuId, @IdRes int[] menuItems, boolean enabled) {
        enableFAB(menuId, enabled);
        for (@IdRes int item : menuItems) {
            findViewById(item).setEnabled(enabled);
        }
    }

    /**
     * Disables and enables FABs
     *
     * @param buttonId the resID of the FAB
     * @param enabled true if the button should be enabled, false if the button should be disabled
     */
    private void enableFAB(@IdRes int buttonId, boolean enabled) {
        findViewById(buttonId).setEnabled(enabled);
        findViewById(buttonId).setBackgroundTintList(ColorStateList.valueOf(enabled ?
                getResources().getColor(R.color.colorAccent) : Color.LTGRAY));
    }

    /**
     * Called when the color has been selected by the ColorPicker
     * @param color The updated color that was selected by the interactor
     */
    @Override
    public void onColorSelected(int color) {
        doAction(new ChangeColorAction(color));

        // Close the menu.
        isColorMenuOpen = toggleMenu(COLOR_MENU_ITEMS, isColorMenuOpen);
        enableCollapsibleMenu(R.id.fab_thickness, THICKNESS_MENU_ITEMS, !isColorMenuOpen);
        enableFAB(R.id.fab_erase, !isColorMenuOpen);
    }
}
