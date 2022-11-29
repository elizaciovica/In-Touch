package edu.msa.intouch.ui

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.msa.intouch.databinding.ActivityConnectionBinding
import edu.msa.intouch.service.BackendApiService


class ConnectionActivity : AppCompatActivity() {

    private val backendApiService = BackendApiService()

    private lateinit var binding: ActivityConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        initializeButtons()
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
                /*
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
                */
                val receiverEmail = binding.connectionEmail.text.toString()
                backendApiService.createConnection(this, receiverEmail)
            }
        }
    }
}