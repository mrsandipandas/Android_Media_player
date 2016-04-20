/*
 * DRM Manager class.
 *
 * @copyright:
 * Copyright (c) 2009-2010 Nextreaming Inc, all rights reserved.
 */

package com.nextreaming.drmsample;

import java.lang.String;

public class DRMManager {
    private static final String TAG = "DrmManager";

    public DRMManager() {

    }

    public static native int initDRMManager(String strEngineLibName);

    static {
        System.loadLibrary("drmsample_jni");
    }
}
