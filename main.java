import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.*;


class main {

	public static void main(String args[]) {
		StatusChecker st = new StatusChecker();
		String status = st.getStatus("github");
		System.out.println(status);


	}


}
