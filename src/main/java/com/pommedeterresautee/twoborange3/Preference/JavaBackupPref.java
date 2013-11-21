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
import android.text.TextUtils;
import com.pommedeterresautee.twoborange3.R;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * Read the preferences regarding the construction of the Backup command.
 *
 * @author minigeantvert
 */
public class JavaBackupPref extends BasePref {

    private final static String BACKUP_PATH_KEY = "backup_path";
    private final static String BACKUP_NAME_KEY = "backup_name";
    private final static String BACKUP_TYPE_KEY = "backup_type";
    private final static String BACKUP_SDEXT_PATH_KEY = "partition_sd-ext_path";
    private final static String BACKUP_SDEXT_ACTIVATED_KEY = "partition_sd-ext";
    private final static String BACKUP_PARTITION_SELECTION_KEY = "backup_partition";
    private final static String BACKUP_YAFFS_AS_TARBALL = "backup_yaff_partition_as_tar";
    private final static String KEEP_SCREEN_ON = "keep_screen_on";
    private final static String BACKUP_VIBRATE = "backup_vibrate_to_notify";
    private final static String BACKUP_TAG = "backup_tag";
    private final static String BACKUP_MD5_SUM_GENERATED_TWRP = "backup_no_md5_twrp";
    private final static String BACKUP_ROMNAME_TAG = "backup_tag_rom_name";
    private final static String BACKUP_DEVICE_SERIAL = "Serial number - for TWRP backup";
    private final static String BACKUP_USE_SYSTEM_BUSYBOX = "backup_internal_busybox";
    private final static String BACKUP_ONANDROID_VERSION = "onandroid_version";

    private final PartitionOptionPreference optionPreference;

    private String[] backupArray;

    public JavaBackupPref(Context context) {
        super(context);
        optionPreference = new PartitionOptionPreference()
                .add("partition_boot", "b")
                .add("partition_recovery", "r")
                .add("partition_system", "s")
                .add("partition_data", "d")
                .add("partition_cache", "c")
                .add("partition_android_secure", "a")
                .add("partition_sd-ext", "x")
                .add("partition_acer", "m")
                .add("partition_wimax", "w")
                .add("partition_appslog", "l")
                .add("partition_datadata", "t")
                .add("partition_efs", "e")
                .add("partition_preload", "o")
                .add("partition_cust_backup", "u")
                .add("partition_flexrom", "f")
                .add("partition_boot_hp", "p")
                .add("partition_cpuid", "i");
    }

    /**
     * @return the version of onandroid installed on the device.
     */
    public String getOnandroidVersion() {
        return readInNewThread(BACKUP_ONANDROID_VERSION, String.class);
    }

    /**
     * Set the version of Onandroid installed on the device.
     *
     * @param version
     */
    public void setOnandroidVersion(String version) {
        saveIt(BACKUP_ONANDROID_VERSION, version);
    }


    /**
     * @return the SD Card path where to backup
     */
    public String getBackupPath() {
        return readInNewThread(BACKUP_PATH_KEY, String.class);
    }

    /**
     * Set the path to the storage where backup is done.
     *
     * @param path
     */
    public void setBackupPath(String path) {
        saveIt(BACKUP_PATH_KEY, path);
    }

    /**
     * TWRP path.
     *
     * @return
     */
    public String getDeviceSerial() {
        return readInNewThread(BACKUP_DEVICE_SERIAL, String.class);
    }

    /**
     * Set the path to the TWRP storage where backup is done.
     *
     * @param serial
     */
    public void setSerialTWRP(String serial) {
        if(!TextUtils.isEmpty(serial) && !serial.contains(" ")){
            saveIt(BACKUP_DEVICE_SERIAL, serial);
        }
    }


    /**
     * Add some ext partition to the backup.
     *
     * @return true if activated
     */
    public boolean isSDExtpathActivated() {
        return readInNewThread(BACKUP_SDEXT_ACTIVATED_KEY, Boolean.class);
    }

    /**
     * @return the SD Ext Card path to backup
     */
    public String getSDExtPath() {
        return readInNewThread(BACKUP_SDEXT_PATH_KEY, String.class);
    }

    public void setSDExtPath(String path) {
        saveIt(BACKUP_SDEXT_PATH_KEY, path);
    }


    /**
     * Remove accents and spaces from a text.
     *
     * @param text
     * @return cleaned text
     */
    private String cleanText(String text) {
        text = Normalizer
                .normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", ""); // remove accents
        text = text.replaceAll("[^a-zA-Z0-9\\.\\-]+", "_"); // replace everything else with _
        return text;
    }

    public void setBackupType(String selection) {
        saveIt(BACKUP_TYPE_KEY, selection);

    }

    public int getBackupTypePosition() {
        String type = getBackupType();
        return Arrays.asList(getContext().getResources().getStringArray(R.array.BackupValues)).indexOf(type);

    }

    /**
     * @return the type of backup.
     */
    private String getBackupType() {
        return readInNewThread(BACKUP_TYPE_KEY, String.class);
    }

    public boolean isOldBackupMethod() {
        String[] keys = getBackupArray();
        String selection = getBackupType();
        return selection == null || selection.equals("") || selection.equals(keys[0]);
    }

    public boolean isIncrementalBackupMethod() {
        String[] keys = getBackupArray();
        String selection = getBackupType();
        return selection != null && selection.equals(keys[1]);
    }

    public boolean isSplitBackupMethod() {
        String[] keys = getBackupArray();
        String selection = getBackupType();
        return selection != null && selection.equals(keys[2]);
    }

    public boolean isTWRPBackupMethod() {
        String[] keys = getBackupArray();
        String selection = getBackupType();
        return selection != null && (selection.equals(keys[3]) || selection.equals(keys[4]));
    }

    private String[] getBackupArray() {
        if (backupArray == null) {
            backupArray = getContext().getResources().getStringArray(R.array.BackupValues);
        }
        return backupArray;
    }

    /**
     * @return the option to introduce to select some partition to backup.
     */
    public String getOptionPartition() {
        return optionPreference.getCommandToExec();
    }

    /**
     * @return true if the partition selection is activated.
     */
    public boolean isPartitionSelectionActivated() {
        return readInCurrentThread(BACKUP_PARTITION_SELECTION_KEY, Boolean.class);
    }

    /**
     * Does the phone should stay on during the backup.
     *
     * @return
     */
    public boolean getKeepScreenOn() {
        return readInNewThread(KEEP_SCREEN_ON, Boolean.class);
    }

    /**
     * Does the device should vibrate at the end of a backup.
     *
     * @return
     */
    public boolean getVibrate() {
        return readInNewThread(BACKUP_VIBRATE, Boolean.class);
    }

    public boolean isTWRPTarCompressionEnabled() {
        String[] keys = getBackupArray();
        String selection = getBackupType();
        return selection != null && selection.equals(keys[4]);
    }

    public boolean isYafsAsTar() {
        return readInNewThread(BACKUP_YAFFS_AS_TARBALL, Boolean.class);
    }

    public boolean isMD5SumfFilesGenerationInTWRPActivated() {
        return readInNewThread(BACKUP_MD5_SUM_GENERATED_TWRP, Boolean.class);
    }

    /**
     * Check if the busybox to use is the one
     * embedded or the one installed on the system.
     *
     * @return true if need to use the one on the system
     */
    public boolean isSetToUseSystemBusybox() {
        return readInNewThread(BACKUP_USE_SYSTEM_BUSYBOX, Boolean.class);
    }

    /**
     * Read the preferences to extract the command to execute.
     */
    private class PartitionOptionPreference {
        private List<String> mKeyList;
        private List<String> mCommandOptionList;

        private PartitionOptionPreference() {
            mKeyList = new ArrayList<String>();
            mCommandOptionList = new ArrayList<String>();
        }

        /**
         * Add each key plus the option which goes with.
         *
         * @param key           in Preference.
         * @param commandOption the ID of the partition in the command.
         * @return
         */
        private PartitionOptionPreference add(String key, String commandOption) {
            mKeyList.add(key);
            mCommandOptionList.add(commandOption);
            return this;
        }

        /**
         * @return a String representing the command to inject.
         */
        public String getCommandToExec() {
            readPreference();
            StringBuilder result = new StringBuilder();
            for (String option : mCommandOptionList) {
                result.append(option);
            }
            return result.toString();
        }

        /**
         * Filter the commands to keep.
         */
        private void readPreference() {
            Iterator<String> it = mCommandOptionList.iterator();
            for (String key : mKeyList) {
                boolean isActivated = readInCurrentThread(key, Boolean.class);
                it.next();
                if (!isActivated) {
                    it.remove();
                }
            }
        }
    }
}
