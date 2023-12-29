Placeholders
============

Placeholders are being used in BlockBall to be replace by ingame values. These placeholders should be
supported by **all** messages you can find ingame. If there are parsing issues where the following placeholders
cannot be resolved, please report them.

You can use placeholders on:

* Messages
* Signs
* Scoreboards
* Bossbars
* Holograms
* ...

Samples
~~~~~~~

**Title message**:

.. code-block:: text

    <redcolor><redscore> : <bluecolor><bluescore>

**Placed ingame message**:

.. code-block:: text

    &c2 : &93

You can see that redcolor gets replaced by the ChatColor red and the redscore with 2.

Placeholderlist
~~~~~~~~~~~~~~~

**Games**

======================================================================   =======
Description                                                              Placeholder
======================================================================   =======
Uses the name of the blue team                                           <blue>
Uses the prefix (default color) of the blue team                         <bluecolor>
Uses the current score of the blue team                                  <bluescore>
Uses the game's displayname                                              <game>
Uses the name of the player who was the last player touching the ball    <player>
Uses the name of the red team                                            <red>
Uses the prefix (default color) of the red team                          <redcolor>
Uses the current score of the red team                                   <redscore>
Uses the game's remaining players amount to join in order to start       <remaining>
Uses the game's state (Enabled,Running,Disabled)                         <state>
Uses the game's current amount of players value                          <sumplayers>
Uses the game's max amount of players value                              <summaxplayers>
Uses the game's current time value                                       <time>
======================================================================   =======

**Games (Using the team of the player receiving the message)**

======================================================================   =======
Description                                                              Placeholder
======================================================================   =======
Uses the name of the team                                                <team>
Uses the prefix (default color) of the team                              <teamcolor>
Uses the teams's current amount of players value                         <players>
Uses the teams's max amount of players value                             <maxplayers>
======================================================================   =======

**BungeeCord Joining Signs**

======================================================================   =======
Description                                                              Placeholder
======================================================================   =======
Uses the name of the target server                                       <server>
======================================================================   =======

**Placeholder API**

It is also possible to redirect some values to the `PlaceHolder API plugin. <https://www.spigotmc.org/resources/placeholderapi.6245/>`__.

Simply use the **Games** placeholders and append the prefix **%blockball_** and **sometimes the** suffix **_<id>**.
Replace <id> with the id of the arena.

Sample placeholder for the current amount of players in arena 1: **%blockball_<sumplayers>_1%**

======================================================================   =======
Description                                                              Placeholder
======================================================================   =======
Uses the name of the blue team                                           %blockball_<blue>_<id>%
Uses the prefix (default color) of the blue team                         %blockball_<bluecolor>_<id>%
Uses the current score of the blue team                                  %blockball_<bluescore>_<id>%
Uses the game's displayname                                              %blockball_<game>_<id>%
Uses the name of the player who was the last player touching the ball    %blockball_<player>_<id>%
Uses the name of the red team                                            %blockball_<red>_<id>%
Uses the prefix (default color) of the red team                          %blockball_<redcolor>_<id>%
Uses the current score of the red team                                   %blockball_<redscore>_<id>%
Uses the game's remaining players amount to join in order to start       %blockball_<remaining>_<id>%
Uses the game's state (Enabled,Running,Disabled)                         %blockball_<state>_<id>%
Uses the game's current amount of players value                          %blockball_<sumplayers>_<id>%
Uses the game's max amount of players value                              %blockball_<summaxplayers>_<id>%
Uses the game's current time value                                       %blockball_<time>_<id>%
Uses the name of the current team of the player                          %blockball_<team>_<id>%
Uses the prefix (default color) of the current team of the player        %blockball_<teamcolor>_<id>%
Uses the current amount of players in the current team of the player     %blockball_<players>_<id>%
Uses the max amount of players in the current team of the player         %blockball_<maxplayers>_<id>%
======================================================================   =======
