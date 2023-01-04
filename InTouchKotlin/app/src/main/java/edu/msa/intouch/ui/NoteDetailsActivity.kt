package edu.msa.intouch.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.PopupMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivityEditNotesBinding
import edu.msa.intouch.databinding.ActivityNoteDetailsBinding

class NoteDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteDetailsBinding
    private var storageRef = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setBinding()
        delegate.setSupportActionBar(binding.toolbarofnotedetails)
        delegate.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        getMenu()
        getProfilePicture()
        setNavigation()
        initializeButtons()
    }

    private fun setBinding() {
        binding = ActivityNoteDetailsBinding.inflate(layoutInflater)
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
        val data = intent

        binding.detailsbutton.setOnClickListener {
            val editIntent = Intent(this, EditNotesActivity::class.java)
            editIntent.putExtra("title", data.getStringExtra("title"))
            editIntent.putExtra("content", data.getStringExtra("content"))
            editIntent.putExtra("noteId", data.getStringExtra("noteId"))
            startActivity(editIntent)
        }

        binding.contentofnotedetail.text = data.getStringExtra("content")
        binding.titleofnotedetail.text = data.getStringExtra("title")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.startActivity(Intent(this, ViewNotesActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}