package id.ac.amikom.amikomsocial;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.SessionStore;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.R.bool;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PostActivity extends Activity {

	private TextView countInfo;
	private EditText reviewEdit;

	private Facebook mFacebook;
	private CheckBox mFacebookCb;
	private ProgressDialog mProgress;

	private Handler mRunOnUi = new Handler();

	private static final String APP_ID = "267873336663584";

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

		mFacebookCb = (CheckBox) findViewById(R.id.cb_facebook);
		//mFacebookCb.setChecked(false);
		mProgress = new ProgressDialog(this);

		mFacebook = new Facebook(APP_ID);

		SessionStore.restore(mFacebook, this);

		if (mFacebook.isSessionValid()) {
			mFacebookCb.setChecked(true);

			String name = SessionStore.getName(this);
			name = (name.equals("")) ? "Unknown" : name;

			//mFacebookCb.setText("  Facebook  (" + name + ")");
		}

		((Button) findViewById(R.id.button_post))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						String review = reviewEdit.getText().toString();

						if (review.equals(""))
							return;

						if (mFacebookCb.isChecked())
							postToFacebook(review);
					}
				});

		// ((CheckBox) findViewById(R.id.cb_facebook))
		// .setOnClickListener(new OnClickListener() {
		// public void onClick(View v) {
		// if (mFacebook.isSessionValid()) {
		//
		// if (mFacebookCb.isChecked()) {
		// mFacebookCb.setChecked(false);
		// }
		// if (!(mFacebookCb.isChecked())) {
		// mFacebookCb.setChecked(true);
		// }
		// } else
		// mFacebookCb.setChecked(false);
		//
		// }
		// });
	}

	private void postToFacebook(String review) {
		mProgress.setMessage("Posting ...");
		mProgress.show();

		AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(mFacebook);

		Bundle params = new Bundle();

		params.putString("message", review);
//		params.putString("name", "Dexter");
//		params.putString("caption", "londatiga.net");
//		params.putString("link", "http://www.londatiga.net");
//		params.putString(
//				"description",
//				"Dexter, seven years old dachshund who loves to catch cats, eat carrot and krupuk");
//		params.putString("picture", "http://twitpic.com/show/thumb/6hqd44");

		mAsyncFbRunner.request("me/feed", params, "POST",
				new WallPostListener());
	}

	private final class WallPostListener extends BaseRequestListener {
		public void onComplete(final String response) {
			mRunOnUi.post(new Runnable() {
				public void run() {
					mProgress.cancel();
					Toast.makeText(PostActivity.this, "Posted to Facebook",
							Toast.LENGTH_SHORT).show();

				}
			});
		}
	}

	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, PostActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

}
