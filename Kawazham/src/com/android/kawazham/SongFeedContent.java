package com.android.kawazham;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.kawazham.util.KawaUtil;

public class SongFeedContent {
	private FeedContentCallback mCallback;
	private Context ctx;
	
	public static final String NO_DATA_FOUND = "com.android.kawazham.NO_DATA_FOUND";
	public static final String NO_DATA_CONNECTION = "com.android.kawazham.NO_DATA_CONNECTION";
	public static final String ERROR_CAUGHT_PRE = "com.android.kawazham.ERROR_CAUGHT: ";
	public static final String ERROR_CAUGHT = "com.android.kawazham.ERROR_CAUGHT: %1s";
		
	private final String URI_SONG_FEED = "http://www.shazam.com/music/web/taglistrss?mode=xml&userName=shazam";
	private final int FEED_CONTENTS_CONNECTION_TIMEOUT = 5000;
	
	public List<SongItem> listSongItems = new ArrayList<SongItem>();

	public Map<String, SongItem> mapSonItems = new HashMap<String, SongItem>();

	
	public SongFeedContent(FeedContentCallback callback, Context aCtx) {
		mCallback = callback;
		ctx = aCtx;
		
		DownloadSongFeed dwnSongFeedTask = new DownloadSongFeed();
		dwnSongFeedTask.execute(new String[] {URI_SONG_FEED});
	}


	private void addItem(SongItem item) {
		listSongItems.add(item);
		mapSonItems.put(item.id, item);
	}

	public int getCount() {
		return listSongItems.size();
	}
	
	
	/**
	 * A dummy item representing a piece of content.
	 */
	public class SongItem {
		public String id;
		public String songTitle;
		public String songArtist;
		public String publishedDate;
		public String trackLink;

		public SongItem(String id, String songTitle, String songArtist, String publishedDate, String tracklLink) {
			this.id = id;
			this.songTitle = songTitle;
			this.songArtist = songArtist;
			this.publishedDate = publishedDate;
			this.trackLink = tracklLink;
		}

		@Override
		public String toString() {
			return songTitle;
		}
	}
	
	
	public class DownloadSongFeed extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... aUrl) {
			String resStr = NO_DATA_FOUND;
			try {
				KawaUtil mKawaUtil = new KawaUtil();
				if (mKawaUtil.isConnectivityOn(ctx)) {
					Connection.Response respObj = Jsoup.connect(URI_SONG_FEED)
							.timeout(FEED_CONTENTS_CONNECTION_TIMEOUT)
							.execute();


					if (respObj.statusCode() == 200)  
						resStr = respObj.body();
				} else 
					resStr = NO_DATA_CONNECTION;

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				resStr = String.format(ERROR_CAUGHT, e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				resStr = String.format(ERROR_CAUGHT, e.getMessage());
			}
			
			return resStr;
		}

		@Override
		protected void onPostExecute(String result) {
			if (isResultValid(result)) {
				int mCnt = 0;
				Document mDoc = Jsoup.parse(result, "", Parser.xmlParser());
				for (Element eItem : mDoc.select("item")) {
					String songTitle = eItem.select("trackName").text();
					String songArtist = eItem.select("trackArtist").text();
					String publishedDate = eItem.select("pubDate").text();
					String tracklLink = eItem.select("link").text();
					addItem(new SongItem(String.valueOf(mCnt++), songTitle, songArtist, publishedDate, tracklLink));
					Log.d("Kywazham", String.valueOf(mCnt) + " - " + songTitle);
				}
				mCallback.gotResults();
			} else
				mCallback.gotErrors(result);
			
		}
	}
	
	private boolean isResultValid(String result) {
		return ((!result.isEmpty()) && 
				(!result.equalsIgnoreCase(NO_DATA_FOUND)) &&
				(!result.equalsIgnoreCase(NO_DATA_CONNECTION)) &&
				(!result.startsWith(ERROR_CAUGHT_PRE))) ;
	}
}
