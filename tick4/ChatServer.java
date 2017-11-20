package tick4;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import tick2.Message;


public class ChatServer {
	
	public static void main(String args[]) throws IOException{
		
		int port;
		ServerSocket server;
		
		try{
			port = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException nfe){
			System.out.println("Usage: java ChatServer <port>");
			return;
		}
		catch(ArrayIndexOutOfBoundsException ae){
			System.out.println("Usage: java ChatServer <port>");
			return;
		}
		
		try{
			server = new ServerSocket(port);
			System.out.println("ServerSocket created");
		}
		catch(IOException ie){
			System.out.println("Cannot use port number <" + port + ">");
			return;
		}
		
		MultiQueue<Message> mq = new MultiQueue<Message>();
		System.out.println("MultiQueue created");

		
		while(true){
			try{
				Socket client = server.accept();
				System.out.println("Client Socket created");

				ClientHandler ch = new ClientHandler(client, mq);
				System.out.println("ClientHandler created");

			}
			catch(IOException ie){
				System.out.println("Cannot connect to client");
			}
		}	
	}
				
}


