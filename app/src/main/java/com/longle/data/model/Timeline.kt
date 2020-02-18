package com.longle.data.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["address", "date", "stayFrom"])
data class Timeline(
    @field:SerializedName("date")
    val date: String,
    @field:SerializedName("dayOfWeek")
    val dayOfWeek: String,
    @field:SerializedName("placeType")
    val placeType: String?,
    @field:SerializedName("placeName")
    val placeName: String?,
    @field:SerializedName("address")
    val address: String,
    @field:SerializedName("stayFrom")
    val stayFrom: Long,
    @field:SerializedName("stayTo")
    var stayTo: Long,
    @field:SerializedName("latitude")
    val latitude: Double?,
    @field:SerializedName("longitude")
    val longitude: Double?
) {

    override fun equals(other: Any?): Boolean {
        return if (other is Timeline) {
            date == other.date && address == other.address
        } else {
            false
        }
    }
}