package passengers;

/**
 * The Class Passengers. Represents a GROUP of passengers that are 
 * traveling together from one floor to another. Tracks information that 
 * can be used to analyze Elevator performance.
 *
 * @author Sabrina Yen-Ko
 */
public class Passengers {
	
	/**  Constant for representing direction. */
	private static final int UP = 1;
	
	/** The Constant DOWN. */
	private static final int DOWN = -1;
	
	/**  ID represents the NEXT available id for the passenger group. */
	private static int ID = 0;

	/** id is the unique ID assigned to each Passenger during construction.
	 *  After assignment, static ID must be incremented.
	 */
	private int id;
	
	/** These fields will be passed into the constructor by the Building.
	 *  This data will come from the .csv file read by the SimController
	 */
	private int time;         // the time that the Passenger will call the elevator
	
	/** The num pass. */
	private int numPass;      // the number of passengers in this group
	
	/** The on floor. */
	private int onFloor;      // the floor that the Passenger will appear on
	
	/** The dest floor. */
	private int destFloor;	  // the floor that the Passenger will get off on
	
	/** The polite. */
	private boolean polite;   // will the Passenger let the doors close?
	
	/** The wait time. */
	private int waitTime;     // the amount of time that the Passenger will wait for the
	                          // Elevator
	
	/** These values will be calculated during construction.
	 */
	private int direction;      // The direction that the Passenger is going
	
	/** The time will give up. */
	private int timeWillGiveUp; // The calculated time when the Passenger will give up
	
	/** These values will actually be set during execution. Initialized to -1 */
	private int boardTime = -1;
	
	/** The time arrived. */
	private int timeArrived = -1;

	/**
	 * Instantiates a new passengers.
	 *
	 * @param time the time
	 * @param numPass the number of people in this Passenger
	 * @param on the floor that the Passenger calls the elevator from
	 * @param dest the floor that the Passenger is going to
	 * @param polite - are the passengers polite?
	 * @param waitTime the amount of time that the passenger will wait before giving up
	 * 
	 * PEER REVIEWED BY MK
	 */
	public Passengers(int time, int numPass, int on, int dest, boolean polite, int waitTime) {
	//       Remember to appropriately adjust the onFloor and destFloor to account
	//       to convert from American to European numbering...
		// set values
		this.time = time;
		this.numPass = numPass;
		this.onFloor = on - 1;
		this.destFloor = dest - 1;
		this.polite = polite;
		this.waitTime = waitTime;

		// calculated values
		this.timeWillGiveUp = time + waitTime;
		this.direction = on < dest ? UP : DOWN; // if starting floor < end floor -> UP, else DOWN

		// id
		this.id = ID;
		ID += 1;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public int getTime() {
		return this.time;
	}

	/**
	 * Gets the number of passengers.
	 *
	 * @return the num pass
	 */
	public int getNumPass() {
		return this.numPass;
	}

	/**
	 * Gets the floor the passenger appears on.
	 *
	 * @return the on floor
	 */
	public int getOnFloor() {
		return this.onFloor;
	}

	/**
	 * Gets the passenger's destination floor.
	 *
	 * @return the dest floor
	 */
	public int getDestFloor() {
		return this.destFloor;
	}

	/**
	 * Gets the wait time.
	 *
	 * @return the wait time
	 */
	public int getWaitTime() {
		return this.waitTime;
	}

	/**
	 * Gets the board time.
	 *
	 * @return the board time
	 */
	public int getBoardTime() {
		return this.boardTime;
	}

	/**
	 * Gets the time arrived.
	 *
	 * @return the time arrived
	 */
	public int getTimeArrived() {
		return this.timeArrived;
	}

	/**
	 * Gets if passenger polite
	 *
	 * @return politeness
	 */
	public boolean isPolite() {
		return polite;
	}

	/**
	 * Gets direction of passenger
	 *
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * Gets the time the passenger will give up
	 *
	 * @return the time will give up
	 */
	public int getTimeWillGiveUp() {
		return timeWillGiveUp;
	}

	/**
	 * Sets the time the passenger boarded
	 *
	 * @param boardTime the board time.
	 */
	public void setBoardTime(int boardTime) {
		this.boardTime = boardTime;
	}


	/**
	 * Sets the time the passenger arrived.
	 *
	 * @param timeArrived the time arrived.
	 */
	public void setTimeArrived(int timeArrived) {
		this.timeArrived = timeArrived;
	}

	/**
	 * Sets the politeness
	 *
	 * @param polite the politeness
	 */
	public void setPolite(boolean polite) {
		this.polite = polite;
	}

	/**
	 * Reset static ID. 
	 * This method MUST be called during the building constructor BEFORE
	 * reading the configuration files. This is to provide consistency in the
	 * Passenger ID's during JUnit testing.
	 * 
	 * PEER REVIEWED BY MK
	 */
	public static void resetStaticID() {
		ID = 0;
	}

	/**
	 * toString - returns the formatted string for this class.
	 *
	 * @return the string
	 * 
	 * PEER REVIEWED BY MK
	 */
	@Override
	public String toString() {
		return(
			"ID="+id+
			"   Time="+time+
			"   NumPass="+numPass+
			"   From="+(onFloor+1)+
			"   To="+(destFloor+1)+
			"   Polite="+polite+
			"   Wait="+waitTime
		);
	}

}
