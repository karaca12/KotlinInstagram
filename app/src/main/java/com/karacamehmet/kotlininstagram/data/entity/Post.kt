package com.karacamehmet.kotlininstagram.data.entity

import com.google.firebase.Timestamp

data class Post(
    val userEmail: String,
    val explanation: String,
    val downloadUrl: String,
    val timeStamp: Timestamp
)
