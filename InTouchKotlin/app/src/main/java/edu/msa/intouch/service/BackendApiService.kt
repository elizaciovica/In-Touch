package edu.msa.intouch.service

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import edu.msa.intouch.model.Client
import edu.msa.intouch.ui.DetailsActivity
import edu.msa.intouch.ui.HomeActivity
import edu.msa.intouch.util.BackendApiCallTypeEnum
import edu.msa.intouch.util.BackendApiCallTypeEnum.*
import edu.msa.intouch.util.HttpMethodTypeEnum
import edu.msa.intouch.util.HttpMethodTypeEnum.*
import okhttp3.*
import java.io.IOException

class BackendApiService {

    private val BACKEND_API_URL = "https://sdtzeggr0z.loclx.io"
    private val JSON = MediaType.parse("application/json; charset=utf-8")

    fun getClientById(activity: Activity) {
        val firebaseId = FirebaseAuth.getInstance().currentUser?.uid
        val endpointUrl = "/users/$firebaseId"

        callBackendEndpoint(activity, endpointUrl, null, GET, GET_CLIENT_BY_ID)
    }

    fun createClient(activity: Activity, client: Client) {
        val endpointUrl = "/users"
        val requestBody = RequestBody.create(JSON, Gson().toJson(client))

        callBackendEndpoint(activity, endpointUrl, requestBody, POST, CREATE_CLIENT)
    }

    fun createConnection(activity: Activity, receiverEmail: String) {
        val endpointUrl = "/connections/$receiverEmail"
        val requestBody = RequestBody.create(JSON, Gson().toJson(null))

        callBackendEndpoint(activity, endpointUrl, requestBody, POST, CREATE_CONNECTION)
    }

    private fun callBackendEndpoint(
        activity: Activity,
        endpointUrl: String,
        requestBody: RequestBody?,
        httpMethodType: HttpMethodTypeEnum,
        backendApiCallType: BackendApiCallTypeEnum,
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

                    val request: Request = if (requestBody != null) {
                        buildRequest(endpointUrl, requestBody, httpMethodType)
                    } else {
                        buildRequest(endpointUrl, httpMethodType)
                    }

                    client?.newCall(request)?.enqueue(
                        when (backendApiCallType) {
                            CREATE_CLIENT -> createClientCallback(activity)
                            GET_CLIENT_BY_ID -> getClientByIdCallback(activity)
                            CREATE_CONNECTION -> createConnectionCallback(activity)
                        }
                    )
                } else {
                    println("Error on getting the Firebase Auth Token! ${task.exception}")
                }
            }
    }

    private fun getClientByIdCallback(activity: Activity) = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            println("API call failure.")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                println("User has already the profile complete.")
                startActivityActionFromLogin(activity)
            } else if (response.code() == 404) {
                println("User not found in PostgreSQL. Must complete his profile first.")
                startDetailsActivity(activity)
            } else {
                println("API Response is not successful")
                showErrorMessage(activity)
            }
            response.close()
        }
    }

    private fun createClientCallback(activity: Activity) = object : Callback {
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
            response.close()
        }
    }

    private fun createConnectionCallback(activity: Activity) = object : Callback {
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
            response.close()
        }
    }

    private fun buildRequest(
        endpointUrl: String,
        requestBody: RequestBody,
        httpMethodType: HttpMethodTypeEnum
    ): Request {
        val requestBuilder = Request.Builder()
            .url(BACKEND_API_URL + endpointUrl)

        return when (httpMethodType) {
            GET -> requestBuilder.get().build()
            POST -> requestBuilder.post(requestBody).build()
            PUT -> requestBuilder.put(requestBody).build()
            DELETE -> requestBuilder.delete(requestBody).build()
        }
    }

    private fun buildRequest(
        endpointUrl: String,
        httpMethodType: HttpMethodTypeEnum
    ): Request {
        val requestBuilder = Request.Builder()
            .url(BACKEND_API_URL + endpointUrl)

        return when (httpMethodType) {
            GET -> requestBuilder.get().build()
            DELETE -> requestBuilder.delete().build()
            else -> requestBuilder.build() // Probably it never gets here
        }
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

    private fun startActivityAction(activity: Activity) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                activity,
                "Your profile was successfully saved.",
                Toast.LENGTH_LONG
            ).show()
        }

        activity.startActivity(Intent(activity, HomeActivity::class.java))
        activity.finish()
    }

    private fun startActivityActionFromLogin(activity: Activity) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                activity,
                "Welcome back",
                Toast.LENGTH_LONG
            ).show()
        }

        activity.startActivity(Intent(activity, HomeActivity::class.java))
        activity.finish()
    }

    private fun startDetailsActivity(activity: Activity) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                activity,
                "You must complete your profile details first.",
                Toast.LENGTH_LONG
            ).show()
        }

        activity.startActivity(Intent(activity, DetailsActivity::class.java))
        activity.finish()
    }

    private fun showErrorMessage(activity: Activity) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                activity,
                "Request was not successful. Please try again!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}