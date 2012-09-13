package id.ac.amikom.amikomsocial;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.os.Bundle;

public class PostActivity extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);

		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar_post);
		actionBar.setTitle(R.string.app_title);
		
		actionBar.setHomeAction(new IntentAction(this, MainActivity
				.createIntent(this), R.drawable.ic_action_back));				
		
		
	}	
	
}
