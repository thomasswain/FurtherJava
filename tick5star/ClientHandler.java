package tick5star;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedList;
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
	private Database database;
		
	public ClientHandler(final Socket s, final MultiQueue<Message> mq, Database d) {
		socket = s;
		this.multiQueue = mq;
		database = d;
		clientMessages = new SafeMessageQueue<Message>();
		//Add 10 recent messages to clientMessages before registering, so that no messages sent around time of join 
		//are sent to client via MultiQueue first.
		try{
			LinkedList<RelayMessage> recentList = database.getRecent();
			while(!recentList.isEmpty()){
				clientMessages.put(recentList.removeLast());
			}
			database.incrementLogins();
		}
		catch(SQLException e){
			System.out.println("Client SQLException - Unable to display recent messages");
		}
		multiQueue.register(clientMessages);
		setNickname();
		sendJoinMessage();

		Thread receiver = new Thread(){
			@Override
			public void run(){
				try{
					final ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
					while(true){
						Object rObject = (Object)ois.readObject();
						if(rObject instanceof ChangeNickMessage){
							ChangeNickMessage cnMessage = (ChangeNickMessage)rObject;
							previousName = nickName;
							setNickname(cnMessage.getName());
							final StatusMessage sMessage = new StatusMessage(previousName + " is now known as " + nickName);
							sendRelayMessage(new RelayMessage(sMessage));
						}
						else if(rObject instanceof ChatMessage){
							ChatMessage cMessage = (ChatMessage)rObject;
							final RelayMessage rMessage = new RelayMessage(nickName, cMessage);
							sendRelayMessage(rMessage);
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
					final ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					while(true){
						Message send = clientMessages.take();
						oos.writeObject(send);
						oos.flush();
					}
				}
				catch(IOException ie){
					sendExitMessage();
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
		final StatusMessage sm = new StatusMessage(joinMessage);
		sendRelayMessage(new RelayMessage(sm));
	}
	
	private void sendExitMessage(){
		multiQueue.deregister(clientMessages);
		String exitMessage = nickName + " has disconnected.";
		StatusMessage sm = new StatusMessage(exitMessage);
		sendRelayMessage(new RelayMessage(sm));
	}
	
	private void sendRelayMessage(RelayMessage r){
		multiQueue.put(r);
		try {
			database.addMessage(r);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	}
