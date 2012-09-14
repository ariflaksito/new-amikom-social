package id.ac.amikom.amikomsocial;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class PostActivity extends Activity {
	
	private TextView countInfo;
	private EditText reviewEdit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);

		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar_post);
		actionBar.setTitle(R.string.app_post_title);

		actionBar.setHomeAction(new IntentAction(this, MainActivity
				.createIntent(this), R.drawable.ic_action_back));
		
		actionBar.addAction(new IntentAction(this, new Intent(this,
				SettingActivity.class), R.drawable.ic_action_share));
		
		reviewEdit = (EditText) findViewById(R.id.post_txt);
		countInfo = (TextView) findViewById(R.id.count_id);
		
		final TextWatcher mTextEditorWatcher = new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				countInfo.setText("Character Remain " + (160 - (s.length())));
			}

			public void afterTextChanged(Editable s) {
			}
		};

		reviewEdit.addTextChangedListener(mTextEditorWatcher);

		
	}
	
	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, PostActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	
}
