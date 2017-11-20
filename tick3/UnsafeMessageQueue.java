package tick3;

public class UnsafeMessageQueue<T> implements MessageQueue<T> {
	
private Link<T> first = null;
private Link<T> last = null;


public void put(T val) {
	Link<T> link = new Link<T>(val);
	
	//Remember: If using commented /*if()*/ block below, return in this block is ESSENTIAL!!!!! If no return, first becomes non-null
	//and commented loop is executed for every put(). Or use els{} with the next if() block in the brackets. Careful with
	//execution blocks. Commented /*if()*/ block not required anyway.
	if(first==null){
		first = link;
		last = first;
		//return;
	}
	
	/*
	if(first!=null && first.next==null){
		first.next = link;
		last.next = first.next;
		last = last.next;
	}
	*/
	
	else{
		last.next = link;
		last = last.next;
		}
	}


public T take() {
while(first == null) //use a loop to block thread until data is available
try {
	Thread.sleep(100);
	} 
catch(InterruptedException ie) {
	
}
	T firstVal = first.val;
	first = first.next;
	return firstVal;
}

	private static class Link<L>{ 
			L val;
			Link<L> next;
		
			Link(L val) { 
				this.val = val; this.next = null; 
			}
	}

}
