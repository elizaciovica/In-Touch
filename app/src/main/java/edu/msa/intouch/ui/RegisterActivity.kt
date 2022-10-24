package edu.msa.intouch.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.msa.intouch.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBiding()
        initializeButtons()

    }

    private fun initializeButtons() {
        binding.signInButton.setOnClickListener {
            loginAction()
        }

        binding.signUpButton.setOnClickListener {
            registerAction()
        }
    }

    private fun registerAction() {
        val password = binding.password.text.toString().trim { it <= ' ' }
        val email = binding.email.text.toString().trim { it <= ' ' }

        when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(
                    this,
                    "Please enter email.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(password) -> {
                Toast.makeText(
                    this,
                    "Please enter password.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            Toast.makeText(
                                this,
                                "Account successfully created",
                                Toast.LENGTH_SHORT
                            ).show()
                            startApplication(firebaseUser, email)
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

    private fun startApplication(firebaseUser: FirebaseUser, email: String) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("userId", firebaseUser.uid)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }

    private fun loginAction() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setBiding() {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}