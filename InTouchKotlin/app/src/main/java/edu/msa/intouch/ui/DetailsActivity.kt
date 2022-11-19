package edu.msa.intouch.ui

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import edu.msa.intouch.databinding.ActivityDetailsBinding
import edu.msa.intouch.model.Client
import edu.msa.intouch.service.BackendApiService

class DetailsActivity : AppCompatActivity() {

    private val backendApiService = BackendApiService()

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        initializeButtons()
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
            backendApiService.callCreateClientEndpoint(this, client)
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
}