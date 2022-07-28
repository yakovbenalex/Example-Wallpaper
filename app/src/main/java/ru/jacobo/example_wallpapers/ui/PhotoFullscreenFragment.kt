package ru.jacobo.example_wallpapers.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.jacobo.example_wallpapers.R
import ru.jacobo.example_wallpapers.api.RetrofitInstance
import ru.jacobo.example_wallpapers.model.json.Photo
import java.io.ByteArrayOutputStream
import java.util.*


private const val ARG_PHOTO = "param1"
private const val photoFullSizeWidth = 3840

class PhotoFullscreenFragment : Fragment() {
    private lateinit var photoId: String
    private var photoURL: String = ""
    private lateinit var ivPhotoFullscreen: ImageView
    private lateinit var buttonSetAsWallpaper: Button
    private lateinit var tvLoading: TextView
    private lateinit var pbPhotoLoading: ProgressBar
    private lateinit var pbWallpaperSetting: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getPhotoFromArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_fullscreen, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "PhotoFullscreenFragment onViewCreated: $photoId")
        findViews()
//        pbPhotoLoading.visibility = View.VISIBLE
        setPhoto()
//        pbPhotoLoading.visibility = View.GONE
//        ivPhotoFullscreen.visibility = View.VISIBLE
    }

    private fun findViews() {
        ivPhotoFullscreen = requireView().findViewById(R.id.ivPhotoFullscreen)
        buttonSetAsWallpaper = requireView().findViewById(R.id.buttonSetAsWallpaper)
        tvLoading = requireView().findViewById(R.id.tvInfoMessage)

        pbPhotoLoading = requireView().findViewById(R.id.pbPhotoLoading)
        pbWallpaperSetting = requireView().findViewById(R.id.pbWallpaperSetting)

        Log.d(TAG, "onResourceReady: photoURL: $photoURL")
        buttonSetAsWallpaper.setOnClickListener {
            Log.d(TAG, "buttonSetAsWallpaper.setOnClickListener: ")

            if (photoURL.isNotEmpty()) {
                Log.d(TAG, "findViews: photoURL: $photoURL")
                Glide.with(this)
                    .asBitmap()
                    .load(photoURL)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            Log.d(TAG, "onResourceReady: photoURL: $photoURL")
                            setWallpaper(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // this is called when imageView is cleared on lifecycle call or for
                            // some other reason.
                            // if you are referencing the bitmap somewhere else too other than this imageView
                            // clear it here as you can no longer have the bitmap
                        }
                    })

            } else {
                Log.d(TAG, "buttonSetAsWallpaper: Photo URL missing")
                Toast.makeText(requireContext(), "Photo URL missing", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // set photo into imageView
    private fun setPhoto() {
        RetrofitInstance.api.getPhoto(photoId).enqueue(
            (object : Callback<Photo?> {
                override fun onResponse(
                    call: Call<Photo?>,
                    response: Response<Photo?>
                ) {
                    if (response.isSuccessful) {

                        /// use this case to set photo size only with @urls.full@ photo
//                        photoURL = "${response.body()!!.urls.full}&w=$photoFullSizeWidth"
                        photoURL = response.body()!!.urls.full
                        buttonSetAsWallpaper.isEnabled = true

                        Glide
                            .with(ivPhotoFullscreen)
                            .load(photoURL)
                            .listener(object : RequestListener<Drawable?> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable?>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    pbPhotoLoading.visibility = View.GONE
                                    tvLoading.text = getString(R.string.loading_error)
                                    Log.d(TAG, "onLoadFailed: $e")
                                    return true
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable?>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    pbPhotoLoading.visibility = View.GONE
                                    buttonSetAsWallpaper.isEnabled = true
                                    return false
                                }
                            })
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter()
                            .into(ivPhotoFullscreen)
                    }

                    Log.d(TAG, "onResponse: photoURL: $photoURL")
                }

                override fun onFailure(call: Call<Photo?>, t: Throwable) {
                    Log.d(TAG, "onFailure: $t")
                }
            })
        )
    }

    private fun setWallpaper(image: Bitmap) {
        // set image as wallpaper directly
        /*val manager = WallpaperManager.getInstance(requireContext())
        try {
            manager.setBitmap(image)
            Log.d(TAG, "setWallpaper:")
            Toast.makeText(activity, "Wallpaper set!", Toast.LENGTH_SHORT).show()
            pbWallpaperSetting.visibility = View.GONE
        } catch (e: IOException) {
            Log.d(TAG, "Error set image!")
            Toast.makeText(activity, "Error set image!", Toast.LENGTH_SHORT).show()
        }*/

        // set image as wallpaper with special application
        Intent(Intent.ACTION_ATTACH_DATA).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
//            setDataAndType(getImageUri(requireContext(), image), "image/jpeg")
            setDataAndType(getImageUri(image), "image/jpeg")
            putExtra("mimeType", "image/jpeg")
            startActivity(Intent.createChooser(this, "Set as:"))
        }
    }

    private fun getImageUri(bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes)
        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmap,
            UUID.randomUUID().toString() + ".png",
            "drawing"
        )
        return Uri.parse(path)
    }

    private fun getPhotoFromArguments() {
        arguments?.let { bundle ->
            bundle.getString(ARG_PHOTO)?.let {
                photoId = it
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String) =
            PhotoFullscreenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PHOTO, id)
                }
            }
    }
}