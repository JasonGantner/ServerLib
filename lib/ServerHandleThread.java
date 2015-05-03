package gantnerj.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerHandleThread extends Thread {

	private TcpServer tcpServer;
	private Socket socket;

	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String request = "";
			while(socket.isConnected()){
				do{ request += br.readLine()+"\n"; } while(br.ready());
				if(tcpServer.isDebuggable()) System.out.print(request);
				tcpServer.handle(request, socket.getOutputStream());
				request = "";
			}
			if(!socket.isClosed()) socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	 public ServerHandleThread(TcpServer tcpServer, Socket socket) {
		this.TcpServer = tcpServer;
		this.socket = socket;
	}

}
