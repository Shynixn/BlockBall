Commands
========

There are 3 commands for PetBlocks:

/petblock - Opens the pet GUI.

/petblocks - Admin configuration.

/petblockreload - Reloads the config.yml


Rename Commands
~~~~~~~~~~~~~~~

You are actually allowed to rename commands and change the permission message inside of the config.yml.

A full server restart (or reload) is required for the command changes to be applied.

**config.yml - before**
::
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



**config.yml - after**
::
    petblocks-gui:
      enabled: true
      command: pet
      useage: /<command>
      description: Opens the pet GUI.
      permission: petblocks.use
      permission-message: 'Premium Rank is required for pets!'
    petblocks-configuration:
      enabled: true
      command: petblocks
      useage: /<command>
      description: Command to configure pets.
      permission: petblocks.commands
      permission-message: You don't have permission














