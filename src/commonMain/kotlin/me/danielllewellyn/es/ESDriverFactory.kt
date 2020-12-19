package me.danielllewellyn.es

import com.squareup.sqldelight.db.SqlDriver

expect class ESDriverFactory {
    fun createDriver(): SqlDriver
}