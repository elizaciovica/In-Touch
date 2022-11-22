package edu.msa.intouch.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.msa.intouch.databinding.ActivityConnectionListBinding
import edu.msa.intouch.model.Client
import edu.msa.intouch.ui.adapter.ConnectionListAdapter

class ConnectionListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnectionListBinding

    private var clientList: List<Client> = listOf(
        Client(
            "Zc7jNTj3F9QeUaGERr9Iur8cbVH2",
            "Test",
            "Test",
            "test",
            "test@gmail.com"
        ),
        Client(
            "nKITPeDVZ2OymvegGrzG4aimTnm1",
            "Test1",
            "Test1",
            "test1",
            "test1@gmail.com"
        )
    )

    private lateinit var listAdapterConnections: ConnectionListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        initList(clientList)
        initButtons()
    }

    private fun setBinding() {
        binding = ActivityConnectionListBinding.inflate(layoutInflater);
        setContentView(binding.root);
    }

    private fun initList(connectionList: List<Client>){
        listAdapterConnections = ConnectionListAdapter(clientList);

        recyclerView = binding.connectionList
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.adapter = listAdapterConnections

        listAdapterConnections.setOnItemClickListener(object: ConnectionListAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val sharedConnection = Intent(this@ConnectionListActivity, SharedConnectionActivity::class.java)
                startActivity(sharedConnection)
            }

        })

    }


    private fun initButtons(){
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
}