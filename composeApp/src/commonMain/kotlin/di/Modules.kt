package di

import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import planning.repository.MissionRepo

// Provide the dependencies through a Koin module
val sharedModule = module {
    singleOf(::SharedPrinter)
    singleOf(::InternalPrinter)
    singleOf(::getSupabaseClient)
    singleOf(::Navigator)
    singleOf(::MissionRepo)

}