import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.*;



class main {
	
	static StatusChecker st = StatusChecker.getInstance();
	static LocalStorage ls = new LocalStorage();

	public static void poll() {
		System.out.println("poll");
	}

	public static void fetch() {
		System.out.println("fetch");
	}

	public static void history() {
		System.out.println(ls.read());
	}

	public static void backup() {
		System.out.println("backup");
	}
	public static void restore() {
		System.out.println("backup");
	}
	public static void services() {
		System.out.println("backup");
	}
	public static void status() {
		System.out.println("backup");
	}
	public static void main(String args[]) throws Exception {

		switch(args[0])
		{
			case "poll": 
				st.poll();
				break;
			case "fetch":
				fetch();
				break;
			case "history":
				history();
				break;
			case "backup":
				backup();
				break;
			case "restore":
				restore();
				break;
			case "services":
				services();
				break;
			case "status":
				status();
				break;
			default:
				System.out.println("Invalid command");
		}

	}


}
