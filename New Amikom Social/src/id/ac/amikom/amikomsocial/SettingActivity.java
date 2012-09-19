package id.ac.amikom.amikomsocial;

import java.util.Calendar;
import java.util.List;

import com.markupartist.android.widget.ActionBar;

import id.ac.amikom.amikomsocial.libs.Cald;
import id.ac.amikom.amikomsocial.libs.DbHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.widget.Toast;

public class SettingActivity extends PreferenceActivity {

	private DbHelper db = null;
	public static final String UPDATE_PREF = "id_auto_time";
	public static final String CALD_PREF = "id_calendar";

	public class BackgroundSync extends AsyncTask<String, Void, Boolean> {

		ProgressDialog dialog = new ProgressDialog(SettingActivity.this);

		@Override
		protected Boolean doInBackground(final String... params) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());
		
			Uri eventUri, alarmUri = null;
			
			
			if (Build.VERSION.SDK_INT >= 8){
				eventUri = Uri.parse("content://com.android.calendar/events");
				alarmUri = Uri.parse("content://com.android.calendar/reminders");
			}else{
				eventUri = Uri.parse("content://calendar/events");
				alarmUri = Uri.parse("content://calendar/reminders");
			}	
				
			Cursor c = getContentResolver().query(eventUri, null, null, null, null);
			
			if (c.moveToFirst()) {
				while (c.moveToNext()) {
					String location = c.getString(c
							.getColumnIndex("eventLocation"));
					String id = c.getString(c.getColumnIndex("_id"));
					String loc = "" + location;

					if (loc.equals("STMIK Amikom") || loc.contains("Amikom")) {

						Uri uri = ContentUris.withAppendedId(eventUri,
								Integer.parseInt(id));
						getContentResolver().delete(uri, null, null);
					}
				}
			}

			List<Cald> cald = db.getCalendar();
			//startManagingCursor(cr);
			
			ContentResolver cv = getContentResolver();			
			
			for (Cald cn : cald) {
					
					long dtstart = 0;
					long dtend = 0;
					Calendar cal_start = Calendar.getInstance();
					String[] istart = cn.get_start().split("\\s+");
					
					String[] start = istart[0].split("\\-+");
					int sYear = Integer.parseInt(start[0]);
					int sMonth = Integer.parseInt(start[1]) - 1;
					int sDay = Integer.parseInt(start[2]);
										
					String[] sh = istart[1].split("\\.+");
					int stHH = Integer.parseInt(sh[0]);
					int stII = Integer.parseInt(sh[1]);

					cal_start.set(sYear, sMonth, sDay, stHH, stII, 0);
					dtstart = cal_start.getTimeInMillis();										
					
					Calendar cal_end = Calendar.getInstance();
					String[] iend = cn.get_end().split("\\s+");										
										
					String[] eh = iend[1].split("\\.+");
					int enHH = Integer.parseInt(eh[0]);
					int enII = Integer.parseInt(eh[1]);										
					
					cal_end.set(sYear, sMonth, sDay, enHH, enII, 0);						
					dtend = cal_end.getTimeInMillis();					

					ContentValues event = new ContentValues();
					event.put("calendar_id", prefs.getString("id_calendar", "0"));					
					event.put("description", cn.get_detail().toUpperCase());
					event.put("eventLocation", cn.get_location());																	   
					event.put("dtstart", dtstart);										
					event.put("hasAlarm", 1);							
					
					if(cn.get_status()==1){
						String[] title = cn.get_title().split("\\-+");
						
						event.put("title", title[1].trim());
						event.put("duration", "P" + ((dtend-dtstart)/DateUtils.SECOND_IN_MILLIS) + "S");
						event.put("rrule", "FREQ=WEEKLY;COUNT=6");	
					}else{
						event.put("title", cn.get_title());
						event.put("dtend", dtend);	
					}
					
					Uri ev = cv.insert(eventUri, event);
										
				    ContentValues values = new ContentValues();
				    values.put( "event_id", Long.parseLong(ev.getLastPathSegment()));
				    values.put( "method", 1 );
				    values.put( "minutes", 30 );
				    
				    cv.insert( alarmUri, values );

				
			}																

			return true;

		}

		protected void onPreExecute() {
			this.dialog.setMessage("Please Wait, Synchronizing Calendar");
			this.dialog.show();
		}

		protected void onPostExecute(final Boolean success) {
			this.dialog.dismiss();
		}

	}

	public SettingActivity() {
		db = new DbHelper(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar_post);
		actionBar.setTitle(R.string.app_setting_title);

		ListPreference listPreferenceCategory = (ListPreference) findPreference("id_calendar");

		String calUriString;
		if (Build.VERSION.SDK_INT >= 8) {
			calUriString = "content://com.android.calendar/calendars";
		} else {
			calUriString = "content://calendar/calendars";
		}				

		
		Uri uri = Uri.parse(calUriString);
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);										

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			CharSequence[] list = new String[cursor.getCount()];
			CharSequence[] valueList = new String[cursor.getCount()];
			
			int i = 0;
			do {
				list[i] = cursor.getString(0);
				
				if (Build.VERSION.SDK_INT >= 14) {
					valueList[i] = cursor.getString(2);
				} else {
					valueList[i] = cursor.getString(cursor.getColumnIndex("displayName"));
				}	
				
				i++;
			} while (cursor.moveToNext());
			
			listPreferenceCategory.setEntries(valueList);
			listPreferenceCategory.setEntryValues(list);
			
		}
		
		

		listPreferenceCategory
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						return true;
					}
				});

		Preference sync = findPreference("id_sync");
		sync.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {

				SharedPreferences pref = getSharedPreferences(
						"cald_pref", Activity.MODE_PRIVATE);
				int refresh = Integer.parseInt(pref.getString("id_calendar", "0"));							

				if (refresh > 0) {
					if (db.isCalendar())
						new BackgroundSync().execute();
					else {
						Toast.makeText(
								getBaseContext(),
								"Error synchronizing calendar, No data calendar. Go to Calendar menu and Refresh data",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(
							getBaseContext(),
							"Error synchronizing calendar, select device calendar first",
							Toast.LENGTH_LONG).show();
				}

				return false;
			}
		});

		setUpdatePreferences();

	}

	private void setUpdatePreferences() {
		ListPreference updatePref = (ListPreference) findPreference(UPDATE_PREF);
		updatePref.setSummary(updatePref.getValue());

		updatePref
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						ListPreference listPreference = (ListPreference) preference;
						listPreference.setSummary((String) newValue);

						SharedPreferences mySharedPreferences = getSharedPreferences(
								"auto_pref", Activity.MODE_PRIVATE);
						SharedPreferences.Editor editor = mySharedPreferences
								.edit();
						editor.putString(UPDATE_PREF, (String) newValue);
						editor.commit();
						return true;
					}
				});
		
		ListPreference caldPref = (ListPreference) findPreference(CALD_PREF);
		caldPref.setSummary(caldPref.getValue());

		caldPref
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						ListPreference listPreference = (ListPreference) preference;
						listPreference.setSummary((String) newValue);

						SharedPreferences mySharedPreferences = getSharedPreferences(
								"cald_pref", Activity.MODE_PRIVATE);
						SharedPreferences.Editor editor = mySharedPreferences
								.edit();
						editor.putString(CALD_PREF, (String) newValue);
						editor.commit();
						return true;
					}
				});
	}
}
