Rewards
=======

It is always a good game design to reward your players for certain action. So does BlockBall support this and you can give your players
certain rewards for the following actions.

* Winning a match
* Participating in a match
* Shooting a goal
* Loosing a match

Configuring ingame
~~~~~~~~~~~~~~~~~~

There are 2 available ways how to reward your players. Via **money** or by using **commands**.

At this stage, you should be able to open the 'Settings' page of your arena chat UI. If not, please take a look at this page.

1. Go to the **Settings Page** of your arena
2. Open the **Rewards Page**

.. image:: ../_static/images/reward1.jpg

1. Let's start by adding a money reward for your players. Make sure you are using an **economy plugin** on your server which supports Vault and of course installed the **plugin Vault**.
2. Press on select and choose an action to bind the reward to. For example the action named winning match belongs to the winning team.

.. image:: ../_static/images/reward2.jpg

3. In this example each player of the winning team receives 5 potatos on win. (this is the plural name of the currency used on this server). You can also see that the connection with Vault is working when your currency name displays correctly.

1. Now let's continue with adding a command reward for our players. You can also use this option to execute commands on your server when this action appears. It does not have to be a reward at all.
2. Press on select and choose an action to bind the reward to. For example the action named winning match belongs to the winning team.

.. image:: ../_static/images/reward3.jpg

3. In this example the server console executes the command */say Hello World* with console permissions when any team wins the match


**Command Modes**

There are 3 different ways to execute your commands.

* CONSOLE_SINGLE: The console executes a single command when the action appears. There are no permission problems.
* COMMAND_PER_PLAYER: The console executes a command for each player and replaces the placeholder *<player>* with the name of each player. There are no permission problems.
* PER_PLAYER: Each player automatically executes the command. The player has to have got the permission to the command.

You can set the command to **none** to disable it.

Configuring in your arena_x.yml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

At this stage, you should be able to find your arena save file. If not, please take a look at this page.

1. Go to the **Open the arena_x.yml** of your arena
2. Search for the following section:

**arena_x.yml**
::
    reward-meta:
      money-reward:
        WIN_MATCH: 5
        SHOOT_GOAL: 0
        PARTICIPATE_MATCH: 2
        LOOSING_MATCH: 0
      command-reward:
        WIN_MATCH:
          mode: CONSOLE_SINGLE
          command: '/say Hello World'
        SHOOT_GOAL:
          mode: PER_PLAYER
          command: 'none'
        PARTICIPATE_MATCH:
          mode: CONSOLE_PER_PLAYER
          command: 'none'
        LOOSING_MATCH:
          mode: CONSOLE_SINGLE
          command: 'none'

3. Customize all available options to your own needs.