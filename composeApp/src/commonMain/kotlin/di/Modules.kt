package di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

// Provide the dependencies through a Koin module
val sharedModule = module {
    singleOf(::SharedPrinter)
    singleOf(::InternalPrinter)
    singleOf(::getSupabaseClient)
}