package edu.msa.intouch.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import edu.msa.intouch.databinding.ActivityHomeBinding
import okhttp3.*
import java.io.IOException

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var payload = "Text"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()

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

//        binding.buttonExample.setOnClickListener {
//            binding.payload.text = payload
//            getData()
//        }
        initializeButtons()

    }

    private fun initializeButtons() {
        binding.connection.setOnClickListener {
            startActivity(Intent(this, ConnectionActivity::class.java))
            finish()
        }

    }

    private fun getData() {
        val client = OkHttpClient()
        val url = "https://saqrb3ukrq.loclx.io/users"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        payload = response.body()?.string().toString()
                    }
                }
            }
        })
    }

    private fun setBinding() {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}