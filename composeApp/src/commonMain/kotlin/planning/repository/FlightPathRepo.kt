package planning.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import planning.domain.InsertableFlightPlan
import planning.domain.FlightPlan
import planning.domain.InsertableMission
import planning.domain.Tables
import presentation.maps.LatLong
import presentation.maps.getCenter
import kotlin.math.tan
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class FlightPlanRepo : KoinComponent {
    val supabase: SupabaseClient by inject<SupabaseClient>()

    suspend fun getPath(id: String): FlightPlan = supabase.from(Tables.FLIGHT_PLAN.path).select {
        filter {
            eq("id", id)
        }
    }.decodeSingle()

    suspend fun upsertFlightPlan(
        selectedMission: InsertableMission,
        plan: InsertableFlightPlan,
    ): FlightPlan {
        println(plan)
        if (selectedMission.id == null) {
            println("error: mission has no id, cant upsert flight plan")
            println(selectedMission)
            throw Error("error: mission has no id, cant upsert flight plan")
        }
        return supabase.from(Tables.FLIGHT_PLAN.path).upsert(plan) { select() }
            .decodeSingle<FlightPlan>().also {
                supabase.from(Tables.MISSION.path).update(selectedMission.copy(plan = it.id)) {
                    filter {
                        eq("id", selectedMission.id)
                    }
                }
            } // error handling
    }

    fun calculateCheckpoints(
        boundary: List<LatLong>,
        cameraFOV: Double = 35.0,
        flightHeight: Double = 10.0,
        overlap: Double = 0.1,
    ): List<LatLong> {
        //TODO remove defaults

        // Check 0 < FOV <= 360
        require(cameraFOV > 0 && cameraFOV <= 360) { "Invalid camera FOV" }

        // Check 0 < height < 200
        require(flightHeight > 0 && flightHeight < 200) { "Invalid flight height" }

        // Check 0 <= overlap < 1
        require(overlap >= 0 && overlap < 1) { "Invalid overlap value" }

        // Check boundary.size > 1
        if (boundary.size <= 1) {
            return listOf()
        }

        data class Dimension(val x: Double, val y: Double)

        data class BoundingBox(val position: LatLong, val w: Double, val h: Double)

        fun toRadians(deg: Double): Double = deg / 180.0 * PI

        fun calculateCameraCoverage(cameraFOV: Double, flightHeight: Double): Dimension {

            // Convert FOV to radians
            val fovRadians = toRadians(cameraFOV)

            // Calculate half FOV
            val halfFOV = fovRadians / 2.0

            // Calculate the width and height of the coverage area
            val width = 2 * flightHeight * tan(halfFOV)
            val height = 2 * flightHeight * tan(halfFOV)

            return Dimension(width, height)
        }

        fun findBoundingBox(boundary: List<LatLong>): BoundingBox {
            // TODO: allow for rotation of the boundary to find better bounding boxes
            val (latCoordinates, longCoordinates) = boundary.map { Pair(it.latitude, it.longitude) }
                .unzip()
            val latmax = latCoordinates.max()
            val longmax = longCoordinates.max()
            val latmin = latCoordinates.min()
            val longmin = longCoordinates.min()
            val lat = latmax
            val long = longmin
            val h = latmax - latmin
            val w = longmax - longmin
            return BoundingBox(LatLong(lat, long), w, h)
        }

        fun pointInBoundingBox(point: LatLong, box: BoundingBox): Boolean {
            return point.latitude <= box.position.latitude && point.latitude >= box.position.latitude - box.h && point.longitude >= box.position.longitude && point.longitude <= box.position.longitude + box.w
        }

        fun displaceCoordinate(
            original: LatLong,
            displacementY: Double = 0.0,
            displacementX: Double = 0.0,
        ): LatLong {
            val latitudeChange = displacementY / 111111.0
            val longitudeChange = displacementX / (111111.0 * cos(toRadians(original.latitude)))

            val newLatitude = original.latitude + latitudeChange
            val newLongitude = original.longitude + longitudeChange

            return LatLong(newLatitude, newLongitude)
        }

        fun rasterizeBoundingBox(
            box: BoundingBox,
            spacing: Dimension,
            overlap: Double,
        ): List<LatLong> {
            val checkpoints = mutableListOf<LatLong>()
            val spacingWithOverlap = Dimension((1 - overlap) * spacing.x, (1 - overlap) * spacing.y)

            var directionDown = true
            var currentPosition = displaceCoordinate(box.position, -(spacing.y / 2), spacing.x / 2)
            checkpoints.add(currentPosition)

            while (true) {
                val yStep = if (directionDown) -spacingWithOverlap.y else spacingWithOverlap.y
                val newPosition = displaceCoordinate(currentPosition, yStep)
                if (pointInBoundingBox(newPosition, box)) {
                    checkpoints.add(newPosition)
                    currentPosition = newPosition
                    continue
                } else {
                    val alternativePosition =
                        displaceCoordinate(currentPosition, displacementX = spacingWithOverlap.x)
                    if (pointInBoundingBox(alternativePosition, box)) {
                        checkpoints.add(alternativePosition)
                        currentPosition = alternativePosition
                        directionDown = !directionDown
                        continue
                    } else {
                        break
                    }
                }
            }

            return checkpoints
        }

        fun pointInPolygon(point: LatLong, polygon: List<LatLong>): Boolean {
            val numVertices = polygon.size
            val x = point.longitude // longitude as x-coordinate
            val y = point.latitude // latitude as y-coordinate
            var inside = false

            var p1 = polygon[0]
            var p2: LatLong

            for (i in 1..numVertices) {
                p2 = polygon[i % numVertices]

                // Ensure y is between the min and max y-coordinates of the edge
                if (y > min(p1.latitude, p2.latitude) && y <= max(p1.latitude, p2.latitude)) {
                    // Ensure x is to the left of the edge
                    if (x <= max(p1.longitude, p2.longitude)) {
                        // Handle horizontal edges or calculate xIntersection
                        if (p1.latitude != p2.latitude) {
                            val xIntersection =
                                (y - p1.latitude) * (p2.longitude - p1.longitude) / (p2.latitude - p1.latitude) + p1.longitude
                            if (x <= xIntersection) {
                                inside = !inside
                            }
                        } else if (x <= p1.longitude && x <= p2.longitude) {
                            // Special handling for horizontal edges
                            inside = !inside
                        }
                    }
                }

                p1 = p2
            }

            return inside
        }

        fun getBoundaryMarkerOnPath(point: LatLong, boundary: List<LatLong>): LatLong {
            fun getSign(p1: LatLong, p2: LatLong): Boolean {
                return 0 < p2.longitude - p1.longitude
            }

            fun distance(p1: LatLong, p2: LatLong): Double {
                val deltaX = p2.longitude - p1.longitude
                val deltaY = p2.latitude - p1.latitude
                return sqrt(deltaX * deltaX + deltaY * deltaY)
            }

            val centroid = boundary.getCenter()
            return boundary
                .filter { getSign(it, centroid) == getSign(point, centroid) }
                .map { Pair(it, distance(centroid, it)) }
                .minBy { it.second }
                .first
        }

        fun alignPathToPolygon(point: Pair<LatLong, Boolean>, polygon: List<LatLong>): LatLong {
            return if (point.second) {
                point.first
            } else {
                getBoundaryMarkerOnPath(point.first, polygon)
            }
        }

        fun removePointsCompletelyOutsidePolygon(
            checkpoints: List<LatLong>,
            polygon: List<LatLong>,
        ): List<LatLong> {
            //TODO detect whether all the following points that lie an the same x axis do not enter the polygon again
            // if one enters again do nothing else delete the points
            val newCheckpoints =
                checkpoints.map { Pair(it, pointInPolygon(it, polygon)) }

            fun isAFuturePointInPolygon(
                index: Int,
                checkpoints: List<Pair<LatLong, Boolean>>,
            ): Boolean {
                for (i in (index+1)..<checkpoints.size) {
                    if (checkpoints[i].first.longitude != checkpoints[index].first.longitude) {
                        return false
                    }
                    if (checkpoints[i].second) {
                        return true
                    }
                }
                return false
            }

            fun isPreviousPointInPolygon(
                index: Int,
                checkpoints: List<Pair<LatLong, Boolean>>,
            ): Boolean {
                if(index == 0) {
                    return false
                }
                if (checkpoints[index-1].first.longitude != checkpoints[index].first.longitude) {
                    return false
                }
                return checkpoints[index-1].second
            }

            return newCheckpoints.filter {
                if (it.second) {
                    true
                } else {
                    isAFuturePointInPolygon(
                        newCheckpoints.indexOf(it), newCheckpoints
                    ) && isPreviousPointInPolygon(
                        newCheckpoints.indexOf(it), newCheckpoints
                    )
                }
            }.map { alignPathToPolygon(it, boundary) }
        }

        val spacing = calculateCameraCoverage(cameraFOV, flightHeight)
        val boundingBox = findBoundingBox(boundary)
        val possibleCheckpoints = rasterizeBoundingBox(boundingBox, spacing, overlap)
        return removePointsCompletelyOutsidePolygon(
            possibleCheckpoints,
            boundary
        )
    }
}

fun List<LatLong>.sortPolarCoordinates(): List<LatLong> {
    val centroid = this.getCenter()
    return this.sortedWith(compareBy {
        atan2(
            it.latitude - centroid.latitude, it.longitude - centroid.longitude
        )
    })
}