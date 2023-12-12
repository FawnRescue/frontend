package di

// Only used in the shared module
internal class InternalPrinter {
    fun print(): String {
        return "Internal printer."
    }
}

// This is the Printer we will later expose to Android/iOS and desktop
class SharedPrinter internal constructor(
    private val internalPrinter: InternalPrinter
) {
    fun print(): String {
        return "Shared printer. ${internalPrinter.print()}"
    }
}

