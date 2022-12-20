package edu.msa.intouch.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivityChatBinding
import edu.msa.intouch.model.Chat
import edu.msa.intouch.model.Client
import edu.msa.intouch.ui.adapter.ChatAdapter

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    var chatList = ArrayList<Chat>()
    private lateinit var recyclerView: RecyclerView

    val currentUser : Client = Client(
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
        recyclerView = binding.chatRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        setUserDetails()
        initializeButtons()
        readMessage(currentUser.firebaseId, (intent.extras?.getString("userId").toString()))
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