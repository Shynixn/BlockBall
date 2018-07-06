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
::
    <redcolor><redscore> : <bluecolor><bluescore>

**Placed ingame message**:
::
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


**Stats Scoreboard**

======================================================================   =======
Description                                                              Placeholder
======================================================================   =======
Uses the name of the player belonging the stats                          <playername>
Uses the winrate value of the player                                     <winrate>
Uses the amount of played games of the player                            <playedgames>
Uses the amount of scored goals of the player                            <goalspergame>
======================================================================   =======

**BungeeCord Joining Signs**

======================================================================   =======
Description                                                              Placeholder
======================================================================   =======
Uses the name of the target server                                       <server>
======================================================================   =======

**Placeholder API**

It is also possible to redirect some values to the PlaceHolder API plugin.

Simply use the **Games** placeholders and append the prefix **%blockball_**.

======================================================================   =======
Description                                                              Placeholder
======================================================================   =======
Uses the name of the blue team                                           %blockball_<blue>
Uses the prefix (default color) of the blue team                         %blockball_<bluecolor>
Uses the current score of the blue team                                  %blockball_<bluescore>
Uses the game's displayname                                              %blockball_<game>
Uses the name of the player who was the last player touching the ball    %blockball_<player>
Uses the name of the red team                                            %blockball_<red>
Uses the prefix (default color) of the red team                          %blockball_<redcolor>
Uses the current score of the red team                                   %blockball_<redscore>
Uses the game's remaining players amount to join in order to start       %blockball_<remaining>
Uses the game's state (Enabled,Running,Disabled)                         %blockball_<state>
Uses the game's current amount of players value                          %blockball_<sumplayers>
Uses the game's max amount of players value                              %blockball_<summaxplayers>
Uses the game's current time value                                       %blockball_<time>
======================================================================   =======
