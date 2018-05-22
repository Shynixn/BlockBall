Commandslist
============

/blockball
~~~~~~~~~~

This administration command opens the chat UI to modify and create new games.

* Players (require permissions) ✔
* Server console ✘
* Command blocks ✘

/blockballstop <id/name>
~~~~~~~~~~~~~~~~~~~~~~~~

This administration command stops the game with the given id or name.

* Players (require permissions) ✔
* Server console ✔
* Command blocks ✔

/blockballreload
~~~~~~~~~~~~~~~~

This administration command reloads the Blockball configuration and restarts all games.

* Players (require permissions) ✔
* Server console ✔
* Command blocks ✔

/bbjoin <game>
~~~~~~~~~~~~~~

This user command allows to join games by entering the name or displayname of the arena. You can find the permissions
at the `permission list <../gettingstarted/permissions.html#permissionlist>`__.

* Players (require permissions) ✔
* Server console ✘
* Command blocks ✘

**Sample list:**

Join the arena by the id '1':
::
  /bbjoin 1

Join the arena by the name 'stadium':
::
  /bbjoin stadium

Join the arena by the name 'awesome stadium':
::
  /bbjoin awesome stadium

Join the arena with id '1' and the team with name 'Team Red':
::
  /bbjoin 1|team red

Join the arena with id '1' and the team with name 'Team Red':
::
  /bbjoin 1/team red

Join the arena with name 'awesome stadium' and the team with name 'Team Spongy':
::
  /bbjoin awesome stadium|team spongy

Join the arena with name 'awesome stadium' and the team with name 'Team Spongy':
::
  /bbjoin awesome stadium/team spongy

/bbleave
~~~~~~~~

This user command allows players to leave games.

* Players (require permissions) ✔
* Server console ✘
* Command blocks ✘


