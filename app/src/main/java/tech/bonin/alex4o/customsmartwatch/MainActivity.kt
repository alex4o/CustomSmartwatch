package tech.bonin.alex4o.customsmartwatch

import android.app.NotificationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.bluetooth.*;
import android.content.ComponentName
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import android.graphics.Movie
import android.view.LayoutInflater
import android.view.ViewGroup
import android.support.annotation.NonNull
import android.support.annotation.LayoutRes
import android.view.View
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Looper
import kotlinx.coroutines.experimental.channels.consumeEach
import zemin.notification.NotificationDelegater
import zemin.notification.NotificationEntry
import zemin.notification.NotificationListener
import java.io.IOException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        val svc =  Intent(ctx.applicationContext, NListener::class.java)
        stopService(svc)
        startService(svc)


        val btAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetooth.text = "Bluetooth stats: ${btAdapter.isEnabled}"


        var list = btAdapter.bondedDevices.toList()
        this.devices.adapter = DeviceAdapter(ctx, list)
        this.devices.onItemClick { p0, p1, item, p3 ->
            val device = list[item]
            var uuids = device.uuids

            val job = async {
                if(Looper.myLooper() == null){
                    Looper.prepare()
                }
                val socket = device.createInsecureRfcommSocketToServiceRecord(uuids[0].uuid)
                try {
                    socket.connect()

                    Connected.istream = socket.inputStream
                    Connected.ostream = socket.outputStream
                    Connected.socket = socket
                } catch (ex: IOException) {
                    runOnUiThread {
                        longToast("I can't connect to that device.");
                    }
                    return@async false
                }
                return@async true
            }
            longToast("Connecting to: ${device}")
            if(job.await()) {
                val intent = Intent(ctx, ConnectedActivity::class.java)
                startActivity(intent)
            }
        }
        toggleNotificationListenerService()

//        launch {
//            Data.notifications.consumeEach {
//                println(it)
//                val notification = it.notification
//                runOnUiThread {
//                    toast("${it.packageName} ${notification.tickerText}")
//                }
//            }
//        }
    }


    fun toggleNotificationListenerService() {
        val pm = getPackageManager()
        pm.setComponentEnabledSetting(ComponentName(this, NListener::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)

        pm.setComponentEnabledSetting(ComponentName(this, NListener::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)

    }
}


