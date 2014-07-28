package net.diogomarques.wifioppish.structs;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import net.diogomarques.wifioppish.networking.Message;

/**
 * Represents a Queue which supports concurrency and removes repeated messages. 
 * It is intended to use with {@link Message} instances.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 *
 */
public class ConcurrentForwardingQueue implements Queue<Message> {
	
	private LinkedList<Message> uniqueQueue;
	
	/**
	 * Creates a new ConcurrentForwardingQueue
	 */
	public ConcurrentForwardingQueue() {
		uniqueQueue = new LinkedList<Message>();
	}

	@Override
	public synchronized boolean addAll(Collection<? extends Message> arg0) {
		boolean changed = false;
		for(Message m : arg0)
			changed |= this.add(m);
		
		return changed;
	}

	@Override
	public synchronized void clear() {
		uniqueQueue.clear();
	}

	@Override
	public synchronized boolean contains(Object object) {
		return uniqueQueue.contains(object);
	}

	@Override
	public synchronized boolean containsAll(Collection<?> arg0) {
		return uniqueQueue.containsAll(arg0);
	}

	@Override
	public synchronized boolean isEmpty() {
		return uniqueQueue.isEmpty();
	}

	@Override
	public synchronized Iterator<Message> iterator() {
		return uniqueQueue.iterator();
	}

	@Override
	public synchronized boolean remove(Object object) {
		return uniqueQueue.remove(object);
	}

	@Override
	public synchronized boolean removeAll(Collection<?> arg0) {
		return uniqueQueue.removeAll(arg0);
	}

	@Override
	public synchronized boolean retainAll(Collection<?> arg0) {
		return uniqueQueue.retainAll(arg0);
	}

	@Override
	public synchronized int size() {
		return uniqueQueue.size();
	}

	@Override
	public synchronized Object[] toArray() {
		return uniqueQueue.toArray();
	}

	@Override
	public synchronized <T> T[] toArray(T[] array) {
		return uniqueQueue.toArray(array);
	}

	@Override
	public synchronized boolean add(Message e) {
		int size = uniqueQueue.size();
		uniqueQueue.addLast(e);
		return (size + 1) == uniqueQueue.size();
	}

	@Override
	public synchronized Message element() {
		return isEmpty() ? null : uniqueQueue.element();
	}

	@Override
	public synchronized boolean offer(Message e) {
		return add(e);
	}

	@Override
	public synchronized Message peek() {
		return element();
	}

	@Override
	public synchronized Message poll() {
		return isEmpty() ? null : uniqueQueue.removeFirst();
	}

	@Override
	public synchronized Message remove() {
		return isEmpty() ? null : uniqueQueue.removeFirst();
	}

}
