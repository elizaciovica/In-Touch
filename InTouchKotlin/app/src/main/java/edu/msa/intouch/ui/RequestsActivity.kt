package edu.msa.intouch.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.msa.intouch.R
import edu.msa.intouch.adapter.RequestAdapter
import edu.msa.intouch.databinding.ActivityRequestsBinding
import edu.msa.intouch.model.ConnectionStatus
import edu.msa.intouch.service.BackendApiService

class RequestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestsBinding

    private val backendApiService = BackendApiService()

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
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            false
        }

        button.setOnClickListener {
            showPopUp.show()
        }

        getRequestConnections()

    }

    private fun setBinding() {
        binding = ActivityRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun getRequestConnections() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        backendApiService.getAllConnectionRequestsByStatus(this, ConnectionStatus.PENDING.status)
        backendApiService.observeRequestsLiveData().observe(
            this
        ) { connectionsList ->
            if (connectionsList.isNotEmpty()) {

                binding.progressBar.isVisible = false
                binding.viewForNoConnections.isVisible = false
                binding.recyclerView.isVisible = true

                connectionsList.forEach {
                    val adapter = RequestAdapter(connectionsList)
                    recyclerView.adapter = adapter
                }
            } else {

                binding.progressBar.isVisible = false
                binding.viewForNoConnections.isVisible = true
                binding.recyclerView.isVisible = false
            }
        }

    }
}