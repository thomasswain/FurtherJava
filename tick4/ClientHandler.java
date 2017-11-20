package tick4;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

import tick2.ChangeNickMessage;
import tick2.ChatMessage;
import tick2.RelayMessage;
import tick2.Message;
import tick2.StatusMessage;

public class ClientHandler{
	
	private static Random r;
	private Socket socket;
	private MultiQueue<Message> multiQueue;
	private String nickName;
	private String previousName;
	private MessageQueue<Message> clientMessages;
		
	public ClientHandler(Socket s, MultiQueue<Message> mq) {
		socket = s;
		multiQueue = mq;
		clientMessages = new SafeMessageQueue<Message>();
		multiQueue.register(clientMessages);
		setNickname();
		sendJoinMessage();

		Thread receiver = new Thread(){
			@Override
			public void run(){
				try{
					ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
					while(true){
						Object rObject = (Object)ois.readObject();
						if(rObject instanceof ChangeNickMessage){
							ChangeNickMessage cnMessage = (ChangeNickMessage)rObject;
							previousName = nickName;
							setNickname(cnMessage.getName());
							StatusMessage sMessage = new StatusMessage(previousName + " is now known as " + nickName);
							multiQueue.put(sMessage);
						}
						else if(rObject instanceof ChatMessage){
							ChatMessage cMessage = (ChatMessage)rObject;
							RelayMessage rMessage = new RelayMessage(nickName, cMessage);
							multiQueue.put(rMessage);
						}
						else{
						}
					}
				}
				catch(IOException ie){
					sendExitMessage();
				}
				catch(ClassNotFoundException cnfe){
					cnfe.printStackTrace();
				}
			}
		};
		receiver.setDaemon(true);
		receiver.start();
		System.out.println("ClientHandler Receiver started");

		
		Thread sender = new Thread(){
			@Override
			public void run(){
				try{
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					while(true){
						Message send = clientMessages.take();
						oos.writeObject(send);
					}
				}
				catch(IOException ie){
				}
			}
		};
		sender.setDaemon(true);
		sender.start();
	}
	
	
	private void setNickname(){
		if(r==null){
			r = new Random();
		}
		int anonNumber = 10000 + r.nextInt(90000);
		nickName = "Anonymous"+Integer.toString(anonNumber);
	}
	
	
	private void setNickname(String newName){
		nickName = newName;
	}
	
	private void sendJoinMessage(){
		String hostName = socket.getInetAddress().getHostName();
		String joinMessage = nickName + " connected from " + hostName;
		StatusMessage sm = new StatusMessage(joinMessage);
		multiQueue.put(sm);
	}
	
	private void sendExitMessage(){
		String exitMessage = nickName + " has disconnected.";
		StatusMessage sm = new StatusMessage(exitMessage);
		multiQueue.put(sm);
	}
	
	}
