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

.. code-block:: text

 /bbjoin 1

Join the arena by the name 'stadium':

.. code-block:: text

  /bbjoin stadium

Join the arena by the name 'awesome stadium':

.. code-block:: text

  /bbjoin awesome stadium

Join the arena with id '1' and the team with name 'Team Red':

.. code-block:: text

  /bbjoin 1|team red

Join the arena with id '1' and the team with name 'Team Red':

.. code-block:: text

  /bbjoin 1/team red

Join the arena with name 'awesome stadium' and the team with name 'Team Spongy':

.. code-block:: text

  /bbjoin awesome stadium|team spongy

Join the arena with name 'awesome stadium' and the team with name 'Team Spongy':

.. code-block:: text

  /bbjoin awesome stadium/team spongy

/bbspectate <game>
~~~~~~~~~~~~~~~~~~

This user command allows to spectate games by entering the name or displayname of the arena. You can find the permissions
at the `permission list <../gettingstarted/permissions.html#permissionlist>`__.

* Players (require permissions) ✔
* Server console ✘
* Command blocks ✘

/bbleave
~~~~~~~~

This user command allows players to leave games.

* Players (require permissions) ✔
* Server console ✘
* Command blocks ✘


