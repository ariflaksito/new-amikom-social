package id.ac.amikom.amikomsocial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

import id.ac.amikom.amikomsocial.libs.DbHelper;
import id.ac.amikom.amikomsocial.libs.ServiceHelper;
import id.ac.amikom.amikomsocial.libs.Shout;
import id.ac.amikom.amikomsocial.libs.CustomAdapter;
import id.ac.amikom.amikomsocial.libs.DateParse;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ShoutActivity extends ListActivity {
			
	private ListAdapter adapter;
	private String[] nid;
	private String[] alias;
	private String[] msg;

	private class GetShout extends AsyncTask<String, Void, String> {		

		@Override
		protected String doInBackground(String... params) {
			ServiceHelper service = new ServiceHelper();
			service.getShoutService(ShoutActivity.this);
			return null;
		}

		@Override
		protected void onPreExecute() {

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
						new GetShout().execute();
					}
				});

		viewListData();

	}

	private void viewListData() {
		DbHelper db = new DbHelper(this);
		List<Shout> shoutList = db.getShout();
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		
		int i = 0;
		for (Shout cn : shoutList) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			DateParse dp = new DateParse(cn.get_time());
			
			if (cn.get_alias().equals("null")) 
				map.put("name", cn.get_name());
			else map.put("name", cn.get_alias());		
			
			nid[i] = cn.get_nid();
			alias[i] = cn.get_alias();
			msg[i] = cn.get_msg();
			
			map.put("icon", R.drawable.none);			
			map.put("msg", cn.get_msg());
			map.put("via", "from " + cn.get_via() + ", " + dp.parseString());
			
			list.add(map);
			i++;
		}

		adapter = new CustomAdapter(this, list, R.layout.activity_shout,
				new String[] { "name", "msg", "icon", "via" }, new int[] {
						R.id.sh_name, R.id.sh_msg, R.id.sh_img, R.id.sh_via });

		setListAdapter(adapter);
		registerForContextMenu(getListView());
	}
	
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);		
		l.showContextMenuForChild(v);   

	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {								
		
		menu.setHeaderTitle("Shout Option");
		menu.add(Menu.NONE, 0, Menu.NONE, "Re-Shout");
		menu.add(Menu.NONE, 1, Menu.NONE, "Reply");
		menu.add(Menu.NONE, 2, Menu.NONE, "Profile");

	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
				
				
		String postAlias = "@"+alias[info.position-1];
		String postMsg = ":O "+ postAlias + " " +msg[info.position-1];
		Intent i = new Intent(ShoutActivity.this, PostActivity.class);
		
		switch (item.getItemId()) {
		case 0:			
			i.putExtra("msg", postMsg);			
			startActivity(i);
		return (true);
		case 1:			
			i.putExtra("msg", postAlias);			
			startActivity(i);
		return (true);
		
		case  2:
			
		return (true);	
		} 
			
		
		return (super.onOptionsItemSelected(item));
		
	}	

}
