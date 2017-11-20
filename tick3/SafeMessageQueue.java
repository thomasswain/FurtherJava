package tick3;

public class SafeMessageQueue<T> implements MessageQueue<T> {
	
private Link<T> first = null;
private Link<T> last = null;


public synchronized void put(T val) {
	Link<T> link = new Link<T>(val);
	
	if(first==null){
		first = link;
		last = first;
		//return;
	}

	else{
		last.next = link;
		last = last.next;
		}
	this.notify();
	}


public synchronized T take() {
while(first == null) //use a loop to block thread until data is available
try {
	this.wait();
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