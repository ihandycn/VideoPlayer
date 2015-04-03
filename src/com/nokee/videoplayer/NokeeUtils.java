package com.nokee.videoplayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.drm.DrmManagerClient;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Debug.MemoryInfo;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.view.Window;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class NokeeUtils {
    private static final String TAG = "NokeeUtils";
    private static final boolean LOG = true;
    
    private NokeeUtils() {}
    
    public static boolean isRtspStreaming(final Uri uri) {
        boolean rtsp = false;
        if (uri != null && "rtsp".equalsIgnoreCase(uri.getScheme())) {
            rtsp = true;
        }
        if (LOG) {
            LogUtils.v(TAG, "isRtspStreaming(" + uri + ") return " + rtsp);
        }
        return rtsp;
    }
    
    public static boolean isHttpStreaming(final Uri uri) {
        boolean http = false;
        if (uri != null && "http".equalsIgnoreCase(uri.getScheme())) {
            http = true;
        }
        if (LOG) {
            LogUtils.v(TAG, "isHttpStreaming(" + uri + ") return " + http);
        }
        return http;
    }
    
    public static boolean isLocalFile(final Uri uri) {
        final boolean local = (!isRtspStreaming(uri) && !isHttpStreaming(uri));
        if (LOG) {
            LogUtils.v(TAG, "isLocalFile(" + uri + ") return " + local);
        }
        return local;
    }
    
    //for drm
    private static DrmManagerClient sDrmClient;
    public static DrmManagerClient getDrmManager(final Context context) {
        if (sDrmClient == null) {
            sDrmClient = new DrmManagerClient(context);
        }
        return sDrmClient;
    }
    
    public static boolean isSupportDrm() {
    	return false;
    }
    
    public static Bitmap overlayDrmIcon(final Context context, final String path, final int action, final Bitmap bkg) {
    	return null;
    }
    
    public static boolean isMediaScanning(final Context context) {
        boolean result = false;
        final Cursor cursor = query(context, MediaStore.getMediaScannerUri(), 
                new String [] { MediaStore.MEDIA_SCANNER_VOLUME }, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                final String scanVolumne = cursor.getString(0);
                result = "external".equals(scanVolumne);
                if (LOG) {
                    LogUtils.v(TAG, "isMediaScanning() scanVolumne=" + scanVolumne);
                }
            }
            cursor.close(); 
        } 
        if (LOG) {
            LogUtils.v(TAG, "isMediaScanning() cursor=" + cursor + ", result=" + result);
        }
        return result;
    }
    
    public static Cursor query(final Context context, final Uri uri, final String[] projection,
            final String selection, final String[] selectionArgs, final String sortOrder) {
        return query(context, uri, projection, selection, selectionArgs, sortOrder, 0);
    }
    
    public static Cursor query(final Context context, Uri uri, final String[] projection,
            final String selection, final String[] selectionArgs, final String sortOrder, final int limit) {
        try {
            final ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return null;
            }
            if (limit > 0) {
                uri = uri.buildUpon().appendQueryParameter("limit", "" + limit).build();
            }
            return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
         } catch (final UnsupportedOperationException ex) {
            return null;
        }
        
    }
    
    public static void enableSpinnerState(final Activity a) {
        if (LOG) {
            LogUtils.v(TAG, "enableSpinnerState(" + a + ")");
        }
        a.getWindow().setFeatureInt(
                Window.FEATURE_PROGRESS,
                Window.PROGRESS_START);
        a.getWindow().setFeatureInt(
                Window.FEATURE_PROGRESS,
                Window.PROGRESS_VISIBILITY_ON);
    }
    
    public static void disableSpinnerState(final Activity a) {
        if (LOG) {
            LogUtils.v(TAG, "disableSpinnerState(" + a + ")");
        }
        a.getWindow().setFeatureInt(
                Window.FEATURE_PROGRESS,
                Window.PROGRESS_END);
        a.getWindow().setFeatureInt(
                Window.FEATURE_PROGRESS,
                Window.PROGRESS_VISIBILITY_OFF);
    }
    
    public static boolean isMediaMounted(final Context context) {
        boolean mounted = false;
        String defaultStoragePath = null;
        String defaultStorageState = null;
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mounted = true;
        } 
//        else {
//            final StorageManager storageManager = (StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
//            if (storageManager != null) {
//                defaultStoragePath = StorageManager.getDefaultPath();
//                defaultStorageState = storageManager.getVolumeState(defaultStoragePath);
//                if (Environment.MEDIA_MOUNTED.equals(defaultStorageState) ||
//                        Environment.MEDIA_MOUNTED_READ_ONLY.equals(defaultStorageState)) {
//                    mounted = true;
//                }
//            }
//        }
        if (LOG) {
            LogUtils.v(TAG, "isMediaMounted() return " + mounted + ", state=" + state
                    + ", defaultStoragePath=" + defaultStoragePath + ", defaultStorageState=" + defaultStorageState);
        }
        return mounted;
    }
    
    public static String stringForTime(final long millis) {
        final int totalSeconds = (int) millis / 1000;
        final int seconds = totalSeconds % 60;
        final int minutes = (totalSeconds / 60) % 60;
        final int hours = totalSeconds / 3600;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
    
    private static Date sDate = new Date(); //cause lots of CPU
    public static String localTime(final long millis) {
        sDate.setTime(millis);
        return sDate.toLocaleString();
    }
    
    public static void logMemory(final String title) {
        final MemoryInfo mi = new MemoryInfo();
        android.os.Debug.getMemoryInfo(mi);
        final String tagtitle = "logMemory() " + title;
        LogUtils.v(TAG, tagtitle + "         PrivateDirty    Pss     SharedDirty");
        LogUtils.v(TAG, tagtitle + " dalvik: " + mi.dalvikPrivateDirty + ", " + mi.dalvikPss
                + ", " + mi.dalvikSharedDirty + ".");
        LogUtils.v(TAG, tagtitle + " native: " + mi.nativePrivateDirty + ", " + mi.nativePss
                + ", " + mi.nativeSharedDirty + ".");
        LogUtils.v(TAG, tagtitle + " other: " + mi.otherPrivateDirty + ", " + mi.otherPss
                + ", " + mi.otherSharedDirty + ".");
        LogUtils.v(TAG, tagtitle + " total: " + mi.getTotalPrivateDirty() + ", " + mi.getTotalPss()
                + ", " + mi.getTotalSharedDirty() + ".");
    }
    
    public static void saveBitmap(final String tag, final String msg, final Bitmap bitmap) {
        if (bitmap == null) {
            LogUtils.v(tag, "[" + msg + "] bitmap=null");
        }
        final long now = System.currentTimeMillis();
        final String fileName = "/mnt/sdcard/nomedia/" + now + ".jpg";
        final File temp = new File(fileName);
        final File dir = temp.getParentFile();
        if (!dir.exists()) { //create debug folder
            dir.mkdir();
        }
        final File nomedia = new File("/mnt/sdcard/nomedia/.nomedia");
        if (!nomedia.exists()) { //add .nomedia file
            try {
                nomedia.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(temp);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
        } catch (final IOException ex1) {
            ex1.printStackTrace();
        } catch (final SecurityException ex2) {
            ex2.printStackTrace();
        } finally  {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (LOG) {
            LogUtils.v(tag, "[" + msg + "] write file filename=" + fileName);
        }
    }
}
