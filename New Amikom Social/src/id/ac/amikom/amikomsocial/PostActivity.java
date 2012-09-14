package id.ac.amikom.amikomsocial;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.SessionStore;
import com.facebook.android.Facebook.DialogListener;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class PostActivity extends Activity {
	private Facebook mFacebook;
	private CheckBox mFacebookBtn;
	private ProgressDialog mProgress;
	private static final String[] PERMISSIONS = new String[] {
			"publish_stream", "read_stream", "offline_access" };
	private static final String APP_ID = "267873336663584";
	private CheckBox mFacebookCb;
	private Handler mRunOnUi = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);

		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar_post);
		actionBar.setTitle(R.string.app_title);

		actionBar.setHomeAction(new IntentAction(this, MainActivity
				.createIntent(this), R.drawable.ic_action_back));
		//
		mFacebookBtn = (CheckBox) findViewById(R.id.cb_facebook);
		mProgress = new ProgressDialog(this);
		mFacebook = new Facebook(APP_ID);
		final EditText reviewEdit = (EditText) findViewById(R.id.post_txt);
		SessionStore.restore(mFacebook, this);

		if (mFacebook.isSessionValid()) {
			mFacebookBtn.setChecked(true);

			String name = SessionStore.getName(this);
			name = (name.equals("")) ? "Unknown" : name;

//			mFacebookBtn.setText("  Facebook (" + name + ")");
//			mFacebookBtn.setTextColor(Color.WHITE);
			//mFacebookBtn.setText("");
			mFacebookBtn.setTextColor(Color.WHITE);
		}

		mFacebookBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				onFacebookClick();
			}
		});

		((Button) findViewById(R.id.button_post))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						String review = reviewEdit.getText().toString();

						if (review.equals(""))
							return;

						if (mFacebookBtn.isChecked())
							postToFacebook(review);
					}
				});
	}

	private void postToFacebook(String review) {
		mProgress.setMessage("Posting ...");
		mProgress.show();

		AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(mFacebook);

		Bundle params = new Bundle();

		params.putString("message", review);
		// params.putString("name", "Dexter");
		// params.putString("caption", "londatiga.net");
		// params.putString("link", "http://www.londatiga.net");
		// params.putString("description",
		// "Dexter, seven years old dachshund who loves to catch cats, eat carrot and krupuk");
		// params.putString("picture", "http://twitpic.com/show/thumb/6hqd44");

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

	private void onFacebookClick() {
		if (mFacebook.isSessionValid()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setMessage("Delete current Facebook connection?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									fbLogout();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();

									mFacebookBtn.setChecked(true);
								}
							});

			final AlertDialog alert = builder.create();

			alert.show();
		} else {
			mFacebookBtn.setChecked(false);

			mFacebook.authorize(this, PERMISSIONS, -1,
					new FbLoginDialogListener());
		}
	}

	private final class FbLoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			SessionStore.save(mFacebook, PostActivity.this);

//			mFacebookBtn.setText("  Facebook (No Name)");
//			mFacebookBtn.setChecked(true);
//			mFacebookBtn.setTextColor(Color.WHITE);
			//mFacebookBtn.setText("");
			mFacebookBtn.setChecked(true);
			mFacebookBtn.setTextColor(Color.WHITE);

			getFbName();
		}

		public void onFacebookError(FacebookError error) {
			Toast.makeText(PostActivity.this, "Facebook connection failed",
					Toast.LENGTH_SHORT).show();

			mFacebookBtn.setChecked(false);
		}

		public void onError(DialogError error) {
			Toast.makeText(PostActivity.this, "Facebook connection failed",
					Toast.LENGTH_SHORT).show();

			mFacebookBtn.setChecked(false);
		}

		public void onCancel() {
			mFacebookBtn.setChecked(false);
		}
	}

	private void getFbName() {
		mProgress.setMessage("Finalizing ...");
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				String name = "";
				int what = 1;

				try {
					String me = mFacebook.request("me");

					JSONObject jsonObj = (JSONObject) new JSONTokener(me)
							.nextValue();
					name = jsonObj.getString("name");
					what = 0;
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				mFbHandler.sendMessage(mFbHandler.obtainMessage(what, name));
			}
		}.start();
	}

	private void fbLogout() {
		mProgress.setMessage("Disconnecting from Facebook");
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				SessionStore.clear(PostActivity.this);

				int what = 1;

				try {
					mFacebook.logout(PostActivity.this);

					what = 0;
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}

	private Handler mFbHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgress.dismiss();

			if (msg.what == 0) {
				String username = (String) msg.obj;
				username = (username.equals("")) ? "No Name" : username;

				SessionStore.saveName(username, PostActivity.this);

				//mFacebookBtn.setText("  Facebook (" + username + ")");
				//mFacebookBtn.setText("");
				
				Toast.makeText(PostActivity.this,
						"Connected to Facebook as " + username,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(PostActivity.this, "Connected to Facebook",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgress.dismiss();

			if (msg.what == 1) {
				Toast.makeText(PostActivity.this, "Facebook logout failed",
						Toast.LENGTH_SHORT).show();
			} else {
//				mFacebookBtn.setChecked(false);
//				mFacebookBtn.setText("  Facebook (Not connected)");
//				mFacebookBtn.setTextColor(Color.GRAY);
				mFacebookBtn.setChecked(false);
				//mFacebookBtn.setText("");
				mFacebookBtn.setTextColor(Color.GRAY);
				

				Toast.makeText(PostActivity.this, "Disconnected from Facebook",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
}
