package edu.msa.intouch.model

data class Connection(
    val id: Int,
    val senderId: Client,
    val receiverId: Client,
    val connectionStatus: Int
)
