package me.danielllewellyn.es

import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object ESConfiguration {
    var verboseLogger: ((String) -> Unit)? = null
    var infoLogger: ((String) -> Unit)? = null
    var exceptionLogger: ((Exception) -> Unit)? = null
}