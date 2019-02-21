import java.util.TreeMap;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.*;

class StatusChecker{

	TreeMap sv = new TreeMap();
	TreeMap statusSv = new TreeMap();

	public StatusChecker() {
		sv.put("bitbucket", "https://status.bitbucket.org");
		sv.put("github", "https://status.github.com");

		statusSv.put("bitbucket", "/api/v2/status.json");
		statusSv.put("github", "/api/status.json");
	}

	public String getStatus(String service) {
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

			getInfo(url, service);

		} 
		catch(Exception e) {
			System.out.println("Impossible to retrieve data");
		}

		return "down";
	}

	public void getInfo(URL url, String service) throws IOException {
		url = new URL(url.toString() + statusSv.get(service));
		System.out.println(url);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();
	}
	
	public void parser(){

	}

}