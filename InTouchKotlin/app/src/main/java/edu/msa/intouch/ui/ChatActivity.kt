package edu.msa.intouch.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivityChatBinding
import edu.msa.intouch.model.Chat
import edu.msa.intouch.model.Client
import edu.msa.intouch.adapter.ChatAdapter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    var chatList = ArrayList<Chat>()
    private lateinit var recyclerView: RecyclerView
    val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setBiding()
        recyclerView = binding.chatRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        setUserDetails()
        initializeButtons()
        val selectedUser =
            Json.decodeFromString<Client>(intent.getSerializableExtra("selectedUser") as String)
        readMessage(currentUser!!.uid, selectedUser.firebaseId)
    }

    private fun setBiding() {
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initializeButtons() {
        val selectedUser =
            Json.decodeFromString<Client>(intent.getSerializableExtra("selectedUser") as String)
        binding.sendButton.setOnClickListener {
            val message_send : EditText = findViewById(R.id.message_send)
            val message : String = message_send.getText().toString()
            if (!message.equals("")){
                sendMessage(currentUser!!.uid, selectedUser.firebaseId, message)
            }
            else{
                Toast.makeText(this@ChatActivity, "You can't send an empty message", Toast.LENGTH_SHORT).show()
            }
            message_send.setText("")
        }
    }

    private fun setUserDetails(){
        val usernameTextView : TextView = findViewById(R.id.username)
        val selectedUser =
            Json.decodeFromString<Client>(intent.getSerializableExtra("selectedUser") as String)
        usernameTextView.text = selectedUser!!.firstName + " " + selectedUser!!.lastName
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

    fun readMessage(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chats")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)
                    if (chat!!.sender.equals(senderId) && chat!!.receiver.equals(receiverId) ||
                        chat!!.sender.equals(receiverId) && chat!!.receiver.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }

                val chatAdapter = ChatAdapter(this@ChatActivity, chatList)

                recyclerView.adapter = chatAdapter
            }
        })
    }
}