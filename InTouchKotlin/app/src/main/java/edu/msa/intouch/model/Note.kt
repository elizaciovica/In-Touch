package edu.msa.intouch.model

data class Note (
    val title: String,
    val content: String
) {
    constructor(): this("", "")
}
