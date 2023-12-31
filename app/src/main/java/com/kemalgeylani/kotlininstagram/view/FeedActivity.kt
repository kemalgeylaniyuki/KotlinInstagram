package com.kemalgeylani.kotlininstagram.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kemalgeylani.kotlininstagram.R
import com.kemalgeylani.kotlininstagram.adapter.FeedRecyclerAdapter
import com.kemalgeylani.kotlininstagram.databinding.ActivityFeedBinding
import com.kemalgeylani.kotlininstagram.model.Post

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var postArrayList : ArrayList<Post>
    private lateinit var adapter : FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        db = Firebase.firestore

        postArrayList = ArrayList<Post>()

        getData()

        adapter = FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this) // görünümler alt alta olması için.
        binding.recyclerView.adapter = adapter

    }

     fun getData() {

         db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->

             if (error != null) {
                 Toast.makeText(this@FeedActivity, error.localizedMessage, Toast.LENGTH_LONG).show()
             } else {
                 if (value != null) {
                     if (!value.isEmpty) {

                         val documents = value.documents

                         postArrayList.clear()

                         for (document in documents) {

                             //casting
                             val comment = document.getString("comment")
                             val userEmail = document.getString("userMail")
                             val downloadUrl = document.getString("downloadUrl")

                             if (comment != null && userEmail != null && downloadUrl != null) {
                                 val post = Post(userEmail, comment, downloadUrl)
                                 postArrayList.add(post)
                             }

                             adapter.notifyDataSetChanged()
                         }

                     }
                 }
             }

         }
     }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.insta_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_post_id){
            val intent = Intent(this@FeedActivity, UploadActivity::class.java)
            startActivity(intent)
        }
        else if (item.itemId == R.id.insta_out_id){
            //signOut
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}