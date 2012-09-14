package id.ac.amikom.amikomsocial;


import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.os.Bundle;

public class SettingActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar_post);
		actionBar.setTitle(R.string.app_setting_title);

		actionBar.setHomeAction(new IntentAction(this, PostActivity
				.createIntent(this), R.drawable.ic_action_back));
		
		
	}	
	
}
