package gantnerj.lib;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Vector;

public class HttpServer extends TcpServer {

	public static String BASE_FOLDER = "/home/gantnerj/www/Etape 6";
	public static String INDEX_FILE = "index.html";
	public static Vector<String> VALID_EXTENSIONS = new Vector<String>();

	public HttpServer(int port) {
		super(port);
		this.init();
	}

	public HttpServer(int port, String BaseFolder){
		super(port);
		BASE_FOLDER=BaseFolder;
		this.init();
	}

	private void init(){
		if(!VALID_EXTENSIONS.contains("txt")) VALID_EXTENSIONS.add("txt");
		if(!VALID_EXTENSIONS.contains("htm")) VALID_EXTENSIONS.add("htm");
		if(!VALID_EXTENSIONS.contains("html")) VALID_EXTENSIONS.add("html");
		if(!VALID_EXTENSIONS.contains("css")) VALID_EXTENSIONS.add("css");
		if(!VALID_EXTENSIONS.contains("js")) VALID_EXTENSIONS.add("js");
		if(!VALID_EXTENSIONS.contains("jpg")) VALID_EXTENSIONS.add("jpg");
		if(!VALID_EXTENSIONS.contains("png")) VALID_EXTENSIONS.add("png");

	}

	@Override
	public void handle(String request,OutputStream out) {
		PrintWriter pw = new PrintWriter(out);
		String[]req = request.split(" ");
		if(req[0].equals("GET")){
			try {
				File requestedFile=new File(BASE_FOLDER+URLDecoder.decode(req[1].split("\\?")[0]
						,"UTF-8"));
				if(super.isDebuggable()) System.out.println("Requested file interpreted as :"+requestedFile.getAbsolutePath());
				if(requestedFile.isDirectory()){
					requestedFile=new File(requestedFile.getAbsolutePath()+'/'+INDEX_FILE);
					if(super.isDebuggable()) System.out.println("Requested File was a directory, reinterpreted as :"+requestedFile.getAbsolutePath());
				}
				String[] path=requestedFile.getAbsolutePath().split("\\.");
				String ext=path[path.length-1];
				if(VALID_EXTENSIONS.contains(ext)){
					try {
						BufferedInputStream bis =  new BufferedInputStream(new FileInputStream(requestedFile));
						String mimeType = URLConnection.getFileNameMap().getContentTypeFor(requestedFile.getAbsolutePath());
						switch(ext){
							case "css":
								mimeType="text/css";
								break;
							case "js":
								mimeType="application/javascript";
								break;
							default: break;
						}
						pw.print("HTTP/1.0 200 OK\nContent-type: "+mimeType+"\n\n");
						pw.flush();
						while(bis.available()>0) out.write(bis.read());
						out.flush();
						bis.close();
					} catch (FileNotFoundException e) {
						pw.print("HTTP/1.0 404 Not Found\nContent-type: text/html\n\n<h1>Impossible de trouver le fichier<h1>");
						pw.flush();
					} catch (IOException e) {
						pw.print("HTTP/1.0 500 Internal Server Error\nContent-type: text/html\n\n<h1>Erreur dans le traitement coté serveur<h1>");
						pw.flush();
					}
				}
				else{
					pw.print("HTTP/1.0 403 Forbidden\nContent-type: text/html\n\n<h1>Ce contenu n'est pas accessible<h1>");
					pw.flush();
				}
			} catch (UnsupportedEncodingException e1) {
				pw.print("HTTP/1.0 500 Internal Server Error\nContent-type: text/html\n\n<h1>Encodage de l'URL pas en UTF-8<h1>");
				pw.flush();
			}

		}
		else if(req[0].equals("HEAD")){
			try {
				File requestedFile=new File(BASE_FOLDER+URLDecoder.decode(req[1].split("\\?")[0]
						,"UTF-8"));
				if(super.isDebuggable()) System.out.println("Requested file interpreted as :"+requestedFile.getAbsolutePath());
				if(requestedFile.isDirectory()){
					requestedFile=new File(requestedFile.getAbsolutePath()+'/'+INDEX_FILE);
					if(super.isDebuggable()) System.out.println("Requested File was a directory, reinterpreted as :"+requestedFile.getAbsolutePath());
				}
				String[] path=requestedFile.getAbsolutePath().split("\\.");
				String ext=path[path.length-1];
				if(VALID_EXTENSIONS.contains(ext)){
					try {
						BufferedInputStream bis =  new BufferedInputStream(new FileInputStream(requestedFile));
						String mimeType = URLConnection.getFileNameMap().getContentTypeFor(requestedFile.getAbsolutePath());
						switch(ext){
							case "css":
								mimeType="text/css";
								break;
							case "js":
								mimeType="application/javascript";
								break;
							default: break;
						}
						pw.print("HTTP/1.0 200 OK\nContent-type: "+mimeType+"\n\n");
						pw.flush();
						while(bis.available()>0) bis.read();
						out.flush();
						bis.close();
					} catch (FileNotFoundException e) {
						pw.print("HTTP/1.0 404 Not Found\nContent-type: text/html\n\n<h1>Impossible de trouver le fichier<h1>");
						pw.flush();
					} catch (IOException e) {
						pw.print("HTTP/1.0 500 Internal Server Error\nContent-type: text/html\n\n<h1>Erreur dans le traitement coté serveur<h1>");
						pw.flush();
					}
				}
				else{
					pw.print("HTTP/1.0 403 Forbidden\nContent-type: text/html\n\n<h1>Ce contenu n'est pas accessible<h1>");
					pw.flush();
				}
			} catch (UnsupportedEncodingException e1) {
				pw.print("HTTP/1.0 500 Internal Server Error\nContent-type: text/html\n\n<h1>Encodage de l'URL pas en UTF-8<h1>");
				pw.flush();
			}

		}

	}

}
