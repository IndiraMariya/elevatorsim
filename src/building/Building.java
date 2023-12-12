package building;

import myfileio.MyFileIO;
import passengers.Passengers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The Class Building.
 */
public class Building {

	/**  Constants for direction. */
	private final static int UP = 1;

	/** The Constant DOWN. */
	private final static int DOWN = -1;

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(Building.class.getName());

	/**  The fh - used by LOGGER to write the log messages to a file. */
	private FileHandler fh;

	/**  The fio for writing necessary files for data analysis. */
	private MyFileIO fio;

	/**  File that will receive the information for data analysis. */
	private File passDataFile;

	/**  passSuccess holds all Passengers who arrived at their destination floor. */
	private ArrayList<Passengers> passSuccess;

	/**  gaveUp holds all Passengers who gave up and did not use the elevator. */
	private ArrayList<Passengers> gaveUp;

	/**  The number of floors - must be initialized in constructor. */
	private final int NUM_FLOORS;

	/**  The size of the up/down queues on each floor. */
	private final int FLOOR_QSIZE = 10;

	/** The floors. */
	public Floor[] floors;

	/** The elevator. */
	private Elevator elevator;

	/**  The Call Manager - it tracks calls for the elevator, 
	 * analyzes them to answer questions and prioritize calls. */
	private CallManager callMgr;

	/** The ID of the last passenger group skipped (for BOARD state)  */
	private int lastSkippedID = -1;

	/**
	 * Instantiates a new building.
	 *
	 * @param numFloors the num floors
	 * @param logfile the logfile
	 * 
	 * PEER REVIEWED BY MK
	 */
	public Building(int numFloors, String logfile) {
		NUM_FLOORS = numFloors;
		passSuccess = new ArrayList<Passengers>();
		gaveUp = new ArrayList<Passengers>();
		initializeBuildingLogger(logfile);
		// passDataFile is where you will write all the results for those passengers who successfully
		// arrived at their destination and those who gave up...
		fio = new MyFileIO();
		passDataFile = fio.getFileHandle(logfile.replaceAll(".log","PassData.csv"));

		// create the floors, call manager and the elevator arrays
		// note that YOU will need to create and config each specific elevator...
		floors = new Floor[NUM_FLOORS];
		for (int i = 0; i < NUM_FLOORS; i++) {
			floors[i]= new Floor(FLOOR_QSIZE);
		}
		callMgr = new CallManager(floors,NUM_FLOORS);
	}

	/**
	 * Initialized the elevator based on the parameters passed in
	 *
	 * @param capacity the capacity
	 * @param floorTicks the floor ticks
	 * @param doorTicks the door ticks
	 * @param passPerTick the passengers per tick
	 * 
	 * PEER REVIEWED BY MK
	 */
	public void initializeElevator(int capacity, int floorTicks, int doorTicks, int passPerTick) {
		elevator = new Elevator(NUM_FLOORS, capacity, floorTicks, doorTicks, passPerTick);
	}

	/**
	 * Adds a group of passengers to the specified floor
	 *
	 * @param group group to be added to the floor
	 * 
	 * PEER REVIEWED BY MK
	 */
	public void addPassengers(Passengers group) {
		floors[group.getOnFloor()].addGroup(group);
		logCalls(group.getTime(), group.getNumPass(), group.getOnFloor(), group.getDirection(), group.getId());
	}

	/**
	 * Called when all passengers have been added -> updates call manager
	 * 
	 * PEER REVIEWED BY MK
	 */
	public void onAllPassengersAdded() {
		callMgr.updateCallStatus();
	}

	/**
	 * Returns whether simulation has ended (based on elevator state)
	 *
	 * @param time current time
	 * @return whether the simulation ended
	 * 
	 * PEER REVIEWED BY MK
	 */
	public boolean hasSimulationEnded(int time) {
		return elevator.getCurrState() == Elevator.STOP && !callMgr.isCallPending();
	}

	/**
	 * Gets the elevator state
	 *
	 * @return the elevator state
	 * 
	 * PEER REVIEWED BY MK
	 */
	public int getElevatorState() {
		return elevator.getCurrState();
	};

	/**
	 * gets the elevator direction
	 *
	 * @return the elevator direction
	 * 
	 * PEER REVIEWED BY MK
	 */
	public int getElevatorDirection() {
		return elevator.getDirection();
	};

	/**
	 * gets the elevator's current floor
	 *
	 * @return the elevator floor
	 */
	public int getElevatorFloor() {
		return elevator.getCurrFloor();
	};

	/**
	 * gets the number of passengers in the elevator
	 *
	 * @return the number of passengers in the elevator
	 * 
	 * PEER REVIEWED BY MK
	 */
	public int getElevatorPassengerCount() {
		return elevator.getNumPassengers();
	};

	/**
	 * Returns the number of passenger groups on a floor and a specific direction
	 *
	 * @param floor floor to look at
	 * @param dir direction to look at
	 * @return Number of passenger groups
	 * 
	 * PEER REVIEWED BY MK
	 */
	public int getNumPassengerGroupsOnFloor(int floor, int dir) {
		return floors[floor].getNumGroups(dir);
	}

	// DO NOT CHANGE ANYTHING BELOW THIS LINE:
	/**
	 * Initialize building logger. Sets formating, file to log to, and
	 * turns the logger OFF by default
	 *
	 * @param logfile the file to log information to
	 */
	void initializeBuildingLogger(String logfile) {
		System.setProperty("java.util.logging.SimpleFormatter.format","%4$-7s %5$s%n");
		LOGGER.setLevel(Level.OFF);
		try {
			fh = new FileHandler(logfile);
			LOGGER.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles STOP state
	 *
	 * @param time time when state is called
	 * @return next state
	 * 
	 * PEER REVIEWED BY MK
	 */
	private int currStateStop(int time) {
		// is a call on the current floor
		if (callMgr.isCallOnFloor(elevator.getCurrFloor())) {
			int dir = callMgr.prioritizePassengerCalls(elevator.getCurrFloor()).getDirection();
			elevator.setDirection(dir);
			return Elevator.OPENDR;
		}
		// calls on other floors
		else if (callMgr.isCallPending()) {
			Passengers nextGroup = callMgr.prioritizePassengerCalls(elevator.getCurrFloor());
			int nextFloor = nextGroup.getOnFloor();
			elevator.setMoveToFloor(nextFloor);
			elevator.setPostMoveToFloorDir(nextGroup.getDirection());
			elevator.setDirection(getDirectionToFloor(nextFloor));
			return Elevator.MVTOFLR;
		}
		// no calls in any direction on any floor
		return Elevator.STOP;
	}

	/**
	 * Handles MVTOFLR state
	 *
	 * @param time time when state is called
	 * @return next state
	 * 
	 * PEER REVIEWED BY MK
	 */
	private int currStateMvToFlr(int time) {
		elevator.moveElevator();

		// reached target floor
		if (elevator.getCurrFloor() == elevator.getMoveToFloor()) {
			elevator.setDirection(elevator.getPostMoveToFloorDir());
			return Elevator.OPENDR;
		}
		// has not reached target floor
		else {
			return Elevator.MVTOFLR;
		}
	}

	/**
	 * Handles OPENDR state
	 *
	 * @param time time when state is called
	 * @return next state
	 * 
	 * PEER REVIEWED BY MK
	 */
	private int currStateOpenDr(int time) {
		elevator.openDoor();

		if (elevator.isDoorTransitioning() || elevator.isDoorOpen()) {
			if (elevator.passengersToExit(elevator.getCurrFloor())) return Elevator.OFFLD;
			else return Elevator.BOARD;
		} else {
			return Elevator.OPENDR;
		}
	}

	/**
	 * Handles OFFLD state
	 *
	 * @param time time when state is called
	 * @return next state
	 * 
	 * PEER REVIEWED BY MK
	 */
	private int currStateOffLd(int time) {
		if (elevator.getCurrState() != elevator.getPrevState()) {
			ArrayList<Passengers> passengersToUnload = elevator.unloadPassengers();

			for (Passengers passengers : passengersToUnload) {
				passengers.setTimeArrived(time);
				logArrival(time, passengers.getNumPass(), elevator.getCurrFloor(), passengers.getId());
			}
			passSuccess.addAll(passengersToUnload);

			callMgr.updateCallStatus();
		}

		if (elevator.isTransitioning()) {
			return Elevator.OFFLD;
		}
		// no longer offloading
		else {
			// passengers to board in current direction
			if (callMgr.isCallOnFloor(elevator.getCurrFloor(), elevator.getDirection())) {
				return Elevator.BOARD;
			}
			else if (
					elevator.getNumPassengers() == 0 &&
					!callMgr.isCallInDirection(elevator.getCurrFloor(), elevator.getDirection()) &&
					callMgr.isCallOnFloor(elevator.getCurrFloor())
			) {
				elevator.switchDirection();
				return Elevator.BOARD;
			}
			else {
				return Elevator.CLOSEDR;
			}
		}
	}

	/**
	 * Handles BOARD state
	 *
	 * @param time time when state is called
	 * @return next state
	 * TODO: LINE LENGTH!!
	 * 
	 * PEER REVIEWED BY MK
	 */
	private int currStateBoard(int time) {
		if (elevator.getPrevState() != elevator.getCurrState()) lastSkippedID = -1;

		Floor currentFloor = floors[elevator.getCurrFloor()];
		int dir = elevator.getDirection();
		Passengers nextGroup = currentFloor.peekNextGroup(dir);

		while (nextGroup != null) {
			// passengers have given up
			if (time > nextGroup.getTimeWillGiveUp()) {
				logGiveUp(time, nextGroup.getNumPass(), elevator.getCurrFloor(), dir, nextGroup.getId());

				if (lastSkippedID == nextGroup.getId()) lastSkippedID = -1; // RESET if skipped group gives up

				gaveUp.add(nextGroup);
				currentFloor.removeNextGroup(dir);
			}
			// not enough room
			else if (elevator.getCapacity() - elevator.getNumPassengers() < nextGroup.getNumPass()) {
				if (lastSkippedID != nextGroup.getId()) {
					logSkip(time, nextGroup.getNumPass(), elevator.getCurrFloor(), dir, nextGroup.getId());
					lastSkippedID = nextGroup.getId();
				}
				// mark skipped group as polite if necessary
				if (!nextGroup.isPolite()) nextGroup.setPolite(true);
				break;
			}
			// board the passenger
			else {
				nextGroup.setBoardTime(time);
				logBoard(time, nextGroup.getNumPass(), elevator.getCurrFloor(), dir, nextGroup.getId());
				currentFloor.removeNextGroup(dir);
				elevator.loadPassenger(nextGroup);
			}
			nextGroup = currentFloor.peekNextGroup(elevator.getDirection());
		}

		callMgr.updateCallStatus();

		// still boarding
		return (elevator.isTransitioning()) ? Elevator.BOARD : Elevator.CLOSEDR;
	}

	/**
	 * Handles CLOSEDR state
	 *
	 * @param time time when state is called
	 * @return next state
	 * TODO: LINE LENGTH@!!
	 */
	private int currStateCloseDr(int time) {
		elevator.closeDoor();

		int curFloor = elevator.getCurrFloor();
		int dir = elevator.getDirection();
		if (callMgr.isCallOnFloor(curFloor, dir) && callMgr.isNextGroupOnFloorImpolite(curFloor, dir)) {
			// TODO: check if action needs to be taken
			return Elevator.OPENDR;
		}

		// door is still open
		if (elevator.isDoorOpen()) {
			return Elevator.CLOSEDR;
		}
		// door is closed, elevator is empty
		else if (elevator.getNumPassengers() == 0) {
			// no calls -> STOP
			if (!callMgr.isCallPending()) {
				return Elevator.STOP;
			}
			// calls not on this floor, in the same direction
			else if (callMgr.isCallInDirection(elevator.getCurrFloor(), elevator.getDirection())) {
				return Elevator.MV1FLR;
			}
			// Calls on this floor in the same direction
			else if (callMgr.isCallOnFloor(elevator.getCurrFloor(), elevator.getDirection())) {
				return Elevator.OPENDR;
			}
			else {
				elevator.switchDirection();

				// if call on current floor
				return (callMgr.isCallOnFloor(elevator.getCurrFloor())) ? Elevator.OPENDR : Elevator.MV1FLR;
			}
		}
		// people in elevator who need to get off
		else {
			return Elevator.MV1FLR;
		}
	}

	/**
	 * Handles MV1FLR state
	 *
	 * @param time time when state is called
	 * @return next state
	 * 
	 * PEER REVIEWED BY MK
	 */
	private int currStateMv1Flr(int time) {
		elevator.moveElevator();

		if (elevator.atFloor()) {
			if (
					elevator.passengersToExit(elevator.getCurrFloor()) ||
					callMgr.isCallOnFloor(elevator.getCurrFloor(), elevator.getDirection())
			) {
				return Elevator.OPENDR;
			}
			else if (
					elevator.getNumPassengers() == 0 &&
					!callMgr.isCallInDirection(elevator.getCurrFloor(), elevator.getDirection()) &&
					callMgr.isCallOnFloor(elevator.getCurrFloor())
			) {
				elevator.switchDirection();
				return Elevator.OPENDR;
			}
		}
		return Elevator.MV1FLR;
	}

	/**
	 * Checks whether the elevator state or floor changed
	 * @return whether the elevator state or floor changed
	 * 
	 * PEER REVIEWED BY MK
	 */
	private boolean elevatorStateOrFloorChanged() {
		return elevator.getPrevState() != elevator.getCurrState() || elevator.getPrevFloor() != elevator.getCurrFloor();
	}

	/**
	 * Gets the direction to move to the floor from the current floor
	 *
	 * @param floor the floor to move to (different than current floor)
	 * @return direction to move based on the floor
	 * 
	 * PEER REVIEWED BY MK
	 */
	private int getDirectionToFloor(int floor) {
		return floor > elevator.getCurrFloor() ? UP : DOWN;
	}

	/**
	 * Update elevator - this is called AFTER time has been incremented.
	 * -  Logs any state changes, if the have occurred,
	 * -  Calls appropriate method based upon currState to perform
	 *    any actions and calculate next state...
	 *
	 * @param time the time
	 */
	public void updateElevator(int time) {
		if (elevatorStateOrFloorChanged())
			logElevatorStateOrFloorChanged(time,elevator.getPrevState(),elevator.getCurrState(),
					                       elevator.getPrevFloor(),elevator.getCurrFloor());

		switch (elevator.getCurrState()) {
		case Elevator.STOP: elevator.updateCurrState(currStateStop(time)); break;
		case Elevator.MVTOFLR: elevator.updateCurrState(currStateMvToFlr(time)); break;
		case Elevator.OPENDR: elevator.updateCurrState(currStateOpenDr(time)); break;
		case Elevator.OFFLD: elevator.updateCurrState(currStateOffLd(time)); break;
		case Elevator.BOARD: elevator.updateCurrState(currStateBoard(time)); break;
		case Elevator.CLOSEDR: elevator.updateCurrState(currStateCloseDr(time)); break;
		case Elevator.MV1FLR: elevator.updateCurrState(currStateMv1Flr(time)); break;
		}

	}

	/**
	 * Process passenger data. Do NOT change this - it simply dumps the
	 * collected passenger data for successful arrivals and give ups. These are
	 * assumed to be ArrayLists...
	 */
	public void processPassengerData() {

		try {
			BufferedWriter out = fio.openBufferedWriter(passDataFile);
			out.write("ID,Number,From,To,WaitToBoard,TotalTime\n");
			for (Passengers p : passSuccess) {
				String str = p.getId()+","+p.getNumPass()+","+(p.getOnFloor()+1)+","+(p.getDestFloor()+1)+","+
				             (p.getBoardTime() - p.getTime())+","+(p.getTimeArrived() - p.getTime())+"\n";
				out.write(str);
			}
			for (Passengers p : gaveUp) {
				String str = p.getId()+","+p.getNumPass()+","+(p.getOnFloor()+1)+","+(p.getDestFloor()+1)+","+
				             p.getWaitTime()+",-1\n";
				out.write(str);
			}
			fio.closeFile(out);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enable logging. Prints the initial configuration message.
	 * For testing, logging must be enabled BEFORE the run starts.
	 * 
	 * PEER REVIEWED BY MK
	 */
	public void enableLogging() {
		LOGGER.setLevel(Level.INFO);
		logElevatorConfig(elevator.getCapacity(),elevator.getTicksPerFloor(), elevator.getTicksDoorOpenClose(),
					          elevator.getPassPerTick(), elevator.getCurrState(),elevator.getCurrFloor());

	}

	/**
	 * Close logs, and pause the timeline in the GUI.
	 *
	 * @param time the time
	 */
	public void closeLogs(int time) {
		if (LOGGER.getLevel() == Level.INFO) {
			logEndSimulation(time);
			fh.flush();
			fh.close();
		}
	}

	/**
	 * Prints the state.
	 *
	 * @param state the state
	 * @return the string
	 */
	private String printState(int state) {
		String str = "";

		switch (state) {
			case Elevator.STOP: 		str =  "STOP   "; break;
			case Elevator.MVTOFLR: 		str =  "MVTOFLR"; break;
			case Elevator.OPENDR:   	str =  "OPENDR "; break;
			case Elevator.CLOSEDR:		str =  "CLOSEDR"; break;
			case Elevator.BOARD:		str =  "BOARD  "; break;
			case Elevator.OFFLD:		str =  "OFFLD  "; break;
			case Elevator.MV1FLR:		str =  "MV1FLR "; break;
			default:					str =  "UNDEF  "; break;
		}
		return(str);
	}

	/**
	 * Log elevator config.
	 *
	 * @param capacity the capacity
	 * @param ticksPerFloor the ticks per floor
	 * @param ticksDoorOpenClose the ticks door open close
	 * @param passPerTick the pass per tick
	 * @param state the state
	 * @param floor the floor
	 */
	private void logElevatorConfig(int capacity, int ticksPerFloor, int ticksDoorOpenClose,
			                       int passPerTick, int state, int floor) {
		LOGGER.info(
				"CONFIG:   Capacity="+capacity+"   Ticks-Floor="+ticksPerFloor+"   Ticks-Door="+ticksDoorOpenClose+
				    "   Ticks-Passengers="+passPerTick+"   CurrState=" + (printState(state))+"   CurrFloor="+(floor+1)
		);
	}

	/**
	 * Log elevator state changed.
	 *
	 * @param time the time
	 * @param prevState the prev state
	 * @param currState the curr state
	 * @param prevFloor the prev floor
	 * @param currFloor the curr floor
	 */
	private void logElevatorStateOrFloorChanged(int time, int prevState, int currState, int prevFloor, int currFloor) {
		LOGGER.info("Time="+time+"   Prev State: " + printState(prevState) + "   Curr State: "+printState(currState)
		            +"   PrevFloor: "+(prevFloor+1) + "   CurrFloor: " + (currFloor+1));
	}

	/**
	 * Log arrival.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param id the id
	 */
	private void logArrival(int time, int numPass, int floor,int id) {
		LOGGER.info("Time="+time+"   Arrived="+numPass+" Floor="+ (floor+1)
		            +" passID=" + id);
	}

	/**
	 * Log calls.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logCalls(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Called="+numPass+" Floor="+ (floor +1)
			 	    +" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);
	}

	/**
	 * Log give up.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 * 
	 * PEER REVIEWED BY MK
	 */
	private void logGiveUp(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   GaveUp="+numPass+" Floor="+ (floor+1)
				    +" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);
	}

	/**
	 * Log skip.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logSkip(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Skip="+numPass+" Floor="+ (floor+1)
			   	    +" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);
	}

	/**
	 * Log board.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logBoard(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Board="+numPass+" Floor="+ (floor+1)
				    +" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);
	}

	/**
	 * Log end simulation.
	 *
	 * @param time the time
	 */
	private void logEndSimulation(int time) {
		LOGGER.info("Time="+time+"   Detected End of Simulation");
	}
}
