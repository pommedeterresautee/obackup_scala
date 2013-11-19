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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;


/**
 * @author pommedeterresautee
 */
public class TextManipulation {

    private static final String DATE_FORMAT_BACKUP = "yyyy-MM-dd.HH.mm.ss";
    private static final String DATE_FORMAT_PACKAGE = "yyyy-MM-dd";
    private static final SimpleDateFormat dateParser = new SimpleDateFormat(DATE_FORMAT_PACKAGE);

    /**
     * @param size
     * @return string more easy to read
     */
    public static String convertSizeInString(double size) {
        String[] result = convertSizeInArray(size);
        return result[0] + " " + result[1];
    }

    public static String[] convertSizeInArray(double size) {
        String unit;
        String mask;
        double sizeInUnit;
        if (size > 1024 * 1024 * 1024) { // Gigabyte
            sizeInUnit = size / (1024 * 1024 * 1024);
            unit = "GB";
            mask = "###.##";
        } else if (size > 1024 * 1024) { // Megabyte
            sizeInUnit = size / (1024 * 1024);
            unit = "MB";
            mask = "###.#";
        } else if (size > 1024) { // Kilobyte
            sizeInUnit = size / 1024;
            unit = "KB";
            mask = "###";
        } else { // Byte
            sizeInUnit = size;
            unit = "Bytes";
            mask = "###";
        }
        // only show two digits after the comma
        return new String[]{new DecimalFormat(mask).format(sizeInUnit), unit};
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for invalid
     */
    public static boolean isNotValiEmail(String email) {
        Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );
        return !EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    public static String getDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_PACKAGE);
        return sdf.format(cal.getTime());
    }

    public static String encrypt(String seed, String cleartext) {
        try {
            byte[] rawKey = getRawKey(seed.getBytes());
            byte[] result = encrypt(rawKey, cleartext.getBytes());
            return Base64.encodeToString(result, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    public static String decrypt(String seed, String encrypted) {
        try {
            byte[] rawKey = getRawKey(seed.getBytes());
            byte[] enc = Base64.decode(encrypted, Base64.DEFAULT);
            byte[] result = decrypt(rawKey, enc);
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static final String md5(final String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    /**
     * Just the date, no hour.
     *
     * @param context
     * @param dateOriginal
     * @return
     */
    static public String getBeautifulDate(Context context, String dateOriginal) {
        String finalDate;
        try {
            finalDate = DateUtils.formatDateTime(context,
                    dateParser.parse(dateOriginal).getTime(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_ALL);
        } catch (ParseException e) {
            e.printStackTrace();
            finalDate = dateOriginal;
        }

        return finalDate;
    }

    /**
     * Just the date, no hour.
     *
     * @param context
     * @param dateOriginal
     * @return
     */
    static public String getBeautifulTextDate(Context context, String dateOriginal) {
        String finalDate;
        try {
            finalDate = DateUtils.formatDateTime(context,
                    dateParser.parse(dateOriginal).getTime(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_ABBREV_ALL);
        } catch (ParseException e) {
            e.printStackTrace();
            finalDate = dateOriginal;
        }

        return finalDate;
    }

    public static String getApplicationVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

    public static String getApplicationBuild(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

    public static void setTV(View v, int id, int base, Object... arg) {
        ((TextView) v.findViewById(id)).setText(String.format(v.getContext().getString(base), arg));
    }

    public static void setTV(Activity c, int id, int base, Object... arg) {
        ((TextView) c.findViewById(id)).setText(String.format(c.getString(base), arg));
    }

    public static String clean(String name) {
//        name = CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('A', 'Z')).retainFrom(name);
//        if (TextUtils.isEmpty(name)) {
//            name = "temp";
//        }
        return name;
    }

    public static String getPlural(Context context, int id, int q) {
        return MessageFormat.format(context.getResources().getString(id), q);
    }
}
