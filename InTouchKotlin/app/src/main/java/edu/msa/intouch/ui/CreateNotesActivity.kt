package edu.msa.intouch.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivityCreateNotesBinding
import edu.msa.intouch.model.Client
import edu.msa.intouch.service.BackendApiService
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CreateNotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateNotesBinding
    private var storageRef = Firebase.storage
    private var firebaseFirestore = Firebase.firestore
    private val backendApiService = BackendApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setBinding()
        delegate.setSupportActionBar(binding.toolbarofcreatenote)
        delegate.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        getMenu()
        setNavigation(this)
        createNotes()
        setUserDetails()
    }

    private fun setBinding() {
        binding = ActivityCreateNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun getMenu() {
        val button: ImageButton = binding.homeIcon

        val showPopUp = PopupMenu(
            this,
            button
        )

        showPopUp.menu.add(Menu.NONE, 0, 0, "See profile details")
        showPopUp.menu.add(Menu.NONE, 1, 1, "Log Out")

        showPopUp.setOnMenuItemClickListener { menuItem ->
            val id = menuItem.itemId
            if (id == 1) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            if (id == 0) {
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
            false
        }

        button.setOnClickListener {
            showPopUp.show()
        }
    }

    private fun setNavigation(activity: Activity) {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener { item ->
            if (item.itemId == R.id.page_1) {
                activity.startActivity(Intent(activity, HomeActivity::class.java))
                true
            }
            if (item.itemId == R.id.page_2) {
                activity.startActivity(Intent(activity, ConnectionActivity::class.java))
                true
            }
            false
        }
    }

    private fun createNotes() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val selectedUser =
            Json.decodeFromString<Client>(intent.getSerializableExtra("selectedUser") as String)

        binding.savebutton.setOnClickListener() {
            var title = binding.createtitleofnote.text.toString()
            var content = binding.createcontentofnote.text.toString()

            val bitEncode = (selectedUser.firebaseId + currentUser).encodeToByteArray()
            val sumArray = bitEncode.sum()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(applicationContext, "Both fields are required", Toast.LENGTH_LONG)
                    .show()
            } else {
                val documentReference = firebaseFirestore.collection("notes").document(sumArray.toString()).collection("MyNotes").document()

                val note = HashMap<String, String>()
                note["title"] = title
                note["content"] = content

                documentReference.set(note).addOnSuccessListener {
                    Toast.makeText(applicationContext, "Note Created Successfully", Toast.LENGTH_LONG)
                        .show()

                    val notesIntent = Intent(this, ViewNotesActivity::class.java)
                    val selectedUser = intent.getSerializableExtra("selectedUser") as String
                    notesIntent.putExtra("selectedUser", selectedUser)
                    startActivity(notesIntent)

                } .addOnFailureListener {
                    Toast.makeText(applicationContext, "Failed to create note", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val notesIntent = Intent(this, ViewNotesActivity::class.java)
                val selectedUser = intent.getSerializableExtra("selectedUser") as String
                notesIntent.putExtra("selectedUser", selectedUser)
                startActivity(notesIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUserDetails(){
        val usernameTextView : TextView = findViewById(R.id.username)
        val selectedUser =
            Json.decodeFromString<Client>(intent.getSerializableExtra("selectedUser") as String)
        usernameTextView.text = selectedUser!!.firstName + " " + selectedUser!!.lastName

        var islandRef = storageRef.reference.child("images/${selectedUser.firebaseId}")
        val ONE_MEGABYTE: Long = 1024 * 1024 * 10
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            binding.homeIcon.background = BitmapDrawable(
                resources,
                Bitmap.createScaledBitmap(
                    bitmap,
                    binding.homeIcon.width,
                    binding.homeIcon.height,
                    false
                )
            )
        }.addOnFailureListener {
            // Handle any errors
        }
    }
}