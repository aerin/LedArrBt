package ru.alexerin.ledarrbt

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

// Настройки приложения:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore( name = "user_settings" )

class MainActivity : AppCompatActivity()
{
    // Контролы:
    private lateinit var btnFind: ImageButton
    private lateinit var btnConn: ImageButton
    private lateinit var tb: Toolbar

    // Для показа списка связанных bt-устройств;
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    // Bluetooth:
    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = null

    // Устройство bt:
    private var btDeviceName: String = ""
    private var btDeviceMac: String = ""

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

        // Регистрируем обработчик выбора устройства из списка связанных устройств:
        resultLauncher = registerForActivityResult( ActivityResultContracts.StartActivityForResult())
        { result ->
            if( RESULT_OK == result.getResultCode() )
            {
                val intent = result.data
                if (intent != null)
                {
                    btDeviceName = intent.getStringExtra("name" ).toString()
                    cfgSave( "device_name", btDeviceName )
                    btDeviceMac = intent.getStringExtra("mac" ).toString()
                    cfgSave( "device_mac", btDeviceMac )

                    tb.title = btDeviceName
                }
            }
            else
            {
                // Если нажать кнопку back:
                Toast.makeText(this, "Херня", Toast.LENGTH_SHORT ).show()
            }
        }

        btnFind = findViewById<ImageButton>( R.id.btnFind ) as ImageButton
        btnConn = findViewById<ImageButton>( R.id.btnConnect ) as ImageButton
        tb = findViewById<View>(R.id.toolbar) as Toolbar

        btDeviceName = cfgLoad( "device_name" )
        btDeviceMac = cfgLoad( "device_mac" )
        Log.d("DataStore", "Saved device: " + btDeviceName + " [" + btDeviceMac + "]" )
        tb.title = btDeviceName

        bluetoothManager = getSystemService( BluetoothManager::class.java )
        bluetoothAdapter = bluetoothManager.getAdapter()

        if( false == bluetoothAdapter?.isEnabled )
            onBtOff()
        else
            onBtOn()

        val filter = IntentFilter( BluetoothDevice.ACTION_ACL_CONNECTED )
        filter.addAction( BluetoothDevice.ACTION_ACL_DISCONNECTED )
        filter.addAction( BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED )
        filter.addAction( BluetoothAdapter.ACTION_STATE_CHANGED )

        registerReceiver( receiver, filter )
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver()
    {
        override fun onReceive( context: Context, intent: Intent )
        {
            when( intent.action )
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
                    val state = intent.getIntExtra( BluetoothAdapter.EXTRA_STATE, -1 )
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
            val intent = Intent( this, BTListActivity::class.java)
            intent.putExtra( "BT_DEVICE_NAME", "aaa" )
            intent.putExtra( "BT_DEVICE_MAC", "" )
            resultLauncher.launch( intent )
        }
        btnConn.setOnClickListener {
            Toast.makeText(this, "Connect", Toast.LENGTH_SHORT ).show()
        }
    }

    private fun onBtOff()
    {
        btnFind.setBackgroundColor( Color.RED )
        btnConn.setBackgroundColor( Color.RED )
        btnFind.setOnClickListener { Toast.makeText(this, "Включите BT!", Toast.LENGTH_SHORT ).show() }
        btnConn.setOnClickListener { Toast.makeText(this, "Включите BT!", Toast.LENGTH_SHORT ).show() }
    }

    //-------------------------------------------------------------------------
    // Сохранение value в качестве значения ключа key из DataStore:
    //-------------------------------------------------------------------------
    private fun cfgSave( key: String, value: String )
    {
        runBlocking {
            this@MainActivity.dataStore.edit {
                it[stringPreferencesKey( key )] = value
            }
        }
    }

    //-------------------------------------------------------------------------
    // Загрузка значения ключа key из DataStore:
    //-------------------------------------------------------------------------
    private fun cfgLoad( key: String ): String
    {
        return runBlocking {
            val valueFlow: Flow<String> = this@MainActivity.dataStore.data.map {
                it[stringPreferencesKey( key )] ?: ""
            }
            valueFlow.first()
        }
    }
}