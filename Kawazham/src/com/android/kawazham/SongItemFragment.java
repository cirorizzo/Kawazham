package com.android.kawazham;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class SongItemFragment extends Fragment {
	public final static String ARG_TRACK_LINK = "com.android.kawazham.tracklink";
	
	private String mTrackLink;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getArguments().containsKey(ARG_TRACK_LINK)) 
			mTrackLink = getArguments().getString(ARG_TRACK_LINK);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_song_list, container, false);
		
		if (mTrackLink != null) 
			((WebView) rootView.findViewById(R.id.webVwSongDetail)).loadUrl(mTrackLink);
		
		return rootView;
	}
}
