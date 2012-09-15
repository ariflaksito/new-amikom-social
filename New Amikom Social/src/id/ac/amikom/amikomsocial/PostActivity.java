package id.ac.amikom.amikomsocial;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PostActivity extends Activity implements LocationListener{
	
	protected LocationManager location;
	private String address;
	private TextView countInfo;
	private EditText reviewEdit;
	private Button btnPost;

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
		btnPost = (Button) findViewById(R.id.button_post);
		
		
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

		btnPost.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				Log.i("==Location==", address);
				
			}
		});
		
		reviewEdit.addTextChangedListener(mTextEditorWatcher);

		location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		Location loc = location.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);		
		if (loc != null) {						
		}else{
			loc = location.getLastKnownLocation(LocationManager.GPS_PROVIDER);			
		}
		
		this.onLocationChanged(loc);
		
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
				address = addr.trim();				
				Log.i("==Location==", address);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, PostActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	public void onProviderDisabled(String arg0) {	
		
	}

	public void onProviderEnabled(String arg0) {	
		
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		location.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*60*30, 1000, this);
		location.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000*60*30, 1000, this);
	}

	
}
