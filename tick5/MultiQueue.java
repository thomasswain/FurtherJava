package tick5;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MultiQueue<T>{
	
	private Set<MessageQueue<T>> outputs;

	public synchronized void register(MessageQueue<T> q) {
		outputs.add(q);
	}
	
	public synchronized void deregister(MessageQueue<T> q) {
		outputs.remove(q);
	}

	public synchronized void put(T message) {
		Iterator<MessageQueue<T>> mIterator = outputs.iterator();
		while(mIterator.hasNext()){
			mIterator.next().put(message);
		}
	}
	
	public MultiQueue(){
		this.outputs = new HashSet<MessageQueue<T>>();
	}


}
