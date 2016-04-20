package com.nextreaming.nexplayerengine;


import java.io.File;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

/**
 * The primary interface to the NexPlayer&trade;&nbsp; engine.  
 * For details on usage, see the 
 * {@linkplain com.nextreaming.nexplayerengine package documentation}.
 * 
 * @author Nextreaming Corporation
 * @version 1.0
 * 
 */

public final class NexPlayer
{
    // NOTE: Unused constants have been disabled to suppress warnings; these can
    //       be re-enabled later.
    
    // Event Definitions
    private static final int NEXPLAYER_EVENT_NOP                        = 0; // interface test message
    private static final int NEXPLAYER_EVENT_COMMON_BASEID              = 0x00010000;
    private static final int NEXPLAYER_EVENT_ENDOFCONTENT               = ( NEXPLAYER_EVENT_COMMON_BASEID + 1 );
    private static final int NEXPLAYER_EVENT_STARTVIDEOTASK             = ( NEXPLAYER_EVENT_COMMON_BASEID + 2 );
    private static final int NEXPLAYER_EVENT_STARTAUDIOTASK             = ( NEXPLAYER_EVENT_COMMON_BASEID + 3 );
    private static final int NEXPLAYER_EVENT_TIME                       = ( NEXPLAYER_EVENT_COMMON_BASEID + 4 );
    private static final int NEXPLAYER_EVENT_ERROR                      = ( NEXPLAYER_EVENT_COMMON_BASEID + 5 );
    private static final int NEXPLAYER_EVENT_RECORDEND                  = ( NEXPLAYER_EVENT_COMMON_BASEID + 6 );
    private static final int NEXPLAYER_EVENT_STATECHANGED               = ( NEXPLAYER_EVENT_COMMON_BASEID + 7 );
    private static final int NEXPLAYER_EVENT_SIGNALSTATUSCHANGED        = ( NEXPLAYER_EVENT_COMMON_BASEID + 8 );
    private static final int NEXPLAYER_EVENT_DEBUGINFO                  = ( NEXPLAYER_EVENT_COMMON_BASEID + 9 );
    private static final int NEXPLAYER_EVENT_ASYNC_CMD_COMPLETE         = ( NEXPLAYER_EVENT_COMMON_BASEID + 10 );
    private static final int NEXPLAYER_EVENT_RTSP_COMMAND_TIMEOUT       = ( NEXPLAYER_EVENT_COMMON_BASEID + 11 );
    private static final int NEXPLAYER_EVENT_PAUSE_SUPERVISION_TIMEOUT  = ( NEXPLAYER_EVENT_COMMON_BASEID + 12 );
    private static final int NEXPLAYER_EVNET_DATA_INACTIVITY_TIMEOUT    = ( NEXPLAYER_EVENT_COMMON_BASEID + 13 );
    
    private static final int NEXPLAYER_EVENT_RECORDING_ERROR            = ( NEXPLAYER_EVENT_COMMON_BASEID + 14 );
    private static final int NEXPLAYER_EVENT_RECORDING                  = ( NEXPLAYER_EVENT_COMMON_BASEID + 15 );
    private static final int NEXPLAYER_EVENT_TIMESHIFT_ERROR            = ( NEXPLAYER_EVENT_COMMON_BASEID + 16 );
    private static final int NEXPLAYER_EVENT_TIMESHIFT                  = ( NEXPLAYER_EVENT_COMMON_BASEID + 17 );
    //  private static final int NEXPLAYER_EVNET_REPEAT                     = ( NEXPLAYER_EVENT_COMMON_BASEID + 18 );
    //  private static final int NEXPLAYER_EVNET_DECODER_INIT_COMPLETE      = ( NEXPLAYER_EVENT_COMMON_BASEID + 19 );
    private static final int NEXPLAYER_EVNET_STATUS_REPORT              = ( NEXPLAYER_EVENT_COMMON_BASEID + 20 );
    
    //--- just for streaming events ----
    private static final int NEXPLAYER_EVENT_STREAMING_BASEID           = 0x00030000;
    private static final int NEXPLAYER_EVENT_BUFFERINGBEGIN             = ( NEXPLAYER_EVENT_STREAMING_BASEID + 1 );
    private static final int NEXPLAYER_EVENT_BUFFERINGEND               = ( NEXPLAYER_EVENT_STREAMING_BASEID + 2 );
    private static final int NEXPLAYER_EVENT_BUFFERING                  = ( NEXPLAYER_EVENT_STREAMING_BASEID + 3 );
    
    //--- just for video / audio render events ---
    private static final int NEXPLAYER_EVENT_AUDIO_RENDER_BASEID        = 0x00060000;
    private static final int NEXPLAYER_EVENT_AUDIO_RENDER_CREATE        = ( NEXPLAYER_EVENT_AUDIO_RENDER_BASEID + 1 );
    private static final int NEXPLAYER_EVENT_AUDIO_RENDER_DELETE        = ( NEXPLAYER_EVENT_AUDIO_RENDER_BASEID + 2 );  
    //  private static final int NEXPLAYER_EVENT_AUDIO_RENDER_RENDER        = ( NEXPLAYER_EVENT_AUDIO_RENDER_BASEID + 3 );
    private static final int NEXPLAYER_EVENT_AUDIO_RENDER_PAUSE         = ( NEXPLAYER_EVENT_AUDIO_RENDER_BASEID + 4 );
    private static final int NEXPLAYER_EVENT_AUDIO_RENDER_RESUME        = ( NEXPLAYER_EVENT_AUDIO_RENDER_BASEID + 5 );
    
    private static final int NEXPLAYER_EVENT_VIDEO_RENDER_BASEID        = 0x00070000;
    private static final int NEXPLAYER_EVENT_VIDEO_RENDER_CREATE        = ( NEXPLAYER_EVENT_VIDEO_RENDER_BASEID + 1 );
    private static final int NEXPLAYER_EVENT_VIDEO_RENDER_DELETE        = ( NEXPLAYER_EVENT_VIDEO_RENDER_BASEID + 2 );  
    private static final int NEXPLAYER_EVENT_VIDEO_RENDER_RENDER        = ( NEXPLAYER_EVENT_VIDEO_RENDER_BASEID + 3 );
    private static final int NEXPLAYER_EVENT_VIDEO_RENDER_CAPTURE       = ( NEXPLAYER_EVENT_VIDEO_RENDER_BASEID + 4 );
    
    private static final int NEXPLAYER_EVENT_TEXT_RENDER_BASEID         = 0x00080000;
    private static final int NEXPLAYER_EVENT_TEXT_RENDER_INIT           = ( NEXPLAYER_EVENT_TEXT_RENDER_BASEID + 1 );
    private static final int NEXPLAYER_EVENT_TEXT_RENDER_RENDER         = ( NEXPLAYER_EVENT_TEXT_RENDER_BASEID + 2 );
    
    //--- Signal status ---
    /** Normal signal status; see 
     * {@link IListener#onSignalStatusChanged(NexPlayer, int, int) onSignalStatusChanged}
     * for details. */
    public static final int NEXPLAYER_SIGNAL_STATUS_NORMAL      = 0;
    /** Weak signal status; see 
     * {@link IListener#onSignalStatusChanged(NexPlayer, int, int) onSignalStatusChanged}
     * for details. */
    public static final int NEXPLAYER_SIGNAL_STATUS_WEAK        = 1;
    /** No signal (out of service area); see 
     * {@link IListener#onSignalStatusChanged(NexPlayer, int, int) onSignalStatusChanged}
     * for details. */
    public static final int NEXPLAYER_SIGNAL_STATUS_OUT         = 2;
    
    //--- Async command completion values ---
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. */
    public static final int NEXPLAYER_ASYNC_CMD_NONE             = 0x00000000;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. */
    public static final int NEXPLAYER_ASYNC_CMD_OPEN_LOCAL       = 0x00000001;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. */
    public static final int NEXPLAYER_ASYNC_CMD_OPEN_STREAMING   = 0x00000002;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. */
    public static final int NEXPLAYER_ASYNC_CMD_OPEN_TV          = 0x00000003; 
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. */
    public static final int NEXPLAYER_ASYNC_CMD_START_LOCAL      = 0x00000005;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. */
    public static final int NEXPLAYER_ASYNC_CMD_START_STREAMING  = 0x00000006;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. */
    public static final int NEXPLAYER_ASYNC_CMD_START_TV         = 0x00000007;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. */
    public static final int NEXPLAYER_ASYNC_CMD_STOP             = 0x00000008;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. */
    public static final int NEXPLAYER_ASYNC_CMD_PAUSE            = 0x00000009;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. */
    public static final int NEXPLAYER_ASYNC_CMD_RESUME           = 0x0000000A;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. */
    public static final int NEXPLAYER_ASYNC_CMD_SEEK             = 0x0000000B;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. */
    public static final int NEXPLAYER_ASYNC_CMD_FORWARD          = 0x0000000C;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. */
    public static final int NEXPLAYER_ASYNC_CMD_BACKWARD         = 0x0000000D;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. */
    public static final int NEXPLAYER_ASYNC_CMD_STEP_SEEK        = 0x0000000E;
    
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. 
     * @deprecated Experimental; may or may not be present in future versions.
     */
    public static final int NEXPLAYER_ASYNC_CMD_RECORD_START     = 0x00000011;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. 
     * @deprecated Experimental; may or may not be present in future versions.
     */
    public static final int NEXPLAYER_ASYNC_CMD_RECORD_STOP      = 0x00000012;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. 
     * @deprecated Experimental; may or may not be present in future versions.
     */
    public static final int NEXPLAYER_ASYNC_CMD_RECORD_PAUSE     = 0x00000013;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. 
     * @deprecated Experimental; may or may not be present in future versions.
     */
    public static final int NEXPLAYER_ASYNC_CMD_RECORD_RESUME    = 0x00000014;
    
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. 
     * @deprecated Experimental; may or may not be present in future versions.
     */
    public static final int NEXPLAYER_ASYNC_CMD_TIMESHIFT_CREATE    = 0x00000021;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. 
     * @deprecated Experimental; may or may not be present in future versions.
     */
    public static final int NEXPLAYER_ASYNC_CMD_TIMESHIFT_DESTROY   = 0x00000022;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. 
     * @deprecated Experimental; may or may not be present in future versions.
     */
    public static final int NEXPLAYER_ASYNC_CMD_TIMESHIFT_PAUSE     = 0x00000023;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. 
     * @deprecated Experimental; may or may not be present in future versions.
     */
    public static final int NEXPLAYER_ASYNC_CMD_TIMESHIFT_RESUME    = 0x00000024;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. 
     * @deprecated Experimental; may or may not be present in future versions.
     */
    public static final int NEXPLAYER_ASYNC_CMD_TIMESHIFT_FORWARD   = 0x00000025;
    /** Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}. 
     * @deprecated Experimental; may or may not be present in future versions.
     */
    public static final int NEXPLAYER_ASYNC_CMD_TIMESHIFT_BACKWARD  = 0x00000026; 
    
    /** Treat <code>path</code> as a local media file; a possible value for the <code>type</code> parameter of {@link NexPlayer#open open}. */
    public static final int NEXPLAYER_SOURCE_TYPE_LOCAL_NORMAL      = 0;
    /** Treat <code>path</code> as a URL to a streaming media source; a possible value for the <code>type</code> parameter of {@link NexPlayer#open open}. */
    public static final int NEXPLAYER_SOURCE_TYPE_STREAMING         = 1;
    
    /** Use TCP as the transport; possible value for the open method */
    public static final int NEXPLAYER_TRANSPORT_TYPE_TCP            = 0;
    /** Use UDP as the transport; possible value for the open method */
    public static final int NEXPLAYER_TRANSPORT_TYPE_UDP            = 1;
    
    // --- Return values for getState() ---
    /** No state information available for NexPlayer (this
     * is the state after {@link NexPlayer#release() release} has
     * been called; a possible return value of {@link NexPlayer#getState()}.
     */
    public static final int NEXPLAYER_STATE_NONE = 0;
    /** No media source is open (this is the state when
     * the <code>NexPlayer</code> instance is initially created, and
     * after {@link NexPlayer#close() close} has completed; 
     * a possible return value of {@link NexPlayer#getState()}.
     */
    public static final int NEXPLAYER_STATE_CLOSED = 1;
    /** A media source is open but is currently stopped (this is the state
     * after {@link NexPlayer#open open} or {@link NexPlayer#stop() stop} 
     * has completed; a possible return value of {@link NexPlayer#getState()}.
     */
    public static final int NEXPLAYER_STATE_STOP = 2;
    /** A media source is open and playing (this is the state
     * after {@link NexPlayer#start(int) start}  
     * has completed; a possible return value of {@link NexPlayer#getState()}.
     */
    public static final int NEXPLAYER_STATE_PLAY = 3;
    /** A media source is open but has been paused (this is the state
     * after {@link NexPlayer#pause() pause}  
     * has completed; a possible return value of {@link NexPlayer#getState()}.
     */
    public static final int NEXPLAYER_STATE_PAUSE = 4;
    
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/
    public static final int CONTENT_INFO_INDEX_MEDIA_TYPE = 0;
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/
    public static final int CONTENT_INFO_INDEX_MEDIA_DURATION = 1;
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/
    public static final int CONTENT_INFO_INDEX_VIDEO_CODEC = 2;
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/
    public static final int CONTENT_INFO_INDEX_VIDEO_WIDTH = 3;
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/
    public static final int CONTENT_INFO_INDEX_VIDEO_HEIGHT = 4;
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/
    public static final int CONTENT_INFO_INDEX_VIDEO_FRAMERATE = 5;
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/
    public static final int CONTENT_INFO_INDEX_VIDEO_BITRATE = 6;
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/
    public static final int CONTENT_INFO_INDEX_AUDIO_CODEC = 7;
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/
    public static final int CONTENT_INFO_INDEX_AUDIO_SAMPLINGRATE = 8;
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/
    public static final int CONTENT_INFO_INDEX_AUDIO_NUMOFCHANNEL = 9;
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/
    public static final int CONTENT_INFO_INDEX_AUDIO_BITRATE = 10;  
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/
    public static final int CONTENT_INFO_INDEX_MEDIA_ISSEEKABLE = 11;
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/
    public static final int CONTENT_INFO_INDEX_MEDIA_ISPAUSABLE = 12;
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/
    public static final int CONTENT_INFO_INDEX_VIDEO_RENDER_AVE_FPS = 13;
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/
    public static final int CONTENT_INFO_INDEX_VIDEO_AVG_BITRATE = 23;
    /** A possible argument value for {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}*/  
    public static final int CONTENT_INFO_INDEX_AUDIO_AVG_BITRATE = 25;
    
    private final static String TAG = "NEXPLAYER_JAVA";
    
    /** Used for Picture-in-Picture support; not supported by current API version.
     * @deprecated Not supported in current API version; do not use.
     */
    public int                  mNativeNexPlayerClient = 0; // accessed by native methods
    
    private Surface mSurface; // accessed by native methods
    private SurfaceHolder mSurfaceHolder;
    
    @SuppressWarnings("unused")  // Used in native code

    private AudioTrack mAudioTrack;
    
    
    private static IListener    mListener;
    
    // Tracks whether the NexPlayer engine has been successfully initialized
    private boolean mNexPlayerInit = false;
    
    /**
     * Categories for errors.
     * 
     * <B>CAUTION:</B> This is experimental and is subject to change.<p>
     * 
     * Each error code has an associated category.  The intent of this is
     * to group errors based on cause so that a friendlier message can be
     * displayed to the user.  The exact groupings may change in future
     * versions.
     * 
     * @author Nextreaming
     *
     */
    public enum NexErrorCategory {
        /** There is no error */
        NO_ERROR, 
        
        /** Something wrong with what was passed to the API; indicates a 
         *  bug in the host application */ 
        API,
        
        /** Something went wrong internally; this could be due to API 
         *  misuse, something wrong with the OS, or a bug */
        INTERNAL,
        
        /** Some feature of the media is not supported */
        NOT_SUPPORT,
        
        /** General errors */
        GENERAL,
        
        /** Errors we can't control relating to the system (for example, 
         *  memory allocation errors) */
        SYSTEM,
        
        /** Something is wrong with the content itself, or it uses a 
         *  feature we don't recognize */
        CONTENT_ERROR,
        
        /** There was an error communicating with the server or an error 
         *  in the protocol */
        PROTOCOL,
        
        /** A network error was detected */
        NETWORK,
        
        /** An error code base value (these shouldn't be used, so this 
         *  should be treated as an internal error) */
        BASE,
        
        /** Authentication error; not authorized to view this content, 
         *  or a DRM error while determining authorization */
        AUTH; 
    }
    
    /**
     * Possible properties that can be set on a NexPlayer instance.<p>
     * 
     * To set a property, call {@link NexPlayer#setProperty(NexProperty, int) setProperty} on
     * the NexPlayer instance.  To get the current value of a property, call
     *  {@link NexPlayer#getProperty(NexProperty) getProperty}.<p>
     *  
     * <h2>Property Fine-Tuning Guidelines</h2>
     * The default values for the properties should be acceptable for most common cases.
     * However, in some cases, adjusting the properties will give better performance or
     * better behavior.<p>
     * 
     * <h3>Fine-Tuning Buffering Time</h3>
     * When dealing with streaming content, adjusting the buffer size can give smoother
     * playback.  For RTSP streaming, the recommended buffering time is between 3 and 5
     * seconds; for HTTP Live Streaming, the recommended buffering time is 8 seconds.<p>
     * 
     * There are two settings for buffering time:  The initial time (the first time data is 
     * buffered before playback starts) and the re-buffering time (if buffering is needed
     * later, after playback has started).  Both default to 5 seconds.  For example, to
     * set the buffering time to 8 seconds for HTTP Live Streaming:<p>
     * 
     * <pre>
     * void setBufferingTime( NexPlayer hNexPlayer ) {
     *     hNexPlayer.setProperty( 
     *         NexProperty.INITIAL_BUFFERING_DURATION,
     *         8000);
     *     hNexPlayer.setProperty( 
     *         NexProperty.RE_BUFFERING_DURATION,
     *         8000);
     * }
     * </pre>
     *  
     * <h2>Numeric Property Identifiers</h2>
     * Properties can also be identified by numeric value.  This is how NexPlayer identifies
     * properties internally, but in general, it is better to use this enum and the methods
     * listed above instead.<p>
     * 
     * If you must work with the numeric property identifiers directly,
     * you can retrieve them using the {@link com.nextreaming.nexplayerengine.NexProperty#getPropertyCode() getPropertyCode}
     * method of a member of this enum, and the methods {@link com.nextreaming.nexplayerengine.NexPlayer#getProperties(int) getProperties(int)} and 
     * {@link com.nextreaming.nexplayerengine.NexPlayer#setProperties(int, int) setProperties(int, int)} can be used to get or set a property based
     * on the numeric identifier.<p>
     * 
     * @author Nextreaming
     *
     */
    public enum NexProperty {
        
        /**
         * Number of milliseconds of media to buffer initially before 
         * beginning streaming playback (HLS, RTSP, etc.).<p>
         * 
         * This is the initial amount of audio and video that NexPlayer buffers
         * when it begins playback.  If further buffering is required later in
         * the playback process, the value of the property 
         * {@link NexPlayer.NexProperty#RE_BUFFERING_DURATION RE_BUFFERING_DURATION} 
         * will be used instead.<p>
         * 
         * <b>type:</b> unsigned int<p>
         * <b>unit:</b> msec (1/1000 sec)<p>
         * <b>default:</b> 5000 (5 seconds)
         */
        INITIAL_BUFFERING_DURATION              (9),
        /**
         * Number of milliseconds of media to buffer if additional buffering
         * is required during streaming playback (HLS, RTSP, etc.).<p>
         * 
         * This is the amount of audio and video that NexPlayer buffers
         * when the buffer becomes empty during playback (requiring additional
         * buffering).  For the initial buffering, the value of the property 
         * {@link NexPlayer.NexProperty#INITIAL_BUFFERING_DURATION INITIAL_BUFFERING_DURATION} 
         * is used instead.<p>
         * 
         * <b>type:</b> unsigned int<p>
         * <b>unit:</b> msec (1/1000 sec)<p>
         * <b>default:</b> 5000 (5 seconds)
         */
        RE_BUFFERING_DURATION                   (10),
        /**
         * The number of milliseconds (as a negative number) that video is allowed 
         * to run ahead of audio before the system waits for audio to catch up.<p>
         * 
         * For example, -20 means that if the current video time is more than 20msec 
         * ahead of the audio time, the current video frame will not be displayed until
         * the audio catches up to the same time stamp.  This is used to adjust video 
         * and audio synchronization.<p>
         * 
         * <b>type:</b> int <i>(should be negative)</i><p>
         * <b>unit:</b> msec (1/1000 sec)<p>
         * <b>default:</b> -20 (20msec)
         */
        TIMESTAMP_DIFFERENCE_VDISP_WAIT         (13),
        /**
         * The number of milliseconds that video is allowed to run behind audio 
         * before the system begins skipping frames to maintain synchronization.<p>
         * 
         * For example, 200 means that if the current video time is more than 200msec 
         * behind the audio time, the current video frame will be skipped.
         * This is used to adjust video and audio synchronization.<p>
         * 
         * <b>type:</b> unsigned int<p>
         * <b>unit:</b> msec (1/1000 sec)<p>
         * <b>default:</b> 200 (0.2 sec)
         */
        TIMESTAMP_DIFFERENCE_VDISP_SKIP         (14),
        /**
         * Amount of time to wait for a server response before
         * generating an error event.<p>
         * 
         * If there is no response from the server for longer than
         * the amount of time specified here, an error event will be
         * generated and playback will stop.<p>
         * 
         * Set this to zero to disable timeout (NexPlayer will wait
         * indefinitely for a response).<p>
         * 
         * <b>type:</b> unsigned int<p>
         * <b>unit:</b> msec (1/1000 sec)<p>
         * <b>default:</b> 60,000 (60 seconds)
         */
        DATA_INACTIVITY_TIMEOUT                 (19),
        /**
         * Amount of time to wait before timing out when establishing
         * a connection to the server.<p>
         * 
         * If the connection to the server (the socket connection) cannot
         * be established within the specified time, an error event will
         * be generated and playback will not start.<p>
         * 
         * Set this to zero to disable timeout (NexPlayer will wait
         * indefinitely for a connection).<p>
         * 
         * <b>type:</b> unsigned int<p>
         * <b>unit:</b> msec (1/1000 sec)<p>
         * <b>default:</b> 10,000 (10 seconds)
         */
        SOCKET_CONNECTION_TIMEOUT               (20),
        /**
         * Minimum possible port number for the RTP port that is created
         * when performing RTSP streaming over UDP.<p>
         * 
         * <b>type:</b> unsigned int<p>
         * <b>default:</b> 12000
         */
        RTP_PORT_MIN                            (22),
        /**
         * Maximum possible port number for the RTP port that is created
         * when performing RTSP streaming over UDP.<p>
         * 
         * <b>type:</b> unsigned int<p>
         * <b>default:</b> 30000
         */
        RTP_PORT_MAX                            (23),
        /**
         * Prevents the audio track from playing back when set to TRUE (1).
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 0
         */
        NOTOPEN_PLAYAUDIO                       (27),
        /**
         * Prevents the video track from playing back when set to TRUE (1).
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 0
         */
        NOTOPEN_PLAYVIDEO                       (28),
        /**
         * Prevents the text (subtitle) track from playing back when set to TRUE (1).
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 0
         */
        NOTOPEN_PLAYTEXT                        (29),
        /**
         * The logging level for the NexPlayer protocol module.<p>
         * 
         * This affects the type of messages that are logged by the
         * protocol module (it does not affect the logging level of
         * other NexPlayer components).<p>
         * 
         * This value is made by or-ing together zero or more of the
         * following values:<p>
         * 
         * <ul>
         * <li><b>LOG_LEVEL_NONE (0x00000000)</b><br />Don't log anything <i>(not currently supported)</i>
         * <li><b>LOG_LEVEL_DEBUG (0x00000001)</b><br /> Log start, stop and errors (default for the debug version)
         * <li><b>LOG_LEVEL_RTP (0x00000002)</b><br /> Generate log entries relating to RTP packets
         * <li><b>LOG_LEVEL_RTCP (0x00000004)</b><br /> Generate log entries relating to RTCP packets
         * <li><b>LOG_LEVEL_FRAME (0x00000008)</b><br /> Log information about the frame buffer
         * <li><b>LOG_LEVEL_ALL (0x0000FFFF)</b><br /> Log everything <i>(not currently supported)</i>
         * </ul>
         * 
         * <b>type:</b> unsigned int<p>
         * <b>default:</b> LOG_LEVEL_DEBUG
         */
        LOG_LEVEL                               (35),
        /**
         * Controls when video initialization happens.<p>
         * 
         * This can be any of the following values:<p>
         * 
         * <ul>
         * <li><b>AV_INIT_PARTIAL (0x00000000)</b><br />
         *  If there is an audio track, wait for audio initialization to complete
         *  before initializing video. 
         * <li><b>AV_INIT_ALL (0x00000001)</b><br />
         *  Begin video initialization as soon as there is any video data, without
         *  any relation to the audio track status.
         * </ul>
         * <b>type:</b> unsigned int<p>
         * <b>default:</b> AV_INIT_PARTIAL
         */
        AV_INIT_OPTION                          (46),
        /**
         * If set to 1, allows media playback even if the audio codec is not supported.<p>
         * 
         * The default behavior (if this is 0) is to return an error or generate an
         * error event if the audio codec is not supported.<p>
         * 
         * <b>type:</b> unsigned int<p>
         * <b>default:</b> 0
         */
        PLAYABLE_FOR_NOT_SUPPORT_AUDIO_CODEC    (48),
        /**
         * If set to 1, allows media playback even if the video codec is not supported.<p>
         * 
         * The default behavior (if this is 0) is to return an error or generate an
         * error event if the video codec is not supported.<p>
         * 
         * <b>type:</b> unsigned int<p>
         * <b>default:</b> 0
         */
        PLAYABLE_FOR_NOT_SUPPORT_VIDEO_CODEC    (49),
        /**
         * If more than this number of frames are skipped during rendering, the 
         * remaining frames up to the next keyframe are forcibly discarded and 
         * playback resumes from the next keyframe.<p>
         * 
         * <b>type:</b> unsigned int<p>
         * <b>default:</b> 0xFFFFFFFF
         */
        MAX_VIDEO_SKIP_FRAME_CNT                (50),
        /**
         * Live HLS playback option.<p>
         * 
         * This must be one of the following values:<p>
         * 
         * <ul>
         * <li><b>LIVE_VIEW_RECENT (0x00000000)</b><br />
         * Start playback from the most recent part in the HLS live playlist.  Except in
         * special cases, this is the value that should be used, as this provides the lowest
         * latency between streaming and playback.
         * <li><b>LIVE_VIEW_FIRST (0x00000001)</b><br />
         * Unconditionally start HLS playback from the first entry in the HLS playlist.
         * </ul>
         * 
         * <b>type:</b> unsigned int<p>
         * <b>default:</b> LIVE_VIEW_RECENT
         */
        LIVE_VIEW_OPTION                    (53),
        
        /**
         * RTSP/HTTP User Agent value.<p>
         * 
         * <b>type:</b> String <p>
         */
        USERAGENT_STRING                    (58),
        
        /**
         * Controls what is displayed while the player is waiting for audio data.
         * 
         * If this is set to 1 (the default), the first video frame is displayed as soon
         * as it has been decoded, and the player waits in a "freeze-frame" state until
         * the audio starts, at which point both the audio and video play together.<p>
         * 
         * If this is set ot 0, the player will not display the first video frame until
         * the audio is ready to play.  Whatever was previously displayed will continue
         * to be visible (typically a black frame).<p>
         * 
         * Once audio has started, the behavior for both settings is the same; this only
         * affects what is displayed while the player is waiting for audio data.<p>
         * 
         * Under old versions of the SDK (prior to the addition of this property) the
         * default behavior was as though this property were set to zero.<p>
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 1
         */
        FIRST_DISPLAY_VIDEOFRAME                (60),
        
        /**
         * If set to true, unconditionally skips all B-frames without decoding them.<p>
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 0
         */
        SET_TO_SKIP_BFRAME                      (103),
        /**
         * Maximum amount of silence to insert to make up for lost audio frames.<p>
         * 
         * Under normal operation, if audio frames are lost (if there is a time gap
         * in received audio frames), silence will be inserted automatically to make
         * up the gap.<p>
         * 
         * However, if the number of audio frames lost represents a span of time
         * larger than the value set for this property, it is assumed that there is
         * a network problem or some other abnormal condition and silence is not
         * inserted.<p>
         * 
         * This prevents, for example, a corruption in the time stamp in an audio
         * frame from causing the system to insert an exceptionally long period of
         * silence (which could possibly prevent further audio playback or cause
         * other unusual behavior).<p>
         * 
         * <b>type:</b> unsigned int<p>
         * <b>unit:</b> msec (1/1000 sec)<p>
         * <b>default:</b> 5000 (5 seconds)
         */
        TOO_MUCH_LOSTFRAME_DURATION             (105),
        /**
         * If set to 1, enables local file playback support.
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 1
         */
        SUPPORT_LOCAL                           (110),
        /**
         * If set to 1, enables RTSP streaming support.
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 1
         */
        SUPPORT_RTSP                            (111),
        /**
         * If set to 1, enables progressive download support.
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 1
         */
        SUPPORT_PD                              (112),
        /**
         * If set to 1, enables Microsoft Windows Media Streaming support.
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 0
         */
        SUPPORT_WMS                             (113),
        /**
         * If set to 1, enables Real media streaming support.
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 0
         */
        SUPPORT_RDT                             (114),
        /**
         * If set to 1, enables Apple HTTP Live Streaming (HLS) support.
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 1
         */
        SUPPORT_APPLE_HTTP                      (115),
        /**
         * If set to 1, enables HLS Adaptive Bit Rate (ABR) support.
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 1
         */
        SUPPORT_ABR                             (116),
        /**
         * When using HLS ABR, the maximum allowable bandwidth.  Any track
         * with a bandwidth greater than this value will not be played back.<p>
         * 
         * Set to zero for no maximum.<p>
         * 
         * <b>type:</b> unsigned int<p>
         * <b>unit:</b> bps (bits per second)<p>
         * <b>default:</b> 0 (no maximum)
         */
        MAX_BW                                  (117),
        /**
         * Limits the H.264 profile that can be selected from an HLS playlist.<p>
         * 
         * Under nomral operation, the track with the higest supported H.264 profile
         * is selected from an HLS playlist.  If this property is set, no track with
         * a profile higher than this value will be selected.<p>
         * 
         * Set this to zero for no limit.<p>
         * 
         * <b>type:</b> unsigned int<p>
         * <b>default:</b> 0 (use any profile)
         */
        MAX_H264_PROFILE                        (118),
        /**
         * If set to 1, lost audio frames are always ignored (silence is never inserted).<p>
         * 
         * See 
         * {@link NexPlayer.NexProperty#TOO_MUCH_LOSTFRAME_DURATION TOO_MUCH_LOSTFRAME_DURATION}
         * for details about the insertion of silence for lost audio frames.<p>
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 0
         */
        IGNORE_AUDIO_LOST_FRAME                 (119),
        /**
         * This is used to force NexPlayer to begin buffering as soon as all
         * availale audio frames have been processed, without regard to the state
         * of the video buffer.<p>
         * 
         * Under normal operation, when there are no audio frames left in the audio
         * buffer, NexPlayer switches to buffering mode and temporarily suspends
         * playback.<p>
         * 
         * There is an exception if the video buffer is more than 60% full.  In this
         * case, NexPlayer will continue video playback even if there is no more
         * audio available.<p>
         * 
         * Setting this property to TRUE (1) bypasses this exception
         * and forces the system to go to buffering immediately if there are no audio
         * frames left to play.<p>
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 0
         */
        ALWAYS_BUFFERING                        (120),
        /**
         * When true (1), this proeprty causes audio/video synchronization to be bypassed.<p>
         * 
         * In this state, audio and video are played back independently as soon as data is
         * received.<p>
         * 
         * This property can be enabled if audio and video synchronization are not important,
         * and if real-time behavior is needed between the server and the client.<p>
         * 
         * In normal cases, this should not be used (it should be set to zero) because it will
         * cause video and audio to quickly lose synchronization for most normal media streams.<p>
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 0
         */
        IGNORE_AV_SYNC                          (121),
        
        /**
         * Enables smooth skipping.<p>
         * 
         * If the decoding or display of video frames takes too long, it may be necessary to skip
         * some frames in order to maintian a normal rate of playback.  Under normal operation,
         * frames are skipped immediately to catch up.  When smooth skipping is enabled, skipped
         * frames are spread out to try to make playback appear smoother.
         */
        SUPPORT_SMOOTH_SKIPPING                 (122),
        
        /**
         * If set to 1, enables MS Smooth Streaming support.
         * 
         * <b>type:</b> boolean<p>
         * <b>default:</b> 1
         */
        SUPPORT_MS_SMOOTH_STREAMING                     (123),
        
        /**
         * Adjusts A/V synchronization by ofsetting video relative to audio. <p>
         * Positive values cause the video to play faster than the audio, while
         * negative values cause the audio to play faster than the video.  Under normal
         * operation, this can be set to zero, but in some cases where the syncronizaiton
         * is bad in the original content, this can be used to correct for the error.<p>
         * 
         * <b>type:</b> integer <p>
         * <b>unit:</b> msec (1/1000 sec)<p>
         * <b>range:</b> -2000 ~ +2000<p>
         * <b>default:</b> 0
         * 
         */
        AV_SYNC_OFFSET                          (124),          // JDKIM 2010/12/09
        
        /**
         * Limits the maximum width (in pixels) of the video tracks that can be
         * selected during streaming play.
         *
         * This is used to prevent NexPlayer&trade; from attempting to play
         * tracks that are encoded at too high a resolution for the device to
         * handle effectively.  NexPlayer&trade; will instead select a track
         * with a lower resolution.
         *
         * <b>type:</b> integer <p>
         * <b>unit:</b> pixels <p>
         * <b>default:</b> 720
         */
        MAX_WIDTH                               (125),
        
        /**
         * Limits the maximum height (in pixels) of the video tracks that can be
         * selected during streaming play.
         *
         * This is used to prevent NexPlayer&trade; from attempting to play
         * tracks that are encoded at too high a resolution for the device to
         * handle effectively.  NexPlayer&trade; will instead select a track
         * with a lower resolution.
         *
         * <b>type:</b> integer <p>
         * <b>unit:</b> pixels <p>
         * <b>default:</b> 480
         */
        MAX_HEIGHT                              (126),

        
        /**
         * Preferred bandwidth when switching tracks during streaming play.<p>
         *
         * Under normal operation (when this property is zero), if the available
         * network bandwidth drops below the minimum needed to play the current 
         * track without buffering, the player will immediately switch to a lower 
         * bandwidth track, if one is available, to minimize any time spent buffering.<p>
         *
         * If this property is set, the player will attempt to choose only tracks 
         * above the specified bandwidth, even if that causes some buffering.  
         * However, if the buffering becomes too severe or lasts for an extended 
         * time, the player may eventually switch to a lower-bandwidth track anyway.<p>
         *
         * <b>type:</b> unsigned int <p>
         * <b>unit:</b> Kbps<p>
         * <b>default:</b> 0
         * 
         * @see {@link NexPlayer.NexProperty#PREFER_AV PREFER_AV}
         */
        PREFER_BANDWIDTH                        (129),
        
        /**
         * Controls whether NexPlayer&trade; prefers tracks with both
         * audio and video content.<p>
         *   
         * Under normal operation (when this property is set to 0), if the available
         * network bandwidth drops below the minimum needed to play the current 
         * track without buffering, the player will immediately switch to a lower 
         * bandwidth track, if one is available, to minimize any time spent buffering.<p>
         *
         * If this property is set to 1, the player will attempt to choose only tracks 
         * that include both audio and video content, even if that causes some buffering.  
         * However, if the buffering becomes too severe or lasts for an extended 
         * time, the player may eventually switch to an audio-only track anyway.<p>
         *
         * <b>type:</b> unsigned int <p>
         * <b>default:</b> 0
         * <b>Values:</b><ul>
         * <li> 0: normal behavior (immediate switching)
         * <li> 1: prefer tracks with both audio and video
         * </ul>
         * 
         * @see {@link NexPlayer.NexProperty#PREFER_BANDWIDTH PREFER_BANDWIDTH}
         */
        PREFER_AV                               (130),
        
        /**
         * Allows NexPlayer&trade; to switch to a lower bandwidth track if the
         * resolution or bitrate of the current track is too high for the
         * device to play smoothly.<p>
         * 
         * Under nomral operation, NexPlayer&trade; switches tracks based solely on
         * current network conditions.  When this property is enabled, NexPlayer&trade;
         * will also switch to a lower bandwith track if too many frames are skipped
         * during playback.<p>
         * 
         * This is useful for content that is targeted for a variety of
         * devices, some of which may not be powerful enough to handle the higher
         * quality streams.<p>
         * 
         * The {@link NexProperty#TRACKDOWN_VIDEO_RATIO TRACKDOWN_VIDEO_RATIO} property
         * controls the threshold at which the track change will occur, if frames
         * are beign skipped.<p>
         * 
         * <b>type:</b> boolean <p>
         * <b>default:</b> 0
         * <b>Values:</b><ul>
         * <li> 0: normal behavior (switch based on network conditions only)
         * <li> 1: switch based on network conditions and device performance
         * </ul>
         * 
         */
        ENABLE_TRACKDOWN                        (131),
        
        /**
         * Controls the ratio of skipped frames that will be tolerated before
         * a track change is forced, if {@link NexProperty#ENABLE_TRACKDOWN ENABLE_TRACKDOWN}
         * is enabled.<p>
         * 
         * The formula used to determined if a track switch is necessary is:<p>
         * <code>&nbsp;&nbsp;&nbsp;&nbsp;100 * (RenderedFrames / DisplayedFrames) &lt; TrackdownVideoRatio </code><p>
         * 
         * In other words, if this property is set to 70, and {@link NexProperty#ENABLE_TRACKDOWN ENABLE_TRACKDOWN}
         * is set to 1, NexPlayer&trade; will require that at least 70% of the decided frames
         * be displayed.  If less than 70% can be displayed (greater than 30% skipped frames),
         * then the next lower bandwidth track will be selected.<p>
         * 
         * A performance-based track switch permanently limits the maximum bandwidth of
         * tracks that are eligible for playback, until the content is closed.  (This differs
         * from the bandwidth-based algorithm, which continuously adapts to current
         * network bandwidth).<p>
         */
        TRACKDOWN_VIDEO_RATIO                   (132),
        
        /**
         * Controls the alogrithm used for bitrate switching when playing an HLS stream.<p>
         *
         * <b>type:</b> unsigned int <p>
         * <b>default:</b> 0<p>
         * <b>Values:</b>
         * 
         * <ul>
         * <li> <b>0:</b> Use a more agressive algorithm: up-switching happens sooner.
         * <li> <b>1:</b> Use a more conservative algorithm: up-switching happens only if a
         *          significant amount of extra bandwidth is available beyond that
         *          required to support the given bitrate.  This is similar to
         *          the iPhone algorithm.
         * </ul>
         *
         */
        HLS_RUNMODE                             (133),

        /**
         * Additional HTTP headers to use to supply credentials when a 401 response
         * is received from the server.<p>
         *
         * The string should be in the form of zero or more HTTP headers (header
         * name and value), and each header (including the last) should be terminated
         * with a CRLF sequence, for example:
         * \code
         * "id: test1\r\npw: 12345\r\n"
         * \endcode
         * The particulars of the headers depend on the server and the authentication
         * method being used.
         *
         * <b>type:</b> String <p>
         */
        HTTP_CREDENTIAL                             (134),

        /**
         * Controls whether the player honors cookies sent by the server.
         *
         * <b>type:</b> unsigned int <p>
         * <b>default:</b>1<p>
         * <b>Values:</b>
         * 
         * <ul>
         * <li> <b>0:</b> Ignore HTTP cookie headers sent by the server.
         * <li> <b>1:</b> Cookie headers received from a streaming server along
         *                with the initial manifest or playlist are included
         *                with further HTTP requests during the session.
         * </ul>
         
         */
        SET_COOKIE                                  (165),


        SET_DURATION_OF_UPDATE_CONTENT_INFO     (166), 
        /**
         * Indicates whether speed control is available on this device.<p>
         *
         * This is useful to determine whether to display the speed control
         * in the user interface.
         *
         * <b>type:</b> unsigned int <i>(read-only)</i><p>
         * <b>Values:</b>
         * 
         * <ul>
         * <li> <b>0:</b> Device does not support speed control.
         * <li> <b>1:</b> Device supports speed control.
         * </ul>
         
         */
        SPEED_CONTROL_AVAILABILITY                  (0x00050001),

        /**
         * Controls the maximum number of pages the player can allocate for
         * the remote file cache.<p>
         *
         * The remote file cache stores data that has been read from disk or
         * received over the network (this incldues local, streaming and
         * progressive content).<p>
         *
         * In general, this value should not be changed, as an incorrect
         * setting can adversely affect performance, particularly when seeking.<p>
         *
         * In order to play multiplexed content, at least one audio chunk and
         * one video chunk must fit inside a single RFC buffer page.  For certain formats
         * (PIFF, for example) at very high bitrates, the chunks may be too big
         * to fit in a single page, in which case the RFC buffer page size will need
         * to be increased.  If the system has limited memory resources, it may be
         * necessary to decrease the buffer count when increasing the page size.<p>
         *
         * Increasing the page size can increase seek times, especially for data
         * received over the network (progressive download and streaming cases), so
         * this value should not be changed unless there are issues playing
         * specific content that cannot be solved in another way.<p>
         *
         * <b>type:</b> unsigned integer <p>
         * <b>unit:</b> number of buffers <p>
         * <b>default:</b> 20<p>
         */
        RFC_BUFFER_COUNT                            (0x00070001),
        
        /**
         * Controls the size of each page in the remote file cache.<p>
         *
         * Use caution when adjusting this value.  Improper settings may
         * adversely affect performance, or may cause some content to
         * fail to play.<p>
         *
         * See RFC_BUFFER_COUNT for a detailed description.<p>
         *
         * <b>type:</b> unsigned integer <p>
         * <b>unit:</b> kilobytes<p>
         * <b>default:</b> 256<p>
         */
        RFC_BUFFER_PAGE_SIZE                        (0x00070002);
        
        private int mIntCode;
        
        /** This is a posisble setting for the LOG_LEVEL property; see that property for details. */
        public static final int LOG_LEVEL_NONE = 0x00000000;
        /** This is a posisble setting for the LOG_LEVEL property; see that property for details. */
        public static final int LOG_LEVEL_DEBUG = 0x00000001;
        /** This is a posisble setting for the LOG_LEVEL property; see that property for details. */
        public static final int LOG_LEVEL_RTP = 0x00000002;
        /** This is a posisble setting for the LOG_LEVEL property; see that property for details. */
        public static final int LOG_LEVEL_RTCP = 0x00000004;
        /** This is a posisble setting for the LOG_LEVEL property; see that property for details. */
        public static final int LOG_LEVEL_FRAME = 0x00000008;
        /** This is a posisble setting for the LOG_LEVEL property; see that property for details. */
        public static final int LOG_LEVEL_ALL = 0x0000FFFF;
        
        /** This is a posisble setting for the AV_INIT_OPTION property; see that property for details. */
        public static final int AV_INIT_PARTIAL = 0x00000000;
        /** This is a posisble setting for the AV_INIT_OPTION property; see that property for details. */
        public static final int AV_INIT_ALL = 0x00000001;
        
        /** This is a posisble setting for the APPLS_LIVE_VIEW_OPTION property; see that property for details. */
        public static final int LIVE_VIEW_RECENT = 0x00000000;
        /** This is a posisble setting for the APPLS_LIVE_VIEW_OPTION property; see that property for details. */
        public static final int LIVE_VIEW_FIRST = 0x00000001;
        
        NexProperty( int intCode ) {
            mIntCode = intCode;
        }
        
        /** 
         * Gets the integer code for the NexPlayer property.
         * 
         * @return the integer code for the property
         */
        public int getPropertyCode( ) {
            return mIntCode;
        }
    }
    
    /**
     * Possible error codes that NexPlayer can return.  This is a Java <i>enum</i> so
     * each error constant is an object, but you can convert to or from a numerical
     * code using instance and class methods.<p>
     * 
     * To get the error constant for a given code, call {@link com.nextreaming.nexplayerengine.NexErrorCode#fromIntegerValue(int) fromIntegerValue(int)}.<p>
     * 
     * To get the error code given an error constant, call {@link com.nextreaming.nexplayerengine.NexErrorCode#getIntegerCode() getIntegerCode()}.<p>
     * 
     * Because this is a Java <i>enum</i>, it is very easy to include the name of the
     * error constant in an error message instead of just the number.  For example, the following
     * code logs the errors that are received from the NexPlayer engine:<p>
     * 
     * <pre>
     * void onError( NexPlayer mp, 
     *               NexErrorCode errorCode ) 
     * {
     *     Log.d( "onError", 
     *            "Received the error: " 
     *               + errorCode.name() 
     *               + " (0x" 
     *               + Integer.toHexString(
     *                    errorCode.getIntegerCode()) 
     *               + ")." 
     *          );
     * }
     * </pre>
     * 
     * @author Nextreaming
     *
     */
    public enum NexErrorCode {
        NONE(                           0x00000000,NexErrorCategory.NO_ERROR,null),
        HAS_NO_EFFECT(                  0x00000001,NexErrorCategory.API,null),
        INVALID_PARAMETER(              0x00000002,NexErrorCategory.API,null),
        INVALID_INFO(                   0x00000003,NexErrorCategory.API,null),
        INVALID_STATE(                  0x00000004,NexErrorCategory.API,null),
        MEMORY_OPERATION(               0x00000005,NexErrorCategory.SYSTEM, "Memory call failure"),
        FILE_OPERATION(                 0x00000006),
        FILE_INVALID_SYNTAX(            0x00000007),
        NOT_SUPPORT_PVX_FILE(           0x00000008,NexErrorCategory.NOT_SUPPORT),
        NOT_SUPPORT_AUDIO_CODEC(        0x00000009,NexErrorCategory.NOT_SUPPORT, "The audio codec is not supported"),
        NOT_SUPPORT_VIDEO_CODEC(        0x0000000A,NexErrorCategory.NOT_SUPPORT, "The video codec is not supported"),
        NOT_SUPPORT_VIDEO_RESOLUTION(   0x0000000B,NexErrorCategory.NOT_SUPPORT, "The video resolution is not supported"),
        NOT_SUPPORT_MEDIA(              0x0000000C,NexErrorCategory.NOT_SUPPORT, "The content format is not supported"),
        INVALID_CODEC(                  0x0000000D,NexErrorCategory.CONTENT_ERROR, "The codec is not supported or is invalid"),
        CODEC(                          0x0000000E,NexErrorCategory.GENERAL, "The codec reported an error"),
        PARTIAL_SUCCESS(                0x0000000F),
        ALREADY_CREATE_ASYNC_PROC(      0x00000010,NexErrorCategory.INTERNAL),
        INVALID_ASYNC_CMD(              0x00000011,NexErrorCategory.INTERNAL),
        ASYNC_OTHERCMD_PRCESSING(       0x00000012),            // The async queue is full (too many commands issued before existing commands completed)
        RTCP_BYE_RECEIVED(              0x00000013,NexErrorCategory.PROTOCOL),
        USER_TERMINATED(                0x00000014),
        SYSTEM_FAIL(                    0x00000015,NexErrorCategory.SYSTEM, "System call failure"),
        NODATA_IN_BUFFER(               0x00000016),
        UNKNOWN(                        0x00000017),
        NOT_SUPPORT_TO_SEEK(            0x00000018,NexErrorCategory.NOT_SUPPORT, "The media source does not support seeking."),
        NOT_SUPPORT_AV_CODEC(           0x00000019,NexErrorCategory.NOT_SUPPORT, "Neither the audio nor video codec is supported"),

        NOT_SUPPORT_DRM(                0x00000020),
        NOT_SUPPORT_WMDRM(          0x00000021),
        
        PROTOCOL_BASE(                  0x00010000, NexErrorCategory.PROTOCOL),
        PROTOCOL_INVALID_URL(           0x00010001, NexErrorCategory.PROTOCOL),
        PROTOCOL_INVALID_RESPONSE(      0x00010002, NexErrorCategory.PROTOCOL),
        PROTOCOL_CONTENTINFO_PARSING_FAIL( 0x00010003, NexErrorCategory.PROTOCOL),
        PROTOCOL_NO_PROTOCOL(           0x00010004, NexErrorCategory.PROTOCOL),
        PROTOCOL_NO_MEDIA(              0x00010005, NexErrorCategory.PROTOCOL),
        PROTOCOL_NET_OPEN_FAIL(         0x00010006, NexErrorCategory.PROTOCOL),
        PROTOCOL_NET_CONNECT_FAIL(      0x00010007, NexErrorCategory.NETWORK),
        PROTOCOL_NET_BIND_FAIL(         0x00010008, NexErrorCategory.NETWORK),
        PROTOCOL_NET_DNS_FAIL(          0x00010009, NexErrorCategory.NETWORK),
        PROTOCOL_NET_CONNECTION_CLOSED( 0x0001000A, NexErrorCategory.NETWORK),
        PROTOCOL_NET_SEND_FAIL(         0x0001000B, NexErrorCategory.NETWORK),
        PROTOCOL_NET_RECV_FAIL(         0x0001000C, NexErrorCategory.NETWORK),
        PROTOCOL_NET_REQUEST_TIMEOUT(   0x0001000D, NexErrorCategory.NETWORK),
        
        ERROR_HTTP_STATUS_CODE(         0x00020000, NexErrorCategory.NETWORK),
        NETWORK_RELATED_PROBLEM(        0x0002FFFF,NexErrorCategory.NETWORK),
        
        ERROR_INTERNAL_BASE(            0x00030000, NexErrorCategory.BASE),
        
        ERROR_EXTERNAL_BASE(            0x00040000, NexErrorCategory.BASE),
        
        DOWNLOADER_INVALID_PARAMETER(   0x00050000,NexErrorCategory.API),
        DOWNLOADER_INVALID_STATE(       0x00050000+1,NexErrorCategory.API),
        DOWNLOADER_MEMORY_OPERATION(    0x00050000+2,NexErrorCategory.SYSTEM, "Memory operation error in downloader layer"),
        DOWNLOADER_FILE_OPERATION(      0x00050000+3),
        DOWNLOADER_CONNECTION_FAIL(     0x00050000+4,NexErrorCategory.NETWORK),
        DOWNLOADER_CONNECTION_CLOSED(   0x00050000+5,NexErrorCategory.NETWORK),
        DOWNLOADER_PVXDOWN_FAIL(        0x00050000+6,NexErrorCategory.PROTOCOL),
        DOWNLOADER_PVXPARSING_FAIL(     0x00050000+7,NexErrorCategory.PROTOCOL),
        DOWNLOADER_NOTMULTIPARTS(       0x00050000+8,NexErrorCategory.PROTOCOL),
        DOWNLOADER_HTTPPARSING_FAIL(    0x00050000+9,NexErrorCategory.PROTOCOL),
        
        TIMESHIFT_BASE(                 0x00060000,NexErrorCategory.BASE),  
        TIMESHIFT_WRITE(                0x00060000+1),  
        TIMESHIFT_READ(                 0x00060000+2),  
        TIMESHIFT_FIND_IFRAME(          0x00060000+3), 
        
        DIVXDRM_BASE(                   0x00070000,NexErrorCategory.BASE),
        DIVXDRM_NOT_AUTHORIZED(         0x00070000+1,NexErrorCategory.AUTH),
        DIVXDRM_NOT_REGISTERED(         0x00070000+2,NexErrorCategory.AUTH),
        DIVXDRM_RENTAL_EXPIRED(         0x00070000+3,NexErrorCategory.AUTH),
        DIVXDRM_GENERAL_ERROR(          0x00070000+4,NexErrorCategory.AUTH),
        DIVXDRM_NEVER_REGISTERED(       0x00070000+5,NexErrorCategory.AUTH),
        
        THUMBNAIL_BASE(                 0x00080000,NexErrorCategory.BASE),
        THUMBNAIL_CREATE_FAIL(          0x00080000+1),
        THUMBNAIL_PROCESS_FAIL(         0x00080000+2),
        THUMBNAIL_DRM_CONTENTS(         0x00080000+3,NexErrorCategory.AUTH),
        
        RECORD_BASE(                    0x00090000,NexErrorCategory.BASE),
        ADDFRAME_SIZEFULL(              0x00090000+1),
        ADDFRAME_TIMEFULL(              0x00090000+2),
        ADDFRAME_MEMFULL(               0x00090000+3),
        ADDFRAME_ERROR(                 0x00090000+4);
        
        private int mCode;
        private String mDesc;
        private NexErrorCategory mCategory;
        
        
        NexErrorCode( int code, String desc ){
            mCode = code;
            mDesc = desc;
            mCategory = NexErrorCategory.GENERAL;
        }
        
        NexErrorCode( int code, NexErrorCategory category, String desc ){
            mCode = code;
            mDesc = desc;
            mCategory = category;
        }
        
        NexErrorCode( int code, NexErrorCategory category ){
            mCode = code;
            mDesc = "An error occurred (error 0x " + Integer.toHexString(mCode) + ": " + this.name() + ").";
            mCategory = category;
        }
        
        NexErrorCode( int code ){
            mCode = code;
            mDesc = "An error occurred (error 0x " + Integer.toHexString(mCode) + ": " + this.name() + ").";
            mCategory = NexErrorCategory.GENERAL;
        }
        
        /**
         * Returns the integer code associated with a given error
         * @return integer error code as provided by the NexPlayer&trade; Engine
         */
        public int getIntegerCode() {
            return mCode;
        }
        
        /**
         * Returns a description of the error suitable for display in an
         * error pop-up.<p>
         * 
         * <B>CAUTION:</B> This is experimental and is subject to change.
         * The strings returned by this method may change in future versions,
         * may not cover all possible errors, and are not currently localized.<p>
         * 
         * @return a string describing the error
         */
        public String getDesc() {
            return mDesc;
        }
        
        /**
         * Returns the category of the error.<p>
         * 
         * <B>CAUTION:</B> This is experimental and is subject to change.<p>
         * 
         * Error categories are an experimental feature.  The idea is that the
         * application can provide a friendlier (and possibly more useful) message
         * based on the category of the error.  For example, if the category is
         * <i>NETWORK</i>, the application may suggest that the user check their
         * network connection. <p>
         * 
         * This is experimental, so the set of categories may change in future
         * versions of the API, or the feature may be removed entirely.  Use it
         * with caution.<p>
         * 
         * @return the category to which the error belongs
         */
        public NexErrorCategory getCategory() {
            return mCategory;
        }
        
        /**
         * returns a NexErrorCode object for the specified error code.
         * 
         * @param code
         *          the integer code to convert into a NexErrorCode object
         * @return
         *          the corresponding NexErrorCode object or <i>null</i> if
         *          an invalid code was passed.
         */
        public static NexErrorCode fromIntegerValue( int code ) {
            for( int i=0; i<NexErrorCode.values().length; i++ ) {
                if( NexErrorCode.values()[i].mCode == code )
                    return NexErrorCode.values()[i];
            }
            return NexErrorCode.values()[23]; // Unknown Error.
        }
    }
    
    /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. */
    public static final int NEXPLAYER_STATUS_REPORT_NONE                    = 0x0;
    /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. */
    public static final int NEXPLAYER_STATUS_REPORT_AUDIO_GET_CODEC_FAILED  = 0x1;
    /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. */
    public static final int NEXPLAYER_STATUS_REPORT_VIDEO_GET_CODEC_FAILED  = 0x2;
    /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. */
    public static final int NEXPLAYER_STATUS_REPORT_AUDIO_INIT_FAILED       = 0x3;
    /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. */
    public static final int NEXPLAYER_STATUS_REPORT_VIDEO_INIT_FAILED       = 0x4;
    /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. */
    public static final int NEXPLAYER_STATUS_REPORT_TRACK_CHANGED           = 0x5;
    /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. */
    public static final int NEXPLAYER_STATUS_REPORT_STREAM_CHANGED          = 0x6;
    /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. */
    public static final int NEXPLAYER_STATUS_REPORT_DSI_CHANGED             = 0x7;
    /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. */
    public static final int NEXPLAYER_STATUS_REPORT_OBJECT_CHANGED          = 0x8;
    /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. */
    public static final int NEXPLAYER_STATUS_REPORT_CONTENT_INFO_UPDATED    = 0x9;
    /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. */
    public static final int NEXPLAYER_STATUS_REPORT_AVMODE_CHANGED          = 0xa;
    /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. */
    public static final int NEXPLAYER_STATUS_REPORT_HTTP_INVALID_RESPONSE   = 0xb;
    /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. */
    public static final int NEXPLAYER_STATUS_REPORT_MAX                     = 0xFFFFFFFF;
    
    //public byte[] pucAudioPCM = new byte[8192];
    
    static
    {
        /*
         * Load the library. If it's already loaded, this does nothing.
         */
        System.loadLibrary("ViewRightWebClient");
        System.loadLibrary("nexplayerengine");
        Log.d(TAG,"Loading nexplayerengine.");
    }   
    
    /** return value of getPlatformInfo() for checking Android Version  
     *  not supported platform.
     */
    private static final int NEX_SUPPORT_PLATFORM_NOTHING = 0x0;
    
    /** return value of getPlatformInfo() for checking Android Version
     * Cupcake  
     */
    private static final int NEX_SUPPORT_PLATFORM_CUPCAKE = 0x15;
    /** return value of getPlatformInfo() for checking Android Version  
     *  Donut
     */
    private static final int NEX_SUPPORT_PLATFORM_DONUT = 0x16;
    /** return value of getPlatformInfo() for checking Android Version
     *  Eclair  
     */
    private static final int NEX_SUPPORT_PLATFORM_ECLAIR = 0x21;
    /** return value of getPlatformInfo() for checking Android Version
     *  Froyo  
     */
    private static final int NEX_SUPPORT_PLATFORM_FROYO = 0x22;
    /** return value of getPlatformInfo() for checking Android Version
     *  Gingerbread  
     */
    private static final int NEX_SUPPORT_PLATFORM_GINGERBREAD = 0x30;
    /** return value of getPlatformInfo() for checking Android Version
     *  Honeycomb  
     */ 
    private static final int NEX_SUPPORT_PLATFORM_HONEYCOMB = 0x31;
    /**
     *  return value of getPlatformInfo() for checking Android Version
     *  Icecream Sandwich
    */    
    private static final int NEX_SUPPORT_PLATFORM_ICECREAM_SANDWICH = 0x40;
    
    /** return Android Version
     */
    private int getPlatformInfo()
    {
        int iPlatform = 0;
        
        String strVersion =android.os.Build.VERSION.RELEASE;
        
        
        Log.d(TAG, "PLATFORM INFO: " + strVersion);
        
        
        if(strVersion.startsWith("1.5"))
        {
            iPlatform = NEX_SUPPORT_PLATFORM_CUPCAKE;
        }
        else if( strVersion.startsWith("1.6"))
        {
            iPlatform = NEX_SUPPORT_PLATFORM_DONUT;
        }
        else if( strVersion.startsWith("2.1"))
        {
            iPlatform = NEX_SUPPORT_PLATFORM_ECLAIR;
        }
        else if( strVersion.startsWith("2.2"))
        {
            iPlatform = NEX_SUPPORT_PLATFORM_FROYO;
        }
        else if( strVersion.startsWith("2.3"))
        {
            iPlatform = NEX_SUPPORT_PLATFORM_GINGERBREAD;
        }
        else if( strVersion.startsWith("3."))
        {
            iPlatform = NEX_SUPPORT_PLATFORM_HONEYCOMB;
        }
        else if( strVersion.startsWith("4."))
        {
            iPlatform = NEX_SUPPORT_PLATFORM_ICECREAM_SANDWICH;
        }
        else
        {
            iPlatform = NEX_SUPPORT_PLATFORM_NOTHING;
        }
        
        
        return iPlatform;
    }
    
    /** A special debugging value for the <code>strModel</code> parameter of {@link NexPlayer#init}.  For testing only, not for release code.  See that method description for details.*/
    public static final String NEX_DEVICE_USE_ONLY_NEX = "Nex";
    /** A special debugging value for the <code>strModel</code> parameter of {@link NexPlayer#init}.  For testing only, not for release code.  See that method description for details.*/
    public static final String NEX_DEVICE_USE_ONLY_ANDROID = "Android";
    /** A special debugging value for the <code>strModel</code> parameter of {@link NexPlayer#init}.  For testing only, not for release code.  See that method description for details.*/
    public static final String NEX_DEVICE_USE_NEX_ANDROID = "Nex Android";
    /** A special debugging value for the <code>strModel</code> parameter of {@link NexPlayer#init}.  For testing only, not for release code.  See that method description for details.*/
    public static final String NEX_DEVICE_USE_ANDROID_NEX = "Android Nex";
    /** A special debugging value for the <code>strModel</code> parameter of {@link NexPlayer#init}.  For testing only, not for release code.  See that method description for details.*/
    public static final String NEX_DEVICE_USE_JAVA = "JAVA";
    /** A special debugging value for the <code>strModel</code> parameter of {@link NexPlayer#init}.  For testing only, not for release code.  See that method description for details.*/
    public static final String NEX_DEVICE_USE_OPENGL = "OPENGL";
    /** A special debugging value for the <code>strModel</code> parameter of {@link NexPlayer#init}.  For testing only, not for release code.  See that method description for details.*/
    public static final String NEX_DEVICE_USE_ANDROID_3D = "Android 3D";
    
    private String getDeviceInfo()
    {
        return android.os.Build.MODEL;
    }
    
    /**
     * Sole constructor for NexPlayer&trade;.
     * 
     * After constructing a NexPlayer object, you <i>must</i> call
     * {@link NexPlayer#init} before you can call any other methods
     */
    public NexPlayer( )
    {
        mNexPlayerInit = false;
    }   
    
    /**
     * Determines if NexPlayer&trade;&nbsp; is currently initialized.
     * 
     * To initialize NexPlayer&trade;, {@link NexPlayer#init init} must be called. If that
     * method returns \c true, then this method will also return
     * \c true if called on the same instance of NexPlayer&trade;.
     * 
     * In some cases, it is necessary to call NexPlayer&trade;&nbsp; functions from event handlers
     * in subclasses of {@link Activity} (such as <code>onPause</code> or <code>onStop</code>).
     * In such event handlers, it is possible for them to be called before code that
     * initializes NexPlayer&trade;&nbsp;, or for them to be called after a failed initialization.  Therefore,
     * any calls to NexPlayer&trade;&nbsp;methods made from <code>onPause</code> or similar event handlers
     * must be protected as follows: 
     * <pre><code>if( nexPlayer.isInitialized() ) 
     * {
     *     // Calls to other methods are safe here 
     * }</code></pre>
     * 
     * @return \c true if NexPlayer&trade;&nbsp; is currently initialized
     */
    public boolean isInitialized() {
        return mNexPlayerInit;
    }
    
    /**
     * Initializes NexPlayer&trade;. This must be called before any
     * other methods.
     * 
     * @param context       The current context; from Activity subclasses, you can
     *                      just pass <code>this</code>.
     * @param strModel      Device model name.  NexPlayer&trade;&nbsp; includes multiple renderer
     *                      modules, and will attempt to select the module most suitable
     *                      to the device based on this value. 
     *                      Under normal (production) use, you should pass the MODEL
     *                      as available via the Android API in {@link android.os.Build.MODEL}.  
     *                      For example:
     * <pre><code>
     * nexPlayer.init(this, android.os.Build.MODEL, 0, 1);
     * </code></pre>
     *                      NexPlayer&trade;&nbsp; uses this to select the most appropriate renderer.  For
     *                      Froyo and higher, this is always the OpenGL renderer.
     *                      There are special values you can pass to force the use of a specific 
     *                      renderer.  This can be useful if your application doesn't implement
     *                      support for the OpenGL renderer.  This is also currently the <em>only</em> way
     *                      to use the Java renderer.  On Honeycomb, only \c NEX_DEVICE_USE_JAVA and \c NEX_DEVICE_USE_OPENGL
     *                      are supported. Possible values (which may change in future versions) are:
     *                          - <b>{@link NexPlayer#NEX_DEVICE_USE_ONLY_NEX NEX_DEVICE_USE_ONLY_NEX}
     *                          ("Nex")</b> Use only NexPlayer's high-performance direct-to-surface renderer.
     *                          - <b>{@link NexPlayer#NEX_DEVICE_USE_ONLY_ANDROID NEX_DEVICE_USE_ONLY_ANDROID}
     *                          ("Android")</b> Use only standard Android API bitmaps to display frames.  This
     *                          is usually slower, but is more portable.
     *                          - <b>{@link NexPlayer#NEX_DEVICE_USE_NEX_ANDROID NEX_DEVICE_USE_NEX_ANDROID}
     *                          ("Nex Android")</b> Attempt to use Nexplayer's direct-to-surface renderer if possible, but
     *                          fall back to the Android renderer if necessary.
     *                          - <b>{@link NexPlayer#NEX_DEVICE_USE_ANDROID_NEX NEX_DEVICE_USE_ANDROID_NEX}
     *                          ("Android Nex")</b> Attempt to use the Android renderer, but fall back to NexPlayer's
     *                          direct-to-surface renderer if necessary.
     *                          - <b>{@link NexPlayer#NEX_DEVICE_USE_JAVA NEX_DEVICE_USE_JAVA}
     *                          ("JAVA")</b> Use the Java renderer.
     *                          - <b>{@link NexPlayer#NEX_DEVICE_USE_OPENGL NEX_DEVICE_USE_OPENGL}
     *                          ("OPENGL")</b> Use the OpenGL renderer.
     *                          - <b>{@link NexPlayer#NEX_DEVICE_USE_ANDROID_3D NEX_DEVICE_USE_ANDROID_3D}
     *                          ("Android 3D")</b>Use the 3D video renderer with standard Android API bitmaps.
     * @param logLevel      NexPlayerSDK logging level.  This affects the messages that the SDK writes to the
     *                      android log.
     *                          - <b>-1</b> : Do not output any log messages
     *                          - <b>0</b> : Output basic log messages only (recommended)
     *                          - <b>1~4</b> : Output detailed log messages; higher numbers result in more verbose
     *                                      log entries, but may cause performance issues in some cases and are
     *                                      not recommended for general release code.
     * @param colorDepth    Video output image color depth.
     *                          - <b>1</b> : RGBA_8888
     *                          - <b>4</b> : RGB_565
     * 
     * @return              \c true if initialization succeeded; \c false in the case of a 
     *                      failure (in the case of failure, check the log for details)
     */
    public boolean init( Context context, String strModel, int logLevel, int colorDepth) {
        Log.d(TAG, "Request to init player; current init status=" + mNexPlayerInit);
        
        int iCPUInfo = 0;
        int iPlatform = 0;
        int iStartIndex = 0;
        int iPackageNameLength = 0;
        String strDeviceModel = "";
        
        String strPackageName = context.getApplicationContext().getPackageName();
        
        File fileDir = context.getFilesDir();
        if( fileDir == null)
            throw new IllegalStateException("No files directory - cannot play video - relates to Android issue: 8886!");
        String strPath = fileDir.getAbsolutePath();
        
        String strLibPath = "";
        
        iPackageNameLength = strPackageName.length();
        iStartIndex = strPath.indexOf(strPackageName);
        
        iCPUInfo = NexSystemInfo.getCPUInfo();
        iPlatform = getPlatformInfo();
        
        if(strModel == null)
        {
            strDeviceModel = getDeviceInfo();
        }
        else
        {
            strDeviceModel = strModel;
        }
        
        if( iPlatform == NEX_SUPPORT_PLATFORM_CUPCAKE )
            iCPUInfo = NexSystemInfo.NEX_SUPPORT_CPU_ARMV5;
        
        strLibPath = strPath.substring(0, iStartIndex + iPackageNameLength) + "/";
        
        Log.d(TAG, "PackageName : " + strPackageName);
        Log.d(TAG, "Files Dir : " + strPath);
        Log.d(TAG, "LibPath :" + strLibPath);
        Log.d(TAG, "CPUINFO :" + iCPUInfo + " SDKINFO : " + iPlatform);
        Log.d(TAG, "Model : " + strDeviceModel);
        Log.d(TAG, "Log Level : " + logLevel);
        
        if( !mNexPlayerInit ) {
            int nRet = _Constructor( new WeakReference<NexPlayer>( this ), 
                                    context.getApplicationContext().getPackageName(),
                                    strLibPath,
                                    iPlatform,
                                    iCPUInfo,
                                    strDeviceModel,
                                    logLevel,
                                    colorDepth);
            if( nRet == 0 ) {
                mNexPlayerInit = true;
                Log.d( TAG, "Init success!" );
            } else {
                Log.d( TAG, "Init failure: " + nRet );
            }
        }
        return mNexPlayerInit;
    }
    
    /**
     * Registers a callback that will be invoked when new events occur.<p>
     * 
     * The events dispatched to this callback interface serve three functions:<p>
     * 
     * <ul>
     * <li>Provide new video and audio data to the application,
     *      for the application to present to the user.</li>
     * <li>Notify the application when a command has completed,
     *      so that the application can issue any follow-up
     *      commands.  For example, issuing {@link NexPlayer#start(int) start} 
     *      when {@link NexPlayer#open open} has completed</li>
     * <li>Notify the application when there are state changes that the
     *      application may wish to reflect in the interface.</li>
     * </ul>
     * 
     * All applications <i>must</i> implement
     * this callback and provide certain minimal functionality. See the
     * {@link NexPlayer.IListener IListener} documentation for a list of 
     * events and information on implementing them.<p> 
     * 
     * In an Android application, there are two common idioms for implementing
     * this.  The most typical is to have the <code>Activity</code> subclass 
     * implement the <code>IListener</code> interface.<p>
     * 
     * The other approach is to define an anonymous class in-line:
     * <pre>
     * mNexPlayer.setListener(new NexPlayer.IListener() {
     *
     *     &#064;Override
     *     public void onVideoRenderRender(NexPlayer mp) {
     *         // ...event implementaton goes here...
     *     }
     *     
     *     // ...other methods defined by the interface go here...
     * });</pre>
     * 
     * @param listener
     *            the object on which methods will be called when new events occur.
     *            This must implement the <code>IListener</code> interface.
     */
    public void setListener( IListener listener )
    {
        if( !mNexPlayerInit )
            Log.d(TAG, "Attempt to call setListered() but player not initialized; call NexPlayer.init() first!");
        mListener = listener;
    }
    
    /**
     * Begins opening the media at the specified path or URL.  This supports both
     * local content and streaming content.  This is an asynchronous operation that
     * will run in the background (even for local content).
     * 
     * When this operation completes,
     * {@link NexPlayer.IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}
     * is called with one of the following command constants (depending on the \c type
     * specified in the \c open call):
     *  - {@link NexPlayer#NEXPLAYER_ASYNC_CMD_OPEN_LOCAL NEXPLAYER_ASYNC_CMD_OPEN_LOCAL}
     *  - {@link NexPlayer#NEXPLAYER_ASYNC_CMD_OPEN_STREAMING NEXPLAYER_ASYNC_CMD_OPEN_STREAMING}
     * 
     * Success or failure of the operation can be determined by checking the \c result
     * argument passed to \c onAsyncCmdComplete.  If the result is 0, the media was
     * successfully opened; if it is any other value, the operation failed.
     * 
     * Calls to \c open must be matched with calls to \c close.
     * 
     * @param path
     *          The location of the content; a path (for local content) or URL (for remote content)
     * @param smiPath
     *          path to a local subtitle file, or \c null for no subtitles.  For streaming content
     *          that already includes subtitles, this should be \c null (using both types of subtitles
     *          at the same time will cause undefined behavior).          
     * @param type
     *          Determines how the path argument is interpreted.  This will be one of:
     *              - {@link NexPlayer#NEXPLAYER_SOURCE_TYPE_LOCAL_NORMAL NEXPLAYER_SOURCE_TYPE_LOCAL_NORMAL} 
     *                  to play local media (the path is a local filesystem path)
     *              - {@link NexPlayer#NEXPLAYER_SOURCE_TYPE_STREAMING NEXPLAYER_SOURCE_TYPE_STREAMING}
     *                  to play remote media sources (including RTSP streaming, 
     *                  progressive download and HTTP Live streaming).  The path is
     *                  interpreted as an URL. \n
     *          Other \c NEXPLAYER_SOURCE_* values are not 
     *                supported in this version and should not be used.
     * @param transportType
     *          The network transport type to use on the connection.  This will be one of:
     *              - {@link NexPlayer#NEXPLAYER_TRANSPORT_TYPE_TCP NEXPLAYER_TRANSPORT_TYPE_TCP}
     *              - {@link NexPlayer#NEXPLAYER_TRANSPORT_TYPE_UDP NEXPLAYER_TRANSPORT_TYPE_UDP}
     * @param bufferingTime
     *          The number of milliseconds of media to buffer before beginning
     *          playback.
     *                
     * @return the status of the operation; this is zero in the case of success, or
     *          a non-zero NexPlayer error code in the case of failure.
     *
     *          \note This only indicates the success or failure of starting the operation.
     *          Even if this reports success, the operation may still fail later,
     *          asynchronously, in which case the application is notified in
     *          \c onAsyncCmdComplete.
     */
    public native int open( String path, String smiPath, int type, int transportType, int bufferingTime );
    
    
    /**
     * Start playing from the specified timestamp.
     * 
     * The media must have already been successfully opened with
     * {@link NexPlayer#open open}.  This only works
     * for media that is in the stopped state; to change the play
     * position of media that is currently playing or paused, call
     * {@link NexPlayer#seek(int) seek} or {@link NexPlayer#seekTo(int, int) seekTo}
     * instead.
     * 
     * When this operation completes,
     * {@link NexPlayer.IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}
     * is called with one of the following command constants (depending on the <code>type</code>
     * specified in the <code>open</code> call):<p>
     * <ul> 
     * <li>{@link NexPlayer#NEXPLAYER_ASYNC_CMD_START_LOCAL NEXPLAYER_ASYNC_CMD_START_LOCAL}</li>
     * <li>{@link NexPlayer#NEXPLAYER_ASYNC_CMD_START_STREAMING NEXPLAYER_ASYNC_CMD_START_STREAMING}</li>
     * </ul>
     * 
     * Success or failure of the operation can be determined by checking the <code>result</code>
     * argument passed to <code>onAsyncCmdComplete</code>.  If the result is 0, the media was
     * successfully opened; if it is any other value, the operation failed.<p>
     * 
     * @param msec
     *            : offset (in milliseconds) from the beginning of the media
     *              at which to start playback; zero to start at the beginning.
     *              
     * @return the status of the operation; this is zero in the case of success, or
     *          a non-zero NexPlayer error code in the case of failure.<p>
     *          Note that
     *          this only indicates the success or failure of starting the operation.
     *          Even if this reports success, the operation may still fail later,
     *          asynchronously, in which case the application is notified in
     *          <code>onAsyncCmdComplete</code>.
     */
    public native int start( int msec );
    
    /** This function pauses the current playback. 
     * @return Zero for success, or a non-zero NexPlayer error code in the event of a failure.
     */
    public native int pause();
    
    /**
     * This function resumes playback beginning at the point at which the player
     * was last paused.
     * @return Zero for success, or a non-zero NexPlayer error code in the event of a failure.
     */
    public native int resume();
    
    /**
     * Seeks the playback position to a specific time.  Doesn't work if
     * NexPlayer is stopped or if the stream doesn't support seeking, but 
     * does work if NexPlayer is playing or paused.
     * 
     * @param msec
     *            : offset in milliseconds from the beginning of the media
     * @return Zero for success, or a non-zero NexPlayer error code in the event of a failure.
     */
    public native int seek( int msec );
    
    /** This function stops the current playback. 
     * 
     * @return Zero for success, or a non-zero NexPlayer error code in the event of a failure.
     */
    public native int stop();
    
    /**
     * This function ends all the work on the content currently opened and
     * closes content data.  The content must be stopped before calling 
     * this method.<p>
     * 
     * The correct way to finish playing content is to either wait for the
     * end of content, or to call <code>stop</code> and wait for the stop
     * operation to complete, then call <code>close</code>. 
     * 
     * @return Zero for success, or a non-zero NexPlayer error code in the event of a failure.
     */
    public native int close();
    
    /**
     * Retrieves the current state of NexPlayer&trade;.
     * 
     * Calling methods such as {@link NexPlayer#open open}
     * and {@link NexPlayer#start start} does not immediately change the
     * state.  The state changes asynchronously, and the new state goes
     * into effect at the same time 
     * {@link NexPlayer.IListener#onAsyncCmdComplete(NexPlayer, int, int, int, int) onAsyncCmdComplete}
     * is called to notify the application.<p>
     * 
     * State progresses according to the following chart:<p>
     * 
     * <img src="doc-files/nexplayer_state.jpeg" />
     * 
     * @return a constant indicating the current state.  This is one 
     *          of the following values:<p>
     *          <ul>
     *          <li>{@link NexPlayer#NEXPLAYER_STATE_CLOSED}</li>
     *          <li>{@link NexPlayer#NEXPLAYER_STATE_NONE}</li>
     *          <li>{@link NexPlayer#NEXPLAYER_STATE_PAUSE}</li>
     *          <li>{@link NexPlayer#NEXPLAYER_STATE_PLAY}</li>
     *          <li>{@link NexPlayer#NEXPLAYER_STATE_STOP}</li>
     *          </ul>
     * 
     */
    public native int getState();
    
    // for Recording
    /** Recording interface; not available in current version.
     * @deprecated Not available in current version; do not use. */
    public native int recStart( String path, int maxsize );
    /** Recording interface; not available in current version.
     * @deprecated Not available in current version; do not use. */
    public native int recPause();
    /** Recording interface; not available in current version.
     * @deprecated Not available in current version; do not use. */
    public native int recResume();
    /** Recording interface; not available in current version.
     * @deprecated Not available in current version; do not use. */
    public native int recStop();
    
    // for TimeShift
    /** Timeshift interface; not available in current version.
     * @deprecated Not available in current version; do not use. */
    public native int timeStart( String AudioFile, String VideoFile, int maxtime, int maxfilesize );
    /** Timeshift interface; not available in current version.
     * @deprecated Not available in current version; do not use. */
    public native int timeResume();
    /** Timeshift interface; not available in current version.
     * @deprecated Not available in current version; do not use. */
    public native int timeStop();
    /** Timeshift interface; not available in current version.
     * @deprecated Not available in current version; do not use. */
    public native int timePause();
    /** Timeshift interface; not available in current version.
     * @deprecated Not available in current version; do not use. */
    public native int timeBackward( int skiptime );
    /** Timeshift interface; not available in current version.
     * @deprecated Not available in current version; do not use. */
    public native int timeForward( int skiptime );
    
    /**
     * This function retrieves the information on the currently
     * open content.
     * 
     * @param info
     *            : content information class object
     */
    private native int getInfo( Object info );
    
    
    /**
     * Returns the name of a CSS class used in the current SMI subtitles file.<p>
     * 
     * Each SMI subtitle file uses CSS classes to differentiate between the available
     * subtitle tracks within the file.<p>
     * 
     * The total number of available subtitle tracks
     * is passed as an argument to onTextRenderInit(), and the CSS class name of an
     * individaul track can be determined by passing the index (zero based) of the track
     * to this function.<p>
     * 
     * Note that CSS class names are the internal names used in the SMI file to
     * identify a track and they should not be displayed directly to the user.<p>
     * 
     * The results of calling this function with an out-of-range value are undefined;
     * it should be called only with values between 0 and n-1 where n is the number 
     * of tracks passed to onTextRenderInit().<p>
     * 
     * For example, to list the CSS classes of all subtitle tracks in a file:<p>
     * 
     * <pre>
     * public void onTextRenderInit(NexPlayer mp, int trackCount) {
     *     for(int i=0; i&lt;trackCount; i++) {
     *         Log.d(LOG_TAG, "ClassName[" + i + "] = " + mp.getSMIClassInfo(i));
     *     }
     * }    
     * </pre>
     * 
     * @deprecated This is no longer supported; use {@link NexContentInformation#mCaptionLanguages} instead.
     * 
     * @param nIndex
     *            : 0-based index of the track
     * @return The name of the CSS class for the specified index; undefined if the
     *              index is out of range.
     */
    public native String getSMIClassInfo( int nIndex ); 
    
    /**
     * Retrieves information on the content that is currently open.<p>
     * 
     * <b>NOTE:</b> The {@link NexPlayer#getContentInfoInt(int) getContentInfoInt} function
     * also returns information on the current content.  In some cases, the same informtion
     * is available through both functions.  However, some items are available only through
     * one of the functions.<p>
     * 
     * <b>PERFORMANCE NOTE:</b> This allocates a new instance of <code>ContentInformation</code>
     * every time it is called, which may place a burden on the garbage collector in some cases.
     * If you need to access multiple fields, save the returned object in a variable. For cases
     * that are particularly sensitive to performance, selected content information is available
     * through {@link NexPlayer#getContentInfoInt(int) getContentInfoInt(int)}, which doesn't allocate
     * any objects.<p>
     * 
     * @return A {@link NexContentInformation} object containing information on the currently open content.
     * @see {@link NexPlayer#getContentInfoInt(int)}
     */
    public NexContentInformation getContentInfo()
    {
        NexContentInformation info = new NexContentInformation();
        
        getInfo( info );
        
        return info;
    }
    
    /**
     * Retrieves the specified content information item.  In most cases, this is equivalent
     * to calling {@link NexPlayer#getContentInfo()} and accessing an indivudal
     * field in the return value.  However, there are a few items that are only available
     * through this method, and for items available through both methods, this one may
     * be more efficient in certain cases.  See
     * <code>getContentInfo</code> for more information.<p>
     * 
     *   Certain fields (such as the list of tracks) are only
     * available through the full structure, and
     * certain fields (such as frames displayed per second) are only available
     * here.<p>
     * 
     * <b>Content Info Indexes:</b> The following integer constants 
     * identify different content information items that are available; they
     * are passed in the <code>info_index</code> argument to specify which
     * content information item the caller is interested in:
     * <ul>
     * <li><b>Also available in <code>NexContentInfo</code>:</b><br>&nbsp;<ul>
     * <li><b>CONTENT_INFO_INDEX_MEDIA_TYPE (0)</b> Same as the <code>mMediaType</code> member of <code>NexContentInfo</code></li>
     * <li><b>CONTENT_INFO_INDEX_MEDIA_DURATION (1)</b> Same as the <code>mMediaDuration</code> member of <code>NexContentInfo</code></li>
     * <li><b>CONTENT_INFO_INDEX_VIDEO_CODEC (2)</b> Same as the <code>mVideoCodec</code> member of <code>NexContentInfo</code></li>
     * <li><b>CONTENT_INFO_INDEX_VIDEO_WIDTH (3)</b> Same as the <code>mVideoWidth</code> member of <code>NexContentInfo</code></li>
     * <li><b>CONTENT_INFO_INDEX_VIDEO_HEIGHT (4)</b> Same as the <code>mVideoHeight</code> member of <code>NexContentInfo</code></li>
     * <li><b>CONTENT_INFO_INDEX_VIDEO_FRAMERATE (5)</b> Same as the <code>mVideoFrameRate</code> member of <code>NexContentInfo</code></li>
     * <li><b>CONTENT_INFO_INDEX_VIDEO_BITRATE (6)</b> Same as the <code>mVideoBitRate</code> member of <code>NexContentInfo</code></li>
     * <li><b>CONTENT_INFO_INDEX_AUDIO_CODEC (7)</b> Same as the <code>mAudioCodec</code> member of <code>NexContentInfo</code></li>
     * <li><b>CONTENT_INFO_INDEX_AUDIO_SAMPLINGRATE (8)</b> Same as the <code>mAudioSamplingRate</code> member of <code>NexContentInfo</code></li>
     * <li><b>CONTENT_INFO_INDEX_AUDIO_NUMOFCHANNEL (9)</b> Same as the <code>mAudioNumOfChannel</code> member of <code>NexContentInfo</code></li>
     * <li><b>CONTENT_INFO_INDEX_AUDIO_BITRATE (10)</b> Same as the <code>mAudioBitRate</code> member of <code>NexContentInfo</code></li>  
     * <li><b>CONTENT_INFO_INDEX_MEDIA_ISSEEKABLE (11)</b> Same as the <code>mIsSeekable</code> member of <code>NexContentInfo</code></li>
     * <li><b>CONTENT_INFO_INDEX_MEDIA_ISPAUSABLE (12)</b> Same as the <code>mIsPausable</code> member of <code>NexContentInfo</code></li>
     * </ul></li>
     * <li><b>Video Performance Infomration (Available only via <code>getContentInfoInt</code>):</b><br>
     * The NexPlayer&trade; engine reads frames from the content, then decodes and displays
     * each frame.  If the device is not powerful enough for the resolution or bitrate being played, the decode or
     * display of some frames may be skipped in order to maintain synchronization with the audio track.  The values
     * of the parameters in this section provide information about the number of frames actually being displayed.
     * Per-second averages are calculated every two seconds (although this interval may change in future releases).
     * Frame counts reset at the same interval, so the ratio is generally more meaningful than the
     * actual numbers (since the interval may change).  Running totals are also provided, and are updated
     * at the same interval. If you wish to perform your own calculations or average over other intervals, you can
     * periodically sample the running totals.  Running totals are reset when new content is opened.<br>&nbsp; <ul>
     * <li><b>CONTENT_INFO_INDEX_VIDEO_RENDER_AVE_FPS (13)</b> Average number of video frames per second decoded.</li>
     * <li><b>CONTENT_INFO_INDEX_VIDEO_RENDER_AVE_DSP (14)</b> Average number of video frames per second actually displayed.</li>
     * <li><b>CONTENT_INFO_INDEX_VIDEO_RENDER_COUNT (15)</b> Number of video frames displayed </li>
     * <li><b>CONTENT_INFO_INDEX_VIDEO_RENDER_TOTAL_COUNT (16)</b> Total number of video frames displayed.</li>
     * <li><b>CONTENT_INFO_INDEX_VIDEO_CODEC_DECODING_COUNT (17)</b> Number of video frames decoded during the last interval. </li>
     * <li><b>CONTENT_INFO_INDEX_VIDEO_CODEC_DECODING_TOTAL_COUNT (18)</b> Total number of video frames decoded.</li>
     * </ul></li>
     * </ul>
     * 
     * 
     * 
     * @param info_index the integer index of the content information item to return.
     *          This is one of the <code>CONTENT_INFO_INDEX_*</code> constants described
     *          above.
     * @return the integer value of the requested content information item.
     * @see {@link NexPlayer#getContentInfo()}
     * @see {@link NexContentInformation}
     */
    public native int getContentInfoInt( int info_index );
    
    /**
     * Sets the value of an individual NexPlayer property.<p>
     * 
     * Properties control the behavior of NexPlayer and the featuers 
     * that are enabled.<p>
     * 
     * This sets integer properties; use the {@link NexPlayer#setProperty(NexProperty, String) setProperty(NexProperty, String)}
     * version of this method for string properties.<p>
     * 
     * See {@link NexProperty} for details.<p>
     * 
     * @param property  The property to set
     * @param value     The new value for the property
     * @return          zero if the property was succesfully set; non-zero if there was an error
     */
    public int setProperty (NexProperty property, int value) {
        return setProperties( property.getPropertyCode(), value );
    }
    
    /**
     * Sets the value of an individual NexPlayer property.<p>
     * 
     * Properties control the behavior of NexPlayer and the featuers 
     * that are enabled.<p>
     * 
     * This sets string properties; use the {@link NexPlayer#setProperty(NexProperty, int) setProperty(NexProperty, int)}
     * version of this method for integer properties.<p>
     * 
     * See {@link NexProperty} for details.<p>
     * 
     * @param property  The property to set
     * @param value     The new string value for the property
     * @return          zero if the property was succesfully set; non-zero if there was an error
     */
    public int setProperty (NexProperty property, String value) {
        return setProperties( property.getPropertyCode(), value );
    }   
    
    /**
     * Gets the value of an individual NexPlayer integer property.<p>
     * 
     * Properties control the behavior of NexPlayer and the featuers 
     * that are enabled.<p>
     * 
     * This gets integer properties; for string properties, use
     * {@link NexPlayer#getStringProperty(NexProperty) getStringProperty}
     * instead.<p>
     * 
     * See {@link NexProperty} for details.<p>
     * 
     * @param property  The property to get
     * @return          value of the property
     */
    public int getProperty (NexProperty property) {
        return getProperties( property.getPropertyCode() );
    }
    
    /**
     * Gets the string value of an individual NexPlayer property.<p>
     * 
     * Properties control the behavior of NexPlayer and the featuers 
     * that are enabled.<p>
     * 
     * This gets string properties; for integer properties, use
     * {@link NexPlayer#getProperty(NexProperty) getProperty}
     * instead.<p>
     * 
     * See {@link NexProperty} for details.<p>
     * 
     * @param property  The property to get
     * @return          String value of the property
     */
    public String getStringProperty (NexProperty property) {
        return getStringProperties( property.getPropertyCode() );
    }   
    
    /**
     * Sets the value of an individual NexPlayer integer property based on the
     * numerical ID of the property.<p>
     * 
     * Normally, {@link NexPlayer#setProperty(NexProperty, int) setProperty} should
     * be used instead of this method.  Use this method only if you have a numeric 
     * property code.<p>
     * 
     * For a full list of properties, see the {@link NexPlayer.NexProperty NexProperty}
     * enum.  To get the numeric code for a property, call the <code>getPropertyCode</code>
     * method on the enum member.<p>
     * 
     * For example:<p>
     * 
     * <pre>
     * setProperties(
     *         NexProperty.SUPPORT_RTSP.getPropertyCode(),
     *         1  // enable RTSP support
     *         );
     * </pre>
     * 
     * @param property  numeric property code identifying property to set
     * @param value     new value for the property
     * @return          zero if the property was set successfully; non-zero 
     *                  if there was an error
     */
    public native int setProperties( int property, int value );
    
    /**
     * Sets the value of an individual NexPlayer string property based on the
     * numerical ID of the property.<p>
     * 
     * This is a string version of {@link NexPlayer#setProperties(int, int) setProperties(int, int)}.<p>
     * 
     * Normally, {@link NexPlayer#setProperty setProperty} should
     * be used instead of this method.  Use this method only if you have a numeric 
     * property code.<p>
     * 
     * @param property  numeric property code identifying property to set
     * @param value     new string value for the property
     * @return          zero if the property was set successfully; non-zero 
     *                  if there was an error
     */
    public native int setProperties( int property, String value );  
    
    /**
     * Gets the value of an individual NexPlayer property based on the
     * numerical ID of the property.<p>
     * 
     * Normally, {@link NexPlayer#getProperty(NexProperty) getProperty} should
     * be used instead of this method.  Use this method only if you have a numeric 
     * property code.<p>
     * 
     * For a full list of properties, see the {@link NexPlayer.NexProperty NexProperty}
     * enum.  To get the numeric code for a property, call the <code>getPropertyCode</code>
     * method on the enum member.<p>
     * 
     * For example:<p>
     * 
     * <pre>
     * int supportRTSP = 
     *     getProperties(
     *         NexProperty.SUPPORT_RTSP.getPropertyCode() 
     *         );
     * </pre>
     * 
     * @param property  numeric property code identifying property to get
     * @return          the value of the property
     */
    public native int getProperties( int property );
    
    /**
     * Gets the string value of an individual NexPlayer property based on the
     * numerical ID of the property.<p>
     * 
     * Normally, {@link NexPlayer#getStringProperty(NexProperty) getStringProperty} should
     * be used instead of this method.  Use this method only if you have a numeric 
     * property code.<p>
     * 
     * For a full list of properties, see the {@link NexPlayer.NexProperty NexProperty}
     * enum.  To get the numeric code for a property, call the <code>getPropertyCode</code>
     * method on the enum member.<p>
     * 
     * For example:<p>
     * 
     * <pre>
     * String userAgent = 
     *     getProperties(
     *         NexProperty.USERAGENT_STRING.getPropertyCode() 
     *         );
     * </pre>
     * 
     * @param property  numeric property code identifying property to get
     * @return          the string value of the property
     */
    public native String getStringProperties( int property );   
    
    /**
     * This function adds an RTSP header to be included with all future
     * RTSP requests.<p>
     * 
     * RTSP headers have the same format as HTTP headers,
     * but the set of field names is different.<p>
     * 
     * There are several request types that are part of the RTSP protocol,
     * and when a header is added, you must specify with which request types
     * it will be included.  This is done by performing a bitwise OR on one
     * or more of the following values, and specifying the result in the 
     * <i>methods</i> parameter:<p>
     * 
     * <ul>
     * <li><b>RTSP_METHOD_DESCRIBE</b><br />
     * <li><b>RTSP_METHOD_SETUP</b><br />
     * <li><b>RTSP_METHOD_OPTIONS</b><br />
     * <li><b>RTSP_METHOD_PLAY</b><br />
     * <li><b>RTSP_METHOD_PAUSE</b><br />
     * <li><b>RTSP_METHOD_GETPARAMETER</b><br />
     * <li><b>RTSP_METHOD_TEARDOWN</b><br />
     * <li><b>RTSP_METHOD_ALL</b><br />
     * </ul>
     *  
     * For example, to set a different user agent for the SETUP and PLAY requests:<p>
     * 
     * <pre>
     * addRTSPHeaderFields( 
     *     RTSP_METHOD_SETUP | RTSP_METHOD_PLAY,
     *     "User-Agent: Nextreaming Android Player");
     * </pre>
     * 
     * @param methods   set of request methods to which this will 
     *                  apply (RTSP_METHOD_* constants OR-ed together)
     * @param str       actual header to add (including header name and value)
     * @return          zero if successful, non-zero if there was an error
     */
    public native int addRTSPHeaderFields( int methods, String str );
    
    
    /**
     * Adds additional header fields to be sent along with the HTTP headers
     * when sending streaming requests (HLS and Smooth Streaming).<p>
     * 
     * The string should contain a single valid HTTP header, include the
     * header name and value and delimiter.<p>
     * 
     * For example:<p>
     * <pre>
     * addHTTPHeaderFields("Cooki: Cooki test value.");
     * </pre>
     * 
     * To add muliple header fields, simply call this funciton multiple times.
     * 
     * @param str   The header (including delimeter) to add to future HTTP requests.
     * @return      zero if successful, non-zero if there was an error
     */
    public native int addHTTPHeaderFields( String str);
    
    /**
     * Control the playback speed by a given persent.
     * Doesn't work if NexPlayer is stopped.
     * 
     * @param iPlaySeed
     *            Range is -50 to 100 persent. 
     */
    public native int playspeedcontrol( int iPlaySeed);
    
    
    /** Experimental audio effect interface.
     * @deprecated Not available in current version; do not use. */
    public native int audiomaveninit();
    /** Experimental audio effect interface.
     * @deprecated Not available in current version; do not use. */
    public native int audiomavensetvolume( float fVolume);
    /** Experimental audio effect interface.
     * @deprecated Not available in current version; do not use. */
    public native int audiomavensetoutput( int uiOutputPath);
    /** Experimental audio effect interface.
     * @deprecated Not available in current version; do not use. */
    public native int audiomavensetparam( int uiMavenMode, int uiMavenStength, int uiBassStrength);
    /** Experimental audio effect interface.
     * @deprecated Not available in current version; do not use. */
    public native int audiomavenseqsetparam( int uiSEQMode, int [] iParam);
    /** Experimental audio effect interface.
     * @deprecated Not available in current version; do not use. */
    public native int audiomavenvms2setparam( int [] iParam);
    /** Experimental audio effect interface.
     * @deprecated Not available in current version; do not use. */
    public native int audiomavenvms2filtersetmode( int [] iVMSMode);
    /** Experimental audio effect interface.
     * @deprecated Not available in current version; do not use. */
    public native int audiomavenvms2filtersetparam( int [] iParam);

    /** Possible return value for NexPlayer::GetRenderMode */
    public static final int NEX_USE_RENDER_NEX_AND  = 0x00000001;
    /** Possible return value for NexPlayer::GetRenderMode */
    public static final int NEX_USE_RENDER_AND      = 0x00000002;
    /** Possible return value for NexPlayer::GetRenderMode */
    public static final int NEX_USE_RENDER_NEX      = 0x00000004;
    /** Possible return value for NexPlayer::GetRenderMode */
    public static final int NEX_USE_RENDER_AND_NEX  = 0x00000008;
    /** Possible return value for NexPlayer::GetRenderMode */
    public static final int NEX_USE_RENDER_JAVA     = 0x00000010;
    /** Possible return value for NexPlayer::GetRenderMode */
    public static final int NEX_USE_RENDER_OPENGL   = 0x00000020;
    /** Possible return value for NexPlayer::GetRenderMode */
    public static final int NEX_USE_RENDER_HW       = 0x00000040;           // JDKIM 2011/07/05
    
    /**
     * Sets a bitmap to be used to receive rendered frames for display, when
     * using the Java-based renderer.
     * 
     * For more information, see the <i>Java Renderer</i> section of
     * {@link com.nextreaming.nexplayerengine}.<p>
     * 
     * @param mFrameBitmap
     * @return Always 0, but may change in future versions.  The return value should be ignored.
     */
    public native int SetBitmap(Object mFrameBitmap);
    
    /**
     * \brief Informs NexPlayer&trade; of the current size of
     *        the GLSurfaceView subclass instance.
     *
     * This should be called whenever the size of the GLSurfaceView subclass
     * instance changes, as well as when the instance is initially created.  This
     * is because internally, OpenGL APIs use a different coordinate system, and
     * NexPlayer&trade; must know the pixel dimensions in order to map the OpenGL
     * coordinate system to per-pixel coordinates.
     *
     * \param width     Width of GLSurfaceView subclass instance, in pixels.
     * \param height    Height of GLSurfaceView subclass instance, in pixels.
     * \returns Always 0, but may change in future versions.  The return value should be ignored.
     */
    public native int GLInit(int width, int height);
    
    /**
     * \brief Draws in the current OpenGL context.
     *
     * \deprecated This remains public to support legacy code that implemented a GLSurfaceView
     *              subclass directly. However, new code should not call this method.  Instead,
     *              simply use the GLRenderer class provided with the NexPlayer&trade; SDK. That
     *              class automatically calls GLDraw when needed.
     *
     * \warning This <em>must</em> be called from the OpenGL renderer thread
     *          (the thread where \c GLSurfaceView.Renderer.onDrawFrame is called).
     *          Calling this from anywhere else will result in undefined behavior
     *          and possibly cause the application to crash.
     *
     * \param mode     The type of drawing operation to perform.
     *                  - <b>0:</b> Draw the most recent video frame
     *                  - <b>1:</b> Erase the surface to black
     * \returns Always 0, but may change in future versions.  The return value should be ignored.
     */
    public native int GLDraw(int mode);
    
    /**
     * \brief Returns the type of renderer in use by the NexPlayer&trade;&nbsp;engine.
     *
     * You must check the render mode using this method and adjust
     * the application behavior appropriately. For details see  
     * \ref javarenderer or \ref glrenderer.
     * 
     * When using the Java renderer (<code>NEX_USE_RENDERER_JAVA</code>), the application
     * must not call <code>setOutputPos</code> or <code>setDisplay</code>.  Doing so may
     * cause the application to crash if running under Honeycomb.<p>
     *
     * When using the OpenGL renderer (<code>NEX_USE_RENDERER_OPENGL</code>), the
     * application must not call <code>setDisplay</code>.<p>
     *
     * @return Render mode; one of:
     * - <b>NexPlayer::NEX_USE_RENDER_NEX_AND</b>
     *          Using only NexPlayer's high-performance 
     *          direct-to-surface renderer.
     * - <b>NexPlayer::NEX_USE_RENDER_AND</b>
     *          Using only standard Android API bitmaps 
     *          to display frames.
     * - <b>NexPlayer::NEX_USE_RENDER_NEX</b>
     *          Attempt to use Nexplayer's direct-to-surface 
     *          renderer if possible, but fall back to the Android 
     *          renderer if necessary
     * - <b>NexPlayer::NEX_USE_RENDER_AND_NEX</b>
     *          Attempt to use the Android renderer, but fall 
     *          back to NexPlayer's direct-to-surface renderer 
     *          if necessary
     * - <b>NexPlayer::NEX_USE_RENDER_JAVA</b>
     *          Don't render to the display.  Instead, each 
     *          frame is decoded and converted to the appropriate 
     *          color space, and then sent to the applicaton to display.
     * - <b>NexPlayer::NEX_USE_RENDER_OPENGL</b>
     *          Using OpenGL ES 2.0 to display frames.
     * - <b>NexPlayer::NEX_USE_RENDER_HW</b>
     *          Using the hardware renderer with a hardware codec. See
     *          \ref hwrender for details. 
     */
    public native int GetRenderMode();
    
    /**
     * \brief Specifies the path to the hardware renderer configuration file.
     *
     * The hardware renderer configuration file defines which combinations of
     * codec and device should make use of the hardware renderer.  The configuration
     * file is provided with the SDK, but it is the responsibility of the app
     * developer to include the file with the application, and specify the path
     * using this method.
     *
     * The path must be specified before opening any content, otherwise the
     * hardware renderer will not be used.
     *
     * \param strConfPath   The path to the configation file.
     *
     * \returns Always 0, but may change in future versions.  The return value should be ignored.
     */
    public native int SetConfigFilePath(String strConfPath);
    
    /** Possible value for arguments to {@link NexPlayer#setMediaStream(int, int, int)}.*/
    public static final int MEDIA_STREAM_DEFAULT_ID     = 0xFFFFFFFF;
    
    /** Possible value for {@link NexStreamInformation#mType}; see there for details.*/
    public static final int MEDIA_STREAM_TYPE_AUDIO     = 0x00;
    /** Possible value for {@link NexStreamInformation#mType}; see there for details.*/
    public static final int MEDIA_STREAM_TYPE_VIDEO     = 0x01;
    /** Possible value for {@link NexStreamInformation#mType}; see there for details.*/
    public static final int MEDIA_STREAM_TYPE_TEXT      = 0x02;
    
    /**
     * For media with multiple streams, selects the streams that will be presented
     * to the user.<p>
     * 
     * The full list of available streams (if any) can be found in
     * the {@link NexContentInformation#mArrStreamInformation mArrStreamInformation}
     * array in NexContentInformation.<p>
     * 
     * Each stream is either an audio stream or a video stream, and one of each may be
     * selected for presentation to the user.<p>
     * 
     * Streams may in turn have associated custom attributes.  Custom attributes
     * limit playback to a subset of tracks within the stream.  Custom attributes are
     * key/value pairs.  Each possible pairing (from all the tracks in a stream) is
     * listed in {@link NexStreamInformation#mArrCustomAttribInformation mArrCustomAttribInformation}
     * along with an associated integer ID.  Specifying that particular integer ID causes 
     * only tracks with that particular key/value pairing to beused.  Only one ID may be
     * specified at any given time.<p>
     * 
     * @param iAudioStreamId        The ID of the stream to use for audio.  
     *                              If this is MEDIA_STREAM_DEFAULT_ID, the first
     *                              stream that is an audio stream will be used.<p>
     * 
     * @param iTextStreamId         The ID of the stream to use for text (subtitles,
     *                              captions, and so on).  
     *                              If this is MEDIA_STREAM_DEFAULT_ID, the first
     *                              stream that is an video stream will be used.<p>
     * 
     * @param iVideoStreamId        The ID of the stream to use for video.  
     *                              If this is MEDIA_STREAM_DEFAULT_ID, the first
     *                              stream that is an video stream will be used.<p>
     * 
     * @param iVideoCustomAttrId    The ID of the custom attribute to use.  If
     *                              this is MEDIA_STREAM_DEFAULT_ID, the default
     *                              custom attribute will be used. <p>
     * 
     */
    public native int setMediaStream(int iAudioStreamId, int iTextStreamId, int iVideoStreamId, int iVideoCustomAttrId);
    
    /**
     * Sets the maximum bandwidth for streaming playback.  This applies in the
     * case where there are multiple tracks at different bandwidths (such as
     * in the case of HLS or Smooth Streaming).  The player will not consider
     * any track over the maximum bandwidth when determining whether a track 
     * change is appropriate, even if it detects more bandwidth available.
     * 
     * @param iBandWidth    Maximum bandwidth in Kbps
     * @return              zero if successful, non-zero if there was an error
     */
    public native int changeMaxBandWidth(int iBandWidth);
    
    /** This is a possible value for the <i>methods</i> parameter of {@link NexPlayer#addRTSPHeaderFields(int, String) addRTSPHeaderFields}.  See that method for details. */
    public static int RTSP_METHOD_DESCRIBE      = 0x00000001;
    /** This is a possible value for the <i>methods</i> parameter of {@link NexPlayer#addRTSPHeaderFields(int, String) addRTSPHeaderFields}.  See that method for details. */
    public static int RTSP_METHOD_SETUP         = 0x00000002;
    /** This is a possible value for the <i>methods</i> parameter of {@link NexPlayer#addRTSPHeaderFields(int, String) addRTSPHeaderFields}.  See that method for details. */
    public static int RTSP_METHOD_OPTIONS       = 0x00000004;
    /** This is a possible value for the <i>methods</i> parameter of {@link NexPlayer#addRTSPHeaderFields(int, String) addRTSPHeaderFields}.  See that method for details. */
    public static int RTSP_METHOD_PLAY          = 0x00000008;
    /** This is a possible value for the <i>methods</i> parameter of {@link NexPlayer#addRTSPHeaderFields(int, String) addRTSPHeaderFields}.  See that method for details. */
    public static int RTSP_METHOD_PAUSE         = 0x00000010;
    /** This is a possible value for the <i>methods</i> parameter of {@link NexPlayer#addRTSPHeaderFields(int, String) addRTSPHeaderFields}.  See that method for details. */
    public static int RTSP_METHOD_GETPARAMETER  = 0x00000020;
    /** This is a possible value for the <i>methods</i> parameter of {@link NexPlayer#addRTSPHeaderFields(int, String) addRTSPHeaderFields}.  See that method for details. */
    public static int RTSP_METHOD_TEARDOWN      = 0x00000040;
    /** This is a possible value for the <i>methods</i> parameter of {@link NexPlayer#addRTSPHeaderFields(int, String) addRTSPHeaderFields}.  See that method for details. */
    public static int RTSP_METHOD_ALL           = ( RTSP_METHOD_DESCRIBE 
                                                   | RTSP_METHOD_SETUP
                                                   | RTSP_METHOD_OPTIONS 
                                                   | RTSP_METHOD_PLAY
                                                   | RTSP_METHOD_PAUSE 
                                                   | RTSP_METHOD_GETPARAMETER
                                                   | RTSP_METHOD_TEARDOWN );
    
    /**
     * Determine the amount of buffered data.
     * 
     * Returns the amount of data that has been buffered ahead.  This is useful
     * to know the areas where it is possible ot seek in (for example) a progressive
     * download without needing to buffer.<p>
     * 
     * @return number of milliseconds (1/1000 sec) of media that has been buffered ahead
     */
    public native int getBufferStatus();
    
    private native int prepareSurface(int surfacetype);
    
    
    /** This is a possible value for the <i>iFlag</i> parameter of {@link NexPlayer#setRenderOption(int) setRenderOption}.  See that method for details. */
    public static final int RENDER_MODE_VIDEO_NONE  =           0x00000000;
    /** This is a possible value for the <i>iFlag</i> parameter of {@link NexPlayer#setRenderOption(int) setRenderOption}.  See that method for details. */
    public static final int RENDER_MODE_VIDEO_FILTERBITMAP =    0x00000001;
    /** This is a possible value for the <i>iFlag</i> parameter of {@link NexPlayer#setRenderOption(int) setRenderOption}.  See that method for details. */
    public static final int  RENDER_MODE_VIDEO_DITHERING =      0x00000002;
    /** This is a possible value for the <i>iFlag</i> parameter of {@link NexPlayer#setRenderOption(int) setRenderOption}.  See that method for details. */
    public static final int  RENDER_MODE_VIDEO_ANTIALIAS =      0x00000004;
    /** This is a possible value for the <i>iFlag</i> parameter of {@link NexPlayer#setRenderOption(int) setRenderOption}.  See that method for details. */
    public static final int  RENDER_MODE_VIDEO_ALLFLAG =        0xFFFFFFFF;
    
    /**
     * This function configures the paint flags used with the Android bitmap rendering module.<p>
     * 
     * There are multiple rendering modules that can be used for displaying video (see {@link NexPlayer#init(Context, String, int, int)} for details) and
     * NexPlayer automatically selects the best one for the given content and device.<p>
     * 
     * In the case where the rendering
     * module uses bitmaps provided through the Android API, the rendering options specified here are
     * used to set up the flags on the {@link android.os.Paint Paint} object that is used to display the bitmap.<p>
     * 
     * For all other rendeirng modules, the values set here are ignored.
     * 
     * @param iFlag
     *            Options for video rendering. This can be zero or more of the following values, combined
     *            together using bitwise <i>OR</i>.  Each value corresponds to an Android API flag available
     *            on a Paint object.<p>
     *            <ul>
     *            <li><b>{@link NexPlayer#RENDER_MODE_VIDEO_NONE} (0x00000000)</b>
     *                      No special paint flags are set.</li>
     *            <li><b>{@link NexPlayer#RENDER_MODE_VIDEO_FILTERBITMAP} (0x00000001)</b>
     *                      Corresponds to {@link android.os.Paint#FILTER_BITMAP_FLAG}</li>
     *            <li><b>{@link NexPlayer#RENDER_MODE_VIDEO_ANTIALIAS} (0x00000002)</b>
     *                      Corresponds to {@link android.os.Paint#ANTI_ALIAS_FLAG}</li>
     *            <li><b>{@link NexPlayer#RENDER_MODE_VIDEO_DITHERING} (0x00000004)</b>
     *                      Corresponds to {@link android.os.Paint#DITHER_FLAG}</li>
     *            <li><b>{@link NexPlayer#RENDER_MODE_VIDEO_ALLFLAG} (0xFFFFFFFF)</b>
     *                      Enables all options.</li>
     *            </ul>
     * @return Always zero, but may change in future versions; the return value should be ignored.
     */
    public native int setRenderOption(int iFlag);
    
    /**
     * Set output video position and size.<p>
     * 
     * The position is relative to and within the surface specified in {@link NexPlayer#setDisplay(SurfaceHolder)} or 
     * relative to and within the application's OpenGL surface, if the OpenGL renderer is being used.<p>
     * 
     * X and Y are the distance from the upper-left corner.  All units are in pixels and are resolution-dependent.<p>
     * 
     * If the video is large than the surface, or part of it is outside the surface, it will be cropped
     * appropriately.  Negative values are therefore acceptable for iX and iY.<p>
     * 
     * @param iX
     *            video display x position
     * @param iY
     *            video display y position
     * @param iWidth
     *            video display width
     * @param iHeight
     *            video display height            
     * @return Always zero, but may change in future versions; the return value should be ignored.
     */
    public native int setOutputPos(int iX, int iY, int iWidth, int iHeight);
    
    /** 
     * Turns video rendering on or off.<p>
     * 
     * If video rendering is turned off, any existing frame will
     * remain on the display.  If you wish to clear it, you may
     * directly draw to the surface and fill it with black pixels
     * after turning off video rendering.<p>
     * 
     * @param bOn <code>true</code> to render video, <code>false</code> to turn off video rendering.
     */
    public void videoOnOff(boolean bOn) {
        videoOnOff(bOn?1:0,0);
    }
    
    /**
     * Turns video rendering on or off.<p>
     * 
     * Use of {@link NexPlayer#videoOnOff(boolean)} is recommended instead of this function.
     * 
     * @param bOn
     *            1 to turn on video rendering, 0 to turn off video rendering; other values
     *            are reserved and should not be used.
     * @param bErase
     *            Reserved; must be zero.
     *            
     * @return Always zero, but may change in future versions; the return value should be ignored.
     */
    public native int videoOnOff(int bOn, int bErase);
    
    private native int prepareAudio(int audiotype);

    /**
     * Sets the player output volume.
     *
     * This affects the output of the player before it is mixed with other sounds.  
     * Normally, this should be left at the default setting of 1.0, and the volume 
     * should be adjusted via the device master volume setting (adjustable by the 
     * the user via the hardware volume buttons on the device).  However, if the
     * application contains multiple audio sources (or if there is other audio being
     * played on the device), this property can be used to reduce the NexPlayer&trade;
     * volume in relation to other sounds.
     *
     * The valid range for this property is 0.0 ~ 1.0.  A value of 0.0 will silence
     * the output of the player, and a value of 1.0 (the default) plays the audio at
     * the original level, affected only by the device master volume setting (controlled
     * by the hardware buttons).
     *
     * It is not recommended to use this setting for volume controlled by the user; that
     * is best handled by adjusting the device master volume.
     */
    public native int setVolume(float fGain);
    
    /**
     * Selects the caption (subtitle) track that will be used. Subtitles for the
     * selected track will be passed to 
     * {@link IListener#onTextRenderRender(NexPlayer, int, byte[]) onTextRenderRender}
     * for display.
     *
     * This is used for file-based captions only.  For streaming media with included
     * captions, \c setMediaStream should be used instead, and local captions should
     * be turned off (running both types of captions at the same time has undefined
     * results).
     * 
     * @param indexOfCaptionLanguage  
     *  An index into the {@link NexContentInformation#mCaptionLanguages mCaptionLanguages}
     *  array specifying which langauge to use.  If there are <b>n</b> entries 
     *  in the caption array, then you may pass <code>0...n-1</code> to 
     *  specify a single language, <code>n</code> to specify all languages,
     *  and <code>n+1</code> to turn off captions. 
     * @return
     *              zero if successful, non-zero if there was an error
     */
    public native int setCaptionLanguage(int indexOfCaptionLanguage);
    
    
    // TODO: Calling this more than once has what effect??
    /**
     * Begins capturing video frames.  This may be used to capture a
     * single frame immediately, or to capture a series of frames at
     * regular intervals. In either case, the captured frames are actually
     * sent to the {@link IListener#onVideoRenderCapture(NexPlayer, int, int, int, Object) onVideoRenderCapture}
     * handler, so your application must implement it to receive the frames.
     * 
     * When this function is called, the current frame will immediately be
     * sent to <code>onVideoRenderCapture</code>, and then any scheduled frames
     * will be sent after the specified interval has elapsed.<p>
     * 
     * @param iCount
     *              The number of frames to capture; this should be at least 1 or the
     *              method has no effect.<p>
     * 
     * @param iInterval
     *              If <code>iCount</code> is greater than 1, this is the number of milliseconds to
     *              wait between captures.  For example, if <code>iCount</code> is 3 and <code>iInterval</code>
     *              is 100, then one frame will be captured immediately, another after 1/10sec, and
     *              a third after a further 1/10sec.<p>
     * @return          
     *              zero if successful, non-zero if there was an error
     */
    public native int captureVideo( int iCount, int iInterval );
    
    /**
     * \brief   Gets \c NexPlayer&trade;SDK version information.
     * 
     * The return value is an integer; the meaning is based on the 
     * \c mode argument passed.
     * 
     * Generally, the components of the version are assembled as follows:<p>
     * 
     * <code><pre>
     * String versionString = nexPlayer.getVersion(0) + "." +
     *                        nexPlayer.getVersion(1) + "." +
     *                        nexPlayer.getVersion(2) + "." +
     *                        nexPlayer.getVersion(3);
     * </pre></code>
     * 
     * @param mode
     *              Version information to return.  This must be one of the following values (other
     *              values are reserved and should not be used).
     *                  - 0 : Major version
     *                  - 1 : Minor version
     *                  - 2 : Patch version
     *                  - 3 : Build version
     * 
     * @return Requested version information (see \c mode above).
     */
    public native int   getVersion(int mode);   
    
    
    /**
     * \brief This function creates the NexPlayer&trade;&nbsp;engine.
     * 
     * @param nexplayer_this
     *            CNexPlayer instance pointer
     * @param strPackageName
     *            Application package name. (ex. com.nextreaming.nexplayersample)
     * @param strLibPath
     *            library path for NexPlayer 
     * @param sdkInfo
     *            Android SDK version
     *              <ul>
     *              <ui>0x15 : SDK version 1.5 CUPCAKE
     *              <ui>0x16 : SDK version 1.6 DONUT
     *              <ui>0x21 : SDK version 2.1 ECLAIR
     *              <ui>0x22 : SDK version 2.2 FROYO
     * @param cpuInfo
     *            cpuVersion
     *              <ul>
     *              <li>4 : armv4</li>
     *              <li>5 : armv5</li>
     *              <li>6 : armv6</li>
     *              <li>7 : armv7</li>
     *              </ul>      
     * @param strDeviceModel
     *            Device model name
     * @param logLevel
     *            NexPlayerSDK log display level
     * @param pixelFormat
     *            The pixel format to use when using the Java renderer. 
     *            For more information, see the <i>Java Renderer</i> section of
     *            {@link com.nextreaming.nexplayerengine}.<p>
     *              <ul>
     *              <li><b>1:</b> RGBA 8888</li>
     *              <li><b>4:</b> RGB 565</li>
     *              </ul>
     */
    private native final int    _Constructor( Object nexplayer_this, String strPackageName, String strLibPath, int sdkInfo, int cpuInfo, String strDeviceModel, int logLevel, int pixelFormat );    
    
    /**
     * This function releases the NexPlayer&trade;&nbsp;engine.
     */
    private native void         _Release();

    
    
    @SuppressWarnings("unused")  // This called from the native code, so it is actually used
    private static void callbackFromNative( Object nexplayer_ref, int what, int arg1,
                                           int arg2, int arg3,int arg4, Object obj )
    {
        @SuppressWarnings("unchecked") // The type cast to WeakReference<NexPlayer> is always safe because 
        // this function is only called by known native code that always
        // passes an object of this type.
        NexPlayer nexplayer = (NexPlayer)( (WeakReference<NexPlayer>)nexplayer_ref ).get();
        if ( nexplayer == null )
        {
            // Log.w(TAG, "callbackFromNative returns null");
            return;
        }
        
        switch ( what )
        {
            case NEXPLAYER_EVENT_NOP:
                
                return;
            case NEXPLAYER_EVENT_ENDOFCONTENT:
                mListener.onEndOfContent( nexplayer );
                break;
            case NEXPLAYER_EVENT_STARTVIDEOTASK:
                mListener.onStartVideoTask( nexplayer );
                break;
            case NEXPLAYER_EVENT_STARTAUDIOTASK:
                mListener.onStartAudioTask( nexplayer );
                break;
            case NEXPLAYER_EVENT_TIME:
                mListener.onTime( nexplayer, arg1);
                break;
            case NEXPLAYER_EVENT_ERROR:
                mListener.onError( nexplayer, NexErrorCode.fromIntegerValue(arg1));
                break;
            case NEXPLAYER_EVENT_STATECHANGED:
                mListener.onStateChanged( nexplayer, arg1, arg2 );
                break;
            case NEXPLAYER_EVENT_SIGNALSTATUSCHANGED:
                mListener.onSignalStatusChanged( nexplayer, arg1, arg2 );
                break;
            case NEXPLAYER_EVENT_ASYNC_CMD_COMPLETE:
                Log.w(TAG, "NEXPLAYER_EVENT_ASYNC_CMD_COMPLETE :" + arg1 +", " + arg2 + ", " + arg3);
                mListener.onAsyncCmdComplete( nexplayer, arg1, arg2, arg3, arg4 );
                break;
            case NEXPLAYER_EVENT_RTSP_COMMAND_TIMEOUT:
                Log.w(TAG, "NEXPLAYER_EVENT_RTSP_COMMAND_TIMEOUT" );
                mListener.onRTSPCommandTimeOut( nexplayer );
                break;
            case NEXPLAYER_EVENT_PAUSE_SUPERVISION_TIMEOUT:
                Log.w(TAG, "NEXPLAYER_EVENT_PAUSE_SUPERVISION_TIMEOUT" );
                mListener.onPauseSupervisionTimeOut( nexplayer );
                break;
            case NEXPLAYER_EVNET_DATA_INACTIVITY_TIMEOUT:
                Log.w(TAG, "NEXPLAYER_EVNET_DATA_INACTIVITY_TIMEOUT" );
                mListener.onDataInactivityTimeOut( nexplayer );
                break;  
            case NEXPLAYER_EVENT_RECORDING_ERROR:
                mListener.onRecordingErr( nexplayer, arg1 );
                break;
            case NEXPLAYER_EVENT_RECORDING:
                mListener.onRecording( nexplayer, arg1, arg2 );
                break;
            case NEXPLAYER_EVENT_RECORDEND:
                mListener.onRecordingEnd( nexplayer, arg1 );
                break;
            case NEXPLAYER_EVENT_TIMESHIFT_ERROR:
                mListener.onTimeshiftErr( nexplayer, arg1 );
                break;
            case NEXPLAYER_EVENT_TIMESHIFT:
                mListener.onTimeshift( nexplayer, arg1, arg2 );
                break;
            case NEXPLAYER_EVENT_BUFFERINGBEGIN:
                mListener.onBufferingBegin( nexplayer );
                break;
            case NEXPLAYER_EVENT_BUFFERINGEND:
                mListener.onBufferingEnd( nexplayer );
                break;
            case NEXPLAYER_EVENT_BUFFERING:
                mListener.onBuffering( nexplayer, arg1 );
                break;
            case NEXPLAYER_EVENT_AUDIO_RENDER_CREATE:
                Log.v( TAG, "[CB] Audio render create !! " + arg1 );
                nexplayer.setAudioTrack(arg1, arg2);
                mListener.onAudioRenderCreate( nexplayer, arg1, arg2);
                break;
            case NEXPLAYER_EVENT_AUDIO_RENDER_DELETE:
                Log.v( TAG, "[CB] Audio render delete !! " );
                nexplayer.releaseAudioTrack();
                mListener.onAudioRenderDelete( nexplayer );
                Log.v( TAG, "[CB] Audio render delete Done!! ");
                break;
            case NEXPLAYER_EVENT_VIDEO_RENDER_CREATE:
                Log.v( TAG, "[CB] Video render create !! " + arg1 );
                mListener.onVideoRenderCreate( nexplayer, arg1, arg2, obj );
                break;
            case NEXPLAYER_EVENT_VIDEO_RENDER_DELETE:
                Log.v( TAG, "[CB] Video render delete !! " );
                mListener.onVideoRenderDelete( nexplayer);
                Log.v( TAG, "[CB] Video render delete Done!! " + arg1 );
                break;
            case NEXPLAYER_EVENT_VIDEO_RENDER_CAPTURE:
                Log.v( TAG, "[CB] Video render capture !! " );
                mListener.onVideoRenderCapture(nexplayer, arg1, arg2,arg3, obj );
                break;
            case NEXPLAYER_EVENT_VIDEO_RENDER_RENDER:
                //Log.v( TAG, "[CB] Video render render !! " + arg1 );//msg.arg1 );
                //this function is only used when java renderer working
                mListener.onVideoRenderRender( nexplayer );
                break;
            case NEXPLAYER_EVENT_TEXT_RENDER_INIT:
                Log.v( TAG, "[CB] Text render init !! " + arg1);//msg.arg1 );
                mListener.onTextRenderInit( nexplayer, arg1 );
                break;          
            case NEXPLAYER_EVENT_TEXT_RENDER_RENDER:
                Log.v( TAG, "[CB] Text render render !! " + arg1 + " index : " + arg2);//msg.arg1 );
                mListener.onTextRenderRender( nexplayer, arg2, obj );
                break;      
            case NEXPLAYER_EVNET_STATUS_REPORT:     
                Log.v( TAG, "[CB] Status Report !! msg :" + arg1 + "   param1 : " + arg2);
                mListener.onStatusReport( nexplayer, arg1, arg2);
                /*case NEXPLAYER_EVENT_DEBUGINFO:       
                 Log.v( TAG, "[CB] Debug Info !! msg :" + arg1 + "   param1 : " + arg2);
                 mListener.onDebugInfo( nexplayer, arg1, (String)obj);*/
            default:
                //Log.e( TAG, "Unknown message type " + msg.what );
                return;
        }
    }
    @SuppressWarnings("unused") // Actually used (called from native code)
    private static int callbackFromNativeRet( Object nexplayer_ref, int what, int arg1,
                                             int arg2, Object obj )
    {
        int nRet = 0;
        @SuppressWarnings("unchecked") // Because the object handle is from known native code, the type is guaranteed
        NexPlayer nexplayer = (NexPlayer)( (WeakReference<NexPlayer>)nexplayer_ref ).get();
        if ( nexplayer == null )
        {
            //Log.e( TAG, "NexPlayer is NULL!!" );
            return -1;
        }
        
        
        switch ( what )
        {
                
            default:
                //Log.e( TAG, "Unknown message type " + msg.what );
                break;
        }
        
        return 0;
    }
    
    
    /**
     * Sets the Android AudioTrack object that NexPlayer will output rendered audio to.<p>
     * 
     * This is normally called from {@link NexPlayer.IListener#onAudioRenderCreate(NexPlayer, int, int) onAudioRenderCreate}
     * after creating the {@link android.media.AudioTrack} object.<p>
     * 
     * The audio track object must have the format <code>ENCODING_PCM_16BIT</code> and the mode 
     * <code>MODE_STREAM</code>.  The sampling rate must match that passed on <code>onAudioRenderCreate</code>
     * and the channel configutation must match the number of channels passed to <code>onAudioRenderCreate</code>.<p>
     * 
     * If you swap out the audio track for another one, you must call the <code>stop</code>, <code>flush</code>,
     * and <code>release</code> methods on the old audio track, in that order.  You must also call these methods
     * when NexPlayer is done with the audio track and calls <code>onAudioRenderDelete</code>.<p>
     * 
     * @param audioTrack The {@link android.media.AudioTrack AudioTrack} object to use for audio output.
     * @return Always zero, but may change in future versions; the return value should be ignored.
     */
    private int setAudioTrack(int samplingRate, int channelNum)
    {
        Log.d(TAG, "setAudioTrack");

        int nChannelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        if (channelNum != 1)
            nChannelConfig = AudioFormat.CHANNEL_CONFIGURATION_STEREO;

        int nMinBufferSize = AudioTrack.getMinBufferSize(samplingRate,
                nChannelConfig, AudioFormat.ENCODING_PCM_16BIT);

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, samplingRate,
                nChannelConfig, AudioFormat.ENCODING_PCM_16BIT, nMinBufferSize * 4,
                AudioTrack.MODE_STREAM);

        prepareAudio(0);
        return 0;
    }
    
    private int releaseAudioTrack()
    {
        if( mAudioTrack != null)
        {
            mAudioTrack.release();
            mAudioTrack = null;
        }

        return 0;
    }
    /**
     * Sets the surface on which video will be displayed.<p>
     *
     * \warning This is not supported with the Java or OpenGL renderers, and should not be called
     *          if one of those renderers is in use.
     * 
     * This function actually takes the {@link android.view.SurfaceHolder SurfaceHolder} associated
     * with the surface on which the video will be displayed.<p>
     * 
     * This function should be called from {@link NexPlayer.IListener#onVideoRenderCreate(NexPlayer, int, int, Object) onVideoRenderCreate}
     * after the surface has been created.  In addition, if the surface object changes (for example, if the 
     * <code>SurfaceHolder</code>'s <code>surfaceCreated</code> callback is after the initial setup), this
     * function should be called again to provide the new surface.<p>
     * 
     * The surface should match the pixel format of the screen, if possible, or should
     * bet set to <code>PixelFormat.RGB_565</code>.<p>
     * 
     * In general, the surface should be created as follows:<p>
     * <code><pre>
     * Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
     * int displayPixelFormat = display.getPixelFormat();
     * 
     * SurfaceView surfaceView = new SurfaceView(this);
     * SurfaceHolder surfaceHolder = mVideoSurfaceView.getHolder();
     * 
     * if( displayPixelFormat == PixelFormat.RGBA_8888 ||
     *     displayPixelFormat == PixelFormat.RGBX_8888 ||
     *     displayPixelFormat == PixelFormat.RGB_888 ||
     *     displayPixelFormat == 5 )
     * {
     *     surfaceHolder.setFormat(PixelFormat.RGBA_8888);
     * }
     * else
     * {
     *     surfaceHolder.setFormat(PixelFormat.RGB_565);
     * }
     * 
     * surfaceHolder.addCallback(new SurfaceHolder.Callback() {
     *     &#x0040;Override
     *     public void surfaceDestroyed(SurfaceHolder holder) {
     *         mSurfaceExists = false;
     *     }
     *     &#x0040;Override
     *     public void surfaceCreated(SurfaceHolder holder) {
     *         mSurfaceExists = true;
     *         if( mPlaybackStarted ) {
     *             mNexPlayer.setDisplay(holder);
     *         }
     *     }
     *     &#x0040;Override
     *     public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
     *         // do nothing
     *     }
     * });
     * </pre></code>
     * 
     * In <code>onVideoRenderCreate</code>, the code should ensure that the surface has already been created
     * before passing the surface holder to <code>setDisplay</code>.  Because <code>onViewRenderCreate</code>
     * can run asynchronously, it may need to wait until the surface is created by sleeping and polling.<p>
     * 
     * For
     * example, if using the example code above, <code>onVideoRenderCreate</code> would wait until
     * <code>mSurfaceExists</code> becomes true, using something like:<p>
     * 
     * <code><pre>
     * while(!mVideoSurfaceExists)
     *     Thread.sleep(10);
     * nexPlayer.setDisplay(surfaceHolder);
     * </pre></code>
     * 
     * @param sh The {@link android.view.SurfaceHolder SurfaceHolder} holding the surface on which to display video.
     */
    public void setDisplay( SurfaceHolder sh ) {
        setDisplay(sh,0);
    }
    
    /**
     * Sets the surface on which video will be displayed.<p>
     * 
     * This is the same as {@link NexPlayer#setDisplay(SurfaceHolder) setDisplay(SurfaceHolder)}, except that
     * it takes an additional surface number parameter.  Currently, only one surface
     * at a time is supported, so this additional parameter must always be zero.<p>
     * 
     * In general, it's better ot use {@link NexPlayer#setDisplay(SurfaceHolder) setDisplay(SurfaceHolder)}.<p>
     * @param sh The {@link android.view.SurfaceHolder SurfaceHolder} holding the surface on which to display video.
     * @param surfaceNumber The surface number to set (must be zero)
     * @return zero if successful, non-zero if there was an error
     */
    public int setDisplay( SurfaceHolder sh, int surfaceNumber )
    {
        
        if ( surfaceNumber == 0 )
        {
            mSurfaceHolder = sh;
            if ( mSurfaceHolder == null )
                mSurface = null;
            else
                mSurface = sh.getSurface();
            Log.w( TAG, "setDisplay : " + mSurfaceHolder + "," + mSurface );
        }
        updateSurfaceScreenOn( surfaceNumber );
        return prepareSurface( surfaceNumber );
    }
    
    
    private void updateSurfaceScreenOn( int surfacetype )
    {
        
        if ( surfacetype == 0 )
        {
            Log.w(TAG, "updateSurfaceScreenOn surface type is 0");
            if ( mSurfaceHolder != null )
            {
                mSurfaceHolder.setKeepScreenOn( true );
            }
            
        }
    }
    
    /**
     * Releases resources used by the \c NexPlayer&trade;&nbsp; instance.
     * This should be called when the instance is no longer needed; after
     * calling this method, the instance can no longer be used, and methods
     * on it should not be called, except for {@link NexPlayer#getState() getState} which
     * will return {@link NexPlayer#NEXPLAYER_STATE_NONE NEXPLAYER_STATE_NONE}.
     */
    
    public void release()
    {
        updateSurfaceScreenOn( 0 );
        releaseAudioTrack();
        _Release();
    }
    
    
    // TODO: Improve this documentation
    /**
     * release native resource
     */
    @Override
    protected void finalize()
    {
        _Release();
    }

    /**
     * 
     * \brief Sets all values which are necessary to make use of the Verimatrix DRM library.  
     * 
     * Normally, this method <i>must</i> be invoked once before playing encrypted content.
     * This method however may NOT be called when playing non-DRM-encrypted content. 
     * 
     * \param identifier        The unique identifier to identify a single authorized user 
     * \param VCASAddrAndPort   The VCAS server address and port number (e.g. "179.12.32.1:80")
     * \param company           The company name. At the moment this should be set to "Verimatrix" for testing. 
     * \param logPath           The full log path to store recorded log of Verimatrix DRM shared library 
     *
     * \returns                 zero if the initialization is successful, non-zero if there's an error
     *
     */
    public native int initVMDRM(String identifier, String VCASAddrAndPort, String company, String logPath);
    
    /**
     * The application must implement this interface in order to receive
     * events from NexPlayer&trade;.<p>
     * 
     * <b>CAUTION:</b> These callbacks may occur in any thread, not
     * necessarily the main application thread. In some cases, it may not
     * be safe to call UI-related functions from within <code>IListener</code>
     * callbacks.  The safest way to update the UI is to use {@link android.os.Handler}
     * to post an event back to the main application thread.<p>
     * 
     * NexPlayer&trade;&nbsp; will call the methods provided in this interface
     * automatically during playback to notify the application when various
     * events have occurred.
     * 
     * In most cases, the handling of these events is optional; NexPlayer&trade;&nbsp;
     * will continue playback normally without the application doing anything
     * special.  There are a few exceptions to this which are listed below.<p>
     * 
     * There are two categories of notifications:  For any asynchronous command
     * issued to NexPlayer&trade;&nbsp; (via the appropriate method call), a callback
     * will occur when that command has completed to notify the application of
     * the success or failure of the operation.
     * 
     * The other category of notifications are notifications that occur during
     * playback to notify the application of changes in the state of NexPlayer&trade;.
     * For example, if NexPlayer&trade;&nbsp; begins buffer data during streaming
     * playback, an event occurs to allow the application to display an appropriate
     * message, if necessary.
     * 
     * For asynchronous commands, the application will generally want to take
     * action in the following cases (some applications may need to handle
     * these differently depending on their requirements; these are correct
     * for most cases):
     * 
     *   - When any command fails, display an error to the user
     *   - When an <i>open</i> command succeeds, issue a <i>start</i> command to
     *      begin actual playback.
     *   - When a <i>stop</i> command succeeds, issue a <i>close</i> command to
     *      close the file.
     * 
     * 
     * This is because commands such as <i>open</i> and <i>stop</i> take some
     * time to execute, and follow-up command such as <i>start</i> and <i>close</i>
     * cannot be called immediately, but must wait until the first command has
     * completed.
     * 
     * See each individual \c IListener method for a recommendation
     * on how to implement that method in your application.
     */ 
    public interface IListener
        {
            /**
             * Playback has completed successfully up to the end of the content.
             * 
             * This event occurs when the player reaches the end of the file or stream.
             * In most cases, applications should respond to this by calling {@link NexPlayer#stop()}
             * and then updating the user interface. 
             * 
             * @param mp The NexPlayer object generating the event.
             */
            void onEndOfContent( NexPlayer mp );
            
            /**
             * The NexPlayer&trade;&nbsp; video task has started.
             * 
             * This is provided for compatibility with older code, and new
             * applications may safely ignore this event.
             * 
             * @param mp The NexPlayer object generating the event.
             */
            void onStartVideoTask( NexPlayer mp );
            
            /**
             * The NexPlayer&trade;&nbsp; audio task has started.
             * 
             * This is provided for compatibility with older code, and new
             * applications may safely ignore this event.
             * 
             * @param mp The NexPlayer object generating the event.
             */
            void onStartAudioTask( NexPlayer mp);
            
            /**
             * Playback has progressed to the certain position.
             * 
             * This event occurs once per second. If the application is
             * displaying the current play position, it should update it
             * to reflect this new value.
             * 
             * Applications that wish to update the play time more often
             * that once per second or with a greater accuracy may ignore
             * this event and create their own timer, in which case they
             * can use the current play time reported in {@link NexContentInformation}.
             * 
             * @param mp The NexPlayer object generating the event.
             * @param sec
             *            Current play position in secondss
             */
            void onTime( NexPlayer mp, int sec );
            
            /**
             * An error has occurred during playback.
             * 
             * @param mp The NexPlayer object generating the event.
             * @param errorcode The error code for the generated error.
             */
            void onError( NexPlayer mp, NexErrorCode errorcode );
            
            /**
             * NexPlayer&trade;&nbsp; signal status has been changed.
             * 
             * @param mp The NexPlayer object generating the event.
             * @param pre
             *            Previous signal status
             * @param now
             *            Current signal status
             */
            void onSignalStatusChanged( NexPlayer mp, int pre, int now );
            
            /**
             * NexPlayer&trade;&nbsp; state has been changed.
             * 
             * @param mp 
             *            The NexPlayer object generating the event.
             * @param pre
             *            Previous play status
             * @param now
             *            Current play status
             */
            void onStateChanged( NexPlayer mp, int pre, int now );
            
            /**
             * NexPlayer&trade;&nbsp; recording error.
             * 
             * @param mp 
             *            The NexPlayer object generating the event.
             */
            void onRecordingErr( NexPlayer mp, int err );
            
            /**
             * NexPlayer&trade;&nbsp; recording end.
             * 
             * @param mp 
             *            The NexPlayer object generating the event.
             */
            void onRecordingEnd( NexPlayer mp, int success );
            
            /**
             * Reports NexPlayer&trade;&nbsp;recording status.
             *  
             * @param mp 
             *            The NexPlayer object generating the event.
             */
            void onRecording( NexPlayer mp, int recDuration, int recSize );
            
            /**
             * NexPlayer&trade;&nbsp;Time shift error.
             */
            void onTimeshiftErr( NexPlayer mp, int err );
            
            /**
             * Reports NexPlayer&trade;&nbsp; Time shift status.
             * 
             * @param mp 
             *            The NexPlayer object generating the event.
             */
            void onTimeshift( NexPlayer mp, int currTime, int TotalTime );
            
            /**
             * \brief   When an asynchronous method of NexPlayer&trade;&nbsp; has completed
             *          successfully or failed, this event occurs.
             * 
             * @param mp 
             *            The NexPlayer object generating the event.
             * 
             * @param command   The command which completed.  This may be any
             *                  of the following values:
             *                  <ul>
             *                    <li><code>NEXPLAYER_ASYNC_CMD_NONE</code> (0x00000000)</li>
             *                    <li><code>NEXPLAYER_ASYNC_CMD_OPEN_LOCAL</code> (0x00000001)</li>
             *                    <li><code>NEXPLAYER_ASYNC_CMD_OPEN_STREAMING</code> (0x00000002)</li>
             *                    <li><code>NEXPLAYER_ASYNC_CMD_OPEN_TV</code> (0x00000003)</li> 
             *                    <li><code>NEXPLAYER_ASYNC_CMD_START_LOCAL</code> (0x00000005)</li>
             *                    <li><code>NEXPLAYER_ASYNC_CMD_START_STREAMING</code> (0x00000006)</li>
             *                    <li><code>NEXPLAYER_ASYNC_CMD_START_TV</code> (0x00000007)</li>
             *                    <li><code>NEXPLAYER_ASYNC_CMD_STOP</code> (0x00000008)</li>
             *                    <li><code>NEXPLAYER_ASYNC_CMD_PAUSE</code> (0x00000009)</li>
             *                    <li><code>NEXPLAYER_ASYNC_CMD_RESUME</code> (0x0000000A)</li>
             *                    <li><code>NEXPLAYER_ASYNC_CMD_SEEK</code> (0x0000000B)</li>
             *                    <li><code>NEXPLAYER_ASYNC_CMD_FORWARD</code> (0x0000000C)</li>
             *                    <li><code>NEXPLAYER_ASYNC_CMD_BACKWARD</code> (0x0000000D)</li>
             *                    <li><code>NEXPLAYER_ASYNC_CMD_STEP_SEEK</code> (0x0000000E)</li>
             *                  </ul>
             * @param result    Zero if the command was successful, otherwise
             *                   an error code.
             * @param param1    A value specific to the command that has completed.  The following
             *                  commands use this value (for all other commands, the value is 
             *                  undefined and reserved for future use, and must be ignored):
             *                  <ul>
             *                    <li><b>NEXPLAYER_ASYNC_CMD_SEEK, NEXPLAYER_ASYNC_CMD_FORWARD, NEXPLAYER_ASYNC_CMD_BACKWARD:</b><br />
             *                      The actual position at which the seek, forward or backward command completed.  Depending on the
             *                      media format, this may be different than the position that was requested for the seek operation.
             *                  </ul>
             * @param param2    A value specific to the command that has completed.  Currently
             *                  there are no commands that pass this parameter, and it is
             *                  reserved for future use.  Applications should ignore this value.
             */
            void onAsyncCmdComplete( NexPlayer mp, int command, int result, int param1, int param2 );
            
            /**
             * Reports RTSP command Timeout.
             */
            void onRTSPCommandTimeOut( NexPlayer mp );
            
            /**
             * Reports Pause Supervision Timeout.
             */
            void onPauseSupervisionTimeOut( NexPlayer mp );
            
            /**
             * Reports Data Inactivity Timeout.
             */
            void onDataInactivityTimeOut( NexPlayer mp );
            
            /**
             * Starts buffering.
             */
            void onBufferingBegin( NexPlayer mp );
            
            /**
             * Buffering end.
             */
            void onBufferingEnd( NexPlayer mp );
            
            /**
             * Reports current buffering status.
             * 
             * @param progress_in_percent
             *            Buffering percentage
             */
            void onBuffering( NexPlayer mp, int progress_in_percent );
            
            /**
             * \brief Notification that the audio rendering thread has been created.
             * 
             * Under previous versions of the SDK, it was necessary to create and
             * manage the audio renderer.  However, under the current version this
             * is done automatically, and the \c onAudioRenderCreate method should
             * be empty or contain only diagnostic code.
             * 
             * @param mp
             *            NexPlayer object to which this event applies.
             * @param samplingRate
             *            Sample rate (in hz) of the content to be played back.
             * @param channelNum
             *            Number of channels in the content (1=mono, 2=stereo)
             */
            void onAudioRenderCreate( NexPlayer mp, int samplingRate, int channelNum );
            
            /**
             * \brief Notification that the audio rendering thread has been destroyed.
             * 
             * Under previous versions of the SDK, it was necessary to destroy
             * the audio renderer.  However, under the current version this
             * is done automatically, and the \c onAudioRenderDelete method should
             * be empty or contain only diagnostic code.
             * 
             * @param mp
             *            NexPlayer object to which this event applies.
             */
            void onAudioRenderDelete( NexPlayer mp );
            
            /**
             * Called when NexPlayer needs the application to create a surface on which
             * to render the video.<p>
             * 
             * The application must respond to this by calling 
             * {@link NexPlayer#setDisplay(SurfaceHolder) setDisplay}.<p>
             * 
             * Generally speaking, the application will actually create the surface earlier,
             * during GUI layout, and will simply use the existing handle in response to this
             * call.  There are, however, some threading considerations.  See 
             * {@link NexPlayer#setDisplay(SurfaceHolder) setDisplay} for details.<p>
             * 
             * @param mp
             *            NexPlayer object to which this event applies.
             * @param width
             *            Source Video width
             * @param height
             *            Source Video height
             * @param rgbBuffer
             *            Direct RGB Buffer(RGB565 format)
             *            This RGB buffer is shared with NexPlayerEngine native code.
             */
            void onVideoRenderCreate( NexPlayer mp, int width, int height, Object rgbBuffer );
            
            /**
             * Called when NexPlayer no longer needs the render surface.<p>
             * 
             * If a surface was created in <code>onVideoRenderCreate</code>, this is the
             * place to destroy it.  However, if (as in most cases) an existing surface
             * was used, then this function need not take any special action, other than
             * updating whatever state the application needs to track.<p>
             * 
             * @param mp
             *            NexPlayer object to which this event applies.
             */
            void onVideoRenderDelete( NexPlayer mp);
            
            /**
             * Request to display Video frame data at JAVA application
             * 
             */
            void onVideoRenderRender( NexPlayer mp );
            
            /**
             * Called when a frame of video has been captured.
             * 
             * After calling {@link NexPlayer#captureVideo(int, int) captureVideo} to
             * set up video capture, this function will be called whenever a frame is
             * captured, and can process the captured frame as necessary.
             * 
             * <pre><code>
             Bitmap bitmap = Bitmap.createBitmap(width, height, pixelbyte==2?Config.RGB_565:Config.ARGB_8888 );
             ByteBuffer RGBBuffer = (ByteBuffer)rgbBuffer;
             RGBBuffer.asIntBuffer();
             bitmap.copyPixelsFromBuffer(RGBBuffer);
             * </code></pre>
             * 
             * @param mp
             *            NexPlayer object to which this event applies.
             * @param width
             *            Width of the captured frame.
             * @param height
             *            Height of the captured frame.
             * @param pixelbyte
             *            Number of bytes per pixel (2 for RGB565; 4 for RGBA).
             * @param bitmap
             */
            void onVideoRenderCapture(NexPlayer mp, int width, int height, int pixelbyte, Object bitmap );
            
            /**
             * Called when initially beginning playback of media content with 
             * associated subtitles.
             * 
             * @param mp
             *          NexPlayer object to which this event applies.
             * 
             * @param numTracks
             *          Number of subtitle tracks available for this media.  Note
             *          that this may 0 if there are no subtites, or this function
             *          may not be called at all.
             */
            void onTextRenderInit( NexPlayer mp, int numTracks );
            
            /**
             * Called when new subtitle data is ready for display.<p>
             * 
             * This is called whenever playback reaches a point in time where subtitles on any
             * track need to be displayed or cleared.<p>
             * 
             * The text to display is provided in a NexID3TagText object as a byte array; 
             * it is the responsibility of the application to convert this to text in the appropriate 
             * encoding.  Where possible, the encoding information will be provided in the 
             * NexID3TagText::mEncodingType, but many subtitle
             * file formats do not explicity specify an encoding, so it may be necessary for
             * the application to guess the encoding or allow the user to select it.<p>
             * 
             * <i>HISTORIAL NOTE 1:</i> In previous API versions, it was the responsibility of the
             * application to handle the case where there were multiple tracks in a file by
             * filtering based <code>trackIndex</code>.  This is no longer necessary (or even
             * possible) as that functionality has been replaced by 
             * {@link NexPlayer#setCaptionLanguage(int) setCaptionLanguage} and <code>trackIndex</code> 
             * is no longer used and is alwyays zero.<p>
             *
             *  <i>HISTORIAL NOTE 2:</i> In previous API versions, the third argument of this method
             * was a Java byte array, and encoding information was not specified.<p>
             * 
             * @param mp
             *          NexPlayer object to which this event applies.
             * 
             * @param trackIndex
             *          This is always zero and should always be ignored.
             * 
             * @param textInfo
             *          The text to be displayed (cast this to a NexID3TagText object)
             */
            void onTextRenderRender( NexPlayer mp, int trackIndex, Object textInfo );
            
            /* Possible value for <code>command</code> parameter of {@link IListener#onAsyncCmdComplete(NexPlayer, int, int) onAsyncCmdComplete}. */
            // public static final int NEXPLAYER_ASYNC_CMD_NONE             = 0x00000000;
            
            /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. 
             * @deprecated Renamed; use {@link NexPlayer#NEXPLAYER_STATUS_REPORT_NONE} instead. */
            public static final int eNEXPLAYER_STATUS_NONE              = 0x00000000;
            /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. 
             * @deprecated Renamed; use {@link NexPlayer#NEXPLAYER_STATUS_REPORT_AUDIO_GET_CODEC_FAILED} instead. */
            public static final int eNEXPLAYER_AUDIO_GET_CODEC_FAILED   = 0x00000001;
            /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. 
             * @deprecated Renamed; use {@link NexPlayer#NEXPLAYER_STATUS_REPORT_VIDEO_GET_CODEC_FAILED} instead. */
            public static final int eNEXPLAYER_VIDEO_GET_CODEC_FAILED   = 0x00000002;
            /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. 
             * @deprecated Renamed; use {@link NexPlayer#NEXPLAYER_STATUS_REPORT_AUDIO_INIT_FAILED} instead. */
            public static final int eNEXPLAYER_AUDIO_INIT_FAILED        = 0x00000003;
            /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. 
             * @deprecated Renamed; use {@link NexPlayer#NEXPLAYER_STATUS_REPORT_VIDEO_INIT_FAILED} instead. */
            public static final int eNEXPLAYER_VIDEO_INIT_FAILED        = 0x00000004;
            /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. 
             * @deprecated Renamed; use {@link NexPlayer#NEXPLAYER_STATUS_REPORT_TRACK_CHANGED} instead. */
            public static final int eNEXPLAYER_TRACK_CHANGED            = 0x00000005;
            /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. 
             * @deprecated Renamed; use {@link NexPlayer#NEXPLAYER_STATUS_REPORT_STREAM_CHANGED} instead. */
            public static final int eNEXPLAYER_STREAM_CHANGED           = 0x00000006;
            /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. 
             * @deprecated Renamed; use {@link NexPlayer#NEXPLAYER_STATUS_REPORT_DSI_CHANGED} instead. */
            public static final int eNEXPLAYER_DSI_CHANGED              = 0x00000007;
            /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. 
             * @deprecated Renamed; use {@link NexPlayer#NEXPLAYER_STATUS_REPORT_OBJECT_CHANGED} instead. */
            public static final int eNEXPLAYER_OBJECT_CHANGED           = 0x00000008;
            /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. 
             * @deprecated Renamed; use {@link NexPlayer#NEXPLAYER_STATUS_REPORT_CONTENT_INFO_UPDATED} instead. */
            public static final int eNEXPLAYER_CONTENT_INFO_UPDATED     = 0x00000009;
            /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onStatusReport(NexPlayer, int, int) onStatusReport}. 
             * @deprecated Renamed; use {@link NexPlayer#NEXPLAYER_STATUS_REPORT_MAX} instead. */
            public static final int NEXPLAYER_STATUS_MAX                = 0xFFFFFFFF;
            
            
            /**
             * Called when there is a change in the available content information.<p>
             * 
             * This can happen, for example, if the track changes during HLS playback,
             * resulting in changes to the bitrate, resolution, or even the codec
             * in use.<p>
             * 
             * The <code>msg</code> parameter contains information about the condition
             * that has changed.<p>
             * 
             * Because multiple calls to this function can be issed for the same event,
             * unknown values for <code>msg</code> should generally be ignored.  To handle
             * general status changes that affect content information without processing
             * duplicate messages, the best approach is just to handle <code>eNEXPLAYER_CONTENT_INFO_UPDATED</code>.<p>
             * 
             * To determine the new content information when this event occurs, call 
             * {@link NexPlayer#getContentInfo() getContentInfo} or {@link NexPlayer#getContentInfoInt(int) getContentInfoInt}.<p>
             * 
             * @param mp
             * 			NexPlayer object to which this event applies.
             * 
             * @param msg The type of notification.  This is one of the following values:
             * 
             * <ul>
             * <li><b>{@link NexPlayer#NEXPLAYER_STATUS_REPORT_NONE NEXPLAYER_STATUS_REPORT_NONE} (0x00000000) </b>
             * 			No status change (this value is not normally passed to <code>onStatusReport</code>, and
             * 			shoudl generally be ignored). </li>
             * <li><b>{@link NexPlayer#NEXPLAYER_STATUS_REPORT_AUDIO_GET_CODEC_FAILED NEXPLAYER_STATUS_REPORT_AUDIO_GET_CODEC_FAILED} (0x00000001) </b>
             * 			Failed to determine the audio codec.  This notification can happen at the beginning of
             * 		    playback, or during playback if there is an audio codec change.  This can happen because of a
             * 			switch to a new codec that NexPlayer does not support, or due to an error in the format 
             * 			of the content or corrupted data in the content.</li>
             * <li><b>{@link NexPlayer#NEXPLAYER_STATUS_REPORT_VIDEO_GET_CODEC_FAILED NEXPLAYER_STATUS_REPORT_VIDEO_GET_CODEC_FAILED} (0x00000002) </b>
             * 			Failed to determine the video codec.  This notification can happen at the beginning of
             * 		    playback, or during playback if there is a video codec change.  This can happen because of a
             * 			switch to a new codec that NexPlayer does not support, or due to an error in the format 
             * 			of the content or corrupted data in the content.</li>
             * <li><b>{@link NexPlayer#NEXPLAYER_STATUS_REPORT_AUDIO_INIT_FAILED NEXPLAYER_STATUS_REPORT_AUDIO_INIT_FAILED} (0x00000003) </b>
             * 			The audio codec failed to initialize.  This can happen for several reasons.  The container may
             * 			indicate the wrong audio codec, or the audio stream may be incorrect or corrupted, or the audio
             * 			stream may use a codec version or features that NexPlayer doesn't support.</li>
             * <li><b>{@link NexPlayer#NEXPLAYER_STATUS_REPORT_VIDEO_INIT_FAILED NEXPLAYER_STATUS_REPORT_VIDEO_INIT_FAILED} (0x00000004) </b>
             * 			The video codec failed to initialize.  This can happen for several reasons.  The container may
             * 			indicate the wrong video codec, or the video stream may be incorrect or corrupted, or the video
             * 			stream may use a codec version or features that NexPlayer doesn't support.</li>
             * <li><b>{@link NexPlayer#NEXPLAYER_STATUS_REPORT_TRACK_CHANGED NEXPLAYER_STATUS_REPORT_TRACK_CHANGED} (0x00000005) </b>
             * 			The track has changed. This happens for protocols such as HLS that provide the content
             * 			in multiple formats or at multiple resolutions or bitrates.  The ID of the new track can
             * 			be found in {@link NexContentInformation#mCurrTrackID}, and also in <code>param1</code>.
             * 			<i>When this event occurs, NexPlayer also generates a eNEXPLAYER_CONTENT_INFO_UPDATED event.</i></li>
             * <li><b>{@link NexPlayer#NEXPLAYER_STATUS_REPORT_STREAM_CHANGED NEXPLAYER_STATUS_REPORT_STREAM_CHANGED} (0x00000006) </b>
             * 			The stream being played back has changed (between the states Audio-Only, Video-Only and Audio+Video).
             * 			The new stream type is in {@link NexContentInformation#mMediaType}, and also in <code>param1</code>.</li>
             * <li><b>{@link NexPlayer#NEXPLAYER_STATUS_REPORT_DSI_CHANGED NEXPLAYER_STATUS_REPORT_DSI_CHANGED} (0x00000007) </b>
             * 			An attribute relating to the video or audio format (such as the resolution, bitrate, etc.) has changed.</li>
             * <li><b>{@link NexPlayer#NEXPLAYER_STATUS_REPORT_OBJECT_CHANGED NEXPLAYER_STATUS_REPORT_OBJECT_CHANGED} (0x00000008) </b>
             * 			One of the codec objects in use has changed (that is, the audio or video codec in use
             * 			has changed). 
             * 			See {@link NexContentInformation#mAudioCodec} and
             * 				{@link NexContentInformation#mVideoCodec} to get the ID of the new codec.</li>
             * <li><b>{@link NexPlayer#NEXPLAYER_STATUS_REPORT_CONTENT_INFO_UPDATED NEXPLAYER_STATUS_REPORT_CONTENT_INFO_UPDATED} (0x00000009) </b>
             * 			The content information has changed.  When onStatusReport is called with any other non-Failure
             * 			value for <code>msg</code>, it will also be called with this one as well.  This is a good
             * 			place to monitor for any non-specific change to the content information.</li>
             * <li><b>{@link NexPlayer#NEXPLAYER_STATUS_REPORT_AVMODE_CHANGED NEXPLAYER_STATUS_REPORT_AVMODE_CHANGED} (0x0000000A) </b>
             * 			The stream being played back has changed and the new stream
             *			has a different media type.  This event happens whenever the state changes between 
             *			video-only, audio-only and audio-video. <code>param1</code> contains the new media type: 1 for audio, 2 for video, 3 for both.</li>
             * <li><b>{@link NexPlayer#NEXPLAYER_STATUS_REPORT_HTTP_INVALID_RESPONSE NEXPLAYER_STATUS_REPORT_HTTP_INVALID_RESPONSE} (0x0000000B) </b>
             * 			An HTTP error response was received from the server.  <code>param1</code> contains the error code (this is
             * 			a normal HTTP response code, such as 404, 500, etc.)</li>
             * <li><b>{@link NexPlayer#NEXPLAYER_STATUS_REPORT_MAX NEXPLAYER_STATUS_REPORT_MAX} (0xFFFFFFFF) </b>
             * 			This value is reserved; do not use it.</li>
             * </ul>
             * 
             * @param param1
             * 			Additional information.  The meaning of this depends on the value of <code>msg</code>.  If the description
             * 			above doesn't refer to <code>param1</code>, then this parameter is undefined for that value of
             * 			<code>msg</code> and should not be used.
             */
            void onStatusReport( NexPlayer mp, int msg, int param1);
            
            /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onDebugInfo(NexPlayer, int, String) onDebugInfo}. */
            public static final int NEXPLAYER_DEBUGINFO_RTSP            = 0x00;
            /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onDebugInfo(NexPlayer, int, String) onDebugInfo}. */
            public static final int NEXPLAYER_DEBUGINFO_RTCP_RR_SEND    = 0x01;
            /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onDebugInfo(NexPlayer, int, String) onDebugInfo}. */
            public static final int NEXPLAYER_DEBUGINFO_RTCP_BYE_RECV   = 0x02;
            /** Possible value for <code>msg</code> parameter of {@link NexPlayer.IListener#onDebugInfo(NexPlayer, int, String) onDebugInfo}. */
            public static final int NEXPLAYER_DEBUGINFO_CONTENT_INFO    = 0x03;
            
            
            /**
             * Provides debugging and diagnostic information during playback.  The information provided
             * here is for debugging purposes only; the contents of the strings provided may change in future
             * versions, so do not attempt to parse them or make programmating decisions based on the contents.
             * Also, do not make assumptions about line length or number of lines.<p>
             * 
             * <b>Superceded:</b> The relevant information provided in the freeform text strings
             *                    that used to be passed to this method is now available directly
             *                    in NexContentInformation.<p>
             * 
             * @param mp
             *          NexPlayer object to which this event applies.
             * 
             * @param msg
             *          Identifies the type of debugging information being provided.  This is one of the followng values:
             * <ul>
             * <li><b>{@link NexPlayer.IListener#NEXPLAYER_DEBUGINFO_RTSP NEXPLAYER_DEBUGINFO_RTSP} (0x00)</b>
             *      Debugging information related to the RTSP connection status.</li>
             * <li><b>{@link NexPlayer.IListener#NEXPLAYER_DEBUGINFO_RTCP_RR_SEND NEXPLAYER_DEBUGINFO_RTCP_RR_SEND} (0x01)</b>
             *      Debugging information associated with the transmission of an RTCP RR (Receiver Report) packet.</li>
             * <li><b>{@link NexPlayer.IListener#NEXPLAYER_DEBUGINFO_RTCP_BYE_RECV NEXPLAYER_DEBUGINFO_RTCP_BYE_RECV} (0x02)</b>
             *      This occurs when an RTCP BYE packet is received.</li>
             * <li><b>{@link NexPlayer.IListener#NEXPLAYER_DEBUGINFO_CONTENT_INFO NEXPLAYER_DEBUGINFO_CONTENT_INFO} (0x03)</b>
             *      General information about the content that is currently playing.  This is intended to be shown
             *      in a "heads-up" style overlay or suplementary display, and replaces information provided in
             *      any previous <code>NEXPLAYER_DEBUGINFO_CONTENT_INFO</code> calls.</li>
             * </ul>
             * @param strDbg
             *      A string containing the debugging information associated with the event.  This may contain
             *      multiple lines of text.
             */
            //void onDebugInfo( NexPlayer mp, int msg, String strDbg);
        }
}
