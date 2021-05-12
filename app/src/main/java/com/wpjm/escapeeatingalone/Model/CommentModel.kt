package com.wpjm.escapeeatingalone.Model

class CommentModel {
    var name: String = ""
    var contents: String = ""
    var timestamp: String = ""
    var boardTimeStamp: String =""

    constructor(name: String, contents: String, timestamp: String, boardTimeStamp: String) {
        this.name = name
        this.contents = contents
        this.timestamp = timestamp
        this.boardTimeStamp = boardTimeStamp
    }
}