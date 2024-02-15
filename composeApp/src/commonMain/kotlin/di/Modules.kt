package di

import hangar.presentation.discover.BluetoothClient
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import repository.FlightDateRepo
import repository.FlightPlanRepo
import repository.AircraftRepo
import repository.MissionRepo
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
    // Throws an error but is correct. Probably because expect classes are in beta
    singleOf(::BluetoothClient)
    singleOf(::UserRepo)
    singleOf(::AircraftRepo)
}