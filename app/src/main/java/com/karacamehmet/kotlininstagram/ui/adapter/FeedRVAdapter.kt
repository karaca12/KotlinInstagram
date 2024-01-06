package com.karacamehmet.kotlininstagram.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.type.DateTime
import com.karacamehmet.kotlininstagram.data.entity.Post
import com.karacamehmet.kotlininstagram.databinding.ItemRecyclerViewActivityFeedBinding

class FeedRVAdapter(private val context: Context, private val posts: ArrayList<Post>) :
    RecyclerView.Adapter<FeedRVAdapter.ItemRecyclerViewActivityFeedHolder>() {
    class ItemRecyclerViewActivityFeedHolder(val binding: ItemRecyclerViewActivityFeedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemRecyclerViewActivityFeedHolder {
        val binding =
            ItemRecyclerViewActivityFeedBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemRecyclerViewActivityFeedHolder(binding)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: ItemRecyclerViewActivityFeedHolder, position: Int) {
        val binding = holder.binding
        val post = posts[position]

        binding.textViewUserEmail.text = post.userEmail
        binding.textViewPostExplanation.text = post.explanation
        binding.textViewPostTimeStamp.text = post.timeStamp.toDate().toString()

        Glide.with(context).load(post.downloadUrl).into(binding.imageViewPostImage)

    }

}