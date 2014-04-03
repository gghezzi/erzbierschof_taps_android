package com.erzbierschoftaps;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TapsActivity extends Activity {

	private static String TAP = "tap";
	private String barId;
	private String barUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taps);
		setTitle(getTitle() + " "
				+ getIntent().getSerializableExtra(MainActivity.BAR_NAME));
		this.barId = (String) getIntent().getSerializableExtra(
				MainActivity.BAR_NAME);
		this.barUrl = (String) getIntent().getSerializableExtra(
				MainActivity.BAR_URL);
		TapsLoader t = new TapsLoader(this);
		t.execute();
	}

	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_taps, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			TapsLoader t = new TapsLoader(this);
			t.execute();
			return true;
		default:
			return false;
		}
	}
	
	private class BeerInfo {
		private String name;
		private String brewery;
		private String style;
		private String amount;
		private String country;
		private String alcohol;

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getAlcohol() {
			return alcohol;
		}

		public void setAlcohol(String alcohol) {
			this.alcohol = alcohol;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getBrewery() {
			return brewery;
		}

		public void setBrewery(String brewery) {
			this.brewery = brewery;
		}

		public String getStyle() {
			return style;
		}

		public void setStyle(String style) {
			this.style = style;
		}

		public String getAmount() {
			return amount;
		}

		public void setAmount(String amount) {
			this.amount = amount;
		}

	}

	private class BeerAdapter extends ArrayAdapter<BeerInfo> {

		private ArrayList<BeerInfo> items;

		/**
		 * Constructor.
		 * 
		 * @param context
		 * @param textViewResourceId
		 * @param items
		 */
		public BeerAdapter(Context context, int textViewResourceId,
				ArrayList<BeerInfo> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		/**
		 * The method that actually loads the exercises found in the listView.
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);
			}
			final BeerInfo beer = items.get(position);
			if (beer != null) {
				((TextView) v.findViewById(R.id.top_left_text)).setText(beer
						.getName()
						+ " "
						+ getApplicationContext().getString(R.string.by)
						+ " "
						+ beer.getBrewery() + " (" + beer.getCountry() + ")");
				((TextView) v.findViewById(R.id.row_number)).setText(position + 1 + "");
				((TextView) v.findViewById(R.id.bottom_left_text)).setText(beer
						.getStyle() + ", " + beer.getAlcohol());
				((TextView) v.findViewById(R.id.right_text)).setText(beer
						.getAmount());
				v.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent("android.intent.action.MAIN");
					    intent.addCategory("android.intent.category.LAUNCHER");
					    PackageManager m = getApplicationContext().getPackageManager();
					    for (ResolveInfo i : m.queryIntentActivities(intent, 0)) {
					    	if (i.activityInfo.name.equals("com.ratebeer.android.gui.Home_")) {
					    		Intent rateBeerIntent = new Intent("android.intent.action.MAIN");
					    		rateBeerIntent.addCategory("android.intent.category.LAUNCHER");
					    		rateBeerIntent.setComponent(new ComponentName(i.activityInfo.packageName, i.activityInfo.name));
					    		rateBeerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					    		rateBeerIntent.putExtra(SearchManager.QUERY, beer.getBrewery() + " " + beer.getName());
					    		startActivity(rateBeerIntent);
						    	break;
					    	}
					    }
					}
				});
				
			}
			return v;
		}
	}

	private class TapsLoader extends
			AsyncTask<Void, Integer, ArrayList<BeerInfo>> {

		private String failMsg = null;
		private Activity activity;
		private ProgressDialog progress;
		private boolean noUpdate;

		public TapsLoader(Activity activity) {
			this.activity = activity;
		}

		protected void onPreExecute() {
			this.progress = new ProgressDialog(this.activity);
			this.progress.setMessage(getApplicationContext().getString(
					R.string.loading_taps)
					+ " "
					+ getApplicationContext().getString(R.string.in)
					+ " " + barId);
			this.progress.setIndeterminate(true);
			this.progress.setCancelable(false);
			this.progress.show();
		}

		protected void onPostExecute(final ArrayList<BeerInfo> res) {
			BeerAdapter tapsAdapter = new BeerAdapter(this.activity,
					R.layout.row, res);
			((ListView) findViewById(R.id.tapsList)).setAdapter(tapsAdapter);
			tapsAdapter.notifyDataSetChanged();
			
			if (this.failMsg != null) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						this.activity);
				alertDialog.setMessage(failMsg);
				alertDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								if (res == null || res.isEmpty()) {
									finish();
								}
							}
						});
				alertDialog.show();
			}
			
			if (this.noUpdate) {
				Toast toast = Toast.makeText(getApplicationContext(),
						getApplicationContext().getString(R.string.no_updates),
						Toast.LENGTH_SHORT);
				toast.show();
			}
			this.progress.dismiss();
		}

		@Override
		protected ArrayList<BeerInfo> doInBackground(Void... params) {
			SharedPreferences prefs = getSharedPreferences("shared_settings", 0);
			ArrayList<BeerInfo> taps = new ArrayList<BeerInfo>();
			// Get what's on tap from the webpage
			try {
				Connection conn = Jsoup.connect(barUrl).timeout(10000);
				Document doc = conn.get();
				String newDate = doc.select(":containsOwn(Update)").get(0).text();
				newDate = newDate.substring(newDate.indexOf("Update: ") + 8).trim();
				//replace("Update: ", "").trim();
				String oldDate = prefs.getString(barId + "_" + "last_update", null);
				if (oldDate == null || oldDate != newDate) {
					this.noUpdate = false;
					Elements rows = doc.select("table").select("tr");
					SharedPreferences.Editor prefsEditor = prefs.edit();
					prefsEditor.putString(barId + "_" + "last_update", newDate);
					// Iterate over the beers table and extract the data
					for (int i = 1; i < rows.size(); i++) {
						BeerInfo info = new BeerInfo();
						info.setCountry(rows.get(i).select("td").get(2).text());
						info.setAlcohol(rows.get(i).select("td").get(5).text());
						info.setAmount(rows.get(i).select("td").get(6).text());
						info.setName(rows.get(i).select("td").get(3).text());
						info.setBrewery(rows.get(i).select("td").get(1).text());
						info.setStyle(rows.get(i).select("td").get(4).text());
						taps.add(info);
						
						prefsEditor.putString(barId + "_" + TAP + "_" + i
								+ "_amount", info.getAmount());
						prefsEditor.putString(barId + "_" + TAP + "_" + i
								+ "_abv", info.getAlcohol());
						prefsEditor.putString(barId + "_" + TAP + "_" + i
								+ "_style", info.getStyle());
						prefsEditor.putString(barId + "_" + TAP + "_" + i
								+ "_name", info.getName());
						prefsEditor.putString(barId + "_" + TAP + "_" + i
								+ "_country", info.getCountry());
						prefsEditor.putString(barId + "_" + TAP + "_" + i
								+ "_brewery", info.getBrewery());
					}
					prefsEditor.commit();
				} else {
					this.noUpdate = true;
				}
			} catch (IOException e) {
				this.failMsg = getApplicationContext().getString(
						R.string.ontap_read_error);
				for (int i = 1; i <= 15; i++) {
					BeerInfo info = new BeerInfo();
					info.setCountry(prefs.getString(barId + "_" + TAP + "_" + i
							+ "_country", null));
					info.setAlcohol(prefs.getString(barId + "_" + TAP + "_" + i
							+ "_abv", null));
					info.setAmount(prefs.getString(barId + "_" + TAP + "_" + i
							+ "_amount", null));
					info.setName(prefs.getString(barId + "_" + TAP + "_" + i
							+ "_name", null));
					info.setBrewery(prefs.getString(barId + "_" + TAP + "_" + i
							+ "_brewery", null));
					info.setStyle(prefs.getString(barId + "_" + TAP + "_" + i
							+ "_style", null));
					taps.add(info);
				}
			}
			return taps;
		}

	}

}
