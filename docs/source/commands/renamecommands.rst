Rename Commands
~~~~~~~~~~~~~~~

You are also allowed to rename the user commands /bbjoin and /bbleave in the config.yml. You can even customize the permissions.

**config.yml**

.. code-block:: yaml

    ############################

    # Command settings

    # Instead of setting the commands inside of the plugin.yml, I believe you should be able to customize
    # the whole command by yourself. Name, description, permission customize it to your needs.

    # global-join: Settings for the command to join games from any world on your server.
    # global-leave: Settings for the command to leave games from any world on your server.

    ############################

    global-join:
      enabled: true
      command: bbjoin
      useage: /<command> <game> <team>
      description: Join a game.
      permission: blockball.command.use
      permission-message: You don't have permission
    global-leave:
      enabled: true
      command: bbleave
      useage: /<command>
      description: Leaves current game.
      permission: blockball.command.use
      permission-message: You don't have permission
