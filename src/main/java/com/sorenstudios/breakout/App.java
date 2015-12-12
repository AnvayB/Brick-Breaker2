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

package com.sorenstudios.breakout;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;

public class App extends Application {
    
    private Stage stage;
    private Engine game;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        Parent root = null;
    
        try {
            root = FXMLLoader.load(getClass().getResource("/breakout.fxml"));
        }
        catch (java.io.IOException e) {
            System.out.println(e);
            System.exit(1);
        }

        Scene scene = new Scene(root, 640, 600);
        this.game = new Breakout(primaryStage, scene);
        primaryStage.setTitle(game.getTitle());
        primaryStage.setResizable(false);
        
        primaryStage.show();
        game.run();
    }
    
    @Override
    public void stop() throws Exception {
        this.game.stop();
        this.stage.close();
        System.exit(0);
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
