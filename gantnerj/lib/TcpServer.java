package gantnerj.lib;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class TcpServer implements Runnable{

	private boolean serverRunning = false;
	private boolean debuggable = false;
	private int port;

	@Override
	public void run() {
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(port);
			Socket socket;
			serverRunning = true;
			while(serverRunning){
				if(debuggable) System.out.println("Waiting for incoming connection on port " + port + "...");
				socket = serverSocket.accept();
				if(debuggable) System.out.println("Request from " + socket.getInetAddress().getHostName() + " (" + socket.getInetAddress().getHostAddress() + ")");
				new ServerHandleThread(this, socket).start();
			}
			serverSocket.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

	public boolean isRunning(){
		return serverRunning;
	}
	public boolean isDebuggable(){
		return debuggable;
	}

	public void toggleDebuggable(){
		debuggable =! debuggable;
	}
	public void stop(){
		serverRunning = false;
	}

	public TcpServer(int port){
		this.port = port;
	}

	public abstract void handle(String request, OutputStream out);

}
