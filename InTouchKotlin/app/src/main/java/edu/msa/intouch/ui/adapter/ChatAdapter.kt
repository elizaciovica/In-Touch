package edu.msa.intouch.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.msa.intouch.R
import edu.msa.intouch.model.Chat
import edu.msa.intouch.ui.ChatActivity

class ChatAdapter(private val context: Context, private val chatList: ArrayList<Chat>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>(){

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType == MESSAGE_TYPE_RIGHT)
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_right, parent, false)
            return ViewHolder(view)
        }
        else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_left, parent, false)
            return ViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        holder.txtUserName.text = chat.message
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName: TextView = view.findViewById(R.id.show_message)
    }

    override fun getItemViewType(position: Int): Int {
        if(chatList[position].sender == ChatActivity().currentUser.firebaseId)
        {
            return MESSAGE_TYPE_RIGHT
        }
        else
        {
            return MESSAGE_TYPE_LEFT
        }
    }

}