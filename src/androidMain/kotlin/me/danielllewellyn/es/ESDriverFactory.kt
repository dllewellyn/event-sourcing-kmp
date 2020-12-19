package  me.danielllewellyn.es

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import me.daniellewellyn.es.Events

actual class ESDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(Events.Schema, context, "events.db")
    }
}