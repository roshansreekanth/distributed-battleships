package views;

import java.rmi.RemoteException;

import controllers.SentryController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import models.ships.*;

@SuppressWarnings("deprecation")

// TODO FIX LAUNCH JSON FILE

public class SentryView extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage firstStage) throws RemoteException 
    {
        Stage secondStage = new Stage();
        
        SentryController controller = new SentryController(); // Initializing the controller serves as a jump off point for all threads to start.

        launchWindow(firstStage, "Kinsale", controller);
        launchWindow(secondStage, "Youghal", controller);
    }
    
    public void launchWindow(Stage stageObject, String stageTitle, SentryController controller) // Can launch N windows for N sentries
    {
        
        stageObject.setTitle(stageTitle);

        GridPane shipMakeGrid = new GridPane();
        shipMakeGrid.setAlignment(Pos.TOP_CENTER);
        shipMakeGrid.setHgap(10);
        shipMakeGrid.setVgap(10);
        shipMakeGrid.setPadding(new Insets(10,10,10, 10));
        
        Label title = new Label(stageObject.getTitle());

        HBox shipMakeButtons = new HBox(10);

        Button makeAircraftCarrierShipButton = new Button("Aircraft Carrier Ship");
        Button makeDestroyerShipButton = new Button("Destroyer Ship");
        Button makeSailingShipButton = new Button("Sailing Ship");

        makeAircraftCarrierShipButton.setOnAction(e ->
        {   
            controller.makeShipController(stageObject.getTitle(), new AircraftCarrierShipFactory());
        });

        makeDestroyerShipButton.setOnAction(e ->
        {
            controller.makeShipController(stageObject.getTitle(), new DestroyerShipFactory());
        });

        makeSailingShipButton.setOnAction(e ->
        {
            controller.makeShipController(stageObject.getTitle(), new SailingShipFactory());
        });

        shipMakeButtons.getChildren().addAll(makeAircraftCarrierShipButton, makeDestroyerShipButton, makeSailingShipButton);

        shipMakeGrid.add(title, 0, 0);
        shipMakeGrid.add(shipMakeButtons, 0, 1);

        Scene scene = new Scene(shipMakeGrid, 500, 500);
        scene.getStylesheets().add("stylesheet.css");
        stageObject.setScene(scene);
        stageObject.show();
    }
}
