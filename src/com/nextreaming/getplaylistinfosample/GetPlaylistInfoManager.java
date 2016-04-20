/*
 * DRM Manager class.
 *
 * @copyright:
 * Copyright (c) 2009-2010 Nextreaming Inc, all rights reserved.
 */

package com.nextreaming.getplaylistinfosample;

public class GetPlaylistInfoManager {

    private static final String TAG = "GetPlaylistInfoManager";

    public GetPlaylistInfoManager() {
    }

    public static native int initManager(String strEngineLibName);

    static 
    {
        System.loadLibrary("getplaylistinfosample_jni");
    }
}
