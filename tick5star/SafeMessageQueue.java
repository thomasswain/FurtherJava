package tick5star;

public class SafeMessageQueue<T> implements MessageQueue<T> {
	
private Link<T> first = null;
private Link<T> last = null;


public synchronized void put(T val) {
	final Link<T> link = new Link<T>(val);
	
	if(first==null){
		first = link;
		last = first;
	}

	else{
		last.next = link;
		last = last.next;
		}
	this.notify();
	}


public synchronized T take() {
while(first == null)
try {
	this.wait();
	} 
catch(InterruptedException ie) {
	
}
	final T firstVal = first.val;
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