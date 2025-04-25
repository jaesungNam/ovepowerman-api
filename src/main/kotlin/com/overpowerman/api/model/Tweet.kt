package com.overpowerman.api.model

data class Tweet(
    var createdAt: Long,
    var id: Long,
    var lang: String,
    var retweet: Boolean,
    var text: String
)
