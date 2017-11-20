package tick4;

public interface MessageQueue<T> {
	
	public abstract void put(T msg); 
	public abstract T take();

}