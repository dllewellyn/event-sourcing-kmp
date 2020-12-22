package me.danielllewellyn.es

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import me.daniellewellyn.es.Events

actual class ESDriverFactory {

    private val driver: SqlDriver by lazy {
        NativeSqliteDriver(Events.Schema, "events.db")
    }

    actual fun createDriver(): SqlDriver = driver

}