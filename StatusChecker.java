import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Scanner;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.time.LocalDateTime;
import java.io.InputStreamReader;

class StatusChecker{

	private static StatusChecker instance = null;

	TreeMap<String, String> sv = new TreeMap<String,String>();
	TreeMap statusSv = new TreeMap();
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
		} 
		catch(IOException e) {
			System.out.println("Impossible to retrieve data");
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

	public void poll(){
		Set<String> keys = sv.keySet();

		for(String key : keys) {
			String tmp = "[" + key + "]" + " " + LocalDateTime.now() + " - " + getStatus(key) + "\n";
			System.out.print(tmp);
			ls.append(tmp);
		}
	}
	
	public void fetch() {
		fetch(5);
	}

	public void fetch(int interval) {
		System.out.println("Polling services every " + interval + " seconds\nPress CTRL-C to abort.");
		while(true) {
			System.out.println("Polling services...");
			poll();
			try {
				Thread.sleep(interval * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void history() {
		System.out.print(ls.read());
	}


	public void backup(String path, String format) {
		LocalStorage newLs = new LocalStorage(path);
		String data = ls.read();
		newLs.write(data);
	}

	public void restore(String path, String[] merge) {
		LocalStorage newLs = new LocalStorage(path);
		String line = "";
		String result = "";
		result = newLs.readLine();

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