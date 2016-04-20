/*
 * DRM Manager class.
 *
 * @copyright:
 * Copyright (c) 2009-2010 Nextreaming Inc, all rights reserved.
 */

package com.nextreaming.smoothstreampiffdescramblesample;

public class SmoothStreamPiffDRMManager {

    private static final String TAG = "SSPiffDrmManager";

    public SmoothStreamPiffDRMManager() {
    }

    public static native int initDRMManager(String strEngineLibName);

    static {
        System.loadLibrary("smoothstreampiffdescramblesample_jni");
    }
}
