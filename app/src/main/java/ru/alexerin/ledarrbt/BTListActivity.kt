package ru.alexerin.ledarrbt

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class BTListActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_btlist)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listBT = findViewById<ListView>( R.id.btlist )
        val listAdapt: MutableList<HashMap<String, String>> = ArrayList()

        val bluetoothManager = getSystemService( BluetoothManager::class.java )
        val bluetoothAdapter = bluetoothManager.getAdapter()
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val map: HashMap<String, String> = HashMap()
            map["name"] = device.name
            map["mac"] = device.address
            listAdapt.add( map )
        }

        val adapter = SimpleAdapter( this, listAdapt, android.R.layout.simple_list_item_2,
                                      arrayOf<String>("name", "mac"), intArrayOf(android.R.id.text1, android.R.id.text2 ) )

        listBT.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val item = parent.getItemAtPosition( position ) as HashMap<*, *>


            val data = Intent()
            data.putExtra( "name", item["name"] as String )
            data.putExtra( "mac", item["mac"] as String )
            setResult( RESULT_OK, data)
            finish()
        }
        listBT.setAdapter( adapter )
    }
}