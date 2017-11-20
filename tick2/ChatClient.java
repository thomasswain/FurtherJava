package tick2;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.lang.Thread;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@FurtherJavaPreamble(
		author = "Thomas Swain",
		date = "19 August 2017",
		crsid = "ts438",
		summary = "Simple messaging chat client.",
		ticker = FurtherJavaPreamble.Ticker.A)

public class ChatClient {
	
	static DynamicObjectInputStream ois = null;
	static ObjectOutputStream oos = null;
	
	public static String timeStamp(){
		String current_time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		return current_time;
	}
	
	public static void main(String[] args) {


String server = null;
int port = 0;

try{
server = args[0];
port = Integer.parseInt(args[1]);
}
catch(ArrayIndexOutOfBoundsException ae){
	System.out.println("Please specify <SERVER> <PORT>");
}
catch(NumberFormatException e){
	System.out.println("Please specify <PORT> as an integer");
}

final Socket s;
try{
	s = new Socket(server, port); 
	
	
	System.out.println("Reached here");
}
catch(Exception e){
	System.out.println("Problem creating socket and/or IO streams.");
	return;
}

System.out.println(timeStamp() + " [CLIENT] Connected to " + server + " on port "+Integer.toString(port) +"." );

Thread output = new Thread() {
	@Override
	public void run() {

		try{
			ois = new DynamicObjectInputStream(s.getInputStream());
			while(true){
				Object rObject = ois.readObject();
				if(rObject instanceof NewMessageType){
					NewMessageType nMessageType = (NewMessageType)rObject;
					String nMessageName = nMessageType.getName();
					byte[] nMessageData = nMessageType.getClassData();
					ois.addClass(nMessageName, nMessageData);
					System.out.println(timeStamp() + " [CLIENT] New class <" + nMessageName + "> loaded.");
				}
				
				if(rObject instanceof StatusMessage){
					StatusMessage sMessage = (StatusMessage)rObject;
					String sMessageContent = sMessage.getMessage();
					System.out.println(timeStamp() + " [SERVER] " + sMessageContent);
				}
				
				if(rObject instanceof RelayMessage){
					RelayMessage rMessage = (RelayMessage)rObject;
					String rMessageFrom = rMessage.getFrom();
					String rMessageContent = rMessage.getMessage();
					System.out.println(timeStamp() + " ["+rMessageFrom+"] " + rMessageContent);
				}
				
				
				else{
					//System.out.println(timeStamp() + " [CLIENT] New message of unknown type received");
					Class<?> uClass = rObject.getClass();
					String uClassName = uClass.getSimpleName();
					
					Field[] uClassFields = uClass.getDeclaredFields();
					String output = timeStamp() + " [CLIENT] " + uClassName + ": ";
					for(int i=0; i<uClassFields.length; i++){
						uClassFields[i].setAccessible(true);
						String fieldName = uClassFields[i].getName();
						Object fieldObject = uClassFields[i].get(rObject);
						if(i==uClassFields.length-1){
							output += fieldName + "(" + fieldObject + ").";
						}
						else{
							output += fieldName + "(" + fieldObject + "), ";
						}
					}
					System.out.println(output);
					
					Method[] uClassMethods = uClass.getDeclaredMethods();
					for(int i=0; i<uClassMethods.length; i++){
						uClassMethods[i].setAccessible(true);
						if (uClassMethods[i].isAnnotationPresent(Execute.class) && 
								uClassMethods[i].getParameterTypes().length==0){
							uClassMethods[i].invoke(rObject, (Object[])null);
						}
					}
					
					
				}
				//ois.close();
			}
		}
	catch(Exception e){
		System.out.println("Problem with receiver.");
		e.printStackTrace();
	}
}
};

output.setDaemon(true);
output.start();
//System.out.println("receiver thread started");

BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
String send="";

try{
	oos = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
while(true) {

	send="";
	send = r.readLine();
	if(send.startsWith("\\")){
		if(send.startsWith("\\nick ")){
			String newNickname = send.substring(6);
			ChangeNickMessage cnMessage = new ChangeNickMessage(newNickname);
			oos.writeObject(cnMessage);
		}
		else{
			if(send.equals("\\quit")){
				System.out.println(timeStamp() + " [CLIENT] Connection terminated.");
				return;
			}
		
			else{
				System.out.println(timeStamp() + " [CLIENT] Unknown command \""+send+"\"");
			}
		}
			
	}
	else{
		ChatMessage cMessage = new ChatMessage(send);
		oos.writeObject(cMessage); 
	}
oos.flush();
}

}

catch(IOException e){
	System.out.println("IOException with client socket");
}
catch(ArrayIndexOutOfBoundsException e){
	System.out.println("Please specify <SERVER> <PORT>");
}
finally{
	try {
		s.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
}