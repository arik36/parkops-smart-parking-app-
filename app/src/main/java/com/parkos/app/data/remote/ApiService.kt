package com.parkos.app.data.remote

import com.parkos.app.data.remote.dto.CreateUserProfileRequest
import com.parkos.app.data.remote.dto.LoginRequest
import com.parkos.app.data.remote.dto.LoginResponse
import com.parkos.app.data.remote.dto.OrganizationDto
import com.parkos.app.data.remote.dto.ParkingLotDto
import com.parkos.app.data.remote.dto.ParkingSpotDto
import com.parkos.app.data.remote.dto.RegisterRequest
import com.parkos.app.data.remote.dto.RegisterResponse
import com.parkos.app.data.remote.dto.ReservationDto
import com.parkos.app.data.remote.dto.ReserveSpotRequest
import com.parkos.app.data.remote.dto.UserProfileDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/v1/signup")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @GET("rest/v1/users")
    suspend fun getUserProfile(
        @Header("Authorization") authorization: String,
        @Query("id") idFilter: String,
        @Query("select") select: String = "id,email,full_name,role,org_id"
    ): Response<List<UserProfileDto>>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/users")
    suspend fun createUserProfile(
        @Header("Authorization") authorization: String,
        @Body request: CreateUserProfileRequest
    ): Response<List<UserProfileDto>>

    @GET("rest/v1/organizations")
    suspend fun getOrganizations(
        @Query("select") select: String = "id"
    ): Response<List<OrganizationDto>>

    @GET("rest/v1/parking_lots")
    suspend fun getParkingLots(
        @Query("select") select: String = "id,org_id,name,address,latitude,longitude,created_at",
        @Query("order") order: String = "name.asc"
    ): Response<List<ParkingLotDto>>

    @GET("rest/v1/parking_lots")
    suspend fun getParkingLotsByOrg(
        @Query("org_id") orgFilter: String,
        @Query("select") select: String = "id,org_id,name,address,latitude,longitude,created_at",
        @Query("order") order: String = "name.asc"
    ): Response<List<ParkingLotDto>>

    @GET("rest/v1/parking_spots")
    suspend fun getParkingSpots(
        @Query("parking_lot_id") parkingLotFilter: String,
        @Query("select") select: String = "id,parking_lot_id,spot_number,status,type,updated_at",
        @Query("order") order: String = "spot_number.asc"
    ): Response<List<ParkingSpotDto>>

    @POST("rest/v1/rpc/expire_old_reservations")
    suspend fun expireOldReservations(
        @Body request: Map<String, String> = emptyMap()
    ): Response<Unit>

    @POST("rest/v1/rpc/reserve_spot")
    suspend fun reserveSpot(
        @Body request: ReserveSpotRequest
    ): Response<ReservationDto>

    @POST("rest/v1/rpc/occupy_reserved_spot")
    suspend fun occupyReservedSpot(
        @Body request: ReserveSpotRequest
    ): Response<ReservationDto>

    @GET("rest/v1/reservations")
    suspend fun getActiveReservations(
        @Query("status") statusFilter: String = "in.(reserved,active)",
        @Query("select") select: String = "id,user_id,spot_id,status,start_time,end_time,created_at,expires_at,occupied_at",
        @Query("order") order: String = "start_time.desc",
        @Query("limit") limit: Int = 1
    ): Response<List<ReservationDto>>

    @POST("rest/v1/rpc/release_active_reservation")
    suspend fun releaseActiveReservation(
        @Body request: Map<String, String> = emptyMap()
    ): Response<ReservationDto>
}