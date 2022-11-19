package edu.msa.intouch.service

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import edu.msa.intouch.model.Client
import edu.msa.intouch.ui.ConnectionActivity
import okhttp3.*
import java.io.IOException

class BackendApiService {

    private val BACKEND_API_URL = "https://yx0jbm4f0j.loclx.io"
    private val JSON = MediaType.parse("application/json; charset=utf-8")

    fun callCreateClientEndpoint(activity: Activity, client: Client) {
        val endpointUrl = "/users"
        val requestBody = RequestBody.create(JSON, Gson().toJson(client))

        callBackendEndpoint(activity, endpointUrl, requestBody)
    }

    private fun callBackendEndpoint(
        activity: Activity,
        endpointUrl: String,
        requestBody: RequestBody
    ) {

        FirebaseAuth.getInstance().currentUser
            ?.getIdToken(true)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token: String? = task.result.token

                    println(token)

                    // Send token to your backend via HTTPS
                    // Initialize Network Interceptor
                    val networkInterceptor = initializeNetworkInterceptor(token)

                    // Build OkHttpClient
                    val client = buildOkHttpClient(networkInterceptor)

                    val request = buildRequest(endpointUrl, requestBody)

                    client?.newCall(request)?.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            println("API call failure.")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (response.isSuccessful) {
                                println("Proceeding to start application.")
                                startActivityAction(activity)
                            } else {
                                println("API Response is not successful")
                                showErrorMessage(activity)
                            }
                        }
                    })
                } else {
                    println("Error on getting the Firebase Auth Token! ${task.exception}")
                }
            }
    }

    private fun buildRequest(endpointUrl: String, requestBody: RequestBody): Request {
        return Request.Builder()
            .url(BACKEND_API_URL + endpointUrl)
            .post(requestBody)
            .build()
    }

    private fun buildOkHttpClient(networkInterceptor: Interceptor): OkHttpClient? {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(networkInterceptor)
            .build()
    }

    private fun initializeNetworkInterceptor(
        token: String?
    ): Interceptor {
        val networkInterceptor = Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            val response = chain.proceed(newRequest)

            response.newBuilder().build()
        }
        return networkInterceptor
    }

    fun startActivityAction(activity: Activity) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                activity,
                "Your profile was successfully saved.",
                Toast.LENGTH_LONG
            ).show()
        }

        activity.startActivity(Intent(activity, ConnectionActivity::class.java))
        activity.finish()
    }

    fun showErrorMessage(activity: Activity) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                activity,
                "Request was not successful. Please try again!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}