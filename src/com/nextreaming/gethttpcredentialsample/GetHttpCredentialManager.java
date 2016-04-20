/*
 * DRM Manager class.
 *
 * @copyright:
 * Copyright (c) 2009-2010 Nextreaming Inc, all rights reserved.
 */

package com.nextreaming.gethttpcredentialsample;

public class GetHttpCredentialManager {

    private static final String TAG = "GetKeyExtManager";

    public GetHttpCredentialManager() {
    }

    public static native int initManager(String strEngineLibName);

    static 
    {
        System.loadLibrary("gethttpcredentialsample_jni");
    }
}
