package org.rookieand.autoplant.data

data class MongoSettings(
    val uri: String,
    val database: String,
    val collection: String
)
