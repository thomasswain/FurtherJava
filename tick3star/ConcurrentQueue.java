package tick3star;

public interface ConcurrentQueue<T> {

	public abstract void offer(T msg); 
	public abstract T poll();
	
}
