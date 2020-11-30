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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javafx.scene.input.KeyCode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public abstract class Engine {

    protected final Stage stage;
    private final Timeline loop;
    private final String title;
    
    private Instant actualFrameTime = Instant.now();
    private KeyCode lastKey;
    
    public Engine(Stage stage, Scene scene, String title, int fps) {
        this.stage = stage;
        this.title = title;
        
        // Attach scene to stage & size
        stage.setScene(scene);
        stage.sizeToScene();
        
        Duration frameTime = Duration.millis(1000.0d / fps);
        KeyFrame frame = new KeyFrame(frameTime, (e) -> {
            this.update(this);
            this.actualFrameTime = Instant.now();
        });
        
        this.loop = new Timeline();
        this.loop.setCycleCount(-1);
        this.loop.getKeyFrames().add(frame);
    }
    
    public void run() {
        this.attachKeyHandler(this.stage.getScene());
        this.loop.play();
    }
    
    public void stop() {
        this.loop.stop();
    }
    
    public double getFPS() {
        return 1000.0d / this.actualFrameTime.until(Instant.now(), ChronoUnit.MILLIS);
    }
    
    private void attachKeyHandler(Scene scene) {
        scene.setOnKeyPressed((e) -> this.lastKey = e.getCode());
        scene.setOnKeyReleased((e) -> this.lastKey = null);
    }
    
    public KeyCode getKeyPressed() {
        return this.lastKey;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public abstract void update(Engine gameObj);
  
}
