package building;
// ListIterator can be used to look at the contents of the floor queues for 
// debug/display purposes...
import java.util.ListIterator;

import genericqueue.GenericQueue;
import passengers.Passengers;
/**
 * The Class Floor. This class provides the up/down queues to hold
 * Passengers as they wait for the Elevator.
 *
 * @author Sabrina Yen-Ko
 */
public class Floor {
	/**  Constant for representing direction. */
	private static final int UP = 1;
	private static final int DOWN = -1;

	/** The queues to represent Passengers going UP or DOWN */	
	private GenericQueue<Passengers> down;
	private GenericQueue<Passengers> up;

	/**
	 * Initializes a new floor
	 *
	 * @param qSize Size of the queue
	 */
	public Floor(int qSize) {
		down = new GenericQueue<Passengers>(qSize);
		up = new GenericQueue<Passengers>(qSize);
	}
	
	/**
	 * Has call on in specified direction
	 *
	 * @param dir determines which queue to look at
	 * @return whether there is a call in that direction
	 * 
	 * PEER REVIEWED BY MK
	 */
	protected boolean hasCall(int dir) {
		return dir == UP ? !up.isEmpty() : !down.isEmpty();
	}

	/**
	 * Gets number of calls in specified queue
	 *
	 * @param dir determines which queue to look at
	 * @return number of calls in queue
	 * 
	 * PEER REVIEWED BY MK
	 */
	protected int getNumGroups(int dir) {
		return dir == UP ? up.size() : down.size();
	}

	/**
	 * Gets next passenger group from specified queue
	 *
	 * @param dir determines which queue to look at
	 * @return Next passenger group in line
	 * 
	 * PEER REVIEWED BY MK
	 */
	protected Passengers peekNextGroup(int dir) {
		return dir == UP ? up.peek() : down.peek();
	}

	/**
	 * Removes next passenger group from specified queue
	 *
	 * @param dir determines which queue to look at
	 * @return passengers who have been removed
	 * 
	 * PEER REVIEWED BY MK
	 */
	protected Passengers removeNextGroup(int dir) {
		if (dir == UP) {
			return up.poll();
		}
		else {
			return down.poll();
		}
	}

	/**
	 * Adds a group of passengers to specified queue
	 *
	 * @param group passengers to add to queue
	 * 
	 * PEER REVIEWED BY MK
	 */
	protected void addGroup(Passengers group) {
		if (group.getDirection() == UP) {
			up.add(group);
		}
		else {
			down.add(group);
		}
	}

	/**
	 * Queue string. This method provides visibility into the queue
	 * contents as a string. What exactly you would want to visualize 
	 * is up to you
	 *
	 * @param dir determines which queue to look at
	 * @return the string of queue contents
	 * 
	 */
	String queueString(int dir) {
		String str = "";
		ListIterator<Passengers> list;
		list = (dir == UP) ?up.getListIterator() : down.getListIterator();
		if (list != null) {
			while (list.hasNext()) {
				// choose what you to add to the str here.
				// Example: str += list.next().getNumPass();
				if (list.hasNext()) str += ",";
			}
		}
		return str;	
	}
	
}
