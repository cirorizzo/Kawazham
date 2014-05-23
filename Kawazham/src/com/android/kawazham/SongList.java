package com.android.kawazham;

import java.util.List;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.kawazham.SongFeedContent.SongItem;
import com.android.kawazham.util.KawaUtil;
import com.android.kawazham.util.UIDialogMessage;

public class SongList extends ListActivity implements FeedContentCallback {

	private SongFeedContent songFeedContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_song_list);

		initFilter();
		
		songFeedContent = new SongFeedContent(this, getApplicationContext());
	}

	private void initFilter() {
		IntentFilter filter = new IntentFilter();

		filter.addAction(KawaUtil.KILLING_COMMAND);
		
		registerReceiver(receiver, filter);	
	}

	@Override
	public void gotResults() {
		// TODO Auto-generated method stub
		setListAdapter(new CustomSongListAdapter(this, R.layout.item_song_list, songFeedContent.listSongItems));
	}

	
	public class CustomSongListAdapter extends ArrayAdapter<SongItem> {
		private TextView txtVwTitle;
    	private TextView txtVwArtist;
    	private TextView txtVwPublishedDate;
    	
		public CustomSongListAdapter(Context context, int resource, List<SongItem> objects) {
			super(context, resource, objects);
		}
		
		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
    		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			convertView = inflater.inflate(R.layout.item_song_list, null);
			
			txtVwTitle = (TextView) convertView.findViewById(R.id.txtVwTitle);
			txtVwArtist = (TextView) convertView.findViewById(R.id.txtVwArtist);
			txtVwPublishedDate = (TextView) convertView.findViewById(R.id.txtVwPublishedDate);
			
			SongItem mSongItem = getItem(position);
	    	
			txtVwTitle.setText(Html.fromHtml(mSongItem.songTitle));
			txtVwArtist.setText(Html.fromHtml(mSongItem.songArtist));
			txtVwPublishedDate.setText(String.format(getResources().getString(R.string.publishedDate), mSongItem.publishedDate)); 
	    	
			return convertView;		
		}
		
	}
	
	@Override
    protected void onListItemClick(ListView aListVw, View aView, int aSongPosition, long id) {
		super.onListItemClick(aListVw, aView, aSongPosition, id);
        
        String mTrackLink = songFeedContent.listSongItems.get(aSongPosition).trackLink;
        
        Bundle arguments = new Bundle();
		arguments.putString(SongItemFragment.ARG_TRACK_LINK, mTrackLink);
		SongItemFragment fragment = new SongItemFragment();
		fragment.setArguments(arguments);
		try {
			getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("Kawazham", e.getMessage());
		}
	}
	
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		
		super.onDestroy();
	}

	@Override
	public void gotErrors(String aError) {
		if (aError.equalsIgnoreCase(SongFeedContent.NO_DATA_FOUND)) {
			showDialog(R.string.dialog_title_nodatafound, R.string.dialog_message_nodatafound);
		} else if (aError.equalsIgnoreCase(SongFeedContent.NO_DATA_CONNECTION)) {
			showDialog(R.string.dialog_title_nodataconnection, R.string.dialog_message_nodataconnection);	    
		} else if (aError.startsWith(SongFeedContent.ERROR_CAUGHT_PRE)) {
			showDialog(R.string.dialog_title_errorcaught, R.string.dialog_message_errorcaught);
		}
	}

	
	private void showDialog(int aTitleID, int aMessageID) {
		DialogFragment uiDialogMessage = UIDialogMessage.newInstance(aTitleID, aMessageID);
		uiDialogMessage.show(getFragmentManager(), "dialog");
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intentReceiver) {
			if (intentReceiver.getAction().equalsIgnoreCase(KawaUtil.KILLING_COMMAND)) {
				SongList.this.finish();
			} 
		}
	};

}
