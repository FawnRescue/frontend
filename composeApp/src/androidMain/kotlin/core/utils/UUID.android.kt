package core.utils

import java.util.UUID

actual fun randomUUID(): String {
    return UUID.randomUUID().toString()
}