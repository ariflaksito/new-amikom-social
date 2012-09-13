package id.ac.amikom.amikomsocial.libs;

public class Login {
	int _id;
	String _usr;
	int _is_mhs;
	String _name;
	String _logdate;
	String _alias;
	int _calendar;

	public Login() {

	}

	public Login(int id, String usr, int is_mhs, String name, String logdate,
			String alias, int calendar) {

		this._id = id;
		this._usr = usr;
		this._is_mhs = is_mhs;
		this._name = name;
		this._logdate = logdate;
		this._alias = alias;
		this._calendar = calendar;
		
	}
	
	public Login(String usr, int is_mhs, String name, String logdate,
			String alias, int calendar) {
		
		this._usr = usr;
		this._is_mhs = is_mhs;
		this._name = name;
		this._logdate = logdate;
		this._alias = alias;
		this._calendar = calendar;
		
	}
	
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String get_usr() {
		return _usr;
	}

	public void set_usr(String _usr) {
		this._usr = _usr;
	}

	public int get_is_mhs() {
		return _is_mhs;
	}

	public void set_is_mhs(int _is_mhs) {
		this._is_mhs = _is_mhs;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public String get_logdate() {
		return _logdate;
	}

	public void set_logdate(String _logdate) {
		this._logdate = _logdate;
	}

	public String get_alias() {
		return _alias;
	}

	public void set_alias(String _alias) {
		this._alias = _alias;
	}

	public int get_calendar() {
		return _calendar;
	}

	public void set_calendar(int _calendar) {
		this._calendar = _calendar;
	}

}
