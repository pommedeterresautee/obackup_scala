/*
 * Copyright (c) 2013
 * This file is part of 2BOrange Android application and is the property of its Author, Michaël BENESTY.
 *
 * You may be sued by the Author for any violation of this property.
 *
 * Using this file in any way without an explicit authorization of the Author is a violation of this property.
 */

package com.pommedeterresautee.twoborange3;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Method;


/**
 * Different function useful to call in the application.
 *
 * @author pommedeterresautee
 */
public class InterfaceFunctions {

    /**
     * Get the view with a type.
     *
     * @param root
     * @param id
     * @param <T>
     * @return
     */
    public static <T extends View> T findView(ViewGroup root, int id) {
        View view = root.findViewById(id);
        return (T) view;
    }

    /**
     * @param v the layout to make visible
     */
    public static void makeVisible(View v) {
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        param.bottomMargin = 0;
        v.setLayoutParams(param);
        v.setVisibility(View.VISIBLE);
    }

    /**
     * @param v the layout to make invisible
     */
    public static void makeInvisible(View v) {
        v.measure(5000, 5000);
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        param.bottomMargin = -v.getMeasuredHeight();
        v.setLayoutParams(param);
        v.setVisibility(View.GONE);
    }

    /**
     * Change the text which appears if the listview is empty.
     *
     * @param add       true if is loading, false otherwise
     * @param layout    contains the listview
     * @param finalText text to display if view is empty after loading
     */
    public static void emptyTextListView(View layout, boolean add, String finalText) {
        if (layout != null) {
            ListView lv = (ListView) layout.findViewById(android.R.id.list);
            if (add) {
                ProgressBar pb = new ProgressBar(layout.getContext());
                pb.setIndeterminate(true);
                lv.setEmptyView(lv);
            } else {
                TextView empty = ((TextView) layout.findViewById(android.R.id.empty));
                if (empty != null) {
                    empty.setText(finalText);
                    empty.setTypeface(FONT.THIN.getTypeFace(layout.getContext()));
                }
            }
        }
    }

    /**
     * Change the text which appears if the listview is empty.
     *
     * @param add    true if is loading, false otherwise
     * @param layout contains the listview
     */
    public static void emptyTextListView(View layout, boolean add) {
        emptyTextListView(layout, add, "No data to display.");
    }

    /**
     * This method convets dp unit to equivalent device specific value in pixels.
     *
     * @param dp      A value in dp(Device independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent Pixels equivalent to dp according to device
     */
    public static int convertDpToPixel(int dp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return px;
    }

    /**
     * Resize drawable in a proportional way.
     *
     * @param context
     * @param drawing   Image to resize.
     * @param finalSize Limit size after transformation.
     * @return the Drawable after transformation.
     */
    public static Drawable scaleImage(Context context, Drawable drawing, int finalSize) {
        // Get the ImageView and its bitmap
        Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();
        // Get current dimensions AND the desired bounding box
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) finalSize) / width;
        float yScale = ((float) finalSize) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(context.getResources(), scaledBitmap);
    }

    /**
     * Force screen to stay in a rotation state
     *
     * @param context
     * @param activate true to activate the function, false to disable.
     */
    public static void LockRotation(Activity context, boolean activate) {
        int lockedRotation;
        if (isLandscape(context)) {
            lockedRotation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        } else {
            lockedRotation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        }
        int rotation = activate ? lockedRotation : ActivityInfo.SCREEN_ORIENTATION_SENSOR;
        context.setRequestedOrientation(rotation);
    }

    public static boolean isLandscape(Activity context) {
        return Configuration.ORIENTATION_LANDSCAPE == context.getResources().getConfiguration().orientation;
    }

    /**
     * Hode the keyboard because a text field is highlighted
     *
     * @param context
     * @param theView
     */
    public static void hideKeyboard(Activity context, View theView) {
        if (context == null) return;
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(theView.getWindowToken(), 0);
    }

    /**
     * Make the text grey with central word Orange.
     *
     * @param prefix
     * @param middle
     * @param suffix
     * @return
     */
    public static SpannableStringBuilder generateBoldColoredText(Context context, String prefix, String middle, String suffix) {
        String textToDisplay = prefix + middle + suffix;
        SpannableStringBuilder sb = new SpannableStringBuilder(textToDisplay);
        ForegroundColorSpan fcs = new ForegroundColorSpan(context.getResources().getColor(R.color.orange));

        int start = prefix.length();
        int end = start + middle.length();

        TypefaceSpan black = FONT.BLACK.getTypefaceSpan(context);

        TypefaceSpan light = FONT.LIGHT.getTypefaceSpan(context);

        TypefaceSpan lightEnd = FONT.LIGHT.getTypefaceSpan(context);

        sb.setSpan(light, 0, start, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        sb.setSpan(fcs, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(black, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        sb.setSpan(lightEnd, end, sb.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);


        return sb;
    }

    private static Method overridePendingTransition;

    static {
        try {
            overridePendingTransition = Activity.class
                    .getMethod(
                            "overridePendingTransition", new Class[]{Integer.TYPE, Integer.TYPE}); //$NON-NLS-1$
        } catch (NoSuchMethodException e) {
            overridePendingTransition = null;
        }
    }


    /**
     * Calls Activity.overridePendingTransition if the method is available
     * (>=Android 2.0)
     *
     * @param activity  the activity that launches another activity
     * @param animEnter the entering animation
     * @param animExit  the exiting animation
     */
    protected static void overridePendingTransition(Activity activity,
                                                    int animEnter, int animExit) {
        if (overridePendingTransition != null) {
            try {
                overridePendingTransition.invoke(activity, animEnter, animExit);
            } catch (Exception e) {
                // do nothing
            }
        }
    }


    public static Drawable flip(boolean flip, Context context, int resource) {
        int rotate = flip ? 180 : 0;
        return rotateDrawable(rotate, context, resource);
    }

    public static Drawable rotateDrawable(float angle, Context context, int resource) {
        Bitmap arrowBitmap = BitmapFactory.decodeResource(context.getResources(), resource);

        // Create blank bitmap of equal size
        Bitmap canvasBitmap = arrowBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvasBitmap.eraseColor(0x00000000);

        // Create canvas
        Canvas canvas = new Canvas(canvasBitmap);

        // Create rotation matrix
        Matrix rotateMatrix = new Matrix();
        rotateMatrix.setRotate(angle, canvas.getWidth() / 2, canvas.getHeight() / 2);

        //Draw bitmap onto canvas using matrix
        canvas.drawBitmap(arrowBitmap, rotateMatrix, null);

        return new BitmapDrawable(context.getResources(), canvasBitmap);
    }

    /**
     * Get title textview.
     *
     * @param context
     * @return null on Gingerbread devices
     */
    public static TextView getTitleTV(Activity context) {
        try {
            Integer titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
            return (TextView) context.getWindow().findViewById(titleId);
        } catch (Exception e) {
        }
        return null;
    }
}