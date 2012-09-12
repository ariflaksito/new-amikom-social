package id.ac.amikom.amikomsocial.libs;

import java.util.Calendar;


public class DateParse {

	private String strDate;

	/* format yyyy-MM-dd HH:ii:ss */
	public DateParse(String format) {
		strDate = format.trim();
	}

	public long parse() {
		Calendar current_time = Calendar.getInstance();
		String[] f = strDate.split("\\s+");

		String[] date = f[0].split("[-]");
		String[] time = f[1].split("[:]");

		current_time.add(Calendar.YEAR, Integer.parseInt(date[0].trim()));
		current_time.add(Calendar.MONTH, Integer.parseInt(date[1].trim()));
		current_time
				.add(Calendar.DAY_OF_YEAR, Integer.parseInt(date[2].trim()));
		current_time
				.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0].trim()));
		current_time.set(Calendar.MINUTE, Integer.parseInt(time[1].trim()));
		current_time.set(Calendar.MILLISECOND, 0);

		return current_time.getTimeInMillis();
	}

	public String parseString() {
		
		String[] f = strDate.split("\\s+");

		String[] date = f[0].split("[-]");
		String[] time = f[1].split("[:]");

		String[] month = {"","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};		
		
		return month[Integer.parseInt(date[1].trim())]+" "+
			date[2]+" at "+time[0]+":"+time[1];
        
	}
}
