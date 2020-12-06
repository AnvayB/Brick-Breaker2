
package application;

/**
 * Used for implementing win/loss handlers.
 *
 * See Ball.addLossListener and Bricks.addWinListener for more details.
 */
public interface LevelListener {
    
    /**
     * Generic method to be implemented in handlers.
     */
    void handleLevelingEvent();
    
}
