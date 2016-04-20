/*
 * DRM Manager class.
 *
 * @copyright:
 * Copyright (c) 2009-2010 Nextreaming Inc, all rights reserved.
 */

package com.nextreaming.piffplayreadydescramblesample;

public class PiffPlayReadyDRMManager {

    private static final String TAG = "SSPlayReadyDrmManager";

    public PiffPlayReadyDRMManager() {
    }

    public static native int initDRMManager(String strEngineLibName);

    static {
        System.loadLibrary("piffplayreadydescramblesample_jni");
    }
}
