package tick3star;

public class TwoLockConcurrentQueue<T> implements ConcurrentQueue<T> {

	//Link data structure - contains node val plus reference to next link.
	private static class Link<L> {
		L val;
		Link<L> next;

		Link(L val) {
			this.val = val;
			this.next = null;
		}
	}

	private Link<T> head = new Link<T>(null); 
	private Link<T> last = head;
	
	private Object headLock = new Object();
	private Object tailLock = new Object();
	
	@Override
	public void offer(T val) {

		final Link<T> node = new Link<>(val);
		synchronized(tailLock) {
			last.next = node;
			last = node;
		}
	}

	@Override
	public T poll() {

		final T result;
		synchronized(headLock) {
			if (head.next == null) {
				result = null;
			} else {
				result = head.next.val;
				head = head.next;
			}
		}
		return result;
	}

}
