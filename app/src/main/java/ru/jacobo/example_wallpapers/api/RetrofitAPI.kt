package ru.jacobo.example_wallpapers.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.jacobo.example_wallpapers.model.json.Photo
import ru.jacobo.example_wallpapers.model.json.Topic
import ru.jacobo.example_wallpapers.util.Constants.Companion.apiKey

interface RetrofitAPI {
    /** @per_page - default value = 10 * */
    @GET("/topics?${apiKey}")
    fun getTopicList(@Query("per_page") per_page: Int): Call<ArrayList<Topic>>

    @GET("/topics/{id_or_slug}/photos?${apiKey}")
    fun getPhotoList(
        @Path("id_or_slug") id_or_slug: String
    ): Call<ArrayList<Photo>>

    @GET("/topics/{id_or_slug}/photos?${apiKey}")
    fun getPhotoList(
        @Path("id_or_slug") id_or_slug: String,
        @Query("per_page") per_page: Int
    ): Call<ArrayList<Photo>>

    @GET("/photos/{id}?${apiKey}")
    fun getPhoto(
        @Path("id") id: String
    ): Call<Photo>
}