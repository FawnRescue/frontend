package di

import di.SharedPrinter

internal class AndroidPrinter(
    private val sharedPrinter: SharedPrinter
) {
    fun print(): String {
        return "Android printer. ${sharedPrinter.print()}"
    }
}