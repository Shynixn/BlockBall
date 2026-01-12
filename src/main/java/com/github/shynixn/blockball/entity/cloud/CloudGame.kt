package com.github.shynixn.blockball.entity.cloud

class CloudGame {
    var id : String = ""
    var startDate : String = ""
    var endDate : String = ""
    var courtName : String = ""
    var teamRed : CloudTeam = CloudTeam()
    var teamBlue : CloudTeam = CloudTeam()
}