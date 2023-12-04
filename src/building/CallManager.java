package building;

import passengers.Passengers;

/**
 * The Class CallManager. This class models all of the calls on each floor,
 * and then provides methods that allow the building to determine what needs
 * to happen (ie, state transitions).
 */
public class CallManager {
	
	/** The floors. */
	private Floor[] floors;
	
	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The Constant UP. */
	private final static int UP = 1;
	
	/** The Constant DOWN. */
	private final static int DOWN = -1;
	
	/** The up calls array indicates whether or not there is a up call on each floor. */
	private boolean[] upCalls;
	
	/** The down calls array indicates whether or not there is a down call on each floor. */
	private boolean[] downCalls;
	
	/** The up call pending - true if any up calls exist */
	private boolean upCallPending;
	
	/** The down call pending - true if any down calls exit */
	private boolean downCallPending;
	
	//TODO: Add any additional fields here..
	
	/**
	 * Instantiates a new call manager.
	 *
	 * @param floors the floors
	 * @param numFloors the num floors
	 */
	public CallManager(Floor[] floors, int numFloors) {
		this.floors = floors;
		NUM_FLOORS = numFloors;
		upCalls = new boolean[NUM_FLOORS];
		downCalls = new boolean[NUM_FLOORS];
		upCallPending = false;
		downCallPending = false;
		
		//TODO: Initialize any added fields here
	}
	
	/**
	 * Update call status when a specified queue changes on a floor
	 *
	 * This is an optional method that could be used to compute
	 * the values of all up and down call fields statically once per tick (to be
	 * more efficient, could only update when there has been a change to the floor queues -
	 * either passengers being added or being removed. The alternative is to dynamically
	 * recalculate the values of specific fields when needed.
	 * @param floor the floor which the queue changed
	 * @param dir specified relevant queue
	 */
	protected void updateCallStatus(int floor, int dir) {

		//TODO: Write this method if you choose to implement it...

		if (dir == UP) {
			upCalls[floor] = floors[floor].hasCall(UP);
			updateCallPending(UP);
		}
		else {
			downCalls[floor] = floors[floor].hasCall(DOWN);
			updateCallPending(DOWN);
		}

		// TODO: consider where this should be called (only in building? in floor?)
	}

	/**
	 * Prioritize passenger calls from STOP STATE
	 *
	 * @param floor the floor
	 * @return the passengers
	 */
	protected Passengers prioritizePassengerCalls(int floor) {
		Floor currentFloor = floors[floor];

		if (isCall(floor)) {
			if (upCalls[floor]) {
				 if (downCalls[floor]) {
					if (currentFloor.getNumCalls(UP) >= currentFloor.getNumCalls(DOWN)) {
						updateCallPending(UP);
						return currentFloor.getNextGroup(UP);
					} else {
						updateCallPending(DOWN);
						return currentFloor.getNextGroup(DOWN);
					}
				 } else {
					updateCallPending(UP);
					return currentFloor.getNextGroup(UP);
				 }
			} else {
				updateCallPending(DOWN);
				return currentFloor.getNextGroup(DOWN);
			}
		} else {
			int numUpCalls = 0, numDownCalls = 0, lowestUpFloor = 0, highestDownFloor = 0;
			for (int i = 0; i < floors.length; i++) {
				if (numUpCalls == 0 && currentFloor.getNumCalls(UP) > 0) lowestUpFloor = i;
				if (currentFloor.getNumCalls(DOWN) > 0) highestDownFloor = i;
				numUpCalls += currentFloor.getNumCalls(UP);
				numDownCalls += currentFloor.getNumCalls(DOWN);
			}
			if (numUpCalls > numDownCalls) return floors[lowestUpFloor].getNextGroup(UP);
			else if (numUpCalls < numDownCalls) return floors[highestDownFloor].getNextGroup(DOWN);
			else {
				int lowestUpDistance = Math.abs(lowestUpFloor - floor);
				int highestDownDistance = Math.abs(highestDownFloor - floor);
				return lowestUpDistance > highestDownDistance 
					? floors[highestDownFloor].getNextGroup(DOWN)
					: floors[lowestUpFloor].getNextGroup(UP);
			}
		}
	}

	/**
	 * Checks whether there is a call in the specified direction on the given floor
	 *
	 * @param floor the floor to check
	 * @param dir the direction to check
	 * @return whether there is a call
	 */
	protected boolean isCall(int floor, int dir) {
		return dir == UP ? upCalls[floor] : downCalls[floor];
	}

	/**
	 * Checks if there is a call in either direction on the given floor
	 *
	 * @param floor floor to check
	 * @return if there is a call in either direction
	 */
	protected boolean isCall(int floor) {
		return isCall(floor, UP) || isCall(floor, DOWN);
	}

	//TODO: Write any additional methods here. Things that you might consider:
	//      1. pending calls - are there any? only up? only down?
	//      2. is there a call on the current floor in the current direction
	//      3. How many up calls are pending? how many down calls are pending? 
	//      4. How many calls are pending in the direction that the elevator is going
	//      5. Should the elevator change direction?
	//
	//      These are an example - you may find you don't need some of these, or you may need more...

	protected int moveToNextFloor() {
		return -1;
	}

	/**
	 * Updates the relevant variable based on current state
	 *
	 * @param dir direction to update
	 */
	private void updateCallPending(int dir) {
		if (dir == UP) {
			for (boolean hasUpCalls: upCalls) {
				if (hasUpCalls) {
					upCallPending = true;
					return;
				}
			}
			upCallPending = false;
		}
		else if (dir == DOWN) {
			for (boolean hasDownCall: downCalls) {
				if (hasDownCall) {
					downCallPending = true;
					return;
				}
			}
			downCallPending = false;
		}
	}
}
