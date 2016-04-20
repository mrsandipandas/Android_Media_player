/*
 * DRM Manager class.
 *
 * @copyright:
 * Copyright (c) 2009-2010 Nextreaming Inc, all rights reserved.
 */

package com.nextreaming.getpdblocksample;

public class GetPDBlockManager {

    private static final String TAG = "GetPDBlockManager";

    public GetPDBlockManager() {
    }

    public static native int initManager(String strEngineLibName);

    static 
    {
        System.loadLibrary("getpdblocksample_jni");
    }
}
