/*
 * Copyright (c) 2013
 * This file is part of 2BOrange Android application and is the property of its Author, Michaël BENESTY.
 *
 * You may be sued by the Author for any violation of this property.
 *
 * Using this file in any way without an explicit authorization of the Author is a violation of this property.
 */

package com.pommedeterresautee.twoborange3;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

/**
 * Load custom font in a static way to optimize performance
 */
public enum FONT {

    THIN("fonts/roboto_thin.ttf"),
    LIGHT("fonts/roboto_light.ttf"),
    REGULAR("fonts/roboto_regular.ttf"),
    ITALIC("fonts/roboto_italic.ttf"),
    BLACK("fonts/roboto_black.ttf"),
    THIN_ITALIC("fonts/roboto_thin_italic.ttf"),
    LIGHT_ITALIC("fonts/roboto_light_italic.ttf"),
    BLACK_ITALIC("fonts/roboto_black_italic.ttf"),
    LOBSTER("fonts/lobster.ttf");

    private String mPath;
    private Typeface mT;

    private FONT(String path) {
        mPath = path;
    }

    public Typeface getTypeFace(Context context) {
        if (mT == null) {
            mT = Typeface.createFromAsset(context.getApplicationContext().getAssets(), mPath);
        }
        return mT;
    }

    /**
     * Get a TypefaceSpan to use on part of a TextView
     *
     */
    public TypefaceSpan getTypefaceSpan(Context context) {
        return new CustomTypefaceSpan(this.name(), getTypeFace(context));
    }

    /**
     * Use a custom font in a TypeFace
     */
    static private class CustomTypefaceSpan extends TypefaceSpan {
        private final Typeface newType;

        private CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        private static void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(tf);
        }
    }
}