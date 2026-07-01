# Changelog

## Release 7.41.2

### Bugs/Issues

* #749 Fixed throw-ins and corner kicks positions were not calculated correctly.

---

## Release 7.41.1

### Bugs/Issues

* #747 Fixed compatibility with Paper 26.2 builds.

---

## Release 7.41.0

### Changes

* #527 Replacing the entire ball physics engine with the new 2026 ball engine. 
* #527 All information can be found on this new [wiki page](https://shynixn.github.io/BlockBall/wiki/site/ball/)
* #527 The ball is no longer tied to the arena and can be configured in the new ``plugins/BlockBall/ball`` folder.
* #527 Added new ball properties and replaced existing ones to make it more realistic. 
* #527 The ball can now be configured to have a realistic bounce, spin, and friction.
* #527 The ball now bounces off walls and players in a more realistic way.
* #527 The ball can be configured to allow grabbing and throwing. A sample ball called hand_ball.yml is included in the ``plugins/BlockBall/ball`` folder.
* #527 Added configurable ball interactions. You can now configure to take sprinting, sneaking, selected hotbar slots, and more into account when interacting with the ball. e.g. the ball can be shot further while sprinting, the ball can do a left curve when the left slots are selected 
* #527 A sample ball called ``curve_ball.yml`` is available to see how to configure such interactions.
* #527 The [developer api](https://shynixn.github.io/BlockBall/wiki/site/api/) has changed. You can now spawn balls outside of games and use it for other minigames or purposes on your server.
* #744 Added support for Minecraft 26.2
* #743 Added a new flag to allow or deny block and items interactions during BlockBall in Minigame mode.

---

## Release 7.40.3

### Bugs/Issues

* #740 Fixed scoreboard race conditions which could cause players to see a wrong scoreboard.

---

## Release 7.40.2

### Changes

* #734 Fix club game reference property loading

---

## Release 7.40.1

### Bugs/Issues

* #732 Replace the goal kick and corner kick detection with dedicated out areas. Take a look at the updated  [wiki page](https://shynixn.github.io/BlockBall/wiki/site/game#advanced-features)  
