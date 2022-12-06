package edu.msa.intouch.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivityChatBinding
import edu.msa.intouch.model.Client

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    private val currentUser : Client = Client(
        "tDr9Q2yibFM914AvaMP0vaevo232",
        "Diana",
        "Miscuta",
        "dianamiscuta",
        "diana@gmail.com"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setBiding()
        setUserDetails()
        initializeButtons()
    }

    private fun setBiding() {
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initializeButtons() {
        binding.sendButton.setOnClickListener {
            val message_send : EditText = findViewById(R.id.message_send)
            val message : String = message_send.getText().toString()
            if (!message.equals("")){
                sendMessage(currentUser.firebaseId, (intent.extras?.getString("userId").toString()), message)
            }
            else{
                Toast.makeText(this@ChatActivity, "You can't send an empty message", Toast.LENGTH_SHORT).show()
            }
            message_send.setText("")
        }
    }

    private fun setUserDetails(){
        val usernameTextView : TextView = findViewById(R.id.username)
        val clientList = ConnectionListActivity().clientList
        val selectedUser = clientList.find{it.firebaseId == (intent.extras?.getString("userId"))}
        usernameTextView.text = selectedUser?.firstName + " " + selectedUser?.lastName
    }

    private fun sendMessage(sender: String, receiver: String, message: String){
        val reference : DatabaseReference = FirebaseDatabase.getInstance().reference

        if(reference != null)
        {
            Toast.makeText(this@ChatActivity, ""+reference, Toast.LENGTH_LONG).show()
        }

        var hashMap : HashMap<String, String> = HashMap<String, String>()
        hashMap.put("sender", sender)
        hashMap.put("receiver", receiver)
        hashMap.put("message", message)

        reference.child("Chats").push().setValue(hashMap)

        //https://intouch-c623b-default-rtdb.europe-west1.firebasedatabase.app/
    }
}