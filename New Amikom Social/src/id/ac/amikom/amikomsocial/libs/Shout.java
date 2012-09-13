package id.ac.amikom.amikomsocial.libs;

public class Shout {
	int _id;
	int _public_id;
	String _nid;
	String _name;
	String _alias;
	String _msg;
	String _foto;
	String _sts;
	String _time;
	String _via;

	public Shout() {
	}

	public Shout(String public_id, String nid, String name, String alias,
			String msg, String foto, String sts, String time, String via) {

		this._public_id = Integer.parseInt(public_id);
		this._nid = nid;
		this._name = name;
		this._alias = alias;
		this._msg = msg;
		this._foto = foto;
		this._sts = sts;
		this._time = time;
		this._via = via;

	}
	
	public Shout(int id, String public_id, String nid, String name, String alias,
			String msg, String foto, String sts, String time, String via) {

		this._id = id;
		this._public_id = Integer.parseInt(public_id);
		this._nid = nid;
		this._name = name;
		this._alias = alias;
		this._msg = msg;
		this._foto = foto;
		this._sts = sts;
		this._time = time;
		this._via = via;

	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int get_public_id() {
		return _public_id;
	}

	public void set_public_id(int _public_id) {
		this._public_id = _public_id;
	}

	public String get_nid() {
		return _nid;
	}

	public void set_nid(String _nid) {
		this._nid = _nid;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public String get_alias() {
		return _alias;
	}

	public void set_alias(String _alias) {
		this._alias = _alias;
	}

	public String get_msg() {
		return _msg;
	}

	public void set_msg(String _msg) {
		this._msg = _msg;
	}

	public String get_foto() {
		return _foto;
	}

	public void set_foto(String _foto) {
		this._foto = _foto;
	}

	public String get_sts() {
		return _sts;
	}

	public void set_sts(String _sts) {
		this._sts = _sts;
	}

	public String get_time() {
		return _time;
	}

	public void set_time(String _time) {
		this._time = _time;
	}

	public String get_via() {
		return _via;
	}

	public void set_via(String _via) {
		this._via = _via;
	}

}