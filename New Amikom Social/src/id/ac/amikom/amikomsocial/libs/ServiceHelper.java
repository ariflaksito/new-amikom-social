package id.ac.amikom.amikomsocial.libs;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import android.content.Context;

public class ServiceHelper {

	private URI uri = URI.create("http://www.amikom.ac.id/index.php/service/");
	private XMLRPCClient client = new XMLRPCClient(uri);

	private URI uris = URI.create("http://www.amikomsocial.com/service/");
	private XMLRPCClient clients = new XMLRPCClient(uris);

	public boolean getShoutService(Context context) {

		String text;
		DbHelper db = new DbHelper(context);
		int lastId = db.getLastShoutId();

		try {

			text = (String) clients.call("getmsg", "" + lastId, "100");
			JSONArray jsonArray = new JSONArray(text);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);

				Shout shout = new Shout(json.getString("id_msg"),
						json.getString("nid"), json.getString("fullname"),
						json.getString("alias"), json.getString("msg"),
						json.getString("thumb"), json.getString("status"),
						json.getString("time"), json.getString("via"));

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

	public boolean login(Context context, String id, String pwd) {
		
		DbHelper db = new DbHelper(context);
		
		try {
			String text = (String) client.call("login", id, pwd);
			JSONArray jsonArray = new JSONArray("[" + text + "]");
			JSONObject json = jsonArray.getJSONObject(0);
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			Date date = new Date();
			
			InternetHelper inet = new InternetHelper();
			String imgName = "amikomuser";
			String imgUrl = "http://www.amikomsocial.com/img/"+id+".png";

			String sts = json.getString("status");
			if (sts.equals("1")) {

				int alumni = Integer.parseInt(json.getString("alumni"));
				int status = (alumni == 1) ? 3 : 1;				
				
				Login login = new Login(id, status, json.getString("name"), dateFormat.format(date), "", 0);
				db.insertLogin(login);
				
				inet.downloadImage(imgUrl, imgName);

				return true;

			} else {
				String txt = (String) client.call("logindosen", id, pwd);
				JSONArray jsArray = new JSONArray("[" + txt + "]");
				JSONObject js = jsArray.getJSONObject(0);								

				String ists = js.getString("status");
				if (ists.equals("1")) {

					Login login = new Login(id, 2, js.getString("name"), dateFormat.format(date), "", 0);
					db.insertLogin(login);
					
					inet.downloadImage(imgUrl, imgName);
					
					return true;

				} else {
					return false;

				}
			}

		} catch (XMLRPCException e) {
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean postShout(String uid, String msg, String location) {
		try {
			
			String device = android.os.Build.MODEL;
			clients.call("postmsg", uid, msg, device, location);

		} catch (XMLRPCException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
