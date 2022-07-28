package ru.jacobo.example_wallpapers.model.json

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Topic(
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("preview_photos")
    val preview_photos: List<Photo>,
    @SerializedName("title")
    val title: String,
    @SerializedName("total_photos")
    val total_photos: Int
) : Parcelable