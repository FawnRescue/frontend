package di

import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import repository.MissionRepo
import repository.FlightPlanRepo
import repository.FlightDateRepo
import repository.UserRepo

// Provide the dependencies through a Koin module
val sharedModule = module {
    singleOf(::SharedPrinter)
    singleOf(::InternalPrinter)
    singleOf(::getSupabaseClient)
    singleOf(::Navigator)
    singleOf(::MissionRepo)
    singleOf(::FlightPlanRepo)
    singleOf(::FlightDateRepo)
    singleOf(::getMissionChannel)
    singleOf(::UserRepo)
}