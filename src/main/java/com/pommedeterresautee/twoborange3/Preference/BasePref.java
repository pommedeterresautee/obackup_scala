/*
 * Copyright (c) 2013
 * This file is part of 2BOrange Android application and is the property of its Author, Michaël BENESTY.
 *
 * You may be sued by the Author for any violation of this property.
 *
 * Using this file in any way without an explicit authorization of the Author is a violation of this property.
 */

package com.pommedeterresautee.twoborange3.Preference;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * @author minigeantvert
 */
abstract public class BasePref {
    private final SharedPreferences pref;
    private final Context mContext;

    public BasePref(Context context) {
        mContext = context.getApplicationContext();
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    protected <T> T readInNewThread(String key, Class<T> returnType) {
        return readThat(key, returnType);
    }

    protected <T> T readInCurrentThread(String key, Class<T> returnType) {
        return readThat(key, returnType);
    }

    /**
     * Erase all preferences.
     * Use commit instead of apply to be sure it has been applied before next operation.
     */
    protected void resetPreference() {
        pref.edit().clear().commit();
    }

    protected void saveIt(final String key, final Object value) {
        SharedPreferences.Editor editor = pref.edit();
        if (value == null)
            editor.remove(key);
        else {
            if (String.class.isInstance(value)) editor.putString(key, String.class.cast(value));
            else if (Long.class.isInstance(value)) editor.putLong(key, Long.class.cast(value));
            else if (Integer.class.isInstance(value)) editor.putInt(key, Integer.class.cast(value));
            else if (Boolean.class.isInstance(value))
                editor.putBoolean(key, Boolean.class.cast(value));
            editor.commit(); // doesn't use apply() method to not have issue with set and read just after.
        }
    }

    protected <T> T readThat(String key, Class<T> returnType) {
        if (returnType.isAssignableFrom(String.class)) {
            return returnType.cast(pref.getString(key, ""));
        } else if (returnType.isAssignableFrom(Integer.class)) {
            return returnType.cast(pref.getInt(key, -1));
        } else if (returnType.isAssignableFrom(Boolean.class)) {
            return returnType.cast(pref.getBoolean(key, false));
        } else if (returnType.isAssignableFrom(Long.class)) {
            return returnType.cast(pref.getLong(key, -1l));
        }
        return null;
    }

    protected Context getContext() {
        return mContext;
    }
}
