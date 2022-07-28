package ru.jacobo.example_wallpapers.model.json

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo(
    @SerializedName("id")
    val id: String,
    @SerializedName("urls")
    val urls: Urls,
    @SerializedName("user")
    val user: User,
    @SerializedName("likes")
    val likes: Int,
    @SerializedName("width")
    val width: Int,
    @SerializedName("height")
    val height: Int,
) : Parcelable