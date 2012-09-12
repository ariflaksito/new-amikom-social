package id.ac.amikom.amikomsocial.libs;

import java.net.URI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import android.content.Context;

public class ServiceHelper {

	//private URI uri = URI.create("http://www.amikom.ac.id/index.php/service/");
	//private XMLRPCClient client = new XMLRPCClient(uri);

	private URI uris = URI.create("http://www.amikomsocial.com/service/");
	private XMLRPCClient clients = new XMLRPCClient(uris);

	public boolean getShoutService(Context context) {

		String text;
		DbHelper db = new DbHelper(context);
		int lastId = db.getLastShoutId();
		
		try {
						
			text = (String) clients.call("getmsg", ""+lastId, "100");
			JSONArray jsonArray = new JSONArray(text);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				
				Shout shout = new Shout(json.getString("id_msg"), json.getString("nid"),
						json.getString("fullname"), json.getString("alias"),
						json.getString("msg"), json.getString("thumb"),
						json.getString("status"), json.getString("time"),
						json.getString("via"));
				
				db.insertShout(shout);
				
			}

		} catch (XMLRPCException er) {
			er.printStackTrace();
			return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
