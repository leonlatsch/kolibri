package dev.leonlatsch.kolibri.util

fun String.Companion.empty() = ""

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }