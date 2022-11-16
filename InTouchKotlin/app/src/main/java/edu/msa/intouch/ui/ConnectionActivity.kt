package edu.msa.intouch.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import edu.msa.intouch.databinding.ActivityConnectionBinding
import okhttp3.*
import java.io.IOException


class ConnectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        initializeButtons()
        val button: Button = binding.homeIcon

        val showPopUp = PopupMenu(
            this,
            button
        )

        showPopUp.menu.add(Menu.NONE, 0, 0, "Upload profile photo")
        showPopUp.menu.add(Menu.NONE, 1, 1, "Log Out")

        showPopUp.setOnMenuItemClickListener { menuItem ->
            val id = menuItem.itemId
            if (id == 1) {
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            }
            false
        }

        button.setOnClickListener {
            showPopUp.show()
        }
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
                val mUser = FirebaseAuth.getInstance().currentUser
                mUser!!.getIdToken(true)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token: String? = task.result.token
                            println(token)
                            // Send token to your backend via HTTPS
                            // Initialize Network Interceptor
                            val networkInterceptor = Interceptor { chain ->
                                val newRequest = chain.request().newBuilder()
                                    .addHeader("Authorization", "Bearer $token")
                                    .build()
                                val response = chain.proceed(newRequest)

                                response.newBuilder().build()
                            }
                            // Build OkHttpClient
                            val client = OkHttpClient.Builder()
                                .addNetworkInterceptor(networkInterceptor)
                                .build()

                            val request = Request.Builder()
                                .url("https://yfv5brx1l4.loclx.io/users/test")
                                .build()

                            client.newCall(request).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {}
                                override fun onResponse(call: Call, response: Response) {
                                    response.use {
                                        if (response.isSuccessful) {
                                            println(response.body()?.string().toString())
                                        }
                                    }
                                }
                            })


                        } else {
                            // Handle error -> task.getException();
                        }
                    }

            }
        }
    }

    private fun setBinding() {
        binding = ActivityConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}