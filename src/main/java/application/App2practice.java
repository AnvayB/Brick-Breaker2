package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;

public class App2practice extends Application {

   
	
	@Override
    public void start(Stage primaryStage) throws Exception {
		Button button = new Button();
        button.setText("Play Game");
 
        button.setOnAction(new EventHandler<ActionEvent>() {
 
        	 //public void start(final Stage primaryStage) {
        	 public void handle(ActionEvent event) {
		
        Parent root = null;
    
        try {
            root = FXMLLoader.load(getClass().getResource("/breakout.fxml"));
        }
        catch (java.io.IOException e) {
            System.out.println(e);
            System.exit(1);
        }

        Scene scene = new Scene(root, 640, 600);
        Engine game = new Breakout(primaryStage, scene);
        primaryStage.setTitle(game.getTitle());
        primaryStage.setResizable(false);
         
        primaryStage.show();
        game.run();
        	 }

 			
        });
 
        StackPane root = new StackPane();
        root.getChildren().add(button);
 
        Scene scene = new Scene(root, 450, 250);
 
        primaryStage.setTitle("Trial");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    @Override
    public void stop() throws Exception {
        System.exit(0);
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
