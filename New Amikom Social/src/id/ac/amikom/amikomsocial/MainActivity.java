package id.ac.amikom.amikomsocial;

import id.ac.amikom.amikomsocial.libs.DbHelper;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	private TabHost mTabHost;
	DbHelper db = null;

	private void setupTabHost() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabs);				

		db = new DbHelper(this);
		
		final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(R.string.app_title);

		actionBar.setHomeAction(new IntentAction(this, MainActivity
				.createIntent(this), R.drawable.logo_actionbar));
		actionBar.addAction(new IntentAction(this, new Intent(this,
				PostActivity.class), R.drawable.ic_action_edit));

		if (Build.VERSION.SDK_INT >= 11)
			actionBar.addAction(new MenuAction());

		setupTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		setupTab(new TextView(this), "SHOUT", "ShoutActivity.class");
		setupTab(new TextView(this), "@ME", "MeActivity.class");
		setupTab(new TextView(this), "CALENDAR", "CalendarActivity.class");

	}

	private void setupTab(final View view, final String tag,
			final String className) {
		View tabview = createTabView(mTabHost.getContext(), tag);

		Intent intent;
		intent = new Intent().setClass(this, ShoutActivity.class);
		if (className.equals("ShoutActivity.class")) {
			intent = new Intent().setClass(this, ShoutActivity.class);
		}
		if (className.equals("MeActivity.class")) {
			intent = new Intent().setClass(this, MeActivity.class);
		}
		if (className.equals("CalendarActivity.class")) {
			intent = new Intent().setClass(this, CalendarActivity.class);
		}

		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview)
				.setContent(intent);
		{
		}
		;

		mTabHost.addTab(setContent);
	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context)
				.inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		
		if(db.isLogin())
			inflater.inflate(R.menu.menu_af_login, menu);
		else inflater.inflate(R.menu.menu_bf_login, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (Build.VERSION.SDK_INT >= 11) {
			menu.setGroupVisible(0, false);
		}

		return true;
	}

	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	private class MenuAction extends AbstractAction {

		public MenuAction() {
			super(R.drawable.ic_action_overflow);
		}

		public void performAction(View view) {

			PopupMenu popup = new PopupMenu(getApplicationContext(), view);
			MenuInflater inflater = popup.getMenuInflater();
			
			if(db.isLogin())
				inflater.inflate(R.menu.menu_af_login, popup.getMenu());
			else inflater.inflate(R.menu.menu_bf_login, popup.getMenu());
			popup.show();
		}

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.id_profile:
			startActivity(new Intent(MainActivity.this, LoginActivity.class));
			return true;
		case R.id.id_sync:
			
			return true;
		case R.id.id_logout:			

			return true;	
		case R.id.id_about:			
			Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.activity_info);
			dialog.setTitle("Amikom Social");
			dialog.setCancelable(true);
			dialog.show();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

}
