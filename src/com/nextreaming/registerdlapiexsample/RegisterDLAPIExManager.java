/*
 * DRM Manager class.
 *
 * @copyright:
 * Copyright (c) 2009-2010 Nextreaming Inc, all rights reserved.
 */

package com.nextreaming.registerdlapiexsample;

public class RegisterDLAPIExManager {

    private static final String TAG = "GetKeyExtManager";

    public RegisterDLAPIExManager() {
    }

    public static native int initDLAPIExManager(String strEngineLibName);

    static 
    {
        System.loadLibrary("registerdlapiexsample_jni");
    }
}
