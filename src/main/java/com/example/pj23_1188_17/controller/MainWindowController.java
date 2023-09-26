package com.example.pj23_1188_17.controller;

import com.example.pj23_1188_17.Run;
import com.example.pj23_1188_17.model.*;
import com.example.pj23_1188_17.model.terminals.Terminal;
import com.example.pj23_1188_17.model.vehicles.Vehicle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.IOException;
import java.util.logging.Level;

public class MainWindowController implements Initializable{

    @FXML
    private Button btnIncident;

    @FXML
    private Button btnPause;

    @FXML
    private Button btnStart;

    @FXML
    private Button btnVehicles;

    @FXML
    private Pane buscarC;

    @FXML
    private   Pane buscarP1;

    @FXML
    private Pane buscarP2;

    @FXML
    private Pane carCustoms;

    @FXML
    private Pane carPoliceLeft;

    @FXML
    private Pane carPoliceRight;

    @FXML
    private Label lblCarBusCustoms;

    @FXML
    private Label lblCarBusLeft;

    @FXML
    private Label lblCarBusRight;

    @FXML
    private Label lblTime;

    @FXML
    private Label lblTruckCustoms;

    @FXML
    private  Label lblTruckPolice;

    @FXML
    private Pane truckC;

    @FXML
    private Pane truckCustoms;

    @FXML
    private Pane truckP;

    @FXML
    private Pane truckPolice;

    @FXML
    private TextField txtId;

    @FXML
    private  GridPane vehicleGrid;

    @FXML
    private Button btnSearch;

    @FXML
    private Label lblImportant;

    private BorderSimulation borderSimulation;

    private ConcurrentLinkedQueue<Vehicle> queue;

    private static Pane[] help = new Pane[5];
    private static Pane[] terminalPictures = new Pane[5];
    private static Pane[] terminalVehicles = new Pane[5];

    public static boolean isSimulationActive;
    public static boolean pause = false;

    private long pausedStartTime;
    private  long pausedTime = 0;

    public static boolean gameOver = false;

    public static Object pauseLock = new Object();

    private OtherVehiclesController otherVehiclesController = new OtherVehiclesController();
    private OccurredIncidentsController occurredIncidentsController = new OccurredIncidentsController();

    private static final Label[] staticLabels = new Label[5];
    private static final Label staticImportant = new Label();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
            for(int i = 0; i < 5 ; i++){
                help[i] = new Pane();
            }
            insertIntoGridPane();
            borderSimulation = new BorderSimulation();
             for (Vehicle vehicle:BorderSimulation.vehicleQueue) {
            if(vehicle.getPosition() < 5){
               // System.out.println(vehicle);
                 updateQueueGUI(vehicle);
            }
             }
             insertTerminals();
             System.out.println("\n");
            for(int i = 0; i<5;i++){
                staticLabels[i] = new Label();
            }
            for (Terminal terminal:BorderSimulation.terminals) {
            drawTerminals(terminal);
            }
            makeLabelsUsable();
            staticLabels[0].setText("Unoccupied");
            staticLabels[1].setText("Unoccupied");
            staticLabels[2].setText("Unoccupied");
            staticLabels[3].setText("Unoccupied");
            staticLabels[4].setText("Unoccupied");
            staticImportant.setText("Nothing has happened yet");
            btnPause.setDisable(true);
            btnSearch.setDisable(true);
            insertedText();

        //need to fix bugs , positions problem >>>> FIXED
            btnStart.setOnAction(event -> btnStartClicked());

            btnPause.setOnAction(event -> btnPauseClicked());

            btnVehicles.setOnAction(event -> btnVehiclesClicked());

            btnIncident.setOnAction(event -> btnIncidentClicked());

            btnSearch.setOnAction(event -> btnSearchClicked());
    }

    private void makeLabelsUsable(){
        connectLabels(lblTruckPolice,staticLabels[0]);
        connectLabels(lblCarBusRight,staticLabels[1]);
        connectLabels(lblCarBusLeft,staticLabels[2]);
        connectLabels(lblTruckCustoms,staticLabels[3]);
        connectLabels(lblCarBusCustoms,staticLabels[4]);
        connectLabels(lblImportant,staticImportant);
    }

    public void insertIntoGridPane(){

        for(int i = 0; i<5; i++) {
           // System.out.println(BorderSimulation.vehicleQueue);
            try {

                vehicleGrid.add(help[i], 0, i);
            }catch (NullPointerException exception){
                Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
            }
        }
    }

     public static void updateQueueGUI(Vehicle vehicle){
        //System.out.println("file:/"+vozilo.getPicture().getPath().replace("\\","/"));

        ImageView imageView = new ImageView(new Image(vehicle.getPicture().toURI().toString()));
        imageView.setFitWidth(62);
        imageView.setFitHeight(48);
         Label tmp = new Label(String.valueOf(vehicle.getVehicleID()));
         //tmp.setLayoutX(40);
         //System.out.println(vehicle.getPosition() + "  POZICIJA VOZILA!");
         tmp.setLayoutX(46);
         tmp.setLayoutY(0);
         tmp.setStyle("-fx-font-size: 14; -fx-text-fill: black;");
        Platform.runLater(()->{
            try {
                ifEnd(vehicle.getPosition());
                help[vehicle.getPosition()].getChildren().addAll(imageView,tmp);
              //  help[vehicle.getPosition()].getChildren().add(tmp); //throws IndexOutOfBoundsException , needs to be
                //added in a single line !!
            }catch (ArrayIndexOutOfBoundsException exception){
                //exception.printStackTrace();
               Run.logger.log(Level.INFO,exception.fillInStackTrace().toString());
            }
        });
    }

    public static void ifEnd(int position) {
        boolean hasPosition = BorderSimulation.vehicles.stream().anyMatch(vehicle -> vehicle.getPosition() == position+1);
        if(!hasPosition){
            if(position < 4) {
                removeFromQueue(position + 1);
            }
        }
    }
    public static void removeFromQueue(int position){
        Platform.runLater(()->{
            try{
            help[position].getChildren().clear();}
            catch (ArrayIndexOutOfBoundsException exception){
                Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
            }
        });

    }

    public static void updateTerminalGUI(Vehicle vehicle,Terminal terminal){
        ImageView imageView = new ImageView(new Image(vehicle.getPicture().toURI().toString()));
        imageView.setFitWidth(40);
        imageView.setFitHeight(35);
        Label tmp = new Label(String.valueOf(vehicle.getVehicleID()));
        //tmp.setLayoutX(40);
        tmp.setLayoutX(37);
        tmp.setLayoutY(0);
        tmp.setStyle("-fx-font-size: 14; -fx-text-fill: black;");
        Platform.runLater(()->{
            for(int i = 1; i<6 ; i++){
                try{
                if(terminal.getTerminalID() == i) {
                    terminalVehicles[i - 1].getChildren().addAll(imageView, tmp);
                }
                    //terminalVehicles[i-1].getChildren().add(tmp); -> arrayindexoutofbounds
                }catch (Exception exception){
                    Run.logger.log(Level.INFO,exception.fillInStackTrace().toString()
                    );
                }
            }
        });
    }


    public void btnVehiclesClicked(){
        if (otherVehiclesController == null) {
            otherVehiclesController = new OtherVehiclesController();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/OtherVehicles.fxml"));
        loader.setController(otherVehiclesController);
        Parent root;
        try{
            root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setTitle("Rest of the Vehicles!");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }catch (IOException exception){
            exception.printStackTrace();
            Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
        }

    }

    public void insertTerminals(){
        for(int i = 0; i <5;i++){
            terminalPictures[i] = new Pane();
            terminalVehicles[i] = new Pane();
            }
        try{
        carPoliceLeft.getChildren().add(terminalPictures[2]);
        carPoliceRight.getChildren().add(terminalPictures[1]);
        truckPolice.getChildren().add(terminalPictures[0]);
        carCustoms.getChildren().add(terminalPictures[4]);
        truckCustoms.getChildren().add(terminalPictures[3]);
        truckP.getChildren().add(terminalVehicles[0]);
        buscarP2.getChildren().add(terminalVehicles[1]);
        buscarP1.getChildren().add(terminalVehicles[2]);
        buscarC.getChildren().add(terminalVehicles[4]);
        truckC.getChildren().add(terminalVehicles[3]);
        }catch (NullPointerException exception){
            Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
        }
    }



    public void drawTerminals(Terminal terminal){
        ImageView imageView = new ImageView(new Image(terminal.getPicture().toURI().toString()));
        imageView.setFitWidth(80);
        imageView.setFitHeight(54);
        Platform.runLater(()->{
            try {
                if(terminal.isNormalVehicles()) {
                    terminalPictures[terminal.getTerminalID() - 1].getChildren().add(imageView);
                }else{
                    Label tmp = new Label("T");
                    tmp.setLayoutX(40);
                    tmp.setLayoutY(25);
                    tmp.setStyle("-fx-font-size: 30; -fx-text-fill: blue;");
                    terminalPictures[terminal.getTerminalID() - 1].getChildren().add(imageView);
                    terminalPictures[terminal.getTerminalID() - 1].getChildren().add(tmp);

                }
            }catch (NullPointerException exception){
                Run.logger.log(Level.INFO,exception.fillInStackTrace().toString());
            }
        });
    }

    public void btnIncidentClicked(){
        if (occurredIncidentsController == null) {
            occurredIncidentsController = new OccurredIncidentsController();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/OccurredIncidents.fxml"));
        loader.setController(occurredIncidentsController);
        Parent root;
        try{
            root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setTitle("Incidents that have occurred!");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }catch (IOException exception){
            Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
        }

    }
    public void btnSearchClicked(){
        try {
            int vehicleID = Integer.parseInt(txtId.getText().trim());

        if(vehicleID < 1 || vehicleID > 50){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Search Information");
            alert.setHeaderText("Invalid Vehicle ID!");
            alert.setContentText("The vehicle with inserted ID does not exist!");
            alert.show();
            txtId.clear();
        }
        else{
            SearchedVehicleController searchedVehicleController = new SearchedVehicleController(vehicleID);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SearchedVehicle.fxml"));
            loader.setController(searchedVehicleController);
            Parent root;
            try {
                root = loader.load();
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setTitle("About the Vehicle!");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
                txtId.clear();
            }catch (IOException exception){
                exception.printStackTrace();
                Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
            }
            }
        }catch (Exception exception){
        Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Search Information");
            alert.setHeaderText("Insert a Number");
            alert.setContentText("You can't insert any characters beside numbers!");
            alert.show();
            txtId.clear();
        }
    }

    public void btnPauseClicked(){
        if(gameOver){
            btnPause.setDisable(true);
        }else{
        if(!pause) {
            System.out.println("Creating the pause!");
            creatingPause();
        }else if(pause) {
            System.out.println("Trying to continue!");
            continueAfterPause();
        }
        }

    }

    public void btnStartClicked(){
        borderSimulation.start();
        btnPause.setDisable(false);
        btnStart.setDisable(true);
        isSimulationActive = true;
        insertTimeVariable();
    }

    public void insertTimeVariable(){

        long currentTime = System.currentTimeMillis();
        long startTime = currentTime - pausedTime;
        //if(isSimulationActive) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
                updateTimeLabel(startTime);
            }));
            timeline.setCycleCount(Timeline.INDEFINITE); // It is repeating itself every 0.1 second
            timeline.play();
        //}
    }

    public void updateTimeLabel(long startTime) {
        checkGameOver();
        if(isSimulationActive && !gameOver){ //---> need to do this after I make the pause possible ----DONE
          //  checkGameOver();
            long currentTime = System.currentTimeMillis();
            long deltaTime = ((currentTime - startTime) / 1000) - (pausedTime)/1000;
            lblTime.setText(deltaTime + "s");
        }

    }

    public static void removeFromPoliceTerminal(Terminal currentTerminal){
        for (Terminal terminal:BorderSimulation.terminals) {
            if(terminal.getTerminalID() == currentTerminal.getTerminalID()){
                switch (currentTerminal.getTerminalID()){
                    case(1):
                        Platform.runLater(()->{
                            terminalVehicles[0].getChildren().clear();
                                });
                        break;
                    case(2):
                        Platform.runLater(()->{
                            terminalVehicles[1].getChildren().clear();
                        });
                        break;
                    case(3):
                        Platform.runLater(()->{
                            terminalVehicles[2].getChildren().clear();
                        });
                        break;
                    default:
                        System.out.println("Nothing has been deleted POLICE TERMINAL !!!!");
                }
            }
        }
    }

    public static void removeFromCustomsTerminal(Terminal currentTerminal) {
        for (Terminal terminal : BorderSimulation.terminals) {
            try{
            if (terminal.getTerminalID() == currentTerminal.getTerminalID()) {
                switch (currentTerminal.getTerminalID()) {
                    case(4):
                        Platform.runLater(() -> {
                            terminalVehicles[3].getChildren().clear();
                        });
                        break;
                    case (5):
                        Platform.runLater(() -> {
                            terminalVehicles[4].getChildren().clear();
                        });
                        break;
                    default:
                        System.out.println("Nothing has been deleted CUSTOMS TERMINAL !!!");
                }
                }
            }catch (Exception exception){exception.printStackTrace();}
        }
    }
    public  void creatingPause(){
        pause = true;
        isSimulationActive = false;
        pausedStartTime = System.currentTimeMillis();
//        synchronized (pauseLock) {
//            pauseLock.notifyAll();
//        }
    }
    public void continueAfterPause(){
        pause = false;
        isSimulationActive = true;
        long currentTime = System.currentTimeMillis();
        pausedTime += (currentTime - pausedStartTime);
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
    }

    public void checkGameOver(){
        int noVehicles = 0;
        if (BorderSimulation.vehicleQueue.isEmpty()) {
            for (Terminal terminal : BorderSimulation.terminals) {
                if (terminal.vehicle == null) {
                    noVehicles++;
                }
            }
            if(noVehicles == 5){
                MainWindowController.gameOver = true;
                btnPause.setDisable(true);
                lblTruckPolice.setText("Done");
                lblCarBusLeft.setText("Done");
                lblCarBusRight.setText("Done");
                lblTruckCustoms.setText("Done");
                lblCarBusCustoms.setText("Done");
                lblImportant.setText("Simulation is done");
            }

        }
    }

    // label cant be @FXML private static Label
    //need to change listener to static label, otherwise I get NullPointerException
    public static void writeEventOnGUI(int terminalID, String string){
        Platform.runLater(()-> {
            switch (terminalID) {
                case (1):
                    //System.out.println(string);
                    staticLabels[0].setText(string);
                    break;
                case (2):
                    staticLabels[1].setText(string);
                    break;
                case (3):
                    staticLabels[2].setText(string);
                    break;
                case (4):
                    staticLabels[3].setText(string);
                    break;
                case (5):
                    staticLabels[4].setText(string);
                    break;
                default:
                    staticImportant.setText(string);

            }
        });
    }
    //connect each terminal label on gui with one static label
    private void connectLabels(Label normal,Label statik){
        statik.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                normal.setText(statik.getText());
            }
        });
    }

    private void insertedText(){
        txtId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                btnSearch.setDisable(false);
            } else {
                btnSearch.setDisable(true);
            }
        });
    }
}








