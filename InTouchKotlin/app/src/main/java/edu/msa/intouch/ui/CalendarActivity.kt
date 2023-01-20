package edu.msa.intouch.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivityCalendarBinding
import edu.msa.intouch.model.Client
import edu.msa.intouch.model.Event
import edu.msa.intouch.model.Note
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarBinding
    private var storageRef = Firebase.storage
    private var firebaseFirestore = Firebase.firestore
    private lateinit var selectedDate : String
    private lateinit var dateText : TextView
    private lateinit var eventAdapter: FirestoreRecyclerAdapter<Event, EventViewHolder>
    private lateinit var staggeredGridLayoutManager : StaggeredGridLayoutManager
    private lateinit var documentReference : DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setNavigation(this)
        initializeButtons()
        setUserDetails()
        selectDateAction()
        getEvents()
    }

    private fun setBinding() {
        binding = ActivityCalendarBinding.inflate(layoutInflater);
        setContentView(binding.root);
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

    @SuppressLint("SimpleDateFormat")
    private fun initializeButtons() {
        dateText = findViewById(R.id.dateText)
        val date = System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd MMM yyyy")
        val dateString = sdf.format(date)
        dateText.text = dateString

        binding.addEventBtn.setOnClickListener {
            val createEventIntent = Intent(this, CreateEventActivity::class.java)
            val selectedUser = intent.getSerializableExtra("selectedUser") as String
            createEventIntent.putExtra("selectedUser", selectedUser)
            createEventIntent.putExtra("selectedDate", dateText.text)
            startActivity(createEventIntent)
        }
    }

    private fun selectDateAction(){

        val myCalendar = Calendar.getInstance()
        
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth -> 
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(myCalendar)
        }


        binding.selectDateBtn.setOnClickListener {
            // open popup with datepicker
            DatePickerDialog(this, datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun updateLabel(myCalendar: Calendar) {
        val sdf = SimpleDateFormat("dd MMM yyyy")
        val dateFormatted = sdf.format(myCalendar.time)
        dateText.text = dateFormatted
        getEvents()
    }

    private fun getEvents() {
        val selectedUser =
            Json.decodeFromString<Client>(intent.getSerializableExtra("selectedUser") as String)
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

        val bitEncode = (selectedUser.firebaseId + currentUser).encodeToByteArray()
        val sumArray = bitEncode.sum()

        val query = firebaseFirestore.collection("events").document(sumArray.toString()).collection(dateText.text.toString())
        val allUsersEvents = FirestoreRecyclerOptions.Builder<Event>().setQuery(query, Event::class.java).build()

        eventAdapter = EventFirestoreRecyclerAdapter(allUsersEvents)
        staggeredGridLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.eventsRecyclerView.layoutManager = staggeredGridLayoutManager
        binding.eventsRecyclerView.adapter = eventAdapter
        binding.eventsRecyclerView.adapter?.notifyDataSetChanged()
        onStart()
    }

    private inner class EventViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun setEventDetails(title: String, time: String) {
            val titleView = view.findViewById<TextView>(R.id.eventTitle)
            val timeView = view.findViewById<TextView>(R.id.eventTime)
            titleView.text = title
            timeView.text = time
        }
    }

    private inner class EventFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<Event>)
        : FirestoreRecyclerAdapter<Event, CalendarActivity.EventViewHolder>(options) {

        override fun onBindViewHolder(eventViewHolder: CalendarActivity.EventViewHolder, position: Int, event: Event) {
            eventViewHolder.setEventDetails(event.title, event.time)

            val docId = eventAdapter.snapshots.getSnapshot(position).id

            onDelete(eventViewHolder.itemView, docId)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
            return EventViewHolder(view)
        }
    }

    override fun onStart() {
        super.onStart()
        eventAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()

        if (eventAdapter != null) {
            eventAdapter!!.stopListening()
        }
    }

    fun onDelete(view: View, docId: String) {
        val deleteButton = view.findViewById<ImageButton>(R.id.deleteEventBtn)
        deleteButton.setOnClickListener() {
            val selectedUser =
                Json.decodeFromString<Client>(intent.getSerializableExtra("selectedUser") as String)
            val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

            val bitEncode = (selectedUser.firebaseId + currentUser).encodeToByteArray()
            val sumArray = bitEncode.sum()

            documentReference = firebaseFirestore.collection("events").document(sumArray.toString())
                .collection(dateText.text.toString()).document(docId)
            documentReference.delete().addOnSuccessListener {
                Toast.makeText(applicationContext, "Event deleted", Toast.LENGTH_SHORT)
                    .show()
            }
                .addOnFailureListener() {
                    Toast.makeText(applicationContext, "Failed to delete", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }
}