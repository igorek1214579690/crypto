package com.example.crypto.presentation.common

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

enum class ErrorKind { NO_INTERNET, UNKNOWN }

fun classifyError(e: Throwable): ErrorKind = when (e) {
    is UnknownHostException, is SocketTimeoutException, is IOException -> ErrorKind.NO_INTERNET
    else -> ErrorKind.UNKNOWN
}