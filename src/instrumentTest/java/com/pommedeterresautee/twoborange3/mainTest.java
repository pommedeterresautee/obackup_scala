package com.pommedeterresautee.twoborange3;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.pommedeterresautee.twoborange3.mainTest \
 * com.pommedeterresautee.twoborange3.tests/android.test.InstrumentationTestRunner
 */
public class mainTest extends ActivityInstrumentationTestCase2<main> {

    public mainTest() {
        super(main.class);
    }

}
