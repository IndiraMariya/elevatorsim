


import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;

import building.Elevator;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.geometry.Insets;


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
		Text time = new Text(34 + "   ");
		time.setFont(Font.font("Helvetica",FontWeight.BOLD, 22));
		
		Label passLabel = new Label("Passengers:");
		passLabel.setFont(Font.font("Helvetica", 20));
		Text pass = new Text(4  + "   ");
		pass.setFont(Font.font("Helvetica",FontWeight.BOLD, 22));
		timebox.getChildren().addAll(timeLabel, time, passLabel, pass);

		HBox[] floors = new HBox[NUM_FLOORS];
	    	for (int i = 0; i < floors.length; i++) {
	    		floors[i] = createHBox();
	    }

	    // Access the first floor's Circle and Text
	    StackPane firstFloorStackPane = (StackPane) floors[0].getChildren().get(0);
	    Circle firstFloorCircle = (Circle) firstFloorStackPane.getChildren().get(0);
	    Text firstFloorText = (Text) firstFloorStackPane.getChildren().get(1);

	    // Call changeCircleProperties for the first floor
	    changeCircleProperties(firstFloorCircle, firstFloorText);

	    // Create the scene with VBox containing all floors
	    VBox vb = createFloors(floors);
        pane.getChildren().addAll(createFloors(floors));
		
		HBox buttons = new HBox();
		buttons.setSpacing(10);
		buttons.setAlignment(Pos.CENTER);
		Button logging = new Button("Logging");
		logging.setFont(Font.font("Helvetica", 16));
		logging.setStyle("-fx-background-color: rgb(199, 186, 255); -fx-background-radius: 5; -fx-border-radius: 5;-fx-border-color: black; -fx-text-fill: black;");
		Button run = new Button("Run");
		run.setFont(Font.font("Helvetica", 16));
		run.setStyle("-fx-background-color: rgb(121, 224, 135); -fx-background-radius: 5; -fx-border-radius: 5;-fx-border-color: black; -fx-text-fill: black;");
		Button step = new Button("Step");
		step.setFont(Font.font("Helvetica", 16));
		step.setStyle("-fx-background-color: rgb(224, 255, 161); -fx-background-radius: 5; -fx-border-radius: 5;-fx-border-color: black; -fx-text-fill: black;");
		Label stepLabel = new Label("Step #:");
		stepLabel.setFont(Font.font("Helvetica", 16));
		TextField stepBy = new TextField();
		stepBy.setPrefColumnCount(7);
		stepBy.setStyle("-fx-background-color: WHITE; -fx-background-radius: 5; -fx-border-radius: 5;-fx-border-color: black;");
//		step.setOnMouseEntered(e -> step.setStyle("-fx-font-weight: bold;-fx-background-color: rgb(224, 255, 161); -fx-background-radius: 5; -fx-border-radius: 5;-fx-border-color: black; -fx-text-fill: black;"));
//        step.setOnMouseExited(e -> step.setStyle("-fx-font-weight: normal;-fx-background-color: rgb(224, 255, 161); -fx-background-radius: 5; -fx-border-radius: 5;-fx-border-color: black; -fx-text-fill: black;"));

		buttons.getChildren().addAll(run,step, stepLabel, stepBy,logging);
		buttons.setStyle("-fx-padding: 0 0 10 0;");
		bp.setBottom(buttons);
		timebox.setStyle("-fx-padding: 10 0 20 0;");
		bp.setTop(timebox);
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage
		
	}

	private HBox createHBox() {
		 HBox hBox = new HBox();
		 hBox.setSpacing(15);
		 hBox.setAlignment(Pos.CENTER);
		
		 StackPane stackPane = new StackPane();
		 VBox arrow = new VBox();
		 arrow.setSpacing(-5);
		
		 Circle c = new Circle(25);
//		 c.setStroke(Color.BLACK);
		 c.setFill(Color.rgb(255, 138, 138));
		 Text people = new Text("0");
		 people.setFont(Font.font("Helvetica",FontWeight.EXTRA_BOLD,18));
		 people.setFill(Color.WHITE);
		 stackPane.getChildren().addAll(c, people);

        Polygon triangle = new Polygon(
        		 300, 100 + Math.sqrt(3) / 3 * 25,   // x1, y1 (top vertex)
                 300 + 25, 100,                      // x2, y2 (right vertex)
                 300, 100 - Math.sqrt(3) / 3 * 25    // x3, y3 (left vertex)
        );
        Rotate rotate = new Rotate(30, 300, 100);
        triangle.getTransforms().add(rotate);
        triangle.setFill(Color.rgb(179, 224, 232));
//        triangle.setStroke(Color.BLACK);
        
        Polygon triangleDown = new Polygon(
                300, 100 + Math.sqrt(3) / 3 * 25,
                300 + 25, 100,
                300, 100 - Math.sqrt(3) / 3 * 25
        );
        Rotate rotate2 = new Rotate(210, 308, 100);
        triangleDown.getTransforms().add(rotate2);
        triangleDown.setFill(Color.rgb(184, 212, 217));	
//        triangleDown.setStroke(Color.BLACK);
	        
	     arrow.getChildren().addAll(triangle,triangleDown);
		 hBox.getChildren().addAll(stackPane, arrow);
		
		 Insets padding = new Insets(0, 0, 0, 10);
		 HBox.setMargin(stackPane, padding);
		 return hBox;
	 }
	
	 private void changeCircleProperties(Circle circle, Text text) {
	     circle.setFill(Color.rgb(121, 224, 135));
	     text.setText("1");
	     text.setFill(Color.WHITE);
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
