import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

class LocalStorage {
	String path = System.getProperty("user.dir") + "/data.txt";
	BufferedWriter bw;
	FileWriter fw;

	public LocalStorage() {}

	public LocalStorage(String path) {
		this.path = path;
	}

	public void write(String text) {
		try {
			fw = new FileWriter(path);
			bw = new BufferedWriter(fw);
			bw.write(text);
			if (bw != null)
				bw.close();
			if (fw != null)
				fw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void append(String text) {
		try {
			fw = new FileWriter(path, true);
			bw = new BufferedWriter(fw);
			bw.write(text);
			if (bw != null)
				bw.close();
			if (fw != null)
				fw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public String read() {
		try {
			File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			String result = "";
			while((line = br.readLine()) != null)
				result += line + "\n";
			return result;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public void filter() {}

}