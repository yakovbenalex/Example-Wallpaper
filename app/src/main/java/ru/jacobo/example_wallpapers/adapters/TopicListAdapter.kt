package ru.jacobo.example_wallpapers.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
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
import ru.jacobo.example_wallpapers.model.json.Topic

class TopicListAdapter(
    private val clickListener: (String) -> Unit
) : RecyclerView.Adapter<TopicListAdapter.MyViewHolder>() {

    private var topicList = mutableListOf<Topic>()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTopicTitle:       TextView =      view.findViewById(R.id.tvTopicTitle)
        private val tvTopicDescription: TextView =      view.findViewById(R.id.tvTopicDescription)
        private val ivTopicThumbnail:   ImageView =     view.findViewById(R.id.ivTopicThumbnail)
        private val llTopic:            LinearLayout =  view.findViewById(R.id.llTopic)
        private val tvInfoMessage:      TextView =      view.findViewById(R.id.tvInfoMessage)
        private val pbPhotoLoading:     ProgressBar =   view.findViewById(R.id.pbPhotoLoading)

        fun bind(context: Context, topic: Topic) {
            tvTopicTitle.text = topic.title
            tvTopicDescription.text = topic.description
            tvInfoMessage.text = ""

            llTopic.setOnClickListener {
                clickListener.invoke(topic.id)
            }
            Glide
                .with(itemView)
                .load(topic.preview_photos[0].urls.thumb)
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
                        pbPhotoLoading.visibility = View.GONE
                        return false
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(ivTopicThumbnail)

        }
    }

    fun updateList(list: ArrayList<Topic>) {
        topicList.clear()
        topicList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_item_topic, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(holder.itemView.context, topicList[position])
    }

    override fun getItemCount(): Int = topicList.size
}