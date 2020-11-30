/*
    A Breakout clone in JavaFX
    Copyright (C) 2015 Nicholas Narsing <soren121@sorenstudios.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package application;

import application.Tutorial2;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.Parent;

public class App3 extends Application {

	Stage window;
	Button play, tutorial, rankings, closeButton;
	Label title;

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setTitle("Brick-Breaker");

		InnerShadow is = new InnerShadow();

		Text title = new Text();
		title.setEffect(is);
		title.setText("BRICK BREAKER");
		title.setFill(Color.MAROON);
		title.setFont(Font.font(null, FontWeight.BOLD, 80));
		title.relocate(120, 150);

		play = new Button("Play Game");
		play.relocate(345, 450);

		play.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {

				Parent root = null;

				try {
					root = FXMLLoader.load(getClass().getResource("/breakout.fxml"));
				} catch (java.io.IOException e) {
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

		tutorial = new Button("Tutorial");
		tutorial.relocate(355, 500);

		tutorial.setOnAction(e -> {
			Tutorial2.display("Tutorial", "Welcome to Brick-Breaker!" + "\n" + "\nHere’s how to play the game:" + "\n"
					+ "\nYour goal is to knock out all the bricks on the screen by moving the paddle. "
					+ "\n\nLaunch the ball by pressing the SPACE bar."
					+ "\nControl the paddle using the LEFT and RIGHT arrow keys on your keyboard." + "\n"
					+ "\nDon't let the ball hit the bottom of the screen, otherwise you lose a life."
					+ "\nYou have 5 lives." + "\n\nHave fun!! :)");

		});

		rankings = new Button("Leaderboard");
		rankings.relocate(340, 550);

		closeButton = new Button("Close game");
		closeButton.setOnAction(e -> window.close());
		closeButton.relocate(343, 600);

		Image img = new Image("file:back.jpeg");
		ImageView mv = new ImageView(img);

		Pane layout = new Pane();
		layout.getChildren().addAll(mv, title, play, tutorial, rankings, closeButton);
		Scene scene = new Scene(layout, 800, 800);
		window.setScene(scene);
		window.setResizable(false);
		window.show();

	}

	@Override
	public void stop() throws Exception {
		System.exit(0);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
