package edu.msa.intouch.model

data class Event (
    val title: String,
    val time: String
) {
    constructor(): this("", "")
}