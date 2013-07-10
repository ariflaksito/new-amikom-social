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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
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

	static String TWITTER_CONSUMER_KEY = "cpZmKRjZ31DPWOJ81Gjazw";
	static String TWITTER_CONSUMER_SECRET = "XMYSsvwiv7w0IVbtk9dhTQaApnqrzGSyN19749J4OE";

	// Preference Constants
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
	static final String PREF_KEY_USER = "username";

	static final String TWITTER_CALLBACK_URL = "oauth://amsos";

	// Twitter oauth urls
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

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

		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"MyPref", 0);

		mTwitterBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				onTwitterClick();
			}
		});

		if (Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		if (!isTwitterLoggedInAlready()) {
			Uri uri = getIntent().getData();
			if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
				String verifier = uri
						.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
				try {
					AccessToken accessToken = twitter.getOAuthAccessToken(
							requestToken, verifier);

					long userID = accessToken.getUserId();
					User user = twitter.showUser(userID);
					String username = user.getName();

					Editor e = mSharedPreferences.edit();
					e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
					e.putString(PREF_KEY_OAUTH_SECRET,
							accessToken.getTokenSecret());
					e.putString(PREF_KEY_USER, username);
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
					e.commit();

					Log.e("Twitter OAuth Token", "> " + accessToken.getToken());

					mTwitterBtn.setChecked(true);
					mTwitterBtn.setText("Twitter (" + username + ")");
					mTwitterBtn.setTextColor(Color.BLACK);

				} catch (Exception e) {
					e.printStackTrace();
					Log.e("Twitter Login Error", "> " + e.getMessage());
				}
			}
		} else {

			mTwitterBtn.setChecked(true);
			mTwitterBtn.setText("Twitter ("
					+ mSharedPreferences.getString(PREF_KEY_USER, "Unnamed")
					+ ")");
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

	private void onTwitterClick() {
		if (isTwitterLoggedInAlready()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setMessage("Delete current Twitter connection?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									twitterLogout();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();

									mTwitterBtn.setChecked(true);
								}
							});

			final AlertDialog alert = builder.create();

			alert.show();
		} else {
			mTwitterBtn.setChecked(false);
			loginToTwitter();
			
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

	protected void onResume() {
		super.onResume();

	}

	private void loginToTwitter() {
		if (!isTwitterLoggedInAlready()) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
			Configuration configuration = builder.build();

			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
				try {
					requestToken = twitter
							.getOAuthRequestToken(TWITTER_CALLBACK_URL);
					this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse(requestToken.getAuthenticationURL())));
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			} else {
				
				mProgress.setMessage("Loading...");
				mProgress.show();
				
				new Thread(new Runnable() {
					public void run() {
						try {
							requestToken = twitter
									.getOAuthRequestToken(TWITTER_CALLBACK_URL);
							ShareActivity.this.startActivity(new Intent(
									Intent.ACTION_VIEW, Uri.parse(requestToken
											.getAuthenticationURL())));
						} catch (TwitterException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		} else {			
			Toast.makeText(getApplicationContext(),
					"Already Logged into twitter", Toast.LENGTH_LONG).show();
		}
	}

	public boolean isTwitterLoggedInAlready() {		
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}
	
	private void twitterLogout() {
		mProgress.setMessage("Disconnecting from Twitter");
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				Editor e = mSharedPreferences.edit();
				e.remove(PREF_KEY_OAUTH_TOKEN);
				e.remove(PREF_KEY_OAUTH_SECRET);
				e.remove(PREF_KEY_TWITTER_LOGIN);
				e.remove(PREF_KEY_USER);
				e.commit();
			}
		}.start();
		
		mProgress.dismiss();
	}

}
