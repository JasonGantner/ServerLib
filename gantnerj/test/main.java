package gantnerj.test;

import gantnerj.lib.HttpServer;
import gantnerj.lib.TcpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

class main{

	private static boolean runInterface=true;
	private static boolean runAsDaemon=false;
	private static int port=8080;
	private static boolean debug=false;
	private static String baseDir="/var/www";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//init phase
		//.allowing common file extensions
		Vector<String> VALID_EXTENSIONS = HttpServer.VALID_EXTENSIONS;
		if(!VALID_EXTENSIONS.contains("txt")) VALID_EXTENSIONS.add("txt");
		if(!VALID_EXTENSIONS.contains("htm")) VALID_EXTENSIONS.add("htm");
		if(!VALID_EXTENSIONS.contains("html")) VALID_EXTENSIONS.add("html");
		if(!VALID_EXTENSIONS.contains("css")) VALID_EXTENSIONS.add("css");
		if(!VALID_EXTENSIONS.contains("js")) VALID_EXTENSIONS.add("js");
		if(!VALID_EXTENSIONS.contains("jpg")) VALID_EXTENSIONS.add("jpg");
		if(!VALID_EXTENSIONS.contains("png")) VALID_EXTENSIONS.add("png");
		//.parsing command line args
		for(int i=0;i<args.length;i++){
			//TODO switch->if
			switch(args[i]){
				//..setting port
				case "-p":
				case "--port":
					int port2=Integer.parseInt(args[i+1]);
					if(port2>0&&port2<65536) port=port2;
					else System.out.println("Invalid port! 8080 wil be used instead.");
					break;
				//..enabling debugging
				case "--debug":
				case "-d":
					debug=true;
					break;
				//..daemon
				case "--daemon":
				case "-D":
					//runAsDaemon=true;
					break;
				//..setting the base directory
				case "-b":
				case "--baseDir":
					baseDir=args[i+1];
					break;
				//..allowing file extensions
				case "-e":
				case "--add-valid-extension":
					if(!VALID_EXTENSIONS.contains(args[i+1])) VALID_EXTENSIONS.add(args[i+1]);
					break;
			}
			//.disabling debugging to stdout if daemon
			if(runAsDaemon&&debug){
				debug=false;
				System.out.println("Debbuging disabled due to -D argument.");
			}
		}
		//.creating server and serverThread
		TcpServer server = new HttpServer(port,baseDir);
		Thread serverThread = new Thread(server);
		//.openning stdin
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		//running
		while(runInterface){
			System.out.print("Server written in Java by Jason Gantner\r\n\t1 - ");
			if(serverThread.isAlive()) System.out.print("Stop");
			else System.out.print("Start");
			System.out.print(" server\n\t2 - Make server run as daemon\n\t3 - ");
			if(server.isDebuggable()) System.out.print("Disable");
			else System.out.print("Enable");
			System.out.println(" debugging\n\t4 - Quit & kill server\n\t5 - Refresh");
			try {
				String choice = in.readLine();
				switch (choice) {
					case "1":
						if(serverThread.isAlive()) server.stop();
						else{
							if(serverThread.getState()==Thread.State.TERMINATED) serverThread = new Thread(server);
							serverThread.start();
						}
						break;
					case  "2":
						/*
						if(server.isRunning()) {
							server.stop;
							while(serverThread.isAlive());
						}
						serverThread=new Thread(server);
						serverThread.setDaemon(true);
						serverThread.start();
						daemonize(); //forget about Java running as daemon for now */
						break;
					case "3":
						server.toggleDebuggable();
						debug=!debug;
						break;
					case "4":
						if(serverThread.isAlive()) server.stop();;
						runInterface=false;
						break;
					default : break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/* forget about java running as daemon for now
		while(runAsDaemon){

		}
	}

	private static void daemonize() throws IOException{
		runInterface=false;
		runAsDaemon=true;
		System.in.close();
		System.out.close();
		System.err.close();//*/
	}
}
