package building;
import java.util.ArrayList;
import passengers.Passengers;

// TODO: Auto-generated Javadoc
/**
 * The Class Elevator.
 *
 * @author This class will represent an elevator, and will contain
 * configuration information (capacity, speed, etc) as well
 * as state information - such as stopped, direction, and count
 * of passengers targeting each floor...
 */
public class Elevator {
	
	/**  Elevator State Variables - These are visible publicly. */
	public final static int STOP = 0;
	
	/** The Constant MVTOFLR. */
	public final static int MVTOFLR = 1;
	
	/** The Constant OPENDR. */
	public final static int OPENDR = 2;
	
	/** The Constant OFFLD. */
	public final static int OFFLD = 3;
	
	/** The Constant BOARD. */
	public final static int BOARD = 4;
	
	/** The Constant CLOSEDR. */
	public final static int CLOSEDR = 5;
	
	/** The Constant MV1FLR. */
	public final static int MV1FLR = 6;

	/**  Door State Variables - These are visible publicly. TODO: ADD COMMENTS. */
	public final static int DRCLOSED = 0;
	public final static int DROPEN = 1;
	public static final int DRMOVING = 2;

	/** Default configuration parameters for the elevator. These should be
	 *  updated in the constructor.
	 */
	private int capacity = 15;				// The number of PEOPLE the elevator can hold
	
	/** The ticks per floor. */
	private int ticksPerFloor = 5;			// The time it takes the elevator to move between floors
	
	/** The ticks door open close. */
	private int ticksDoorOpenClose = 2;  	// The time it takes for doors to go from OPEN <=> CLOSED
	
	/** The pass per tick. */
	private int passPerTick = 3;            // The number of PEOPLE that can enter/exit the elevator per tick
	
	private int numPassengersTransitioning = 0; 	// This is set whenever passengers are offloading or boarding. 

	/**  Finite State Machine State Variables. */
	private int currState;		// current state
	
	/** The prev state. */
	private int prevState;      // prior state
	
	/** The prev floor. */
	private int prevFloor;      // prior floor
	
	/** The curr floor. */
	private int currFloor;      // current floor
	
	/** The direction. */
	private int direction;      // direction the Elevator is traveling in.

	/** The time in state. */
	private int timeInState = 1;	// represents the time in a given state
	                        		// reset on state entry, used to determine if
	                            	// state has completed or if floor has changed
	                            	// *not* used in all states 

	/** The door state. */
	private int doorState;      // used to model the state of the doors - OPEN, CLOSED
	                            // or moving. 0 = CLOSED, 1 = OPEN, 2 = MOVING
	
	/** The passengers. */
	private int passengers;  	// the number of people in the elevator
	
	/** The pass by floor. */
	private ArrayList<Passengers>[] passByFloor;  // Passengers to exit on the corresponding floor

	/** The move to floor. */
	private int moveToFloor;	// When exiting the STOP state, this is the floor to move to without
	                            // stopping.
	
	/** The post move to floor dir. */
	private int postMoveToFloorDir; // This is the direction that the elevator will travel AFTER reaching
	                                // the moveToFloor in MVTOFLR state.

	/**
	 * Instantiates a new elevator.
	 *
	 * @param numFloors the num floors
	 * @param capacity the capacity
	 * @param floorTicks the floor ticks
	 * @param doorTicks the door ticks
	 * @param passPerTick the pass per tick
	 */
	@SuppressWarnings("unchecked")
	public Elevator(int numFloors, int capacity, int floorTicks, int doorTicks, int passPerTick) {		
		this.prevState = STOP;
		this.currState = STOP;
		this.timeInState = 0;
		this.currFloor = 0;
		passByFloor = new ArrayList[numFloors];
		
		for (int i = 0; i < numFloors; i++) 
			passByFloor[i] = new ArrayList<Passengers>(); 

		//TODO: Finish this constructor, adding configuration initialiation and
		//      initialization of any other private fields, etc.
		this.capacity = capacity;
		this.ticksPerFloor = floorTicks;
		this.ticksDoorOpenClose = doorTicks;
		this.passPerTick = passPerTick;
	}
	
	//TODO: Add Getter/Setters and any methods that you deem are required. Examples 
	//      include:
	//      1) moving the elevator
	//      2) closing the doors
	//      3) opening the doors
	//      and so on...

	/**
	 * moves the elevator to the specified floor
	 *
	 * @param floor
	 */
	protected void moveElevator(int floor) {
		do {
			moveElevator();
		} while (currFloor < floor);
	}

	/**
	 * moves the elevator to the specified floor
	 */
	protected void moveElevator() {
		prevFloor = currFloor;
		if ((timeInState % ticksPerFloor) == 0) {
			currFloor = currFloor + direction;
		}
	}

	/**
	 * closes the elevator door
	 */
	protected void closeDoor() {
		if (timeInState >= ticksDoorOpenClose) {
			this.doorState = DRCLOSED;
		}
	}

	/**
	 * opens the elevator door
	 */
	protected void openDoor() {
		if (timeInState >= ticksDoorOpenClose) {
			this.doorState = DROPEN;
		}
	}

	/**
	 * returns all passengers on current floor to be unloaded
	 */
	protected ArrayList<Passengers> unloadPassengers() {
		numPassengersTransitioning += passByFloor[currFloor].size();
		ArrayList<Passengers> passengersToUnload = new ArrayList<Passengers>();
		for (int i = 0; i < passByFloor[currFloor].size(); i++) {
			passengersToUnload.add(passByFloor[currFloor].get(i));
		}
		passByFloor[currFloor].clear(); 
		return passengersToUnload;
	}

	/**
	 * loads 1 passenger group
	 *
	 * @param group group to add to elevator
	 */
	protected void loadPassenger(Passengers group) {
		numPassengersTransitioning += group.getNumPass();
		if (getNumPassengers() + group.getNumPass() > 15) return;
		passByFloor[group.getDestFloor()].add(group);
	}

	/**
	 * Update curr state.
	 *
	 * @param nextState the next state
	 */
	void updateCurrState(int nextState) {
		this.prevState = this.currState;
		this.currState = nextState;
		if (this.prevState != this.currState) {
			timeInState = 1;
			numPassengersTransitioning = 0;
		}
		else timeInState++;
	}

	public boolean isDoorOpen() {
		if (doorState == DROPEN) return true;
		else return false;
	}
	
	// returns whether the door is between opening and closing
	protected boolean isDoorTransitioning() {
		if (doorState == DRMOVING) return true;
		else return false;
	}

	// returns whether there are passengers that need to exit on the specified floor
	protected boolean passengersToExit(int floor) {
		if (passByFloor[floor].size() > 0) return true;
		else return false;
	}

	// returns true if the elevator is still in the process of offloading or boarding (delay has not passed)
	protected boolean isTransitioning() {
		numPassengersTransitioning -= passPerTick;
		return numPassengersTransitioning > 0;
	}

	// returns true if the elevator is NOT transitioning between floors
	protected boolean atFloor() {
		if (currState == MV1FLR || currState == MVTOFLR) return false;
		return true;
	}

	// helper method to switch direction from up to down or vice versa
	protected void switchDirection() {
		direction *= -1;
	}
		
	/**
	 * Gets number of passengers in elevator
	 *
	 * @return number of passengers in the elevator
	 */
	public int getNumPassengers() {
		// TODO: consider if you want to keep a running count of passengers (as a field) and only update when necessary
		int count = 0;
		for (ArrayList<Passengers> groupByFloor : passByFloor) {
			for (Passengers group: groupByFloor) {
				count += group.getNumPass();
			}
		}
		return count;
	}

	/**
	 * Gets the capacity.
	 *
	 * @return the capacity
	 */
	int getCapacity() {
		return this.capacity;
	}

	/**
	 * Gets the ticks per floor.
	 *
	 * @return the ticks per floor
	 */
	int getTicksPerFloor() {
		return this.ticksPerFloor;
	}

	/**
	 * Gets the ticks door open close.
	 *
	 * @return the ticks door open close
	 */
	int getTicksDoorOpenClose() {
		return this.ticksDoorOpenClose;
	}

	/**
	 * Gets the pass per tick.
	 *
	 * @return the pass per tick
	 */
	int getPassPerTick() {
		return this.passPerTick;
	}

	/**
	 * Gets the curr state.
	 *
	 * @return the curr state
	 */
	int getCurrState() {
		return this.currState;
	}

	/**
	 * Gets the prev state.
	 *
	 * @return the prev state
	 */
	int getPrevState() {
		return this.prevState;
	}

	/**
	 * Gets the prev floor.
	 *
	 * @return the prev floor
	 */
	int getPrevFloor() {
		return this.prevFloor;
	}

	/**
	 * Gets the curr floor.
	 *
	 * @return the curr floor
	 */
	int getCurrFloor() {
		return this.currFloor;
	}

	/**
	 * Gets the direction
	 *
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}

	public boolean arePassengersExitingOnFloor(int floor) {
		if (passByFloor[floor].size() > 0) return true;
		else return false;
	}

	/**
	 * Gets moveToFloor
	 *
	 * @return moveToFloor
	 */
	public int getMoveToFloor() {
		return moveToFloor;
	}

	/**
	 * Sets moveToFloor
	 *
	 * @param moveToFloor floor to move to
	 */
	public void setMoveToFloor(int moveToFloor) {
		this.moveToFloor = moveToFloor;
	}

	/**
	 * Sets direction
	 *
	 * @param direction sets direction
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * Gets direction after reaching moveToFloor
	 *
	 * @return postMoveToFloorDir
	 */
	public int getPostMoveToFloorDir() {
		return postMoveToFloorDir;
	}

	/**
	 * Sets postMoveToFloor direction
	 *
	 * @param postMoveToFloorDir postMoveToFloor direction
	 */
	public void setPostMoveToFloorDir(int postMoveToFloorDir) {
		this.postMoveToFloorDir = postMoveToFloorDir;
	}
}
