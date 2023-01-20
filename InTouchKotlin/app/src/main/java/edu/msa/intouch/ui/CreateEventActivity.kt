package edu.msa.intouch.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivityCreateEventBinding
import edu.msa.intouch.model.Client
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*

class CreateEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateEventBinding
    private var storageRef = Firebase.storage
    private var firebaseFirestore = Firebase.firestore
    private lateinit var timePicker : TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        getMenu()
        setNavigation(this)
        getProfilePicture()
        setDate()
        initButtons()
        createEvent()
        setUserDetails()
    }

    private fun setBinding() {
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initButtons() {
        timePicker = findViewById(R.id.timePicker1)
        timePicker.setIs24HourView(true)
        timePicker.setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
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

    private fun getProfilePicture() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        var islandRef = storageRef.reference.child("images/$userId")
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

    private fun setDate() {
        val selectedDate = intent.getSerializableExtra("selectedDate") as String
        val selectedDateText : TextView = findViewById(R.id.selectedDateText)
        selectedDateText.text = selectedDate
    }

    private fun createEvent(){
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val selectedUser =
            Json.decodeFromString<Client>(intent.getSerializableExtra("selectedUser") as String)

        binding.saveBtn.setOnClickListener {
            var title = binding.createTitleEvent.text.toString()
            val timeHour = timePicker.hour.toString()
            val timeMinute : String
            if(timePicker.minute > 9) {
                timeMinute = timePicker.minute.toString()
            }
            else {
                timeMinute = "0" + timePicker.minute.toString()
            }
            var time = "$timeHour:$timeMinute"

            val bitEncode = (selectedUser.firebaseId + currentUser).encodeToByteArray()
            val sumArray = bitEncode.sum()

            if (title.isEmpty()) {
                Toast.makeText(applicationContext, "Title is required", Toast.LENGTH_LONG)
                    .show()
            } else {
                val selectedDate = intent.getSerializableExtra("selectedDate") as String
                val documentReference = firebaseFirestore.collection("events").document(sumArray.toString()).collection(selectedDate).document()

                val event = HashMap<String, String>()
                event["title"] = title
                event["time"] = time

                documentReference.set(event).addOnSuccessListener {
                    Toast.makeText(applicationContext, "Event created successfully", Toast.LENGTH_LONG)
                        .show()

                    val eventIntent = Intent(this, CalendarActivity::class.java)
                    val selectedUser = intent.getSerializableExtra("selectedUser") as String
                    eventIntent.putExtra("selectedUser", selectedUser)
                    eventIntent.putExtra("selectedDate", selectedDate)
                    startActivity(eventIntent)

                } .addOnFailureListener {
                    Toast.makeText(applicationContext, "Failed to create note", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
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