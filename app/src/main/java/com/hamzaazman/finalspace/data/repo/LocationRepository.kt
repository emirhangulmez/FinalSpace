package com.hamzaazman.finalspace.data.repo

import com.hamzaazman.finalspace.data.database.LocationDao
import com.hamzaazman.finalspace.data.network.SpaceApi
import com.hamzaazman.finalspace.util.fromLocationToModel
import com.hamzaazman.finalspace.util.networkBoundResource
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val locationDao: LocationDao,
    private val api: SpaceApi
) {

    fun getLocations() = networkBoundResource(
        query = { locationDao.getAll() },
        fetch = { api.getAllLocations() },
        saveFetchResult = {
            locationDao.deleteAll()
            val list = it.body()?.let { locations ->
                locations.map { location ->
                    fromLocationToModel(location)
                }
            }

            locationDao.insertAll(list!!)
        },
        shouldFetch = { locations ->
            locations.isEmpty()
        }
    )

    suspend fun getCharactersByLocation(url: String) = api.getCharacterByLocation(url)
}