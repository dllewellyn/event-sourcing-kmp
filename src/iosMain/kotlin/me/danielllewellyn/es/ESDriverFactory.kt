package me.danielllewellyn.es

import com.squareup.sqldelight.db.SqlDriver
import me.daniellewellyn.es.Events
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class ESDriverFactory {

    private val driver: SqlDriver by lazy {
        NativeSqliteDriver(Events.Schema, "events.db")
    }

    actual fun createDriver(): SqlDriver = driver

}