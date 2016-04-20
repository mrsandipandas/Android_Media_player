/**
   File : HLSTsDescrambleSample_jni.c

   Copyright (c) 2010 Nextreaming Inc, all rights reserved.
 */


#include "dlfcn.h"
#include "nexplayer_jni.h"


#include <jni.h>
#include <assert.h>
#include <ctype.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <android/log.h>


#define NEXPLAYERENGINE_LIB "/data/data/com.nextreaming.nexplayersample/lib/libnexplayerengine.so"
#define NEXPLAYERENGINE_GETPDBLOCK_CALLBACK_FUNC "nexPlayerSWP_RegisterGetPDBlockCallBackFunc"
                                                        

#define  LOG_TAG    "GETPDBLOCK_SAMPLE"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGV(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)


static unsigned int GetPDBlockCallbackFunc(char* 			pBlockBuf,
											long long 		ulOffset,
											int 				uiBlockSize,
											void*			pUserData)
{
	LOGI("[GetPDBlockCallbackFunc ] block :0x%x Offset %lld Size : %d  UserData0x%x\n", pBlockBuf,ulOffset, uiBlockSize, pUserData );
	return 0;	
}								 

	

jint Java_com_nextreaming_getpdblocksample_GetPDBlockManager_initManager (JNIEnv * env, 
														   	   jobject clazz,
														   	   jstring libName)
{
    LOGI("[initManager] Start \n");
    
    void *handle = NULL;

    int (*fptr)(NEXPLAYERGetPDBlockCallbackFunc pCallbackFunc, void *pUserData); 
    
    char buf[512];
    const jbyte *str;
    str = (*env)->GetStringUTFChars(env, libName, NULL);
    if(str != NULL)
	{
		handle = dlopen(str, RTLD_LAZY);  
		
		LOGI("[initDRMManager] libName[0x%X]:%s",handle, str);
	}
    else
    {
	    /* Load Default NexPlayerEngine library */
	    handle = dlopen(NEXPLAYERENGINE_LIB, RTLD_LAZY);
	}
	
    LOGI("initializeAgent : nextreaming handle=%p", handle);
    if (handle == NULL)
    {
        LOGI("[initDRMManager] error=%s", dlerror());
        return -1;
    }
    
    /* Get DRM register function pointer*/   
    fptr = (int (*)(NEXPLAYERGetPDBlockCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_GETPDBLOCK_CALLBACK_FUNC);
    
    LOGI("[initDRMManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initDRMManager] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)(GetPDBlockCallbackFunc, NULL);		

    dlclose(handle); 
   
    return 0;
}
