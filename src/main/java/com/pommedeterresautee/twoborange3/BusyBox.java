package com.pommedeterresautee.twoborange3;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class BusyBox {

    private static final String BUSYBOX_FILENAME = "busybox";
    private static final String BUSYBOX_FILENAME_INTERN = "busybox-linus";
    private static final long BUSYBOX_SIZE = 1165484l;

    public static void startCopyAsyncBusyboxIfNeeded(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                copyBusyBoxIfNotYetDone(context.getApplicationContext());
            }
        }).run();
    }

    /**
     * Get the path to the BusyBox file.
     *
     * @param context
     * @return path
     */
    public static String getBusyBoxPath(Context context) {
//        if (RootPref.isBusyboxInternal()) {
//            return getInternalBusybox(context).getAbsolutePath();
//        } else {
//            return BUSYBOX_FILENAME;
//        }
        return "";
    }

    private static File getInternalBusybox(Context context) {
        return new File(context.getFilesDir(), BUSYBOX_FILENAME);
    }

    private static boolean copyBusyBoxIfNotYetDone(Context context) {
        Context appContext = context.getApplicationContext();
        if (!checkBusyBox(appContext)) {
            return copyBusyBox(appContext) > 0l;
        } else {
            return false;
        }
    }

    /**
     * Copy BusyBox executable to the private data folder.
     *
     * @param context
     */
    private static long copyBusyBox(Context context) {

        long sizeCopied = 0l;
        try {
            File busybox = getInternalBusybox(context);
            if (busybox.exists()) {
//                Preconditions.checkState(busybox.delete(), "Impossible to delete existing busybox");
            }
            FileOutputStream fos = context.openFileOutput(BUSYBOX_FILENAME, Context.MODE_PRIVATE);
            InputStream is = context.getAssets().open(BUSYBOX_FILENAME_INTERN);
//            sizeCopied = ByteStreams.copy(is, fos);
//            Preconditions.checkArgument(sizeCopied > 0l, "Failed to copy busybox to this destination: " + busybox.getAbsolutePath());
            boolean readable = busybox.setReadable(false);
            boolean writable = busybox.setWritable(false);
            boolean exec = busybox.setExecutable(true, true);
//            Preconditions.checkArgument(readable && writable && exec, "Failed to change properties of this file: " + busybox.getAbsolutePath());
            return sizeCopied;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sizeCopied;
    }


    /**
     * Check if BusyBox application has been copied
     * in the private data folder of the application.
     *
     * @param context
     * @return true if already copied.
     */
    private static boolean checkBusyBox(Context context) {
        File busybox = getInternalBusybox(context);
        return busybox.exists()
                && busybox.length() == BUSYBOX_SIZE
                && busybox.canExecute();
    }
}
