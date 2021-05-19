package com.wpjm.escapeeatingalone.Model

class ChatroomModel {
    var users: MutableList<String>?=null
    var title: String? = null
    var storeName: String? = null
    var date: String? = null
    var count: Int = 0
    var chatroomId: String? = null

    constructor(users: MutableList<String>?, title: String?, storeName: String?, date: String, count: Int, chatroomId: String?) {
        this.users = users
        this.title = title
        this.storeName = storeName
        this.date = date
        this.count = count
        this.chatroomId = chatroomId
    }

}