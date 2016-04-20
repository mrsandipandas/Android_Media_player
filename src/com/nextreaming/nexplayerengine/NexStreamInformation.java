package com.nextreaming.nexplayerengine;

import android.util.Log;

/**
 * Provides information on a content stream.  
 * 
 * Content streams are listed in the
 *  {@link NexContentInformation#mArrStreamInformation mArrStreamInformation}
 *  member of {@link NexContentInformation}. See there for details.
 *  
 * @author Nextreaming Corporation
 *
 */
public final class NexStreamInformation
{
	/** 
	 * A unique integer used to identify this stream in API calls.
	 * For example, this is used when calling 
	 * {@link NexPlayer#setMediaStream(int, int, int) setMediaStream }.
	 */
	public int 		mID;
	
	/** 
	 * The stream type (audio or video).
	 * This is one of:
	 * <ul>
	 * <li><b>(0x00)</b>{@link NexPlayer#MEDIA_STREAM_TYPE_AUDIO MEDIA_STREAM_TYPE_AUDIO}
	 * <li><b>(0x01)</b>{@link NexPlayer#MEDIA_STREAM_TYPE_VIDEO MEDIA_STREAM_TYPE_VIDEO}
	 * <li><b>(0x02)</b>{@link NexPlayer#MEDIA_STREAM_TYPE_TEXT MEDIA_STREAM_TYPE_TEXT}
	 * </ul> 
	 */
	public int 		mType;
	
	/**
	 * The name of the media stream, for streaming formats that
	 * have named streams.  This is an arbitrary value set by the
	 * author, and is generally intended for user display (to allow
	 * the user to select among different alternative streams).
	 */
	public NexID3TagText mName;
	//public String	mName;
	
	/**
	 * The language of the media stream, for streaming formats that
	 * include language data.  This is an arbitrary value set by the
	 * author, and is intended for user display (to allow users to
	 * select among different alternative streams).  Applications
	 * should NOT rely on this being any particular format; it is
	 * most likely to be the display name of the language, but may
	 * be any string.
	 */
	public NexID3TagText mLanguage;
	//public String	mLanguage;
	
	/**
	 * Number of custom attributes associated with this stream.
	 */
	public int 		mAttrCount;
	
	/**
	 * Number of tracks associated with this stream.  This is the
	 * same as the length of mArrTrackInformation, and may be zero.
	 */
	public int 		mTrackCount;

	/**
	 * The ID of the track within this stream that is currently
	 * playing, or -1 if no track in this stream is currently playing.
	 * This ID matches a value in <code>mArrTrackInformation[].mTrackID</code>.
	 * If the <code>mArrTrackInformation</code> array is empty, this value
	 * is undefined.
	 */
	public int		mCurrTrackID;
	
	/**
	 * The ID of the custom attribute within this stream that is currently
	 * active, or -1 if no custom attribute in this stream is currently active.
	 * This ID matches a value in <code>NexCustomAttribInformation[].mID</code>.
	 * If the <code>NexCustomAttribInformation</code> array is empty, this value
	 * is undefined.
	 */
	public int 		mCurrCustomAttrID;
	
	/**
	 * For formats such as HLS that support multiple tracks for
	 * a given stream, this is an array containing information on
	 * each track associated with this stream.  This may be an
	 * empty array for formats that don't have track information.
	 */
	public NexTrackInformation[] mArrTrackInformation;
	
	/**
	 * An array of the custom attributes associated with this
	 * stream, for formats such as Smooth Streaming that support
	 * custom attributes.
	 */
	public NexCustomAttribInformation[] mArrCustomAttribInformation;
	
	/**
	 * Sole constructor for NexStreamInformation. The parameters match
	 * the members of the class one-to-one. Generally, it is not
	 * necessary to call the constructor; rather, objects of this class
	 * are created by NexPlayer internally and made available through
	 * {@link NexContentInformation#mArrStreamInformation}.
	 * @param iID			Initial value for mID member
	 * @param iType			Initial value for mType member
	 * @param currCustomAttrId Initial value for mCurrCustomAttrId member
	 * @param currTrackId 	Initial value for mCurrTrackId member
	 * @param name			Initial value for mName member
	 * @param language		Initial value for mLanguage member
	 */
	public NexStreamInformation( int iID, int iType, int currCustomAttrId, int currTrackId, NexID3TagText name, NexID3TagText language)
	{
		mID = iID;
		mType = iType;
		mName = name;
		mLanguage = language;
		mCurrCustomAttrID = currCustomAttrId;
		mCurrTrackID = currTrackId;
	}
	
	/*public void clearStreamInformation()
	{
		mID		= 0;
		mType	= 0;
		mName	= "";
		mLanguage = "";
		
		mAttrCount = 0;
		mTrackCount = 0;
	}*/
	
	@SuppressWarnings("unused")		// Called from native code
	private void copyCustomAttribInformation(NexCustomAttribInformation[] customAttribInformation)
	{
		mArrCustomAttribInformation = customAttribInformation;
		mAttrCount = mArrCustomAttribInformation.length;
	}
	
	@SuppressWarnings("unused")		// Called from native code
	private void copyTrackInformation(NexTrackInformation[] trackInformation)
	{
		mArrTrackInformation = trackInformation;
		mTrackCount = mArrTrackInformation.length;
	}
}
