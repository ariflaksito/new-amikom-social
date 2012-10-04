package id.ac.amikom.amikomsocial;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

import id.ac.amikom.amikomsocial.libs.DbHelper;
import id.ac.amikom.amikomsocial.libs.MCrypt;
import id.ac.amikom.amikomsocial.libs.ServiceHelper;
import id.ac.amikom.amikomsocial.libs.Shout;
import id.ac.amikom.amikomsocial.libs.CustomAdapter;
import id.ac.amikom.amikomsocial.libs.DateParse;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ShoutActivity extends ListActivity {

	private ListAdapter adapter;
	private List<Shout> shoutList;

	private class GetShout extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			ServiceHelper service = new ServiceHelper();
			service.getShout(ShoutActivity.this);

			DbHelper db = new DbHelper(ShoutActivity.this);
			if (db.isLogin())
				service.updateVersion(ShoutActivity.this);

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				viewListData();
			} catch (Exception e) {				
				e.printStackTrace();
			}

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

		try {
			viewListData();
		} catch (Exception e) {			
			e.printStackTrace();
		}

	}

	private void viewListData() throws Exception {
		DbHelper db = new DbHelper(this);
		shoutList = db.getShout();

		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		for (Shout cn : shoutList) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			DateParse dp = new DateParse(cn.get_time());

			if (cn.get_alias().equals("null"))
				map.put("name", cn.get_name());
			else
				map.put("name", cn.get_alias());

			MCrypt mc = new MCrypt();
			File f = new File("/mnt/sdcard/amikom/" + mc.bytesToHex(mc.encrypt(cn.get_nid())));

			if (f.exists())
				map.put("icon", "/mnt/sdcard/amikom/" + mc.bytesToHex(mc.encrypt(cn.get_nid())));
			else
				map.put("icon", R.drawable.none);

			
			String msg = cn.get_msg().replace("@(\\w)", " <b>nick</b> ");
			map.put("msg", msg);
			map.put("via", "from " + cn.get_via() + ", " + dp.parseString());

			list.add(map);

		}

		adapter = new CustomAdapter(this, list, R.layout.activity_shout,
				new String[] { "name", "msg", "icon", "via" }, new int[] {
						R.id.sh_name, R.id.sh_msg, R.id.sh_img, R.id.sh_via });

		setListAdapter(adapter);
		registerForContextMenu(getListView());
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		DbHelper db = new DbHelper(this);

		if (db.isLogin())
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

		Shout shout = shoutList.get(info.position - 1);

		String name;
		if (shout.get_alias().equals("null")) {
			String[] nm = shout.get_name().split("\\s+");
			name = nm[0].toLowerCase();
		} else
			name = shout.get_alias();

		String nid = shout.get_nid();
		String postAlias = "@" + name;
		String postMsg = ":O " + postAlias + " " + shout.get_msg();
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

		case 2:
			Intent in = new Intent(ShoutActivity.this, UserActivity.class);
			in.putExtra("nid", nid);
			startActivity(in);
			return (true);
		}

		return (super.onOptionsItemSelected(item));

	}

}
