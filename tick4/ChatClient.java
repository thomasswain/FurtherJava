package tick4;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import tick2.ChangeNickMessage;
import tick2.ChatMessage;
import tick2.DynamicObjectInputStream;
import tick2.Execute;
import tick2.FurtherJavaPreamble;
import tick2.NewMessageType;
import tick2.RelayMessage;
import tick2.StatusMessage;

import java.lang.Thread;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@FurtherJavaPreamble(
		author = "Thomas Swain",
		date = "23 August 2017",
		crsid = "ts438",
		summary = "Simple messaging chat client.",
		ticker = FurtherJavaPreamble.Ticker.A)

public class ChatClient {
	
	public static void main(String[] args) {


		String server = null;
		int port = 0;

		try{
			server = args[0];
			port = Integer.parseInt(args[1]);
		}
		catch(ArrayIndexOutOfBoundsException ae){
			System.out.println("Usage: java ChatClient <server> <port>");
		}
		catch(NumberFormatException e){
			System.out.println("Please specify <PORT> as an integer");
		}

		final Socket socket;
		try{
			socket = new Socket(server, port); 
		}
		catch(Exception e){
			System.out.println("Problem creating socket and/or IO streams.");
			return;
		}

		System.out.println(timeStamp() + " [CLIENT] Connected to " + server + " on port "+Integer.toString(port) +"." );

		Thread receiver = new Thread() {
			@Override
			public void run() {
				DynamicObjectInputStream ois = null;
				try{
					ois = new DynamicObjectInputStream(socket.getInputStream());
					while(true){
						Object rObject = ois.readObject();
						if(rObject instanceof NewMessageType){
							NewMessageType nMessageType = (NewMessageType)rObject;
							String nMessageName = nMessageType.getName();
							byte[] nMessageData = nMessageType.getClassData();
							ois.addClass(nMessageName, nMessageData);
							System.out.println(nMessageType.getCreationTime() + " [CLIENT] New class <" + nMessageName + "> loaded.");
						}
						else if(rObject instanceof StatusMessage){
							StatusMessage sMessage = (StatusMessage)rObject;
							String sMessageContent = sMessage.getMessage();
							System.out.println(sMessage.getCreationTime() + " [SERVER] " + sMessageContent);
						}
						else if(rObject instanceof RelayMessage){
							RelayMessage rMessage = (RelayMessage)rObject;
							String rMessageFrom = rMessage.getFrom();
							String rMessageContent = rMessage.getMessage();
							System.out.println(rMessage.getCreationTime() + " ["+rMessageFrom+"] " + rMessageContent);
						}
						else{
							System.out.println(timeStamp() + " [CLIENT] New message of unknown type received");
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
					}
				}
				catch(Exception e){
					System.out.println("Problem with receiver.");
					e.printStackTrace();
					return;
				}
				finally{
					if(ois!=null){
						try{
							ois.close();
						}
						catch(IOException ie){
							ie.printStackTrace();
						}
					}
				}
			}
		};
		receiver.setDaemon(true);
		receiver.start();

		final Scanner clientIn = new Scanner(System.in) ;
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		String send="";
		try{
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			while(true) {
				send="";
				send = clientIn.nextLine();
					if(send.startsWith("\\\\nick ")){
						String newNickname = send.substring(7);
						ChangeNickMessage cnMessage = new ChangeNickMessage(newNickname);
						oos.writeObject(cnMessage);
						oos.flush();
					}
					else if(send.equals("\\\\quit")){
						System.out.println("quit condition true");
						System.out.println(timeStamp() + " [CLIENT] Connection terminated.");
						return;
					}
					else{
						ChatMessage cMessage = new ChatMessage(send);
						oos.writeObject(cMessage); 
						oos.flush();
						}
					}

			
			
					
				
			}
	
		catch(IOException e){
			System.out.println("IOException with client socket");
		}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Please specify <SERVER> <PORT>");
		}
		finally{
			try{
				socket.close();
				}
			catch(IOException ie){
				ie.printStackTrace();
			}
		}
	}
	
	public static String timeStamp(){
		String current_time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		return current_time;
	}
	
	
}