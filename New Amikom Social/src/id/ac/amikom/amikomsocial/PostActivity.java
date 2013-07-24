package id.ac.amikom.amikomsocial;

import id.ac.amikom.amikomsocial.libs.DbHelper;
import id.ac.amikom.amikomsocial.libs.Login;
import id.ac.amikom.amikomsocial.libs.ServiceHelper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.SessionStore;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PostActivity extends Activity implements LocationListener {

	protected LocationManager location;
	private String address = "";

	private TextView countInfo;
	private EditText reviewEdit;

	private Facebook mFacebook;
	private CheckBox mFacebookCb;
	private CheckBox mTwitterCb;
	private ProgressDialog mProgress;
	
	static String TWITTER_CONSUMER_KEY = "cpZmKRjZ31DPWOJ81Gjazw";
	static String TWITTER_CONSUMER_SECRET = "XMYSsvwiv7w0IVbtk9dhTQaApnqrzGSyN19749J4OE";

	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
	static final String PREF_KEY_USER = "username";
	private static SharedPreferences mSharedPreferences;

	private Handler mRunOnUi = new Handler();

	private static final String APP_ID = "327355724027124";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);

		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar_post);
		actionBar.setTitle(R.string.app_post_title);

		actionBar.setHomeAction(new IntentAction(this, MainActivity
				.createIntent(this), R.drawable.ic_action_back));

		actionBar.addAction(new IntentAction(this, new Intent(this,
				ShareActivity.class), R.drawable.ic_action_share));

		reviewEdit = (EditText) findViewById(R.id.post_txt);
		countInfo = (TextView) findViewById(R.id.count_id);

		try {
			Bundle extras = getIntent().getExtras();
			String msg = extras.getString("msg");

			if (!msg.equals("null")) {
				
				String fmsg = msg.replaceAll("(\\*facebook+)", "");
				String imsg = fmsg.replaceAll("(\\*twitter+)", "");
								
				reviewEdit.setText(imsg);
				countInfo.setText("Character Remain " + (140 - imsg.length()));
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		final TextWatcher mTextEditorWatcher = new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				countInfo.setText("Character Remain " + (140 - (s.length())));
			}

			public void afterTextChanged(Editable s) {
			}
		};

		reviewEdit.addTextChangedListener(mTextEditorWatcher);
		mFacebookCb = (CheckBox) findViewById(R.id.cb_facebook);
		mTwitterCb = (CheckBox) findViewById(R.id.cb_twitter);
		mProgress = new ProgressDialog(this);
		mFacebook = new Facebook(APP_ID);
		SessionStore.restore(mFacebook, this);

		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"MyPref", 0);

		if (mFacebook.isSessionValid()) {
			mFacebookCb.setChecked(true);

			String name = SessionStore.getName(this);
			name = (name.equals("")) ? "Unknown" : name;

		}

		if (isTwitterLoggedInAlready())
			mTwitterCb.setChecked(true);

		((Button) findViewById(R.id.button_post))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {

						String review = reviewEdit.getText().toString();
						if (review.equals(""))
							return;

						new PostingTask().execute(review);

					}
				});

		((CheckBox) findViewById(R.id.cb_facebook))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						SessionStore.restore(mFacebook, PostActivity.this);
						if (mFacebook.isSessionValid() == false) {
							mFacebookCb.setChecked(false);
						}
					}
				});

		((CheckBox) findViewById(R.id.cb_twitter))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {						
						if (!isTwitterLoggedInAlready()) {
							mTwitterCb.setChecked(false);
						}
					}
				});

		location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location loc = location
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (loc != null) {
		} else {
			loc = location.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}

		this.onLocationChanged(loc);

	}

	private void postToFacebook(String review) {
		mProgress.setMessage("Posting to Facebook...");
		mProgress.show();

		AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(mFacebook);
		Bundle params = new Bundle();
		params.putString("message", review);

		mAsyncFbRunner.request("me/feed", params, "POST",
				new WallPostListener());
	}
	
	private void postToTwitter(String post){
		try {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

			// Access Token
			String access_token = mSharedPreferences.getString(
					PREF_KEY_OAUTH_TOKEN, "");
			// Access Token Secret
			String access_token_secret = mSharedPreferences.getString(
					PREF_KEY_OAUTH_SECRET, "");

			AccessToken accessToken = new AccessToken(access_token,
					access_token_secret);
			Twitter twitter = new TwitterFactory(builder.build())
					.getInstance(accessToken);

			// Update status
			twitter4j.Status response = twitter.updateStatus(post);

			Log.d("Status", "> " + response.getText());
		} catch (TwitterException e) {
			// Error in updating status
			Log.d("Twitter Update Error", e.getMessage());
		}
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

	public void onLocationChanged(Location loc) {

		double lat = 0;
		double lon = 0;

		if (loc != null) {
			lat = loc.getLatitude();
			lon = loc.getLongitude();

		}

		Geocoder geocoder = new Geocoder(this, Locale.getDefault());

		try {
			List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

			if (addresses != null && addresses.size() > 0) {
				Address returnedAddress = addresses.get(0);
				String addr = "";
				for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
					addr = addr + " " + returnedAddress.getAddressLine(i);
				}

				if (lat != 0 && lon != 0)
					address = addr.trim();
				else
					address = "";

				Log.i("==Address==", address);

			} else {
				Log.i("==Address==", "Not Found");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public class PostingTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog dialog = new ProgressDialog(PostActivity.this);
		private String msg;

		protected void onPreExecute() {
			dialog.setMessage("Posting Shout..");
			dialog.show();
		}

		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			if (mFacebookCb.isChecked() && mFacebook.isSessionValid())
				postToFacebook(msg);

			if(isTwitterLoggedInAlready() && mTwitterCb.isChecked())
				postToTwitter(msg);
			
			finish();
		}

		@Override
		protected Boolean doInBackground(String... params) {

			msg = params[0];
			ServiceHelper srv = new ServiceHelper();
			DbHelper db = new DbHelper(PostActivity.this);
			Login login = db.getLogin();
			
			String share = "";
			if(mFacebookCb.isChecked()) share += " *facebook";
			if(mTwitterCb.isChecked()) share += " *twitter";
			
			srv.postShout(login.get_usr(), msg + share, address);

			return true;
		}

	}

	public boolean isTwitterLoggedInAlready() {
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		location.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000 * 60 * 30, 1000, this);
		location.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				1000 * 60 * 30, 1000, this);

	}
}
