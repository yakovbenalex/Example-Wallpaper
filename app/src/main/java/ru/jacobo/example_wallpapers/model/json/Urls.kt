package ru.jacobo.example_wallpapers.model.json

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Urls(
    @SerializedName("thumb")
    val thumb: String,
    @SerializedName("regular")
    val regular: String,
    @SerializedName("full")
    val full: String
) : Parcelable