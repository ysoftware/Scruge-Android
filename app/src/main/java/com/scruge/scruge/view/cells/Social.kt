package com.scruge.scruge.view.cells

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.model.entity.Social
import kotlinx.android.synthetic.main.cell_social.view.*

class SocialAdapter(private val social:List<Social>): RecyclerView.Adapter<SocialViewHolder>() {

    lateinit var tap:(Social)->Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialViewHolder {
        return SocialViewHolder(LayoutInflater.from(parent.context)
                                        .inflate(R.layout.cell_social, parent, false))
    }

    override fun getItemCount(): Int {
        return social.size
    }

    override fun onBindViewHolder(holder: SocialViewHolder, position: Int) {
        social.let {
            val item = it[position]
            holder.setup(item)
            holder.itemView.setOnClickListener { tap(item) }
        }
    }
}

class SocialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(social: Social) {
        itemView.social_image_view.setImageResource(imageForNetwork(social.name))
    }

    private fun imageForNetwork(name:String):Int {
        return when (name) {
            "facebook" -> R.drawable.facebook
            "twitter" -> R.drawable.twitter
            "website" -> R.drawable.website
            "vk" -> R.drawable.vk
            "instagram" -> R.drawable.instagram
            "telegram" -> R.drawable.telegram
            "slack" -> R.drawable.slack
            "linkedIn" -> R.drawable.linked_in
            else -> R.drawable.website
        }
    }
}