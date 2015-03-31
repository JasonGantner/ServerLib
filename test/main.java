package gantnerj.test;

import gantnerj.lib.HttpServer;
import gantnerj.lib.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class main {
	
	private static boolean runInterface=true;
	private static boolean runAsDaemon=false;
	private static int port=8080;
	private static boolean debug=false;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for(int i=0;i<args.length;i++){
			switch(args[i]){
				case "-p":
				case "--port":
					int port2=Integer.parseInt(args[i+1]);
					if(port2>0&&port2<65536) port=port2;
					else System.out.println("Invalid port! 8080 wil be used instead.");
					break;
				case "--debug":
				case "-d":
					debug=true;
					break;
				case "-D":
					
					break;
			}
			if(runAsDaemon&&debug){
				debug=false;
				System.out.println("Debbuging disabled due to -D argument.");
			}
		}
		Server server = new HttpServer(port);
		Thread serverThread = new Thread(server);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
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
						if(server.isRunning()) {
							server.stop();
							while(serverThread.isAlive());
						}
						serverThread=new Thread(server);
						serverThread.setDaemon(true);
						serverThread.start();
						daemonize();
						break;
					case "3":
						server.toggleDebuggable();
						debug=!debug;						
						break;
					case "4":
						if(server.isRunning()) server.stop();
						runInterface=false;
						break;
					default : break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while(runAsDaemon){
			
		}
	}
	
	private static void daemonize() throws IOException{
		runInterface=false;
		runAsDaemon=true;
		System.in.close();
		System.out.close();
		System.err.close();
	}
}
