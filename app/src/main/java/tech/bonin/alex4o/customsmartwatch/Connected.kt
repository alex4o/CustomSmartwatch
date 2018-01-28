package tech.bonin.alex4o.customsmartwatch

import android.bluetooth.BluetoothSocket
import android.icu.util.Output
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import java.io.InputStream
import java.io.OutputStream
import android.content.pm.PackageManager
import android.content.ComponentName



/**
 * Created by alex4o on 1/21/18.
 */
object Connected {
    var istream: InputStream? = null
    var ostream: OutputStream? = null
    var socket: BluetoothSocket? = null
}

object Data {
    var notifications = Channel<StatusBarNotification>(10)
    var toSend = Channel<ByteArray>(10)
}

public fun send(n: StatusBarNotification) = launch {
    Data.notifications.send(n)
}
