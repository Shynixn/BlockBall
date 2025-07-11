# Permission

The following permissions are available in BlockBall.

### Levels

* User: A permission all players can have.
* Admin/User: Depending on your server, you may give this permission to your players. If you are not sure, try to build
  your server around **not giving this permission** to your players.
* Admin: A permission only admins should have.

### Recommended Permissions

| Permission                           | Level | Description                                           |   
|--------------------------------------|-------|-------------------------------------------------------|
| blockball.command                    | User  | Allows to use the /blockball command.                 |   
| blockball.join.*                     | User  | Allows to join all games.                             |
| blockball.shyscoreboard.scoreboard.* | User  | Allows to see all blockball scoreboards during games. |
| blockball.shybossbar.bossbar.*       | User  | Allows to see all blockball bossbars during games.    |

### All Permissions

| Permission                                             | Level      | Description                                                                                          |   
|--------------------------------------------------------|------------|------------------------------------------------------------------------------------------------------|
| blockball.command                                      | User       | Allows to use the /blockball command.                                                                |   
| blockball.join.*                                       | User       | Allows to join all games. The **blockball.command** permission is also required.                     |  
| blockball.join.[name]                                  | User       | Allows to join a specific game. The **blockball.command** permission is also required.               |
| blockball.command.staff                                | Admin/User | Allows to execute commands while ingame. This permission will be replaced in the future.             |  
| blockball.game.inventory                               | Admin/User | Allows open and click in inventories while ingame.   This permission will be replaced in the future. |
| blockball.edit                                         | Admin      | Allows to create, edit and delete games.                                                             |                          
| blockball.referee.join                                 | Admin      | Allows to manipulate games using /blockball referee commands                                         |  
| blockball.shyscoreboard.scoreboard.*                   | User       | Allows to see all scoreboards                                                                        |
| blockball.shyscoreboard.scoreboard.\[scoreboard-name\] | User       | Allows to see a specific scoreboard                                                                  |
| blockball.shyscoreboard.command                        | Admin      | Allows to use the /blockballscoreboard command.                                                      |
| blockball.shyscoreboard.reload                         | Admin      | Allows to reload configurations.                                                                     |
| blockball.shyscoreboard.add                            | Admin      | Allows to add a scoreboard to a player                                                               |
| blockball.shyscoreboard.set                            | Admin      | Allows to set a scoreboard to a player                                                               |
| blockball.shyscoreboard.remove                         | Admin      | Allows to remove a scoreboard from a player                                                          |
| blockball.shyscoreboard.update                         | Admin      | Allows to refresh a scoreboard                                                                       |
| blockball.shybossbar.bossbar.*                         | User       | Allows to see all bossbars                                                                           |
| blockball.shybossbar.bossbar.\[bossbar-name\]          | User       | Allows to see a specific bossbar                                                                     |
| blockball.shybossbar.command                           | Admin      | Allows to use the /blockballbossbar command.                                                         |
| blockball.shybossbar.reload                            | Admin      | Allows to reload configurations.                                                                     |
| blockball.shybossbar.add                               | Admin      | Allows to add a bossbar to a player                                                                  |
| blockball.shybossbar.set                               | Admin      | Allows to set a bossbar to a player                                                                  |
| blockball.shybossbar.remove                            | Admin      | Allows to remove a bossbar from a player                                                             |
| blockball.shybossbar.update                            | Admin      | Allows to refresh a bossbar                                                                          |
| blockball.shycommandsigns.command                      | Admin      | Allows to use the /blockballsign command.                                                            
| blockball.shycommandsigns.add                          | Admin      | Allows to add a join and leave sign                                                                  
| blockball.shycommandsigns.reload                       | Admin      | Allows to reload configurations.                                                                     
