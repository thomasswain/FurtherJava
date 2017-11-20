package tick5star;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

import tick2.Message;
import tick2.RelayMessage;
import tick5star.MultiQueue;

public class FederationHandler {

	//Analogous to ClientHandler's clientMessages - message queue for server handled by this FederationHandler.
	private MessageQueue<Message> serverMessages = new SafeMessageQueue<>();

	public FederationHandler(final Socket socket, final MultiQueue<Message> multiQueue, Database db) {

		multiQueue.register(serverMessages);

		final Thread receiver = new Thread() {
			@Override
			public void run() {
				try (final ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
					while (true) {
						final Object rObject = input.readObject();
						if (!(rObject instanceof Message)) {
							continue;
						}
						final RelayMessage rMessage = (RelayMessage) rObject;
						try{
							if(!db.checkDuplicate(rMessage)){
								multiQueue.put(rMessage);
								db.addMessage(rMessage);
							}
						}
						catch(SQLException e){
							e.printStackTrace();
						}
						
						
					}

				} 
				catch (final IOException | ClassNotFoundException e) {
					multiQueue.deregister(serverMessages);
				} 
			} 
		}; 
		receiver.start();
		
		final Thread sender = new Thread() {
			@Override
			public void run() {
				try (final ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {
					while (true) {
						final Message sMessage = serverMessages.take();
						output.writeObject(sMessage);
					} 
						
				} 
				catch (final IOException e) {
					multiQueue.deregister(serverMessages);
				} 
			} 
		}; 
		sender.start();
	}

}
