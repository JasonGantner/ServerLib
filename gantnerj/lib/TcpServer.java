package gantnerj.lib;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public abstract class TcpServer implements Runnable{

	private boolean serverRunning = false;
	private boolean debuggable = false; //show debug info ?
	private int port; //port as in listening port
	ServerSocket serverSocket;

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			Socket socket;
			serverRunning = true;
			while(serverRunning){
				if(debuggable) System.out.println("Waiting for incoming connection on port " + port + "...");
				try{
					socket = serverSocket.accept();
					if(debuggable) System.out.println("Request from " + socket.getInetAddress().getHostName() + " (" + socket.getInetAddress().getHostAddress() + ")");
					new ServerHandleThread(this, socket).start();
				}
				catch(SocketException se){
					if(debuggable) se.printStackTrace();
				}
			}
			serverSocket.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

	public void stop(){
		serverRunning = false;
		try{if(!serverSocket.isClosed()) serverSocket.close();}
		catch(IOException ioe){};
	}

  /////////////////////////
 // getters and setters //
/////////////////////////
	public boolean isDebuggable(){
		return debuggable;
	}

	public void toggleDebuggable(){
		debuggable =! debuggable;
	}

  /////////////////
 // constructor //
/////////////////
	public TcpServer(int port){
		this.port = port;
	}

  ///////////////////////////////////////
 // do what you want with the request //
///////////////////////////////////////
	public abstract void handle(String request, OutputStream out);

}
