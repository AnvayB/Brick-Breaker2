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

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.layout.Region;

/**
 * This class handles brick collision with a given ball, as well as damage values for the bricks.
 */
public class Bricks {
    
    private GridPane bricks;
    private List<Node> brickList;
    private int[][] damage;
    private int bricksCleared;
    
    private final List<String> damageStyles = Arrays.asList("damage-1", "damage-2", "damage-3");
    private List<LevelListener> winListeners = new ArrayList<LevelListener>();

    /**
     * Creates a Bricks handler object.
     *
     * @param bricks The GridPane object that holds the aforementioned bricks.
     */
    public Bricks(GridPane bricks) {
        this.bricks = bricks;
        this.brickList = bricks.getChildren();
        this.damage = new int[4][6];
    }
    
    /**
     * Adds a listener object that is called by increaseDamange() when the player clears all bricks.
     *
     * @param newListener A LevelListener object to attach.
     */
    public void addWinListener(LevelListener newListener) {
        this.winListeners.add(newListener);
    }
    
    /**
     * Checks for a collision between the given ball and any brick in our GridPane.
     *
     * @param ball The ball to check for collisions against.
     * @return 1 for a horizontal hit, -1 for a vertical hit, and 0 for no hit.
     */
    public int checkCollision(Circle ball) {
        Bounds ballBounds = ball.getBoundsInParent();
        final double ballMinX = ballBounds.getMinX();
        final double ballMinY = ballBounds.getMinY();
        final double ballMaxX = ballBounds.getMaxX();
        final double ballMaxY = ballBounds.getMaxY();
        
        final boolean atBricksTop = ballMinY >= this.bricks.getLayoutY() - ball.getRadius() * 2;
        final boolean atBricksBottom = ballMaxY <= (this.bricks.getLayoutY() + this.bricks.getHeight()) + ball.getRadius() * 2;
        
        // Only check for collisions if the ball is near the brick field
        if (atBricksTop && atBricksBottom) {
            // Check in reverse from bottom to top for speed
            for(int i = this.brickList.size() - 1; i >= 0; i--) {
                Region brick = (Region)this.brickList.get(i);
                // Skip already broken bricks
                if(!brick.isVisible()) {
                    continue;
                }
                
                // Precalculate bounds
                Bounds brickBounds = brick.getBoundsInParent();
                final double brickMinX = this.bricks.getLayoutX() + brickBounds.getMinX();
                final double brickMinY = this.bricks.getLayoutY() + brickBounds.getMinY();
                final double brickMaxX = brickMinX + brickBounds.getWidth();
                final double brickMaxY = brickMinY + brickBounds.getHeight();
                
                final boolean insideX = ballMaxX >= brickMinX && ballMinX <= brickMaxX;
                final boolean insideY = ballMaxY >= brickMinY && ballMinY <= brickMaxY;
                
                // Verify that the ball is touching/colliding with the current brick
                if(insideX && insideY) {
                    final boolean atTop = ballMinY < brickMinY;
                    final boolean atBottom = ballMaxY > brickMaxY;
                    final boolean atLeft = ballMinX < brickMinX;
                    final boolean atRight = ballMaxX > brickMaxX;
                    
                    if(atTop || atBottom) {
                        increaseDamage(brick);
                        return 1;
                    }
                    if(atLeft || atRight) {
                        increaseDamage(brick);
                        return -1;
                    }
                }
            }
        }
        
        return 0;
    }
    
    /** 
     * Increments the damage level of the given brick.
     *
     * @param brick The Region object representing the brick.
     */
    public void increaseDamage(Region brick) {
        List<String> styles = brick.getStyleClass();
        // Get row & col from style classes
        int row = Integer.valueOf(styles.get(0).substring(4));
        int col = Integer.valueOf(styles.get(1).substring(4));
        
        this.damage[row][col]++;
        // Damage of 3 indicates a broken brick
        if(this.damage[row][col] == 3) {
            brick.setVisible(false);
            this.bricksCleared++;
            
            if(isCleared()) {
                // Notify any attached listeners of a win
                for(LevelListener ls : winListeners) {
                    ls.handleLevelingEvent();
                }
            }
        }
        
        // Remove all damage styles and add the new one
        styles.removeAll(this.damageStyles);
        styles.add("damage-" + this.damage[row][col]);
        
        // Debug
        System.out.println(Arrays.toString(styles.toArray()));
    }
    
    /**
     * Checks to see if all bricks have been broken.
     *
     * @return True if the brick field is clear, false if it isn't.
     */
    public boolean isCleared() {
        System.out.println(bricksCleared + " bricks cleared");
        return this.bricksCleared == this.brickList.size();
    }
    
    /**
     * Resets the brick field and removes all damage.
     */
    public void reset() {
        for(Node brick : this.brickList) {
            brick.setVisible(true);
            brick.getStyleClass().removeAll(this.damageStyles);
        }
        
        this.damage = new int[4][6];
        this.bricksCleared = 0;
    }
    
}
