# Conchordance

This is a library for programmatically generating chords for guitar and similar instruments.

The advantage of generating chord fingerings programmatically, rather than storing them in a database, is that any tuning or instrument can be accomodated. The ChordEngine can just as easily reason about a guitar as Ukelele or a Bouzouki, or an instrument that hasn't been invented yet!

The challenge comes when trying to define rules to capture what the left hand can and can't do. That part of the logic is going to require the most tweaking and testing as this library evolves.

You can find a desktop application driven by this engine [here](https://github.com/jbauschatz/ChordUI).

## IDE Setup instructions

1. Install [gradle](http://www.gradle.org/)
2. Run the task for the IDE of your choice:
    * IntelliJ: `gradle idea`
    * Eclipse: `gradle eclipse`
3. Import the (now-existing) project into your IDE.

## Gradle tasks
* `gradle build` - builds the jar (to `./build/libs/conchordance-$version.jar`)
* `gradle test` - run all unit tests (this will be done as part of `build` as well)
* `gradle clean` - cleans out all build/compile artifacts
