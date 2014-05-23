package com.android.kawazham;

public interface FeedContentCallback {
	void gotResults();
	
	void gotErrors(String aError);
}
