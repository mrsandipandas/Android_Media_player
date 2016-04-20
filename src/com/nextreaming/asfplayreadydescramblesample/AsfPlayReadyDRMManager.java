/*
 * DRM Manager class.
 *
 * @copyright:
 * Copyright (c) 2009-2010 Nextreaming Inc, all rights reserved.
 */

package com.nextreaming.asfplayreadydescramblesample;

public class AsfPlayReadyDRMManager {

    private static final String TAG = "AsfPlayReadyDrmManager";

    public AsfPlayReadyDRMManager() {
    }

    public static native int initDRMManager(String strEngineLibName);

    static {
        System.loadLibrary("asfplayreadydescramblesample_jni");
    }
}
