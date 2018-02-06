Commands
========

The articles in this section explain how to use the /petblocks administration commands.


.. toctree::


Rename Commands
~~~~~~~~~~~~~~~

Even though this fits more into customizing, it is important to know that you are allowed to rename commands:
::
    ############################

    # Command settings

    # Instead of setting the commands inside of the plugin.yml, I believe you should be able to customize
    # the whole command by yourself. Name, description, permission customize it to your needs.

    # petblocks-gui: Settings for the command to open the petblock-GUI
    # petblock-configuration: Settings for the command to manage petblock-configuration

    ############################
    petblocks-gui:
     enabled: true
     command: petblock
     useage: /<command>
     description: Opens the pet GUI.
     permission: petblocks.use
     permission-message: You don't have permission
    petblocks-configuration:
     enabled: true
     command: petblocks
     useage: /<command>
     description: Command to configure pets.
     permission: petblocks.commands
     permission-message: You don't have permission


Useage
~~~~~~

The commands displayed when using /petblocks are very special kinds of commands. They can be executed by the following things:

* Players (require permissions) ✔
* Server console ✔
* Command blocks ✔

Commandlist
~~~~~~~~~~~

This guide does not contain a full list of all commands as they are listed ingame with a very detail description.

Simply type **/petblocks <page>** (Example: /petblocks 2) to view the commands on this page.

Also all commands provide **tooltips by moving your mouse on top of them**.

Console or Command blocks
~~~~~~~~~~~~~~~~~~~~~~~~~

All commands can take an optional player parameter [player] to specify the player who involves the action. This optional parameter
has to be used when using console or command blocks.

The command gets executed by the server with server rights so it does not mess with permissions.


