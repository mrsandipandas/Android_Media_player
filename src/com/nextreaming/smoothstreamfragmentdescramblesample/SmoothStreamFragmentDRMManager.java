/*
 * DRM Manager class.
 *
 * @copyright:
 * Copyright (c) 2009-2010 Nextreaming Inc, all rights reserved.
 */

package com.nextreaming.smoothstreamfragmentdescramblesample;

import java.lang.String;

public class SmoothStreamFragmentDRMManager {
    private static final String TAG = "SSFragmentDrmManager";

    public SmoothStreamFragmentDRMManager() {

    }

    public static native int initDRMManager(String strEngineLibName);

    static {
        System.loadLibrary("smoothstreamfragmentdescramblesample_jni");
    }
}
