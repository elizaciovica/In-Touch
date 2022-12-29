package edu.msa.intouch.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import edu.msa.intouch.databinding.ActivityDetailsBinding
import edu.msa.intouch.model.Client
import edu.msa.intouch.service.BackendApiService
import java.net.URI

class DetailsActivity : AppCompatActivity() {

    private val backendApiService = BackendApiService()
    private lateinit var profilePicture: ImageButton
    private lateinit var uri: Uri
    private var storageRef = Firebase.storage

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        initializeButtons()
        uploadImage()
    }

    private fun setBinding() {
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initializeButtons() {
        binding.setProfileButton.setOnClickListener {
            createProfileAction()
        }
    }

    private fun createProfileAction() {
        // get data from text views
        val client = getDataFromTextViews()

        // validate data -> if not valid -> show error -> Toast
        val isClientValid = isClientValid(client)

        if (isClientValid) {
            backendApiService.createClient(this, client)
        } else {
            Toast.makeText(
                this,
                "The client is not valid. Try again!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun isClientValid(client: Client): Boolean {
        if (TextUtils.isEmpty(client.firebaseId)
            || TextUtils.isEmpty(client.email)
            || TextUtils.isEmpty(client.firstName)
            || TextUtils.isEmpty(client.lastName)
            || TextUtils.isEmpty(client.username)
        ) {
            return false
        }

        return true
    }

    private fun getDataFromTextViews(): Client {
        val currentUser = FirebaseAuth.getInstance().currentUser

        val firebaseId = currentUser?.uid.toString()
        val email = currentUser?.email.toString()
        val firstName = binding.firstName.text.toString()
        val lastName = binding.lastName.text.toString()
        val username = binding.username.text.toString()

        return Client(
            firebaseId,
            firstName,
            lastName,
            username,
            email
        )
    }

    private fun uploadImage() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        profilePicture = binding.homeIcon
        storageRef = FirebaseStorage.getInstance()

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                profilePicture.setImageURI(it)
                uri = it!!
            }
        )
        binding.uploadImageButton.setOnClickListener() {
            galleryImage.launch("image/+")
        }

        binding.upload.setOnClickListener {
            storageRef.getReference("images").child(userId)
                .putFile(uri)
                .addOnSuccessListener {

                    val mapImage = mapOf(
                        "url" to it.toString()
                    )

                    val databaseReference = FirebaseDatabase.getInstance().getReference("userImages")
                    databaseReference.child(userId).setValue(mapImage)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Image successfully uploaded", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener() { error ->
                            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                        }

                }
        }

    }
}