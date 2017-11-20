package tick2;

import java.io.Serializable;
import java.lang.reflect.Field;

public class ReflectionTest implements Serializable {

	private static final long serialVersionUID = 1L;
	String description;
	int value;
	
	@Execute
	public void printOut(){
		System.out.println("*** Method Invocation Works ***");
	}

	ReflectionTest(String desc, int val){
		this.description = desc;
		this.value = val;
	}


	public static void main(String[] args){
		ReflectionTest rt = new ReflectionTest("thomas", 125);
		Class<?> uClass = rt.getClass();
		String uClassName = uClass.getName();
		Field[] uClassFields = uClass.getDeclaredFields();
		String output = uClassName + ": ";
		for(int i=0; i<uClassFields.length; i++){
			if(i==uClassFields.length-1){
				output += uClassFields[i].getName() + ".";
			}
			else{
				output += uClassFields[i].getName() + ", ";
			}
		}
		System.out.println(output);

	}
	
}