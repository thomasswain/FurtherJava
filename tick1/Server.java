package tick1;

import java.io.*;
import java.net.*;

public class Server {

	public static void main(String[] args) {
		ServerSocket startSocket;
		byte[] sendBuffer = new byte[1024];


		//while(true){
		try{
			startSocket = new ServerSocket(Integer.parseInt(args[0]));
			System.out.println("Awaiting client");
			Socket socket = startSocket.accept();
            		System.out.println("Connection with client (" + socket.getInetAddress().getHostName() + ") established " );

			Thread output = new Thread() {
	
@Override
public void run() {
//TODO: read bytes from the socket, interpret them as string data and
// print the resulting string data to the screen.
	try{
String receive = "";
while(!receive.startsWith("goodbye")){
	receive = "";
	InputStream inputStream = socket.getInputStream();
	byte[] receiveBuffer = new byte[1024];
	inputStream.read(receiveBuffer);
    receive = new String(receiveBuffer);
	System.out.println("Received From Friend  >  "+ receive + "\n");
	//System.out.println("receive is: '" + receive + "'");
	//if(receive.startsWith("goodbye")) {break;}
	
}
socket.close();
System.exit(0);
}
	catch(IOException e){
		if(!socket.isClosed()){
		System.out.println("IOException on server socket receive / display");
		}
	}
}
};

output.setDaemon(true);
output.start();

BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
String send="";
while(!send.startsWith("goodbye")) {
	send="";
	send = r.readLine();
	//System.out.println("send is: '" + send + "'");
	sendBuffer = send.getBytes();
	OutputStream outputStream = socket.getOutputStream();
	outputStream.write(sendBuffer);
}

output.interrupt();

socket.close();
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

