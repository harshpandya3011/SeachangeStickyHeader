package com.seachange.healthandsafty.model

class RiskAssement {

    var id: String? = null
    var type: Int? = null
    var version: Int? = null
    var dateScheduled: String? = null
    var site: site? = null
    var group: group? =null
    var partner: partner ? =null
    var riskAssessor: riskAssessor? =null
    var status: status? =null
}


class site {
    var id: Int? = null
    var name: String? = null
    var imageUrl: String? = null
}

class group {
    var id: Int? = null
    var name: String? = null
}

class partner {
    var id: Int? = null
    var name: String? = null
}

class riskAssessor {
    var id: Int? = null
    var firstName: String? = null
    var lastName: String? = null
    var fullName: String? = null
    var avatarUrl: String? = null
}

class status {
    var code: Int? = null
    var label: String? = null
}
