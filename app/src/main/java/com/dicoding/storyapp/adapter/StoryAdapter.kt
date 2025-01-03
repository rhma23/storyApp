package com.dicoding.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.response.ListStoryItem
import com.dicoding.storyapp.view.main.DetailActivity

class StoryAdapter(private val stories: List<ListStoryItem?>, private val onItemClick: (ListStoryItem) -> Unit) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.nameTextView.text = story?.name
        holder.descriptionTextView.text = story?.description

        Glide.with(holder.itemView)
            .load(story?.photoUrl)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            story?.let { selectedStory ->
                val intent = Intent(holder.itemView.context, DetailActivity::class.java)
                intent.putExtra("EXTRA_STORY", selectedStory)

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    holder.imageView,
                    "storyImageTransition"
                )

                holder.itemView.context.startActivity(intent, options.toBundle())
            }
        }
    }

    override fun getItemCount(): Int = stories.size

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageLogo)
        val nameTextView: TextView = itemView.findViewById(R.id.yourName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description)
    }
}
