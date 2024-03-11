package di

import androidx.compose.runtime.remember
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import hangar.presentation.discover.BluetoothClient
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import repository.AircraftRepo
import repository.FlightDateRepo
import repository.FlightPlanRepo
import repository.MissionRepo
import repository.UserRepo
import repository.CommandRepo
import repository.LocationRepo
import repository.LocationService

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
    singleOf(::CommandRepo)
    singleOf(::LocationRepo)
    singleOf(::LocationService)
}