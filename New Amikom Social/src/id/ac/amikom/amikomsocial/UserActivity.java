package id.ac.amikom.amikomsocial;

import id.ac.amikom.amikomsocial.libs.ServiceHelper;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class UserActivity extends Activity {

	private String id;
	private TextView usrName, usrAlias, usrReg, usrLoc, usrShout;
	private ImageView usrImg;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar_post);
		actionBar.setTitle(R.string.app_user_title);

		actionBar.setHomeAction(new IntentAction(this, MainActivity
				.createIntent(this), R.drawable.ic_action_back));

		Bundle extras = getIntent().getExtras();
		id = extras.getString("nid");

		usrImg = (ImageView) findViewById(R.id.usrImg);
		usrName = (TextView) findViewById(R.id.usrName);
		usrAlias = (TextView) findViewById(R.id.usrAlias);
		usrReg = (TextView) findViewById(R.id.usrReg);
		usrLoc = (TextView) findViewById(R.id.usrLoc);
		usrShout = (TextView) findViewById(R.id.usrShout);

		new LoadData().execute();
	}

	private class LoadData extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog dialog = new ProgressDialog(
				UserActivity.this);
		private JSONArray jsonData;
		private Bitmap foto;

		protected void onPreExecute() {
			dialog.setMessage("Loading..");
			dialog.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			try {
				JSONObject json = jsonData.getJSONObject(0);

				if (!json.getString("foto").equals("")) {
					usrImg.setImageBitmap(foto);
				} else {
					usrImg.setImageDrawable(getResources().getDrawable(
							R.drawable.none));
				}

				usrName.setText(json.getString("fullname") + " (" + id + ")");

				String sts = "";
				if (json.getString("status").equals("1"))
					sts = "Student";
				else if (json.getString("status").equals("2"))
					sts = "Lecturer";
				else
					sts = "Alumnus";

				usrAlias.setText(json.getString("alias") + " (" + sts + ")");
				usrReg.setText("Reg.Date " + json.getString("logdate"));
				usrShout.setText(json.getString("shout") + " Shout");
				usrLoc.setText(json.getString("device") + " from "
						+ json.getString("location"));

			} catch (JSONException e) {

				e.printStackTrace();
			}

			dialog.dismiss();
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {

			ServiceHelper srv = new ServiceHelper();
			jsonData = srv.getUser(id);

			try {

				JSONObject json = jsonData.getJSONObject(0);

				InputStream in = new java.net.URL(json.getString("foto"))
						.openStream();
				foto = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return true;
		}

	}

}
