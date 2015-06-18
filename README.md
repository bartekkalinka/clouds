Clouds
======

Simple 2D java game featuring jumping from cloud to cloud to get somewhere.

Features include: random cloud shape generation (from square tiles) and  collision detection (between player and a number of clouds).

Written by Bartek Kalinka, in Ruby in 2011, then ported to Java in 2012.  

Added to github for further experiments with gameplay, branching into other games and trying out some unrelated dev techniques.

#### How to build and run

cd clouds_main

mvn install

cd ..

clouds.cmd (on windows)
./clouds.sh (on linux)

#### How to play

Goal: Find the golden cloud and jump on top of it. 

Hint: Golden cloud emits yellow tiles. 

Controls: Arrows for right, left and jump. 

More info: Big clouds fly slower, small quicker. With player on top, cloud loses energy 
and eventually begins to fall. Big clouds have a lot of energy, small have little. 
