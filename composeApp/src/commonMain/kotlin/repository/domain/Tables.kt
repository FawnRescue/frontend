package repository.domain

enum class Tables(val path: String) {
    MISSION("mission"),
    FLIGHT_PLAN("flightplan"),
    FLIGHT_DATE("flightdate"),
    USER("user"),
    AIRCRAFT("aircraft"),
    COMMAND("command"),
    DETECTION("detection"),
}