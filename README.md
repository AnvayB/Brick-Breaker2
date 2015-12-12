# Breakout

A reasonably simple clone of Breakout that uses JavaFX 8.

Licensed under the GNU General Public License v3 (or any later version.) See `LICENSE.txt` for more details.

## Compiling the game

This project requires that you have Java 8 and [Maven](https://maven.apache.org/) 3.0+ already installed.

#### To compile to a JAR file and run it:

    $ mvn jfx:jar
    $ java -jar target/jfx/app/breakout-{VERSION}-jfx.jar

#### To compile + run it immediately (useful for development):

    $ mvn jfx:run
