package tick2;

import tick2.TestMessage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

class TestMessageReadWrite {
	
static boolean writeMessage(String message, String filename) {
	System.out.println("Attempting Write");
	try{
		TestMessage testMessage = new TestMessage();
		testMessage.setMessage(message);
		FileOutputStream fos = new FileOutputStream(filename+".jobj");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(testMessage);
		oos.close();
	}
	catch (Exception e){
		System.out.println("Error during Write");
		return false;
	}
	System.out.println("Successful Write");
	return true;
}
static String readMessage(String location) {
	System.out.println("Attempting Read");
	String messageText;
	try{
		FileInputStream fis = new FileInputStream(location+".jobj");
		ObjectInputStream ois = new ObjectInputStream(fis);
		TestMessage testMessage = (TestMessage)ois.readObject();
		messageText = testMessage.getMessage();
	}
	catch (Exception e){
		System.out.println("Error during Read");
		return null;
	}
	System.out.println("Successful Read");
	return messageText;
}
public static void main(String args[]) {
	try{
	String input = args[0];
	String filename = args[1];
	boolean write = writeMessage(input, filename);
	String read = readMessage(filename);
	System.out.println("Successful Execution");
	System.out.println("Input string ('"+read+"') successfully written and read back. ");
	}
	catch(Exception e){
		System.out.println("Error during execution");
		return;
	}
}

}