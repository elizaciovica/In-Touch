package edu.msa.intouch.ui

import android.app.Activity
import android.app.DownloadManager.Query
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
import edu.msa.intouch.databinding.ActivityViewNotesBinding
import edu.msa.intouch.model.Client
import edu.msa.intouch.model.Note
import edu.msa.intouch.service.BackendApiService
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.random.Random

class ViewNotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewNotesBinding
    private var storageRef = Firebase.storage
    private lateinit var staggeredGridLayoutManager : StaggeredGridLayoutManager
    private var firebaseFirestore = Firebase.firestore
    private val backendApiService = BackendApiService()
    private lateinit var noteAdapter: FirestoreRecyclerAdapter<Note, NoteViewHolder>
    private lateinit var documentReference : DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        getMenu()
        getProfilePicture()
        setNavigation()
        initializeButtons()
        getNotes()
    }

    private fun setBinding() {
        binding = ActivityViewNotesBinding.inflate(layoutInflater)
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

    private fun setNavigation() {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener { item ->
            if (item.itemId == R.id.page_1) {
                this.startActivity(Intent(this, HomeActivity::class.java))
                true
            }
            if (item.itemId == R.id.page_2) {
                this.startActivity(Intent(this, ConnectionActivity::class.java))
                true
            }
            false
        }
    }

    private fun initializeButtons() {
        binding.floatingButton.setOnClickListener {
            val createIntent = Intent(this, CreateNotesActivity::class.java)
            val selectedUser = intent.getSerializableExtra("selectedUser") as String
            createIntent.putExtra("selectedUser", selectedUser)
            startActivity(createIntent)
        }
    }

    //todo put extra from shared connection activity so we can get the guid of the connection
    private fun getNotes() {
        val selectedUser =
            Json.decodeFromString<Client>(intent.getSerializableExtra("selectedUser") as String)
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

        val bitEncode = (selectedUser.firebaseId + currentUser).encodeToByteArray()
        val sumArray = bitEncode.sum()
        val query = firebaseFirestore.collection("notes").document(sumArray.toString()).collection("MyNotes")
        val allUsersNotes = FirestoreRecyclerOptions.Builder<Note>().setQuery(query, Note::class.java).build()

        noteAdapter = NoteFirestoreRecyclerAdapter(allUsersNotes)
        staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = staggeredGridLayoutManager
        binding.recyclerView.adapter = noteAdapter
        binding.recyclerView.adapter?.notifyDataSetChanged()

    }

    private inner class NoteViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun setNoteDetails(title: String, content: String) {
            val textView = view.findViewById<TextView>(R.id.notetitle)
            val textContentView = view.findViewById<TextView>(R.id.notecontent)
            textView.text = title
            textContentView.text = content
        }
    }

    private inner class NoteFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<Note>)
        : FirestoreRecyclerAdapter<Note, NoteViewHolder>(options) {

        override fun onBindViewHolder(noteViewHolder: NoteViewHolder, position: Int, note: Note) {
            val colorCode = getRandomColor() as Int

            noteViewHolder.itemView.findViewById<LinearLayout>(R.id.note).setBackgroundColor(noteViewHolder.itemView.resources.getColor(colorCode, null))
            noteViewHolder.setNoteDetails(note.title, note.content)

            val docId = noteAdapter.snapshots.getSnapshot(position).id
            println("HERE " + docId + "hereeee")

            noteViewHolder.itemView.setOnClickListener() {
                onClick(noteViewHolder.itemView, note, docId)
            }

            onDelete(noteViewHolder.itemView, note, docId)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.notes_layout, parent, false)
            return NoteViewHolder(view)
        }

        fun onClick(view: View, note: Note, docId: String) {
            onDetailsClick(note, docId)
        }
    }

    fun onDelete(view: View, note: Note, docId: String) {
        val popupButton = view.findViewById<ImageView>(R.id.menupopbutton)
        popupButton.setOnClickListener() {
            val popupMenu = PopupMenu(applicationContext, view)
            popupMenu.gravity = Gravity.END
            popupMenu.menu.add("Edit").setOnMenuItemClickListener {
                onMenuEditItemClick(note, docId)
            }
            popupMenu.menu.add("Delete").setOnMenuItemClickListener {
                onMenuDeleteItemClick(docId)
            }
            popupMenu.show()
        }
    }

    fun onDetailsClick(note: Note, docId: String) {
        val detailsIntent = Intent(this, NoteDetailsActivity::class.java)
        detailsIntent.putExtra("title", note.title)
        detailsIntent.putExtra("content", note.content)
        detailsIntent.putExtra("noteId", docId)

        val selectedUser = intent.getSerializableExtra("selectedUser") as String
        detailsIntent.putExtra("selectedUser", selectedUser)
        startActivity(detailsIntent)
    }

    fun onMenuEditItemClick(note: Note, docId: String) : Boolean {
        val editIntent = Intent(this, EditNotesActivity::class.java)
        editIntent.putExtra("title", note.title)
        editIntent.putExtra("content", note.content)
        editIntent.putExtra("noteId", docId)

        val selectedUser = intent.getSerializableExtra("selectedUser") as String
        editIntent.putExtra("selectedUser", selectedUser)
        startActivity(editIntent)
        return false
    }

    fun onMenuDeleteItemClick(docId: String) : Boolean {
        val selectedUser =
            Json.decodeFromString<Client>(intent.getSerializableExtra("selectedUser") as String)
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

        val bitEncode = (selectedUser.firebaseId + currentUser).encodeToByteArray()
        val sumArray = bitEncode.sum()

        documentReference = firebaseFirestore.collection("notes").document(sumArray.toString()).collection("MyNotes").document(docId)
        documentReference.delete().addOnSuccessListener {
            Toast.makeText(applicationContext, "The note has been deleted", Toast.LENGTH_LONG)
            .show()
        }
            .addOnFailureListener() {
                Toast.makeText(applicationContext, "Failed to delete", Toast.LENGTH_LONG)
                    .show()
            }

        return false
    }

    override fun onStart() {
        super.onStart()
        noteAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()

        if (noteAdapter != null) {
            noteAdapter!!.stopListening()
        }
    }

    private fun getRandomColor(): Int {
        val colorCode = ArrayList<Int>()
        colorCode.add(R.color.mine1)
        colorCode.add(R.color.mine2)
        colorCode.add(R.color.mine3)
        colorCode.add(R.color.mine4)
        colorCode.add(R.color.mine5)
        colorCode.add(R.color.mine6)
        colorCode.add(R.color.mine7)

        val randomNumber = colorCode.random()
        return randomNumber
    }
}
