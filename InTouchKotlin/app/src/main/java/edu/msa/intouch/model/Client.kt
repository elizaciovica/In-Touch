package edu.msa.intouch.model

import kotlinx.serialization.Serializable

@Serializable
data class Client(
    val firebaseId: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String
)
