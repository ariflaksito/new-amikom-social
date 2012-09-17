package id.ac.amikom.amikomsocial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

import id.ac.amikom.amikomsocial.libs.DbHelper;
import id.ac.amikom.amikomsocial.libs.Login;
import id.ac.amikom.amikomsocial.libs.ServiceHelper;
import id.ac.amikom.amikomsocial.libs.Shout;
import id.ac.amikom.amikomsocial.libs.CustomAdapter;
import id.ac.amikom.amikomsocial.libs.DateParse;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListAdapter;

public class MeActivity extends ListActivity {

	private ListAdapter adapter;
	private String name;

	private class GetShout extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			ServiceHelper service = new ServiceHelper();
			service.getShoutMe(MeActivity.this, name);
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

		DbHelper db = new DbHelper(this);
		if (db.isLogin()) {

			((PullToRefreshListView) getListView())
					.setOnRefreshListener(new OnRefreshListener() {
						public void onRefresh() {
							new GetShout().execute();
						}
					});
						
			Login login = db.getLogin();
						
			if (login.get_alias().equals("null")) {
				String[] nm = login.get_name().split("\\s+");
				name = nm[0].toLowerCase();
			}else name = login.get_alias();
			
			viewListData();

		}

	}

	private void viewListData() {
		
		DbHelper db = new DbHelper(this);
		List<Shout> shoutList = db.getShoutMe(name);
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		for (Shout cn : shoutList) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			DateParse dp = new DateParse(cn.get_time());

			if (cn.get_alias().equals("null"))
				map.put("name", cn.get_name());
			else
				map.put("name", cn.get_alias());

			map.put("icon", R.drawable.none);
			map.put("msg", cn.get_msg());
			map.put("via", "from " + cn.get_via() + ", " + dp.parseString());

			list.add(map);
		}

		adapter = new CustomAdapter(this, list, R.layout.activity_shout,
				new String[] { "name", "msg", "icon", "via" }, new int[] {
						R.id.sh_name, R.id.sh_msg, R.id.sh_img, R.id.sh_via });

		setListAdapter(adapter);
		registerForContextMenu(getListView());
	}

}
