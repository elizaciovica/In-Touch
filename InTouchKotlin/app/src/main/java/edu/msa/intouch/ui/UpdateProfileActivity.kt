package edu.msa.intouch.ui

import android.Manifest.permission.*
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Menu
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivityUpdateProfileBinding
import edu.msa.intouch.model.Client
import edu.msa.intouch.service.BackendApiService

class UpdateProfileActivity : AppCompatActivity() {

    private val backendApiService = BackendApiService()
    private lateinit var profilePicture: ImageButton
    private lateinit var selectedImageUri: Uri
    private var storageRef = Firebase.storage
    var SELECT_PICTURE = 200

    private lateinit var binding: ActivityUpdateProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()

        val storagePermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(READ_EXTERNAL_STORAGE, false) -> {}
                permissions.getOrDefault(WRITE_EXTERNAL_STORAGE, false) -> {}
                permissions.getOrDefault(MANAGE_EXTERNAL_STORAGE, false) -> {}
            }
        }
        storagePermissionRequest.launch(
            arrayOf(
                READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE,
                MANAGE_EXTERNAL_STORAGE
            )
        )

        backendApiService.getClientById(this)
        backendApiService.observeClientLiveData().observe(
            this
        ) { client ->
            binding.firstname.setText(client.firstName)
            binding.lastname.setText(client.lastName)
            binding.username.setText(client.username)
        }

        getProfilePicture()
        getMenu()
        uploadImage()
        setNavigation(this)
        getClientDetails()
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

    private fun setBinding() {
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun updateProfileAction() {
        // get data from text views
        val client = getDataFromTextViews()

        // validate data -> if not valid -> show error -> Toast
        val isClientValid = isClientValid(client)

        if (isClientValid) {
            backendApiService.updateClient(this, client)
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
        val firstName = binding.firstname.text.toString()
        val lastName = binding.lastname.text.toString()
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

        binding.uploadImageButton.setOnClickListener() {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
        }

        binding.updateProfileButton.setOnClickListener {
            storageRef.getReference("images").child(userId)
                .putFile(selectedImageUri)
                .addOnSuccessListener {
                    val mapImage = mapOf(
                        "url" to it.toString()
                    )

                    val databaseReference =
                        FirebaseDatabase.getInstance().getReference("userImages")
                    databaseReference.child(userId).setValue(mapImage)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Image successfully uploaded", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnFailureListener() { error ->
                            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                        }

                }
            updateProfileAction()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data!!.getData()!!
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    var bitmap = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), Uri.parse(
                            selectedImageUri.toString()
                        )
                    )
                    profilePicture.background = BitmapDrawable(
                        resources,
                        Bitmap.createScaledBitmap(
                            bitmap,
                            profilePicture.width,
                            profilePicture.height,
                            false
                        )
                    )
                }
            }
        }
    }

    private fun setNavigation(activity: Activity) {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener { item ->
            if(item.itemId == R.id.page_1) {
                activity.startActivity(Intent(activity, HomeActivity::class.java))
                true
            }
            if(item.itemId == R.id.page_2) {
                activity.startActivity(Intent(activity, ConnectionActivity::class.java))
                true
            }
            false
        }
    }

    private fun getClientDetails() {
        backendApiService.getClientById(this)
    }
}