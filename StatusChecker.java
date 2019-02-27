import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Arrays;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.time.LocalDateTime;
import java.io.InputStreamReader;

class StatusChecker{

	private static StatusChecker instance = null;

	TreeMap<String, String> sv = new TreeMap<String,String>();
	TreeMap<String, String> statusSv = new TreeMap<String,String>();
	LocalStorage ls = new LocalStorage( System.getProperty("user.dir") + "/data.txt");


	private StatusChecker() {
		statusSv.put("bitbucket", "/api/v2/status.json");
		statusSv.put("github", "/api/v2/status.json");
		statusSv.put("slack", "/api/current");
		try {
			File file = new File(System.getProperty("user.dir") + "/config.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while((line = br.readLine()) != null) {
				String[] token = line.split("[|]");
				sv.put(token[0], token[1]);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static StatusChecker getInstance() {
		if (instance == null)
			instance = new StatusChecker();
		return instance;
	}

	public String getStatus(String service) {
		String txt = "";
		try {	
			URL url = new URL(sv.get(service).toString());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setFollowRedirects(true);
			con.setConnectTimeout(10000); //10 seconds timeout
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			if(responseCode == 302)
				url = new URL(con.getHeaderField(11)); //11 is the new location position in header
				con = (HttpURLConnection) url.openConnection();
				responseCode = con.getResponseCode();
			if(responseCode != 200)
				throw new IOException();

			txt = getInfo(url, service);
			return parser(txt);
		} catch(SocketTimeoutException e) {
			System.err.println("Connection timed out");
			return "unknown";
		} catch(IOException e) {
			System.err.println("Impossible to retrieve data");
			return "unknown";
		}
	}

	public String getInfo(URL url, String service) throws IOException {
		url = new URL(url.toString() + statusSv.get(service));		
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String inputLine;
        String result = "";
        while ((inputLine = in.readLine()) != null)
            result += inputLine;
        in.close();
        return result;
	}
	
	public String parser(String text){
		String[] tokens = text.split("[:{,}]+");
		Boolean inStatus = false;
		for(int i = 0; i < tokens.length; i++) {
			if(tokens[i].contains("status"))
				inStatus = true;
			if(inStatus && (tokens[i].contains("ok") || tokens[i].contains("All Systems Operational")))
				return "up";
		}
		if(inStatus) 
			// status is in json but not up/ok/operational
			return "down";
		else
			// invalid json format
			return "unknown";
	}

	public String dataToCSV(String text) {
		String[] parsed = text.split("\\n");
		String result = "";
		for(String line : parsed) {
			String[] parsedLine = line.split("[\\[\\]\\s]+");
			result += parsedLine[1] + "," + parsedLine[2] + "," + parsedLine[4] + "\n";
		}
		return result;
	}

	public String dataToTXT(String text) {
		String[] parsed = text.split("\\n");
		String result = "";
		for(String line : parsed) {
			String[] parsedLine = line.split("[\\[\\]\\s]+");
			result += parsedLine[1] + " was " + parsedLine[4] + " on " + parsedLine[2] + "\n";
		}
		return result;
	}

	public void poll(String[] only, String[] exclude){
		Set<String> keys = sv.keySet();

		if(only != null) {
			keys.retainAll(Arrays.asList(only));
		}

		if(exclude != null) {
			for(String arg: exclude)
				keys.remove(arg);
		}

		for(String key : keys) {
			String tmp = "[" + key + "]" + " " + LocalDateTime.now() + " - " + getStatus(key) + "\n";
			System.out.print(tmp);
			ls.append(tmp);
		}
	}

	public void fetch(String[] param, String[] only, String[] exclude) {
		int interval = (param == null) ? 5 : Integer.parseInt(param[0]);
		System.out.println("Polling services every " + interval + " seconds\nPress CTRL-C to abort.");
		while(true) {
			System.out.println("Polling services...");
			poll(only, exclude);
			try {
				Thread.sleep(interval * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void history(String[] only) {
		System.out.print(ls.readLine(only));
	}


	public void backup(String path, String[] format) {
		LocalStorage newLs = new LocalStorage(path);
		String data = ls.read();
		if(format == null) {}
		else if(format[0].equals("csv"))
			data = dataToCSV(data);
		else if(format[0].equals("txt"))
			data = dataToTXT(data);
		newLs.write(data);
	}

	public void restore(String path, String[] merge) {
		LocalStorage newLs = new LocalStorage(path);
		String line = "";
		String result = "";
		result = newLs.readLine(null);

		if (merge == null)
			ls.write(result);
		else
			ls.append(result);
	}

	public void services() {
		Set<String> keys = sv.keySet();
		System.out.println("List of configured services");
		for(String key : keys) {
			System.out.println("\tService name: " + key + "\t\t" + "Service endpoint: " + sv.get(key));
		}
	}

}