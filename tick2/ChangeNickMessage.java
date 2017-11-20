package tick2;

import java.io.Serializable;

public class ChangeNickMessage extends Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public String name;

	public ChangeNickMessage(String name) {
		super();
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
}