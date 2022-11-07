package edu.msa.intouch.ui

import android.os.Bundle
import android.text.TextUtils
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
                            // Send token to your backend via HTTPS
                            // Initialize Network Interceptor
                            val networkInterceptor = Interceptor { chain ->
                                val newRequest = chain.request().newBuilder()
                                    .addHeader("Authorization", "$token")
                                    .build()
                                val response = chain.proceed(newRequest)

                                response.newBuilder().build()
                            }
                            // Build OkHttpClient
                            val client = OkHttpClient.Builder()
                                .addNetworkInterceptor(networkInterceptor)
                                .build()

                            val request = Request.Builder()
                                .url("https://pay3v6lqkh.loclx.io/users/test")
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