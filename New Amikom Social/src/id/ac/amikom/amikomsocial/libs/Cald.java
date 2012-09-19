package id.ac.amikom.amikomsocial.libs;

public class Cald {
	
	int _id;
	String _title;
	String _start;
	String _end;
	String _location;
	String _detail;
	int _status;
	
	public Cald(){ }
	
	public Cald(int id, String title, String start, String end, String location, String detail, int status){
		this._id = id;
		this._title = title;
		this._start = start;
		this._end = end;
		this._location = location;
		this._detail = detail;
		this._status = status;
	}
	
	public Cald(String title, String start, String end, String location, String detail, int status){	
		this._title = title;
		this._start = start;
		this._end = end;
		this._location = location;
		this._detail = detail;
		this._status = status;
	}
	
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String get_title() {
		return _title;
	}

	public void set_title(String _title) {
		this._title = _title;
	}

	public String get_start() {
		return _start;
	}

	public void set_start(String _start) {
		this._start = _start;
	}

	public String get_end() {
		return _end;
	}

	public void set_end(String _end) {
		this._end = _end;
	}

	public String get_location() {
		return _location;
	}

	public void set_location(String _location) {
		this._location = _location;
	}

	public String get_detail() {
		return _detail;
	}

	public void set_detail(String _detail) {
		this._detail = _detail;
	}

	public int get_status() {
		return _status;
	}

	public void set_status(int _status) {
		this._status = _status;
	}

	
	
	
}
