package id.ac.amikom.amikomsocial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.ac.amikom.amikomsocial.libs.Calendar;
import id.ac.amikom.amikomsocial.libs.CustomAdapter;
import id.ac.amikom.amikomsocial.libs.DbHelper;
import id.ac.amikom.amikomsocial.libs.ServiceHelper;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListAdapter;

public class CalendarActivity extends ListActivity {
	
	private ListAdapter adapter;		
	
	private class GetCalendar extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			ServiceHelper service = new ServiceHelper();
			if(service.checkCalendar(CalendarActivity.this))
				service.getCalendar(CalendarActivity.this);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			viewListData();

			((PullToRefreshListView) getListView()).onRefreshComplete();
			super.onPostExecute(result);
		}

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shout);
		
		((PullToRefreshListView) getListView())
		.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				new GetCalendar().execute();
			}
		});
		
		viewListData();

	}
	
	private void viewListData() {
		List<Calendar> calList;
		
		DbHelper db = new DbHelper(this);
		calList = db.getCalendar();		
		
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		
		for (Calendar cn : calList) {
			HashMap<String, Object> map = new HashMap<String, Object>();			

			String[] ruang = cn.get_location().split("\\-+");
			String[] st = cn.get_start().split("\\s+");
			String[] en = cn.get_end().split("\\s+");
			
			map.put("icon", R.drawable.calendar);
			map.put("name", cn.get_title().toUpperCase());
			map.put("msg", " (" + ruang[1].trim() + ")"  + " - "+ cn.get_detail());
			map.put("via", "jam "+ st[1] + " - " + en[1]);
			
			list.add(map);
			
		}

		adapter = new CustomAdapter(this, list, R.layout.activity_shout,
				new String[] { "name", "msg", "icon", "via" }, new int[] {
						R.id.sh_name, R.id.sh_msg, R.id.sh_img, R.id.sh_via });

		setListAdapter(adapter);
		registerForContextMenu(getListView());
	}
	
}
