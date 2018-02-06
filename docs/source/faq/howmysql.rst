How to use the MySQL database?
==============================

First of all, make sure you thought about the reason why you want to use this option.

It is recommend to use the MySQL support if:

* You have got a large playerbase (performance) ✔
* You want players to have the same pets on different servers (bungeecord) ✔
* You have already got a MySQL server and want to keep all saves at one places (backups) ✔


**before**
::
    ############################

    # Database settings

    # All petblocks are stored in a local file by default.
    # It is recommend to edit these settings when you are using a MySQL database
    # and have got a bigger player-base. Also if you want to use the same pets
    # cross servers. (BungeeCord)

    ############################

    sql:
      enabled: false
      host: localhost
      port: 3306
      database: ''
      username: ''
      password: ''


You can get the data from your MySQL database host.

**after**
::
    ############################

    # Database settings

    # All petblocks are stored in a local file by default.
    # It is recommend to edit these settings when you are using a MySQL database
    # and have got a bigger player-base. Also if you want to use the same pets
    # cross servers. (BungeeCord)

    ############################

    sql:
      enabled: true
      host: 'your_db_host_address'
      port: 3306
      database: 'your_db_name'
      username: 'your_db_login_username'
      password: 'your_db_login_password'