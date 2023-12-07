package org.fawnrescue.project

import SharedPrinter

internal class AndroidPrinter(
    private val sharedPrinter: SharedPrinter
) {
    fun print(): String {
        return "Android printer. ${sharedPrinter.print()}"
    }
}