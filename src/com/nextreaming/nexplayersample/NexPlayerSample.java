/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nextreaming.nexplayersample;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.nextreaming.gethttpcredentialsample.GetHttpCredentialManager;
import com.nextreaming.getpdblocksample.GetPDBlockManager;
import com.nextreaming.nexlogger.NexLogRecorder;
import com.nextreaming.nexplayerengine.GLRenderer;
import com.nextreaming.nexplayerengine.NexContentInformation;
import com.nextreaming.nexplayerengine.NexID3TagPicture;
import com.nextreaming.nexplayerengine.NexID3TagText;
import com.nextreaming.nexplayerengine.NexPlayer;
import com.nextreaming.nexplayerengine.NexPlayer.NexErrorCode;
import com.nextreaming.nexplayerengine.NexPlayer.NexProperty;
/**
 * Use a custom platform library.
 */
public class NexPlayerSample extends Activity implements
		SurfaceHolder.Callback, NexPlayer.IListener, GLRenderer.IListener {
	private static final String TAG = "[PLAYER_SAMPLE]";

	private NexPlayer mNexPlayer;
	private Uri mUri;
	private String mPath;
	/*
	 *  Android Renderer and H/W Renderer can't be used at same time.
	 *  and two renderer uses other conditions.
	 */
	private SurfaceView mPreviewForSW;	// for SW Video Renderer
	private SurfaceView mPreviewForHW;	// for H/W Video Renderer
	private SurfaceHolder mSurfaceHolderForSW;
	private SurfaceHolder mSurfaceHolderForHW;	
	private Boolean mCreateSurface = false;	// Flag for checking creation of surface.s
	private Boolean mFullscreen;
	public int mPlayingTime;
	public int mContentDuration;
	public int mBufferedTime;
	private String mStrMsg;
	private String mStrContentInfo;

	public ArrayAdapter mAdaptBookmark;

	private PowerManager.WakeLock mWakeLock = null;
	private PowerManager.WakeLock mDimLock = null;

	private int mDevicePixelFormat;
	private int mScreenPixelFormat;
	private int mSurfaceWidth = 0;
	private int mSurfaceHeight = 0;
	private int mVideoWidth = 0;
	private int mVideoHeight = 0;
	private int mScreenWidth = 0;
	private int mScreenHeight = 0;

	private boolean mFirstVideoRenderCreate = false;
	
	private boolean mPlayStarted = false;
	private boolean mFilterVideo = false;
	private boolean mShowContentInfo = false;
	private boolean mEnableTrackDown = false;				
	private boolean mVM = false;							
	private boolean mBookmarkActivityEnabled = false;

	private NexContentInformation mContentInfo;

	private ImageView mImageView = null;
	private ImageView mThumbnailView = null;
	
	public final NexPlayerSample mMP = this;

	public static final Handler mHandler = new Handler();

	AlertDialog mAudioEffectDialog;

	static final int DIALOG_SELECT_AUDIOSTREAM = 1;
	static final int DIALOG_SELECT_VIDEOSTREAM = 2;
	
	private Bitmap mFrameBitmap = null;

	private Paint solidPaint = null;
	private Paint blitPaint = null;
	private boolean mBoolIsSetDisplayed = false;
	private int mClearReq = 0;
	private GLRenderer glRenderer;
	private boolean UseOpenGL = false;
	private Context mContext;
	private GLRenderer.IListener mGLListener;
	
	private int mStatusReportMessage = 0; // MONGTO
	private int	mStatusReportParam1 = 0;
	
	private boolean mInitGLRenderer = false;

	/*
	 * Called when the activity is first created.
	 */
	public void onCreate(Bundle icicle) {
		// NexLogRecorder write log when exception generated, while sample is running.
		NexLogRecorder.getInstance().startLogging(this);
		super.onCreate(icicle);
		
		Log.d(TAG, "layout.nexplayer_sample is " + R.layout.nexplayer_sample);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.nexplayer_sample);

		// Get Pixel Format of Device. NexPlayer use it as default.
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		mDevicePixelFormat = display.getPixelFormat();
		Log.d(TAG, "Device Pixel Format :  " + mDevicePixelFormat);

		// Set video rendering window
		mPreviewForSW = (SurfaceView) findViewById(R.id.surface);
		mPreviewForHW = (SurfaceView) findViewById(R.id.surface2);
		mSurfaceHolderForSW = mPreviewForSW.getHolder();
		mSurfaceHolderForSW.addCallback(this);
		mSurfaceHolderForSW.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);

		mSurfaceHolderForHW = mPreviewForHW.getHolder();
		mSurfaceHolderForHW.addCallback(this);
		mSurfaceHolderForHW.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		// Both renderer can't visible at the same time.
		// Only one surface can be used.
		mPreviewForSW.setVisibility(View.INVISIBLE); 
		mPreviewForHW.setVisibility(View.INVISIBLE);
		Log.d(TAG, "mPreview for SW is " + mPreviewForSW);		
		Log.d(TAG, "mPreview for HW is " + mPreviewForHW);
		Log.d(TAG, "mSurfaceHolder For SW is " + mSurfaceHolderForSW);
		Log.d(TAG, "mSurfaceHolder For HW is " + mSurfaceHolderForHW);

		if (mDevicePixelFormat == PixelFormat.RGBA_8888
				|| mDevicePixelFormat == PixelFormat.RGBX_8888
				|| mDevicePixelFormat == PixelFormat.RGB_888
				|| mDevicePixelFormat == 5)
		{
			mScreenPixelFormat = PixelFormat.RGBA_8888;
			Log.d(TAG, "888 : DevicePixelFormat:" + mDevicePixelFormat + "  ScreenPixelFormat:" + mScreenPixelFormat);
		} 
		else 
		{
			mScreenPixelFormat = PixelFormat.RGB_565;
			Log.d(TAG, "565 : DevicePixelFormat:" + mDevicePixelFormat + "  ScreenPixelFormat:" + mScreenPixelFormat);
		}

		// we advice to use RGB565, when device is Moto droid.
		// if RGB8888, renderer performance is reduced drastically.
		if (android.os.Build.MODEL.equals("Milestone")) {
			Log.d(TAG, "THIS IS Motorola DROID.");
			mScreenPixelFormat = PixelFormat.RGB_565;
		}

		// mScreenPixelFormat = PixelFormat.RGB_565; // Comment out if you want
		// to display video as RGB565 mode always.

		mSurfaceHolderForSW.setFormat(mScreenPixelFormat);
		mSurfaceHolderForHW.setFormat(mScreenPixelFormat);

		mFullscreen = false;

		mImageView = (ImageView) findViewById(R.id.imageview);
		mImageView.setVisibility(View.INVISIBLE);
		
		mThumbnailView = (ImageView)findViewById(R.id.thumnailView);
		mThumbnailView.setVisibility(View.INVISIBLE);
		
		Button button;

		button = (Button) findViewById(R.id.buttonLocal);
		button.setOnClickListener(mLocalTestListener);

		button = (Button) findViewById(R.id.buttonRTSP);
		button.setOnClickListener(mRTSPTestListener);

		button = (Button) findViewById(R.id.buttonDownload);
		button.setOnClickListener(mDownloadTestListener);

		button = (Button) findViewById(R.id.buttonHTTPLive);
		button.setOnClickListener(mHTTPLiveTestListener);

		button = (Button) findViewById(R.id.buttonStop);
		button.setOnClickListener(mStopListener);

		button = (Button) findViewById(R.id.buttonPausePlay);
		button.setOnClickListener(mPausePlayListener);

		button = (Button) findViewById(R.id.buttonRew);
		button.setOnClickListener(mRewListener);

		button = (Button) findViewById(R.id.buttonFF);
		button.setOnClickListener(mFFListener);

		button = (Button) findViewById(R.id.buttonBookmark);
		button.setOnClickListener(mBookmarkListener);

		button = (Button) findViewById(R.id.buttonAudioStream);
		button.setOnClickListener(mAudioStreamListener);
		button.setEnabled(false);

		button = (Button) findViewById(R.id.buttonVideoStream);
		button.setOnClickListener(mVideoStreamListener);
		button.setEnabled(false);
		
		button = (Button) findViewById(R.id.buttonCapture);
		button.setOnClickListener(mVideoCaptureListener);
		button.setEnabled(false);
		
		// for test changeMaxBandwidth() method of nexPlayer.
		SeekBar seekbar = (SeekBar)findViewById(R.id.seekBar_BandWidth);
		seekbar.setOnSeekBarChangeListener(mSeekbarListner);
		seekbar.setProgress(100);
		seekbar.setEnabled(true);
		seekbar.setPadding(40, 0,40, 0);

		mHandler.post(new Runnable() {
			public void run() {
				try {
					TextView txtProgress = (TextView) findViewById(R.id.BW_Info);
					txtProgress.setText(" BW :");
					//txtProgress.setPadding(0, 0, 0, (int) (3 * scaleDIPtoPX + 0.5f));

				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});

		TextView txtSubtitle = (TextView) findViewById(R.id.subtitle);
		txtSubtitle.setShadowLayer(2, 3, 3, 0);

		CheckBox checkBox = (CheckBox) findViewById(R.id.checkSmoothRender);
		checkBox.setOnCheckedChangeListener(mSmoothRenderListner);

		checkBox = (CheckBox) findViewById(R.id.checkVideoFilter);
		checkBox.setOnCheckedChangeListener(mVideoFilterListner);
		
		checkBox = (CheckBox) findViewById(R.id.enableTrackDown);			// JDKIM 2011/04/19
		checkBox.setOnCheckedChangeListener(mEnableTrackDownListner);		// JDKIM 2011/04/19

		checkBox = (CheckBox) findViewById(R.id.checkShowContentInfo);
		checkBox.setOnCheckedChangeListener(mShowContentInfoListner);
		
		
		checkBox = (CheckBox) findViewById(R.id.enableVM);
		checkBox.setOnCheckedChangeListener(mVMListener);
		
		mPath = new String();

		int nLogLevel = 0;
		mNexPlayer = new NexPlayer();
		
		// for HW Renderer
		// HW Renderer need configuration file and location of configuration file.
		// NexPlayer decide to use H/W Renderer based on configuration file.
		mNexPlayer.SetConfigFilePath("/sdcard/nexDevice.cfg");

		/* 
		 * Definitely you do create DLAPIExmanager after create NexPlayer.
		 * DLAPI is for dynamic load. 
		 * RegisterDLAPIExSample_jni.c show example.
		 * 
		 */
		/*
		{ 
			RegisterDLAPIExManager DLAPIExManager = new RegisterDLAPIExManager(); 
			 String strEnginePath;
			 strEnginePath = "/data/data/com.nextreaming.nexplayersample/lib/libnexplayerengine.so"; 
			 DLAPIExManager.initDLAPIExManager(strEnginePath); 
		}
		*/
		
		
		// Color Depth is for nexPlayer's Renderer.
		int colorDepth;
		if(this.mScreenPixelFormat == PixelFormat.RGBA_8888)
		{
			colorDepth = 1;
		}
		else
		{
			colorDepth = 4;
		}
		
		String strVersion =android.os.Build.VERSION.RELEASE;		
		Log.d(TAG, "PLATFORM INFO: " + strVersion);
		
		// if Honeycomb, NexPlayer must use OpenGL Renderer.
		if(strVersion.startsWith("3."))
		{
			UseOpenGL = true;
		}
		
		// if you wanna use opengl set UseOpenGL to true.
		if(UseOpenGL)
		{
			colorDepth = 4;
			// NexPlayer.NEX_DEVICE_USE_OPENGL value forces to use OpenGLRenderer
			if (mNexPlayer.init(this, NexPlayer.NEX_DEVICE_USE_OPENGL, nLogLevel, colorDepth) == false) // JDKIM 2010/11/11
			{
				Log.d(TAG, "NexPlayerSDK Initiaize Fail.");
				return;
			}
		}
		else
		{
			if (mNexPlayer.init(this, android.os.Build.MODEL, nLogLevel, colorDepth) == false) // JDKIM 2010/11/11
			{
				Log.d(TAG, "NexPlayerSDK Initiaize Fail.");
				return;
			}
		}		


		// RenderMode is changed case by case.
		// You need to check render mode.
		// If you want to support H/W Renderer, you have to check after open().
		if(mNexPlayer.GetRenderMode() == NexPlayer.NEX_USE_RENDER_OPENGL)
		{
			UseOpenGL = true;
		}
		// JDKIM : end

		// if possible, nexPlayer didn't down lower bitrates than this value.
		mNexPlayer.setProperty(NexProperty.PREFER_BANDWIDTH, 100);
		// if possible, nexPlayer didn't down audio track.
		// but it has possibility to go down audio only track.
		mNexPlayer.setProperty(NexProperty.PREFER_AV, 1);
		
		mNexPlayer.setListener(this);
		Log.w(TAG, "SetListner Done. ");
		
		// mNexPlayer.setProperties(35, 11); // Show full protocol log.
		// mNexPlayer.setProperty(NexProperty.LOG_LEVEL,
		// NexProperty.LOG_LEVEL_DEBUG | NexProperty.LOG_LEVEL_RTP |
		// NexProperty.LOG_LEVEL_FRAME); // Smooth rendering
		// mNexPlayer.setProperty(NexProperty.AV_SYNC_OFFSET, 2000);
		//mNexPlayer.setProperties(58, "NexPlayer 4.2");
		//String strUserAgent = mNexPlayer.getStringProperties(58);
		//Log.d(TAG, "New User Agent:" + strUserAgent);
		Intent intent = getIntent();
		mUri = intent.getData();
		Log.d(TAG, "intent uri = " + mUri);
		try {
			String scheme = mUri.getScheme();
			Log.d(TAG, "intent uri's scheme = " + scheme);
		} catch (Exception e) {
			Log.d(TAG, "intent uri's scheme = NULL");

		}
		
		updateControlPanelUI(0);

		// Show SDK version information.
		TextView txtProgress = (TextView) findViewById(R.id.PlayTime);
		txtProgress.setText("SDK Version : " + mNexPlayer.getVersion(0) + "."
				+ mNexPlayer.getVersion(1) + "." + mNexPlayer.getVersion(2)
				+ "." + mNexPlayer.getVersion(3));

		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE
				| PowerManager.ACQUIRE_CAUSES_WAKEUP, "NexPlayer");
		mDimLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"NexPlayer");

		mWakeLock.acquire();
		mDimLock.acquire();

		// if openGL, following next code.
		if(UseOpenGL)
		{
			mContext = this;
			mGLListener = this;
			glRenderer = new GLRenderer(mContext, mNexPlayer, mGLListener, colorDepth);
			FrameLayout v = (FrameLayout)findViewById(R.id.gl_container);		
			
			v.addView(glRenderer);
		}

		/* Sample code to register DRM Descramlbe callback function */
		/*
		{ 
			 DRMManager drmManager = new DRMManager(); 
			 String strEnginePath;
			 strEnginePath = "/data/data/com.nextreaming.nexplayersample/lib/libnexplayerengine.so"; 
			 drmManager.initDRMManager(strEnginePath); 
		}
		*/

		/* Sample code to register remote file I/O function */
		/*
		 { 
			 remoteFileIOManager remoteFileIOManager = new remoteFileIOManager(); 
			 String strEnginePath; strEnginePath = "/data/data/com.nextreaming.nexplayersample/lib/libnexplayerengine.so"; 
			 remoteFileIOManager.registerRemoteFileIO(strEnginePath); 
		}
		*/ 
		
		/* Sample code to register HLS TS Descramlbe callback function */
		/*
		{ 
			 HLSTsDRMManager drmManager = new HLSTsDRMManager(); 
			 String strEnginePath; strEnginePath = "/data/data/com.nextreaming.nexplayersample/lib/libnexplayerengine.so"; 
			 drmManager.initDRMManager(strEnginePath); 
		}
		*/
	
		/*
		 * Sample code to register Smooth Stream Fragment Descramlbe callback
		 * function
		 */
		/*
		  { 
			  SmoothStreamFragmentDRMManager drmManager = new
			  SmoothStreamFragmentDRMManager(); 
			  String strEnginePath; strEnginePath = "/data/data/com.nextreaming.nexplayersample/lib/libnexplayerengine.so";
			  drmManager.initDRMManager(strEnginePath); 
		  }
		 */

		/*
		 * Sample code to register Smooth Stream PlayReady callback
		 * function
		 */
		/*
		{
			SmoothStreamPlayReadyDRMManager drmManager = new SmoothStreamPlayReadyDRMManager(); 
			String strEnginePath;
			strEnginePath = "/data/data/com.nextreaming.nexplayersample/lib/libnexplayerengine.so";
			drmManager.initDRMManager(strEnginePath);
		}
		*/
		
		/*
		 * Sample code to register Piff PlayReady callback
		 * function
		 */
		/*
		{
			PiffPlayReadyDRMManager drmManager = new PiffPlayReadyDRMManager(); 
			String strEnginePath;
			strEnginePath = "/data/data/com.nextreaming.nexplayersample/lib/libnexplayerengine.so";
			drmManager.initDRMManager(strEnginePath);
		}
		*/
		
		/*
		 * Sample code to register Asf PlayReady callback
		 * function
		 */
		/*
		{
			AsfPlayReadyDRMManager drmManager = new AsfPlayReadyDRMManager(); 
			String strEnginePath;
			strEnginePath = "/data/data/com.nextreaming.nexplayersample/lib/libnexplayerengine.so";
			drmManager.initDRMManager(strEnginePath);
		}
		*/
		
		
		/*
		 * Sample code to register GetPDBlock callback
		 * function
		 */
		
		{
			GetPDBlockManager drmManager = new GetPDBlockManager(); 
			String strEnginePath;
			strEnginePath = "/data/data/com.nextreaming.nexplayersample/lib/libnexplayerengine.so";
			drmManager.initManager(strEnginePath);
		}
	
		

		/*
		 * Sample code to register GetKeyExt callback
		 * function
		 */
		/*
		{
			GetHttpCredentialManager drmManager = new GetHttpCredentialManager(); 
			String strEnginePath;
			strEnginePath = "/data/data/com.nextreaming.nexplayersample/lib/libnexplayerengine.so";
			drmManager.initManager(strEnginePath);
		}
		
		*/		
		/*
		 * Sample code to register GetPlaylistInfo callback
		 * function
		 */
		/*
		{
			GetPlaylistInfoManager drmManager = new GetPlaylistInfoManager(); 
			String strEnginePath;
			strEnginePath = "/data/data/com.nextreaming.nexplayersample/lib/libnexplayerengine.so";
			drmManager.initManager(strEnginePath);
		}
		*/
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) 
		{
		// nexplayer support content to have multi audio stream.
		// conftent information includes about it,
		case DIALOG_SELECT_AUDIOSTREAM:
			AlertDialog.Builder audioStreamDialog = new AlertDialog.Builder(
					this);
			audioStreamDialog.setTitle("Select AudioStream");
			Log.d(TAG,"TEST AUDIO DIALOG for multi");
			Log.d(TAG,"TEST AUDIO STREAM ID:" +mContentInfo.mCurrAudioStreamID );

			int iAudioCount = 0;
			int iSelectAudioStream = 0;
			for( int i = 0 ; i <  mContentInfo.mStreamNum; i++)
			{
				if(mContentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_AUDIO)
				{
					if( mContentInfo.mCurrAudioStreamID == mContentInfo.mArrStreamInformation[i].mID)
					{
						Log.d(TAG,"TEST AUDIO STREAM ID:" +mContentInfo.mCurrAudioStreamID );
						iSelectAudioStream = iAudioCount;
					}
					iAudioCount++;
				}
			}
			
			String[] arrStrMediaStream = new String[iAudioCount];

			for (int i = 0; i < mContentInfo.mStreamNum; i++) 
			{
				if (mContentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_AUDIO) 
				{
					NexID3TagText name = mContentInfo.mArrStreamInformation[i].mName;

					if (name == null || name.getTextData() == null) 
					{
						arrStrMediaStream[i] = "unnamed Audio";
					}
					else
					{
						try 
						{
							arrStrMediaStream[i] = new String( name.getTextData(), 0, name.getTextData().length, "EUC-KR");
						} 
						catch (UnsupportedEncodingException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

			audioStreamDialog.setSingleChoiceItems(arrStrMediaStream, iSelectAudioStream,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							Log.d(TAG, "VideoStream Choice Items :" + whichButton);
							int iAudioCount = 0;
							int i = 0;
							for ( i = 0; i < mContentInfo.mStreamNum; i++) 
							{
								if(mContentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_AUDIO)
								{
									if( whichButton == iAudioCount)
									{
										// you can change audio stream or track using setMediaStream API.
										// other things like video, text, have to use default value.
										mNexPlayer.setMediaStream(
												mContentInfo.mArrStreamInformation[i].mID,
												NexPlayer.MEDIA_STREAM_DEFAULT_ID,
												NexPlayer.MEDIA_STREAM_DEFAULT_ID,
												NexPlayer.MEDIA_STREAM_DEFAULT_ID);
										break;
									}
									iAudioCount++;
								}
							}
							dialog.dismiss();
						}
					}).setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
		                public void onClick(DialogInterface dialog, int whichButton) 
		                {
		                }
		            });

			dialog = audioStreamDialog.create();
			break;
		case DIALOG_SELECT_VIDEOSTREAM:
			// nexplayer support content to have multi video stream.
			// conftent information includes about it,
			AlertDialog.Builder videoStreamDialog = new AlertDialog.Builder(
					this);
			videoStreamDialog.setTitle("Select VideoStream");
			
			int iVideoCount = 0;
			int iSelectVideoStream = 0;
			
			for( int i = 0 ; i <  mContentInfo.mStreamNum; i++)
			{
				if(mContentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_VIDEO)
				{
					if( mContentInfo.mCurrVideoStreamID == mContentInfo.mArrStreamInformation[i].mID)
					{
						int j = 0;
						for( j = 0 ; j <  mContentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length ; j++)
						{
							if(mContentInfo.mArrStreamInformation[i].mCurrCustomAttrID == mContentInfo.mArrStreamInformation[i].mArrCustomAttribInformation[j].mID)
							{
								Log.d(TAG,"TEST VIDEO STREAM ID:" +mContentInfo.mCurrVideoStreamID + "  ATTR ID :" +mContentInfo.mArrStreamInformation[i].mCurrCustomAttrID );
								break;
							}
						}
						iSelectVideoStream = iVideoCount + j;
					}
					if(mContentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length > 0)
					{
						iVideoCount += mContentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length;
					}
					else
					{
						iVideoCount++;
					}
				}
			}
			Log.d(TAG, "Video Stream Count : " + iVideoCount);
			
			String[] arrStrVideoStream = new String[iVideoCount];
			int k = 0;
			for (int i = 0; i < mContentInfo.mStreamNum; i++) {
				if (mContentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_VIDEO) {
					if (mContentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length > 0) {
						for (int j = 0; j < mContentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length; j++) {
							
							NexID3TagText name = mContentInfo.mArrStreamInformation[i].mName;
							if (name == null || name.getTextData() == null) {
								arrStrVideoStream[k] = "unnamed ";
							} else {
								
								try {
									arrStrVideoStream[k] = new String( name.getTextData(), 0, name.getTextData().length, "EUC-KR");
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							if (mContentInfo.mArrStreamInformation[i].mArrCustomAttribInformation[j].mName != null) 
							{
								NexID3TagText attrName = mContentInfo.mArrStreamInformation[i].mArrCustomAttribInformation[j].mName;
								NexID3TagText attrValue = mContentInfo.mArrStreamInformation[i].mArrCustomAttribInformation[j].mValue;
								
								try 
								{
									String tmpName = new String(attrName.getTextData(), 0, attrName.getTextData().length, "EUC-KR");
									String tmpValue = new String(attrValue.getTextData(), 0, attrValue.getTextData().length, "EUC-KR");
									arrStrVideoStream[k] += "(" + tmpName + " : " + tmpValue + ")";
								} 
								catch (UnsupportedEncodingException e) 
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} 
							else 
							{
								arrStrVideoStream[k] += "(attr : none)";
							}
							Log.d(TAG, "Name:" + arrStrVideoStream[k]);
							k++;
						}
					} else {
						NexID3TagText name = mContentInfo.mArrStreamInformation[i].mName;
						
						if (name == null || name.getTextData() == null) 
						{
							arrStrVideoStream[k] = "unnamed Video";
						} 
						else 
						{
							try 
							{
								arrStrVideoStream[k] = new String( name.getTextData(), 0, name.getTextData().length, "EUC-KR");
							}
							catch (UnsupportedEncodingException e) 
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						Log.d(TAG, "Name:" + arrStrVideoStream[k]);
						k++;
					}
				}
			}

			videoStreamDialog.setSingleChoiceItems(arrStrVideoStream, iSelectVideoStream,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							Log.d(TAG, "VideoStream Choice Items :"
									+ whichButton);
							int iVideoCount = 0;
							int i = 0;
							for ( i = 0; i < mContentInfo.mStreamNum; i++) 
							{
								if(mContentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_VIDEO)
								{
									if( mContentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length> 0)
									{
										for(int j = 0 ; j < mContentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length; j++)
										{
											// you can change video stream or track using setMediaStream API.
											// If content has several attribute id, this api call with Stream ID and Attribute ID
											// other things like audio, text, have to use default value.
											if( whichButton == iVideoCount)
											{
												mNexPlayer.setMediaStream(
														NexPlayer.MEDIA_STREAM_DEFAULT_ID,
														NexPlayer.MEDIA_STREAM_DEFAULT_ID,
														mContentInfo.mArrStreamInformation[i].mID,
														mContentInfo.mArrStreamInformation[i].mArrCustomAttribInformation[j].mID);
												break;
											}
											iVideoCount++;
										}
									}
									else
									{
										if( whichButton == iVideoCount)
										{
											mNexPlayer.setMediaStream(
													NexPlayer.MEDIA_STREAM_DEFAULT_ID,
													NexPlayer.MEDIA_STREAM_DEFAULT_ID,
													mContentInfo.mArrStreamInformation[i].mID,
													NexPlayer.MEDIA_STREAM_DEFAULT_ID);
											break;
										}
										iVideoCount++;
									}
								}
							}
							dialog.dismiss();
						}
					}).setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
		                public void onClick(DialogInterface dialog, int whichButton) 
		                {
		                }
		            });

			dialog = videoStreamDialog.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop() is called");
		
		NexLogRecorder.getInstance().stopLogging();	// stop NexLogRecorder
		try {
			if (mNexPlayer.isInitialized()) 
			{
				Log.d(TAG, "onDestroy start " + mNexPlayer.getState());
				if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PLAY) 
				{
					mNexPlayer.stop();

					while ((mNexPlayer.getState() != NexPlayer.NEXPLAYER_STATE_CLOSED)) 
					{
						Log.d(TAG, "NexPlayer Thread sleep cs="
								+ mNexPlayer.getState());
						if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_STOP || 
								mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PLAY || 
								mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PAUSE)
							mNexPlayer.close();

						if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_NONE
								|| mNexPlayer.getState() == -1)
							break;

						Thread.sleep(100);
					}
				} 
				else 
				{
					mNexPlayer.close();
				}

				updateControlPanelUI(0);
				updateContentInfo("");
			}
		} catch (Exception e) {
			Log.e(TAG, "error: " + e.getMessage(), e);
		}

		super.onStop();

		updateControlPanelUI(0);
		updateContentInfo("");
	}

	@Override
	protected void onPause() 
	{
		// TODO Auto-generated method stub
		// this.unregisterReceiver(mEarphoneEvent);
		Log.d(TAG, "onPause() is called");
		
		if(mNexPlayer.getState() != NexPlayer.NEXPLAYER_STATE_NONE ||
				mNexPlayer.getState() != NexPlayer.NEXPLAYER_STATE_CLOSED)
			mNexPlayer.pause();
		
		super.onPause();
	}

	/*
	 * BroadcastReceiver mEarphoneEvent = new BroadcastReceiver() {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) { //
	 * intent.getStringExtra("state"); int iState = intent.getIntExtra("state",
	 * 18181818);
	 * 
	 * if( iState == 1 ) { mNexPlayer.audiomavensetoutput(0); // earphone } else
	 * { mNexPlayer.audiomavensetoutput(1); // earphone } Log.d(TAG,
	 * "----------------------------------------------------"); Log.d(TAG,
	 * "State : " + iState); Log.d(TAG,
	 * "----------------------------------------------------"); } };
	 */

	@Override
	protected void onResume() {
		// this.registerReceiver(mEarphoneEvent, new
		// IntentFilter(Intent.ACTION_HEADSET_PLUG));
		Log.d(TAG, "onResume() is called");
		
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy() is called");

		mWakeLock.release();
		mDimLock.release();
		
		try {
			// release NexPlayer resources.
			Log.d(TAG, "onDestroy start");
			mNexPlayer.release();
			Log.d(TAG, "onDestroy end ");

		} catch (Exception e) {
			Log.e(TAG, "error: " + e.getMessage(), e);
		}
		
		ActivityManager am  = (ActivityManager)getSystemService(Activity.ACTIVITY_SERVICE);
	    am.restartPackage(getPackageName());
		
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		Log.d(TAG, "TouchEvent: " + event.getAction());

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			
			Log.d(TAG, "Check flag to use OpenGL "  + UseOpenGL);

			// clear Screen for Android Renderer
			// only for JAVA Renderer
			if(mNexPlayer.GetRenderMode() == NexPlayer.NEX_USE_RENDER_JAVA)
			{
				clearCanvas();
			}

			if (this.mCreateSurface == true) 
			{
				if (mFullscreen) 
				{
					Log.d(TAG, "NON-FULL SCREEN MODE");
					mFullscreen = false;
					int top = (mScreenHeight - mVideoHeight) / 2;
					int left = (mScreenWidth - mVideoWidth) / 2;
					Log.d(TAG, "Touch Event - ORIGINAL_SIZE : " + left + " " + top + " " + mVideoWidth + " " + mVideoHeight + " ");
					Log.d(TAG, "Surface Width : " + mScreenWidth + " SurfaceHeight : " + mScreenHeight);

					// If H/W Renderer, if you want to change size of video screen you need to change layout param.
					// H/W Renderer case, it always display video on all space of surface.
					if(mNexPlayer.GetRenderMode() == NexPlayer.NEX_USE_RENDER_HW)
					{						
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
								RelativeLayout.LayoutParams.FILL_PARENT,
								RelativeLayout.LayoutParams.FILL_PARENT);
						params.width = mVideoWidth;
						params.height = mVideoHeight;
						params.topMargin = top;
						params.leftMargin = left;
						mPreviewForHW.setLayoutParams(params);
						//mPreview.setPadding(left, top, mVideoWidth, mVideoHeight);
					}
					// if S/W Renderer, please use setOutputPos API() to resize screen region.
					else if(mNexPlayer.GetRenderMode() != NexPlayer.NEX_USE_RENDER_JAVA)
					{
						mNexPlayer.setOutputPos(left, top, mVideoWidth, mVideoHeight);

						// if openGL mode and pause state, app have to call request render for redrawing.
						if(UseOpenGL)
							glRenderer.requestRender();
					}
				} 
				else 
				{
					Log.d(TAG, "FULL SCREEN MODE");					
					mFullscreen = true;
					float scale = Math.min((float) mScreenWidth / (float) mVideoWidth, (float) mScreenHeight / (float) mVideoHeight);
					int width = (int) (mVideoWidth * scale);
					int height = (int) (mVideoHeight * scale);
					int top = (mScreenHeight - height) / 2;
					int left = (mScreenWidth - width) / 2;

					Log.d(TAG, "Touch Event - FILLSCREEN : " + left + " " + top + " " + width + " " + height + " ");
					Log.d(TAG, "Surface Width : " + mScreenWidth + " SurfaceHeight : " + mScreenHeight);
					if(mNexPlayer.GetRenderMode() == NexPlayer.NEX_USE_RENDER_HW)
					{						
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
								RelativeLayout.LayoutParams.FILL_PARENT,
								RelativeLayout.LayoutParams.FILL_PARENT);
						params.width = width;
						params.height = height;
						params.topMargin = top;
						params.leftMargin = left;
						mPreviewForHW.setLayoutParams(params);
						//mPreview.setPadding(left, top, mVideoWidth, mVideoHeight);
					}
					else if(mNexPlayer.GetRenderMode() != NexPlayer.NEX_USE_RENDER_JAVA)
					{
						mNexPlayer.setOutputPos(left, top, width, height);
						// if openGL mode and pause state, app have to call request render for redrawing.
						if(UseOpenGL)
							glRenderer.requestRender();
						
					}
				}
			}
		}

		return super.onTouchEvent(event);
	}

	View.OnClickListener mAudioStreamListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PLAY
					|| mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PAUSE) 
			{
				mContentInfo = mNexPlayer.getContentInfo();
				// If content has multi stream, show dialog for selection.
				if (mContentInfo.mStreamNum > 0) 
				{
					removeDialog(DIALOG_SELECT_AUDIOSTREAM);
					showDialog(DIALOG_SELECT_AUDIOSTREAM);
				}
			}
		}
	};
	View.OnClickListener mVideoStreamListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PLAY
					|| mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PAUSE) 
			{
				mContentInfo = mNexPlayer.getContentInfo();
				// If content has multi stream, show dialog for selection.
				if (mContentInfo.mStreamNum > 0) 
				{
					removeDialog(DIALOG_SELECT_VIDEOSTREAM);
					showDialog(DIALOG_SELECT_VIDEOSTREAM);
				}
			}
		}
	};
	
	View.OnClickListener mPausePlayListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			// only play state, call pause.
			if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PLAY) 
			{
				mNexPlayer.pause();
			}
			// only pause state, call resume.
			else if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PAUSE) 
			{
				mNexPlayer.resume();
			}
		}
	};
	
	
	
	View.OnClickListener mRewListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			// nexPlayer didn't provide rewind api. but you can rewind to use seek() api.
			// mPlaying time is current time of playing content.
			if( mPlayingTime < 30000)
				mNexPlayer.seek(0);
			else
				mNexPlayer.seek(mPlayingTime * 1000 - 30000);
		}
	};

	View.OnClickListener mFFListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			// nexPlayer didn't provide rewind api. but you can rewind to use seek() api.
			mNexPlayer.seek(mPlayingTime * 1000 + 30000);
		}
	};

	View.OnClickListener mPauseListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PLAY)
			{
				mNexPlayer.pause();
			}
		}
	};

	View.OnClickListener mStopListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PLAY || 
				mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PAUSE) 
			{
				mNexPlayer.stop();
			}
			else 
			{
				mNexPlayer.close();
				updateUserMessage("Stop");
			}

			updateControlPanelUI(0);
			updateContentInfo("");
		}
	};

	View.OnClickListener mBookmarkListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			// move toward other activity.
			mBookmarkActivityEnabled = true;
			if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PLAY || 
				mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PAUSE) 
			{
				mNexPlayer.stop();
			}

			// delete view for opengl.
			if(UseOpenGL)
			{
				FrameLayout view = (FrameLayout)findViewById(R.id.gl_container);
				view.removeView(glRenderer);
			}
			
			setContentView(R.layout.nexplayer_bookmark);

			Button button;
			button = (Button) findViewById(R.id.buttonOpenURL);
			button.setOnClickListener(mOpenURLListener);

			mAdaptBookmark = new ArrayAdapter<String>(v.getContext(),
					R.layout.nexplayer_urltext);

			((ListView) findViewById(R.id.listURL)).setAdapter(mAdaptBookmark);

			if (mPath.length() > 0) {
				EditText txtURL = (EditText) findViewById(R.id.editTxtURL);
				txtURL.setText(mPath.toString());
			}
			
			
			File currentFolder = null;
			currentFolder = Environment.getExternalStorageDirectory();
			
			File[] bFile = currentFolder.listFiles();

			for( int i = 0 ; i < bFile.length ; i++)
			{
				if( !bFile[i].isDirectory())
				{
					String name = bFile[i].getName().toLowerCase();
					String line;					
					if( name.endsWith(".nx") ||  name.endsWith(".nx.txt")||
						name.endsWith(".nxb") ||  name.endsWith(".nxb.txt"))
					{
						try{
							BufferedReader reader = new BufferedReader(new FileReader(bFile[i]));
							while( (line = reader.readLine())!=null ) 
							{
								String url;
								String title;
								line = line.trim();
								int sp = line.indexOf(" ");
								if( sp < 0 ) {
									url = line;
									title = line;
								} else {
									url = line.substring(0, sp);
									title = line.substring(sp);
								}
								title = title.trim();
								url = url.trim();
								if( title.length() < 1 )
									title = url;
								if( url.length() >= 4 )
								{
									Log.d(TAG,"Bookmark URL:" + url);
									mAdaptBookmark.add(url);
								}
							}
							
						} 
						catch (Exception e) 
						{
							Log.d(TAG,"Error reading bookmarks file.", e);
						}
					}
				}
				
				bFile[i].getName();
			}
			
			((ListView) findViewById(R.id.listURL)).setOnItemClickListener(new AdapterView.OnItemClickListener() 
			{
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
				{
					String strURL = (String) mAdaptBookmark.getItem((int) id);
					if (strURL != null) 
					{
						EditText txtURL = (EditText) findViewById(R.id.editTxtURL);
						txtURL.setText(strURL.toString());
					}
				}
			});
		}
	};
	
	View.OnClickListener mVideoCaptureListener = new OnClickListener() {
		public void onClick(View v) {
			if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PLAY || 
				mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PAUSE) 
			{
				// captureVideo() api can capture video.
				// it called, video data is delivered through onVideoRenderCapture().
				mNexPlayer.captureVideo(1, 0);
			}
		}
	};

	View.OnClickListener mOpenURLListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			EditText txtURL = (EditText) findViewById(R.id.editTxtURL);
			CharSequence pURL = txtURL.getText();
			String strURL = pURL.toString();

			mPath = strURL;

			setContentView(R.layout.nexplayer_sample);

			mPreviewForSW = (SurfaceView) findViewById(R.id.surface);

			mSurfaceHolderForSW = mPreviewForSW.getHolder();
			mSurfaceHolderForSW.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
			mSurfaceHolderForSW.setFormat(mScreenPixelFormat);
			mSurfaceHolderForSW.addCallback(mMP);
			
			mPreviewForHW = (SurfaceView) findViewById(R.id.surface2);

			mSurfaceHolderForHW = mPreviewForHW.getHolder();
			mSurfaceHolderForHW.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
			mSurfaceHolderForHW.setFormat(mScreenPixelFormat);
			mSurfaceHolderForHW.addCallback(mMP);
			
			mPreviewForSW.setVisibility(View.INVISIBLE);
			mPreviewForHW.setVisibility(View.INVISIBLE);
			
			mImageView = (ImageView)findViewById(R.id.imageview);
			mImageView.setVisibility(View.INVISIBLE);
			
			mThumbnailView = (ImageView)findViewById(R.id.thumnailView);
			mThumbnailView.setVisibility(View.INVISIBLE);

			Button button;
			button = (Button) findViewById(R.id.buttonLocal);
			button.setOnClickListener(mLocalTestListener);

			button = (Button) findViewById(R.id.buttonRTSP);
			button.setOnClickListener(mRTSPTestListener);

			button = (Button) findViewById(R.id.buttonDownload);
			button.setOnClickListener(mDownloadTestListener);

			button = (Button) findViewById(R.id.buttonHTTPLive);
			button.setOnClickListener(mHTTPLiveTestListener);

			button = (Button) findViewById(R.id.buttonStop);
			button.setOnClickListener(mStopListener);

			button = (Button) findViewById(R.id.buttonPausePlay);
			button.setOnClickListener(mPausePlayListener);

			button = (Button) findViewById(R.id.buttonRew);
			button.setOnClickListener(mRewListener);

			button = (Button) findViewById(R.id.buttonFF);
			button.setOnClickListener(mFFListener);

			button = (Button) findViewById(R.id.buttonBookmark);
			button.setOnClickListener(mBookmarkListener);
			
			button = (Button) findViewById(R.id.buttonCapture);
			button.setOnClickListener(mVideoCaptureListener);

			button = (Button) findViewById(R.id.buttonAudioStream);
			button.setOnClickListener(mAudioStreamListener);
			button.setEnabled(false);

			button = (Button) findViewById(R.id.buttonVideoStream);
			button.setOnClickListener(mVideoStreamListener);
			button.setEnabled(false);
			
			TextView txtSubtitle = (TextView) findViewById(R.id.subtitle);
			txtSubtitle.setShadowLayer(2, 3, 3, 0);

			CheckBox checkBox = (CheckBox) findViewById(R.id.checkSmoothRender);
			checkBox.setOnCheckedChangeListener(mSmoothRenderListner);

			checkBox = (CheckBox) findViewById(R.id.checkVideoFilter);
			checkBox.setOnCheckedChangeListener(mVideoFilterListner);			
			
			checkBox = (CheckBox) findViewById(R.id.enableTrackDown);			// JDKIM 2011/04/19
			checkBox.setOnCheckedChangeListener(mEnableTrackDownListner);		// JDKIM 2011/04/19
			checkBox.setChecked(mEnableTrackDown);								// JDKIM 2011/04/19

			checkBox = (CheckBox) findViewById(R.id.checkShowContentInfo);
			checkBox.setOnCheckedChangeListener(mShowContentInfoListner);
			
			SeekBar seekbar = (SeekBar)findViewById(R.id.seekBar_BandWidth);
			seekbar.setOnSeekBarChangeListener(mSeekbarListner);
			seekbar.setProgress(100);
			seekbar.setEnabled(true);
			seekbar.setPadding(40, 0, 40, 0);


			if(UseOpenGL)
			{
				// Create GLRenderer.
				int colorDepth = 4;
				glRenderer = new GLRenderer(mContext, mNexPlayer, mGLListener, colorDepth);
				FrameLayout view = (FrameLayout)findViewById(R.id.gl_container);
				view.addView(glRenderer);
			}

			if (strURL.length() == 0) 
			{
				return;
			}
			int nIdx = strURL.indexOf("://");
			if (nIdx >= 0) 
			{
				String strType = new String(strURL.getBytes(), 0, nIdx);
				if (strType.equalsIgnoreCase("rtsp")
					|| strType.equalsIgnoreCase("http")
					|| strType.equalsIgnoreCase("https")
					|| strType.equalsIgnoreCase("mms")) 
				{
					updateUserMessage("Connecting...");
					if (mNexPlayer.open(strURL, null,
										NexPlayer.NEXPLAYER_SOURCE_TYPE_STREAMING,
										NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP, 0) != 0) 
					{
						updateUserMessage("[Error] Can't fopen URL.");
					} 
					else 
					{
						updateControlPanelUI(1);
					}
				} 
				else 
				{
					updateUserMessage("Opening...");
					if (mNexPlayer.open(strURL, null,
										NexPlayer.NEXPLAYER_SOURCE_TYPE_LOCAL_NORMAL,
										NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP, 0) != 0)
					{
						updateUserMessage("[Error] Can't open file.");
					} else {
						updateControlPanelUI(1);
					}
				}
			} 
			else 
			{
				// NexPlayer support subtitle (smi, srt, sub).				
				updateUserMessage("Opening...");
				String strSMI;
				String strTemp;
				int nIndex = strURL.lastIndexOf('.');
				strSMI = strURL.substring(0, nIndex);
				strTemp = strSMI + ".srt";
				File file = new File(strTemp);
				if(file.exists())
				{
					strSMI = strTemp;
				}
				else
				{
					strTemp = strSMI + ".sub";
					file = new File(strTemp);
					if(file.exists())
					{
						strSMI = strTemp;
					}
					else
					{
						strSMI = strSMI + ".smi";
					}
				}

				if (mNexPlayer.open(strURL, strSMI,
						NexPlayer.NEXPLAYER_SOURCE_TYPE_LOCAL_NORMAL,
						NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP, 0) != 0)
				{
					updateUserMessage("[Error] Can't open file.");
				} 
				else 
				{
					updateControlPanelUI(1);
				}
			}
		}
	};

	View.OnClickListener mLocalTestListener = new OnClickListener() {
		public void onClick(View v) {

			String path="/sdcard/2177K.avi";
			
			if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_STOP)
				mNexPlayer.close();

			updateUserMessage("Opening...");
			if (mNexPlayer.open(path, null,
					NexPlayer.NEXPLAYER_SOURCE_TYPE_LOCAL_NORMAL,
					NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP, 0) == 0) // JDKIM
																		// 2010/07/13
			{
				mPath = path;

				updateControlPanelUI(1);
			} else {
				Log.e(TAG, "[Error] Can't Open file " + path);
				updateUserMessage("[Error] Can't Open file.");
			}
		}
	};

	View.OnClickListener mRTSPTestListener = new OnClickListener() {
		public void onClick(View v) {
			String 			path = "rtsp://211.216.53.145/1.3gp";

			if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_STOP)
			{
				mNexPlayer.close();
			}

			updateUserMessage("Connecting...");
			mNexPlayer.open(path, null,
					NexPlayer.NEXPLAYER_SOURCE_TYPE_STREAMING,
					NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP, 0); // JDKIM
																	// 2010/07/13
			mPath = path;

			updateControlPanelUI(1);
		}
	};

	View.OnClickListener mDownloadTestListener = new OnClickListener() {
		public void onClick(View v) {
			String path = "http://220.117.193.94/pd/MileyCyrus.3gp";

			if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_STOP)
				mNexPlayer.close();

			updateUserMessage("Connecting...");

			mNexPlayer.open(path, null,
					NexPlayer.NEXPLAYER_SOURCE_TYPE_STREAMING,
					NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP, 0); // JDKIM
																	// 2010/07/13
			mPath = path;
			updateControlPanelUI(1);
		}
	};
	
	
	View.OnClickListener mHTTPLiveTestListener = new OnClickListener() {
		public void onClick(View v) {
			String 	path="http://www.playon.tv/online/iphone5/main.m3u8";
			
			 if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_STOP)
				mNexPlayer.close();

			updateUserMessage("Connecting...");
			mNexPlayer.open(path, null,
					NexPlayer.NEXPLAYER_SOURCE_TYPE_STREAMING,
					NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP, 0); // JDKIM
																	// 2010/07/13
			mPath = path;

			updateControlPanelUI(1);
		}
	};
	
	// UI Listener for changeMaxBandwidth()
	SeekBar.OnSeekBarChangeListener mSeekbarListner = new OnSeekBarChangeListener()
	{
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) 
		{
			Log.d(TAG, "OnSeekBarChangeListener Seekbar Change. " + progress);			
		}

		public void onStartTrackingTouch(SeekBar seekBar) 
		{
			Log.d(TAG, "OnSeekBarChangeListener onStartTrackingTouch");
		}

		public void onStopTrackingTouch(SeekBar seekBar) 
		{
			int value = seekBar.getProgress();
			Log.d(TAG, "onStopTrackingTouch : " + value + " chnageMaxBandwidth BW : " + (int)(value*102.4/5.0) + "kbps" );

			mNexPlayer.changeMaxBandWidth((int)(value*102.4/5.0));//Range : 0~10mbps
			final String strTemp = "BW : " + (int)(value*102.4/5.0) + "kbps";

			mHandler.post(new Runnable() 
			{
				public void run() 
				{
					try 
					{
						TextView txtProgress = (TextView) findViewById(R.id.BW_Info);
						txtProgress.setText(strTemp);
					}
					catch (Throwable e) 
					{
						e.printStackTrace();
					}
				}
			});
		}
	};

	// set smooth rendering(skipping).
	// if device performance didn't support content, nexPlayer distributes video frame to render.
	CompoundButton.OnCheckedChangeListener mSmoothRenderListner = new OnCheckedChangeListener() 
	{
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
		{
			Log.d(TAG, "Smooth Rendering : " + isChecked);

			if (isChecked) {
				mNexPlayer.setProperty(NexProperty.SUPPORT_SMOOTH_SKIPPING, 1); // Smooth
																				// rendering
			} else {
				mNexPlayer.setProperty(NexProperty.SUPPORT_SMOOTH_SKIPPING, 0); // Smooth
																				// rendering
			}// Smooth
			
		}
	};

	// this option increase quality of video when nexplayer use Android Renderer or JAVA Renderer
	CompoundButton.OnCheckedChangeListener mVideoFilterListner = new OnCheckedChangeListener() 
	{
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
		{
			Log.d(TAG, "Video Filter : " + isChecked);
			mFilterVideo = isChecked;
			
			if(mFrameBitmap==null ) 
			{
				if (mFilterVideo == true)
					mNexPlayer.setRenderOption(NexPlayer.RENDER_MODE_VIDEO_FILTERBITMAP);
				else
					mNexPlayer.setRenderOption(NexPlayer.RENDER_MODE_VIDEO_NONE);
			}
			else
			{
				blitPaint.setFilterBitmap(mFilterVideo);
			}
		}
	};

	// HLS and SmoothStreaming cases, app uses this value.
	// Although network status supports bitrates of track, 
	//if device performance can't support it this property forces to go down track with percentage of rendering video.  
	CompoundButton.OnCheckedChangeListener mEnableTrackDownListner = new OnCheckedChangeListener() 
	{
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
		{
			Log.d(TAG, "EnableTrackDown : " + isChecked);

			mEnableTrackDown = isChecked;
			if(mEnableTrackDown)
			{
				mNexPlayer.setProperty(NexProperty.ENABLE_TRACKDOWN, 1);
				mNexPlayer.setProperty(NexProperty.TRACKDOWN_VIDEO_RATIO, 60); // percentage of rendering video frame 
			}
			else
			{
				mNexPlayer.setProperty(NexProperty.ENABLE_TRACKDOWN, 0);
				
			}
		}
	};
	// JDKIM : end

	CompoundButton.OnCheckedChangeListener mShowContentInfoListner = new OnCheckedChangeListener() 
	{
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
		{
			Log.d(TAG, "Show Content Info : " + isChecked);

			mShowContentInfo = isChecked;
			if (mShowContentInfo == true) 
			{
				String strContentInfo = "";
				String strTmp;

				NexContentInformation info = mNexPlayer.getContentInfo();
				
				strTmp = "  [Video]  Codec:" + info.mVideoCodec + "   W:"
						+ info.mVideoWidth + "   H:" + info.mVideoHeight + "\n";
				strContentInfo += strTmp;
				strTmp = "  [Audio]  Codec:" + info.mAudioCodec + "   SR:"
						+ info.mAudioSamplingRate + "   CN:"
						+ info.mAudioNumOfChannel + "\n";
				strContentInfo += strTmp;
				strTmp = "    AVType:" + info.mMediaType + "   TotalTime:"
						+ info.mMediaDuration + "   Pause:" + info.mIsPausable
						+ "   Seek:" + info.mIsSeekable + "\n";
				strContentInfo += strTmp;

				// find information of id of current audio stream
				for (int i = 0; i < info.mStreamNum; i++)
				{
					if (info.mCurrAudioStreamID == info.mArrStreamInformation[i].mID)
					{
						strTmp 	= "    Audio current Stream ID:" + info.mCurrAudioStreamID
								+ "   Track  ID:" + info.mArrStreamInformation[i].mCurrTrackID
								+ "   AttrID:" + info.mArrStreamInformation[i].mCurrCustomAttrID + "\n";
					}
				}
				strContentInfo += strTmp;

				strTmp = "";
				// find information of id of current video stream
				for (int i = 0; i < info.mStreamNum; i++)
				{
					if (info.mCurrVideoStreamID == info.mArrStreamInformation[i].mID)
					{
						strTmp 	= "    Video current Stream ID:" + info.mCurrVideoStreamID
								+ "   Track  ID:" + info.mArrStreamInformation[i].mCurrTrackID
								+ "   AttrID:" + info.mArrStreamInformation[i].mCurrCustomAttrID + "\n";
					}
				}
				strContentInfo += strTmp;

				strTmp = "";
				// find information of id of current text stream
				for (int i = 0; i < info.mStreamNum; i++)
				{
					if (info.mCurrTextStreamID == info.mArrStreamInformation[i].mID)
					{
						if(info.mArrStreamInformation[i].mName != null && info.mArrStreamInformation[i].mName.getTextData() != null)
						{
							strTmp = "    Text current Stream ID:" + info.mCurrTextStreamID
								+ "   Track  ID:" + info.mArrStreamInformation[i].mCurrTrackID
								+ "   AttrID:" + info.mArrStreamInformation[i].mCurrCustomAttrID + "\n";
						}
					}
				}

				strContentInfo += strTmp;						
				
				for (int i = 0; i < info.mStreamNum; i++) 
				{
					strTmp 	= "       Stream[id:" + info.mArrStreamInformation[i].mID + "]  type:"
							+ info.mArrStreamInformation[i].mType + "\n";
					strContentInfo += strTmp;

					for (int j = 0; j < info.mArrStreamInformation[i].mTrackCount; j++) 
					{
						if(info.mArrStreamInformation[i].mCurrTrackID == info.mArrStreamInformation[i].mArrTrackInformation[j].mTrackID)
						{
							strTmp = "*";
						}
						strTmp 	+= "          Track[id:" + info.mArrStreamInformation[i].mArrTrackInformation[j].mTrackID
								+ "/" + info.mArrStreamInformation[i].mArrTrackInformation[j].mCustomAttribID
								+ "] BW:" + info.mArrStreamInformation[i].mArrTrackInformation[j].mBandWidth
								+ "  Type:" + info.mArrStreamInformation[i].mArrTrackInformation[j].mType + "\n";
						strContentInfo += strTmp;
						strTmp = "";
					}
				}
				updateContentInfo(strContentInfo);
			} 
			else 
			{
				updateContentInfo("");
			}
			
			Log.d(TAG, "Show Content Info END!!");

		}
	};
	
	
	CompoundButton.OnCheckedChangeListener mVMListener = new OnCheckedChangeListener() 
	{
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
		{
			Log.d(TAG, "VM Info : " + isChecked);
			mVM = isChecked;
			
			 // register initVMDRM before open 
			if(mVM)
			{
				String uniqueIdentifier;	
				{
					WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE); 
					WifiInfo wifiInfo = wifiManager.getConnectionInfo(); 

					uniqueIdentifier = wifiInfo.getMacAddress();
					Log.d(TAG, "MAC ADDR : " + uniqueIdentifier);
				}

				int result;
				result = mNexPlayer.initVMDRM("3C:5A:37:01:C6:E3", "dtvhlscsm.idtv.belgacom.net:80", "Verimatrix", "/mnt/sdcard/Android/data/NexPlayer/");
				
				if(result == 0) 
				{
					Log.d(TAG, "SUCCESS on initializing VMDRM...");
				}
				else 
				{
					Log.d(TAG, "ERROR due to library security problem ret : " + result);
				}
			}
			
		}
	};

	
	private void updateControlPanelUI(int nState) // nState (0:Stop, 1:Play)
	{
		Button button;
		
		if(mBookmarkActivityEnabled)
			return;
		
		if (nState == 1) 
		{
			button = (Button) findViewById(R.id.buttonLocal);
			button.setEnabled(false);

			button = (Button) findViewById(R.id.buttonRTSP);
			button.setEnabled(false);

			button = (Button) findViewById(R.id.buttonDownload);
			button.setEnabled(false);

			button = (Button) findViewById(R.id.buttonHTTPLive);
			button.setEnabled(false);

			button = (Button) findViewById(R.id.buttonStop);
			button.setEnabled(true);

			button = (Button) findViewById(R.id.buttonBookmark);
			button.setEnabled(false);

			button = (Button) findViewById(R.id.buttonRew);
			button.setEnabled(true);

			button = (Button) findViewById(R.id.buttonFF);
			button.setEnabled(true);

			button = (Button) findViewById(R.id.buttonPausePlay);
			button.setEnabled(true);
			
			button = (Button) findViewById(R.id.buttonCapture);
			button.setEnabled(true);
		} 
		else 
		{			
			button = (Button) findViewById(R.id.buttonLocal);
			button.setEnabled(true);

			button = (Button) findViewById(R.id.buttonRTSP);
			button.setEnabled(true);

			button = (Button) findViewById(R.id.buttonDownload);
			button.setEnabled(true);

			button = (Button) findViewById(R.id.buttonHTTPLive);
			button.setEnabled(true);

			button = (Button) findViewById(R.id.buttonStop);
			button.setEnabled(false);

			button = (Button) findViewById(R.id.buttonBookmark);
			button.setEnabled(true);

			button = (Button) findViewById(R.id.buttonRew);
			button.setEnabled(false);

			button = (Button) findViewById(R.id.buttonFF);
			button.setEnabled(false);

			button = (Button) findViewById(R.id.buttonPausePlay);
			button.setEnabled(false);

			button = (Button) findViewById(R.id.buttonAudioStream);
			button.setEnabled(false);

			button = (Button) findViewById(R.id.buttonVideoStream);
			button.setEnabled(false);
			
			button = (Button) findViewById(R.id.buttonCapture);
			button.setEnabled(false);
		}
	}

	public void updatePlayingTime(int curr, int bufferedTime) 
	{
		this.mPlayingTime = curr / 1000;	// time unit of nexplayer is millisecond.
		this.mBufferedTime = bufferedTime / 1000; // buffering amount of a current time. 
		mHandler.post(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					TextView txtProgress = (TextView) findViewById(R.id.PlayTime);
					txtProgress.setText(mPlayingTime + ":" + mContentDuration + "(" + mBufferedTime + ")");
					txtProgress = (TextView) findViewById(R.id.content_statistics);
					
					int nFPS = mNexPlayer.getContentInfoInt(NexPlayer.CONTENT_INFO_INDEX_VIDEO_RENDER_AVE_FPS);
					String strMsg;
					strMsg = "AVE Dec : " + nFPS/10.0 + " fps\n";
					txtProgress.setText(strMsg);

				}
				catch (Throwable e) 
				{
					e.printStackTrace();
				}				
			}
		});
	}

	public void updateUserMessage(String strMsg) 
	{
		this.mStrMsg = strMsg;
		mHandler.post(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					TextView txtProgress = (TextView) findViewById(R.id.PlayTime);
					txtProgress.setText(mStrMsg);

				}
				catch (Throwable e) 
				{
					e.printStackTrace();
				}
			}
		});
	}

	public void updateContentInfo(String strMsg) 	
	{
		Log.d(TAG, "updateContentInfo() is called");
		
		this.mStrContentInfo = strMsg;
		mHandler.post(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					TextView txtProgress = (TextView) findViewById(R.id.content_info);
					txtProgress.setText(mStrContentInfo);

				}
				catch (Throwable e) 
				{
					e.printStackTrace();
				}
			}
		});
	}

	// JDKIM : end

	public void updateMediaStreamInfo() 
	{

		mHandler.post(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					Button button;
					mContentInfo = mNexPlayer.getContentInfo();
					if (mContentInfo.mStreamNum > 0) 
					{
						int iAudioCount = 0;
						int iVideoCount = 0;
						for(int i = 0 ; i < mContentInfo.mStreamNum ; i++)
						{
							// count of audio stream
							if( mContentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_AUDIO)
							{
								iAudioCount++;
							}
							else if( mContentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_VIDEO)
							{
								// count of video stream
								// if custom attribute exist, count  custom attributes.
								if(mContentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length > 0)
								{
									Log.d(TAG, "Attribute Count : " + mContentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length);
									for(int j = 0; j <  mContentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length; j++)
										iVideoCount++;
								}
								else
								{
									iVideoCount++;
								}
							}
						}
						Log.d(TAG, "Audio Count(" + iAudioCount + ")" + " Video Count(" + iVideoCount + ")");

						if( iAudioCount > 2)
						{
							button = (Button) findViewById(R.id.buttonAudioStream);
							button.setEnabled(true);
						}
						
						if( iVideoCount > 2)
						{
							button = (Button) findViewById(R.id.buttonVideoStream);
							button.setEnabled(true);
						}
					} 
					else 
					{
						button = (Button) findViewById(R.id.buttonAudioStream);
						button.setEnabled(false);
						button = (Button) findViewById(R.id.buttonVideoStream);
						button.setEnabled(false);
						
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	// SurfaceHolder::Callback
	// if surface size changed, this callback is called.	
	public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w, int h) 
	{

		Log.d(TAG, "surfaceChanged called width : " + w + "   height : " + h);
		
		mSurfaceWidth = w;
		mSurfaceHeight = h;
		mScreenWidth =  mSurfaceWidth;
		mScreenHeight = mSurfaceHeight;
		
		if (mUri != null) 
		{
			Log.d(TAG, "Received URI: " + mUri.toString());
			
			String videoData = null;
			String proj[] = { MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE };

			Cursor videoCursor = managedQuery(mUri, proj, null, null, null);
			if (videoCursor != null && videoCursor.moveToFirst()) 
			{
				int videoDataCol = videoCursor.getColumnIndex(MediaStore.Images.Media.DATA);
				videoData = videoCursor.getString(videoDataCol);
				Log.d(TAG, "Received Content PATH:" + videoData);
				videoCursor.close();
			}
			else 
			{
				videoData = Uri.decode(mUri.toString());
				Log.d(TAG, "Received File PATH:" + videoData);
			}

			String strType = mUri.getScheme();
			String strURL = videoData;

			Log.d(TAG, "[surfaceChanged] scheme=" + strType + "  URL=" + strURL);

			if (strType.equalsIgnoreCase("rtsp") || strType.equalsIgnoreCase("http")) 
			{
				updateUserMessage("Connecting...");
				if (mNexPlayer.open(strURL, null,
					NexPlayer.NEXPLAYER_SOURCE_TYPE_STREAMING,
					NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP, 0) != 0)
				{
					updateUserMessage("[Error] Can't fopen URL.");
				}
			} 
			else 
			{
				if (strType.equalsIgnoreCase("file")) 
				{
					strURL = strURL.substring(7, strURL.length());
				}

				updateUserMessage("Opening...");
				if (mNexPlayer.open(strURL, null,
					NexPlayer.NEXPLAYER_SOURCE_TYPE_LOCAL_NORMAL,
					NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP, 0) != 0)
				{
					updateUserMessage("[Error] Can't open file.");
				}
			}

			mHandler.post(new Runnable() 
			{
				public void run() 
				{
					try 
					{
						updateControlPanelUI(1);
					}
					catch (Throwable e) 
					{
						e.printStackTrace();
					}
				}
			});
		}
		else
		{
			try 
			{
				if (mCreateSurface == true) 
				{
					if (!mFullscreen) 
					{
						int top = (mSurfaceHeight - mVideoHeight) / 2;
						int left = (mSurfaceWidth - mVideoWidth) / 2;

						Log.d(TAG, "ORIGINAL_SIZE : " + left + " " + top + " " + mVideoWidth + " " + mVideoHeight + " ");
						Log.d(TAG, "Surface Width : " + mSurfaceWidth + " SurfaceHeight : " + mSurfaceHeight);

						// if not JAVA Renderer, call setOutputPos().
						if(mNexPlayer.GetRenderMode() != NexPlayer.NEX_USE_RENDER_JAVA)
						{
							mNexPlayer.setOutputPos(left, top, mVideoWidth, mVideoHeight);
							// if openGL mode and pause state, app have to call request render for redrawing.
							if(UseOpenGL)
								glRenderer.requestRender();
						}
					} 
					else 
					{
						float scale = Math.min((float) mSurfaceWidth
								/ (float) mVideoWidth, (float) mSurfaceHeight
								/ (float) mVideoHeight);
						int newWidth = (int) (mVideoWidth * scale);
						int newHeight = (int) (mVideoHeight * scale);
						int top = (mSurfaceHeight - mVideoHeight) / 2;
						int left = (mSurfaceWidth - mVideoWidth) / 2;

						Log.d(TAG, "FILLSCREEN : " + left + " " + top + " " + newWidth
								+ " " + newHeight + " ");
						
						if(mNexPlayer.GetRenderMode() != NexPlayer.NEX_USE_RENDER_JAVA)
						{
							mNexPlayer.setOutputPos(left, top, newWidth, newHeight);
						// if openGL mode and pause state, app have to call request render for redrawing.

							if(UseOpenGL)
								glRenderer.requestRender();
						}
					}
				}
			} 
			catch (Throwable e) 
			{
				e.printStackTrace();
			}
		}
	}

	public void surfaceDestroyed(SurfaceHolder surfaceholder) 
	{
		Log.d(TAG, "surfaceDestroyed called");
		int rendermode = mNexPlayer.GetRenderMode();
		if(rendermode != NexPlayer.NEX_USE_RENDER_JAVA)//ignore when java renderer mode
			mCreateSurface = false;
	}

	public void surfaceCreated(SurfaceHolder holder) 
	{
		Log.d(TAG, "surfaceCreated called");
		Log.d(TAG, "mCreateSurface set to true.");
		
		mCreateSurface = true;		
		
		Rect rect = holder.getSurfaceFrame(); // get region of surface.
		mScreenWidth = rect.width();
		mScreenHeight = rect.height();
		
		Log.d(TAG, "Screen size - W : " + mScreenWidth + " H : " + mScreenHeight);

		// mPlayStarted is true after onVideoRenderCreate()
		if (mPlayStarted == true) 
		{
			Log.d(TAG, "Surface register again!");
			// JAVA Renderer and  openGL Renderer don't need register surface to nexPlayer.
			if(mNexPlayer.GetRenderMode() != NexPlayer.NEX_USE_RENDER_JAVA && mNexPlayer.GetRenderMode() != NexPlayer.NEX_USE_RENDER_OPENGL)
			{
				if( mNexPlayer.GetRenderMode() ==NexPlayer.NEX_USE_RENDER_HW)
				{
					mNexPlayer.setDisplay(mSurfaceHolderForHW, 0);
				}
				else
				{
					mNexPlayer.setDisplay(mSurfaceHolderForSW, 0);
				}
			}
		}
	}

	// NexPlayer::IListener implementation
	// onEndofContent is called when content meets end.
	public void onEndOfContent(NexPlayer mp) 
	{
		mNexPlayer.stop();
		mHandler.post(new Runnable() 
		{
			public void run() 
			{
				updateControlPanelUI(0);
				updateContentInfo("");
			}
		});
	}

	// onStartVideoTask is called when VideoTask is created.
	public void onStartVideoTask(NexPlayer mp) 
	{
		Log.d(TAG, "onStartVideoTask called");
	}
	
	// onStartAudioTask is called when AudioTask is created.
	public void onStartAudioTask(NexPlayer mp) 
	{
		Log.d(TAG, "void onStartAudioTask called");
	}

	// onTime is called periodically during playing. 
	public void onTime(NexPlayer mp, int sec) 
	{
		int nBuffSec = mp.getBufferStatus();
		Log.d(TAG, "onTime called (" + sec + " msec)");

		this.updatePlayingTime(sec, nBuffSec);		
	}

	// onError is called when error is generated.
	public void onError(NexPlayer mp, NexPlayer.NexErrorCode errorCode) 
	{
		if (errorCode == null) 
		{
			Log.d(TAG, "onError: Unknown");
		}
		else 
		{
			Log.d(TAG, "onError: 0x" 
					+ Integer.toHexString(errorCode.getIntegerCode()) + " ("
					+ errorCode.getCategory() + "/" + errorCode.name() + ")");
		}

		mHandler.post(new Runnable() 
		{
			public void run() 
			{
				int state = mNexPlayer.getState();
				if (state == NexPlayer.NEXPLAYER_STATE_PLAY
					|| state == NexPlayer.NEXPLAYER_STATE_PAUSE) 
				{
					mNexPlayer.stop();
				}
				else 
				{
					mNexPlayer.close();
				}
				updateControlPanelUI(0);
			}
		});
	}

	// this is called when nexPlayer change state.
	public void onStateChanged(NexPlayer mp, int pre, int now) 
	{
		Log.d(TAG, "onStateChanged called (" + pre + "->" + now + ")");
	}
	
	// this is called when nexPlayer's signal status is changed. 
	public void onSignalStatusChanged(NexPlayer mp, int pre, int now) 
	{
		Log.d(TAG, "onSignalStatusChanged called before: " + pre + ", after : " + now);
	}

	// not support to record now
	public void onRecordingErr(NexPlayer mp, int err) 
	{
		Log.d(TAG, "onRecordingErr called " + err);
	}
	// not support to record now
	public void onRecording(NexPlayer mp, int recDuration, int recSize) 
	{
		Log.d(TAG, "onRecording called Duratoin: " + recDuration + ", Size: "
				+ recSize);
	}
	// not support to record now
	public void onRecordingEnd(NexPlayer mp, int success) 
	{
		Log.d(TAG, "onRecordingEnd called " + success);
	}
	// not support timeshift now
	public void onTimeshiftErr(NexPlayer mp, int err) 
	{
		Log.d(TAG, "onTimeshiftErr called " + err);
	}
	// not support timeshift now
	public void onTimeshift(NexPlayer mp, int currTime, int TotalTime) 
	{
		Log.d(TAG, "onTimeshift called curTime: " + currTime + ", TotalTime: "
				+ TotalTime);
	}

	// this is called when audio renderer is created.
	// HLS, SmoothStreaming cases, this is called several times.
	public void onAudioRenderCreate(NexPlayer mp, int samplingRate, int channelNum) 
	{
		Log.d(TAG, "onAudioRenderCreate called (SamplingRate:" + samplingRate + " ChannelNum : " + channelNum);
	}

	// this is called when audio renderer is deleted.
	// HLS, SmoothStreaming cases, this is called several times.
	public void onAudioRenderDelete(NexPlayer mp) 
	{
		Log.d(TAG, "mAudioTrack.release() Done");
	}

	// this is called when video renderer is created.
	// HLS, SmoothStreaming cases, this is called several times.
	public void onVideoRenderCreate(NexPlayer mp, int width, int height, Object rgbBuffer) 
	{
		Log.d(TAG, "onVideoRenderCreate called ( Width:" + width + " Height : " + height + ")");
		int nRenderMode = mNexPlayer.GetRenderMode();
		mVideoWidth = width;
		mVideoHeight = height;

		// All renderer must not be used with other renderer.
		// if not JAVA Renderer and OpenGL Renderer, app must register surface to nexplayer.
		if(nRenderMode != NexPlayer.NEX_USE_RENDER_JAVA && nRenderMode != NexPlayer.NEX_USE_RENDER_OPENGL)
		{
			try 
			{
				// app must wait until suface is created because nexplayer didn't render without surface.
				while (mCreateSurface == false) 
				{
					Log.d(TAG, "WAIT for surface creation!");
					Thread.sleep(10);
				}
				if( mNexPlayer.GetRenderMode() ==NexPlayer.NEX_USE_RENDER_HW)
				{
					mNexPlayer.setDisplay(mSurfaceHolderForHW, 0);
				}
				else
				{
					mNexPlayer.setDisplay(mSurfaceHolderForSW, 0);
				}				
	
			} 
			catch (InterruptedException e) 
			{
				Log.d(TAG, "ERROR THREAD! VideoRenderCreate.");
			}
		}
		
		// OpenGL renderer case, app has to wait until initializing GL Renderer.
		if(nRenderMode == NexPlayer.NEX_USE_RENDER_OPENGL)
		{
			try 
			{
				while (mInitGLRenderer == false) 
				{
					Log.d(TAG, "WAIT for GLRenderer Initialization!");
					Thread.sleep(10);
				}
			} 
			catch (InterruptedException e) 
			{
				Log.d(TAG, "ERROR THREAD! VideoRenderCreate.");
			}
		}

		// if this is called first 
		if(mFirstVideoRenderCreate == false)
		{	
			mFirstVideoRenderCreate = true;			
			mHandler.post(new Runnable() 
			{
				public void run() 
				{
					int renderMode = mNexPlayer.GetRenderMode();
					int top = (mSurfaceHeight - mVideoHeight) / 2;
					int left = (mSurfaceWidth - mVideoWidth) / 2;
					
					Log.d(TAG, "VideoRender Created : " + left + " " + top + " " + mVideoWidth + " " + mVideoHeight + " ");
					Log.d(TAG, "Surface Width : " + mSurfaceWidth + " SurfaceHeight : " + mSurfaceHeight);

					// change screen size.
					if(mNexPlayer.GetRenderMode() == NexPlayer.NEX_USE_RENDER_HW)
					{						
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
								RelativeLayout.LayoutParams.FILL_PARENT,
								RelativeLayout.LayoutParams.FILL_PARENT);
								
						params.width = mVideoWidth;
						params.height = mVideoHeight;
						params.topMargin = top;
						params.leftMargin = left;
						mPreviewForHW.setLayoutParams(params);
						//mPreview.setPadding(left, top, mVideoWidth, mVideoHeight);
					}
					else if(renderMode != NexPlayer.NEX_USE_RENDER_JAVA)
					{
						mNexPlayer.setOutputPos(left, top, mVideoWidth, mVideoHeight);
					}
				}
			});
		}

		// set rendering option
		if(	nRenderMode == NexPlayer.NEX_USE_RENDER_AND )			
		{
			if (mFilterVideo == true)
				mNexPlayer.setRenderOption(NexPlayer.RENDER_MODE_VIDEO_FILTERBITMAP);
			else
				mNexPlayer.setRenderOption(NexPlayer.RENDER_MODE_VIDEO_NONE);
		}
		else if (nRenderMode == NexPlayer.NEX_USE_RENDER_JAVA )
		{
			// set pixel format.
			if(this.mScreenPixelFormat == PixelFormat.RGBA_8888)
	    	{
				mFrameBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888 );
	    	}
			else
			{
				mFrameBitmap = Bitmap.createBitmap(width, height, Config.RGB_565 );
			}
			blitPaint = new Paint();
			mNexPlayer.SetBitmap(mFrameBitmap);
			clearCanvas();
		}
		
		mPlayStarted = true;

	}

	// this is called when video renderer is deleted.
	public void onVideoRenderDelete(NexPlayer mp) 
	{
		Log.d(TAG, "onVideoRenderDelete called");
		mPlayStarted = false;
	}

	// this is called after app call captureVideo() API.
	// rgbBuffer is byte buffer. if pixelbyte is 2 data is RGB5565 and if pixelbyte is 4 data is RGB 8888 
	public void onVideoRenderCapture(NexPlayer mp, int width, int height, int pixelbyte, Object rgbBuffer) 
	{
		Log.d(TAG, "onVideoRenderCapture called");
		
		final Bitmap thumbnailBitmap;		
		Bitmap bitmap = Bitmap.createBitmap(width, height, pixelbyte == 2 ? Config.RGB_565 : Config.ARGB_8888);
		ByteBuffer RGBBuffer = (ByteBuffer) rgbBuffer;
		
		if (RGBBuffer.capacity() > 0) 
		{
			RGBBuffer.asIntBuffer();
			bitmap.copyPixelsFromBuffer(RGBBuffer);
			
			if (bitmap != null) 
			{
				thumbnailBitmap = Bitmap.createScaledBitmap(
						(Bitmap) bitmap, 100, 75, true);
				
				mHandler.post(new Runnable() 
				{
					public void run() 
					{
						mThumbnailView = (ImageView)findViewById(R.id.thumnailView);
						mThumbnailView.setImageBitmap(thumbnailBitmap);
						mThumbnailView.setEnabled(true);
						mThumbnailView.setVisibility(View.VISIBLE);
						mThumbnailView.requestLayout();
					}
				});
				
				Timer timer = new Timer();
				timer.schedule(new TimerTask() 
				{
					public void run() 
					{
						mHandler.post(new Runnable() 
						{
							public void run() 
							{
								mThumbnailView.setVisibility(View.INVISIBLE);
							}
						});
					}
				}, 5000);
			}			
		} 
		else 
		{
			if(mBoolIsSetDisplayed == true)
			mHandler.post(new Runnable() 
			{
				public void run() 
				{
					clearCanvas();
					mThumbnailView.setVisibility(View.VISIBLE);
					mThumbnailView.requestLayout();
				}
			});
		}
	}
	
	// this is called during nexplayer draws video.
	// JAVA Renderer and OpenGL renderer uses this callback.
	public void onVideoRenderRender(NexPlayer mp) 
	{
		if(UseOpenGL)
		{
			glRenderer.requestRender();
		}
		
		// RENDER_JAVA
		if(mFrameBitmap!=null ) 
		{
			//Log.d(TAG, "Trying");
			Canvas canvas = mSurfaceHolderForSW.lockCanvas();
			//Log.d(TAG, "LockCanvas");
			
			if(canvas != null)
			{
				// JAVA Renderer has to clear two times.
				if( mClearReq>0 ) 
				{
					Log.d(TAG, "ClearCanvas");
					mClearReq--;
					solidPaint.setColor(0xFF000000);
					canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), solidPaint); 
				}
				
				Rect rctSrc = new Rect(0, 0, mFrameBitmap.getWidth(), mFrameBitmap.getHeight());
				
				canvas.drawColor(Color.BLACK);	// SWSEO 2010/09/30
				
				Rect rctDst;
				if(mFullscreen == false)
				{
					int top = (canvas.getHeight()-mFrameBitmap.getHeight())/2;
					int left = (canvas.getWidth()-mFrameBitmap.getWidth())/2;
					rctDst = new Rect(left, top, left+mFrameBitmap.getWidth(), top+mFrameBitmap.getHeight());
				}
				else
				{
					rctDst = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
				}
				
				canvas.drawBitmap(mFrameBitmap, rctSrc, rctDst, blitPaint);//solidPaint);
				mSurfaceHolderForSW.unlockCanvasAndPost(canvas);
			}
		}
	}

	// this is called when nexplayer has subtitle(caption)and text renderer is created.
	public void onTextRenderInit(NexPlayer mp, int classNum) 
	{
		Log.d(TAG, "onTextRenderInit ClassNum : " + classNum );
	}

	// this is called during playing.
	public void onTextRenderRender(NexPlayer mp, int classIndex, Object textInfo) 
	{
		NexID3TagText tagTextInfo = (NexID3TagText)textInfo;
		String strText = null;

		try {
			if( tagTextInfo.getTextData() == null)
			{
				strText = "";
			}
			else
			{
				// need to change encoding type of string.
				strText = new String( tagTextInfo.getTextData(), 0, tagTextInfo.getTextData().length, "EUC-KR");
				Log.d(TAG, "SMI : encoding :"+ tagTextInfo.getEncodingType() + "cap:" + strText);
			}			
		} 
		catch (UnsupportedEncodingException e) 
		{
			Log.d(TAG, "SMI : UnsupportedEncodingException");

			e.printStackTrace();
		}
		final String strSub = strText;

		mHandler.post(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					TextView txtSubtitle = (TextView) findViewById(R.id.subtitle);
					txtSubtitle.setText(strSub);

				}
				catch (Throwable e) 
				{
					e.printStackTrace();
				}
			}
		});
	}

	// JDKIM : end

	// this is called after calling open(), start(),pause(), resume(), seek(), stop() and close().
	// you need pair each api. you have to call close() after you call open().
	// you have to call start() after you call stop().
	// and app have to call with flow.
	public void onAsyncCmdComplete(NexPlayer mp, int command, int result, int param1, int param2) {
		int duration;
		Log.d(TAG, "onAsyncCmdComplete playerID: " + mp + ", called " + command
				+ " " + result);
		switch (command) 
		{
		case NexPlayer.NEXPLAYER_ASYNC_CMD_OPEN_LOCAL:
		case NexPlayer.NEXPLAYER_ASYNC_CMD_OPEN_STREAMING: 
		{
			Log.d(TAG, "onAsyncCmdComplete called mp: " + mp + " cmd: "
					+ command + " result: " + result);

			// success case.
			if (result == 0) 
			{
				// running time.
				duration = mNexPlayer.getContentInfoInt(NexPlayer.CONTENT_INFO_INDEX_MEDIA_DURATION);
				Log.d(TAG, "Content Info duration is" + duration);
				
				try{
					mHandler.post(new Runnable(){
						public void run()
						{
							// seperate H/W Renderer and S/W Renderer if app use both of them.
							if( mNexPlayer.GetRenderMode() == NexPlayer.NEX_USE_RENDER_HW)
							{
								mPreviewForHW.setVisibility(View.VISIBLE);
								mPreviewForSW.setVisibility(View.INVISIBLE);
							}
							else
							{
								mPreviewForSW.setVisibility(View.VISIBLE);
								mPreviewForHW.setVisibility(View.INVISIBLE);					
							}
						}
					});
				}catch(Exception e)
				{
					e.printStackTrace();
				}

				Log.d(TAG, "Content Info duration is" + duration);
				mNexPlayer.start(0);
				printContentInfomation();
			} else {
				updateUserMessage("[Error] Can't Open Content.");

				onError(mp, NexErrorCode.fromIntegerValue(result));
			}
					
			mHandler.post(new Runnable() 
			{
				public void run() 
				{
					//Content Information.
					mContentInfo = mNexPlayer.getContentInfo();
					try 
					{
						if (mContentInfo.mID3Tag != null && mContentInfo.mMediaType == 1)
						{
							NexID3TagText text = null;
							String strInfo = "";
							String str = "";

							text = mContentInfo.mID3Tag.getArtist();
							str = new String(text.getTextData(), 0, text.getTextData().length, "EUC-KR");
							strInfo += str + "\n";
							
							text = mContentInfo.mID3Tag.getTitle();
							str = new String(text.getTextData(), 0, text.getTextData().length, "EUC-KR");
							strInfo += str + "\n";
							
							text = mContentInfo.mID3Tag.getAlbum();
							str = new String(text.getTextData(), 0, text.getTextData().length, "EUC-KR");
							strInfo += str + "\n";
							TextView txtSubtitle = (TextView) findViewById(R.id.subtitle);
							txtSubtitle.setText(strInfo);
						}									
						
						// mp3 can includes image.
						NexID3TagPicture picture = null;
						if (mContentInfo.mID3Tag != null)
						{
							picture = mContentInfo.mID3Tag.getPicture();
							
							Log.d( TAG,	"TEST PICTURE Size :" + picture.getPictureData().length);

							Bitmap bm = BitmapFactory.decodeByteArray(picture.getPictureData(),0, picture.getPictureData().length);
							mImageView.setImageBitmap(bm);
							mImageView.setVisibility(View.VISIBLE);

						}
					} catch (Throwable e) {
						Log.d( TAG,	"ID3TAGInformation Error");
						e.printStackTrace();
					}
					
				}
			});
		}
			break;
		case NexPlayer.NEXPLAYER_ASYNC_CMD_START_LOCAL:
		case NexPlayer.NEXPLAYER_ASYNC_CMD_START_STREAMING:
			if (result != 0) 
			{
				Log.d(TAG, "onAsyncCmdComplete Start Fail : " + result);
				onError(mp, NexErrorCode.fromIntegerValue(result));
			}
			break;
		case NexPlayer.NEXPLAYER_ASYNC_CMD_STOP:
			Log.d(TAG, "[MAIN] onAsyncCmdComplete STOP. mp:" + mp);
			
			clearCanvas();
			
			mFirstVideoRenderCreate = false;
			mHandler.post(new Runnable() 
			{
				public void run() 
				{
					try {
						
						Log.d(TAG, "[MAIN] run NexPlayerClose()");
						mNexPlayer.close();
						mImageView.setVisibility(View.INVISIBLE);
						mThumbnailView.setVisibility(View.INVISIBLE);
						
						TextView txtSubtitle = (TextView) findViewById(R.id.subtitle);
						txtSubtitle.setText("");	
						updateControlPanelUI(0);

					}
					catch (Throwable e) 
					{
						e.printStackTrace();
					}
				}
			});

			updateUserMessage("Stop");

			break;

		case NexPlayer.NEXPLAYER_ASYNC_CMD_SEEK:
			break;

		case NexPlayer.NEXPLAYER_ASYNC_CMD_TIMESHIFT_CREATE:
			Log.d(TAG, "[MAIN] onAsyncCmdComplete Timeshift start:" + result);
			mNexPlayer.timePause();
			break;
		case NexPlayer.NEXPLAYER_ASYNC_CMD_TIMESHIFT_DESTROY:
			Log.d(TAG, "[MAIN] onAsyncCmdComplete Timeshift Destroy:" + result);
			mNexPlayer.resume();
			break;
		case NexPlayer.NEXPLAYER_ASYNC_CMD_TIMESHIFT_FORWARD:
			Log.d(TAG, "[MAIN] onAsyncCmdComplete Timeshift Forward:" + result);
			mNexPlayer.timeResume();
			mNexPlayer.resume();
			break;
		case NexPlayer.NEXPLAYER_ASYNC_CMD_TIMESHIFT_BACKWARD:
			Log
					.d(TAG, "[MAIN] onAsyncCmdComplete Timeshift Backward:"
							+ result);
			mNexPlayer.timeResume();
			mNexPlayer.resume();
			break;

		case NexPlayer.NEXPLAYER_ASYNC_CMD_RECORD_START:
			Log.d(TAG, "[MAIN] onAsyncCmdComplete RecordingStart :" + result);
			break;
		}
	}

	public void onRTSPCommandTimeOut(NexPlayer mp) {
		if (mp.getState() == NexPlayer.NEXPLAYER_STATE_PLAY) {
			mp.stop();
		}
	}

	public void onPauseSupervisionTimeOut(NexPlayer mp) {
		if (mp.getState() == NexPlayer.NEXPLAYER_STATE_PLAY) {
			mp.stop();
		}
	}

	public void onDataInactivityTimeOut(NexPlayer mp) {
		if (mp.getState() == NexPlayer.NEXPLAYER_STATE_PLAY) {
			mp.stop();
		}
	}

	// this is called when nexplayer enters buffering state.
	public void onBufferingBegin(NexPlayer mp) {
		Log.d(TAG, "Buffering begin");
		updateUserMessage("Buffering 0 %");
	}
	// this is called after nexplayer comes out form buffering state.
	public void onBufferingEnd(NexPlayer mp) {
		Log.d(TAG, "Buffering end");
		updateUserMessage("Buffering 100 %");
	}

	// this is called periodically during buffering 
	public void onBuffering(NexPlayer mp, int progress_in_percent) {
		Log.d(TAG, "Buffering " + progress_in_percent + " %");
		updateUserMessage("Buffering " + progress_in_percent + " %");
	}

	// nexPlayer have some change, this is called
	// for example, track change, DSI Change,...
	public void onStatusReport(NexPlayer mp, int msg, int param1) {
		Log.d(TAG, "onStatusReport  msg:" + msg + "  param1:" + param1);
		
		mStatusReportMessage = msg;
		mStatusReportParam1 = param1;
		
		mHandler.post(new Runnable() {
			public void run() 
			{
				if (mStatusReportMessage == NexPlayer.NEXPLAYER_STATUS_REPORT_CONTENT_INFO_UPDATED) {
					NexContentInformation info = mNexPlayer.getContentInfo();
				
					if (mShowContentInfo) {
						Log.d(TAG, "Content Info updated. ");
		
						String strContentInfo = "";
						String strTmp;
		
						strTmp = "  [Video]  Codec:" + info.mVideoCodec + "   W:"
								+ info.mVideoWidth + "   H:" + info.mVideoHeight + "\n";
						strContentInfo += strTmp;
		
						strTmp = "  [Audio]  Codec:" + info.mAudioCodec + "   SR:"
								+ info.mAudioSamplingRate + "   CN:"
								+ info.mAudioNumOfChannel + "\n";
						strContentInfo += strTmp;
						
						strTmp = "    AVType:" + info.mMediaType + "   TotalTime:"
								+ info.mMediaDuration + "   Pause:" + info.mIsPausable
								+ "   Seek:" + info.mIsSeekable + "\n";
						strContentInfo += strTmp;
		
						strTmp = "    Stream Num:" +  info.mStreamNum + "\n";
						strContentInfo += strTmp;
						
						for(int i = 0 ; i < info.mStreamNum; i++)
						{
							if( info.mCurrAudioStreamID ==  info.mArrStreamInformation[i].mID)
							{
								strTmp = "    Audio current Stream ID:"+ info.mCurrAudioStreamID + "   Track  ID:" + info.mArrStreamInformation[i].mCurrTrackID + "   AttrID:" + info.mArrStreamInformation[i].mCurrCustomAttrID+ "\n";
								strContentInfo += strTmp;
							}
						}
						
						for(int i = 0 ; i < info.mStreamNum; i++)
						{
							if( info.mCurrVideoStreamID ==  info.mArrStreamInformation[i].mID)
							{
								strTmp = "    Video current Stream ID:" + info.mCurrVideoStreamID + "   Track  ID:" + info.mArrStreamInformation[i].mCurrTrackID + "   AttrID:" + info.mArrStreamInformation[i].mCurrCustomAttrID+ "\n";
								strContentInfo += strTmp;
							}
						}
						
						for (int i = 0; i < info.mStreamNum; i++) 
						{
							strTmp = "    Stream [" + i + "]: ID: "
									+ info.mArrStreamInformation[i].mID + " Name : "
									+ getStringData(info.mArrStreamInformation[i].mName) + "Lang:"
									+ getStringData(info.mArrStreamInformation[i].mLanguage)
									+ " Type:" + info.mArrStreamInformation[i].mType
									+ "\n";
							strContentInfo += strTmp;
		
							for (int j = 0; j < info.mArrStreamInformation[i].mAttrCount; j++) {
								strTmp = "        Attr ["
										+ j
										+ "]: ID: "
										+ info.mArrStreamInformation[i].mArrCustomAttribInformation[j].mID
										+ "  "
										+ getStringData(info.mArrStreamInformation[i].mArrCustomAttribInformation[j].mName)
										+ ":"
										+ getStringData(info.mArrStreamInformation[i].mArrCustomAttribInformation[j].mValue)
										+ "\n";
								strContentInfo += strTmp;
							}
		
							for (int k = 0; k < info.mArrStreamInformation[i].mTrackCount; k++) {
								strTmp = "        Track ["
										+ k
										+ "]: ID: "
										+ info.mArrStreamInformation[i].mArrTrackInformation[k].mTrackID
										+ "/"
										+ info.mArrStreamInformation[i].mArrTrackInformation[k].mCustomAttribID
										+ " BW : "
										+ info.mArrStreamInformation[i].mArrTrackInformation[k].mBandWidth
										+ " Type : "
										+ info.mArrStreamInformation[i].mArrTrackInformation[k].mType
										+ "\n";
								strContentInfo += strTmp;
							}
						}
						updateContentInfo(strContentInfo);
					}
					updateMediaStreamInfo();
					
				} 
				else if (mStatusReportMessage == NexPlayer.NEXPLAYER_STATUS_REPORT_TRACK_CHANGED) 
				{
					Log.d(TAG, "Track changed to " + mStatusReportParam1);
				}
				else if( mStatusReportMessage == NexPlayer.NEXPLAYER_STATUS_REPORT_HTTP_INVALID_RESPONSE)
				{
					Log.d(TAG, "HTTP Invalid Response " + mStatusReportParam1);
				}
			}
		});

	}

	// JDKIM : end

	// JDKIM 2010/11/02
	public void onDebugInfo(NexPlayer mp, int msg, String strDbg) {
		Log.d(TAG, "onDebugInfo  msg:" + msg + "  param1:" + strDbg);
		// this.mStrContentInfo = strMsg;
		final String strMsg = strDbg;
		mHandler.post(new Runnable() {
			public void run() {
				try {
					TextView txtProgress = (TextView) findViewById(R.id.content_statistics);
					txtProgress.setText(strMsg);

				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	// JDKIM : end
	public void printContentInfomation() {
		NexContentInformation info;
		info = mNexPlayer.getContentInfo();

		Log.d(TAG,
				"------------------- CONTENTS INFORMATION -------------------");
		Log.d(TAG, "MEDIA TYPE				: " + info.mMediaType);
		Log.d(TAG, "MEDIA DURATION			: " + info.mMediaDuration);
		Log.d(TAG, "VIDEO CODEC				: " + info.mVideoCodec);
		Log.d(TAG, "VIDEO WIDTH				: " + info.mVideoWidth);
		Log.d(TAG, "VIDEO HEIGHT			: " + info.mVideoHeight);
		Log.d(TAG, "VIDEO FRAMERATE			: " + info.mVideoFrameRate);
		Log.d(TAG, "VIDEO BITRATE			: " + info.mVideoBitRate);
		Log.d(TAG, "AUDIO CODEC				: " + info.mAudioCodec);
		Log.d(TAG, "AUDIO SAMPLINGRATE		: " + info.mAudioSamplingRate);
		Log.d(TAG, "AUDIO NUMOFCHANNEL		: " + info.mAudioNumOfChannel);
		Log.d(TAG, "AUDIO BITRATE			: " + info.mAudioBitRate);
		Log.d(TAG, "MEDIA IS SEEKABLE		: " + info.mIsSeekable);
		Log.d(TAG, "MEDIA IS PAUSABLE		: " + info.mIsPausable);
		Log.d(TAG,
				"------------------------------------------------------------");

		mContentDuration = info.mMediaDuration / 1000;
	}

	private void clearCanvas() {
		Paint clsPaint = new Paint();
		
		if(!UseOpenGL)
		{
			if( mNexPlayer.GetRenderMode() != NexPlayer.NEX_USE_RENDER_HW)
			{
				Canvas canvas = mSurfaceHolderForSW.lockCanvas();
				if (canvas != null) {
					Rect rctDst = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
					clsPaint.setColor(Color.BLACK);
					canvas.drawRect(rctDst, clsPaint);
					mSurfaceHolderForSW.unlockCanvasAndPost(canvas);
				}
				canvas = mSurfaceHolderForSW.lockCanvas();
				if (canvas != null) {
					Rect rctDst = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
					clsPaint.setColor(Color.BLACK);
					canvas.drawRect(rctDst, clsPaint);
					mSurfaceHolderForSW.unlockCanvasAndPost(canvas);
				}
			}
		}
		else
		{
			glRenderer.mClearScreen = true;
			glRenderer.requestRender();
		}
	}
	
	// JDKIM 2011/04/18
	private String getStringData(NexID3TagText id3Text)
	{
		String strData = "(null)";
		
		if(id3Text != null) {
			try {
				strData = new String(id3Text.getTextData(), 0, id3Text.getTextData().length, "EUC-KR");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return strData;
	}
	// JDKIM : end 
	
	// If it use GLRenderer and surface of GL renderer is changed, this is called.
	public void onGLChangeSurfaceSize(int width, int height)
	{
		mSurfaceWidth = width;
		mSurfaceHeight = height;
		mInitGLRenderer = true;
		
		Log.d(TAG, "GLsurfaceChanged called width : " + width + "   height : " + height);
		
		if (!mFullscreen)
		{
			Log.d(TAG, "NON-FULL SCREEN MODE");
			int top = (mSurfaceHeight - mVideoHeight) / 2;
			int left = (mSurfaceWidth - mVideoWidth) / 2;
			Log.d(TAG, "GLSurface - ORIGINAL_SIZE : " + left + " " + top + " " + mVideoWidth + " " + mVideoHeight + " ");
			Log.d(TAG, "Surface Width : " + mSurfaceWidth + " SurfaceHeight : " + mSurfaceHeight);

			mNexPlayer.setOutputPos(left, top, mVideoWidth, mVideoHeight);
			// if openGL mode and pause state, app have to call request render for redrawing.
			if(UseOpenGL)
				glRenderer.requestRender();
		} 
		else 
		{
			Log.d(TAG, "FULL SCREEN MODE");
			float scale = Math.min((float) mSurfaceWidth / (float) mVideoWidth, (float) mSurfaceHeight / (float) mVideoHeight);
			int w = (int) (mVideoWidth * scale);
			int h = (int) (mVideoHeight * scale);
			int top = (mSurfaceHeight - h) / 2;
			int left = (mSurfaceWidth - w) / 2;

			Log.d(TAG, "GLSurface - FILLSCREEN : " + left + " " + top + " " + w + " " + h + " ");
			Log.d(TAG, "Surface Width : " + mSurfaceWidth + " SurfaceHeight : " + mSurfaceHeight);
			mNexPlayer.setOutputPos(left, top, w, h);
			// if openGL mode and pause state, app have to call request render for redrawing.
			if(UseOpenGL)
				glRenderer.requestRender();
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyDown() is called");
		
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		
		return super.onKeyDown(keyCode, event);
	}
}

