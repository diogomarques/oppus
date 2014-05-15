package net.diogomarques.wifioppish.structs;

import java.util.Collection;

import net.diogomarques.wifioppish.networking.Message;

/**
 * Implementation of {@link ConcurrentForwardingQueue} with a fixed sized. 
 * When a new {@link Message} arrives and queue already reached it's limit, 
 * the new Message is added, and the older one is discarded.
 * 
 * The default queue limit is defined by {@link #QUEUE_LIMIT} constant.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 */
public class FixedConcurrentForwardingQueue extends ConcurrentForwardingQueue {
	
	/**
	 * Queue default limit
	 */
	private final int QUEUE_LIMIT = 20;
	private int maxSize;
	
	/**
	 * Creates a fixed size queue with the default capacity (see {@link #QUEUE_LIMIT})
	 */
	public FixedConcurrentForwardingQueue() {
		super();
		maxSize = QUEUE_LIMIT;
	}
	
	/**
	 * Creates a fixed size queue with a defined capacity
	 * @param limit Total maximum number of elements stored in the queue
	 */
	public FixedConcurrentForwardingQueue(int limit) {
		super();
		maxSize = limit;
	}
	
	@Override
	public synchronized boolean add(Message e) {
		if(size() + 1 > maxSize) {
			poll();
		}
		
		return super.add(e);
	}

	@Override
	public synchronized boolean addAll(Collection<? extends Message> arg0) {
		boolean changed = false;
		for(Message m : arg0)
			changed |= this.add(m);
		
		return changed;
	}
}
