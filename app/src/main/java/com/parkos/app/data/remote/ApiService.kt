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
import com.parkos.app.data.remote.dto.AdminUpdateParkingSpotRequest
import com.parkos.app.data.remote.dto.AdminCreateParkingSpotRequest
import com.parkos.app.data.remote.dto.ParkingFloorDto
import com.parkos.app.data.remote.dto.ParkingLayoutElementDto
import com.parkos.app.data.remote.dto.AdminDeleteParkingSpotRequest
import com.parkos.app.data.remote.dto.AdminCreateLayoutElementRequest
import com.parkos.app.data.remote.dto.AdminDeleteLayoutElementRequest
import com.parkos.app.data.remote.dto.AdminMoveLayoutElementRequest
import com.parkos.app.data.remote.dto.GetReservationHistoryRequest
import com.parkos.app.data.remote.dto.ReservationHistoryDto
import com.parkos.app.data.remote.dto.UpdateFullNameRequest
import com.parkos.app.data.remote.dto.ResolveStaffRequestRequest
import com.parkos.app.data.remote.dto.StaffRequestDto
import com.parkos.app.data.remote.dto.RevokeStaffAccessRequest
import com.parkos.app.data.remote.dto.StaffMemberDto
import com.parkos.app.data.remote.dto.CreateIncidentReportRequest
import com.parkos.app.data.remote.dto.GetIncidentReportsRequest
import com.parkos.app.data.remote.dto.IncidentReportDto

interface ApiService {

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/v1/signup")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("rest/v1/rpc/admin_update_parking_spot")
    suspend fun adminUpdateParkingSpot(
        @Body request: AdminUpdateParkingSpotRequest
    ): Response<ParkingSpotDto>

    @POST("rest/v1/rpc/update_my_full_name")
    suspend fun updateMyFullName(
        @Body request: UpdateFullNameRequest
    ): Response<UserProfileDto>

    @POST("rest/v1/rpc/admin_delete_parking_spot")
    suspend fun adminDeleteParkingSpot(
        @Body request: AdminDeleteParkingSpotRequest
    ): Response<ParkingSpotDto>

    @POST("rest/v1/rpc/admin_create_parking_spot")
    suspend fun adminCreateParkingSpot(
        @Body request: AdminCreateParkingSpotRequest
    ): Response<ParkingSpotDto>

    @POST("rest/v1/rpc/admin_create_layout_element")
    suspend fun adminCreateLayoutElement(
        @Body request: AdminCreateLayoutElementRequest
    ): Response<ParkingLayoutElementDto>

    @POST("rest/v1/rpc/get_my_reservation_history")
    suspend fun getMyReservationHistory(
        @Body request: GetReservationHistoryRequest
    ): Response<List<ReservationHistoryDto>>

    @POST("rest/v1/rpc/admin_delete_layout_element")
    suspend fun adminDeleteLayoutElement(
        @Body request: AdminDeleteLayoutElementRequest
    ): Response<ParkingLayoutElementDto>

    @POST("rest/v1/rpc/admin_move_layout_element")
    suspend fun adminMoveLayoutElement(
        @Body request: AdminMoveLayoutElementRequest
    ): Response<ParkingLayoutElementDto>

    @POST("rest/v1/rpc/admin_get_pending_staff_requests")
    suspend fun adminGetPendingStaffRequests(): Response<List<StaffRequestDto>>

    @POST("rest/v1/rpc/admin_resolve_staff_request")
    suspend fun adminResolveStaffRequest(
        @Body request: ResolveStaffRequestRequest
    ): Response<UserProfileDto>

    @POST("rest/v1/rpc/admin_get_org_staff_members")
    suspend fun adminGetOrgStaffMembers(): Response<List<StaffMemberDto>>

    @POST("rest/v1/rpc/admin_revoke_staff_access")
    suspend fun adminRevokeStaffAccess(
        @Body request: RevokeStaffAccessRequest
    ): Response<UserProfileDto>

    @POST("rest/v1/rpc/staff_create_incident_report")
    suspend fun staffCreateIncidentReport(
        @Body request: CreateIncidentReportRequest
    ): Response<IncidentReportDto>

    @POST("rest/v1/rpc/staff_get_my_incident_reports")
    suspend fun staffGetMyIncidentReports(
        @Body request: GetIncidentReportsRequest
    ): Response<List<IncidentReportDto>>

    @GET("rest/v1/users")
    suspend fun getUserProfile(
        @Header("Authorization") authorization: String,
        @Query("id") idFilter: String,
        @Query("select") select: String = "id,email,full_name,role,org_id,staff_status"
    ): Response<List<UserProfileDto>>

    @GET("rest/v1/parking_layout_elements")
    suspend fun getParkingLayoutElements(
        @Query("parking_lot_id") parkingLotIdFilter: String,
        @Query("order") order: String = "row_index.asc,col_index.asc"
    ): Response<List<ParkingLayoutElementDto>>

    @GET("rest/v1/parking_floors")
    suspend fun getParkingFloors(
        @Query("parking_lot_id") parkingLotIdFilter: String,
        @Query("order") order: String = "floor_order.asc"
    ): Response<List<ParkingFloorDto>>

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