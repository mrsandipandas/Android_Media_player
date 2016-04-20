package com.nextreaming.nexplayerengine;

/**
 * Information about an individual content track, for formats that
 * use multiple tracks (such as HLS).  See {@link NexContentInformation} for details.
 * 
 * @author Nextreaming Corporation
 */
public final class NexTrackInformation
{
	/** 
	 * The ID of the track.  This is an arbitrary value, not an index, but can be matched
	 * to the currently playing track as indicated by {@link NexStreamInformation#mCurrTrackID}. 
	 */
	public int mTrackID;

	/** 
	 * The Custom Attribute ID of the track.  In some cases, a stream may have multiple
	 * equivalent tracks.  Setting a custom attribute ID in 
	 * {@link NexPlayer#setMediaStream(int, int, int) setMediaStream} causes only tracks
	 * with a matching custom attribute ID to be selected.  A custom attribute ID
	 * represents a particular key/value attribute pair.  The full list of available pairs
	 * and their associated ID values can be found in 
	 * {@link NexStreamInformation#mArrCustomAttribInformation mArrCustomAttribInformation}.<p>
	 * 
	 * Please keep in mind that this is an arbitrary value, not an index into the custom
	 * attribute array. 
	 */
	public int mCustomAttribID;
	
	/**
	 *  Bandwidth of the track in bytes per second 
	 */
	public int mBandWidth;
	
	/** 
	 * Track type
	 * 
	 * <ul>
	 * <li>1 : Audio Only</li>
	 * <li>2 : Video Only</li>
	 * <li>3 : AV</li>
	 * </ul> 
	 */
	public int mType;
	
	/**
	 * Indicates if this track is valid (that is, if the codecs and bit rates and so on are
	 * supported by NexPlayer).<p>
	 * 
	 * <ul>
	 * <li>0 : Unsupported or invalid track</li>
	 * <li>1 : Valid and supported track</li>
	 * </ul> 
	 */
	public int mValid;

    /** Possible value for NexTrackInfomration::mReason */ 
	public final static int REASON_TRACK_NOT_SUPPORT_VIDEO_CODEC		= 0x0000001;
    /** Possible value for NexTrackInfomration::mReason */ 
	public final static int REASON_TRACK_NOT_SUPPORT_AUDIO_CODEC		= 0x0000002;
    /** Possible value for NexTrackInfomration::mReason */ 
	public final static int REASON_TRACK_NOT_SUPPORT_VIDEO_RESOLUTION	= 0x0000003;
    /** Possible value for NexTrackInfomration::mReason */ 
	public final static int REASON_TRACK_NOT_SUPPORT_VIDEO_RENDER		= 0x0000004;
	
    /**
     * For invalid tracks, indicates the reasion they a not currently valid.
     *
     * This may be any of the following values:
     *
     * - ::REASON_TRACK_NOT_SUPPORT_VIDEO_CODEC         if the player doens't support the video codec used for ths content
     * - ::REASON_TRACK_NOT_SUPPORT_AUDIO_CODEC         if the player doens't support audio video codec used for ths content
     * - ::REASON_TRACK_NOT_SUPPORT_VIDEO_RESOLUTION    if the track is locked out because the video resolution is too high to play, as determined by the setting of the ::MAX_HEIGHT and ::MAX_WIDTH properties.
     * - ::REASON_TRACK_NOT_SUPPORT_VIDEO_RENDER        if the track was locked out because the video renderer wasn't capable of playing it smoothly (resolution and/or bitrate too high)
     */
	public int mReason;
	
	/**
	 * Sole initializer for this class.<p>
	 * 
	 * The arguments match the names of the relevant member variables, and
	 * are simply assigned on a 1-to-1 basis.<p>
	 * 
	 * @param iTrackID initializes mTrackID
	 * @param iCustomAttribID initializes mCustomAttribID
	 * @param iBandWidth initializes mBandWidth
	 * @param iType initializes mType
	 * @param iValid initializes mValid
	 */
	public NexTrackInformation( int iTrackID, int iCustomAttribID, int iBandWidth, int iType, int iValid, int iReason)
	{
		mTrackID = iTrackID;
		mCustomAttribID = iCustomAttribID;
		mBandWidth = iBandWidth;
		mType = iType;
		mValid = iValid;
		mReason = iReason;
	}
}
