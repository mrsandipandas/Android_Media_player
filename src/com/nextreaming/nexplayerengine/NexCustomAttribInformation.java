package com.nextreaming.nexplayerengine;

/**
 * Information about an individual content track, for formats that
 * use multiple tracks (such as HLS).  See {@link NexContentInformation} for details.
 * 
 * @author Nextreaming Corporation
 */
public final class NexCustomAttribInformation
{
	/**
	 * Unique arbitrary integer ID for this custom attribute. This is used
	 * when calling {@link NexPlayer#setMediaStream(int, int, int)}; see
	 * there for further details.
	 */
	public int 		mID;
	
	public NexID3TagText mName;
	public NexID3TagText mValue;
	
	/** The name of the attribute */
	//public String	mName;
	
	/** The value of the attribute */
	//public String	mValue;
	
	private NexCustomAttribInformation( int iID, NexID3TagText name, NexID3TagText value)
	{
		mID = iID;
		mName = name;
		mValue = value;
	}
}
