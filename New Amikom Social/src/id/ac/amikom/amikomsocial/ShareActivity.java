package id.ac.amikom.amikomsocial;

import org.json.JSONObject;
import org.json.JSONTokener;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.Toast;

public class ShareActivity extends Activity {

	private Facebook mFacebook;
	private CheckBox mFacebookBtn;
	private ProgressDialog mProgress;
	private CheckBox mTwitterBtn;
	private static Twitter twitter;
	private static RequestToken requestToken;
	private static SharedPreferences mSharedPreferences;

	static String CONSUMER_KEY = "cpZmKRjZ31DPWOJ81Gjazw";
	static String CONSUMER_SECRET = "XMYSsvwiv7w0IVbtk9dhTQaApnqrzGSyN19749J4OE";

	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TOKEN = "oauth_token";
	static final String PREF_USERNAME = "username";

	static final String CALLBACK_URL = "oauth://t4jsample";

	static final String IEXTRA_AUTH_URL = "auth_url";
	static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
	static final String IEXTRA_OAUTH_TOKEN = "oauth_token";

	private static final String[] PERMISSIONS = new String[] {
			"publish_stream", "read_stream", "offline_access" };

	private static final String APP_ID = "327355724027124";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);

		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar_post);
		actionBar.setTitle(R.string.app_setting_title);

		actionBar.setHomeAction(new IntentAction(this, PostActivity
				.createIntent(this), R.drawable.ic_action_back));

		try {
			Bundle extras = getIntent().getExtras();
			String msg = extras.getString("msg");

			if (!msg.equals("null")) {
				actionBar.setHomeAction(new IntentAction(this, SettingActivity
						.createIntent(this), R.drawable.ic_action_back));
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		mFacebookBtn = (CheckBox) findViewById(R.id.cb_facebook);
		mTwitterBtn = (CheckBox) findViewById(R.id.cb_twitter);

		mProgress = new ProgressDialog(this);
		mFacebook = new Facebook(APP_ID);

		SessionStore.restore(mFacebook, this);
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				PREFERENCE_NAME, MODE_PRIVATE);

		if (mFacebook.isSessionValid()) {
			mFacebookBtn.setChecked(true);

			String name = SessionStore.getName(this);
			name = (name.equals("")) ? "Unknown" : name;

			mFacebookBtn.setText("Facebook (" + name + ")");
			mFacebookBtn.setTextColor(Color.BLACK);
		}

		mFacebookBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				onFacebookClick();
			}
		});

		final Thread th = new Thread(new Runnable() {
			public void run() {
				askOAuth();
			}
		});

		mTwitterBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				askOAuth();
			}
		});

		final float scale = this.getResources().getDisplayMetrics().density;
		mFacebookBtn
				.setPadding(mFacebookBtn.getPaddingLeft()
						+ (int) (15.0f * scale + 1.5f),
						mFacebookBtn.getPaddingTop(),
						mFacebookBtn.getPaddingRight(),
						mFacebookBtn.getPaddingBottom());

		mTwitterBtn.setPadding(mTwitterBtn.getPaddingLeft()
				+ (int) (15.0f * scale + 1.5f), mTwitterBtn.getPaddingTop(),
				mTwitterBtn.getPaddingRight(), mTwitterBtn.getPaddingBottom());

		if (isConnected()) {
			
			mTwitterBtn.setChecked(true);
			mTwitterBtn.setText("Twitter (" + mSharedPreferences.getString(PREF_USERNAME, "") + ")");
			mTwitterBtn.setTextColor(Color.BLACK);
						
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
			SessionStore.save(mFacebook, ShareActivity.this);

			mFacebookBtn.setText("Facebook (No Name)");
			mFacebookBtn.setChecked(true);
			mFacebookBtn.setTextColor(Color.BLACK);

			getFbName();
		}

		public void onFacebookError(FacebookError error) {
			Toast.makeText(ShareActivity.this, "Facebook connection failed",
					Toast.LENGTH_SHORT).show();

			mFacebookBtn.setChecked(false);
		}

		public void onError(DialogError error) {
			Toast.makeText(ShareActivity.this, "Facebook connection failed",
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
				SessionStore.clear(ShareActivity.this);

				int what = 1;

				try {
					mFacebook.logout(ShareActivity.this);

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

				SessionStore.saveName(username, ShareActivity.this);

				mFacebookBtn.setText("Facebook (" + username + ")");

				Toast.makeText(ShareActivity.this,
						"Connected to Facebook as " + username,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ShareActivity.this, "Connected to Facebook",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgress.dismiss();

			if (msg.what == 1) {
				Toast.makeText(ShareActivity.this, "Facebook logout failed",
						Toast.LENGTH_SHORT).show();
			} else {
				mFacebookBtn.setChecked(false);
				mFacebookBtn.setText("Facebook (Not connected)");
				mFacebookBtn.setTextColor(Color.GRAY);

				Toast.makeText(ShareActivity.this,
						"Disconnected from Facebook", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	private void askOAuth() {
		if (!isConnected()) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(CONSUMER_KEY);
			builder.setOAuthConsumerSecret(CONSUMER_SECRET);
			Configuration configuration = builder.build();

			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			try {
				requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
				this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse(requestToken.getAuthenticationURL())));
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {
			// user already logged into twitter
			Toast.makeText(getApplicationContext(),
					"Already Logged into twitter", Toast.LENGTH_LONG).show();
		}

	}

	private boolean isConnected() {
		return mSharedPreferences.getString(PREF_KEY_TOKEN, null) != null;
	}

	protected void onResume() {
		super.onResume();

		if (!isConnected()) {
			Uri uri = getIntent().getData();
			if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
				String verifier = uri.getQueryParameter(IEXTRA_OAUTH_VERIFIER);
				try {
					AccessToken accessToken = twitter.getOAuthAccessToken(
							requestToken, verifier);
					
					long userID = accessToken.getUserId();
					User user = twitter.showUser(userID);
					String username = user.getName();
					
					Editor e = mSharedPreferences.edit();
					e.putString(PREF_KEY_TOKEN, accessToken.getToken());
					e.putString(PREF_KEY_SECRET, accessToken.getTokenSecret());
					e.putString(PREF_USERNAME,username);
					e.commit();
					
					mTwitterBtn.setChecked(true);
					mTwitterBtn.setText("Twitter (" + username + ")");
					mTwitterBtn.setTextColor(Color.BLACK);

				} catch (Exception e) {
					Log.e("Error AmikomSocial ==>>", e.getMessage());
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
							.show();
				}
			} else {
				Toast.makeText(this, "Not Connected", Toast.LENGTH_LONG).show();
			}
		}

	}

}
