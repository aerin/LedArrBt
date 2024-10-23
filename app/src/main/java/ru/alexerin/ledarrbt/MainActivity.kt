package ru.alexerin.ledarrbt

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity()
{
    // Контролы:
    private lateinit var btnFind: ImageButton
    private lateinit var btnConn: ImageButton

    // Bluetooth:
    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = null

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate( savedInstanceState )
        enableEdgeToEdge()
        setContentView( R.layout.activity_main )
        ViewCompat.setOnApplyWindowInsetsListener( findViewById( R.id.main ) )
        {
            v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnFind = findViewById<ImageButton>( R.id.btnFind )
        btnConn = findViewById<ImageButton>( R.id.btnConnect )

        bluetoothManager = getSystemService( BluetoothManager::class.java )
        bluetoothAdapter = bluetoothManager.getAdapter()

        if( false == bluetoothAdapter?.isEnabled )
            onBtOff();
        else
            onBtOn();

        val filter = IntentFilter( BluetoothDevice.ACTION_ACL_CONNECTED )
        filter.addAction( BluetoothDevice.ACTION_ACL_DISCONNECTED );
        filter.addAction( BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED );
        filter.addAction( BluetoothAdapter.ACTION_STATE_CHANGED );

        registerReceiver( receiver, filter )
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver()
    {
        override fun onReceive( context: Context, intent: Intent )
        {
            //val action = intent.action
            when( intent.action /*action*/ )
            {
                BluetoothDevice.ACTION_ACL_CONNECTED ->
                {

                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED ->
                {

                }
                BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED ->
                {

                }
                BluetoothAdapter.ACTION_STATE_CHANGED ->
                {
                    val state = intent.getIntExtra( BluetoothAdapter.EXTRA_STATE, -1 );
                    if( BluetoothAdapter.STATE_OFF == state )
                        onBtOff()
                    else
                        if( BluetoothAdapter.STATE_ON == state )
                            onBtOn()
                }
            }
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()

        unregisterReceiver( receiver )
    }

    private fun onBtOn()
    {
        btnFind.setBackgroundColor( Color.BLUE )
        btnConn.setBackgroundColor( Color.BLUE )
        btnFind.setOnClickListener {
            Toast.makeText(this, "Find", Toast.LENGTH_SHORT).show()
        }
        btnConn.setOnClickListener {
            Toast.makeText(this, "Connect", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onBtOff()
    {
        btnFind.setBackgroundColor( Color.RED )
        btnConn.setBackgroundColor( Color.RED )
        btnFind.setOnClickListener { Toast.makeText(this, "Включите BT!", Toast.LENGTH_SHORT ).show() }
        btnConn.setOnClickListener { Toast.makeText(this, "Включите BT!", Toast.LENGTH_SHORT ).show() }
    }
}