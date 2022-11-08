package edu.msa.intouch.ui

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import edu.msa.intouch.databinding.ActivityConnectionListBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.msa.intouch.R
import edu.msa.intouch.ui.adapter.UserAdapter
import edu.msa.intouch.ui.model.User

class ConnectionListActivity : AppCompatActivity() {

    var binding : ActivityConnectionListBinding? = null
    var database : FirebaseDatabase? = null
    var users : ArrayList<User>? = null
    var usersAdapter : UserAdapter? = null
    var user : User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection_list)

        /*database = FirebaseDatabase.getInstance()
        users = ArrayList<User>()
        usersAdapter = UserAdapter(this@ConnectionListActivity, users!!)
        val layoutManager = GridLayoutManager(this@ConnectionListActivity, 2)
        binding!!.mRec.layoutManager = layoutManager
        database!!.reference.child("users")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError){}
            })
        binding!!.mRec.adapter = usersAdapter
        database!!.reference.child("users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                users!!.clear()
                for(snapshot1 in snapshot.children){
                    val user:User? = snapshot1.getValue(User::class.java)
                    if(!user!!.uid.equals(FirebaseAuth.getInstance().uid))
                        users!!.add(user)
                }
                usersAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })*/
    }

//    override fun onResume() {
//        super.onResume()
//        val currentId = FirebaseAuth.getInstance().uid
//        database!!.reference.child("presence").child(currentId!!).setValue("Online")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        val currentId = FirebaseAuth.getInstance().uid
//        database!!.reference.child("presence").child(currentId!!).setValue("Offline")
//    }
}