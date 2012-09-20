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

	public boolean getShout(Context context) {

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
						json.getString("time"), json.getString("via"),
						json.getString("location"));

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
	
	public boolean getShoutMe(Context context, String alias) {

		String text;
		DbHelper db = new DbHelper(context);
		int firstId = db.getFirstShoutId();

		try {

			text = (String) clients.call("getmsgme", alias, "" + firstId);
			JSONArray jsonArray = new JSONArray(text);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);

				Shout shout = new Shout(json.getString("id_msg"),
						json.getString("nid"), json.getString("fullname"),
						json.getString("alias"), json.getString("msg"),
						json.getString("thumb"), json.getString("status"),
						json.getString("time"), json.getString("via"),
						json.getString("location"));

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

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date date = new Date();

			InternetHelper inet = new InternetHelper();
			String imgName = "usr@default";
			String imgUrl = "http://www.amikomsocial.com/img/" + id + ".png";
			
			JSONArray jsUsr = new JSONArray((String) clients.call("getuser", id));
			JSONObject dtUsr = jsUsr.getJSONObject(0);

			String sts = json.getString("status");
			if (sts.equals("1")) {

				int alumni = Integer.parseInt(json.getString("alumni"));
				int status = (alumni == 1) ? 3 : 1;

				Login login = new Login(id, status, json.getString("name"),
						dateFormat.format(date), dtUsr.getString("alias"), 0);
				db.insertLogin(login);

				inet.downloadImage(imgUrl, imgName);

				return true;

			} else {
				String txt = (String) client.call("logindosen", id, pwd);
				JSONArray jsArray = new JSONArray("[" + txt + "]");
				JSONObject js = jsArray.getJSONObject(0);

				String ists = js.getString("status");
				if (ists.equals("1")) {

					Login login = new Login(id, 2, js.getString("name"),
							dateFormat.format(date), dtUsr.getString("alias"), 0);
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
	
	public boolean getCalendar(Context context){
		DbHelper db = new DbHelper(context);
		Login log = db.getLogin();
		String text;
		
		try{
			if (db.isMhs())
				text = (String) client.call("jdwkuliah", log.get_usr());
			else
				text = (String) client.call("jdwdosen", log.get_usr());

			JSONArray jsonArray = new JSONArray(text);
			if (jsonArray.length() > 0) {

				db.deleteCalendar();

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject json = jsonArray.getJSONObject(i);

					String[] jam = json.getString("jam").split("\\-+");
					if (db.isMhs()) {
						Cald cal = new Cald(json.getString("hari").trim() + " - "
								+ json.getString("kuliah"),
								json.getString("fdate") + " " + jam[0],
								json.getString("edate") + " " + jam[1],
								"STMIK Amikom - " + json.getString("ruang"),
								json.getString("dosen"),
								Integer.parseInt(json.getString("weekly")));
						db.insertCalendar(cal);
						
					}else{
						Cald cal = new Cald(json.getString("hari").trim() + " - "
								+ json.getString("mkul"),
								json.getString("fdate") + " " + jam[0],
								json.getString("edate") + " " + jam[1],
								"STMIK Amikom - " + json.getString("ruang"),
								json.getString("kelasgab"),
								Integer.parseInt(json.getString("weekly")));
						db.insertCalendar(cal);
					}
				}
				
				updateCalendarId(context);
			}	
			
			//getCalendarAc(context);
			
		} catch (XMLRPCException ex) {
			//getCalendarAc(context);
			ex.printStackTrace();
			return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		
		
		return true;
	}
	
	public boolean getCalendarAc(Context context){
		DbHelper db = new DbHelper(context);
		try {
			String text = (String) client.call("getcalendar");
			JSONArray jsonArray = new JSONArray(text);

			if (jsonArray.length() > 0) {

				db.deleteCalendarAc();

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject json = jsonArray.getJSONObject(i);
					
					Cald cal = new Cald(json.getString("detail"),
							json.getString("start"), json.getString("finish"),
							"Amikom - Amikom Calendar",
							json.getString("fdate"),
							Integer.parseInt(json.getString("weekly")));

					db.insertCalendar(cal);
				}

				updateCalendarId(context);
			}

			db.close();

		} catch (XMLRPCException er) {
			er.printStackTrace();
			return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void updateCalendarId(Context context) {
		try {
			String text = (String) client.call("jdwupdate");
			int id = Integer.parseInt(text);
			Login log = new Login();
			log.set_calendar(id);
			
			DbHelper db = new DbHelper(context);
			db.updateLogin(log);

		} catch (XMLRPCException e) {
			e.printStackTrace();
		}
	}
	
	public boolean checkCalendar(Context context) {
		DbHelper db = new DbHelper(context);
		
		Login l = db.getLogin();		
		int id = l.get_calendar();
		int pid = 0;

		try {
			String str = (String) client.call("jdwupdate");
			pid = Integer.parseInt(str);

		} catch (XMLRPCException e) {
			e.printStackTrace();
			return false;
		}

		if (!db.isCalendar() || pid > id)
			return true;
		else
			return false;
	}
	
	public String[] updateUsername(String uid, String usr) {

		String[] out = new String[2];
		try {

			String service = (String) clients.call("edituser", uid, usr);
			JSONArray jsonArray = new JSONArray(service);
			JSONObject json = jsonArray.getJSONObject(0);
			
			out[0] = json.getString("sts");
			out[1] = json.getString("msg");

		} catch (XMLRPCException e) {
			out[0] = "0";
			out[1] = "No Internet Connection";
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return out;
	}
}
