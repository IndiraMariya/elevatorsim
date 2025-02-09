package building;
import passengers.Passengers;

/**
 * 
 * @author Matthew Kim
 * 
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
		
	/**
	 * Instantiates a new call manager.
	 *
	 * @param floors the floors
	 * @param numFloors the num floors
	 * 
	 * PEER REVIEWED BY IM
	 */
	public CallManager(Floor[] floors, int numFloors) {
		this.floors = floors;
		NUM_FLOORS = numFloors;
		upCalls = new boolean[NUM_FLOORS];
		downCalls = new boolean[NUM_FLOORS];
		upCallPending = false;
		downCallPending = false;
	}
	
	/**
	 * Update call status when a specified queue changes on a floor
	 *
	 * This is an optional method that could be used to compute
	 * the values of all up and down call fields statically once per tick (to be
	 * more efficient, could only update when there has been a change to the floor queues -
	 * either passengers being added or being removed. The alternative is to dynamically
	 * recalculate the values of specific fields when needed.
	 * 
	 * PEER REVIEWED BY IM
	 */
	protected void updateCallStatus() {
		for (int i = 0; i < NUM_FLOORS; i++) {
			upCalls[i] = floors[i].hasCall(UP);
			downCalls[i] = floors[i].hasCall(DOWN);
		}
		updateCallPending(UP);
		updateCallPending(DOWN);
	}

	/**
	 * Prioritize passenger calls from STOP STATE
	 *
	 * @param floor the floor
	 * @return the passengers
	 * 
	 * PEER REVIEWED BY IM
	 */
	protected Passengers prioritizePassengerCalls(int floor) {
		Floor currentFloor = floors[floor];
		int direction;

		if (isCallOnFloor(floor)) {
			if (upCalls[floor]) {
				if (downCalls[floor]) {
					 direction = 
					 	getNumCallsInDirection(floor, UP) >= getNumCallsInDirection(floor, DOWN) 
						? UP : DOWN;
				} else direction = UP; // if only up calls
			} else direction = DOWN; // if only down calls
			
		updateCallPending(direction);
		return currentFloor.peekNextGroup(direction);
		} else {
			int numUpCalls = 0, numDownCalls = 0, lowestUpFloor = 0, highestDownFloor = 0;
			for (int i = 0; i < floors.length; i++) {
				if (numUpCalls == 0 && upCalls[i]) lowestUpFloor = i;
				if (downCalls[i]) highestDownFloor = i;
				numUpCalls += upCalls[i] ? 1 : 0;
				numDownCalls += downCalls[i] ? 1 : 0;
			}
			if (numUpCalls > numDownCalls) return floors[lowestUpFloor].peekNextGroup(UP);
			else if (numUpCalls < numDownCalls) return floors[highestDownFloor].peekNextGroup(DOWN);
			else {
				int lowestUpDistance = Math.abs(lowestUpFloor - floor);
				int highestDownDistance = Math.abs(highestDownFloor - floor);
				return lowestUpDistance > highestDownDistance 
					? floors[highestDownFloor].peekNextGroup(DOWN)
					: floors[lowestUpFloor].peekNextGroup(UP);
			}
		}
	}

	/**
	 * Checks whether there is a call in the specified direction on the given floor
	 *
	 * @param floor the floor to check
	 * @param dir the direction to check
	 * @return whether there is a call
	 * 
	 * PEER REVIEWED BY IM
	 */
	protected boolean isCallOnFloor(int floor, int dir) {
		return dir == UP ? upCalls[floor] : downCalls[floor];
	}

	/**
	 * Checks if there is a call in either direction on the given floor
	 *
	 * @param floor floor to check
	 * @return if there is a call in either direction
	 * 
	 * PEER REVIEWED BY IM
	 */
	protected boolean isCallOnFloor(int floor) {
		return isCallOnFloor(floor, UP) || isCallOnFloor(floor, DOWN);
	}

	/**
	 * Updates the relevant variable based on current state
	 *
	 * @param dir direction to update
	 * 
	 * PEER REVIEWED BY IM
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

	/**
	 * Checks if there is a call pending
	 *
	 * @return Whether there is a call pending
	 * 
	 * PEER REVIEWED BY IM
	 */
	protected boolean isCallPending() {
		return upCallPending || downCallPending;
	}
		
	/**
	 * returns if there are calls (in any direction) above / below the current floor
	 *
	 * @param floor the floor to check from
	 * @param dir the direction to check
	 * @return whether there is a call in the specified direction
	 * 
	 * PEER REVIEWED BY IM
	 */
	protected boolean isCallInDirection(int floor, int dir) {
		return getNumCallsInDirection(floor, dir) != 0;
	}

	/**
	 * returns the number of calls (in any direction) above / below the current floor
	 *
	 * @param floor the floor to check from
	 * @param dir the direction to check
	 * @return number of calls in the specified direction (does NOT include current floor)
	 * 
	 * PEER REVIEWED BY IM
	 */
	private int getNumCallsInDirection(int floor, int dir) {
		int count = 0;
		if (dir == UP) {
			for (int i = floor + 1; i < NUM_FLOORS; i++) {
				if (upCalls[i] || downCalls[i]) count ++;
			}
		}
		else if (dir == DOWN) {
			for (int i = floor - 1; i >= 0; i--) {
				if (upCalls[i] || downCalls[i]) count++;
			}
		}
		return count;
	}

	/**
	 * Checks politeness of next group
	 * Assumes there is a group on the floor -> if not, returns False
	 *
	 * @param floor floor to check
	 * @param dir direction of elevator
	 * @return whether the next group is impolite or not
	 * 
	 * PEER REVIEWED BY IM
	 */
	protected boolean isNextGroupOnFloorImpolite(int floor, int dir) {
		Passengers nextGroup = floors[floor].peekNextGroup(dir);
		if (nextGroup == null) return false;
		return !nextGroup.isPolite();
	}
}
