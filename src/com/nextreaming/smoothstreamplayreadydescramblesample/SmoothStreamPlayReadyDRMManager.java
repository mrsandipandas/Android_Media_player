/*
 * DRM Manager class.
 *
 * @copyright:
 * Copyright (c) 2009-2010 Nextreaming Inc, all rights reserved.
 */

package com.nextreaming.smoothstreamplayreadydescramblesample;

public class SmoothStreamPlayReadyDRMManager {

    private static final String TAG = "SSPlayReadyDrmManager";

    public SmoothStreamPlayReadyDRMManager() {
    }

    public static native int initDRMManager(String strEngineLibName);

    static {
        System.loadLibrary("smoothstreamplayreadydescramblesample_jni");
    }
}
