package net.diogomarques.wifioppish.networking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a group of {@link Message Messages} ready to be
 * sent/received to/from a network. It uses Java's Serialization 
 * to ensure the compatibility with network format.
 * <p>
 * The advantage of using MessageGroup over sending several Messages 
 * is that you only need to send a single packet instead of sending 
 * multiple ones. Usually, a large number of Messages is still capable 
 * of fitting into a single UDP datagram.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 */
public class MessageGroup implements Serializable, Iterable<Message> {

	/**
	 * Generated Serial UID
	 */
	private static final long serialVersionUID = 2619813141960185538L;
	private ArrayList<Message> messages;
	
	/**
	 * Creates a new MessageGroup
	 */
	public MessageGroup() {
		messages = new ArrayList<Message>();
	}
	
	/**
	 * Adds a new Message to this MessageGroup
	 * @param m Message to add
	 * @return True if Message was added; false otherwise
	 */
	public boolean addMessage(Message m) {
		return messages.add(m);
	}
	
	/**
	 * Adds a set of Messages to this MessageGroup
	 * @param messages Message to add
	 * @return True if Messages were added; false otherwise
	 */
	public boolean addAllMessages(Collection<Message> msgs) {
		return messages.addAll(msgs);
	}
	
	/**
	 * Returns the total ammount of Message inside this MessageGroup
	 * @return total number of Messages
	 */
	public int totalMessages() {
		return messages.size();
	}

	@Override
	public Iterator<Message> iterator() {
		return messages.iterator();
	}

	@Override
	public String toString() {
		return "MessageGroup [totalMessages=" + totalMessages() + ", messages=" + messages + "]";
	}
}
