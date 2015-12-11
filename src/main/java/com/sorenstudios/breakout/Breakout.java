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

import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Label;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 * This class is the controller for the game. All 
 * game-specific functions are in here.
*/
public class Breakout extends Game {
    
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
    public Breakout(Stage stage, Parent rootNode) {
        super(stage, "Breakout!", 60, 640, 600);
        
        // Required to initialize FXML early
        Scene scene = getScene();
        scene.setRoot(rootNode);
        
        // Lookup game nodes
        groups = new Group[5];
        groups[0] = (Group)scene.lookup("#titleGroup");
        groups[1] = (Group)scene.lookup("#gameGroup");
        groups[2] = (Group)scene.lookup("#levelInterstitial");
        groups[3] = (Group)scene.lookup("#winInterstitial");
        groups[4] = (Group)scene.lookup("#lostInterstitial");
        
        levelInd = (Label)scene.lookup("#level");
        livesInd = (Label)scene.lookup("#lives");
        levelIndIn = (Label)scene.lookup("#levelIn");
        livesIndIn = (Label)scene.lookup("#livesIn");
        
        bricks = new Bricks((GridPane)scene.lookup("#bricks"));
        paddle = new Paddle((Rectangle)scene.lookup("#paddle"));
        ball = new Ball((Circle)scene.lookup("#ball"), bricks, paddle);
        
        // Add win/loss listeners
        bricks.addWinListener(() -> levelUp());
        ball.addLossListener(() -> loseLife());
        
        // Handle non-constant controls
        scene.addEventHandler(KeyEvent.KEY_PRESSED, this::toggleGameState);
        
        // Play title screen music
        bgm = new BackgroundMusic();
        bgm.setMusic(0);
        bgm.play();
    }

    @Override
    public void update(Game game, GameTime gameTime) {
        if(gameStarted) {
            KeyManager km = game.getKeyManager();
            
            // Animate paddle
            Boolean moved;
            if(km.isKeyPressed(KeyCode.LEFT)) {
                moved = paddle.animate(-paddleSpeed);
                // Move ball in sync with paddle if it hasn't been launched yet
                if(!ballLaunched && moved) ball.setTranslateX(ball.getTranslateX() - paddleSpeed);
            }
            if(km.isKeyPressed(KeyCode.RIGHT)) {
                moved = paddle.animate(paddleSpeed);
                if(!ballLaunched && moved) ball.setTranslateX(ball.getTranslateX() + paddleSpeed);
            }
            
            // Animate ball if it's been launched
            if(ballLaunched) {
                ball.animate();
            }
        }
    }
    
    /** 
     * Sets the player's number of lives remaining.
     *
     * @param num The new number of lives remaining.
     */
    private void setLives(int num) {
        livesRemaining = num;
        // Update info bar & interstitial screen
        livesInd.setText("" + livesRemaining);
        livesIndIn.setText("x " + livesRemaining);
    }
    
    /** 
     * Sets the player's current level in the game.
     *
     * @param The new level.
     */
    private void setLevel(int num) {
        level = num;
        // Update info bar & interstitial screen
        levelInd.setText("Level " + level);
        levelIndIn.setText("Level " + level);
    }
    
    /**
     * Switches the visible group. There are five to choose from: 
     * title screen, game canvas, level interstitial, victory screen, and game over screen.
     *
     * @param newGroup The group to make visible.
     */
    private void switchToGroup(int newGroup) {
        if(newGroup < groups.length && newGroup >= 0 && !isGroupVisible(newGroup)) {
            for(int i = 0; i < groups.length; i++) {
                if(i == newGroup) {
                    groups[i].setVisible(true);
                }
                else {
                    groups[i].setVisible(false);
                }
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
        if(i < groups.length && i >= 0) {
            return groups[i].isVisible();
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
        ballLaunched = false;
        
        paddle.setTranslateX(0);
        ball.setStartingSpeed(startingSpeed);
        ball.setTranslateX(0);
        ball.setTranslateY(0);
    }
    
    /**
     * Resets the entire level, including brick state.
     *
     * @param startingSpeed The starting speed of the ball.
     */    
    private void resetLevel(double startingSpeed) {
        bricks.reset();
        resetPlayer(startingSpeed);
    }
    
    /**
     * Starts a new game.
     */
    private void startGame() {
        // Show level interstitial first
        showInterstitial(2, 1);
        
        // Stop title music and start game
        bgm.stop();
        gameStarted = true;
    }
    
    /**
     * Stops a running game, or exits the program if one is not running.
     */
    private void stopGame() {
        // Stop game if one is in progress
        if(gameStarted) {
            gameStarted = false;
            
            // Start title music
            // Special music is played after winning the game
            if(level == levelCount + 1) {
                bgm.setMusic(1);
            }
            else {
                bgm.setMusic(0);
            }
            bgm.play();
            
            // Only show title screen here if the game was quit manually
            // levelUp and loseLife show interstitials on their own
            if(isGroupVisible(1)) {
                switchToGroup(0);
            }
            
            // Reset game canvas
            resetLevel(initialSpeed);
            setLives(maxLives);
            setLevel(1);
        }
        // Quit otherwise
        else {
            javafx.application.Platform.exit();
        }
    }
    
    /**
     * Increases the player's level by one.
     */
    private void levelUp() {
        if(gameStarted) {
            setLevel(level + 1);
            
            // If the last level was won, show the victory screen
            if(level == levelCount + 1) {
                showInterstitial(3, 0);
                stopGame();
            }
            else {
                // Show the level interstitial
                showInterstitial(2, 1);
                
                // On the last level, play special music
                if(level == levelCount) {
                    bgm.setMusic(2);
                    bgm.play();
                }
                else {
                    bgm.stop();
                }
                
                resetLevel(level + 3);
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
        if(gameStarted) {
            setLives(livesRemaining - 1);

            // If player has lives left, show the level interstitial
            if(livesRemaining > 0) {
                showInterstitial(2, 1);
                resetPlayer(level + 3);
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
                if(!gameStarted && !isGroupVisible(3)) {
                    startGame();
                }
                // Launch ball
                // Do not allow if the level interstitial is shown
                else if(gameStarted && !ballLaunched && !isGroupVisible(2)) {
                    ballLaunched = true;
                }
                break;
            case ESCAPE:
                // Only allow on title screen and game canvas
                if(isGroupVisible(0) || isGroupVisible(1)) {
                    stopGame();
                }
                break;
            case DIGIT1:
            case DIGIT2:
            case DIGIT3:
            case DIGIT4:
            case DIGIT5:
            case NUMPAD1:
            case NUMPAD2:
            case NUMPAD3:
            case NUMPAD4:
            case NUMPAD5:
                // Allow cheat codes on title screen
                if(!gameStarted) startGame();
                // Set to level - 1 because levelUp will increment
                level = Integer.valueOf(e.getText()) - 1;
                // Set max lives
                setLives(maxLives);
                levelUp();
        }
    }

}
