package id.ac.amikom.amikomsocial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import id.ac.amikom.amikomsocial.libs.DbHelper;
import id.ac.amikom.amikomsocial.libs.FileHelper;
import id.ac.amikom.amikomsocial.libs.HttpFileUploader;
import id.ac.amikom.amikomsocial.libs.Login;
import id.ac.amikom.amikomsocial.libs.ServiceHelper;
import id.ac.amikom.amikomsocial.libs.Shout;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity  {

	private static final int RESULT_LOAD_IMAGE = 1;
	private Button btnLogin, btnProfile;
	private EditText txtId, txtPwd;
	private String id, pwd;
	private TextView viewId, viewUsr, viewName, viewSts, viewLog;
	private ImageView viewImg;

	public class LoginTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

		protected void onPreExecute() {
			dialog.setMessage("Login as " + id + "..");
			dialog.show();
		}

		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			if (result == true) {
				Toast.makeText(LoginActivity.this, "Login Success",
						Toast.LENGTH_LONG).show();
				finish();

			} else {
				Toast.makeText(LoginActivity.this, "Login Failed",
						Toast.LENGTH_LONG).show();
			}

		}

		protected Boolean doInBackground(String... params) {
			ServiceHelper srv = new ServiceHelper();

			if (srv.login(LoginActivity.this, id, pwd)) {
				return true;
			} else {
				return false;
			}

		}

	}

	public class EditUserTask extends AsyncTask<String, Void, Boolean> {

		ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
		boolean result = false;
		String[] text = null;

		@Override
		protected Boolean doInBackground(String... params) {
			DbHelper db = new DbHelper(LoginActivity.this);
			ServiceHelper srv = new ServiceHelper();

			Login login = db.getLogin();
			text = srv.updateUsername(login.get_usr(), params[0]);

			if (text[0].equals("1")) {
				login.set_alias(params[0]);
				db.updateLogin(login);

				Shout shout = new Shout();
				shout.set_alias(params[0]);
				db.updateAlias(login.get_usr(), params[0]);
			}

			return true;
		}

		protected void onPreExecute() {
			this.dialog.setMessage("Edit Username..");
			this.dialog.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {

			Toast.makeText(LoginActivity.this, text[1], Toast.LENGTH_LONG)
					.show();

			this.dialog.dismiss();

		}
	}

	public class UploadTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
		String out;

		protected void onPreExecute() {
			dialog.setMessage("Uploading..");
			dialog.show();
		}

		protected void onPostExecute(Boolean result) {
			this.dialog.dismiss();

			String imgPath = "";
			boolean exists = (new File("/mnt/sdcard/amikom/usr@tmp")).exists();
			if (exists) {
				imgPath = "/mnt/sdcard/amikom/usr@tmp";
			} else {
				imgPath = "/mnt/sdcard/amikom/usr@default";
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			Bitmap bmp = BitmapFactory.decodeFile(imgPath, options);
			viewImg.setImageBitmap(bmp);

			Toast.makeText(LoginActivity.this, out, Toast.LENGTH_LONG).show();

		}

		@Override
		protected Boolean doInBackground(String... params) {

			try {
				HttpFileUploader htfu = new HttpFileUploader(
						LoginActivity.this,
						"http://amikomsocial.com/service/srvimg",
						"noparamshere", params[0]);
				out = htfu.doStart();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DbHelper db = new DbHelper(this);
		if (db.isLogin()) {
			setContentView(R.layout.activity_profile);

			btnProfile = (Button) findViewById(R.id.btnEdit);
			viewId = (TextView) findViewById(R.id.viewId);
			viewUsr = (TextView) findViewById(R.id.viewUsr);
			viewName = (TextView) findViewById(R.id.viewName);
			viewSts = (TextView) findViewById(R.id.viewSts);
			viewLog = (TextView) findViewById(R.id.viewLog);
			viewImg = (ImageView) findViewById(R.id.viewImg);

			Login login = db.getLogin();

			String identity = "";
			if (login.get_is_mhs() == 1)
				identity = "Amikom Student";
			else if (login.get_is_mhs() == 2)
				identity = "Amikom Lecturer";
			else
				identity = "Amikom Alumni";

			viewId.setText("Id User. " + login.get_usr());
			viewUsr.setText("Username. " + login.get_alias());
			viewName.setText("Name User. " + login.get_name());
			viewSts.setText("Status User. " + identity);
			viewLog.setText("Login Date. " + login.get_logdate());

			String imgPath = "/mnt/sdcard/amikom/usr@default";
			if (new File(imgPath).exists()) {

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap bmp = BitmapFactory.decodeFile(imgPath, options);
				viewImg.setImageBitmap(bmp);
			}

			viewImg.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					Intent i = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

					startActivityForResult(i, RESULT_LOAD_IMAGE);

				}
			});

			btnProfile.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {

					AlertDialog.Builder alert = new AlertDialog.Builder(
							LoginActivity.this);
					alert.setTitle("Edit Username");

					LayoutInflater factory = LayoutInflater
							.from(LoginActivity.this);
					View textEntryView = factory.inflate(
							R.layout.dialog_username, null);
					final EditText postText = (EditText) textEntryView
							.findViewById(R.id.user_txt);

					alert.setView(textEntryView);

					alert.setPositiveButton("Save",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									new EditUserTask().execute(postText
											.getText().toString());

								}
							});

					alert.show();

				}
			});

		} else {
			setContentView(R.layout.activity_login);
			btnLogin = (Button) findViewById(R.id.btnLog);
			txtId = (EditText) findViewById(R.id.inputId);
			txtPwd = (EditText) findViewById(R.id.inputPwd);

			btnLogin.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					id = txtId.getText().toString();
					pwd = txtPwd.getText().toString();
					
					FileHelper fh = new FileHelper();
					fh.deleteData();
					new LoginTask().execute();

				}

			});
		}

		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar_post);
		actionBar.setTitle(R.string.app_login_title);

		actionBar.setHomeAction(new IntentAction(this, MainActivity
				.createIntent(this), R.drawable.ic_action_back));

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);

			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			Uri filePathUri = Uri.parse(cursor.getString(column_index));
			String fileName = filePathUri.getLastPathSegment().toString();

			Log.i("picture name....", fileName);
			Log.i("picture path....", picturePath);

			cursor.close();

			try {
				int inWidth = 0;
				int inHeight = 0;
				int dstWidth = 150;
				int dstHeight = 150;

				String pathOfInputImage = picturePath;
				String pathOfOutputImage = "/mnt/sdcard/amikom/usr@tmp";

				InputStream is = new FileInputStream(pathOfInputImage);

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(is, null, options);
				is.close();
				is = null;

				inWidth = options.outWidth;
				inHeight = options.outHeight;

				is = new FileInputStream(pathOfInputImage);
				options = new BitmapFactory.Options();

				options.inSampleSize = Math.max(inWidth / dstWidth, inHeight
						/ dstHeight);

				Bitmap roughBitmap = BitmapFactory.decodeStream(is, null,
						options);
				
				Matrix m = new Matrix();
				RectF inRect = new RectF(0, 0, roughBitmap.getWidth(),
						roughBitmap.getHeight());
				RectF outRect = new RectF(0, 0, dstWidth, dstHeight);
				m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
				float[] values = new float[9];
				m.getValues(values);

				// resize bitmap
				Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap,
						(int) (roughBitmap.getWidth() * values[0]),
						(int) (roughBitmap.getHeight() * values[4]), true);

				// save image
				try {
					FileOutputStream out = new FileOutputStream(
							pathOfOutputImage);
					resizedBitmap
							.compress(Bitmap.CompressFormat.JPEG, 100, out);

					new UploadTask().execute(pathOfOutputImage);

				} catch (Exception e) {
					Log.e("Image", e.getMessage(), e);
				}
			} catch (IOException e) {
				Log.e("Image", e.getMessage(), e);
			}

		}

	}

}
