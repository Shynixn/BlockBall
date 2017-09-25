SELECT stat.id, stat.shy_player_id, stat.wins, stat.games, stat.goals FROM SHY_BLOCKBALL_STATS stat, SHY_PLAYER play
WHERE stat.shy_player_id = play.id
AND play.uuid = ?;