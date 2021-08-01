//package com.example.emojistatus
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter
//import com.firebase.ui.firestore.FirestoreRecyclerOptions
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//
//
//
//
//class EmojiRecyclerView : AppCompatActivity() {
//
//    // For Accessing a Cloud Firestore instance from your Activity
//    private val db = Firebase.firestore
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // it is the query we make to access the items in the database collections
//        val query = db.collection("User")

//        val rvUsers = findViewById<RecyclerView>(R.id.rvUsers)
//
//        // this is a reference to the recyclerView we get from firebase, it is of type User class Builder and takes in 2 params - the query that has to be run and what has to be retrieved, hence (query, User::class.java)
//        val options = FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java)
//            .setLifecycleOwner(this).build()
//
            // make an adapter for the recycler view to adapt to - classic Firebase format and otherwise also - see SleeTracker app
//        val adapter = object : FirestoreRecyclerAdapter<User, UserViewHolder>(options){
//
//              //in this we always inflate our ViewHolder - make custom or use system made
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

//                // we use inbuilt layout from android -we can also make our own - like in sleeptTracker app
//                val view = LayoutInflater.from(this@EmojiRecyclerView).inflate(android.R.layout.simple_list_item_2, parent,false)
//                return UserViewHolder(view)
//
//            }
//              // Just bind the textViews or any other items that is in our layout to the items of the data class. This is done in order to display the values. Try to use binding here
//            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
//                val tvName : TextView = holder.itemView.findViewById(android.R.id.text1)
//                val tvEmoji : TextView = holder.itemView.findViewById(android.R.id.text2)
//                tvName.text = model.displayName
//                tvEmoji.text = model.emojiStat
//
//            }
//
//        }
//
//        rvUsers.adapter = adapter
//        rvUsers.layoutManager = LinearLayoutManager(this)
//
//
//    }
//
//

//                    Snackbar.make(contextView, R.string.text_label, Snackbar.LENGTH_LONG)
//                        .setAction("Login) {
//                    val loginIntent = Intent(this, LoginActivity::class.java)  // we made logout intent as a separate variable since we need to use it to clear the backstack - we could have also done it like startActivity(Intent(__ , __))
//
//                    // now we want the backstack to end completely, hence we use intent flags
//                    logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // to clear everything from the backstack and start intent activity again
//                    startActivity(logoutIntent)
//
//                        }
//                        .show()
//}