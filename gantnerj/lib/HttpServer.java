package gantnerj.lib;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.Integer;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Vector;

public class HttpServer extends TcpServer {

	private String BASE_FOLDER = "/var/www/";
	public String INDEX_FILE = "index.html";
	public static Vector<String> VALID_EXTENSIONS = new Vector<String>();
	private String httpVersion;

	public HttpServer(int port) {
		super(port);
	}

	public HttpServer(int port, String BaseFolder){
		super(port);
		BASE_FOLDER=BaseFolder;
	}

	@Override
	public void handle(String request,OutputStream out) {
		PrintWriter pw = new PrintWriter(out);
		String[] reqLines = request.split("\n");
		String[]req = reqLines[0].split(" ");
		String httpCommand = req[0];
		try {
			String httpPath = URLDecoder.decode(req[1].split("\\?")[0],"UTF-8");
			httpVersion = req[2];
			if(httpVersion=="HTTP/2.0")httpVersion="HTTP/1.1";
			File requestedFile=new File(BASE_FOLDER+httpPath);
			if(super.isDebuggable()) System.out.println("Requested file interpreted as :"+requestedFile.getAbsolutePath());
			if(requestedFile.isDirectory()){
				requestedFile = new File(requestedFile.getAbsolutePath()+'/'+INDEX_FILE);
				if(super.isDebuggable()) System.out.println("Requested File was a directory, reinterpreted as :"+requestedFile.getAbsolutePath());
			}
			String[] path = requestedFile.getAbsolutePath().split("\\.");
			String ext = path[path.length-1];
			//TODO httpHeaders = new Hashtable
			if(VALID_EXTENSIONS.contains(ext)){
				try {
					BufferedInputStream bis =  new BufferedInputStream(new FileInputStream(requestedFile));
					int contentLength = bis.available();
					byte content[] = new byte[contentLength];
					bis.read(content,0,contentLength);
					bis.close();
					pw.print(httpVersion+" 200 OK\nContent-type: "+generateMimeType(requestedFile)+"\nContent-Length: "+contentLength+"\n\n");
					pw.flush();
					if(httpCommand.equals("GET")){
						out.write(content);
						out.flush();
					}
					else if(httpCommand.equals("HEAD")){
					}
				} catch (FileNotFoundException e) {
					error(pw, 404, "Impossible de trouver le fichier");
					pw.flush();
				} catch (IOException e) {
					error(pw,500,"Le serveur rencontre des problèmes");
				}
			}
			else{
				error(pw, 403, "Ce contenu n'est pas accessible");
			}
		} catch (UnsupportedEncodingException e1) {
			error(pw, 500, "Encodage de l'URL pas en UTF-8");
		}
		pw.flush();
	}
	void error(PrintWriter pw, int errorCode){

	}
	void error(PrintWriter pw, int errorCode, String message){
		//TODO empty pw if data already in it
		pw.print(httpVersion);
		if(errorCode==404) pw.print("404 Not Found");
		else if(errorCode==403) pw.print("403 Forbidden");
		else if(errorCode==500) pw.print("500 Internal Server Error");
		pw.print("\nContent-type: text/html\nContent-Length: "+message.length()+"\n\n" + message);
		pw.flush();
	}

	String generateMimeType(File file){
		String[] path = file.getAbsolutePath().split("\\.");
		String ext = path[path.length-1];
		String mimeType;
		//TODO cleanup
		switch(ext){
			case "css":
			mimeType = "text/css";
			break;
			case "js":
			mimeType = "application/javascript";
			break;
			default:
			mimeType = URLConnection.getFileNameMap().getContentTypeFor(file.getAbsolutePath());
			break;
		}
		return mimeType;
	}

}
