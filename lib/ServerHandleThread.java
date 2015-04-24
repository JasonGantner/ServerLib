package gantnerj.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerHandleThread extends Thread {

	private Server server;
	private Socket socket;
	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String request="";
			do
			{
				request += br.readLine()+"\n";
			}
			while(br.ready());
			if(server.isDebuggable())System.out.print(request);
			server.handle(request, socket.getOutputStream());
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	 public ServerHandleThread(Server server, Socket socket) {
		this.server=server;
		this.socket=socket;
	}

}
