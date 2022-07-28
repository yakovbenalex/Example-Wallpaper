package ru.jacobo.example_wallpapers.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.jacobo.example_wallpapers.R
import ru.jacobo.example_wallpapers.model.json.Photo
import ru.jacobo.example_wallpapers.ui.TAG

class PhotoListAdapter(
    private val clickListener: (Photo) -> Unit
) : RecyclerView.Adapter<PhotoListAdapter.MyViewHolder>() {

    private var photoList = mutableListOf<Photo>()


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivPhoto: ImageView = view.findViewById(R.id.ivPhoto)
        private val ivLikes: ImageView = view.findViewById(R.id.ivLikes)
        private val tvAuthorPhoto: TextView = view.findViewById(R.id.tvAuthorPhoto)
        private val tvLikes: TextView = view.findViewById(R.id.tvLikes)
        private val tvInfoMessage: TextView = view.findViewById(R.id.tvInfoMessage)
        private val pbPhotoLoading: ProgressBar = view.findViewById(R.id.pbPhotoLoading)

        fun bind(context: Context, photo: Photo) {
            ivPhoto.setOnClickListener {
                clickListener.invoke(photo)
            }

            Glide
                .with(itemView)
                .load(photo.urls.regular)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        pbPhotoLoading.visibility = View.GONE
                        tvInfoMessage.text = context.getString(R.string.loading_error)
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        tvAuthorPhoto.text = photo.user.username
                        tvLikes.text = "${photo.likes}"



                        pbPhotoLoading.visibility = View.GONE
                        ivLikes.visibility = View.VISIBLE
                        return false
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(ivPhoto)
        }
    }

    fun updateList(list: ArrayList<Photo>) {
        photoList.clear()
        photoList.addAll(list)
        notifyDataSetChanged()
    }

    fun addToList(list: ArrayList<Photo>) {
        photoList.addAll(list)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_item_photo, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(holder.itemView.context, photoList[position])
    }

    override fun getItemCount(): Int = photoList.size
}