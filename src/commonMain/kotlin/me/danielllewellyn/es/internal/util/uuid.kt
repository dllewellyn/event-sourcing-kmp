package me.danielllewellyn.es.internal.util

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

internal fun uuid() = (1..50)
    .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
    .map(charPool::get)
    .joinToString("")