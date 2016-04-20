package com.nextreaming.nexplayerengine;

import android.util.Log;

/**
 * Contains information about the meta-data associated with the  
 * content, from sources such as ID3 tags.
 * 
 * This includes a series of text fields (the set of fields that actually
 * contain information will vary depending on the format and which
 * fields the content creator has filled in).  These fields include
 * information such as the album name, artist, lyrics and so on.
 * 
 * This also includes the picture associated with the content, for formats 
 * such as MP3 and AAC that can have an optional associated still image.
 *  
 * This is generally used in place of video for content that does not
 * have video.  The exact use of the still image is up to the content
 * producer. In the case of an MP3 or AAC audio file, it is usually
 * the album cover artwork.  In the case of HTTP Live Streaming,
 * audio-only tracks often have a still image to be shown in place of
 * the video.  This image may change during playback (for example,
 * some audio-only HLS streams provide a new image every ten seconds
 * or so).
 * 
 * @author Nextreaming
 */
public class NexID3TagInformation {

	private NexID3TagText mTitle;
	private NexID3TagText mAlbum;
	private NexID3TagText mArtist;
	private NexID3TagText mDate;
	private NexID3TagText mGenre;
	private NexID3TagText mSessionInfo;
	private NexID3TagText mTrackNum;
	private NexID3TagText mYear;
	private NexID3TagPicture mPicture;
	private NexID3TagText mLyric;

	private NexID3TagInformation(	NexID3TagText title, NexID3TagText album, NexID3TagText artist,
									NexID3TagText date, NexID3TagText genre, NexID3TagText sessionInfo, NexID3TagText trackNum,
									NexID3TagText year, NexID3TagPicture picture, NexID3TagText lyric)
	{
		Log.d("ID3Tag", "ID3Tag Constructor");
		mTitle = title;
		mAlbum = album;
		mArtist = artist;
		mDate = date;
		mGenre = genre;
		mSessionInfo = sessionInfo;
		mTrackNum = trackNum;
		mYear = year;
		mPicture = picture;
		mLyric = lyric;
	}
	
	public NexID3TagText getTitle()
	{
		return mTitle;
	}

	public NexID3TagText getAlbum()
	{
		return mAlbum;
	}
	
	public NexID3TagText getArtist()
	{
		return mArtist;
	}
	
	public NexID3TagText getDate()
	{
		return mDate;
	}
	
	public NexID3TagText getSessionInfo()
	{
		return mSessionInfo;
	}
	
	public NexID3TagText getTrackNumber()
	{
		return mTrackNum;
	}
	
	public NexID3TagText getYear()
	{
		return mYear;
	}
	
	public NexID3TagPicture getPicture()
	{
		return mPicture;
	}

	public NexID3TagText getLyric() 
	{
		return mLyric;
	}

	
	/**
	 * 
	 * @param size		
	 * @param mimeType	
	 * @param data		
	 */

	/**
	 * The sole constructor.
	 * 
	 * @param title			Initial value for mTitle
	 * @param album			Initial value for mAlbum
	 * @param artist		Initial value for mArtist
	 * @param date			Initial value for mDate
	 * @param genre			Initial value for mGenre
	 * @param sessioninfo	Initial value for mSessionInfo
	 * @param trackNum		Initial value for mTrackNum
	 * @param year			Initial value for mYear
	 * @param size			Initial value for mSize (should match data.length)
	 * @param mimeType		Initial value for mMimeType
	 * @param data			Initial value for mByteData
	 */
	/*
	public NexID3TagInformation( 	String title, String album, String artist,
								String date, String genre, String sessioninfo, 
								String trackNum, String year,
								int size, String mimeType, byte[] data)
	{
		mTitle = title;
		mAlbum = album;
		mArtist = artist;
		mDate = date;
		mGenre = genre;
		mSessionInfo = sessioninfo;
		mTrackNum = trackNum;
		mYear = year;

		mImageSize = size;
		mImageMimeType = mimeType;
		mImageByteData = data;
	}
	*/

	
	/** The image data, encoded according to the MIME type in mMimeType */
	//public byte[] 	mImageByteData;
	
	/** The size of the image data, in bytes.  This is the same as mByteData.length */
	//public int 		mImageSize;
	
	/** The MIME type of the data in mByteData.  This can be used to determine how to decode the image data. */
	//public String 	mImageMimeType;
	
	/** The 'Title' ID3 tag or equivalent. Often a song title. */
	//public String 	mTitle;
	/** The 'Album' ID3 tag or equivalent. Usually the name of the album containing the song. */
	//public String	mAlbum;
	/** The 'Artist' ID3 tag or equivalent. Usually the name of the performer. */
	//public String 	mArtist;
	/** The 'Date' ID3 tag or equivalent. Usually the date when the content was produced. This is a string and the internal format may vary depending on the content type and content producer. */	
	//public String 	mDate;
	/** The 'Genre' ID3 tag or equivalent. For formats that store genre as a number, the number is converted into the appropriate string and the string is given here. */
	//public String 	mGenre;
	/** The 'Session Info' ID3 tag or equivalent. */
	//public String	mSessionInfo;
	/** The 'Track Number' ID3 tag or equivalent. For content that is also available on a CD, this is usually the matching track number from the CD. */
	//public String	mTrackNum;
	/** The 'Year' ID3 tag or equivalent. This is usually the year when the album was produced or the content was produced, where applicable. */
	//public String	mYear;
}
