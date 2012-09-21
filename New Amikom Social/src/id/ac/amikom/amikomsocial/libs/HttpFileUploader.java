package id.ac.amikom.amikomsocial.libs;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.util.Log;

public class HttpFileUploader implements Runnable {

	URL connectURL;
	String params;
	String responseString;
	DbHelper db = null;
	String fileName;
	byte[] dataToServer;

	String s;

	public HttpFileUploader(Context context, String urlString, String params,
			String fileName) {
		db = new DbHelper(context);

		try {
			connectURL = new URL(urlString);
		} catch (Exception ex) {
			Log.i("URL FORMATION", "MALFORMATED URL");
		}
		this.params = params + "=";
		this.fileName = fileName;

	}

	public String doStart() throws FileNotFoundException {
		fileInputStream = new FileInputStream(new File(fileName));
		uploadFile();

		return s;
	}

	FileInputStream fileInputStream = null;

	void uploadFile() {

		Login login = db.getLogin();
		String exsistingFileName = login.get_usr() + ".png";

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";		
		
		try {			
						
			HttpURLConnection conn = (HttpURLConnection) connectURL
					.openConnection();

			conn.setDoInput(true);

			conn.setDoOutput(true);

			conn.setUseCaches(false);

			conn.setRequestMethod("POST");

			conn.setRequestProperty("Connection", "Keep-Alive");

			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"ifile2094\";filename=\""
					+ exsistingFileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);			

			int bytesAvailable = fileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];

			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			fileInputStream.close();
			dos.flush();

			InputStream is = conn.getInputStream();

			int ch;

			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			s = b.toString();
			Log.i("Response", s);
			dos.close();

		} catch (MalformedURLException ex) {
		} catch (IOException ioe) {
		}
	}

	public void run() {

	}

}
