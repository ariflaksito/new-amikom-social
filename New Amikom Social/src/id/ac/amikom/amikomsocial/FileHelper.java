package id.ac.amikom.amikomsocial;

import java.io.File;
import java.io.IOException;

public class FileHelper {
	
	private String path;
	
	public FileHelper(){
		path = "/mnt/sdcard/amikom/";
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void deleteData() {

		File file = new File(path);

		if (file.exists()) {
			String deleteCmd = "rm -r " + path;
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(deleteCmd);
			} catch (IOException e) {
			}
		}
	}
	
	public void createDir(){
		new File(path).mkdirs();
	}
	
	public boolean checkDir(){
		return new File(path).isDirectory();
	}
	
	
}
