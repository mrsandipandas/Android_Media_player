/*
 * DRM Manager class.
 *
 * @copyright:
 * Copyright (c) 2009-2010 Nextreaming Inc, all rights reserved.
 */

package com.nextreaming.remoteFileIO;

import java.lang.String;

public class remoteFileIOManager {
    private static final String TAG = "remoteFileIOManager";

    public remoteFileIOManager() {

    }

    public static native int registerRemoteFileIO(String strEngineLibName);

    static {
        System.loadLibrary("remotefileiosample_jni");
    }
}
