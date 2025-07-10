# Signs

This page explains how you can create join and leave signs in BlockBall.

### Creating a join sign

* Place a new sign in your world
* Execute the following command: 

```
/blockballsign add blockball_join_sign arena <game>
```

e.g.

```
/blockballsign add blockball_join_sign arena game1
```

* Right-click on your placed sign. The sign will be converted to a join sign.

### Other sign types

**Leave**
```
/blockballsign add blockball_leave_sign
```

**Join Team Red**
```
/blockballsign add blockball_join_red_sign arena <game>
```

**Join Team Blue**
```
/blockballsign add blockball_join_red_sign arena <game>
```

### Removing a sign

* Simply destroy the sign with your hand

### Customizing signs

* Open the ``plugins/BlockBall/sign`` folder and edit the files. 
* Execute ``/blockballsign reload`` or ``/blockball reload`` to load the changes.

BlockBall uses a version of [ShyCommandSigns](https://shynixn.github.io/ShyCommandSigns/wiki/site/installation/) internally, if you want to learn more about all available options.



