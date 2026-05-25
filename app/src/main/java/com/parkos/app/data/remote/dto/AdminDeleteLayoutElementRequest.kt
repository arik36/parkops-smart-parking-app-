package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminDeleteLayoutElementRequest(
    @SerializedName("p_element_id")
    val elementId: String
)