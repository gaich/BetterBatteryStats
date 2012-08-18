/*
 * Copyright (C) 2011 asksven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *et
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.asksven.betterbatterystats;

/**
 * Shows alarms in a list
 * @author sven
 */

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.asksven.android.common.privateapiproxies.BatteryStatsProxy;
import com.asksven.android.common.privateapiproxies.BatteryStatsTypes;
import com.asksven.betterbatterystats.R;
import com.asksven.betterbatterystats.data.StatsProvider;

public class NetworkStatsActivity extends ListActivity
{
	/**
	 * The logging TAG
	 */
	private static final String TAG = "NetworkStatsActivity";
	
	/**
	 * a progess dialog to be used for long running tasks
	 */
	ProgressDialog m_progressDialog;
	
	/**
	 * The ArrayAdpater for rendering the ListView
	 */
	private StatsAdapter m_listViewAdapter;
	
	/**
	 * @see android.app.Activity#onCreate(Bundle@SuppressWarnings("rawtypes")
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarms);
	}
	
	/* Request updates at startup */
	@Override
	protected void onResume()
	{
		super.onResume();
		new LoadStatData().execute(this);
	}

    /** 
     * Add menu items
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    public boolean onCreateOptionsMenu(Menu menu)
    {  
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.kernelwakelocks_menu, menu);
        return true;
    }  

    /** 
     * Define menu action
     * 
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {  
        switch (item.getItemId())
        {  
	        case R.id.refresh:
            	// Refresh
	        	doRefresh();
            	break;

        }  
        return false;  
    }    

	
	private void doRefresh()
	{
		BatteryStatsProxy.getInstance(this).invalidate();
    	//new LoadStatData().execute(this);
		// Display the reference of the stat
		
//    	this.setListViewAdapter();
    	
    	m_listViewAdapter.notifyDataSetChanged();

	}

	// @see http://code.google.com/p/makemachine/source/browse/trunk/android/examples/async_task/src/makemachine/android/examples/async/AsyncTaskExample.java
	// for more details
	private class LoadStatData extends AsyncTask<Context, Integer, StatsAdapter>
	{
		@Override
	    protected StatsAdapter doInBackground(Context... params)
	    {
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(NetworkStatsActivity.this);
			boolean bFilter = sharedPrefs.getBoolean("filter_data", true);
			

			try
			{
				m_listViewAdapter = new StatsAdapter(NetworkStatsActivity.this, 
						StatsProvider.getInstance(NetworkStatsActivity.this).getNativeNetworkUsageStatList(bFilter, BatteryStatsTypes.STATS_CURRENT));
			}
			catch (Exception e)
			{
				m_listViewAdapter = null;
				Log.e(TAG, "An error occured while loading the network stats");
			}
	    	//StatsActivity.this.setListAdapter(m_listViewAdapter);
	        // getStatList();
	        return m_listViewAdapter;
	    }
		
		@Override
		protected void onPostExecute(StatsAdapter o)
	    {
			super.onPostExecute(o);
	        // update hourglass
	    	if (m_progressDialog != null)
	    	{
	    		m_progressDialog.hide();
	    		m_progressDialog = null;
	    	}
	    	NetworkStatsActivity.this.setListAdapter(o);
	    }
	    @Override
	    protected void onPreExecute()
	    {
	        // update hourglass
	    	// @todo this code is only there because onItemSelected is called twice
	    	if (m_progressDialog == null)
	    	{
		    	m_progressDialog = new ProgressDialog(NetworkStatsActivity.this);
		    	m_progressDialog.setMessage("Computing...");
		    	m_progressDialog.setIndeterminate(true);
		    	m_progressDialog.setCancelable(false);
		    	m_progressDialog.show();
	    	}
	    }
	}
	
}
