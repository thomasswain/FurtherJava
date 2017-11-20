package tick2;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	private Date creationTime;
	
	public Message() {
		creationTime = new Date();
	}

	protected Message(Message copy) {
		creationTime = copy.creationTime;
	}

	protected Message(Date time) {
		creationTime = time;
	}
	
	public Long getCreationMillis(){
		return creationTime.getTime();
	}
	
	public String getCreationTime() {
		return new SimpleDateFormat("HH:mm:ss").format(creationTime);
		}	
	
		
	}
	
