
package application;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaException;
import java.net.URISyntaxException;

/**
 * Used for playing pre-set background music.
 */
public class BackgroundMusic {
    
    private MediaPlayer player;
    private boolean playbackAllowed = true;
    
    private String[] slots = {
        "UnderOurSpell.mp3",
        "GreatFairyFountain.mp3",
        "PursuitCornered.mp3"
    };
    
    /**
     * Sets the background music (BGM) slot to be played.
     * There are three slots to choose from, indexed from 0-2.
     *
     * @param selection The BGM slot to play.
     */
    public void setMusic(int selection) {
        if(this.playbackAllowed) {
            // Stop music before setting a new track
            stop();
            
            if(selection < this.slots.length && selection >= 0) {
                String path = "";
                try {
                    path = getClass().getResource("/music/" + this.slots[selection]).toURI().toString();
                }
                catch(URISyntaxException | NullPointerException ex) {
                    System.out.println("Specified music does not exist.");
                    return;
                }
                
                // Dispose of old this.player if one exists
                if(this.player != null) this.player.dispose();
                
                // Initialize new this.player
                // (MediaPlayer objects are immutable once created)
                try {
                    this.player = new MediaPlayer(new Media(path));
                    // Loop infinitely
                    this.player.setCycleCount(-1);
                }
                catch(MediaException ex) {
                    // Disable audio playback where it's unsupported
                    System.out.println("Your environment does not support audio playback. Music in this game has been disabled.");
                    this.playbackAllowed = false;
                }
                
            }
            else {
                System.out.println("BGM slot does not exist");
            }
        }
    }
    
    /**
     * Plays the set background music, but only if setMusic() was called beforehand.
     */
    public void play() {
        if(this.player != null) this.player.play();
    }

    /**
     * Stops the set background music, but only if setMusic() was called beforehand.
     */    
    public void stop() {
        if(this.player != null) this.player.stop();
    }

    /**
     * Determines if background music is currently playing.
     *
     * @return True if there is music playing, false if there isn't.
     */    
    public boolean isPlaying() {
        if(this.player != null) {
            return this.player.getStatus() == MediaPlayer.Status.PLAYING;
        }
        else {
            return false;
        }
    }
    
}
