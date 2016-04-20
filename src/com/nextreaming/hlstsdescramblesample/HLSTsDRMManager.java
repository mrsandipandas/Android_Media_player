/*
 * DRM Manager class.
 *
 * @copyright:
 * Copyright (c) 2009-2010 Nextreaming Inc, all rights reserved.
 */

package com.nextreaming.hlstsdescramblesample;

import java.lang.String;

public class HLSTsDRMManager {
    private static final String TAG = "HLSTsDrmManager";

    public HLSTsDRMManager() {

    }

    public static native int initDRMManager(String strEngineLibName);

    static {
        System.loadLibrary("hlstsdescramblesample_jni");
    }
}
