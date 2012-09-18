package id.ac.amikom.amikomsocial.libs;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "db_adem";

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, 44);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE if not exists shout "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, public_id INTEGER, "
				+ "nid TEXT, " + "name TEXT, alias TEXT, " + "msg TEXT, "
				+ "foto TEXT, " + "sts TEXT, location TEXT, "
				+ "time TIMESTAMP NOT NULL DEFAULT current_timestamp, "
				+ "via TEXT);");

		db.execSQL("CREATE TABLE if not exists calendar "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "title TEXT, "
				+ "start TIMESTAMP NOT NULL DEFAULT current_timestamp, "
				+ "end TIMESTAMP NOT NULL DEFAULT current_timestamp, "
				+ "location TEXT, " + "detail TEXT, " + "status INTEGER);");

		db.execSQL("CREATE TABLE if not exists login "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "usr TEXT, "
				+ "is_mhs INTEGER, " + "name TEXT, " + "logdate DATE,"
				+ "alias TEXT," + "calendar INTEGER);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS shout");
		db.execSQL("DROP TABLE IF EXISTS login");
		db.execSQL("DROP TABLE IF EXISTS materi");
		db.execSQL("DROP TABLE IF EXISTS calendar");
		db.execSQL("DROP TABLE IF EXISTS news");
		db.execSQL("DROP TABLE IF EXISTS info");

		onCreate(db);
	}

	public void insertShout(Shout shout) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put("public_id", shout.get_public_id());
		values.put("nid", shout.get_nid());
		values.put("name", shout.get_name());
		values.put("alias", shout.get_alias());
		values.put("msg", shout.get_msg());
		values.put("foto", shout.get_foto());
		values.put("sts", shout.get_sts());
		values.put("time", shout.get_time());
		values.put("via", shout.get_via());
		values.put("location", shout.get_location());

		db.insert("shout", null, values);
		db.close();

	}

	public List<Shout> getShout() {
		List<Shout> shoutList = new ArrayList<Shout>();

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cur = db.rawQuery(
				"Select _id,nid,name,alias,msg,foto,sts,time,via,location "
						+ "From shout Order By time Desc Limit 100", null);

		if (cur.moveToFirst()) {
			do {
				Shout shout = new Shout();
				shout.set_id(Integer.parseInt(cur.getString(cur
						.getColumnIndex("_id"))));
				shout.set_nid(cur.getString(cur.getColumnIndex("nid")));
				shout.set_name(cur.getString(cur.getColumnIndex("name")));
				shout.set_alias(cur.getString(cur.getColumnIndex("alias")));
				shout.set_msg(cur.getString(cur.getColumnIndex("msg")));
				shout.set_foto(cur.getString(cur.getColumnIndex("foto")));
				shout.set_sts(cur.getString(cur.getColumnIndex("sts")));
				shout.set_time(cur.getString(cur.getColumnIndex("time")));
				shout.set_via(cur.getString(cur.getColumnIndex("via")));
				shout.set_location(cur.getString(cur.getColumnIndex("location")));

				shoutList.add(shout);
			} while (cur.moveToNext());
		}

		cur.close();
		db.close();

		return shoutList;
	}

	public List<Shout> getShoutMe(String alias) {
		List<Shout> shoutList = new ArrayList<Shout>();

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cur = db.rawQuery(
				"Select _id,nid,name,alias,msg,foto,sts,time,via,location "
						+ "From shout Where msg like '%@" + alias + "%' "
						+ "Order By time Desc", null);

		if (cur.moveToFirst()) {
			do {
				Shout shout = new Shout();
				shout.set_id(Integer.parseInt(cur.getString(cur
						.getColumnIndex("_id"))));
				shout.set_nid(cur.getString(cur.getColumnIndex("nid")));
				shout.set_name(cur.getString(cur.getColumnIndex("name")));
				shout.set_alias(cur.getString(cur.getColumnIndex("alias")));
				shout.set_msg(cur.getString(cur.getColumnIndex("msg")));
				shout.set_foto(cur.getString(cur.getColumnIndex("foto")));
				shout.set_sts(cur.getString(cur.getColumnIndex("sts")));
				shout.set_time(cur.getString(cur.getColumnIndex("time")));
				shout.set_via(cur.getString(cur.getColumnIndex("via")));
				shout.set_location(cur.getString(cur.getColumnIndex("location")));

				shoutList.add(shout);
			} while (cur.moveToNext());
		}

		cur.close();
		db.close();

		return shoutList;
	}

	public int getLastShoutId() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor pub_id = db.rawQuery(
				"Select _id,public_id From shout Order By time Desc Limit 1",
				new String[] {});

		int lastid = 0;
		if (pub_id.moveToFirst()) {
			do {
				lastid = Integer.parseInt(pub_id.getString(1).toString());
			} while (pub_id.moveToNext());
		}

		pub_id.close();
		db.close();

		return lastid;
	}

	public int getFirstShoutId() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor pub_id = db.rawQuery(
				"Select _id,public_id From shout Order By time Limit 1",
				new String[] {});

		int lastid = 0;
		if (pub_id.moveToFirst()) {
			do {
				lastid = Integer.parseInt(pub_id.getString(1).toString());
			} while (pub_id.moveToNext());
		}

		pub_id.close();
		db.close();

		return lastid;
	}

	public void deleteShout() {
		SQLiteDatabase db = this.getWritableDatabase();
		String str = "Delete From shout";

		db.execSQL(str);
		db.close();
	}

	public void insertLogin(Login login) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put("name", login.get_name());
		values.put("usr", login.get_usr());
		values.put("alias", login.get_alias());
		values.put("logdate", login.get_logdate().toString());
		values.put("is_mhs", login.get_is_mhs());
		values.put("calendar", login.get_calendar());

		db.insert("login", null, values);
		db.close();
	}

	public Login getLogin() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor c = db.rawQuery(
				"Select _id,name, is_mhs, alias, usr, logdate, calendar "
						+ "From login Limit 1", new String[] {});
		if (c != null)
			c.moveToFirst();

		Login login = new Login(c.getInt(0), c.getString(4), c.getInt(2),
				c.getString(1), c.getString(5), c.getString(3), c.getInt(6));

		c.close();
		db.close();

		return login;

	}

	public void updateLogin(Login login) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("alias", login.get_alias());
		values.put("calendar", login.get_calendar());
		
		// updating row
		db.update("login", values, "_id = ?",
				new String[] { String.valueOf(login.get_id()) });
		
		db.close();
		
	}

	public boolean isLogin() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor c = db.rawQuery(
				"Select _id,name, is_mhs, alias, usr, logdate, calendar "
						+ "From login Limit 1", new String[] {});
		if (c != null)
			c.moveToFirst();

		int count = c.getCount();
		c.close();
		db.close();

		if (count > 0)
			return true;
		else
			return false;
	}

	public boolean isMhs() {
		Login log = getLogin();
		int sts = log.get_is_mhs();

		if (sts == 1)
			return true;
		else
			return false;
	}

	public void deleteLogin() {
		SQLiteDatabase db = this.getWritableDatabase();
		String str = "Delete From login";

		db.execSQL(str);
		db.close();
	}

	public void insertCalendar(Calendar cal) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put("title", cal.get_title());
		values.put("start", cal.get_start());
		values.put("end", cal.get_end());
		values.put("location", cal.get_location());
		values.put("detail", cal.get_detail());
		values.put("title", cal.get_title());
		values.put("status", cal.get_status());

		db.insert("calendar", null, values);
		db.close();

	}

	public List<Calendar> getCalendar() {
		List<Calendar> calList = new ArrayList<Calendar>();
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor c = db.rawQuery(
				"Select _id,title,start,end,location,detail,status "
						+ "From calendar Order By _id", new String[] {});

		if (c != null)
			c.moveToFirst();		
				
		if (c.moveToFirst()) {
			do {
				Calendar cal = new Calendar(c.getString(1), c.getString(2),
						c.getString(3), c.getString(4), c.getString(5),
						Integer.parseInt(c.getString(6)));

				calList.add(cal);
			} while (c.moveToNext());
		}

		c.close();
		db.close();

		return calList;

	}

	public boolean isCalendar() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor c = db.rawQuery(
				"Select _id,title,start,end,location,detail,status "
						+ "From calendar Order By _id", new String[] {});
		
		if (c != null)
			c.moveToFirst();

		int count = c.getCount();
		c.close();
		db.close();

		if (count > 0)
			return true;
		else
			return false;
	}

	public void deleteCalendar() {
		SQLiteDatabase db = this.getWritableDatabase();
		String str = "Delete From calendar Where location like 'STMIK Amikom%'";

		db.execSQL(str);
		db.close();
	}

	public void deleteCalendarAc() {
		SQLiteDatabase db = this.getWritableDatabase();
		String str = "Delete From calendar Where location like 'Amikom%'";

		db.execSQL(str);
		db.close();
	}

}
