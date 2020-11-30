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

import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Label;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 * This class is the controller for the game. All 
 * game-specific functions are in here.
*/
public class Breakout extends Engine {
    
    private boolean gameStarted, ballLaunched = false;
    final private int levelCount = 5;
    final private int maxLives = 5;
    final private double initialSpeed = 4;
    final private double paddleSpeed = 37d/3d;
    
    private int level = 1;
    private int livesRemaining = maxLives;
    
    private Group[] groups;
    private Label levelInd, livesInd, levelIndIn, livesIndIn;
    private Bricks bricks;
    private Paddle paddle;
    private Ball ball;
    
    private BackgroundMusic bgm;
    
    /**
     * Creates a new Breakout game.
     * 
     * @param stage The primary Stage of the JavaFX application.
     * @param rootNode The root node of the FXML document.
     */
    public Breakout(Stage stage, Scene scene) {
        super(stage, scene, "Breakout!", 60);
        
        // Lookup game nodes
        this.groups = new Group[5];
        this.groups[0] = (Group)scene.lookup("#titleGroup");
        this.groups[1] = (Group)scene.lookup("#gameGroup");
        this.groups[2] = (Group)scene.lookup("#levelInterstitial");
        this.groups[3] = (Group)scene.lookup("#winInterstitial");
        this.groups[4] = (Group)scene.lookup("#lostInterstitial");
        
        this.levelInd = (Label)scene.lookup("#level");
        this.livesInd = (Label)scene.lookup("#lives");
        this.levelIndIn = (Label)scene.lookup("#levelIn");
        this.livesIndIn = (Label)scene.lookup("#livesIn");
        
        this.bricks = new Bricks((GridPane)scene.lookup("#bricks"));
        this.paddle = new Paddle((Rectangle)scene.lookup("#paddle"));
        this.ball = new Ball((Circle)scene.lookup("#ball"), this.bricks, this.paddle);
        
        // Add win/loss listeners
        this.bricks.addWinListener(this::levelUp);
        this.ball.addLossListener(this::loseLife);
        
        // Handle non-constant controls
        scene.addEventHandler(KeyEvent.KEY_PRESSED, this::toggleGameState);
        
        // Play title screen music
        this.bgm = new BackgroundMusic();
        this.bgm.setMusic(0);
        this.bgm.play();
    }

    @Override
    public void update(Engine game) {
        if(this.gameStarted) {
            // Animate paddle
            Boolean moved;
            if(game.getKeyPressed() == KeyCode.LEFT) {
                moved = this.paddle.animate(-paddleSpeed);
                // Move ball in sync with paddle if it hasn't been launched yet
                if(!this.ballLaunched && moved) this.ball.setTranslateX(this.ball.getTranslateX() - paddleSpeed);
            }
            if(game.getKeyPressed() == KeyCode.RIGHT) {
                moved = this.paddle.animate(paddleSpeed);
                if(!this.ballLaunched && moved) this.ball.setTranslateX(this.ball.getTranslateX() + paddleSpeed);
            }
            
            // Animate ball if it's been launched
            if(this.ballLaunched) {
                this.ball.animate();
            }
        }
    }
    
    /** 
     * Sets the player's number of lives remaining.
     *
     * @param num The new number of lives remaining.
     */
    private void setLives(int num) {
        this.livesRemaining = num;
        // Update info bar & interstitial screen
        this.livesInd.setText("" + this.livesRemaining);
        this.livesIndIn.setText("x " + this.livesRemaining);
    }
    
    /** 
     * Sets the player's current level in the game.
     *
     * @param The new level.
     */
    private void setLevel(int num) {
        this.level = num;
        // Update info bar & interstitial screen
        this.levelInd.setText("Level " + this.level);
        this.levelIndIn.setText("Level " + this.level);
    }
    
    /**
     * Switches the visible group. There are five to choose from: 
     * title screen, game canvas, level interstitial, victory screen, and game over screen.
     *
     * @param newGroup The group to make visible.
     */
    private void switchToGroup(int newGroup) {
        if(newGroup < this.groups.length && newGroup >= 0 && !isGroupVisible(newGroup)) {
            for(int i = 0; i < this.groups.length; i++) {
                // When looping over groups, only make the new group visible
                this.groups[i].setVisible(i == newGroup);
            }
        }
    }
    
    /**
     * Checks if a given group is visible.
     *
     * @param i The group to check.
     * @return True if the group exists, false if it doesn't.
     */
    private boolean isGroupVisible(int i) {
        if(i < this.groups.length && i >= 0) {
            return this.groups[i].isVisible();
        }
        else {
            return false;
        }
    }
    
    /**
     * Shows a given group for 2 seconds before switching to 
     * the second group. Useful for interstitial screens, hence
     * the name.
     * 
     * @param first The first group to show.
     * @param second The second group to show.
     */
    private void showInterstitial(int first, int second) {
        switchToGroup(first);
        
        // Sleep in async task to play nice with JavaFX's threading model
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(2000);
                } 
                catch (InterruptedException e) {}
                return null;
            }
        };
        sleeper.setOnSucceeded(e -> switchToGroup(second));
        new Thread(sleeper).start();
    }
    
    /**
     * Resets the state of the player objects (paddle and ball.)
     *
     * @param startingSpeed The starting speed of the ball.
     */
    private void resetPlayer(double startingSpeed) {
        // Stop the ball's animation
        this.ballLaunched = false;
        
        this.paddle.setTranslateX(0);
        this.ball.setStartingSpeed(startingSpeed);
        this.ball.setTranslateX(0);
        this.ball.setTranslateY(0);
    }
    
    /**
     * Resets the entire level, including brick state.
     *
     * @param startingSpeed The starting speed of the ball.
     */    
    private void resetLevel(double startingSpeed) {
        this.bricks.reset();
        resetPlayer(startingSpeed);
    }
    
    /**
     * Starts a new game.
     */
    private void startGame() {
        // Show level interstitial first
        showInterstitial(2, 1);
        
        // Stop title music and start game
        this.bgm.stop();
        this.gameStarted = true;
    }
    
    /**
     * Stops a running game, or exits the program if one is not running.
     */
    private void stopGame() {
        // Stop game if one is in progress
        if(this.gameStarted) {
            this.gameStarted = false;
            
            // Start title music
            // Special music is played after winning the game
            if(this.level == this.levelCount + 1) {
                this.bgm.setMusic(1);
            }
            else {
                this.bgm.setMusic(0);
            }
            this.bgm.play();
            
            // Only show title screen here if the game was quit manually
            // levelUp and loseLife show interstitials on their own
            if(isGroupVisible(1)) {
                switchToGroup(0);
            }
            
            // Reset game canvas
            resetLevel(this.initialSpeed);
            setLives(this.maxLives);
            setLevel(1);
        }
        // Quit otherwise
        else {
            stop();
            this.stage.close();
        }
    }
    
    /**
     * Increases the player's level by one.
     */
    private void levelUp() {
        if(this.gameStarted) {
            setLevel(this.level + 1);
            
            // If the last level was won, show the victory screen
            if(this.level == this.levelCount + 1) {
                showInterstitial(3, 0);
                stopGame();
            }
            else {
                // Show the level interstitial
                showInterstitial(2, 1);
                
                // On the last level, play special music
                if(this.level == this.levelCount) {
                    this.bgm.setMusic(2);
                    this.bgm.play();
                }
                else {
                    this.bgm.stop();
                }
                
                resetLevel(this.level + 3);
            }
        }
        else {
            System.out.println("Invalid levelUp() event issued");
        }
    }
    
    /**
     * Decrements the player's level by one.
     */
    private void loseLife() {
        if(this.gameStarted) {
            setLives(this.livesRemaining - 1);

            // If player has lives left, show the level interstitial
            if(this.livesRemaining > 0) {
                showInterstitial(2, 1);
                resetPlayer(this.level + 3);
            }
            // Oh no! Show the game over screen
            else {
                showInterstitial(4, 0);
                stopGame();
            }
        }
        else {
            System.out.println("Invalid loseLife() event issued");
        }
    }
    
    /**
     * Handles global key events for starting a game, launching the ball, 
     * ending a game, ending the program, and cheat codes.
     *
     * @param KeyEvent The event from a KeyEvent handler.
     */
    private void toggleGameState(KeyEvent e) {
        KeyCode currentKey = e.getCode();
        
        switch(currentKey) {
            case SPACE:
                // Start game
                // Do not allow startGame if the victory screen is shown
                if(!this.gameStarted && !isGroupVisible(3)) {
                    startGame();
                }
                // Launch ball
                // Do not allow if the level interstitial is shown
                else if(this.gameStarted && !this.ballLaunched && !isGroupVisible(2)) {
                    this.ballLaunched = true;
                }
                break;
            case ESCAPE:
                // Only allow on title screen and game canvas
                if(isGroupVisible(0) || isGroupVisible(1)) {
                    stopGame();
                }
                break;
//            case DIGIT1:
//            case DIGIT2:
//            case DIGIT3:
//            case DIGIT4:
//            case DIGIT5:
//            case NUMPAD1:
//            case NUMPAD2:
//            case NUMPAD3:
//            case NUMPAD4:
//            case NUMPAD5:
//                // Allow cheat codes on title screen
//                if(!this.gameStarted) startGame();
//                // Set to level - 1 because levelUp will increment
//                this.level = Integer.valueOf(e.getText()) - 1;
//                // Set max lives
//                setLives(this.maxLives);
//                levelUp();
        }
    }

}
