import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.Iterator;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.*;
import java.time.LocalDateTime;

class StatusChecker{

	private static StatusChecker instance = null;

	TreeMap<String, String> sv = new TreeMap<String,String>();
	TreeMap statusSv = new TreeMap();

	private StatusChecker() {
		statusSv.put("bitbucket", "/api/v2/status.json");
		statusSv.put("github", "/api/status.json");
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

	public void poll(){
		Set<String> keys = sv.keySet();

		for(String key : keys) {
			String tmp = "[" + key + "]" + " " + LocalDateTime.now() + " - " + getStatus(key);
			System.out.println(tmp);
		}


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
		} 
		catch(Exception e) {
			System.out.println("Impossible to retrieve data");
		}


		return parser(txt);
	}

	public String getInfo(URL url, String service) throws IOException {
		url = new URL(url.toString() + statusSv.get(service));
		System.out.println(url);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String inputLine;
        String result = "";
        while ((inputLine = in.readLine()) != null)
            result += inputLine;
        in.close();
        return result;
	}
	
	public String parser(String text){
		String[] tokens = text.split(":");
		for(int i = 0; i < tokens.length; i++) {
			if(tokens[i].contains("status"))
				System.out.println(tokens[i]);
				if(tokens[i+1].contains("ok") || tokens[i+1].contains("good") || tokens[i+3].contains("All Systems Operational"))
					return "up";
				else
					return "down";
		}
		return "unknow";
	}

}