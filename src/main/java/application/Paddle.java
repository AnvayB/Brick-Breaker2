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

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.layout.Pane;
import javafx.geometry.Bounds;

/**
 * This class handles paddle animation and provides a method to check collision against a given ball.
 */
public class Paddle {
    
    private Rectangle paddle;
    
    /**
     * Creates a new Paddle object.
     *
     * @param paddle The Rectangle object representing the paddle.
     */
    public Paddle(Rectangle paddle) {
        this.paddle = paddle;
    }
    
    /**
     * Animates the paddle at the given speed.
     * The paddle will not move outside of its parent's bounds.
     *
     * @param dx The speed in pixels per update at which to move the paddle horizontally.
     * @return True if the paddle was animated, false if it wasn't.
     */
    public boolean animate(double dx) {
        Bounds bounds = this.paddle.getBoundsInParent();
        final boolean atLeftBorder = bounds.getMinX() + dx <= 0;
        final boolean atRightBorder = bounds.getMaxX() + dx >= ((Pane)this.paddle.getParent()).getWidth();
        
        // Don't allow paddle to be moved outside of the game canvas
        // Return true if the paddle was moved
        if (!atLeftBorder && !atRightBorder) {
            this.paddle.setTranslateX(this.paddle.getTranslateX() + dx);
            return true;
        }
        else {
            return false;
        }
    }
    
    /** 
     * Checks if there is a collision between the paddle and the given ball.
     *
     * @param ball The Circle object representing the ball.
     * @return 1 if there is a collision, 0 if there isn't.
     */
    public int checkCollision(Circle ball) {
        Bounds ballBounds = ball.getBoundsInParent();
        Bounds paddleBounds = this.paddle.getBoundsInParent();
        
        final double insideY = ballBounds.getMaxY() - paddleBounds.getMinY();
        
        if(ballBounds.intersects(paddleBounds)) {
            // Quick & dirty hack to fix deep collisions
            if(insideY > 3) {
                ball.setTranslateY(-(insideY - 3));
            }
            
            return 1;
        }
        
        return 0;
    }   
    
    /**
     * Convenience method to access the internal node's getTranslateX() method.
     *
     * @return The Rectangle's translateX property.
     */
    public double getTranslateX() { 
        return this.paddle.getTranslateX(); 
    }
    
    /**
     * Convenience method to access the internal node's setTranslateX() method.
     *
     * @param x The x-axis value to set.
     */   
    public void setTranslateX(double x) { 
        this.paddle.setTranslateX(x); 
    }
    
    /**
     * Returns the internal node held by this handler.
     *
     * @return The held Rectangle object.
     */
    public Rectangle getNode() {
        return this.paddle;
    }
    
}
