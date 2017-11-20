package tick5star;

public interface MessageQueue<T> {
	
	public abstract void put(T msg); 
	public abstract T take();

}