import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class LocalStorage {
	String path = System.getProperty("user.dir") + "/data.txt";
	String format = "\\[\\w+\\] \\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d.\\d\\d\\d - (unknown|up|down)";
	BufferedWriter bw;
	FileWriter fw;

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

	public String readLine() {
		try {
			File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String result = "", line = "";
			while((line = br.readLine()) != null)
				if(!isValid(line)) {
					System.err.println("File " + line + " is invalid!");
					System.exit(1);
				} else 
					result += line + "\n";
			return result;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isValid(String line) {
		Pattern p = Pattern.compile(format);
		Matcher m = p.matcher(line);
		return m.matches();
	}

	public void filter() {}

}