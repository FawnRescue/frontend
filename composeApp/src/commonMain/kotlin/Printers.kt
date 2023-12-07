import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

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

// Provide the dependencies through a Koin module
val sharedModule = module {
    singleOf(::InternalPrinter)
    singleOf(::SharedPrinter)
}