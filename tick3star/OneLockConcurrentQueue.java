package tick3star;

public class OneLockConcurrentQueue<T> implements ConcurrentQueue<T> {
	
	private static class Link<L> {
		L val;
		Link<L> next;

		Link(L val) {
			this.val = val;
			this.next = null;
		}
	}
	
	private Link<T> first = null;
	private Link<T> last = null;

	public synchronized void offer(T val) {
		Link<T> node = new Link<T>(val);
		if(first==null){
			first = node;
			last = first;
		}

		else{
			last.next = node;
			last = last.next;
		}
		this.notify();
		}

	public synchronized T poll() {
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


}