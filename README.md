# Breakout

A reasonably simple clone of Breakout that uses JavaFX 8.

Licensed under the GNU General Public License v3 (or any later version.) See `LICENSE.txt` for more details.

## Compiling the game

This project requires that you have Java 8 and [Maven](https://maven.apache.org/) 3.0+ already installed.

#### To compile to a JAR file and run it:

    $ mvn package
    $ java -jar target/jfx/app/breakout-{VERSION}-jfx.jar

#### To compile + run it immediately (useful for development):

    $ mvn jfx:run

#### To build a native distributable/bundle:

    $ mvn jfx:native
    
 * Note that this will only build a native bundle for the platform you're executing on. E.g. if you want a Windows bundle, you'll have to build on Windows.