package edu.msa.intouch.model

enum class ConnectionStatus(val status: Int) {
    PENDING(1),
    ACCEPTED(2),
    REJECTED(3)
}