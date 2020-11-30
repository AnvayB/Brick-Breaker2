
package application;

import javafx.scene.shape.Circle;
import javafx.scene.layout.Pane;
import javafx.geometry.Bounds;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/**
 * This class handles ball animation and calls the appropriate collision check methods.
 */
public class Ball {
    
    private Circle ball;
    private double dx;
    private double dy;
    private Bricks bricks;
    private Paddle paddle;
    
    private List<LevelListener> lossListeners = new ArrayList<LevelListener>();
    
    /**
     * Creates a new ball handler object.
     *
     * @param ball The Circle object that represents the ball.
     * @param bricks The Bricks handler.
     * @param paddle The Paddle handler.
     * @param speed The initial speed of the ball, in pixels per update.
     */
    public Ball(Circle ball, Bricks bricks, Paddle paddle, double speed) {
        this.ball = ball;
        this.bricks = bricks;
        this.paddle = paddle;
        setStartingSpeed(speed);
    }
    
    /**
     * A convenience constructor that has a pre-set speed for new games.
     *
     * @param ball The Circle object that represents the ball.
     * @param bricks The Bricks handler.
     * @param paddle The Paddle handler.
     */
    public Ball(Circle ball, Bricks bricks, Paddle paddle) {
        this(ball, bricks, paddle, 4);
    }
    
    /**
     * Sets the starting speed of the ball.
     *
     * @param speed The initial speed of the ball, in pixels per update.
     */
    public void setStartingSpeed(double speed) {
        // Randomize initial starting angle
        this.dx = (new Random()).nextBoolean() ? speed : -speed;
        this.dy = -speed;
    }
    
    /**
     * Adds a listener object that is called by animate() when the player loses.
     *
     * @param newListener A LevelListener object to attach.
     */
    public void addLossListener(LevelListener newListener) {
        this.lossListeners.add(newListener);
    }
    
    /**
     * Animates the ball and handles collision detection.
     */
    public void animate() {
        // Move the ball
        this.ball.setTranslateX(this.ball.getTranslateX() + dx);
        this.ball.setTranslateY(this.ball.getTranslateY() + dy);
        
        Bounds bounds = this.ball.getBoundsInParent();
        Pane canvas = (Pane)this.ball.getParent();
        final boolean atTopBorder = bounds.getMinY() <= 0;
        final boolean atRightBorder = bounds.getMaxX() >= canvas.getWidth();
        final boolean atBottomBorder = bounds.getMaxY() >= canvas.getHeight();
        final boolean atLeftBorder = bounds.getMinX() <= 0;
        
        final int atBrick = this.bricks.checkCollision(this.ball);
        final int atPaddle = this.paddle.checkCollision(this.ball);
        
        // Calculate angle of reflection (bounce the ball off things)
        if(atLeftBorder || atRightBorder || atBrick == -1) dx *= -1;
        if(atTopBorder || atBrick == 1 || atPaddle == 1) dy *= -1;
        
        // If the ball hits the bottom of the screen, the player loses a life
        // Notify any attached loss listeners
        if(atBottomBorder) {
            for(LevelListener ls : this.lossListeners) {
                ls.handleLevelingEvent();
            }
        }
    }
    
    /**
     * Convenience method to access the internal node's getTranslateX() method.
     *
     * @return The Circle's translateX property.
     */
    public double getTranslateX() { 
        return this.ball.getTranslateX(); 
    }

    /**
     * Convenience method to access the internal node's getTranslateY() method.
     *
     * @return The Circle's translateY property.
     */    
    public double getTranslateY() { 
        return this.ball.getTranslateY(); 
    }
 
     /**
     * Convenience method to access the internal node's setTranslateX() method.
     *
     * @param x The x-axis value to set.
     */   
    public void setTranslateX(double x) { 
        this.ball.setTranslateX(x); 
    }
    
    /**
     * Convenience method to access the internal node's setTranslateY() method.
     *
     * @param y The y-axis value to set.
     */     
    public void setTranslateY(double y) { 
        this.ball.setTranslateY(y); 
    }
    
    /**
     * Returns the internal node held by this handler.
     *
     * @return The held Circle object.
     */
    public Circle getNode() {
        return this.ball;
    }
    
}
