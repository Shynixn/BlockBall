SELECT 1 FROM SHY_BLOCKBALL_STATS stat, SHY_PLAYER play
WHERE stat.shy_player_id = play.id
AND play.uuid = ?;