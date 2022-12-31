package edu.msa.intouch.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import edu.msa.intouch.databinding.ActivityConnectionBinding
import edu.msa.intouch.service.BackendApiService


class ConnectionActivity : AppCompatActivity() {

    private val backendApiService = BackendApiService()
    private var storageRef = Firebase.storage

    private lateinit var binding: ActivityConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        getProfilePicture()
        getMenu()
        initializeButtons()

    }

    private fun getMenu() {
        val button: ImageButton = binding.homeIcon

        val showPopUp = PopupMenu(
            this,
            button
        )

        //showPopUp.menu.add(Menu.NONE, 0, 0, "Upload profile photo")
        showPopUp.menu.add(Menu.NONE, 1, 1, "Log Out")

        showPopUp.setOnMenuItemClickListener { menuItem ->
            val id = menuItem.itemId
            if (id == 1) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            false
        }

        button.setOnClickListener {
            showPopUp.show()
        }
    }

    private fun setBinding() {
        binding = ActivityConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initializeButtons() {
        binding.createConnectionId.setOnClickListener {
            createConnectionAction()
        }
    }

    private fun createConnectionAction() {
        val email = binding.connectionEmail.text.toString().trim { it <= ' ' }

        when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(
                    this,
                    "Please enter email.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                val receiverEmail = binding.connectionEmail.text.toString()
                backendApiService.createConnection(this, receiverEmail)
            }
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
}