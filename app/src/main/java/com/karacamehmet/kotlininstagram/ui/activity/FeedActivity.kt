package com.karacamehmet.kotlininstagram.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karacamehmet.kotlininstagram.data.entity.Post
import com.karacamehmet.kotlininstagram.databinding.ActivityFeedBinding
import com.karacamehmet.kotlininstagram.ui.adapter.FeedRVAdapter

class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var posts: ArrayList<Post>
    private lateinit var feedRVAdapter: FeedRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        firestore = Firebase.firestore

        posts = ArrayList()

        getData()

        binding.recyclerViewFeed.layoutManager = LinearLayoutManager(this)
        feedRVAdapter = FeedRVAdapter(this, posts)
        binding.recyclerViewFeed.adapter = feedRVAdapter

        binding.imageButtonSignOut.setOnClickListener {
            Snackbar.make(it, "Are you sure you want to signout ?", Snackbar.LENGTH_LONG)
                .setAction("YES!") {
                    auth.signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }.show()

        }
        binding.floatingActionButtonPost.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getData() {
        firestore.collection("Post").orderBy("timeStamp",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error != null) {
                Snackbar.make(
                    binding.root,
                    error.localizedMessage as CharSequence,
                    Snackbar.LENGTH_LONG
                )
                    .show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {
                        val documents = value.documents
                        posts.clear()
                        for (document in documents) {
                            val explanation = document.get("explanation") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            val timeStamp = document.get("timeStamp") as Timestamp
                            val userEmail = document.get("userEmail") as String
                            val post = Post(userEmail, explanation, downloadUrl, timeStamp)
                            posts.add(post)
                        }
                        feedRVAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }


}