package tick2;

import java.io.*;
import java.net.*;

public class Server {
	
	int anonNumber = 0;


	public static void main(String[] args) {
		ServerSocket startSocket;
		byte[] sendBuffer = new byte[1024];

		try{
			startSocket = new ServerSocket(Integer.parseInt(args[0]));
			System.out.println("Awaiting client");
			Socket socket = startSocket.accept();
			System.out.println("Connection with client (" + socket.getInetAddress().getHostName() + ") established " );

			Thread output = new Thread() {
	
@Override
public void run() {
	try{
		
	ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
	
	
	
String receive = "";
while(true){
	Object rObject = ois.readObject();
	if(rObject instanceof ChatMessage){
		ChatMessage cMessage = (ChatMessage)rObject;
		String cMessageContent = cMessage.getMessage();
		System.out.println("["+socket.getInetAddress().getHostName()+"] " + cMessageContent);
	}
	
	if(ois.readObject() instanceof RelayMessage){
		RelayMessage rMessage = (RelayMessage)rObject;
		String rMessageFrom = rMessage.getFrom();
		String rMessageContent = rMessage.getMessage();
		System.out.println("["+rMessageFrom+"] " + rMessageContent);
	}
	
}
//socket.close();
//System.exit(0);
}
	catch(Exception e){
		if(!socket.isClosed()){
		System.out.println("IOException on server socket receive / display");
		}
	}
}
};

output.setDaemon(true);
output.start();

BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));

String send="";
while(true) {
	send="";
	send = r.readLine();
	
	ReflectionTest rt = new ReflectionTest(send,5);
	oos.writeObject(rt);
	oos.flush();
	//System.out.println("oos flushed");
//}

//output.interrupt();

//socket.close();
		}
	}
		catch(IOException e){
			System.out.println("IOException with server socket");
		}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Please specify server PORT");
		}
	
//}
		
	
		
	}
	

}
//}
