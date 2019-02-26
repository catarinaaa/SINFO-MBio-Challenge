import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



class main {
	
	static StatusChecker st = StatusChecker.getInstance();

	public static void printError(String msg) {
		System.out.println(msg);
		System.exit(1);
	}

	public static String[] parseOption(String[] args, String option) {
		String regex = "--" + option + "=\\w+(,\\w+)*";
		Pattern p = Pattern.compile(regex);
		String[] result;
		for(String arg : args) {
			Matcher m = p.matcher(arg);
			if(m.matches())
				return arg.substring(arg.indexOf("=")+1).split("[,]+");
		}
		return null;
	}

	public static void main(String args[]) throws Exception {

		if(args.length == 0) printError("No command introduced");
		
		String[] only = parseOption(args, "only");
		String[] except = parseOption(args, "except") ;
		String[] format = parseOption(args, "format");
		String[] merge = parseOption(args, "merge");
		String[] refresh = parseOption(args, "refresh");

		switch(args[0])
		{
			case "poll": 
				if(format != null || refresh != null || merge != null)
					printError("Invalid option");
				st.poll();
				break;
			case "fetch":
				if(format != null || merge != null)
					printError("Invalid option");
				if(refresh != null && (refresh.length > 1 || !refresh[0].matches("\\d+")))
					printError("Invalid option argument");
				if(refresh != null)
					st.fetch(Integer.parseInt(refresh[0]));
				else
					st.fetch();
				break;
			case "history":
				st.history();
				break;
			case "backup":
				
				break;
			case "restore":
				break;
			case "services":
				break;
			case "status":
				break;
			default:
				System.out.println("Invalid command");
		}

	}


}
