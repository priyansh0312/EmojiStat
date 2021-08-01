package com.example.emojistatus

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase

// for recycler view, always make a data class
data class User(
    val displayName: String = "",
    val emojistat: String = ""
)

// ViewHolder class for the recycler view - to hold the view into
class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){}

class MainActivity : AppCompatActivity() {

    // this companion object has a set of characters codes that are valid for emojis
    private companion object{
        private const val TAG = "MainActivity"
        private val VALID_CHAR_TYPES = listOf(
            Character.NON_SPACING_MARK, // 6
            Character.DECIMAL_DIGIT_NUMBER, // 9
            Character.LETTER_NUMBER, // 10
            Character.OTHER_NUMBER, // 11
            Character.SPACE_SEPARATOR, // 12
            Character.FORMAT, // 16
            Character.SURROGATE, // 19
            Character.DASH_PUNCTUATION, // 20
            Character.START_PUNCTUATION, // 21
            Character.END_PUNCTUATION, // 22
            Character.CONNECTOR_PUNCTUATION, // 23
            Character.OTHER_PUNCTUATION, // 24
            Character.MATH_SYMBOL, // 25
            Character.CURRENCY_SYMBOL, //26
            Character.MODIFIER_SYMBOL, // 27
            Character.OTHER_SYMBOL // 28
        ).map { it.toInt() }.toSet()
    }
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth


        val rvUsers = findViewById<RecyclerView>(R.id.rvUsers)
        // Query the users collection
        val query = db.collection("users").orderBy("displayName")
        val options = FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java)
            .setLifecycleOwner(this).build()


        val adapter = object: FirestoreRecyclerAdapter<User, UserViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                val view = LayoutInflater.from(this@MainActivity).inflate(android.R.layout.simple_list_item_2, parent, false)
                return UserViewHolder(view)
            }

            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
                val tvName: TextView = holder.itemView.findViewById(android.R.id.text1)
                val tvEmojis: TextView = holder.itemView.findViewById(android.R.id.text2)
                tvName.text = model.displayName
                tvEmojis.text = model.emojistat
            }
        }
        rvUsers.adapter = adapter
        rvUsers.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuLogout){
            Log.i(TAG, "Logout")
            Firebase.auth.signOut()  // we can also make a variable auth = Firebase.auth and then call auth.signout
            val logoutIntent = Intent(this, LoginActivity::class.java)  // we made logout intent as a separate variable since we need to use it to clear the backstack - we could have also done it like startActivity(Intent(__ , __))

            // now we want the backstack to end completely, hence we use intent flags
            logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // to clear everything from the backstack and start intent activity again
            startActivity(logoutIntent)
        }
        else if (item.itemId == R.id.editStat){
            Log.i(TAG, "Show Alert Dialogue to update status")
            updateStatus()
        }
        return super.onOptionsItemSelected(item)
    }

    // Update stat function to,  1.Implement the AlertDialogue to update the status, 2.
    private fun updateStatus() {

        // this is to get the curr user from the database
        val currUser = auth.currentUser

        // this is the text field that will appear in the alert dialogue
        val editText = EditText(this)

        val contextView = findViewById<View>(R.id.rvUsers)

        val emojiFilter = EmojiFilter()
        val lengthFilter = InputFilter.LengthFilter(9)
        editText.filters = arrayOf(lengthFilter, emojiFilter)


        val updateStatDialog  = MaterialAlertDialogBuilder(this)
            .setTitle("Update Your EmojiStat")
            .setView(editText)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Update", null)
            .show()
        // for SnackBar

        // now set an onClickListener for the positive Button when we hit the Update button
        updateStatDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            Log.i(TAG, "clicked on positive button")
            val newStatus = editText.text.toString()
            if (newStatus.isBlank()){
                Snackbar.make(contextView, "Empty Text", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            } else{
                if (currUser == null){  // this can also be made not null in the currUser definition
                    Snackbar.make(contextView, "User not found", Snackbar.LENGTH_LONG)
                        .setAction("Login") {
                            val loginIntent = Intent(this, LoginActivity::class.java)  // we made logout intent as a separate variable since we need to use it to clear the backstack - we could have also done it like startActivity(Intent(__ , __))

                    // now we want the backstack to end completely, hence we use intent flags
                    loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // to clear everything from the backstack and start intent activity again
                    startActivity(loginIntent)
                }.show()
                    updateStatDialog.dismiss()
                } else{
                    // find the current user in the documents in collection in the Firestore database
                    db.collection("users").document(currUser.uid)
                        .update("emojistat", newStatus)
                    Snackbar.make(contextView, "Status updated successfully", Snackbar.LENGTH_SHORT).show()
                    updateStatDialog.dismiss()
                }
            }

        }
    }

    // function to filter the input to only emojis - iterate to every character of the input string and check if the type of character is
    // valid as per the character map made above
    inner class EmojiFilter : InputFilter {

        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence {
            if (source == null || source.isBlank()) {
                return ""
            }
            Log.i(TAG, "Added text $source has length of ${source.length} characters")
            val contextView = findViewById<View>(R.id.rvUsers)

            for (inputChar in source) {
                val type = Character.getType(inputChar)
                Log.i(TAG, "Character type $type")
                if (!VALID_CHAR_TYPES.contains(type)) {
                    Snackbar.make(contextView, "Can only use Emojis as input", Snackbar.LENGTH_SHORT)
                        .show()
                    return ""
                }
            }
            // The CharSequence being added is a valid emoji! Allow it to be added
            return source
        }
    }
}