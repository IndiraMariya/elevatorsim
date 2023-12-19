//Owned By: Indira Mariya
import building.Elevator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;


public class ElevatorSimulation extends Application {
	/** Instantiate the GUI fields */
	private ElevatorSimController controller;
	private final int NUM_FLOORS;
	private int currFloor;
	private int passengers;
	private int time;
	
	/** you MUST use millisPerTick as the duration for your timeline */
	private static int millisPerTick = 250;

	/** Local copies of the states for tracking purposes */
	private final int STOP = Elevator.STOP;
	private final int MVTOFLR = Elevator.MVTOFLR;
	private final int OPENDR = Elevator.OPENDR;
	private final int OFFLD = Elevator.OFFLD;
	private final int BOARD = Elevator.BOARD;
	private final int CLOSEDR = Elevator.CLOSEDR;
	private final int MV1FLR = Elevator.MV1FLR;

	private final int UP = 1;
	private final int DOWN = -1;

	private Pane pane;
	private Scene scene;
	private StackPane sp;
	private BorderPane bp;
	
	Text currTime= new Text();
	Text pass= new Text();
	Text state= new Text();
	Text dir = new Text();
	private Timeline t;
	String currState;
	HBox buttons;
	Button logging;
	Button run;
	Button step;
	Label stepLabel;
	TextField stepBy;
	HBox[] floors;
	Polygon triangle;
	Polygon triangleDown;
	

	/**
	 * Instantiates a new elevator simulation.
	 */
	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);	
		NUM_FLOORS = controller.getNumFloors();
		currFloor = 0;
	}

	/**
	 * Initialize the GUI design. 
	 *
	 * @param primaryStage the primary stage
	 * @throws Exception the exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// You need to design the GUI. Note that the test name should
		// appear in the Title of the window!!
		//TODO: Complete your GUI, including adding any helper methods.
				//      Meet the 30 line limit...
		primaryStage.setTitle("Elevator Simulation - "+ controller.getTestName());
		bp = new BorderPane();
		pane = new Pane();
		bp.setCenter(pane);
		scene = new Scene(bp, 450, 525);
		
		HBox timebox = new HBox();
		timebox.setSpacing(10);
		timebox.setAlignment(Pos.CENTER);
		
		Label timeLabel = new Label("Time:");
		currTime.setFont(Font.font("Helvetica",FontWeight.BOLD, 22));
		
		Label passLabel = new Label("Passengers:");
		pass.setFont(Font.font("Helvetica",FontWeight.BOLD, 22));
		
		Label stateLabel = new Label("State:");
		state.setFont(Font.font("Helvetica",FontWeight.BOLD, 22));
		
		state.setFont(Font.font("Helvetica",FontWeight.BOLD, 22));
		
		timebox.getChildren().addAll(timeLabel, currTime, passLabel, pass, stateLabel, state, dir);

		floors = new HBox[NUM_FLOORS];
		for (int i = 0; i < floors.length; i++) {
			floors[i] = createHBox((NUM_FLOORS-i) + "");
	    }

	    // Create the scene with VBox containing all floors
        pane.getChildren().addAll(createFloors(floors));
		createButtons();
		buttons.getChildren().addAll(run,step, stepLabel, stepBy,logging);
		buttons.setStyle("-fx-padding: 0 0 10 0;");
		bp.setBottom(buttons);
		timebox.setStyle("-fx-padding: 10 0 20 0;");
		bp.setTop(timebox);
        initTimeline();
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage
	}

	 
	 /**
	  * Helper method to create a HBox, including arrows
	  * and floor indicators, for a single floor. 
	  * 
	  * @param floor the floor
	  * @return HBox the generated HBox
	  */
	 private HBox createHBox(String floor) {
		 HBox hBox = new HBox();
		 hBox.setSpacing(15);
		 hBox.setAlignment(Pos.CENTER_LEFT);
		
		 StackPane stackPane = new StackPane();
		 hBox.setMinWidth(150); // Set a fixed width for the HBox
		 VBox arrow = new VBox();
		 arrow.setSpacing(-5);
		
		 Circle c = new Circle(25);
		 c.setFill(Color.rgb(161, 161, 161));
		 Text people = new Text(floor);
		 people.setFont(Font.font("Helvetica",FontWeight.EXTRA_BOLD,18));
		 people.setFill(Color.WHITE);
		 stackPane.getChildren().addAll(c, people);

        triangle = new Polygon(
        		 300, 100 + Math.sqrt(3) / 3 * 25,   // x1, y1 (top vertex)
                 300 + 25, 100,                      // x2, y2 (right vertex)
                 300, 100 - Math.sqrt(3) / 3 * 25    // x3, y3 (left vertex)
        );
        Rotate rotate = new Rotate(30, 300, 100);
        triangle.getTransforms().add(rotate);
        triangle.setFill(Color.rgb(184, 212, 217));	
        
        triangleDown = new Polygon(
                300, 100 + Math.sqrt(3) / 3 * 25,
                300 + 25, 100,
                300, 100 - Math.sqrt(3) / 3 * 25
        );
        Rotate rotate2 = new Rotate(210, 308, 100);
        triangleDown.getTransforms().add(rotate2);
        triangleDown.setFill(Color.rgb(184, 212, 217));	
	  
        
	    arrow.getChildren().addAll(triangle,triangleDown);
		hBox.getChildren().addAll(stackPane, arrow);
		
		Insets padding = new Insets(0, 0, 0, 10);
		HBox.setMargin(stackPane, padding);
		return hBox;
	 }
	
	 
	 /** 
	  * Indicate the active floor that the elevator
	  * is currently on.
	  * 
	  * @param floor the floor
	  * @param state the state
	  */
	 public void setFloor(int floor, int state) {
		 for (int i = 0; i < floors.length; i++) {
			 StackPane floorStackPane = (StackPane) floors[i].getChildren().get(0);
		     Circle circle = (Circle) floorStackPane.getChildren().get(0);
			 circle.setFill(Color.rgb(161, 161, 161));
		 }
	     StackPane floorStackPane = (StackPane) floors[floors.length-floor-1].getChildren().get(0);
	     Circle circle = (Circle) floorStackPane.getChildren().get(0);
		 circle.setFill(Color.rgb(191, 232, 181));
	 }

	 
	 /** 
	  * Show the direction of the elevator on a given floor.
	  * 
	  * @param floor the floor
	  * @param dir the direction
	  * @param call if there's a call
	  */
	 public void showDirection(int floor, int dir, Boolean call) {
		 VBox arrows = (VBox) floors[floors.length-floor-1].getChildren().get(1);
	     Polygon triangle2 = (Polygon) arrows.getChildren().get(0);
		 Polygon triangle3 = (Polygon) arrows.getChildren().get(1);
		 if (dir == 1 && call) {
			 triangle2.setFill(Color.DARKGRAY);
		 }
		 else if (dir == -1 && call) {
			 triangle3.setFill(Color.DARKGRAY);
		 }
		 else if (dir == 1 && !call) {
			 triangle2.setFill(Color.rgb(184, 212, 217));
		 }
		 else if (dir == -1 && !call) {
			 triangle3.setFill(Color.rgb(184, 212, 217));
		 }
	 }

	 /**
	 * Create a passenger icon in the GUI.
	 *
	 * @param floorNum floor number
	 * @param groupUp number of groups going up
	 * @param groupDown number of groups going down
	 */
	public void createPass(int floorNum, int groupUp, int groupDown) {
		HBox floor = floors[floors.length - 1 - floorNum];
		HBox passengerGroups = new HBox();
		passengerGroups.setSpacing(10);
		StackPane group;

		for (int i = 0; i < groupUp; i++) {
			group = createPassengerGroup(UP);
			passengerGroups.getChildren().add(group);
		}
		for (int i = 0; i < groupDown; i++) {
			group = createPassengerGroup(DOWN);
			passengerGroups.getChildren().add(group);
		}

		while (floor.getChildren().size() > groupUp + groupDown + 2) {
			floor.getChildren().remove(floor.getChildren().size()-1);
		}
		floor.getChildren().add(passengerGroups);
	 }

	/**
	 * Helper method creating Stack group representing a passenger group
	 *
	 * @param dir Direction of passenger group
	 * @return StackPane representing PassengerGroup
	 */
	private StackPane createPassengerGroup(int dir) {
		Text passengers;
		StackPane group;

		group = new StackPane();
		Rectangle r = new Rectangle(50, 50);
		r.setStroke(Color.BLACK);
		if (dir == UP) r.setFill(Color.rgb(255, 242, 161));
		else if (dir == DOWN) r.setFill(Color.rgb(208, 194, 255));
		passengers = new Text();
		if (dir == UP) passengers.setText("UP");
		else if (dir == DOWN) passengers.setText("DOWN");
		passengers.setFont(Font.font("Helvetica", FontWeight.EXTRA_BOLD, 12));
		passengers.setFill(Color.BLACK);
		group.getChildren().addAll(r, passengers);

		return group;
	}

	 /**
	  * Sets time and currState in the top bar
	  * of the GUI. 
	  * 
	  * @param time the time
	  * @param eState the state of the elevator at the given time
	  * @param ePass the number of passengers at the given time
	  */
	 public void setTimebox(int time, int eState, int ePass, int direc){
		 currTime.setText("" + time);
		 if (eState == STOP) currState = "STOP";
		 if (eState == MVTOFLR) currState = "MVTOFLR";
		 if (eState == OPENDR) currState = "OPENDR";
		 if (eState == OFFLD) currState = "OFFLD";
		 if (eState == BOARD) currState = "BOARD";
		 if (eState == CLOSEDR) currState = "CLOSEDR";
		 if (eState == MV1FLR) currState = "MV1FLR";
		 state.setText("" + currState);
		 pass.setText("" + ePass);
		 if (direc == 1) dir.setText("UP");
		 else dir.setText("DOWN");
		 
				 
	 }
	 
	 /**
	  * Creates Run, Step, and Logging buttons in the bottom bar.
	  * 
	  */
	 private void createButtons() {
		buttons = new HBox();
		buttons.setSpacing(10);
		buttons.setAlignment(Pos.CENTER);
		
		logging = new Button("Logging");
		logging.setOnAction(e -> controller.enableLogging());
		logging.setFont(Font.font("Helvetica", 16));
		logging.setStyle("-fx-background-color: rgb(199, 186, 255); " +
				"-fx-background-radius: 5; " +
				"-fx-border-radius: 5;" +
				"-fx-border-color: black; " +
				"-fx-text-fill: black;");
		
		run = new Button("Run");
		run.setOnAction(e -> t.play());
		run.setFont(Font.font("Helvetica", 16));
		run.setStyle("-fx-background-color: rgb(121, 224, 135); " +
				"-fx-background-radius: 5; " +
				"-fx-border-radius: 5;" +
				"-fx-border-color: black; " +
				"-fx-text-fill: black;");
		
		step = new Button("Step");
		step.setOnAction(e -> controller.stepSim());
		step.setFont(Font.font("Helvetica", 16));
		step.setStyle("-fx-background-color: rgb(224, 255, 161); " +
				"-fx-background-radius: 5; " +
				"-fx-border-radius: 5;" +
				"-fx-border-color: black; " +
				"-fx-text-fill: black;");
		
		stepLabel = new Label("Step #:");
		stepLabel.setFont(Font.font("Helvetica", 16));
		
		stepBy = new TextField();
		stepBy.setPrefColumnCount(7);
		stepBy.setOnAction(e -> stepUntil(Integer.parseInt(stepBy.getText())));
		stepBy.setStyle("-fx-background-color: WHITE; " +
				"-fx-background-radius: 5; " +
				"-fx-border-radius: 5;" +
				"-fx-border-color: black;");
	 }
	
	 
	 /** 
	  * Creates a VBox out of a list of floor HBoxes.
	  * 
	  * @param floors List of HBoxes
	  * @return VBox the VBox
	  */
	 private VBox createFloors(HBox[] floors) {
	     VBox vb = new VBox();
	     vb.setAlignment(Pos.CENTER);
	     vb.setSpacing(10);
	
	     for (HBox floor : floors) {
	         vb.getChildren().add(floor);
	     }
	     return vb;
	 }
	 
	 /** 
	  * Ends the simulation.
	  */
	 public void endSimulation() {
		 t.stop();
	 }
	 

	 /** 
	  * Initializes the timeline.
	  */
	 private void initTimeline() {
		t = new Timeline(new KeyFrame(Duration.millis(millisPerTick), e -> controller.stepSim()));
		t.setCycleCount(Animation. INDEFINITE);
		currTime.setText("" + time);
		pass.setText("" + passengers);
		state.setText("STOP");
	 }
	 
	 
	 /** 
	  * Runs stepSim until iteration number stepNum. 
	  * 
	  * @param stepNum number of steps to run
	  */
	 private void stepUntil(int stepNum) {
		 for (int i = 0; i < stepNum; i++) {
			 controller.stepSim();
		 }
		 time += stepNum;
	 }
	
	/**
	 * The main method. Allows command line to modulate the speed of the simulation.
	 *
	 * @param args the arguments
	 */
	public static void main (String[] args) {
		if (args.length>0) {
			for (int i = 0; i < args.length-1; i++) {
				if ("-m".equals(args[i])) {
					try {
						ElevatorSimulation.millisPerTick = Integer.parseInt(args[i+1]);
					} catch (NumberFormatException e) {
						System.out.println("Unable to update millisPerTick to "+args[i+1]);
					}
				}
			}
		}
		Application.launch(args);
	}

}
