package tick1;

import java.io.*;
import java.net.*;
import java.lang.Thread;

public class Client {
	
public static void main(String[] args) {


byte[] sendBuffer = new byte[1024];

String server = null;
int port = 0;
server = args[0];
port = Integer.parseInt(args[1]);

//TODO: parse and decode server and port numbers from "args"
// if the server and port number are malformed print the same
// error messages as you did for your implementation of StringReceive
// and call return in order to halt execution of the program.
//TODO: why is "s" declared final? Explain in a comment here.
try{
final Socket s = new Socket(server, port); //TODO: connect to "server" on "port"

System.out.println("Connection with server (" + s.getInetAddress().getHostName() + ") established " );

Thread output = new Thread() {
	
@Override
public void run() {
	
//TODO: read bytes from the socket, interpret them as string data and
// print the resulting string data to the screen.
	try{
		String receive = "";
		
		while(!receive.startsWith("goodbye")){
			receive="";
			InputStream inputStream = s.getInputStream();
			byte[] receiveBuffer = new byte[1024];
			inputStream.read(receiveBuffer);
			receive = new String(receiveBuffer);
			System.out.println("Received From Friend  >  "+ receive + "\n");
			//if(receive.startsWith("goodbye")) {break;}

		}
s.close();
System.exit(0);

}
	catch(IOException e){
		System.out.println("IOException on socket receive / display");
	}
}
};

output.setDaemon(true); //TODO: Check documentation to see what this does.
output.start();

BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

String send="";

while(!send.startsWith("goodbye")) {
//TODO: read data from the user, blocking until ready. Convert the
// string data from the user into an array of bytes and write
// the array of bytes to "socket".
//
//Hint: call "r.readLine()" to read a new line of input from the user.
// this call blocks until a user has written a complete line of text
// and presses the enter key.
	send="";
	send = r.readLine();
	sendBuffer = send.getBytes();
	OutputStream outputStream = s.getOutputStream();
	outputStream.write(sendBuffer);
}

output.interrupt();
s.close();
}

catch(IOException e){
	System.out.println("IOException with client socket");
}
catch(ArrayIndexOutOfBoundsException e){
	System.out.println("Please specify server PORT");
}
}
}
