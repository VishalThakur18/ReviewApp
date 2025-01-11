package model

import com.google.firebase.Timestamp

data class Restaurant(
    var id: String = "",
    var name: String = "",
    var imageUrl: String = "",
    var votes: Int = 0,
    val lastVoteReset: Timestamp? = null // Use Firebase's Timestamp
)
