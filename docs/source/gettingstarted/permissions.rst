Configuring Permissions
=======================

.. toctree::

Before you start using PetBlocks on your server, you should also configure the permissions for your players.

Please notice that the permissions changed at the beginning of 2018. `Click here <../updating/index.html>`_ to update your permissions if
these permissions do not match with your configuration.

Admin
~~~~~

These are the recommend permissions for administrators or moderators.

=======================================================  =======
Description                                              Permission
=======================================================  =======
Allows to use the **/petblocks** administration command  petblocks.command.admin
Allows to use the **/petblockreload** command            petblocks.command.reload
=======================================================  =======

Users
~~~~~

There are many different ways how to setup your permissions. You can split it up in groups, ranks, etc.
These 2 examples only show the most common setups:

**Sample 1 for full access to all options the plugin offers.**

===================================================  ================================================
Description                                          Permission
===================================================  ================================================
Allows to use the /petblock command                  petblocks.command.use
Allows to rename your pet                            petblocks.action.rename
Allows to use any skin what a player enters in chat  petblocks.action.customskin
Allows to ride your pet                              petblocks.action.ride
Allows to wear your pet                              petblocks.action.wear
Allows to shoot your pet                             petblocks.action.cannon
Allows to use all pet engines                        petblocks.selection.engines.all
Allows to use all simple block costumes              petblocks.selection.simpleblockcostumes.all
Allows to use all colored block costumes             petblocks.selection.coloredblockcostumes.all
Allows to use all player head costumes               petblocks.selection.playerheadcostumes.all
Allows to use all pet costumes                       petblocks.selection.petcostumes.all
Allows to use all particles                          petblocks.selection.particles.all
===================================================  ================================================


**Sample 2 for restricted access to costumes.**

===================================================  ================================================
Description                                          Permission
===================================================  ================================================
Allows to use the /petblock command                  petblocks.command.use
Allows to rename your pet                            petblocks.action.rename
Allows to use any skin what a player enters in chat  petblocks.action.customskin
Allows to ride your pet                              petblocks.action.ride
Allows to wear your pet                              petblocks.action.wear
Allows to shoot your pet                             petblocks.action.cannon
Allows to use all pet engines                        petblocks.selection.engines.all
Allows to use the simple block costume at slot 3     petblocks.selection.simpleblockcostumes.3
Allows to use the simple block costume at slot 5     petblocks.selection.simpleblockcostumes.5
Allows to use all colored block costumes             petblocks.selection.coloredblockcostumes.all
Allows to use all particles                          petblocks.selection.particles.all
===================================================  ================================================

Permissionlist
~~~~~~~~~~~~~~

**Permissions for commands**

===================================================  =======
Description                                          Permission
===================================================  =======
Allows to use the /petblock command                  petblocks.command.use
Allows to use the /petblocks administration command  petblocks.command.admin
Allows to use the /petblockreload command            petblocks.command.reload
===================================================  =======

**Permissions for actions**

===================================================  =======
Description                                          Permission
===================================================  =======
Allows to rename your pet                            petblocks.action.rename
Allows to use any skin what a player enters in chat  petblocks.action.customskin
Allows to ride your pet                              petblocks.action.ride
Allows to wear your pet                              petblocks.action.wear
Allows to shoot your pet                             petblocks.action.cannon
===================================================  =======

**Permissions for selections**

===================================================  =======
Description                                          Permission
===================================================  =======
Allows to use all pet engines                        petblocks.selection.engines.all
Allows to use a single pet engine                    petblocks.selection.engines.<number>
Allows to use all simple block costumes              petblocks.selection.simpleblockcostumes.all
Allows to use a single simple block costume          petblocks.selection.simpleblockcostumes.<number>
Allows to use all colored block costumes             petblocks.selection.coloredblockcostumes.all
Allows to use a single colored block costume         petblocks.selection.coloredblockcostumes.<number>
Allows to use all player head costumes               petblocks.selection.playerheadcostumes.all
Allows to use a single player head costume           petblocks.selection.playerheadcostumes.<number>
Allows to use all pet costumes                       petblocks.selection.petcostumes.all
Allows to use a single pet costume                   petblocks.selection.petcostumes.<number>
Allows to use all particles                          petblocks.selection.particles.all
Allows to use a single particle                      petblocks.pet.particles.<number
Allows to use all head database costumes             petblocks.selection.headdatabasecostumes.all
===================================================  =======






