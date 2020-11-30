
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
