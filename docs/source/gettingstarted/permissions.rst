Configuring Permissions
=======================

.. toctree::

Before you start using BlockBall on your server, you should also configure the permissions for your players.

Admin
~~~~~

These are the recommend permissions for administrators or moderators.

=======================================================  =======
Description                                              Permission
=======================================================  =======
Allows to use the **/blockball** administration command  blockball.command.admin
Allows to use the **/blockballreload** command           blockball.command.reload
=======================================================  =======

Users
~~~~~

There are 2 different ways how to setup your permissions. You can allow all of your players to join all games or
you can allow certain groups or players join certain games.

**Sample 1 for full access to all games.**

=======================================================  ================================================
Description                                              Permission
=======================================================  ================================================
Allows to use the **/bbjoin** and **/bbleave** command   blockball.command.use
Allows to join all games                                 blockball.games.all
=======================================================  ================================================


**Sample 2 for restricted access to games.**

=======================================================  ================================================
Description                                              Permission
=======================================================  ================================================
Allows to use the **/bbjoin** and **/bbleave** command   blockball.command.use
Allows to join the game named 'soccerarena'              blockball.games.soccerarena
Allows to join the game named 'blockballgame127'         blockball.games.blockballgame127
=======================================================  ================================================

Permissionlist
~~~~~~~~~~~~~~

**Permissions for commands**

========================================================  =======
Description                                               Permission
========================================================  =======
Allows to use the **/blockball** administration command   blockball.command.admin
Allows to use the **/blockballreload** command            blockball.command.reload
Allows to use the **/bbjoin** and **/bbleave** command    blockball.command.use
========================================================  =======

**Permissions for actions**

===================================================  =======
Description                                          Permission
===================================================  =======
Allows to join a single game                         blockball.games.<name>
Allows to join all games                             blockball.games.all
===================================================  =======