package com.wpjm.escapeeatingalone.Model

class BoardModel{
    var profile:String? = null
    var name: String? = null
    var title: String? = null
    var contents: String? = null
    var date: String? = null


    constructor(profile: String, name: String, title:String, contents:String, date:String){
        this.profile = profile
        this.name = name
        this.title = title
        this.contents = contents
        this.date = date
    }
}