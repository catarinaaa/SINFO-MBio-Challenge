import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



class main {
	
	static StatusChecker st = StatusChecker.getInstance();

	public static void printError(String msg) {
		System.err.println(msg);
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
				if(format != null || refresh != null || merge != null || except != null)
					printError("Invalid option");
				st.history();
				break;

			case "backup":
				if(refresh != null || merge != null || except != null)
					printError("Invalid option");
				if(format != null && (format[0] != "csv" || format[0] != "txt"))
					printError("Invalid argument option");
				if(args.length == 1)
					printError("Must provide a file");
				st.backup(args[1], format[0]);
				break;

			case "restore":
				if(format != null || refresh != null || only != null || except != null)
					printError("Invalid option");
				if(merge != null && !merge[0].equals("true"))
					printError("Invalid argument option");
				if(args.length == 1)
					printError("Must provide a file");
				st.restore(args[1], merge);
				break;

			case "services":
				if(format != null || refresh != null || merge != null || except != null || only != null)
					printError("Invalid option");				
				st.services();
				break;

			case "help":
				if(format != null || refresh != null || merge != null || except != null || only != null)
					printError("Invalid option");
				String cmd = "Usage:\n\ttool command [options]\n\nCommand:\n" +
				"\tpoll [--only | --except]\t\tRetrieves the status of all configured services\n" +
				"\tfetch [--refresh] [--only | --except]\tRetrieves the status of all configured services in intervals\n" +
				"\thistory [--only]\t\t\tOutputs all data from local storage\n" +
				"\tbackup <file>\t\t\t\tBackups the current internal state to a file\n" +
				"\trestore <file> [--merge]\t\tImports the internal state from a file\n" +
				"\tservices\t\t\t\tList all configured services\n" +
				"\thelp\t\t\t\t\tShows help screen\n"; 
				String opt = "\nOptions:\n\t--only=<name>\t\tSelects a specific set of services\n" +
				"\t--except=<name>\t\tExcludes a specific set of services\n" +
				"\t--refresh=<number>\tChoose polling interval [default: 5]\n" +
				"\t--merge\t\tMerge the content of the file\n"+
				"\t--format=<name>\t\tSelects the format of the output file\n";
				System.out.print(cmd + opt);
				break;
			default:
				System.out.println("Invalid command");
		}

	}


}
