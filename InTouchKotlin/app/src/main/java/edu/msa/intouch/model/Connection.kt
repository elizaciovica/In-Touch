package edu.msa.intouch.model

data class Connection(
    val id: Int,
    val senderId: String,
    val receiverId: String,
    val connectionStatus: Int
)
