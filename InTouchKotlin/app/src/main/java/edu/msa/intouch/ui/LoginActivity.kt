package edu.msa.intouch.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.msa.intouch.databinding.ActivityLoginBinding
import edu.msa.intouch.service.BackendApiService

class LoginActivity : AppCompatActivity() {

    private val backendApiService = BackendApiService()

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        //verifyLoggedInUser()
        initializeButtons()
    }

    private fun setBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

//    private fun verifyLoggedInUser() {
//        val user = Firebase.auth.currentUser
//
//        if (user !== null) {
//            user.email?.let { startApplication() }
//        }
//    }

    private fun initializeButtons() {
        binding.signUpButton.setOnClickListener {
            createAccountAction()
        }

        binding.loginButton.setOnClickListener {
            loginAction()
        }
    }

    private fun createAccountAction() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loginAction() {
        when {
            TextUtils.isEmpty(binding.email.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please enter email.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(binding.password.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please enter password.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                val email: String = binding.email.text.toString().trim { it <= ' ' }
                val password: String = binding.password.text.toString().trim { it <= ' ' }

                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            Toast.makeText(
                                this,
                                "Login successful",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Call API and see if the user already exists in the DB
                            backendApiService.getClientById(this)
                        } else {
                            Toast.makeText(
                                this,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
}