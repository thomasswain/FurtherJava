package tick5;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import tick2.Message;


public class ChatServer {
	
	public static void main(String args[]) throws IOException, ClassNotFoundException{
		
		int port;
		String dPath;
		ServerSocket server = null;
		Database d;
	  try{
		try{
			port = Integer.parseInt(args[0]);
			dPath = args[1];
			d = new Database(dPath);
		}
		catch(NumberFormatException nfe){
			System.out.println("Usage: java ChatServer <port> <database path>");
			return;
		}
		catch(ArrayIndexOutOfBoundsException ae){
			System.out.println("Usage: java ChatServer <port> <database path>");
			return;
		}
		catch(SQLException e){
			System.out.println("Check argument <database path> is correct");
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
				ClientHandler ch = new ClientHandler(client, mq, d);
				System.out.println("ClientHandler created");
			}
			
			catch(IOException ie){
				System.out.println("Cannot connect to client");
			}
		}
	  }
	  finally{
		  if(server!=null && !server.isClosed()){
			  server.close();
		  }
	  }
	  }
	 }  



