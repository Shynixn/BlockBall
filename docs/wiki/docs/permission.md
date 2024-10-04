# Permission

The following permissions are available in BlockBall.

### Levels

* User: A permission all players can have.
* Admin/User: Depending on your server, you may give this permission to your players. If you are not sure, try to build
  your server around **not giving this permission** to your players.
* Admin: A permission only admins should have.

### Minimum Required Permissions

| Permission               | Level      | Description                                                                                          |   
|--------------------------|------------|------------------------------------------------------------------------------------------------------|
| blockball.command        | User       | Allows to use the /blockball command.                                                                |   
| blockball.join.*         | User       | Allows to join all games.                 |

### All Permissions

| Permission               | Level      | Description                                                                                          |   
|--------------------------|------------|------------------------------------------------------------------------------------------------------|
| blockball.command        | User       | Allows to use the /blockball command.                                                                |   
| blockball.join.*         | User       | Allows to join all games. The **blockball.command** permission is also required.                     |  
| blockball.join.[name]    | User       | Allows to join a specific game. The **blockball.command** permission is also required.               |
| blockball.command.staff  | Admin/User | Allows to execute commands while ingame. This permission will be replaced in the future.             |  
| blockball.game.inventory | Admin/User | Allows open and click in inventories while ingame.   This permission will be replaced in the future. |
| blockball.edit           | Admin      | Allows to create, edit and delete games.                                                             |                          
| blockball.referee.join   | Admin      | Allows to manipulate games using /blockball referee commands                                         |  
