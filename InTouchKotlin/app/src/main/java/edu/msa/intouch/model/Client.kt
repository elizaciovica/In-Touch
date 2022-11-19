package edu.msa.intouch.model

data class Client(
    val firebaseId: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String
)
