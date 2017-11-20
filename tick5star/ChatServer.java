package tick5star;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import tick2.Message;
import tick5star.MultiQueue;


public class ChatServer {
	
	public static void main(String args[]) throws IOException, ClassNotFoundException{
		
		final String usage = "Usage: ChatServer <dbpath> <client> <fed> [fedsrv1:port fedsrv2:port ...]";
		final String dbMessage = "Check argument <database path> is correct";

		
		final int clientPort;
		final int federationPort;
		final String dPath;
		Database d;
	  
		  
		//Use first 3 command-line arguments to set up server client database, client port and federation port.
		try{
			dPath = args[0];
			clientPort = Integer.parseInt(args[1]);
			federationPort = Integer.parseInt(args[2]);
			d = new Database(dPath);
		}
		catch(NumberFormatException nfe){
			System.out.println(usage);
			return;
		}
		catch(ArrayIndexOutOfBoundsException ae){
			System.out.println(usage);
			return;
		}
		catch(SQLException e){
			System.out.println(dbMessage);
			return;
		}
		System.out.println("Client port: "+clientPort);
		System.out.println("Federation port: "+federationPort);

		//ServerSocket for use in thread to listen and connect to client connection requests.
		final ServerSocket clientSocket;
		try{
			clientSocket = new ServerSocket(clientPort);
		}
		catch(IOException ie){
			System.out.println("Cannot use port number <" + clientPort + ">");
			return;
		}
		
		//ServerSocket for use in thread to listen and connect to federation connection requests.
		final ServerSocket federationSocket;
		try{
			federationSocket = new ServerSocket(federationPort);
		}
		catch(IOException ie){
			System.out.println("Cannot use port number <" + federationPort + ">");
			return;
		}
		
		final MultiQueue<Message> mq = new MultiQueue<Message>();
		
		final String interpretWarning = "Warning: cannot interpret '%s' as 'fedsrv:port'. Ignoring.";
		final String cannotConnect = "Cannot connect to: ";
		for (int i = 3; i < args.length; i++) {
			try {
				final String[] fedServer = args[i].split(":");
				if (fedServer.length != 2) {
					System.err.printf(interpretWarning, args[i]);
					continue;
				}
				//Server acts as a client and requests connection to fedsrv:port
				new FederationHandler(new Socket(fedServer[0], Integer.parseInt(fedServer[1])), mq, d);
				System.out.println("This server is client to server: "+args[i]);

			} 
			catch (final IOException ioe) {
				System.err.println(cannotConnect + "to federated server: " + args[i]);
			} 
			catch (final NumberFormatException nfe) {
				System.err.printf(interpretWarning, args[i]);
			}
		}
		
		//Thread listens for other server federation requests on separate thread.
		Thread fedRequestListener = new Thread(){
			@Override
			public void run(){
				try {
					while (true) {
						new FederationHandler(federationSocket.accept(), mq, d);
						System.out.println("serving a new server");
					}
				} catch (final IOException ioe) {

					System.err.println("[ERROR] External server socket.");
					ioe.printStackTrace();
					
		}
			}
		};
		fedRequestListener.setDaemon(true);
		fedRequestListener.start();
				
		try{
			//ChatServer can listen on the main thread for client connections now that FederationListener has started.
			while(true){
				try{
					Socket client = clientSocket.accept();
					new ClientHandler(client, mq, d);
				}
				
				catch(IOException ie){
					System.out.println(cannotConnect + "to chat client: " + clientPort);
				}
			}
		}
		finally{
			if(clientSocket!=null && !clientSocket.isClosed()){
			  clientSocket.close();
			}
			if(federationSocket!=null && !federationSocket.isClosed()){
			  federationSocket.close();
			}
	  	}
	}
}  



