package me.danielllewellyn.es.internal.util

actual class DriverFactory {

    val driver: SqlDriver by lazy {
        NativeSqliteDriver(Password.Schema, "notes.db")
    }
    actual fun createDriver(): SqlDriver = driver

}