package edu.msa.intouch.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
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
                            Toast.makeText(
                                this,
                                "Account successfully created",
                                Toast.LENGTH_SHORT
                            ).show()
                            startDetailsActivity()
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

    private fun startDetailsActivity() {
        val intent = Intent(this, DetailsActivity::class.java)
        startActivity(intent)
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