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
	
	private Pane pane;
	private Scene scene;
	private StackPane sp;
	private BorderPane bp;
	
	Text currTime= new Text();
	Text pass= new Text();
	Text state= new Text();
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
	 * Start.
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
		timeLabel.setFont(Font.font("Helvetica", 20));
		currTime.setFont(Font.font("Helvetica",FontWeight.BOLD, 22));
		
		Label passLabel = new Label("Passengers:");
		passLabel.setFont(Font.font("Helvetica", 20));
		pass.setFont(Font.font("Helvetica",FontWeight.BOLD, 22));
		
		Label stateLabel = new Label("State:");
		stateLabel.setFont(Font.font("Helvetica", 20));
		state.setFont(Font.font("Helvetica",FontWeight.BOLD, 22));
		timebox.getChildren().addAll(timeLabel, currTime, passLabel, pass, stateLabel, state);


		floors = new HBox[NUM_FLOORS];
	    	for (int i = 0; i < floors.length; i++) {
	    		floors[i] = createHBox((NUM_FLOORS-i) + "");
	    }

	    // Create the scene with VBox containing all floors
	    VBox vb = createFloors(floors);
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

	 private HBox createHBox(String floor) {
		 HBox hBox = new HBox();
		 hBox.setSpacing(15);
		 hBox.setAlignment(Pos.CENTER);
		
		 StackPane stackPane = new StackPane();
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
        triangle.setFill(Color.rgb(179, 224, 232));
//        triangle.setStroke(Color.BLACK);
        
        triangleDown = new Polygon(
                300, 100 + Math.sqrt(3) / 3 * 25,
                300 + 25, 100,
                300, 100 - Math.sqrt(3) / 3 * 25
        );
        Rotate rotate2 = new Rotate(210, 308, 100);
        triangleDown.getTransforms().add(rotate2);
        triangleDown.setFill(Color.rgb(184, 212, 217));	
//      triangleDown.setStroke(Color.BLACK);
	  
        
	    arrow.getChildren().addAll(triangle,triangleDown);
		hBox.getChildren().addAll(stackPane, arrow);
		
		Insets padding = new Insets(0, 0, 0, 10);
		HBox.setMargin(stackPane, padding);
		return hBox;
	 }
	
	 public void setFloor(int floor, int state) {
		 for (int i = 0; i < floors.length; i++) {
			 StackPane floorStackPane = (StackPane) floors[i].getChildren().get(0);
		     Circle circle = (Circle) floorStackPane.getChildren().get(0);
			 circle.setFill(Color.rgb(161, 161, 161));
		 }
	     StackPane floorStackPane = (StackPane) floors[floors.length-floor-1].getChildren().get(0);
	     Circle circle = (Circle) floorStackPane.getChildren().get(0);
		 circle.setFill(Color.rgb(191, 232, 181));
//		 if (state == OFFLD || state == BOARD) circle.setFill(Color.rgb(121, 224, 135));

//		 circle.setStroke(Color.BLACK);
	 }

	 public void showDirection(int floor, int dir, Boolean call) {
//		 if (dir == 1 && call) {
//			 VBox arrows = (VBox) floors[floor].getChildren().get(1);
//		     Polygon triangle2 = (Polygon) arrows.getChildren().get(0);
//			 triangle2.setFill(Color.RED);
//		 }
//		 if (dir == -1 && call) {
//			 triangleDown.setFill(Color.SPRINGGREEN);
//		 }
	 }
	 
	 public void createPass(int groupNum, int floorNum) {
		 for (int i = 0; i < groupNum; i ++) {
			 HBox floor = floors[floorNum];
			 StackPane group = new StackPane();
			 Rectangle r = new Rectangle(50, 50);
			 r.setFill(Color.rgb(191, 232, 181));
			 r.setStroke(Color.rgb(161, 161, 161));
			 Text passengers = new Text("1");
			 passengers.setFont(Font.font("Helvetica",FontWeight.EXTRA_BOLD,18));
			 passengers.setFill(Color.BLACK);
			 group.getChildren().addAll(r,passengers);
			 floor.getChildren().addAll(group);
		 }
	 }
	 
	 public void setTimebox(int time, int eState, int ePass){
		 currTime.setText("" + time);
		 if (eState == 0) currState = "STOP";
		 if (eState == 1) currState = "MVTOFLR";
		 if (eState == 2) currState = "OPENDR";
		 if (eState == 3) currState = "OFFLD";
		 if (eState == 4) currState = "BOARD";
		 if (eState == 4) currState = "CLOSEDR";
		 if (eState == 4) currState = "MV1FLR";
		 state.setText("" + currState);
		 pass.setText("" + ePass);
				 
	 }
	 
	 public void createButtons() {
		buttons = new HBox();
		buttons.setSpacing(10);
		buttons.setAlignment(Pos.CENTER);
		
		logging = new Button("Logging");
		logging.setFont(Font.font("Helvetica", 16));
		logging.setStyle("-fx-background-color: rgb(199, 186, 255); -fx-background-radius: 5; -fx-border-radius: 5;-fx-border-color: black; -fx-text-fill: black;");
		
		run = new Button("Run");
		run.setOnAction(e -> t.play());
		run.setFont(Font.font("Helvetica", 16));
		run.setStyle("-fx-background-color: rgb(121, 224, 135); -fx-background-radius: 5; -fx-border-radius: 5;-fx-border-color: black; -fx-text-fill: black;");
		
		step = new Button("Step");
		step.setOnAction(e -> controller.stepSim());
		step.setFont(Font.font("Helvetica", 16));
		step.setStyle("-fx-background-color: rgb(224, 255, 161); -fx-background-radius: 5; -fx-border-radius: 5;-fx-border-color: black; -fx-text-fill: black;");
		
		stepLabel = new Label("Step #:");
		stepLabel.setFont(Font.font("Helvetica", 16));
		
		stepBy = new TextField();
		stepBy.setPrefColumnCount(7);
		stepBy.setOnAction(e -> stepUntil(Integer.parseInt(stepBy.getText())));
		stepBy.setStyle("-fx-background-color: WHITE; -fx-background-radius: 5; -fx-border-radius: 5;-fx-border-color: black;");
		//			step.setOnMouseEntered(e -> step.setStyle("-fx-font-weight: bold;-fx-background-color: rgb(224, 255, 161); -fx-background-radius: 5; -fx-border-radius: 5;-fx-border-color: black; -fx-text-fill: black;"));
		//	        step.setOnMouseExited(e -> step.setStyle("-fx-font-weight: normal;-fx-background-color: rgb(224, 255, 161); -fx-background-radius: 5; -fx-border-radius: 5;-fx-border-color: black; -fx-text-fill: black;"));

	 }
	 
	 private StackPane showDirection() {
		 StackPane arr = new StackPane();
		 Polygon arrowUp = new Polygon(
	                300, 100 + Math.sqrt(3) / 3 * 25,
	                300 + 25, 100,
	                300, 100 - Math.sqrt(3) / 3 * 25
	        );
		 Polygon negArrow = new Polygon(
	                300, 100 + Math.sqrt(3) / 3 * 25,
	                300 + 25, 100,
	                300, 100 - Math.sqrt(3) / 3 * 25
	        );
		 Rectangle stem = new Rectangle();
		 arr.getChildren().addAll(arrowUp, negArrow, stem);
		 return arr;
	 }
	
	 private VBox createFloors(HBox[] floors) {
	     VBox vb = new VBox();
	     vb.setAlignment(Pos.CENTER);
	     vb.setSpacing(10);
	
	     for (HBox floor : floors) {
	         vb.getChildren().add(floor);
	     }
	     return vb;
	 }
	 
	 public void endSimulation() {
		 t.stop();
	 }
	 
	 private void initTimeline() {
		t = new Timeline(new KeyFrame(Duration.millis(millisPerTick), e -> controller.stepSim()));
		t.setCycleCount(Animation. INDEFINITE);
		currTime.setText("" + time);
		pass.setText("" + passengers);
		state.setText("STOP");
	 }
	 
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
