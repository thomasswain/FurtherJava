package tick3star;

import java.util.concurrent.atomic.AtomicReference;

public class NoLockConcurrentQueue<T> implements ConcurrentQueue<T> {

	//Link data structure - contains node val plus AtomicReference to next link.
	private static class Link<L> {
		final L val;
		final AtomicReference<Link<L>> next = new AtomicReference<>();

		Link(final L val) {
			this.val = val;
		}
	}
	
	//Create empty queue. qHead points to null, qTail points to qHead.
	//AtomicReference essential for this queue implementation.
	private AtomicReference<Link<T>> qHead = new AtomicReference<>(new Link<>(null));
	private AtomicReference<Link<T>> qTail = new AtomicReference<>(qHead.get());

	//Below offer loops until conditions are met to succeed in enqueue operation.
	//For a loop to fail, another enqueue operation must have been successful - hence non-blocking.
	@Override
	public synchronized void offer(T val) {
		//Create node representing data item offered.
		final Link<T> node = new Link<>(val);
		//Loop until successful.
		while (true) {
			//Create a temp node to hold qTail ref.
			final Link<T> tail = qTail.get();
			//Create a temp node to hold this qTail.next ref.
			final Link<T> next = tail.next.get();
			//Check that tail and next are consistent across creation.
			if (tail == qTail.get()) {
				//Check qTail next ref is still null.
				if (next == null) {
					//Point tail.next to node.
					if (tail.next.compareAndSet(next, node)) {
						//Point qTail to node (if above CAS was successful)
						qTail.compareAndSet(tail, node);
						//Offer successful.
						return;
					}
				} 
				//qTail ref was not null, point qTail to next.
				else {
					qTail.compareAndSet(tail, next);
				}
			}
		}

	}

	//Below offer loops until conditions are met to succeed in dequeue operation.
	//For a loop to fail, other enqueue/dequeue operations must have been successful - hence non-blocking.
	@Override
	public synchronized T poll() {

		while (true) {
			//Create a temp node to hold qHead ref.
			final Link<T> head = qHead.get();
			//Create a temp node to hold qTail ref.
			final Link<T> tail = qTail.get();
			//Create a temp node to hold above qHead.next ref.
			final Link<T> next = head.next.get();
			//Check that head, tail and next are consistent across creation.
			if (head == qHead.get()) {
				//Is tail behind, or queue empty?
				if (head == tail) {
					//Is queue empty?
					if (next == null) {
						//Poll failed - queue empty.
						return null;
					}
					//Tail behind, set qTail to next.
					qTail.compareAndSet(tail, next);
				} 
				//Set qHead to next - poll successful.
				else if (qHead.compareAndSet(head, next)) {
					return next.val;
				}
			}
		}
	}

}